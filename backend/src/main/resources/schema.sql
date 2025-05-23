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
                       enabled          BOOLEAN      DEFAULT TRUE,
                       suspend_start TIMESTAMP NULL,
                       suspend_end   TIMESTAMP NULL,
                       created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
                       first_exercise VARCHAR(50) NULL,
                       second_exercise VARCHAR(50) NULL,
                       third_exercise VARCHAR(50) NULL,
                       role VARCHAR(20) DEFAULT 'ROLE_USER'
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
                         reporter_name  VARCHAR(50) NOT NULL COMMENT '신고자 name',
                         reportee_id      BIGINT    NOT NULL COMMENT '피신고자 id',
                         reportee_name  VARCHAR(50) NOT NULL COMMENT '피신고자 name',
                         type  VARCHAR(20) NOT NULL COMMENT 'article, comment, user 등',
                         article_id    BIGINT    NOT NULL,
                         content      TEXT,
                         action int NULL COMMENT '조치 내용',
                         created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         is_handled BOOLEAN DEFAULT FALSE,
                         FOREIGN KEY (article_id) REFERENCES articles(article_id) ON DELETE CASCADE
) ENGINE=InnoDB;


CREATE TABLE follow (
                        follower_id BIGINT NOT NULL,
                        followee_id BIGINT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (follower_id, followee_id),
                        FOREIGN KEY (follower_id) REFERENCES users(user_id) ON DELETE CASCADE,
                        FOREIGN KEY (followee_id) REFERENCES users(user_id) ON DELETE CASCADE
);


-- badge 기본 설정
INSERT INTO badges (badge_id, name, icon_url, description)
VALUES ('ARTICLE_POSTER_LV1', '게시글 작성자 Lv.1', '/images/badges/article_poster_lv1.png', '게시글 3개 작성 배지'),
       ('ARTICLE_POSTER_LV2', '게시글 작성자 Lv.2', '/images/badges/article_poster_lv2.png', '게시글 10개 작성 배지'),
       ('ARTICLE_POSTER_LV3', '게시글 작성자 Lv.3', '/images/badges/article_poster_lv3.png', '게시글 50개 작성 배지')
;

INSERT INTO badges (badge_id, name, icon_url, description)
VALUES
    ('STREAK_3_DAYS', '3일 연속 챌린지', '/images/badges/streak_3.png', '3일 연속으로 챌린지를 완료했습니다!'),
    ('STREAK_7_DAYS', '7일 연속 챌린지', '/images/badges/streak_7.png', '7일 연속으로 챌린지를 완료했습니다!'),
    ('STREAK_30_DAYS', '30일 연속 챌린지', '/images/badges/streak_30.png', '30일 연속으로 챌린지를 완료했습니다!'),
    ('STREAK_100_DAYS', '100일 연속 챌린지', '/images/badges/streak_100.png', '100일 연속으로 챌린지를 완료했습니다!');


select * from users;
select * from reports;

INSERT INTO users(user_id, username, password, nickname, email)
VALUES (1, "robo", "$2a$10$BSTy3.gRRCOYkQX6dapqg.5fAriwypedkQKQx6TyZ/8q8ikaWQx3u", "robo", "robo@gmail.com"),
       (2, "dijk", "$2a$10$BSTy3.gRRCOYkQX6dapqg.5fAriwypedkQKQx6TyZ/8q8ikaWQx3u", "dijk", "dijk@gmail.com")

UPDATE users
SET role="ROLE_ADMIN"
WHERE username="robo";


-- articles
INSERT INTO articles (user_id, category, title, content, tag) VALUES
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소'),
                                                                  (3, 'question', '로보의 첫 운동일지', '오늘도 열심히 운동했습니다.', '운동,헬스'),
                                                                  (3, 'question', '로보의 식단공유', '단백질 위주 식단방식입니다.', '식단,영양'),
                                                                  (3, 'question', '다이크의 하체루틴', '스쿼트 100개 성공했어요!', '하체,스쿼트'),
                                                                  (3, 'question', '다이크의 운동질문', '러닝머신 몇 분이 적당한가요?', '러닝,유산소');
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
INSERT INTO reports (report_category, reporter_id, reporter_name, reportee_id, reportee_name, type, article_id, content) VALUES
    ('욕설', 1,'robo',  2, 'dijk', 'ARTICLE', 3, '욕설 및 비방성 글 신고합니다');
