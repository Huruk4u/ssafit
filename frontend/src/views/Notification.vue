<template>
  <div>
    <div>
      <Header />
    </div>
    <h2>유저 알림 처리</h2>

    <div class="noti-actions">
      <button @click="markAllAsRead" :disabled="notificationList.length === 0">
        모든 알림 읽음 처리
      </button>
      <button
        @click="deleteAllNotifications"
        :disabled="notificationList.length === 0"
      >
        모든 알림 삭제
      </button>
    </div>

    <div class="noti-list-wrapper">
      <ul class="noti-list">
        <li
          v-for="notification in notificationList"
          :key="notification.notificationId"
          :class="['noti-card', { unread: !notification.isRead }]"
        >
          <div class="noti-main">
            <router-link
              v-if="
                notification.type === 'comment' && getArticleId(notification)
              "
              :to="`/board/detail/${getArticleId(notification)}`"
              class="noti-link"
            >
              {{ renderMessage(notification) }}
            </router-link>
            <span v-else>{{ renderMessage(notification) }}</span>
          </div>
          <div class="noti-meta">
            <span class="noti-date">{{ notification.createdAt }}</span>
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
import Header from "@/components/Header.vue";

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
    return `게시글 ${payloadObj.articleId}에 새 댓글이 달렸습니다.`;
  } else if (notification.type === "report") {
    // 예: 광고로 인해 0일 정지되었습니다.
    return `${payloadObj.category || "사유없음"}로 인해 ${
      payloadObj.action ?? "?"
    }일 정지되었습니다.`;
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

// 알림 목록 불러오기
const loadNotifications = () => {
  api
    .get("/api_notification/list")
    .then((res) => {
      notificationList.value = res.data;
      console.log("알림 목록", notificationList.value);
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
.unread span,
.unread .noti-link {
  font-weight: bold;
  color: #1a7f5a;
}

.actions button {
  margin-left: 8px;
}
</style>