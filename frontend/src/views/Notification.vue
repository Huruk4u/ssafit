<template>
  <div class="noti-root">
    <div class="noti-header-row">
      <h2 class="noti-title">알림</h2>
      <div class="noti-actions">
        <button @click="markAllAsRead" :disabled="notificationList.length === 0">
          모두 읽음
        </button>
        <button
          @click="deleteAllNotifications"
          :disabled="notificationList.length === 0"
        >
          모두 삭제
        </button>
      </div>
    </div>
    <div class="noti-list-wrapper">
      <ul class="noti-list">
        <li
          v-for="notification in notificationList"
          :key="notification.notificationId"
          :class="['noti-card', { unread: !notification.isRead }]"
        >
          <div class="noti-main-row">
            <div class="noti-main">
              <router-link
                v-if="notification.type === 'comment' && getArticleId(notification)"
                :to="`/board/detail/${getArticleId(notification)}`"
                class="noti-link"
              >
                {{ renderMessage(notification) }}
              </router-link>
              <span v-else>{{ renderMessage(notification) }}</span>
            </div>
            <span class="noti-date">{{ formatDate(notification.createdAt) }}</span>
          </div>
          <div class="noti-actions-inline">
            <button
              @click="markAsRead(notification.notificationId)"
              :disabled="notification.isRead"
            >
              읽음
            </button>
            <button @click="deleteNotification(notification.notificationId)">
              삭제
            </button>
          </div>
        </li>
      </ul>
      <div v-if="notificationList.length === 0" class="noti-empty">
        알림이 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import api from "@/api/axiosInstance";

const notificationList = ref([]);

// 알림 메시지 생성 함수
const renderMessage = (notification) => {
  if (!notification.payload) return "알림 내용 없음";

  let payloadObj = {};
  try {
    payloadObj = JSON.parse(notification.payload);
  } catch (e) {
    return "알림 데이터 오류";
  }

  if (notification.type === "comment" && payloadObj.articleId) {
    return `나의 게시글에 새 댓글이 달렸습니다.`;
  } else if (notification.type === "report") {
    return `${payloadObj.category || "사유없음"}로 인해 ${
      payloadObj.action ?? "?"
    }일 정지되었습니다.`;
  } else if (notification.type === "follow") {
    return `${payloadObj.followerName || "알 수 없음"}님이 나를 팔로우했습니다.`;
  } else {
    return "새로운 알림이 도착했습니다.";
  }
};

// payload에서 articleId 추출 (router-link용)
const getArticleId = (notification) => {
  if (!notification.payload) return null;

  try {
    const payloadObj = JSON.parse(notification.payload);
    return payloadObj.articleId || null;
  } catch {
    return null;
  }
};

// 날짜 포맷 함수 (YYYY-MM-DD HH:mm)
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  const hh = String(date.getHours()).padStart(2, "0");
  const min = String(date.getMinutes()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
};

// 알림 목록 불러오기
const loadNotifications = () => {
  api
    .get("/api_notification/list")
    .then((res) => {
      notificationList.value = res.data;
    })
    .catch(() => {
      alert("알림 목록을 가져오는데 실패했습니다.");
    });
};

onMounted(() => {
  loadNotifications();
});

// 읽음 처리 API 호출
const markAsRead = (notificationId) => {
  api
    .put(`/api_notification/read/${notificationId}`)
    .then(() => {
      const target = notificationList.value.find(
        (n) => n.notificationId === notificationId
      );
      if (target) target.isRead = true;
    })
    .catch(() => {
      alert("읽음 처리 실패");
    });
};

// 삭제 API 호출
const deleteNotification = (notificationId) => {
  api
    .delete(`/api_notification/delete/${notificationId}`)
    .then(() => {
      notificationList.value = notificationList.value.filter(
        (n) => n.notificationId !== notificationId
      );
    })
    .catch(() => {
      alert("알림 삭제 실패");
    });
};

