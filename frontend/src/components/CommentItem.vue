<template>
  <li class="comment-card card">
    <div class="comment-header">
      <div class="comment-author-info">
        <img
          :src="commentAuthorProfileImage"
          alt="프로필"
          class="comment-avatar"
        />
        <div class="comment-author-details">
          <span
            class="comment-author-name author-name-wrapper"
            @click="onToggleMenu"
          >
            {{ comment.nickname || comment.username }}
            <svg
              class="dropdown-arrow"
              viewBox="0 0 24 24"
              width="16"
              height="16"
              style="color: #6c757d"
            >
              <path fill="currentColor" d="M7,10L12,15L17,10H7Z" />
            </svg>
            <div v-if="showMenu" class="user-dropdown" @click.stop>
              <ul>
                <li @click="onViewUserInfo">
                  <svg
                    viewBox="0 0 24 24"
                    width="16"
                    height="16"
                    style="color: #6c757d"
                  >
                    <path
                      fill="currentColor"
                      d="M12,4A4,4 0 0,1 16,8A4,4 0 0,1 12,12A4,4 0 0,1 8,8A4,4 0 0,1 12,4M12,14C16.42,14 20,15.79 20,18V20H4V18C4,15.79 7.58,14 12,14Z"
                    />
                  </svg>
                  프로필 보기
                </li>
                <li v-if="userId !== comment.userId" @click="onReportUser">
                  <svg
                    viewBox="0 0 24 24"
                    width="16"
                    height="16"
                    style="color: #6c757d"
                  >
                    <path
                      fill="currentColor"
                      d="M13,14H11V10H13M13,18H11V16H13M1,21H23L12,2L1,21Z"
                    />
                  </svg>
                  신고하기
                </li>
              </ul>
            </div>
          </span>
          <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
        </div>
      </div>
      <!-- 오른쪽 상단에 수정/삭제/저장/취소 버튼 -->
      <div v-if="isCommentAuthor" class="comment-header-actions">
        <template v-if="!isEditing">
          <button class="action-btn edit-btn" @click="$emit('edit', comment)">
            수정
          </button>
          <button
            class="action-btn delete-btn"
            @click="$emit('delete', comment.commentId)"
          >
            삭제
          </button>
        </template>
        <template v-else>
          <button
            class="action-btn edit-btn"
            @click="$emit('confirmEdit', comment)"
          >
            저장
          </button>
          <button
            class="action-btn delete-btn"
            @click="$emit('delete', comment.commentId)"
          >
            취소
          </button>
        </template>
      </div>
    </div>
    <div class="comment-content" v-if="!isEditing">{{ comment.content }}</div>
    <div v-else>
      <textarea
        class="comment-edit-textarea"
        :value="editContent"
        @input="$emit('update:editContent', $event.target.value)"
      />
    </div>
    <div class="comment-actions">
      <button
        @click="$emit('like', comment)"
        :class="['reaction-btn', 'like-btn']"
      >
        <svg viewBox="0 0 24 24" width="20" height="20" style="color: #42b983">
          <path
            fill="currentColor"
            d="M5,9V21H1V9H5M9,21A2,2 0 0,1 7,19V9C7,8.45 7.22,7.95 7.59,7.59L14.17,1L15.23,2.06C15.5,2.33 15.67,2.7 15.67,3.11L15.64,3.43L14.69,8H21C22.11,8 23,8.9 23,10V12C23,12.26 22.95,12.5 22.86,12.73L19.84,19.78C19.54,20.5 18.83,21 18,21H9M9,19H18.03L21,12V10H12.21L13.34,4.68L9,9.03V19Z"
          />
        </svg>
        <span>{{ comment.likeCount }}</span>
      </button>
      <button
        @click="$emit('dislike', comment)"
        :class="['reaction-btn', 'dislike-btn']"
      >
        <svg viewBox="0 0 24 24" width="20" height="20" style="color: #dc3545">
          <path
            fill="currentColor"
            d="M19,15V3H23V15H19M15,3A2,2 0 0,1 17,5V15C17,15.55 16.78,16.05 16.41,16.41L9.83,23L8.77,21.94C8.5,21.67 8.33,21.3 8.33,20.89L8.36,20.57L9.31,16H3C1.89,16 1,15.1 1,14V12C1,11.74 1.05,11.5 1.14,11.27L4.16,4.22C4.46,3.5 5.17,3 6,3H15M15,5H5.97L3,12V14H11.79L10.66,19.32L15,14.97V5Z"
          />
        </svg>
        <span>{{ comment.dislikeCount }}</span>
      </button>
    </div>
  </li>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import api from "@/api/axiosInstance.js";
import { useUserImage } from "@/composables/useUserImage"; // 추가

