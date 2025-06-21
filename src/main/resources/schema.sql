CREATE DATABASE IF NOT EXISTS timeeat CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE timeeat;

drop table if exists member;
drop table if exists store;
drop table if exists bookmark;
drop table if exists menu;

CREATE TABLE `store`
(
    `id`            BIGINT       NOT NULL,
    `name`          VARCHAR(255) NOT NULL,
    `category`      VARCHAR(255) NOT NULL,
    `introduction`  TEXT         NOT NULL,
    `phone_number`  VARCHAR(255) NOT NULL COMMENT '(`-` 없이))',
    `interest_area` ENUM (
        '종로구', '중구', '용산구', '성동구', '광진구',
        '동대문구', '중랑구', '성북구', '강북구', '도봉구',
        '노원구', '은평구', '서대문구', '마포구', '양천구',
        '강서구', '구로구', '금천구', '영등포구', '동작구',
        '관악구', '서초구', '강남구', '송파구', '강동구'
        )                        NOT NULL COMMENT '(서울시 25개 구)',
    `address`       VARCHAR(255) NOT NULL COMMENT '(전체주소)',
    `latitude`      DOUBLE       NOT NULL,
    `longitude`     DOUBLE       NOT NULL,
    `open_time`     TIME         NOT NULL,
    `close_time`    TIME         NOT NULL,
    `image_url`     VARCHAR(511) NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `member`
(
    `id`               BIGINT       NOT NULL,
    `social_id`        BIGINT       NOT NULL,
    `nickname`         VARCHAR(255) NULL,
    `phone_number`     VARCHAR(255) NULL COMMENT '(`-` 없이))',
    `interest_area`    ENUM (
        '종로구', '중구', '용산구', '성동구', '광진구',
        '동대문구', '중랑구', '성북구', '강북구', '도봉구',
        '노원구', '은평구', '서대문구', '마포구', '양천구',
        '강서구', '구로구', '금천구', '영등포구', '동작구',
        '관악구', '서초구', '강남구', '송파구', '강동구'
        )                           NULL COMMENT '(서울시 25개 구)',
    `opt_in_marketing` BOOLEAN      NULL DEFAULT true,
    PRIMARY KEY (`id`)
);

CREATE TABLE `bookmark`
(
    `id`        BIGINT NOT NULL,
    `member_id` BIGINT NOT NULL,
    `store_id`  BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_member_store` (`member_id`, `store_id`)
);

CREATE TABLE `menu`
(
    `id`                  BIGINT       NOT NULL,
    `store_id`            BIGINT       NOT NULL,
    `name`                VARCHAR(255) NOT NULL,
    `description`         VARCHAR(255) NULL,
    `price`               INTEGER      NOT NULL,
    `discount_price`      INTEGER      NULL,
    `discount_start_time` TIME         NULL,
    `discount_end_time`   TIME         NULL,
    `image_url`           VARCHAR(511) NULL,
    PRIMARY KEY (`id`)
);
