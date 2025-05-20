-- ========================================
-- 1. 데이터베이스 생성/사용
-- ========================================
DROP DATABASE IF EXISTS ssafitdb;
CREATE DATABASE IF NOT EXISTS ssafitdb;
USE ssafitdb;

-- ========================================
-- 2. 유저 / 관리자 / 배지
-- ========================================
CREATE TABLE badges (
                        badge_id    VARCHAR(100) PRIMARY KEY,
                        name        VARCHAR(100) NOT NULL,
                        icon_url    VARCHAR(255),
                        description TEXT
) ENGINE=InnoDB;

CREATE TABLE users (
                       user_id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
                       username         VARCHAR(100) NOT NULL UNIQUE,
                       password         VARCHAR(255) NOT NULL,
                       nickname         VARCHAR(100),
                       email            VARCHAR(255),
                       profile_image    VARCHAR(255),
                       background_image VARCHAR(255),
                       badge_id         VARCHAR(100),
                       height           DECIMAL(5,2),
                       weight           DECIMAL(5,2),
                       enabled          BOOLEAN      DEFAULT TRUE,
                       suspend_start TIMESTAMP NULL,
                       suspend_end   TIMESTAMP NULL,
                       created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
                       first_exercise VARCHAR(50) NULL,
                       second_exercise VARCHAR(50) NULL,
                       third_exercise VARCHAR(50) NULL,
                       role VARCHAR(20) DEFAULT 'ROLE_USER',
                       FOREIGN KEY (badge_id) REFERENCES badges(badge_id)
) ENGINE=InnoDB;

