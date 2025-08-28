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

# 소스 코드를 복사하기 전에, 각 모듈의 build.gradle 파일을 먼저 복사해야 의존성 분석이 가능
COPY mpl-domain/build.gradle ./mpl-domain/

# 의존성 다운로드
RUN ./gradlew :mpl-domain:dependencies

# 실제 소스 코드를 복사
COPY mpl-domain/src ./mpl-domain/src

# 빌드
RUN ./gradlew :mpl-domain:build -x test

# 런타임 스테이지
FROM amazoncorretto:17-alpine3.21

# 작업 디렉토리 설정
WORKDIR /app

ENV JVM_OPTS=""

# 빌드 스테이지에서 jar 파일만 복사
COPY --from=builder /app/mpl-domain/build/libs/*.jar app.jar

# 80 포트 노출시킬거면 아래 주석 해제
# EXPOSE 80

# jar 파일 실행
# ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]

ENTRYPOINT ["sh", "-c", "echo '--- Printing Environment Variables ---'; printenv; echo '--- Starting Application ---'; java ${JVM_OPTS} -jar app.jar"]