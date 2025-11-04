# 🚀 GCP VM 배포 빠른 시작 가이드

> Cloud Run에서 VM으로 전환하는 분들을 위한 간단 요약본

## ✅ 체크리스트

### 1️⃣ GCP에서 해야 할 일

-   [ ] **VM 인스턴스 생성** (5분)

    -   이름: `greenmap-server`
    -   리전: 서울 (`asia-northeast3`)
    -   머신 타입: `e2-small` (2GB RAM)
    -   OS: Ubuntu 22.04 LTS
    -   부팅 디스크: 20GB
    -   방화벽: HTTP, HTTPS 체크
    -   외부 IP: **고정 IP 예약** (중요!)

-   [ ] **방화벽 규칙** (1분)

    ```bash
    gcloud compute firewall-rules create allow-app-8080 \
      --allow tcp:8080 \
      --source-ranges 0.0.0.0/0
    ```

-   [ ] **서비스 계정 권한 추가** (2분)
    -   기존 `github-actions-deployer` 서비스 계정에
    -   **Compute Instance Admin** 역할 추가

### 2️⃣ VM에서 해야 할 일

VM에 SSH 접속 후:

```bash
# 1. 시스템 업데이트
sudo apt update && sudo apt upgrade -y

# 2. Docker 설치 (한 번만)
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
sudo apt install docker-compose git -y
exit  # 재접속 필요

# 3. 저장소 클론 (재접속 후)
cd ~
git clone https://github.com/ucamp-greenmap/greenmap-back.git
cd greenmap-back/greenmap

# 4. 환경변수 파일 생성
nano .env
```

`.env` 파일 내용:

```env
DB_URL=jdbc:mysql://[CLOUD_SQL_IP]:3306/greenmap?useSSL=false
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET_KEY=your_secret_key
JWT_MS=86400000
KAKAO_CLIENT_ID=your_kakao_id
KAKAO_REDIRECT_URI=http://[VM_IP]:8080/api/kakao/callback
KAKAO_REDIRECT_LOCAL=http://localhost:8080/api/kakao/callback
KAKAO_REDIRECT_PROD=http://[VM_IP]:8080/api/kakao/callback
CLIENT_ID=your_client_id
CLIENT_SECRET=your_client_secret
KEPCO_API_KEY=your_kepco_key
SEOUL_BIKE_API_KEY=your_bike_key
SPRING_PROFILES_ACTIVE=prod
```

```bash
# 5. 배포 스크립트 생성
nano deploy.sh
```

`deploy.sh` 내용:

```bash
#!/bin/bash
cd ~/greenmap-back
git pull origin dev
cd greenmap
docker stop greenmap || true
docker rm greenmap || true
docker build -t greenmap-app .
docker run -d \
  --name greenmap \
  --restart unless-stopped \
  -p 8080:8080 \
  --env-file .env \
  greenmap-app
docker logs --tail 50 greenmap
```

```bash
# 6. 실행 권한 부여 및 첫 배포
chmod +x deploy.sh
./deploy.sh

# 7. 확인
docker ps
curl http://localhost:8080/actuator/health
```

### 3️⃣ Nginx 설치 (선택, 80포트 사용하려면)

```bash
# 설치
sudo apt install nginx -y

# 설정
sudo nano /etc/nginx/sites-available/default
```

설정 파일 내용:

```nginx
server {
    listen 80;
    server_name _;
    client_max_body_size 10M;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# 재시작
sudo nginx -t
sudo systemctl restart nginx
sudo systemctl enable nginx
```

### 4️⃣ GitHub Actions 설정

**이미 작성된 파일 사용:**

-   `.github/workflows/deploy-vm.yml` 파일이 생성되어 있습니다.

**GitHub Secrets 추가 필요 (있으면 스킵):**

-   `GCP_PROJECT_ID`: GCP 프로젝트 ID
-   `GCP_SA_KEY`: 서비스 계정 JSON 키

**서비스 계정에 권한 추가:**

```bash
gcloud projects add-iam-policy-binding [PROJECT_ID] \
  --member="serviceAccount:github-actions-deployer@[PROJECT_ID].iam.gserviceaccount.com" \
  --role="roles/compute.instanceAdmin.v1"
```

---

## 🔧 자주 사용하는 명령어

### 애플리케이션 관리

```bash
# 재배포
cd ~/greenmap-back/greenmap && ./deploy.sh

# 로그 확인
docker logs -f greenmap

# 재시작
docker restart greenmap

# 중지
docker stop greenmap

# 시작
docker start greenmap
```

### 시스템 확인

```bash
# 컨테이너 상태
docker ps

# 시스템 리소스
htop  # 설치: sudo apt install htop

# 디스크 공간
df -h

# 포트 확인
sudo netstat -tlnp | grep 8080
```

### 문제 해결

```bash
# 전체 로그
docker logs greenmap

# 환경변수 확인
docker exec greenmap env

# DB 연결 테스트
mysql -h [CLOUD_SQL_IP] -u root -p

# Docker 정리
docker system prune -a
```

---

## 📊 Cloud Run vs VM 비교

| 항목          | Cloud Run         | VM (Compute Engine) |
| ------------- | ----------------- | ------------------- |
| 인스턴스 개수 | 자동 스케일 (0~N) | 1개 고정            |
| 파일 시스템   | 인스턴스마다 독립 | ✅ 영구 저장        |
| 세션 공유     | ❌ 불가능         | ✅ 가능             |
| 비용 (월)     | ~$5-10            | ~$21                |
| 관리          | 자동              | 수동                |
| 확장성        | 자동              | 수동                |
| 시작 시간     | 콜드 스타트 있음  | 항상 켜져 있음      |

---

## 💰 예상 비용

### e2-small 인스턴스 (서울)

-   **월 약 $21** (730시간 기준)
-   무료 크레딧 $300으로 **14개월** 사용 가능

### 비용 절감

-   **1년 약정**: 37% 할인 → $13/월
-   **3년 약정**: 55% 할인 → $9.45/월

---

## 🆘 문제 발생 시

### 1. 애플리케이션이 안 떠요

```bash
docker logs greenmap  # 로그 확인
docker ps -a  # 컨테이너 상태 확인
```

### 2. DB 연결이 안 돼요

-   Cloud SQL Private IP 확인
-   VM과 Cloud SQL이 같은 VPC에 있는지 확인
-   `.env` 파일의 DB_URL 확인

### 3. 외부에서 접속이 안 돼요

-   VM 외부 IP 확인: `gcloud compute instances list`
-   방화벽 규칙 확인: GCP Console → VPC → 방화벽
-   애플리케이션 실행 확인: `docker ps`

### 4. GitHub Actions 배포가 실패해요

-   서비스 계정 권한 확인
-   VM 이름/Zone이 일치하는지 확인
-   VM에 `deploy.sh` 파일이 있는지 확인

---

## 🎯 다음 할 일

1. ✅ VM 생성 및 초기 설정
2. ✅ 애플리케이션 배포
3. ⬜ 도메인 연결 (선택)
4. ⬜ HTTPS 설정 (Let's Encrypt)
5. ⬜ 모니터링 설정
6. ⬜ 백업 자동화

---

## 📚 상세 가이드

전체 상세 내용은 `GCP_VM_DEPLOYMENT_GUIDE.md` 참고

---

**도움이 필요하면 언제든지 물어보세요!** 🙋‍♂️
