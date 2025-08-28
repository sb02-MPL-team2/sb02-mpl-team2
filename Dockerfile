FROM amazoncorretto:17 AS builder

# 타임존 설정: 서울
ENV TZ=Asia/Seoul

# 작업 디렉토리 설정
WORKDIR /app

COPY gradle ./gradle
COPY gradlew ./
COPY settings.gradle ./

COPY build.gradle ./
COPY mpl-domain/build.gradle ./mpl-domain/

RUN ./gradlew dependencies

COPY . .
# 프로젝트 빌드 (실행 가능한 Jar 생성)
RUN ./gradlew :mpl-domain:bootJar -x test

#런타임 스테이지 (Runtime Stage)
FROM amazoncorretto:17-alpine3.21

WORKDIR /app

# JVM 옵션을 환경 변수로 설정하여 유연성 확보
ENV PROJECT_NAME=sb02-mpl-team2 \
    PROJECT_VERSION=0.0.1-SNAPSHOT \
    JVM_OPTS="-Duser.timezone=Asia/Seoul  -Xms1g -Xmx1g"

COPY --from=builder /app/mpl-domain/build/libs/*.jar app.jar

EXPOSE 8080

#애플리케이션 실행
ENTRYPOINT ["sh", "-c", "echo '--- Printing Environment Variables ---'; printenv; echo '--- Starting Application ---'; java ${JVM_OPTS} -Dspring.profiles.active=prod -jar app.jar"]