-- dijk가 robo의 글(article_id=1)을 신고
INSERT INTO reports (report_category, reporter_id, reporter_name, reportee_id, reportee_name, type, article_id, content) VALUES
    ('광고', 2, 'dijk', 1, 'robo', 'ARTICLE', 1, '홍보성 게시글로 의심');

select * from articles;-- 3번 유저의 challenge에 최근 6개월 내 연속된 날짜 포함 60개 데이터 삽입 예시
INSERT INTO challenges (user_id, record_date) VALUES
                                                  (3, '2023-12-01'), (3, '2023-12-02'), (3, '2023-12-03'), (3, '2023-12-04'), (3, '2023-12-05'),
                                                  (3, '2023-12-07'), (3, '2023-12-08'), (3, '2023-12-09'), (3, '2023-12-10'), (3, '2023-12-11'),
                                                  (3, '2023-12-13'), (3, '2023-12-14'), (3, '2023-12-15'), (3, '2023-12-16'), (3, '2023-12-17'),
                                                  (3, '2023-12-20'), (3, '2023-12-21'), (3, '2023-12-22'), (3, '2023-12-23'), (3, '2023-12-24'),
                                                  (3, '2024-01-01'), (3, '2024-01-02'), (3, '2024-01-03'), (3, '2024-01-04'), (3, '2024-01-05'),
                                                  (3, '2024-01-06'), (3, '2024-01-07'), (3, '2024-01-08'), (3, '2024-01-09'), (3, '2024-01-10'),
                                                  (3, '2024-01-15'), (3, '2024-01-16'), (3, '2024-01-17'), (3, '2024-01-18'), (3, '2024-01-19'),
                                                  (3, '2024-01-20'), (3, '2024-01-21'), (3, '2024-01-22'), (3, '2024-01-23'), (3, '2024-01-24'),
                                                  (3, '2024-02-01'), (3, '2024-02-02'), (3, '2024-02-03'), (3, '2024-02-04'), (3, '2024-02-05'),
                                                  (3, '2024-02-10'), (3, '2024-02-11'), (3, '2024-02-12'), (3, '2024-02-13'), (3, '2024-02-14'),
                                                  (3, '2024-03-01'), (3, '2024-03-02'), (3, '2024-03-03'), (3, '2024-03-04'), (3, '2024-03-05'),
                                                  (3, '2024-03-10'), (3, '2024-03-11'), (3, '2024-03-12'), (3, '2024-03-13'), (3, '2024-03-14');
