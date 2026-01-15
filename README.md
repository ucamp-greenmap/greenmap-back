# 🌱 Green Map

> **내 주변 친환경 시설을 한눈에 🌍**  
> 위치 기반으로 친환경 시설을 찾고,  
> 활동 인증을 통해 포인트를 적립하며 환경을 지켜요

🔗 **서비스 바로가기**  
👉 https://greenmap-ucamp.netlify.app/

---

## 📌 프로젝트 개요 (Project Overview)

| 항목 | 내용 |
|---|---|
| 프로젝트명 | **Green Map** |
| 개발 기간 | 2025.10.23 ~ 2025.11.12 |
| 배포 기간 | 2025.11.01 ~ |
| 목적 | 친환경 시설 정보 제공 및 환경 보호 활동 장려 |

---

## 👥 팀원 소개 (Team Members)
| 김승한 | 류예나 | 정재민 | 이정호 | 
|:------:|:------:|:------:|:------:| 
| <img src="https://avatars.githubusercontent.com/u/165791110?s=96&v=4" alt="김승한" width="150"> | <img src="https://avatars.githubusercontent.com/u/183960634?s=96&v=4" alt="류예나" width="150"> | <img src="https://avatars.githubusercontent.com/u/109647335?s=96&v=4" alt="정재민" width="150"> | <img src="https://avatars.githubusercontent.com/u/91371560?s=96&v=4" alt="이정호" width="150"> | 
| BE | BE | BE | FE | 
| [GitHub](https://github.com/ipcarepi) | [GitHub](https://github.com/Ryuyena0305) | [GitHub](https://github.com/sai06266) | [GitHub](https://github.com/paul-lee-dev) |
---

## ✨ 주요 기능 (Key Features)

### 👤 회원 & 마이페이지
- 소셜 로그인 (Kakao OAuth)
- 회원 정보 조회 / 수정 / 탈퇴
- 활동 기록 조회
- 북마크 등록 / 삭제

### 📰 뉴스
- 환경 관련 뉴스 제공
- 뉴스 목록 조회

### 🗺️ 친환경 시설 지도
- 현재 위치 기반 시설 조회
- 장소 검색 및 필터링
- 전기차 / 수소차 충전소
- 따릉이 스테이션
- 재활용 센터
- 제로웨이스트 매장
- 친환경 시설 데이터베이스 구축

### ✅ 인증 & 챌린지
- 친환경 활동 인증
  - 따릉이 이용
  - 전기차 / 수소차 이용
  - 제로웨이스트 사용
- 환경 챌린지 참여
- 이벤트 인증 내역 조회

### 🏆 포인트 & 뱃지
- 활동 기반 포인트 적립 / 사용
- 포인트 내역 조회
- 랭킹 시스템
- 성취 기반 뱃지 보상
- 뱃지 조회 및 대표 뱃지 설정

---

## 🗂 ERD

![ERD](https://github.com/user-attachments/assets/eca4cac7-c25b-4825-b087-3c6bdb72956c)

---

## 🏗 아키텍처 (Architecture)

![Architecture](https://github.com/user-attachments/assets/50403438-5533-43b7-b63a-cebbb47c2c03)

---

## 📑 API 명세서

> API 명세서는 **Notion**에서 관리 중입니다.

![API1](https://github.com/user-attachments/assets/75ed7e14-d280-485e-9d17-2ca39f606351)
![API2](https://github.com/user-attachments/assets/b8f166ff-7a63-4096-8566-11f5ab0888e1)

---

## 🧑‍💻 역할 분담 (Tasks & Responsibilities)

| 이름 | 프로필 | 담당 역할 | 
|------|--------|-----------| 
| 김승한 | <img src="https://avatars.githubusercontent.com/u/165791110?s=96&v=4" alt="김승한" width="150"> | <ul><li>조장</li><li>활동 인증</li><li>뉴스 조회</li></ul> | 
| 류예나 | <img src="https://avatars.githubusercontent.com/u/183960634?s=96&v=4" alt="류예나" width="150"> | <ul><li>소셜 로그인</li><li>마이페이지</li><li>챌린지</li></ul> | 
| 정재민 | <img src="https://avatars.githubusercontent.com/u/109647335?s=96&v=4" alt="정재민" width="150"> | <ul><li>포인트</li><li>장소 관리</li></ul> | 
| 이정호 | <img src="https://avatars.githubusercontent.com/u/91371560?s=96&v=4" alt="이정호" width="150"> | <ul><li>배포</li></ul> |

---

## 🛠 기술 스택 (Technology Stack)

### Language
| | | |
|-----------------|-----------------|-----------------| 
| Java |<img src="https://cdn.iconscout.com/icon/free/png-256/free-java-60-1174953.png?f=webp" alt="JAVA" width="100">| 17.0.11 |

### Backend
| | | | | |
|-----------------|-----------------|-----------------|-----------------|-----------------|
| Spring | <img src="https://blog.kakaocdn.net/dn/diVmCB/btqOcQWrLh9/K1AW5ftq5ih97pkt2rK9nk/img.png" alt="spring" width="100"> | 3.3.5 |JWT | <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8LJrZbMTtW1Hv4-ZIGC8UghRJUl8e6rzS9g&s" alt="Jwt" width="100"> | 
| MySQL | <img src="https://rastalion.dev/wp-content/uploads/2019/04/mysql_PNG19.png" alt="mysql" width="100"> | 8.4.3 |Oauth2 | <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRmrtF1vDXPOPYq-Hu1IYMf9DZReMpMte2fnw&s" alt="Oauth2" width="100"> | 
| Google Cloud | <img src="https://www.meshcloud.io/wp-content/uploads/2022/08/googlecloud.svg" alt="gcp" width="100"> | | Docker | <img src="https://avatars.githubusercontent.com/u/5429470?s=200&v=4" alt="Docker" width="100"> |

### Collaboration
| | | 
|-----------------|-----------------| 
| <img src="https://github.com/user-attachments/assets/483abc38-ed4d-487c-b43a-3963b33430e6" alt="git" width="100"> | <img src="https://github.com/user-attachments/assets/34141eb9-deca-416a-a83f-ff9543cc2f9a" alt="Notion" width="100"> |
---

## 📁 프로젝트 구조 (Project Structure)

<br/>

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

## 🔄 개발 워크플로우 (Development Workflow)

### GitHub Ruleset
- PR 시 **1명 이상 승인 필수**
- `develop` 브랜치 직접 커밋 금지

### 브랜치 전략
- 소문자 + 하이픈(`-`) 사용
- 필요 시 이슈 번호 포함

#### Branch Type
- `feat/` : 기능 개발
- `fix/` : 버그 수정
- `refactor/` : 리팩토링

**Example**

- `feat/add-social-login`



<br/>



## 🚀 실행 방법

### 로컬 실행
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

## 📄 License

Copyright (c) 2025 UCamp Greenmap Team
