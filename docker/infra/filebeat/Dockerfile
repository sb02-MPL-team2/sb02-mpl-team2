# 1. Filebeat 공식 이미지를 기반으로 시작합니다.
FROM docker.elastic.co/beats/filebeat:8.19.2

# 2. root 사용자로 전환하여 파일 복사 및 권한 변경 작업을 수행합니다.
USER root

# 3. 호스트에 있는 filebeat.yml을 컨테이너 안으로 복사합니다.
COPY filebeat.yml /usr/share/filebeat/filebeat.yml

# 4. root 권한으로 권한 변경을 실행합니다. (이제 성공합니다)
RUN chmod go-w /usr/share/filebeat/filebeat.yml

# 5. 보안을 위해 다시 원래의 filebeat 사용자로 전환합니다.
USER filebeat