-- 3번 유저의 challenge에 넣었던 날짜에 맞춰 inbody_data도 같이 삽입 (예시, 값은 임의)
INSERT INTO inbody_data (user_id, weight, muscle_mass, body_fat, body_fat_percentage, bmi, uploaded_at) VALUES
                                                                                                            (3, 70.0, 35.0, 15.0, 21.5, 22.0, '2023-12-01'),
                                                                                                            (3, 70.2, 35.1, 15.1, 21.6, 22.1, '2023-12-02'),
                                                                                                            (3, 70.3, 35.2, 15.2, 21.7, 22.2, '2023-12-03'),
                                                                                                            (3, 70.4, 35.3, 15.3, 21.8, 22.3, '2023-12-04'),
                                                                                                            (3, 70.5, 35.4, 15.4, 21.9, 22.4, '2023-12-05'),
                                                                                                            (3, 70.7, 35.5, 15.5, 22.0, 22.5, '2023-12-07'),
                                                                                                            (3, 70.8, 35.6, 15.6, 22.1, 22.6, '2023-12-08'),
                                                                                                            (3, 70.9, 35.7, 15.7, 22.2, 22.7, '2023-12-09'),
                                                                                                            (3, 71.0, 35.8, 15.8, 22.3, 22.8, '2023-12-10'),
                                                                                                            (3, 71.1, 35.9, 15.9, 22.4, 22.9, '2023-12-11'),
                                                                                                            (3, 71.3, 36.0, 16.0, 22.5, 23.0, '2023-12-13'),
                                                                                                            (3, 71.4, 36.1, 16.1, 22.6, 23.1, '2023-12-14'),
                                                                                                            (3, 71.5, 36.2, 16.2, 22.7, 23.2, '2023-12-15'),
                                                                                                            (3, 71.6, 36.3, 16.3, 22.8, 23.3, '2023-12-16'),
                                                                                                            (3, 71.7, 36.4, 16.4, 22.9, 23.4, '2023-12-17'),
                                                                                                            (3, 71.9, 36.5, 16.5, 23.0, 23.5, '2023-12-20'),
                                                                                                            (3, 72.0, 36.6, 16.6, 23.1, 23.6, '2023-12-21'),
                                                                                                            (3, 72.1, 36.7, 16.7, 23.2, 23.7, '2023-12-22'),
                                                                                                            (3, 72.2, 36.8, 16.8, 23.3, 23.8, '2023-12-23'),
                                                                                                            (3, 72.3, 36.9, 16.9, 23.4, 23.9, '2023-12-24'),
                                                                                                            (3, 72.5, 37.0, 17.0, 23.5, 24.0, '2024-01-01'),
                                                                                                            (3, 72.6, 37.1, 17.1, 23.6, 24.1, '2024-01-02'),
                                                                                                            (3, 72.7, 37.2, 17.2, 23.7, 24.2, '2024-01-03'),
                                                                                                            (3, 72.8, 37.3, 17.3, 23.8, 24.3, '2024-01-04'),
                                                                                                            (3, 72.9, 37.4, 17.4, 23.9, 24.4, '2024-01-05'),
                                                                                                            (3, 73.0, 37.5, 17.5, 24.0, 24.5, '2024-01-06'),
                                                                                                            (3, 73.1, 37.6, 17.6, 24.1, 24.6, '2024-01-07'),
                                                                                                            (3, 73.2, 37.7, 17.7, 24.2, 24.7, '2024-01-08'),
                                                                                                            (3, 73.3, 37.8, 17.8, 24.3, 24.8, '2024-01-09'),
                                                                                                            (3, 73.4, 37.9, 17.9, 24.4, 24.9, '2024-01-10'),
                                                                                                            (3, 73.5, 38.0, 18.0, 24.5, 25.0, '2024-01-15'),
                                                                                                            (3, 73.6, 38.1, 18.1, 24.6, 25.1, '2024-01-16'),
                                                                                                            (3, 73.7, 38.2, 18.2, 24.7, 25.2, '2024-01-17'),
                                                                                                            (3, 73.8, 38.3, 18.3, 24.8, 25.3, '2024-01-18'),
                                                                                                            (3, 73.9, 38.4, 18.4, 24.9, 25.4, '2024-01-19'),
                                                                                                            (3, 74.0, 38.5, 18.5, 25.0, 25.5, '2024-01-20'),
                                                                                                            (3, 74.1, 38.6, 18.6, 25.1, 25.6, '2024-01-21'),
                                                                                                            (3, 74.2, 38.7, 18.7, 25.2, 25.7, '2024-01-22'),
                                                                                                            (3, 74.3, 38.8, 18.8, 25.3, 25.8, '2024-01-23'),
                                                                                                            (3, 74.4, 38.9, 18.9, 25.4, 25.9, '2024-01-24'),
                                                                                                            (3, 74.5, 39.0, 19.0, 25.5, 26.0, '2024-02-01'),
                                                                                                            (3, 74.6, 39.1, 19.1, 25.6, 26.1, '2024-02-02'),
                                                                                                            (3, 74.7, 39.2, 19.2, 25.7, 26.2, '2024-02-03'),
                                                                                                            (3, 74.8, 39.3, 19.3, 25.8, 26.3, '2024-02-04'),
                                                                                                            (3, 74.9, 39.4, 19.4, 25.9, 26.4, '2024-02-05'),
                                                                                                            (3, 75.0, 39.5, 19.5, 26.0, 26.5, '2024-02-10'),
                                                                                                            (3, 75.1, 39.6, 19.6, 26.1, 26.6, '2024-02-11'),
                                                                                                            (3, 75.2, 39.7, 19.7, 26.2, 26.7, '2024-02-12'),
                                                                                                            (3, 75.3, 39.8, 19.8, 26.3, 26.8, '2024-02-13'),
                                                                                                            (3, 75.4, 39.9, 19.9, 26.4, 26.9, '2024-02-14'),
                                                                                                            (3, 75.5, 40.0, 20.0, 26.5, 27.0, '2024-03-01'),
                                                                                                            (3, 75.6, 40.1, 20.1, 26.6, 27.1, '2024-03-02'),
                                                                                                            (3, 75.7, 40.2, 20.2, 26.7, 27.2, '2024-03-03'),
                                                                                                            (3, 75.8, 40.3, 20.3, 26.8, 27.3, '2024-03-04'),
                                                                                                            (3, 75.9, 40.4, 20.4, 26.9, 27.4, '2024-03-05'),
                                                                                                            (3, 76.0, 40.5, 20.5, 27.0, 27.5, '2024-03-10'),
                                                                                                            (3, 76.1, 40.6, 20.6, 27.1, 27.6, '2024-03-11'),
                                                                                                            (3, 76.2, 40.7, 20.7, 27.2, 27.7, '2024-03-12'),
                                                                                                            (3, 76.3, 40.8, 20.8, 27.3, 27.8, '2024-03-13'),
                                                                                                            (3, 76.4, 40.9, 20.9, 27.4, 27.9, '2024-03-14');
