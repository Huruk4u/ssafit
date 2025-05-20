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
-- 7. 동영상 / 동영상-태그 / 즐겨찾기
-- ========================================
CREATE TABLE videos (
                        video_id     BIGINT    PRIMARY KEY AUTO_INCREMENT,
                        title        VARCHAR(255),
                        url          VARCHAR(255),
                        thumbnail    VARCHAR(255),
                        uploaded_by  BIGINT,
                        created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (uploaded_by) REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE user_favorite_videos (
                                      user_id      BIGINT    NOT NULL,
                                      video_id     BIGINT    NOT NULL,
                                      favorited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (user_id, video_id),
                                      FOREIGN KEY (user_id)  REFERENCES users(user_id),
                                      FOREIGN KEY (video_id) REFERENCES videos(video_id)
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
select * from challenges;
delete from challenges;

INSERT INTO users(user_id, username, password, email, nickname, role)
VALUES (987654321, "root", "fhqjxmtms26!",
        "sungmin915_@naver.com", "im_admin", "ROLE_ADMIN");

UPDATE users
SET role="ROLE_ADMIN"
WHERE username="root";