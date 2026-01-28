--CREATE TABLE IF NOT EXISTS chatroom (
--    id BIGINT AUTO_INCREMENT PRIMARY KEY,
--    title VARCHAR(255),
--    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
--    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
--);


CREATE TABLE IF NOT EXISTS users (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    email VARCHAR(255),
    name VARCHAR(255),
    password VARCHAR(255),
    phone VARCHAR(255),
    role ENUM ('ADMIN','USER'),
    user_type ENUM ('CUSTOMER', 'OWNER'),
    must_change_password tinyint(1) default 0
);



CREATE TABLE IF NOT EXISTS email_verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255),
    email VARCHAR(255),
    expired_at DATETIME,
    verified TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS inquiry_chat (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 owner_id BIGINT DEFAULT NULL,
 user_id BIGINT DEFAULT NULL,
 restaurant_id BIGINT,
 created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
 updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 FOREIGN KEY (owner_id)
 REFERENCES users(id),

 FOREIGN KEY (user_id)
 REFERENCES users(id),

  CONSTRAINT uk_inquiry_chat_user_restaurant
  UNIQUE (user_id,restaurant_id)
);

CREATE TABLE IF NOT EXISTS message (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
  content VARCHAR(255),
  sender VARCHAR(255),
  user_id BIGINT,
  chatroom_id BIGINT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (user_id)
  REFERENCES users(id),

  FOREIGN KEY(chatroom_id)
  REFERENCES inquiry_chat(id)

);

CREATE TABLE IF NOT EXISTS refresh_entity(
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 expiration VARCHAR(255) ,
 refresh VARCHAR(255),
 username VARCHAR(255)
);


CREATE TABLE IF  NOT EXISTS user_chatroom_mapping (

 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 chatroom_id BIGINT,
 user_id BIGINT,
 last_read_message_id BIGINT

 FOREIGN KEY (chatroom_id)
 REFERENCES inquiry_chat(id),

 FOREIGN KEY (user_id)
 REFERENCES users(id),

 CONSTRAINT uq_user_chatroom
 UNIQUE (user_id, chatroom_id);
);


-- 레스토랑 이미지 및 레스토랑 키워드는 따로 테이블로 분류
CREATE TABLE IF NOT EXISTS restaurant (

 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(255),
 business_number VARCHAR(12),
 description TEXT,
 category ENUM('한식','중식','일식','양식','카페','분식','치킨','피자','베이커리','기타'),
 phone VARCHAR(255),
 address VARCHAR(255),
 latitude DECIMAL(10,7),
 longitude DECIMAL(10,7),
 divide VARCHAR(255),
 open_time TIME,
 close_time TIME,
 has_break_time TINYINT(1) NOT NULL DEFAULT 0,
 break_start_time TIME,
 break_end_time TIME,
 image_key VARCHAR(255),
 owner_id BIGINT,

 FOREIGN KEY (owner_id)
 REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS restaurant_keyword(

    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    keyword VARCHAR(255),

    FOREIGN KEY (restaurant_id)
    REFERENCES restaurant(id)
);

CREATE TABLE IF NOT EXISTS restaurant_images(

 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 restaurant_id BIGINT NOT NULL,
 image_key VARCHAR(255),
 sort_order TINYINT,

 FOREIGN KEY (restaurant_id)
 REFERENCES restaurant(id)

);

CREATE TABLE IF NOT EXISTS restaurant_review (

 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 content TEXT,
 user_id BIGINT,
 restaurant_id BIGINT,

 FOREIGN KEY (user_id)
 REFERENCES users(id),

 FOREIGN KEY (restaurant_id)
 REFERENCES restaurant(id)
);

CREATE TABLE user_like_restaurant(

    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,

    FOREIGN KEY (user_id)
    REFERENCES users(id),

    FOREIGN KEY (restaurant_id)
    REFERENCES restaurant(id)
);


CREATE TABLE user_score_restaurant(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    score TINYINT,
    user_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,



    FOREIGN KEY (user_id)
    REFERENCES users(id),

    FOREIGN KEY (restaurant_id)
    REFERENCES restaurant(id),

    CONSTRAINT uk_user_restaurant
    UNIQUE (user_id,restaurant_id)
);

create table menu(

	id bigint auto_increment primary key,
	name varchar(255),
	price int,
	restaurant_id BIGINT,

	foreign key (restaurant_id)
	references restaurant(id)
);

--레스토랑 인덱스
create index idx_category_divide on restaurant (category,divide);
create index idx_divide on restaurant (divide);


--레스토랑 키워드 인덱스
create index idx_restaurant_id on restaurant_keyword (restaurant_id);

--레스토랑 이미지 인덱스
create index idx_restaurant_id on restaurant_images (restaurant_id,sort_order);

--좋아요(찜) 인덱스
create index inx_restaurant_user on user_like_restaurant (user_id, restaurant_id);


--레스토랑 리뷰 인덱스
create index inx_restaurant_id on restaurant_review (restaurant_id, created_at);


--메뉴 인덱스

create index idx_restaurant_id on menu (restaurant_id);

--메시지 인덱스
create index idx_inquiry_chat on message (chatroom_id,id);

