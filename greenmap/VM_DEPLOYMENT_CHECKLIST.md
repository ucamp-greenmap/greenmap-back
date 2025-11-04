# ✅ GCP VM 배포 체크리스트

이 체크리스트를 하나씩 체크하면서 진행하세요!

---

## Phase 1: GCP 프로젝트 준비 (5분)

### GCP Console 설정

-   [ ] GCP 계정 생성 및 로그인
-   [ ] 프로젝트 선택 또는 생성
-   [ ] 프로젝트 ID 메모 (예: `elevated-nuance-476701-d9`)
-   [ ] 결제 계정 연결 확인

### API 활성화

GCP Console → API 및 서비스 → 라이브러리에서:

-   [ ] Compute Engine API
-   [ ] Cloud SQL Admin API (이미 있을 수 있음)

또는 Cloud Shell에서:

```bash
gcloud services enable compute.googleapis.com sqladmin.googleapis.com
```

---

## Phase 2: VM 인스턴스 생성 (10분)

### VM 생성 및 설정

GCP Console → Compute Engine → VM 인스턴스 → 인스턴스 만들기

-   [ ] **이름**: `greenmap-server`
-   [ ] **리전**: `asia-northeast3` (서울)
-   [ ] **영역**: `asia-northeast3-a`
-   [ ] **머신 시리즈**: E2
-   [ ] **머신 유형**: `e2-small` (2 vCPU, 2GB)
-   [ ] **부팅 디스크**: Ubuntu 22.04 LTS, 20GB
-   [ ] **방화벽**: HTTP 트래픽 허용 ✅
-   [ ] **방화벽**: HTTPS 트래픽 허용 ✅
-   [ ] **외부 IP**: 임시 → **고정 IP 예약** ✅ (중요!)
    -   이름: `greenmap-ip`
-   [ ] "만들기" 클릭 → 1-2분 대기

### VM 외부 IP 메모

-   [ ] VM이 생성되면 외부 IP 주소 복사 (예: `34.64.xxx.xxx`)
-   [ ] 📝 메모장에 기록: `_____._____._____.____`

---

## Phase 3: 방화벽 규칙 추가 (3분)

### 8080 포트 개방

Cloud Shell 또는 로컬 터미널에서:

```bash
gcloud compute firewall-rules create allow-app-8080 \
  --allow tcp:8080 \
  --source-ranges 0.0.0.0/0 \
  --description "Allow Spring Boot app port"
```

-   [ ] 명령 실행 완료
-   [ ] GCP Console → VPC 네트워크 → 방화벽에서 규칙 확인

---

## Phase 4: VM 접속 및 환경 설정 (15분)

### VM SSH 접속

-   [ ] GCP Console → Compute Engine → VM 인스턴스
-   [ ] `greenmap-server` 옆의 **SSH** 버튼 클릭
-   [ ] 브라우저 터미널 창 열림 확인

### 시스템 업데이트

```bash
sudo apt update
sudo apt upgrade -y
```

-   [ ] 명령 실행 완료 (약 3-5분)

### Docker 설치

```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
sudo apt install docker-compose git -y
sudo systemctl start docker
sudo systemctl enable docker
```

-   [ ] Docker 설치 완료
-   [ ] `exit` 명령으로 SSH 종료
-   [ ] 다시 SSH 버튼으로 재접속 (그룹 권한 적용)

### Docker 설치 확인

```bash
docker --version
docker ps
```

-   [ ] Docker 버전 출력 확인
-   [ ] sudo 없이 명령 실행 확인

---

## Phase 5: 애플리케이션 배포 (20분)

### 저장소 클론

```bash
cd ~
git clone https://github.com/ucamp-greenmap/greenmap-back.git
cd greenmap-back/greenmap
```

**Private 저장소인 경우:**

-   [ ] GitHub Personal Access Token 생성 (Settings → Developer settings)
-   [ ] repo 권한 체크

```bash
git clone https://[YOUR_TOKEN]@github.com/ucamp-greenmap/greenmap-back.git
```