CREATE TABLE user_badges (
                             user_id        BIGINT,
                             badge_id       VARCHAR(100),
                             earned_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             is_represented BOOLEAN DEFAULT FALSE,
                             PRIMARY KEY (user_id, badge_id),
                             FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                             FOREIGN KEY (badge_id) REFERENCES badges(badge_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ========================================
-- 3. 챌린지 / 인바디
-- ========================================
CREATE TABLE challenges (
                            challenge_id        BIGINT    PRIMARY KEY AUTO_INCREMENT,
                            user_id             BIGINT    NOT NULL,
                            record_date DATE NOT NULL,
                            created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(user_id),
                            UNIQUE(user_id, record_date)
) ENGINE=InnoDB;

CREATE TABLE inbody_data (
                             inbody_id    BIGINT    PRIMARY KEY AUTO_INCREMENT,
                             user_id      BIGINT    NOT NULL,
                             weight       DECIMAL(5,2),
                             muscle_mass  DECIMAL(5,2),
                             body_fat     DECIMAL(5,2),
                             body_fat_percentage DECIMAL(5,2),
                             bmi          DECIMAL(5,2),
                             uploaded_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- ========================================
-- 4. 게시글 / 댓글 / 좋아요·싫어요
-- ========================================

CREATE TABLE articles (
                          article_id  BIGINT     PRIMARY KEY AUTO_INCREMENT,
                          user_id     BIGINT     NOT NULL,
                          category    VARCHAR(255)  NOT NULL,
                          title       VARCHAR(255) NOT NULL,
                          content     TEXT,
                          tag    VARCHAR(255),
                          view_count BIGINT DEFAULT 0,
                          like_count    BIGINT NOT NULL DEFAULT 0,
                          dislike_count    BIGINT NOT NULL DEFAULT 0,
                          url         VARCHAR(255),
                          created_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
                          updated_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (user_id)  REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE comments (
                          comment_id  BIGINT     PRIMARY KEY AUTO_INCREMENT,
                          article_id  BIGINT     NOT NULL,
                          user_id     BIGINT     NOT NULL,
                          content     TEXT        NOT NULL,
                          like_count    BIGINT NOT NULL DEFAULT 0,
                          dislike_count    BIGINT NOT NULL DEFAULT 0,
                          created_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
                          updated_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (article_id) REFERENCES articles(article_id) ON DELETE CASCADE,
                          FOREIGN KEY (user_id)    REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE article_likes (
                               article_like_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               article_id      BIGINT NOT NULL,
                               user_id         BIGINT NOT NULL,
                               created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (article_id) REFERENCES articles(article_id) ON DELETE CASCADE,
                               FOREIGN KEY (user_id)    REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE article_dislikes (
                                  article_dislike_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  article_id         BIGINT NOT NULL,
                                  user_id            BIGINT NOT NULL,
                                  created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (article_id) REFERENCES articles(article_id) ON DELETE CASCADE,
                                  FOREIGN KEY (user_id)    REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE comment_likes (
                               comment_like_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               comment_id      BIGINT NOT NULL,
                               user_id         BIGINT NOT NULL,
                               created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE,
                               FOREIGN KEY (user_id)     REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE comment_dislikes (
                                  comment_dislike_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  comment_id         BIGINT NOT NULL,
                                  user_id            BIGINT NOT NULL,
                                  created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE,
                                  FOREIGN KEY (user_id)     REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ========================================
-- 5. 알림 (Notifications)
-- ========================================
CREATE TABLE notifications (
                               notification_id BIGINT    PRIMARY KEY AUTO_INCREMENT,
                               user_id         BIGINT    NOT NULL,
                               type            VARCHAR(50) NOT NULL COMMENT 'challenge, reply, like 등',
                               payload         JSON       NOT NULL COMMENT '관련 대상(articleId, commentId 등)',
                               is_read         BOOLEAN    DEFAULT FALSE,
                               created_at      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- ========================================
-- 8. 신고 (Report) — Polymorphic 구조
-- ========================================
CREATE TABLE reports (
                         report_id    BIGINT    PRIMARY KEY AUTO_INCREMENT,
                         report_category VARCHAR(100),
                         reporter_id      BIGINT    NOT NULL COMMENT '신고자 id',
                         reportee_id      BIGINT    NOT NULL COMMENT '피신고자 id',
                         type  VARCHAR(20) NOT NULL COMMENT 'article, comment, user 등',
                         article_id    BIGINT    NOT NULL,
                         content      TEXT,
                         action VARCHAR(200) NULL COMMENT '조치 내용',
                         created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         is_handled BOOLEAN DEFAULT FALSE,
                         FOREIGN KEY (article_id) REFERENCES articles(article_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- badge 기본 설정
INSERT INTO badges (badge_id, name, icon_url, description)
VALUES ('ARTICLE_POSTER_LV1', '게시글 작성자 Lv.1', '/assets/badges/article_poster_lv1.png', '게시글 3개 작성 배지'),
       ('ARTICLE_POSTER_LV2', '게시글 작성자 Lv.2', '/assets/badges/article_poster_lv2.png', '게시글 10개 작성 배지'),
       ('ARTICLE_POSTER_LV3', '게시글 작성자 Lv.3', '/assets/badges/article_poster_lv3.png', '게시글 50개 작성 배지')
;

INSERT INTO badges (badge_id, name, icon_url, description)
VALUES
    ('STREAK_3_DAYS', '3일 연속 챌린지', '/badges/streak_3.png', '3일 연속으로 챌린지를 완료했습니다!'),
    ('STREAK_7_DAYS', '7일 연속 챌린지', '/badges/streak_7.png', '7일 연속으로 챌린지를 완료했습니다!'),
    ('STREAK_30_DAYS', '30일 연속 챌린지', '/badges/streak_30.png', '30일 연속으로 챌린지를 완료했습니다!'),
    ('STREAK_100_DAYS', '100일 연속 챌린지', '/badges/streak_100.png', '100일 연속으로 챌린지를 완료했습니다!');


select * from users;
select * from reports;

INSERT INTO users(user_id, username, password, checkPassword, nickname, email)
VALUES (1, "robo", "$2a$10$BSTy3.gRRCOYkQX6dapqg.5fAriwypedkQKQx6TyZ/8q8ikaWQx3u", "$2a$10$BSTy3.gRRCOYkQX6dapqg.5fAriwypedkQKQx6TyZ/8q8ikaWQx3u", "robo", "robo@gmail.com"),
       (2, "dijk", "$2a$10$BSTy3.gRRCOYkQX6dapqg.5fAriwypedkQKQx6TyZ/8q8ikaWQx3u", "$2a$10$BSTy3.gRRCOYkQX6dapqg.5fAriwypedkQKQx6TyZ/8q8ikaWQx3u", "dijk", "dijk@gmail.com")

UPDATE users
SET role="ROLE_ADMIN"
WHERE username="robo";

-- articles
INSERT INTO articles (user_id, category, title, content, tag) VALUES
                                                                  (1, '운동', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (1, '후기', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (2, '운동', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (2, '질문', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소');
-- comments
-- robo -> dijk 게시글(3,4)에 댓글
INSERT INTO comments (article_id, user_id, content) VALUES
                                                        (3, 1, '대단하세요! 하체 불꽃입니다.'),
                                                        (4, 1, '저도 궁금해요. 전문가 답변 부탁드려요.');
-- dijk -> robo 게시글(1,2)에 댓글
INSERT INTO comments (article_id, user_id, content) VALUES
                                                        (1, 2, '운동 꿀팁 감사합니다!'),
                                                        (2, 2, '식단 참고할게요~');

-- inbody_data
-- robo (user_id=1)
INSERT INTO inbody_data (user_id, weight, muscle_mass, body_fat, body_fat_percentage, bmi, uploaded_at) VALUES
                                                                                                            (1, 70.2, 35.1, 16.0, 22.8, 23.1, '2024-05-01'),
                                                                                                            (1, 70.1, 35.2, 15.9, 22.7, 23.0, '2024-05-02'),
                                                                                                            (1, 70.0, 35.3, 15.8, 22.6, 22.9, '2024-05-03'),
                                                                                                            (1, 69.8, 35.4, 15.5, 22.2, 22.7, '2024-05-04'),
                                                                                                            (1, 69.7, 35.6, 15.4, 22.1, 22.6, '2024-05-05'),
                                                                                                            (1, 69.5, 35.7, 15.2, 22.0, 22.5, '2024-05-06'),
                                                                                                            (1, 69.9, 35.5, 15.3, 22.3, 22.7, '2024-05-07'),
                                                                                                            (1, 69.4, 35.6, 15.0, 21.9, 22.4, '2024-05-08'),
                                                                                                            (1, 69.2, 35.8, 14.9, 21.7, 22.3, '2024-05-09'),
                                                                                                            (1, 69.1, 36.0, 14.6, 21.2, 22.2, '2024-05-10'),
                                                                                                            (1, 69.0, 36.1, 14.5, 21.1, 22.1, '2024-05-11'),
                                                                                                            (1, 68.9, 36.2, 14.3, 20.9, 22.0, '2024-05-12'),
                                                                                                            (1, 68.8, 36.3, 14.2, 20.8, 21.9, '2024-05-13'),
                                                                                                            (1, 68.7, 36.4, 14.1, 20.7, 21.8, '2024-05-14');

-- dijk (user_id=2)
INSERT INTO inbody_data (user_id, weight, muscle_mass, body_fat, body_fat_percentage, bmi, uploaded_at) VALUES
                                                                                                            (2, 83.5, 38.2, 17.9, 21.5, 25.1, '2024-05-01'),
                                                                                                            (2, 83.2, 38.2, 17.7, 21.3, 25.0, '2024-05-02'),
                                                                                                            (2, 83.0, 38.3, 17.5, 21.1, 24.9, '2024-05-03'),
                                                                                                            (2, 82.8, 38.4, 17.3, 20.9, 24.8, '2024-05-04'),
                                                                                                            (2, 82.7, 38.5, 17.1, 20.7, 24.7, '2024-05-05'),
                                                                                                            (2, 82.5, 38.6, 17.0, 20.6, 24.6, '2024-05-06'),
                                                                                                            (2, 82.3, 38.7, 16.8, 20.4, 24.5, '2024-05-07'),
                                                                                                            (2, 82.0, 38.8, 16.6, 20.2, 24.4, '2024-05-08'),
                                                                                                            (2, 81.9, 38.9, 16.5, 20.1, 24.3, '2024-05-09'),
                                                                                                            (2, 82.0, 39.0, 16.3, 19.9, 24.2, '2024-05-10'),
                                                                                                            (2, 81.8, 39.2, 16.2, 19.7, 24.1, '2024-05-11'),
                                                                                                            (2, 81.7, 39.2, 16.1, 19.7, 24.1, '2024-05-12'),
                                                                                                            (2, 81.6, 39.3, 16.0, 19.6, 24.0, '2024-05-13'),
                                                                                                            (2, 81.5, 39.4, 15.9, 19.5, 23.9, '2024-05-14');

-- reports
-- robo가 dijk의 글(article_id=3)을 신고
INSERT INTO reports (report_category, reporter_id, reportee_id, type, article_id, content) VALUES
    ('욕설', 1, 2, 'article', 3, '욕설 및 비방성 글 신고합니다');
-- dijk가 robo의 글(article_id=1)을 신고
INSERT INTO reports (report_category, reporter_id, reportee_id, type, article_id, content) VALUES
    ('광고', 2, 1, 'article', 1, '홍보성 게시글로 의심');