select * from users;
select * from notifications;
select * from reports;
select * from inbody_data;
select * from article_dislikes;
select * from follow;
-- 3번 유저의 최근 6개월(2024-12-01 ~ 2025-05-23) 동안 연속 스트릭 + 랜덤 날짜 포함, 총 60개 예시
INSERT INTO challenges (user_id, record_date) VALUES
-- 2025-04-01 ~ 2025-05-23 연속 스트릭 (53일)
(3, '2025-04-01'), (3, '2025-04-02'), (3, '2025-04-03'), (3, '2025-04-04'), (3, '2025-04-05'),
(3, '2025-04-06'), (3, '2025-04-07'), (3, '2025-04-08'), (3, '2025-04-09'), (3, '2025-04-10'),
(3, '2025-04-11'), (3, '2025-04-12'), (3, '2025-04-13'), (3, '2025-04-14'), (3, '2025-04-15'),
(3, '2025-04-16'), (3, '2025-04-17'), (3, '2025-04-18'), (3, '2025-04-19'), (3, '2025-04-20'),
(3, '2025-04-21'), (3, '2025-04-22'), (3, '2025-04-23'), (3, '2025-04-24'), (3, '2025-04-25'),
(3, '2025-04-26'), (3, '2025-04-27'), (3, '2025-04-28'), (3, '2025-04-29'), (3, '2025-04-30'),
(3, '2025-05-01'), (3, '2025-05-02'), (3, '2025-05-03'), (3, '2025-05-04'), (3, '2025-05-05'),
(3, '2025-05-06'), (3, '2025-05-07'), (3, '2025-05-08'), (3, '2025-05-09'), (3, '2025-05-10'),
(3, '2025-05-11'), (3, '2025-05-12'), (3, '2025-05-13'), (3, '2025-05-14'), (3, '2025-05-15'),
(3, '2025-05-16'), (3, '2025-05-17'), (3, '2025-05-18'), (3, '2025-05-19'), (3, '2025-05-20'),
(3, '2025-05-21'), (3, '2025-05-22'), (3, '2025-05-23'),

-- 랜덤 날짜 7개 추가 (연속 구간 외)
(3, '2024-12-05'), (3, '2024-12-12'), (3, '2024-12-25'),
(3, '2025-01-10'), (3, '2025-02-14'), (3, '2025-03-03'), (3, '2025-03-21');
