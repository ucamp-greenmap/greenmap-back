# 멀티 스테이지 빌드를 사용하여 최종 이미지 크기 최적화
# 1단계: 빌드 스테이지
FROM gradle:8.5-jdk21 AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 설정 파일 복사 (의존성 캐싱을 위해 먼저 복사)
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성 다운로드 (캐시 레이어 생성)
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드 (테스트 제외, 캐시 활용)
RUN ./gradlew build -x test --no-daemon --build-cache

# 2단계: 실행 스테이지
FROM eclipse-temurin:21-jre-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션 포트 노출 (Spring Boot 기본 포트)
EXPOSE 8080

# 환경 변수 설정 (프로덕션 프로파일 사용)
ENV SPRING_PROFILES_ACTIVE=prod

# JVM 옵션 설정 (메모리 최적화)
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
