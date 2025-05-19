<template>
  <div>
    <h2>유저 알림 처리</h2>

    <div style="margin-bottom: 16px;">
      <button @click="markAllAsRead" :disabled="notificationList.length === 0">
        모든 알림 읽음 처리
      </button>
      <button @click="deleteAllNotifications" :disabled="notificationList.length === 0" style="margin-left: 8px;">
        모든 알림 삭제
      </button>
    </div>

    <ul>
      <li
        v-for="notification in notificationList"
        :key="notification.notificationId"
        :class="{ unread: !notification.isRead }"
        style="margin-bottom: 12px;"
      >
        <!-- 메시지 클릭 시 게시글 상세로 이동 -->
        <router-link
          v-if="notification.type === 'comment' && getArticleId(notification)"
          :to="`/board/detail/${getArticleId(notification)}`"
          class="article-link"
          style="cursor: pointer; text-decoration: underline; color: blue;"
        >
          {{ renderMessage(notification) }}
        </router-link>

        <!-- 댓글 알림이 아닐 경우 그냥 텍스트 출력 -->
        <span v-else>{{ renderMessage(notification) }}</span>

        <div class="actions" style="margin-top: 6px;">
          <button
            @click="markAsRead(notification.notificationId)"
            :disabled="notification.isRead"
          >
            읽음
          </button>
          <button @click="deleteNotification(notification.notificationId)">삭제</button>
        </div>
      </li>
    </ul>
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

  switch (notification.type) {
    case "comment":
      return `게시글 ${payloadObj.articleId}에 새 댓글이 달렸습니다.`;
    case "like":
      return `게시글 ${payloadObj.articleId}가 좋아요를 받았습니다.`;
    case "challenge":
      return `새로운 챌린지가 도전 가능합니다!`;
    default:
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
.unread .article-link {
  font-weight: bold;
}

.actions button {
  margin-left: 8px;
}
</style>