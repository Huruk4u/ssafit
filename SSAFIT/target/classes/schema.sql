-- ========================================
-- 1. 데이터베이스 생성/사용
-- ========================================
DROP DATABASE IF EXISTS ssafydb;
CREATE DATABASE IF NOT EXISTS ssafydb;
USE ssafydb;

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
                       FOREIGN KEY (badge_id) REFERENCES badges(badge_id)
) ENGINE=InnoDB;

CREATE TABLE admins (
                        admin_id     BIGINT    PRIMARY KEY AUTO_INCREMENT,
                        user_id      BIGINT    NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- ========================================
-- 3. 챌린지 / 인바디
-- ========================================
CREATE TABLE challenges (
                            challenge_id        BIGINT    PRIMARY KEY AUTO_INCREMENT,
                            user_id             BIGINT    NOT NULL,
                            streak_count        INT       DEFAULT 0,
                            longest_streak      INT       DEFAULT 0,
                            streak_map          JSON      COMMENT '날짜별 수행여부 맵',
                            created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE inbody_data (
                             inbody_id    BIGINT    PRIMARY KEY AUTO_INCREMENT,
                             user_id      BIGINT    NOT NULL,
                             weight       DECIMAL(5,2),
                             muscle_mass  DECIMAL(5,2),
                             body_fat     DECIMAL(5,2),
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
                          view_count BIGINT DEFAULT 0,
                          like_count    BIGINT NOT NULL DEFAULT 0,
                          dislike_count    BIGINT NOT NULL DEFAULT 0,
                          created_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
                          updated_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (user_id)  REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE comments (
                          comment_id  BIGINT     PRIMARY KEY AUTO_INCREMENT,
                          article_id  BIGINT     NOT NULL,
                          user_id     BIGINT     NOT NULL,
                          content     TEXT        NOT NULL,
                          created_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
                          updated_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (article_id) REFERENCES articles(article_id),
                          FOREIGN KEY (user_id)    REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE article_likes (
                               article_like_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               article_id      BIGINT NOT NULL,
                               user_id         BIGINT NOT NULL,
                               created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (article_id) REFERENCES articles(article_id),
                               FOREIGN KEY (user_id)    REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE article_dislikes (
                                  article_dislike_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  article_id         BIGINT NOT NULL,
                                  user_id            BIGINT NOT NULL,
                                  created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (article_id) REFERENCES articles(article_id),
                                  FOREIGN KEY (user_id)    REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE comment_likes (
                               comment_like_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               comment_id      BIGINT NOT NULL,
                               user_id         BIGINT NOT NULL,
                               created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (comment_id) REFERENCES comments(comment_id),
                               FOREIGN KEY (user_id)     REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE comment_dislikes (
                                  comment_dislike_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                  comment_id         BIGINT NOT NULL,
                                  user_id            BIGINT NOT NULL,
                                  created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (comment_id) REFERENCES comments(comment_id),
                                  FOREIGN KEY (user_id)     REFERENCES users(user_id)
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
-- 6. 태그 / 게시글-태그 매핑
-- ========================================
CREATE TABLE tags (
                      tag_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      name   VARCHAR(50) UNIQUE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE article_tags (
                              article_id BIGINT NOT NULL,
                              tag_id     BIGINT NOT NULL,
                              PRIMARY KEY (article_id, tag_id),
                              FOREIGN KEY (article_id) REFERENCES articles(article_id),
                              FOREIGN KEY (tag_id)     REFERENCES tags(tag_id)
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

CREATE TABLE video_tags (
                            video_id BIGINT NOT NULL,
                            tag_id   BIGINT NOT NULL,
                            PRIMARY KEY (video_id, tag_id),
                            FOREIGN KEY (video_id) REFERENCES videos(video_id),
                            FOREIGN KEY (tag_id)   REFERENCES tags(tag_id)
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
                         user_id      BIGINT    NOT NULL,
                         target_type  VARCHAR(20) NOT NULL COMMENT 'article, comment, user 등',
                         target_id    BIGINT    NOT NULL,
                         content      TEXT,
                         created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(user_id)
    -- 타겟별 FK 제약조건은 애플리케이션 레이어에서 관리 권장
) ENGINE=InnoDB;