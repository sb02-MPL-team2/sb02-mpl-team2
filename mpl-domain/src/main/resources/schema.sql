-- Spring Batch 메타데이터 테이블
CREATE TABLE BATCH_JOB_INSTANCE
(
    JOB_INSTANCE_ID BIGINT       NOT NULL PRIMARY KEY,
    VERSION         BIGINT,
    JOB_NAME        VARCHAR(100) NOT NULL,
    JOB_KEY         VARCHAR(32)  NOT NULL,
    CONSTRAINT JOB_INST_UN UNIQUE (JOB_NAME, JOB_KEY)
);

CREATE TABLE BATCH_JOB_EXECUTION
(
    JOB_EXECUTION_ID BIGINT    NOT NULL PRIMARY KEY,
    VERSION          BIGINT,
    JOB_INSTANCE_ID  BIGINT    NOT NULL,
    CREATE_TIME      TIMESTAMP NOT NULL,
    START_TIME       TIMESTAMP DEFAULT NULL,
    END_TIME         TIMESTAMP DEFAULT NULL,
    STATUS           VARCHAR(10),
    EXIT_CODE        VARCHAR(2500),
    EXIT_MESSAGE     VARCHAR(2500),
    LAST_UPDATED     TIMESTAMP
);

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS
(
    JOB_EXECUTION_ID BIGINT       NOT NULL,
    PARAMETER_NAME   VARCHAR(100) NOT NULL,
    PARAMETER_TYPE   VARCHAR(100) NOT NULL,
    PARAMETER_VALUE  VARCHAR(2500),
    IDENTIFYING      CHAR(1)      NOT NULL
);

CREATE TABLE BATCH_STEP_EXECUTION
(
    STEP_EXECUTION_ID  BIGINT       NOT NULL PRIMARY KEY,
    VERSION            BIGINT       NOT NULL,
    STEP_NAME          VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID   BIGINT       NOT NULL,
    CREATE_TIME        TIMESTAMP    NOT NULL,
    START_TIME         TIMESTAMP DEFAULT NULL,
    END_TIME           TIMESTAMP DEFAULT NULL,
    STATUS             VARCHAR(10),
    COMMIT_COUNT       BIGINT,
    READ_COUNT         BIGINT,
    FILTER_COUNT       BIGINT,
    WRITE_COUNT        BIGINT,
    READ_SKIP_COUNT    BIGINT,
    WRITE_SKIP_COUNT   BIGINT,
    PROCESS_SKIP_COUNT BIGINT,
    ROLLBACK_COUNT     BIGINT,
    EXIT_CODE          VARCHAR(2500),
    EXIT_MESSAGE       VARCHAR(2500),
    LAST_UPDATED       TIMESTAMP
);

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT
(
    STEP_EXECUTION_ID  BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT
);

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT
(
    JOB_EXECUTION_ID   BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT
);

-- Spring Batch 시퀀스
CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;

