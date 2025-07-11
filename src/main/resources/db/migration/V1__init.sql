CREATE TABLE `store`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(255) NOT NULL,
    `category`      VARCHAR(255) NOT NULL,
    `introduction`  TEXT         NOT NULL,
    `phone_number`  VARCHAR(255) NOT NULL COMMENT '(`-` 없이)',
    `interest_area` VARCHAR(50)  NOT NULL COMMENT '(서울시 25개 구, Java Enum 이름으로 저장: ex, GANGNAM)',
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
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `email`            VARCHAR(255) NOT NULL,
    `social_id`        VARCHAR(255) NOT NULL,
    `nickname`         VARCHAR(255) NULL,
    `phone_number`     VARCHAR(255) NULL COMMENT '(`-` 없이))',
    `opt_in_marketing` BOOLEAN      NULL DEFAULT true,
    PRIMARY KEY (`id`)
);

CREATE TABLE `bookmark`
(
    `id`        BIGINT NOT NULL AUTO_INCREMENT,
    `member_id` BIGINT NOT NULL,
    `store_id`  BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_member_store` (`member_id`, `store_id`)
);

CREATE TABLE `menu`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `store_id`       BIGINT       NOT NULL,
    `name`           VARCHAR(255) NOT NULL,
    `description`    VARCHAR(255) NULL,
    `price`          INTEGER      NOT NULL,
    `discount_price` INTEGER      NULL,
    `start_time`     DATETIME     NULL,
    `end_time`       DATETIME     NULL,
    `image_url`      VARCHAR(511) NULL,
    PRIMARY KEY (`id`)
);

