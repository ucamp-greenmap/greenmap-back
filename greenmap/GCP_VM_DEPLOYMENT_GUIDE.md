# GCP Compute Engine (VM) 배포 가이드

> **주의**: 이 가이드는 GCP를 처음 사용하는 분들을 위한 상세한 단계별 설명입니다.

## 📌 왜 VM을 사용하나요?

Cloud Run의 문제점:

-   ❌ 인스턴스가 여러 개 뜨면 파일 시스템이 공유되지 않음
-   ❌ 세션 스토어가 인스턴스마다 분리됨
-   ❌ 로컬 캐시가 동기화되지 않음

VM의 장점:

-   ✅ 단일 인스턴스로 모든 요청 처리
-   ✅ 파일 시스템 영구 저장
-   ✅ 세션/캐시 공유 문제 해결
-   ✅ 더 많은 제어권

---

## 🚀 Step 1: GCP VM 인스턴스 생성

### 1.1 GCP Console 접속

1. https://console.cloud.google.com 접속
2. 기존 프로젝트 선택 또는 새 프로젝트 생성
3. 좌측 메뉴 → **Compute Engine** → **VM 인스턴스** 클릭

### 1.2 VM 인스턴스 만들기

"인스턴스 만들기" 버튼 클릭 후 다음과 같이 설정:

#### 기본 설정

```
이름: greenmap-server
리전: asia-northeast3 (서울)
영역: asia-northeast3-a (또는 b, c 중 선택)
```

#### 머신 구성

```
시리즈: E2
머신 유형: e2-small (2 vCPU, 2GB 메모리)
※ 트래픽이 많으면 e2-medium 이상 권장
```

#### 부팅 디스크

```
"변경" 클릭:
- 운영체제: Ubuntu
- 버전: Ubuntu 22.04 LTS (x86/64)
- 부팅 디스크 유형: 균형적 영구 디스크
- 크기: 20 GB (필요시 늘리기)
```

#### 방화벽

```
✅ HTTP 트래픽 허용
✅ HTTPS 트래픽 허용
```

#### 네트워킹 (고급 옵션)

```
네트워크 인터페이스 → "편집" 클릭
- 네트워크: default
- 외부 IPv4 주소: "Ephemeral" → "고정 IP 주소 예약" 클릭
  → 새 이름 입력 (예: greenmap-ip) → "예약" 클릭
```

**"만들기"** 버튼 클릭 → 약 1-2분 대기

---

## 🔧 Step 2: VM에 접속 및 초기 설정

### 2.1 VM 접속

VM 인스턴스 목록에서 `greenmap-server` 옆의 **"SSH"** 버튼 클릭
→ 브라우저에서 터미널이 열립니다

### 2.2 시스템 업데이트

```bash
# 패키지 목록 업데이트
sudo apt update

# 설치된 패키지 업그레이드
sudo apt upgrade -y
```

### 2.3 필수 프로그램 설치

#### Docker 설치

```bash
# Docker 설치 스크립트 다운로드 및 실행
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 현재 사용자를 docker 그룹에 추가 (sudo 없이 사용)
sudo usermod -aG docker $USER

# docker-compose 설치
sudo apt install docker-compose -y

# Docker 서비스 시작 및 부팅 시 자동 시작 설정
sudo systemctl start docker
sudo systemctl enable docker

# 그룹 변경사항 적용을 위해 재로그인 (중요!)
exit
```

다시 **SSH** 버튼으로 접속한 후:

```bash
# Docker 설치 확인
docker --version
docker-compose --version
```

#### Git 설치

```bash
sudo apt install git -y
git --version
```

---

## 📦 Step 3: 애플리케이션 배포

### 3.1 저장소 클론

```bash
# 홈 디렉토리로 이동
cd ~

# GitHub 저장소 클론 (private repo인 경우 인증 필요)
git clone https://github.com/ucamp-greenmap/greenmap-back.git

# 프로젝트 디렉토리로 이동
cd greenmap-back/greenmap
```

**Private 저장소인 경우 Personal Access Token 필요:**

```bash
# GitHub에서 Settings → Developer settings → Personal access tokens
# → Tokens (classic) → Generate new token (repo 권한 체크)
git clone https://[YOUR_TOKEN]@github.com/ucamp-greenmap/greenmap-back.git
```

### 3.2 환경 변수 파일 생성

```bash
# .env 파일 생성
nano .env
```

다음 내용을 입력 (실제 값으로 변경):

```env
# Database
DB_URL=jdbc:mysql://[CLOUD_SQL_IP]:3306/greenmap?useSSL=false
DB_USERNAME=root
DB_PASSWORD=your_db_password

# JWT
JWT_SECRET_KEY=your_jwt_secret_key_min_256bits
JWT_MS=86400000

# Kakao OAuth
KAKAO_CLIENT_ID=your_kakao_rest_api_key
KAKAO_REDIRECT_URI=http://your_vm_ip:8080/api/kakao/callback
KAKAO_REDIRECT_LOCAL=http://localhost:8080/api/kakao/callback
KAKAO_REDIRECT_PROD=http://your_vm_ip:8080/api/kakao/callback

# Naver API
CLIENT_ID=your_naver_client_id
CLIENT_SECRET=your_naver_client_secret

# Other API Keys
KEPCO_API_KEY=your_kepco_api_key
SEOUL_BIKE_API_KEY=your_seoul_bike_api_key

# Spring Profile
SPRING_PROFILES_ACTIVE=prod
```