-- 애플리케이션 테이블 생성
CREATE TABLE IF NOT EXISTS binary_contents
(
    id            BIGSERIAL PRIMARY KEY,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP,
    file_name     VARCHAR(255),
    size          BIGINT,
    content_type  VARCHAR(255),
    extension     VARCHAR(255),
    url           VARCHAR(2048),
    upload_status VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users
(
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(30)              NOT NULL UNIQUE,
    email           VARCHAR(50)              NOT NULL UNIQUE,
    password        VARCHAR(100)             NOT NULL,
    is_locked       BOOLEAN                  NOT NULL DEFAULT false,
    is_deleted      BOOLEAN                  NOT NULL DEFAULT false,
    follower_count  INTEGER                  NOT NULL DEFAULT 0,
    following_count INTEGER                  NOT NULL DEFAULT 0,
    picture_url     VARCHAR(255),
    provider_id     VARCHAR(255),
    provider        VARCHAR(255)             NOT NULL,
    role            VARCHAR(255)             NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE,
    profile_id      BIGINT UNIQUE
);

CREATE TABLE IF NOT EXISTS password_reset_token
(
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP    NOT NULL,
    user_id     BIGINT       NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS contents
(
    id          BIGSERIAL PRIMARY KEY,
    created_at  TIMESTAMP,
    title       VARCHAR(255),
    category    VARCHAR(255),
    description TEXT,
    image_url   TEXT,
    runtime     INTEGER,
    provider    VARCHAR(255),
    external_id VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS live_watch_rooms
(
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    user_id    BIGINT,
    content_id BIGINT UNIQUE
);

CREATE TABLE IF NOT EXISTS live_watch_messages
(
    id                 BIGSERIAL PRIMARY KEY,
    content            TEXT         NOT NULL,
    message_type       VARCHAR(255) NOT NULL,
    sent_at            TIMESTAMP    NOT NULL DEFAULT now(),
    user_id            BIGINT,
    live_watch_room_id BIGINT
);

CREATE TABLE IF NOT EXISTS notifications
(
    id           BIGSERIAL PRIMARY KEY,
    created_at   TIMESTAMP    NOT NULL,
    content      TEXT         NOT NULL,
    publisher_id BIGINT,
    receiver_id  BIGINT,
    target_id    BIGINT,
    title        VARCHAR(255) NOT NULL,
    type         VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS alarm_setting
(
    id                                        BIGSERIAL PRIMARY KEY,
    follow_alarm_enabled                      BOOLEAN NOT NULL DEFAULT true,
    permission_change_alarm_enabled           BOOLEAN NOT NULL DEFAULT true,
    new_playlist_from_following_alarm_enabled BOOLEAN NOT NULL DEFAULT true,
    subscribe_playlist_alarm_enable           BOOLEAN NOT NULL DEFAULT true,
    dm_alarm_enabled                          BOOLEAN NOT NULL DEFAULT true,
    recommend_playlist_alarm_enabled          BOOLEAN NOT NULL DEFAULT true,
    user_id                                   BIGINT  NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS playlists
(
    id          BIGSERIAL PRIMARY KEY,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    title       VARCHAR(255),
    description VARCHAR(255),
    user_id     BIGINT
);

CREATE TABLE IF NOT EXISTS playlist_items
(
    id          BIGSERIAL PRIMARY KEY,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    order_index INTEGER,
    content_id  BIGINT,
    playlist_id BIGINT
);

CREATE TABLE IF NOT EXISTS subscribes
(
    id            BIGSERIAL PRIMARY KEY,
    subscribed_at TIMESTAMP,
    playlist_id   BIGINT,
    user_id       BIGINT,
    CONSTRAINT uq_subscribes_user_playlist UNIQUE (user_id, playlist_id)
);

CREATE TABLE IF NOT EXISTS playlist_subscriber_history
(
    id               BIGSERIAL PRIMARY KEY,
    created_at       TIMESTAMP,
    subscriber_count INTEGER,
    playlist_id      BIGINT
);

CREATE TABLE IF NOT EXISTS playlist_score
(
    id          BIGSERIAL PRIMARY KEY,
    created_at  TIMESTAMP,
    score       DOUBLE PRECISION,
    playlist_id BIGINT
);

CREATE TABLE IF NOT EXISTS reviews
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    comment    VARCHAR(255) NOT NULL,
    rating     INTEGER      NOT NULL,
    content_id BIGINT       NOT NULL,
    user_id    BIGINT       NOT NULL
);

CREATE TABLE IF NOT EXISTS direct_message_channels
(
    id           BIGSERIAL PRIMARY KEY,
    created_at   TIMESTAMP NOT NULL,
    from_user_id BIGINT    NOT NULL,
    to_user_id   BIGINT    NOT NULL,
    CONSTRAINT uq_dm_channel_users UNIQUE (from_user_id, to_user_id)
);

CREATE TABLE IF NOT EXISTS direct_messages
(
    id                        BIGSERIAL PRIMARY KEY,
    content                   TEXT      NOT NULL,
    created_at                TIMESTAMP NOT NULL,
    direct_message_channel_id BIGINT    NOT NULL,
    user_id                   BIGINT
);

CREATE TABLE IF NOT EXISTS follows
(
    id           BIGSERIAL PRIMARY KEY,
    created_at   TIMESTAMP NOT NULL,
    from_user_id BIGINT    NOT NULL,
    to_user_id   BIGINT    NOT NULL,
    CONSTRAINT uq_follows_users UNIQUE (from_user_id, to_user_id)
);

-- 외래 키(Foreign Key) 및 제약 조건 추가
ALTER TABLE BATCH_JOB_EXECUTION
    ADD CONSTRAINT JOB_INST_EXEC_FK FOREIGN KEY (JOB_INSTANCE_ID) REFERENCES BATCH_JOB_INSTANCE (JOB_INSTANCE_ID);
ALTER TABLE BATCH_JOB_EXECUTION_PARAMS
    ADD CONSTRAINT JOB_EXEC_PARAMS_FK FOREIGN KEY (JOB_EXECUTION_ID) REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID);
ALTER TABLE BATCH_STEP_EXECUTION
    ADD CONSTRAINT JOB_EXEC_STEP_FK FOREIGN KEY (JOB_EXECUTION_ID) REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID);
ALTER TABLE BATCH_STEP_EXECUTION_CONTEXT
    ADD CONSTRAINT STEP_EXEC_CTX_FK FOREIGN KEY (STEP_EXECUTION_ID) REFERENCES BATCH_STEP_EXECUTION (STEP_EXECUTION_ID);
ALTER TABLE BATCH_JOB_EXECUTION_CONTEXT
    ADD CONSTRAINT JOB_EXEC_CTX_FK FOREIGN KEY (JOB_EXECUTION_ID) REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID);

ALTER TABLE users
    ADD CONSTRAINT fk_users_profile FOREIGN KEY (profile_id) REFERENCES binary_contents (id);
ALTER TABLE password_reset_token
    ADD CONSTRAINT fk_password_reset_token_user FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE live_watch_rooms
    ADD CONSTRAINT fk_live_watch_rooms_user FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE live_watch_rooms
    ADD CONSTRAINT fk_live_watch_rooms_content FOREIGN KEY (content_id) REFERENCES contents (id);
ALTER TABLE live_watch_messages
    ADD CONSTRAINT fk_live_watch_messages_user FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE live_watch_messages
    ADD CONSTRAINT fk_live_watch_messages_room FOREIGN KEY (live_watch_room_id) REFERENCES live_watch_rooms (id);
ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_receiver FOREIGN KEY (receiver_id) REFERENCES users (id);
ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_publisher FOREIGN KEY (publisher_id) REFERENCES users (id);
ALTER TABLE alarm_setting
    ADD CONSTRAINT fk_alarm_setting_user FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE playlists
    ADD CONSTRAINT fk_playlists_user FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE playlist_items
    ADD CONSTRAINT fk_playlist_items_playlist FOREIGN KEY (playlist_id) REFERENCES playlists (id);
ALTER TABLE playlist_items
    ADD CONSTRAINT fk_playlist_items_content FOREIGN KEY (content_id) REFERENCES contents (id);
ALTER TABLE subscribes
    ADD CONSTRAINT fk_subscribes_playlist FOREIGN KEY (playlist_id) REFERENCES playlists (id);
ALTER TABLE subscribes
    ADD CONSTRAINT fk_subscribes_user FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE playlist_subscriber_history
    ADD CONSTRAINT fk_playlist_subscriber_history_playlist FOREIGN KEY (playlist_id) REFERENCES playlists (id);
ALTER TABLE playlist_score
    ADD CONSTRAINT fk_playlist_score_playlist FOREIGN KEY (playlist_id) REFERENCES playlists (id);
ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_content FOREIGN KEY (content_id) REFERENCES contents (id);
ALTER TABLE direct_message_channels
    ADD CONSTRAINT fk_dm_channels_from_user FOREIGN KEY (from_user_id) REFERENCES users (id);
ALTER TABLE direct_message_channels
    ADD CONSTRAINT fk_dm_channels_to_user FOREIGN KEY (to_user_id) REFERENCES users (id);
ALTER TABLE direct_messages
    ADD CONSTRAINT fk_dm_messages_user FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE direct_messages
    ADD CONSTRAINT fk_dm_messages_channel FOREIGN KEY (direct_message_channel_id) REFERENCES direct_message_channels (id);
ALTER TABLE follows
    ADD CONSTRAINT fk_follows_from_user FOREIGN KEY (from_user_id) REFERENCES users (id);
ALTER TABLE follows
    ADD CONSTRAINT fk_follows_to_user FOREIGN KEY (to_user_id) REFERENCES users (id);