const props = defineProps({
  comment: Object,
  isEditing: Boolean,
  isCommentAuthor: Boolean,
  editContent: String,
  showMenu: Boolean,
  formatDate: Function,
  userId: Number,
});
const emit = defineEmits([
  "toggleMenu",
  "viewUserInfo",
  "reportUser",
  "like",
  "dislike",
  "edit",
  "confirmEdit",
  "delete",
  "update:editContent",
]);

const commentAuthor = ref({});

// useUserImage로 프로필 이미지 처리
const { getProfileImage } = useUserImage(commentAuthor.value);
const commentAuthorProfileImage = computed(() => getProfileImage());

const fetchComment = async () => {
  const res = await api.get(
    `/api_user/get/user/userId/${props.comment.userId}`
  );
  if (res.status === 200) {
    commentAuthor.value = res.data;
  }
};

const onToggleMenu = (e) => {
  emit("toggleMenu", props.comment.commentId, e);
};
const onViewUserInfo = () => {
  emit("viewUserInfo", props.comment);
};
const onReportUser = () => {
  emit("reportUser", props.comment);
};

onMounted(() => {
  fetchComment();
});
</script>

<style scoped>
@import url("https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css");

.comment-card {
  margin-bottom: 1.5rem;
  padding: 1.5rem 2rem;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #e9ecef;
  transition: all 0.3s;
  list-style: none;
  font-family: "Pretendard Variable", "Pretendard", "Noto Sans KR", Arial,
    sans-serif;
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 좌우 분리 */
  margin-bottom: 0.5rem;
}

.comment-header-actions {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.comment-author-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  position: relative;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.comment-author-details {
  display: flex;
  flex-direction: column;
  gap: 0.1rem;
}

.comment-author-name {
  font-weight: 600;
  color: #212529;
  font-size: 1rem;
  cursor: pointer;
  user-select: none;
  display: flex;
  align-items: center;
  gap: 0.3rem;
  position: relative;
}

.dropdown-arrow {
  color: #6c757d;
  margin-left: 2px;
}

.user-dropdown {
  position: absolute;
  top: 120%;
  left: 0;
  background: #fff;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  min-width: 170px;
  z-index: 9999;
  margin-top: 0.5rem;
  overflow: hidden;
}

.user-dropdown ul {
  list-style: none;
  margin: 0;
  padding: 0.5rem 0;
}

.user-dropdown li {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  cursor: pointer;
  font-size: 0.9rem;
  color: #212529;
  transition: background 0.2s;
}

.user-dropdown li:hover {
  background: #f8f9fa;
}

.user-dropdown svg {
  color: #6c757d;
  flex-shrink: 0;
  margin-right: 0.5rem;
  vertical-align: middle;
}

.comment-date {
  font-size: 0.85rem;
  color: #6c757d;
  margin-left: 0.5rem;
}

.comment-content {
  margin: 0.5rem 0 0.5rem 0;
  font-size: 1.05rem;
  color: #212529;
  line-height: 1.7;
  word-break: break-word;
}

.comment-edit-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  font-size: 1rem;
  margin-bottom: 0.5rem;
  resize: vertical;
}

.comment-actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
  margin-top: 0.5rem;
}

.reaction-btn {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.5rem 1rem;
  border: 2px solid #e9ecef;
  background: #fff;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 600;
  font-size: 0.9rem;
  min-width: 60px;
  justify-content: center;
}

.reaction-btn svg {
  margin-right: 2px;
  vertical-align: middle;
}

.reaction-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.like-btn {
  color: #6c757d;
  border-color: #e9ecef;
}
.like-btn:hover {
  color: #42b983;
  border-color: #42b983;
  background: rgba(66, 185, 131, 0.05);
}

.dislike-btn {
  color: #6c757d;
  border-color: #e9ecef;
}
.dislike-btn:hover {
  color: #dc3545;
  border-color: #dc3545;
  background: rgba(220, 53, 69, 0.05);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  font-size: 0.9rem;
}

.edit-btn {
  background: #42b983;
  color: white;
}

.edit-btn:hover {
  background: #369870;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.delete-btn {
  background: #dc3545;
  color: white;
}

.delete-btn:hover {
  background: #c82333;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(220, 53, 69, 0.3);
}

/* 반응형 */
@media (max-width: 600px) {
  .comment-card {
    padding: 1rem 0.5rem;
    border-radius: 10px;
  }
  .comment-avatar {
    width: 30px;
    height: 30px;
  }
  .comment-content {
    font-size: 0.95rem;
  }
  .reaction-btn,
  .action-btn {
    padding: 0.4rem 0.7rem;
    font-size: 0.85rem;
  }
  .comment-header-actions {
    gap: 0.2rem;
  }
}
</style>