-   [ ] 저장소 클론 완료
-   [ ] `ls` 명령으로 파일 확인

### 환경 변수 파일 생성

```bash
nano .env
```

-   [ ] `.env` 파일 편집기 열림

다음 내용 붙여넣기 (값 수정 필요):

```env
DB_URL=jdbc:mysql://[CLOUD_SQL_PRIVATE_IP]:3306/greenmap?useSSL=false
DB_USERNAME=root
DB_PASSWORD=your_db_password
JWT_SECRET_KEY=your_jwt_secret_key_min_256bits
JWT_MS=86400000
KAKAO_CLIENT_ID=your_kakao_rest_api_key
KAKAO_REDIRECT_URI=http://[VM_EXTERNAL_IP]:8080/api/kakao/callback
KAKAO_REDIRECT_LOCAL=http://localhost:8080/api/kakao/callback
KAKAO_REDIRECT_PROD=http://[VM_EXTERNAL_IP]:8080/api/kakao/callback
CLIENT_ID=your_naver_client_id
CLIENT_SECRET=your_naver_client_secret
KEPCO_API_KEY=your_kepco_api_key
SEOUL_BIKE_API_KEY=your_seoul_bike_api_key
SPRING_PROFILES_ACTIVE=prod
```

-   [ ] 환경 변수 값 모두 입력
-   [ ] `[CLOUD_SQL_PRIVATE_IP]` 교체
-   [ ] `[VM_EXTERNAL_IP]` 교체
-   [ ] Ctrl+X → Y → Enter로 저장

### Cloud SQL Private IP 확인

-   [ ] GCP Console → SQL → 인스턴스 클릭
-   [ ] 연결 탭 → Private IP 주소 복사
-   [ ] 📝 메모: `10._____._____.____`

### 배포 스크립트 생성

```bash
nano deploy.sh
```

-   [ ] 편집기 열림

다음 내용 붙여넣기:

```bash
#!/bin/bash
echo "🚀 Starting deployment..."
cd ~/greenmap-back
git pull origin dev
cd greenmap
echo "⏹️  Stopping existing container..."
docker stop greenmap || true
docker rm greenmap || true
docker rmi greenmap-app || true
echo "🔨 Building new image..."
docker build -t greenmap-app .
echo "▶️  Starting new container..."
docker run -d \
  --name greenmap \
  --restart unless-stopped \
  -p 8080:8080 \
  --env-file .env \
  greenmap-app
echo "📋 Container logs:"
docker logs --tail 50 greenmap
echo "✅ Deployment completed!"
```

-   [ ] Ctrl+X → Y → Enter로 저장

### 실행 권한 부여

```bash
chmod +x deploy.sh
```

-   [ ] 실행 권한 부여 완료

### 첫 배포 실행

```bash
./deploy.sh
```

-   [ ] 빌드 시작 (약 5-10분 소요)
-   [ ] 빌드 완료 메시지 확인
-   [ ] 컨테이너 시작 로그 확인

### 애플리케이션 실행 확인

```bash
docker ps
docker logs -f greenmap
```

-   [ ] 컨테이너 `greenmap` 상태 Up 확인
-   [ ] 로그에서 "Started GreenmapApplication" 메시지 확인
-   [ ] Ctrl+C로 로그 모니터링 종료

### 헬스체크 테스트

```bash
curl http://localhost:8080/actuator/health
```

-   [ ] `{"status":"UP"}` 응답 확인

### 외부 접속 테스트

로컬 브라우저에서:

```
http://[YOUR_VM_IP]:8080/actuator/health
```

-   [ ] 브라우저에서 헬스체크 확인
-   [ ] API 엔드포인트 테스트

---

## Phase 6: Nginx 설치 (선택, 10분)

### Nginx 설치 및 설정

```bash
sudo apt install nginx -y
```

-   [ ] Nginx 설치 완료

### 설정 파일 백업 및 편집

```bash
sudo mv /etc/nginx/sites-available/default /etc/nginx/sites-available/default.bak
sudo nano /etc/nginx/sites-available/default
```

