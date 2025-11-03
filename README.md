# GreenMap Backend

Spring Boot 기반 환경 지도 서비스 백엔드 API

## 📚 배포 가이드

### Cloud Run 배포 (자동 스케일링)

-   📖 [Cloud Run 배포 가이드](greenmap/DEPLOYMENT_GUIDE.md)

### VM 배포 (단일 인스턴스, 파일 시스템 공유)

-   🚀 [VM 빠른 시작 가이드](greenmap/QUICK_START_VM.md)
-   📖 [VM 상세 배포 가이드](greenmap/GCP_VM_DEPLOYMENT_GUIDE.md)

## 🏗️ 프로젝트 구조

```
greenmap/
├── src/main/java/com/ucamp/greenmap/
│   ├── badge/          # 뱃지 시스템
│   ├── challenge/      # 챌린지 기능
│   ├── member/         # 회원 관리
│   ├── news/           # 뉴스 피드
│   ├── place/          # 장소 정보
│   ├── point/          # 포인트 시스템
│   ├── Kakao/          # 카카오 OAuth
│   └── ...
└── src/main/resources/
    ├── application.properties
    └── application-prod.properties
```

## 🚀 로컬 실행

```bash
cd greenmap
./gradlew bootRun
```

## 🐳 Docker 실행

```bash
cd greenmap
docker build -t greenmap-app .
docker run -p 8080:8080 greenmap-app
```

## 📝 환경 변수

필수 환경 변수:

-   `DB_URL`: 데이터베이스 연결 URL
-   `DB_USERNAME`: 데이터베이스 사용자명
-   `DB_PASSWORD`: 데이터베이스 비밀번호
-   `JWT_SECRET_KEY`: JWT 서명 키
-   `KAKAO_CLIENT_ID`: 카카오 REST API 키
-   기타 API 키들

## 🛠️ 기술 스택

-   **Framework**: Spring Boot 3.x
-   **Language**: Java 21
-   **Database**: MySQL (Cloud SQL)
-   **Build Tool**: Gradle
-   **Container**: Docker
-   **Cloud**: Google Cloud Platform (Cloud Run / Compute Engine)

## 📄 License

Copyright (c) 2025 UCamp Greenmap Team
