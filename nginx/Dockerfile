FROM nginx:1.25-alpine

LABEL maintainer="sb02-mpl-team2" \
      description="Custom Nginx for ECS Multi-Container Deployment" \
      version="1.0"

# 필수 패키지 설치
RUN apk update && \
    apk upgrade && \
    apk add --no-cache \
        curl \
        ca-certificates \
    && rm -rf /var/cache/apk/*

# 기본 nginx 설정 제거
RUN rm -rf /etc/nginx/conf.d/default.conf

# 커스텀 nginx 설정 파일 복사
COPY nginx.conf /etc/nginx/nginx.conf

# 에러 페이지 파일들 복사
COPY 50x.html /var/www/html/50x.html
COPY 404.html /var/www/html/404.html

# 로그 디렉토리 생성 및 권한 설정
RUN mkdir -p /var/log/nginx && \
    touch /var/log/nginx/access.log && \
    touch /var/log/nginx/error.log && \
    chown -R nginx:nginx /var/log/nginx

# nginx 캐시 및 임시 디렉토리 생성 및 권한 설정
RUN mkdir -p /var/cache/nginx/client_temp \
             /var/cache/nginx/proxy_temp \
             /var/cache/nginx/fastcgi_temp \
             /var/cache/nginx/uwsgi_temp \
             /var/cache/nginx/scgi_temp && \
    chown -R nginx:nginx /var/cache/nginx

# 공유 볼륨을 위한 디렉토리 생성
RUN mkdir -p /var/www/html && \
    chown -R nginx:nginx /var/www/html

# 기본 index.html 생성 (React 빌드 파일이 없을 때 대비)
RUN echo '<!DOCTYPE html><html><head><title>Loading...</title></head><body><h1>Application Loading...</h1><p>Please wait while the application starts.</p></body></html>' > /var/www/html/index.html

# ECS에서 포트 80 바인딩을 위해 root로 실행
# nginx.conf의 user 지시어로 워커 프로세스는 nginx 사용자로 실행됨

# 포트 80 노출
EXPOSE 80

# Nginx 시작 명령 (데몬 모드 비활성화 - 컨테이너에서 필수)
CMD ["nginx", "-g", "daemon off;"]