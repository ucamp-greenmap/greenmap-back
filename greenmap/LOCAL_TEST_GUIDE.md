# 로컬 환경에서 배포된 백엔드 테스트 가이드

## 변경 사항

모든 하드코딩된 프론트엔드 URL을 환경 변수 `FRONTEND_URL`로 관리하도록 변경했습니다.

### 수정된 파일들:

1. **application.properties** - `frontend.url` 기본값: `http://localhost:5173`
2. **application-prod.properties** - `frontend.url` 기본값: `https://greenmap-ucamp.netlify.app`
3. **SecurityConfig.java** - logout URL에 환경 변수 사용
4. **OAuth2FailureHandler.java** - 실패 redirect URL에 환경 변수 사용
5. **OAuth2SuccessHandler.java** - 성공 redirect URL에 환경 변수 사용
6. **CorsConfig.java** - CORS allowed origins에 환경 변수 사용

## 로컬 테스트 방법

### 1. 환경 변수 설정

#### 방법 A: IDE에서 실행 시 (IntelliJ IDEA)

Run Configuration에서 Environment Variables 추가:

```
FRONTEND_URL=http://localhost:5173
```

#### 방법 B: .env 파일 사용

`.env.example`을 복사하여 `.env` 파일 생성:

```bash
cp .env.example .env
```

`.env` 파일에서 `FRONTEND_URL` 설정:

```
FRONTEND_URL=http://localhost:5173
```

#### 방법 C: 터미널에서 직접 실행 시

```bash
export FRONTEND_URL=http://localhost:5173
./gradlew bootRun
```

### 2. 프론트엔드 실행

프론트엔드를 `http://localhost:5173`에서 실행합니다.

### 3. 백엔드 실행

백엔드를 실행하면 자동으로 `FRONTEND_URL` 환경 변수를 읽어서:

-   CORS 설정
-   OAuth2 리다이렉트
-   로그아웃 리다이렉트

모두 `http://localhost:5173`으로 설정됩니다.

## 운영 환경 배포 시

운영 환경(GCP Cloud Run 등)에서는 환경 변수를 다음과 같이 설정:

```
FRONTEND_URL=https://greenmap-ucamp.netlify.app
```

또는 `application-prod.properties`의 기본값이 사용됩니다.

## 테스트 확인 사항

1. ✅ `http://localhost:5173`에서 프론트엔드 실행
2. ✅ 백엔드 API 호출 (CORS 확인)
3. ✅ 카카오 로그인 테스트 (성공/실패 리다이렉트 확인)
4. ✅ 로그아웃 테스트 (로그인 페이지로 리다이렉트 확인)

## 주의사항

-   로컬 테스트 시 반드시 `FRONTEND_URL=http://localhost:5173` 설정 필요
-   환경 변수 미설정 시 `application.properties`의 기본값 사용
-   프로덕션 배포 시 `FRONTEND_URL`을 배포된 프론트엔드 URL로 변경
