# GCP Cloud Run 자동 배포 가이드

이 문서는 GitHub Actions를 사용하여 Spring Boot 애플리케이션을 Google Cloud Run에 자동으로 배포하는 방법을 안내합니다.

## 📋 사전 준비사항

### 1. GCP 계정 및 프로젝트
- Google Cloud 계정 생성 (https://cloud.google.com)
- 신용카드 등록 (무료 크레딧 $300 제공)
- 새 프로젝트 생성

### 2. 로컬 환경
- Docker 설치
- gcloud CLI 설치 (선택사항)

---

## 🚀 단계별 배포 설정

### Step 1: GCP 프로젝트 설정

#### 1.1 GCP 콘솔에서 프로젝트 생성
```
1. https://console.cloud.google.com 접속
2. 상단 프로젝트 선택 → "새 프로젝트" 클릭
3. 프로젝트 이름: greenmap-prod (원하는 이름으로 변경 가능)
4. "만들기" 클릭
5. 프로젝트 ID를 메모해두세요 (예: greenmap-prod-123456)
```

#### 1.2 필요한 API 활성화
```
왼쪽 메뉴 → "API 및 서비스" → "라이브러리"에서 다음 API 검색 후 활성화:
- Cloud Run API
- Cloud Build API
- Artifact Registry API
- Secret Manager API
```

또는 gcloud CLI로 한번에 활성화:
```bash
gcloud services enable run.googleapis.com \
  cloudbuild.googleapis.com \
  artifactregistry.googleapis.com \
  secretmanager.googleapis.com
```

### Step 2: Artifact Registry 설정 (Docker 이미지 저장소)

#### 2.1 저장소 생성
```
1. GCP 콘솔 → "Artifact Registry" 메뉴
2. "저장소 만들기" 클릭
3. 설정:
   - 이름: greenmap
   - 형식: Docker
   - 리전: asia-northeast3 (서울)
   - 암호화: Google 관리 암호화 키
4. "만들기" 클릭
```

또는 gcloud CLI 사용:
```bash
gcloud artifacts repositories create greenmap \
  --repository-format=docker \
  --location=asia-northeast3 \
  --description="Greenmap Docker repository"
```

### Step 3: Cloud SQL (MySQL) 설정

#### 3.1 Cloud SQL 인스턴스 생성
```
1. GCP 콘솔 → "SQL" 메뉴
2. "인스턴스 만들기" → "MySQL 선택"
3. 설정:
   - 인스턴스 ID: greenmap-db
   - 비밀번호: 강력한 root 비밀번호 설정 (메모!)
   - 데이터베이스 버전: MySQL 8.0
   - 리전: asia-northeast3 (서울)
   - 영역 가용성: 단일 영역 (개발용)
   - 머신 유형: db-f1-micro (최소 사양, 필요시 조정)
4. "인스턴스 만들기" 클릭 (5-10분 소요)
```

#### 3.2 데이터베이스 생성
```
1. 생성된 인스턴스 클릭
2. "데이터베이스" 탭 → "데이터베이스 만들기"
3. 이름: greenmap
4. "만들기" 클릭
```

#### 3.3 Cloud Run에서 접근 설정
```
1. 인스턴스 상세 페이지
2. "연결" 탭
3. "연결 이름" 복사 (예: project-id:region:instance-name)
```

### Step 4: Secret Manager 설정 (환경변수 저장)

#### 4.1 각 시크릿 생성
```
GCP 콘솔 → "Secret Manager" 메뉴 → "보안 비밀 만들기"

다음 시크릿들을 각각 생성하세요:
```

| 시크릿 이름 | 값 예시 | 설명 |
|------------|---------|------|
| DB_URL | jdbc:mysql://10.x.x.x:3306/greenmap?useSSL=false | Cloud SQL 연결 URL |
| DB_USERNAME | root | 데이터베이스 사용자명 |
| DB_PASSWORD | your-db-password | 데이터베이스 비밀번호 |
| JWT_SECRET_KEY | your-jwt-secret-key-min-256bits | JWT 서명 키 (최소 256비트) |
| JWT_MS | 86400000 | JWT 만료 시간 (밀리초) |
| KAKAO_CLIENT_ID | your-kakao-rest-api-key | 카카오 REST API 키 |
| KAKAO_REDIRECT_URI | https://your-domain.com/api/kakao/callback | 카카오 리다이렉트 URI |
| KAKAO_REDIRECT_LOCAL | http://localhost:8080/api/kakao/callback | 로컬 테스트용 |
| KAKAO_REDIRECT_PROD | https://your-domain.com/api/kakao/callback | 프로덕션 |
| CLIENT_ID | your-naver-client-id | 네이버 API 클라이언트 ID |
| CLIENT_SECRET | your-naver-client-secret | 네이버 API 시크릿 |

**주의**: DB_URL의 IP 주소는 Cloud SQL의 Private IP를 사용하세요.

gcloud CLI 사용 예시:
```bash
echo -n "jdbc:mysql://10.x.x.x:3306/greenmap" | \
  gcloud secrets create DB_URL --data-file=-

echo -n "root" | \
  gcloud secrets create DB_USERNAME --data-file=-

echo -n "your-password" | \
  gcloud secrets create DB_PASSWORD --data-file=-
```

### Step 5: 서비스 계정 생성 및 권한 설정

#### 5.1 서비스 계정 생성
```
1. GCP 콘솔 → "IAM 및 관리" → "서비스 계정"
2. "서비스 계정 만들기" 클릭
3. 설정:
   - 이름: github-actions-deployer
   - 설명: GitHub Actions에서 배포할 때 사용하는 계정
4. "만들기 및 계속하기" 클릭
```

#### 5.2 역할 부여
다음 역할들을 추가하세요:
- Cloud Run Admin
- Service Account User
- Artifact Registry Writer
- Secret Manager Secret Accessor
- Storage Admin

```bash
# gcloud CLI로 역할 부여
PROJECT_ID="your-project-id"
SA_EMAIL="github-actions-deployer@${PROJECT_ID}.iam.gserviceaccount.com"

gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/run.admin"

gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/iam.serviceAccountUser"

gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/artifactregistry.writer"

gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/secretmanager.secretAccessor"

gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member="serviceAccount:${SA_EMAIL}" \
  --role="roles/storage.admin"
```

#### 5.3 JSON 키 생성
```
1. 생성한 서비스 계정 클릭
2. "키" 탭 → "키 추가" → "새 키 만들기"
3. JSON 선택 → "만들기"
4. 다운로드된 JSON 파일을 안전하게 보관
```

### Step 6: GitHub Secrets 설정

#### 6.1 GitHub 저장소 설정
```
1. GitHub 저장소 페이지 이동
2. "Settings" → "Secrets and variables" → "Actions"
3. "New repository secret" 클릭
```

#### 6.2 필요한 Secrets 추가

| Secret 이름 | 값 | 설명 |
|-------------|-----|------|
| GCP_PROJECT_ID | your-project-id | GCP 프로젝트 ID |
| GCP_SA_KEY | JSON 키 내용 전체 | 다운로드한 서비스 계정 JSON 파일의 전체 내용을 복사 |

**GCP_SA_KEY 설정 방법**:
1. 다운로드한 JSON 파일을 텍스트 에디터로 열기
2. 전체 내용을 복사 (첫 `{`부터 마지막 `}`까지)
3. GitHub Secret에 붙여넣기

### Step 7: 첫 배포 실행

#### 7.1 코드 푸시
```bash
git add .
git commit -m "Add deployment configuration"
git push origin main
```

#### 7.2 배포 진행 확인
```
1. GitHub 저장소 → "Actions" 탭
2. 실행 중인 워크플로우 클릭
3. 각 단계별 로그 확인
```

#### 7.3 배포 완료 확인
```
1. GCP 콘솔 → "Cloud Run" 메뉴
2. "greenmap-api" 서비스 클릭
3. 상단에 표시된 URL 확인 (예: https://greenmap-api-xxxxx-an.a.run.app)
4. 해당 URL로 접속하여 API 동작 확인
```

---

## 🔧 문제 해결

### 빌드 실패 시
```bash
# 로컬에서 Docker 빌드 테스트
cd greenmap
docker build -t test-image .
```

### 데이터베이스 연결 실패 시
- Cloud SQL의 Private IP 확인
- Cloud Run 서비스에 Cloud SQL 연결 추가:
```bash
gcloud run services update greenmap-api \
  --add-cloudsql-instances your-project-id:asia-northeast3:greenmap-db \
  --region asia-northeast3
```

### Secret 접근 권한 오류
```bash
# Secret Manager 권한 재확인
gcloud projects get-iam-policy your-project-id \
  --flatten="bindings[].members" \
  --filter="bindings.members:serviceAccount:github-actions-deployer*"
```

---

## 📊 모니터링 및 로그

### 로그 확인
```
1. GCP 콘솔 → "Cloud Run" → "greenmap-api" 서비스
2. "로그" 탭 클릭
3. 실시간 로그 스트림 확인
```

### 비용 모니터링
```
1. GCP 콘솔 → "결제" 메뉴
2. "예산 및 알림" 설정
3. 월 예산 한도 설정 (예: $10)
```

---

## 🎯 다음 단계

1. **커스텀 도메인 연결**: Cloud Run에 본인 도메인 연결
2. **CI/CD 개선**: 테스트 자동화, 스테이징 환경 추가
3. **보안 강화**: VPC 설정, IAP 설정
4. **성능 최적화**: CDN 설정, 캐싱 전략

---

## 💰 예상 비용 (월간)

- Cloud Run: $0-5 (트래픽 따라 변동, 무료 할당량 있음)
- Cloud SQL (db-f1-micro): ~$9
- Artifact Registry: ~$0.1
- Secret Manager: ~$0.06
- **총 예상**: $10-15/월

무료 크레딧 $300으로 약 20개월 이상 사용 가능합니다.

---

## 📞 지원

문제가 발생하면:
1. GitHub Actions 로그 확인
2. Cloud Run 로그 확인
3. GCP 커뮤니티 포럼 활용

---

**작성일**: 2025-10-30
**버전**: 1.0.0
