# 빌드 스테이지
FROM amazoncorretto:17 AS builder
# 타임존 설정: 서울
ENV TZ=Asia/Seoul

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 파일 먼저 복사
COPY gradle ./gradle
COPY gradlew ./gradlew

# Gradle 캐시를 위한 의존성 파일 복사
COPY build.gradle settings.gradle ./

# 의존성 다운로드
RUN ./gradlew dependencies

# 소스 코드 복사 및 빌드
COPY src ./src
RUN ./gradlew build -x test

# 런타임 스테이지
FROM amazoncorretto:17-alpine3.21

# 작업 디렉토리 설정
WORKDIR /app

ENV JVM_OPTS=""

# 빌드 스테이지에서 jar 파일만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 80 포트 노출시킬거면 아래 주석 해제
# EXPOSE 80

# jar 파일 실행
ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]