// 모든 알림 읽음 처리
const markAllAsRead = () => {
  api
    .put("/api_notification/read/all")
    .then(() => {
      notificationList.value.forEach((n) => (n.isRead = true));
    })
    .catch(() => {
      alert("모든 알림 읽음 처리 실패");
    });
};

// 모든 알림 삭제
const deleteAllNotifications = () => {
  api
    .delete("/api_notification/delete/all")
    .then(() => {
      notificationList.value = [];
    })
    .catch(() => {
      alert("모든 알림 삭제 실패");
    });
};
</script>

<style scoped>
@import url("https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css");

.noti-root {
  font-family: "Pretendard Variable", "Pretendard", "Noto Sans KR", Arial, sans-serif;
  background: #f8f9fa;
  min-height: 100vh;
  padding: 32px 0 0 0;
}

.noti-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 700px;
  margin: 0 auto 18px auto;
  padding: 0 8px;
}

.noti-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #212529;
  letter-spacing: -1px;
  margin: 0;
}

.noti-actions {
  display: flex;
  gap: 10px;
}

.noti-actions button {
  padding: 8px 20px;
  background: linear-gradient(135deg, #42b983, #369870);
  color: #fff;
  border: none;
  border-radius: 20px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(66,185,131,0.10);
  transition: background 0.2s, box-shadow 0.2s;
}

.noti-actions button:disabled {
  background: #adb5bd;
  color: #fff;
  cursor: not-allowed;
  box-shadow: none;
}

.noti-list-wrapper {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.07);
  padding: 0;
  margin-top: 10px;
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
}

.noti-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.noti-card {
  padding: 1.3rem 2rem 1.1rem 2rem;
  border-bottom: 1px solid #f1f3f5;
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
  background: #fff;
  transition: background 0.15s;
}

.noti-card.unread {
  background: linear-gradient(90deg, #e6f9f1 0%, #fff 100%);
}

.noti-main-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.noti-main {
  font-size: 1.08rem;
  color: #212529;
  word-break: break-word;
  flex: 1;
}

.noti-link {
  color: #1a7f5a;
  text-decoration: underline;
  font-weight: 600;
  transition: color 0.2s;
}

.noti-link:hover {
  color: #42b983;
  text-decoration: underline;
}

.noti-date {
  font-size: 0.97rem;
  color: #adb5bd;
  min-width: 120px;
  text-align: right;
  font-family: "Pretendard Variable", "Pretendard", Arial, sans-serif;
}

.noti-actions-inline {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

.noti-actions-inline button {
  padding: 6px 16px;
  background: #f8f9fa;
  color: #42b983;
  border: 1.5px solid #e9ecef;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background 0.2s, color 0.2s, border 0.2s;
}

.noti-actions-inline button:disabled {
  color: #adb5bd;
  border-color: #e9ecef;
  background: #f1f3f5;
  cursor: not-allowed;
}

.noti-actions-inline button:hover:not(:disabled) {
  background: #e6f9f1;
  color: #369870;
  border-color: #42b983;
}

.noti-empty {
  text-align: center;
  color: #adb5bd;
  font-size: 1.1rem;
  padding: 40px 0;
  background: #f8f9fa;
  border-radius: 0 0 18px 18px;
}

@media (max-width: 800px) {
  .noti-list-wrapper {
    max-width: 98vw;
    border-radius: 10px;
  }
  .noti-card {
    padding: 1rem 0.7rem;
  }
  .noti-header-row {
    max-width: 98vw;
    padding: 0 2vw;
  }
}

@media (max-width: 500px) {
  .noti-title {
    font-size: 1.1rem;
  }
  .noti-card {
    padding: 0.7rem 0.3rem;
    font-size: 0.95rem;
  }
  .noti-list-wrapper {
    border-radius: 6px;
  }
  .noti-date {
    min-width: 70px;
    font-size: 0.88rem;
  }
}
</style>