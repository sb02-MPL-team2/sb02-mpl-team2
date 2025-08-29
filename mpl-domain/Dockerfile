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

ENV JVM_OPTS="-Xms512m -Xmx1200m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Duser.timezone=Asia/Seoul"

# 빌드 스테이지에서 실행 가능한 jar 파일만 복사 (plain.jar 제외)
COPY --from=builder /app/mpl-domain/build/libs/mpl-domain.jar app.jar

# ECS 공유 볼륨을 위한 정적 파일 복사
COPY --from=builder /app/mpl-domain/src/main/resources/static /app/static-source

# Spring Boot 기본 포트 노출
EXPOSE 8080

# 간단한 시작 스크립트를 RUN으로 생성
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'echo "=== Static files copy process starting ==="' >> /app/start.sh && \
    echo 'mkdir -p /app/static' >> /app/start.sh && \
    echo 'if [ -d "/app/static-source" ] && [ "$(ls -A /app/static-source)" ]; then' >> /app/start.sh && \
    echo '    echo "Copying static files from /app/static-source to /app/static"' >> /app/start.sh && \
    echo '    cp -rv /app/static-source/* /app/static/' >> /app/start.sh && \
    echo '    echo "Static files copied successfully. Contents of /app/static:"' >> /app/start.sh && \
    echo '    ls -la /app/static/' >> /app/start.sh && \
    echo '    chmod -R 755 /app/static' >> /app/start.sh && \
    echo 'else' >> /app/start.sh && \
    echo '    echo "Warning: No static files found in /app/static-source or directory is empty"' >> /app/start.sh && \
    echo '    echo "Creating placeholder index.html"' >> /app/start.sh && \
    echo '    echo "<!DOCTYPE html><html><head><title>Loading...</title></head><body><h1>Application Loading...</h1></body></html>" > /app/static/index.html' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo 'echo "=== Static files copy process completed ==="' >> /app/start.sh && \
    echo 'echo "Starting Spring Boot application..."' >> /app/start.sh && \
    echo 'exec java ${JVM_OPTS} -jar app.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

# 시작 스크립트 실행
ENTRYPOINT ["/app/start.sh"]