**저장**: `Ctrl + X` → `Y` → `Enter`

### 3.3 Docker로 애플리케이션 빌드 및 실행

```bash
# Docker 이미지 빌드
docker build -t greenmap-app .

# 컨테이너 실행
docker run -d \
  --name greenmap \
  --restart unless-stopped \
  -p 8080:8080 \
  --env-file .env \
  greenmap-app

# 로그 확인
docker logs -f greenmap
```

**컨테이너가 정상 실행되면**:

-   `Ctrl + C`로 로그 모니터링 종료
-   애플리케이션이 백그라운드에서 계속 실행됨

### 3.4 실행 확인

```bash
# VM 내부에서 확인
curl http://localhost:8080/actuator/health

# 외부에서 확인 (브라우저나 로컬 터미널)
curl http://[YOUR_VM_EXTERNAL_IP]:8080/actuator/health
```

---

## 🔒 Step 4: 방화벽 규칙 확인

### 4.1 방화벽 규칙 확인

GCP Console → **VPC 네트워크** → **방화벽** → 다음 규칙이 있는지 확인:

```
default-allow-http (tcp:80)
default-allow-https (tcp:443)
```

### 4.2 8080 포트 개방 (필요시)

만약 포트 8080을 직접 사용하려면:

```bash
# GCP Cloud Shell 또는 로컬에서 gcloud 명령 실행
gcloud compute firewall-rules create allow-app-8080 \
  --allow tcp:8080 \
  --source-ranges 0.0.0.0/0 \
  --description "Allow port 8080 for Spring Boot app"
```

---

## 🌐 Step 5: Nginx 리버스 프록시 설정 (권장)

포트 8080 대신 80/443 포트로 서비스하려면:

### 5.1 Nginx 설치

```bash
sudo apt install nginx -y
```

### 5.2 Nginx 설정

```bash
# 기존 default 설정 백업
sudo mv /etc/nginx/sites-available/default /etc/nginx/sites-available/default.bak

# 새 설정 파일 생성
sudo nano /etc/nginx/sites-available/default
```

다음 내용 입력:

```nginx
server {
    listen 80;
    server_name _;  # 또는 your_domain.com

    # 클라이언트 요청 크기 제한 (파일 업로드용)
    client_max_body_size 10M;

    # Spring Boot 앱으로 프록시
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # WebSocket 지원 (필요시)
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # 헬스체크 엔드포인트
    location /actuator/health {
        proxy_pass http://localhost:8080/actuator/health;
        access_log off;
    }
}
```

**저장**: `Ctrl + X` → `Y` → `Enter`

### 5.3 Nginx 시작

```bash
# 설정 테스트
sudo nginx -t

# Nginx 재시작
sudo systemctl restart nginx

# 부팅 시 자동 시작 설정
sudo systemctl enable nginx

# 상태 확인
sudo systemctl status nginx
```

이제 `http://[YOUR_VM_IP]`로 접속 가능합니다!

---

## 🔄 Step 6: GitHub Actions로 자동 배포 설정

### 6.1 VM에 배포 스크립트 생성

```bash
cd ~/greenmap-back/greenmap
nano deploy.sh
```

다음 내용 입력:

```bash
#!/bin/bash

echo "🚀 Starting deployment..."

# 저장소 업데이트
cd ~/greenmap-back
git pull origin dev

cd greenmap

# 기존 컨테이너 중지 및 제거
echo "⏹️  Stopping existing container..."
docker stop greenmap || true
docker rm greenmap || true

# 이전 이미지 삭제 (선택사항)
docker rmi greenmap-app || true

# 새 이미지 빌드
echo "🔨 Building new image..."
docker build -t greenmap-app .

# 새 컨테이너 시작
echo "▶️  Starting new container..."
docker run -d \
  --name greenmap \
  --restart unless-stopped \
  -p 8080:8080 \
  --env-file .env \
  greenmap-app

# 로그 확인
echo "📋 Container logs:"
docker logs --tail 50 greenmap

echo "✅ Deployment completed!"
```

실행 권한 부여:

```bash
chmod +x deploy.sh
```

### 6.2 수동 배포 테스트

```bash
./deploy.sh
```

### 6.3 GitHub Actions 워크플로우 생성 (VM용)

로컬 개발 환경에서 다음 파일을 생성하겠습니다.

---

## 🔐 Step 7: Cloud SQL 연결 설정

### 7.1 Cloud SQL Private IP 확인

1. GCP Console → **SQL** → Cloud SQL 인스턴스 클릭
2. **연결** 탭 → **Private IP** 주소 복사

### 7.2 VM에서 Cloud SQL 접근 테스트