다음 내용 붙여넣기:

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
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    location /actuator/health {
        proxy_pass http://localhost:8080/actuator/health;
        access_log off;
    }
}
```

-   [ ] Ctrl+X → Y → Enter로 저장

### Nginx 시작

```bash
sudo nginx -t
sudo systemctl restart nginx
sudo systemctl enable nginx
sudo systemctl status nginx
```

-   [ ] 설정 테스트 통과 (syntax is ok)
-   [ ] Nginx 재시작 완료
-   [ ] Active: active (running) 확인

### Nginx 접속 테스트

로컬 브라우저에서:

```
http://[YOUR_VM_IP]/actuator/health
```

-   [ ] 80 포트로 접속 확인 (포트 번호 없이)

---

## Phase 7: GitHub Actions 자동 배포 설정 (10분)

### 서비스 계정 권한 추가

Cloud Shell 또는 로컬에서:

```bash
gcloud projects add-iam-policy-binding [YOUR_PROJECT_ID] \
  --member="serviceAccount:github-actions-deployer@[YOUR_PROJECT_ID].iam.gserviceaccount.com" \
  --role="roles/compute.instanceAdmin.v1"
```

-   [ ] 명령 실행 완료
-   [ ] 권한 부여 확인

### GitHub Secrets 확인

GitHub Repository → Settings → Secrets and variables → Actions

필요한 Secrets (이미 있으면 스킵):

-   [ ] `GCP_PROJECT_ID`: 프로젝트 ID
-   [ ] `GCP_SA_KEY`: 서비스 계정 JSON 키

### 워크플로우 파일 확인

로컬 프로젝트에서:

-   [ ] `.github/workflows/deploy-vm.yml` 파일 존재 확인
-   [ ] VM_INSTANCE: `greenmap-server` 확인
-   [ ] VM_ZONE: `asia-northeast3-a` 확인

### 배포 테스트

```bash
git add .
git commit -m "Add VM deployment configuration"
git push origin dev
```

-   [ ] GitHub Actions 실행 확인
-   [ ] 워크플로우 성공 확인 (녹색 체크)

---

## Phase 8: 최종 확인 및 테스트 (5분)

### 모든 기능 테스트

-   [ ] 헬스체크: `http://[VM_IP]:8080/actuator/health`
-   [ ] Nginx (있는 경우): `http://[VM_IP]/actuator/health`
-   [ ] API 엔드포인트 테스트
-   [ ] 카카오 로그인 테스트
-   [ ] 데이터베이스 연결 확인

### 모니터링 설정

```bash
# 로그 확인 명령
docker logs -f greenmap
docker stats greenmap
htop  # sudo apt install htop
```

-   [ ] 로그 정상 출력 확인
-   [ ] 리소스 사용량 확인

---

## 🎉 완료!

축하합니다! GCP VM에 Spring Boot 애플리케이션을 성공적으로 배포했습니다.

### 📝 중요 정보 기록

-   VM 외부 IP: `___________________`
-   VM 인스턴스 이름: `greenmap-server`
-   VM Zone: `asia-northeast3-a`
-   배포 브랜치: `dev`
-   애플리케이션 포트: `8080`

### 🔧 자주 사용할 명령어

**재배포:**

```bash
cd ~/greenmap-back/greenmap && ./deploy.sh
```

**로그 확인:**

```bash
docker logs -f greenmap
```

**재시작:**

```bash
docker restart greenmap
```

**상태 확인:**

```bash
docker ps
```

---

## 🆘 문제 발생 시

### 1단계: 로그 확인

```bash
docker logs greenmap
```

### 2단계: 컨테이너 상태 확인

```bash
docker ps -a
```

### 3단계: 재배포

```bash
cd ~/greenmap-back/greenmap
./deploy.sh
```

### 여전히 문제가 있다면

-   상세 가이드 참고: `GCP_VM_DEPLOYMENT_GUIDE.md`
-   Cloud SQL 연결 확인
-   환경 변수 확인: `docker exec greenmap env`

---

**배포 완료 날짜**: ****\_\_\_****
**배포 담당자**: ****\_\_\_****
