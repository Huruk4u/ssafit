--  다음 user의 변경사항
  1. profile_image, background_image, 는 File이라는 객체로서 유저에게 존재하지 않을까 생각중
  2. height, weight는 유저의 챌린지 기록으로 자연스럽게 조사될 예정

CREATE TABLE users (
                       user_id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
                       username         VARCHAR(100) NOT NULL UNIQUE,
                       password         VARCHAR(255) NOT NULL,
                       nickname         VARCHAR(100) NOT NULL,
                       email            VARCHAR(255),
                       profile_image    VARCHAR(255),
                       background_image VARCHAR(255),
                       badge_id         VARCHAR(100),
                       height           DECIMAL(5,2),
                       weight           DECIMAL(5,2),
                       enabled          BOOLEAN      DEFAULT TRUE,
                       created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (badge_id) REFERENCES badges(badge_id)
) ENGINE=InnoDB;