```bash
# MySQL 클라이언트 설치
sudo apt install mysql-client -y

# 연결 테스트
mysql -h [CLOUD_SQL_PRIVATE_IP] -u root -p
# 비밀번호 입력 후 연결 확인

# 연결 성공하면
mysql> show databases;
mysql> exit
```

### 7.3 네트워크 설정 확인

VM과 Cloud SQL이 같은 VPC 네트워크에 있어야 합니다:

-   GCP Console → **VPC 네트워크** → **VPC 네트워크**
-   VM과 Cloud SQL이 모두 `default` 네트워크에 있는지 확인

---

## 📊 Step 8: 모니터링 및 관리

### 8.1 유용한 Docker 명령어

```bash
# 컨테이너 상태 확인
docker ps

# 실시간 로그 확인
docker logs -f greenmap

# 최근 100줄 로그 확인
docker logs --tail 100 greenmap

# 컨테이너 재시작
docker restart greenmap

# 컨테이너 중지
docker stop greenmap

# 컨테이너 시작
docker start greenmap

# 컨테이너 내부 접속
docker exec -it greenmap /bin/sh
```

### 8.2 시스템 리소스 모니터링

```bash
# CPU, 메모리 사용량 확인
htop  # 설치: sudo apt install htop

# Docker 리소스 사용량
docker stats greenmap

# 디스크 사용량
df -h

# 로그 크기 확인
sudo du -sh /var/lib/docker/containers/*/*-json.log
```

### 8.3 로그 로테이션 설정

Docker 로그가 너무 커지는 것을 방지:

```bash
# Docker 데몬 설정
sudo nano /etc/docker/daemon.json
```

다음 내용 추가:

```json
{
    "log-driver": "json-file",
    "log-opts": {
        "max-size": "10m",
        "max-file": "3"
    }
}
```

Docker 재시작:

```bash
sudo systemctl restart docker
docker restart greenmap
```

---

## 🆘 문제 해결

### 애플리케이션이 시작되지 않을 때

```bash
# 상세 로그 확인
docker logs greenmap

# 환경변수 확인
docker exec greenmap env | grep DB_URL

# 포트 사용 확인
sudo netstat -tlnp | grep 8080
```

### 데이터베이스 연결 실패

```bash
# Cloud SQL 연결 테스트
ping [CLOUD_SQL_IP]
telnet [CLOUD_SQL_IP] 3306

# MySQL 직접 연결 테스트
mysql -h [CLOUD_SQL_IP] -u root -p
```

### 메모리 부족

```bash
# 메모리 사용량 확인
free -h

# VM 인스턴스 타입 업그레이드
# GCP Console → Compute Engine → 인스턴스 중지 → 수정 → 머신 유형 변경
```

### 디스크 공간 부족

```bash
# 디스크 사용량 확인
df -h

# Docker 정리
docker system prune -a

# 부팅 디스크 크기 증가
# GCP Console → Compute Engine → 디스크 → 크기 수정
```

---

## 💰 예상 비용

### VM 인스턴스 (e2-small, 서울 리전)

-   시간당: $0.0287
-   월간 (730시간): **약 $21**

### Cloud SQL (db-f1-micro)

-   월간: **약 $9**

### 고정 외부 IP

-   사용 중: **무료**
-   미사용 시: $7/월

### 총 예상 비용: **$30/월**

### 비용 절감 팁

1. **예약 할인**: 1년/3년 약정 시 최대 57% 할인
2. **사용량 기반 할인**: 월 사용량에 따라 자동 할인
3. **무료 크레딧**: 신규 가입 시 $300 크레딧 (90일)

---

## 🎯 다음 단계

### 1. HTTPS 설정 (Let's Encrypt)

```bash
# Certbot 설치
sudo apt install certbot python3-certbot-nginx -y

# SSL 인증서 발급 (도메인 필요)
sudo certbot --nginx -d your-domain.com
```

### 2. 도메인 연결

1. 도메인 DNS 설정에서 A 레코드 추가
2. IP 주소: VM의 고정 IP
3. TTL: 3600

### 3. 백업 설정

```bash
# 자동 백업 스크립트 작성
crontab -e

# 매일 새벽 2시에 백업
0 2 * * * docker commit greenmap greenmap-backup-$(date +\%Y\%m\%d)
```

### 4. 모니터링 강화

-   GCP Monitoring (구 Stackdriver) 설정
-   Uptime check 설정
-   알림 규칙 생성

---

## 📚 참고 자료

-   [GCP Compute Engine 문서](https://cloud.google.com/compute/docs)
-   [Docker 공식 문서](https://docs.docker.com/)
-   [Nginx 공식 문서](https://nginx.org/en/docs/)
-   [Spring Boot 프로덕션 가이드](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)

---

## 📞 지원

문제가 발생하면:

1. Docker 로그 확인: `docker logs greenmap`
2. Nginx 로그 확인: `sudo tail -f /var/log/nginx/error.log`
3. 시스템 로그 확인: `sudo journalctl -u docker`

---

**작성일**: 2025-11-03
**버전**: 1.0.0
**대상**: GCP 초보자
