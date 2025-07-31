CREATE TABLE IF NOT EXISTS binary_contents (
        id BIGSERIAL PRIMARY KEY,
        created_at TIMESTAMP WITH TIME ZONE,
        file_name VARCHAR(255),
        size BIGINT,
        content_type VARCHAR(255),
        extension VARCHAR(255),
        url VARCHAR(2048),
        upload_status VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS contents (
        id BIGSERIAL PRIMARY KEY,
        created_at TIMESTAMP WITH TIME ZONE,
        title VARCHAR(255),
        category VARCHAR(255),
        description TEXT,
        binary_content_id BIGINT
);

CREATE TABLE IF NOT EXISTS live_chat_messages (
        id BIGSERIAL PRIMARY KEY,
        content TEXT,
        created_at TIMESTAMP WITH TIME ZONE,
        user_id BIGINT,
        chat_room_id BIGINT
);

CREATE TABLE IF NOT EXISTS live_chat_rooms (
        id BIGSERIAL PRIMARY KEY,
        content_id BIGINT,
        created_at TIMESTAMP WITH TIME ZONE,
        total_messages BIGINT
);

CREATE TABLE IF NOT EXISTS playlists (
        id BIGSERIAL PRIMARY KEY,
        created_at TIMESTAMP WITH TIME ZONE,
        updated_at TIMESTAMP WITH TIME ZONE,
        user_id BIGINT,
        subscription_count BIGINT,
        description VARCHAR(255),
        title VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS playlist_items (
        id BIGSERIAL PRIMARY KEY,
        created_at TIMESTAMP WITH TIME ZONE,
        playlist_id BIGINT,
        content_id BIGINT,
        order_index INTEGER
);

CREATE TABLE IF NOT EXISTS reviews (
        id BIGSERIAL PRIMARY KEY,
        created_at TIMESTAMP WITH TIME ZONE,
        updated_at TIMESTAMP WITH TIME ZONE,
        user_id BIGINT,
        content_id BIGINT,
        comment VARCHAR(255),
        rating INTEGER
);

CREATE TABLE IF NOT EXISTS follows (
        id BIGSERIAL PRIMARY KEY,
        from_user_id BIGINT,
        to_user_id BIGINT,
        created_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS users (
        id BIGSERIAL PRIMARY KEY,
        username VARCHAR(255),
        email VARCHAR(255) UNIQUE,
        password VARCHAR(255),
        is_locked BOOLEAN,
        is_deleted BOOLEAN,
        profile_id BIGINT,
        role VARCHAR(255),
        created_at TIMESTAMP WITH TIME ZONE,
        updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS notifications (
        id BIGSERIAL PRIMARY KEY,
        receiver_id BIGINT,
        title VARCHAR(255),
        content TEXT,
        type VARCHAR(255),
        target_id BIGINT,
        publisher_id BIGINT
);

CREATE TABLE IF NOT EXISTS direct_message_channels (
        id BIGSERIAL PRIMARY KEY,
        created_at TIMESTAMP WITH TIME ZONE,
        from_user_id BIGINT,
        to_user_id BIGINT
);

CREATE TABLE IF NOT EXISTS direct_messages (
        id BIGSERIAL PRIMARY KEY,
        content TEXT,
        created_at TIMESTAMP WITH TIME ZONE,
        user_id BIGINT,
        direct_message_channel_id BIGINT
);

-- live_chat_messages 테이블의 외래 키 제약 조건 추가
ALTER TABLE live_chat_messages
    ADD CONSTRAINT fk_live_chat_messages_user
        FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE live_chat_messages
    ADD CONSTRAINT fk_live_chat_messages_chat_room
        FOREIGN KEY (chat_room_id) REFERENCES live_chat_rooms (id);

---

-- live_chat_rooms 테이블의 외래 키 제약 조건 추가
ALTER TABLE live_chat_rooms
    ADD CONSTRAINT fk_live_chat_rooms_content
        FOREIGN KEY (content_id) REFERENCES contents (id);

---

-- playlists 테이블의 외래 키 제약 조건 추가
ALTER TABLE playlists
    ADD CONSTRAINT fk_playlists_user
        FOREIGN KEY (user_id) REFERENCES users (id);

---

-- playlist_items 테이블의 외래 키 제약 조건 추가
ALTER TABLE playlist_items
    ADD CONSTRAINT fk_playlist_items_playlist
        FOREIGN KEY (playlist_id) REFERENCES playlists (id);

ALTER TABLE playlist_items
    ADD CONSTRAINT fk_playlist_items_content
        FOREIGN KEY (content_id) REFERENCES contents (id);

---

-- reviews 테이블의 외래 키 제약 조건 추가
ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_user
        FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_content
        FOREIGN KEY (content_id) REFERENCES contents (id);

---

-- follows 테이블의 외래 키 제약 조건 추가
ALTER TABLE follows
    ADD CONSTRAINT fk_follows_from_user
        FOREIGN KEY (from_user_id) REFERENCES users (id);

ALTER TABLE follows
    ADD CONSTRAINT fk_follows_to_user
        FOREIGN KEY (to_user_id) REFERENCES users (id);

---

-- users 테이블의 외래 키 제약 조건 추가
ALTER TABLE users
    ADD COLUMN profile_id BIGINT,
    ADD CONSTRAINT fk_users_profile_id
        FOREIGN KEY (profile_id) REFERENCES binary_contents (id);

---

-- notifications 테이블의 외래 키 제약 조건 추가
ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_receiver
        FOREIGN KEY (receiver_id) REFERENCES users (id);

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_publisher
        FOREIGN KEY (publisher_id) REFERENCES users (id);

---

-- direct_message_channels 테이블의 외래 키 제약 조건 추가
ALTER TABLE direct_message_channels
    ADD CONSTRAINT fk_dm_channels_from_user
        FOREIGN KEY (from_user_id) REFERENCES users (id),
    ADD CONSTRAINT fk_dm_channels_to_user
        FOREIGN KEY (to_user_id) REFERENCES users (id);

-- direct_messages 테이블의 외래 키 제약 조건 추가
ALTER TABLE direct_messages
    ADD CONSTRAINT fk_dm_messages_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    ADD CONSTRAINT fk_dm_messages_channel
        FOREIGN KEY (direct_message_channel_id) REFERENCES direct_message_channels (id);


ALTER TABLE contents
    ADD CONSTRAINT fk_binary_content
        FOREIGN KEY (binary_content_id) REFERENCES binary_contents (id);