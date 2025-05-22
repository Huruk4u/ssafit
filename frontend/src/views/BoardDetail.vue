<template>
  <div class="board-detail-container">
    <Header />
    <div class="detail-content" v-if="!isLoading">
      <h2 class="title">{{ article.title }}</h2>
      <div class="meta">
        <span
          class="author-name-wrapper"
          @click="toggleUserMenu"
          ref="authorNameRef"
        >
          <!-- 프로필 이미지(아바타) 추가 -->
          <img :src="authorProfileImage" alt="프로필" class="author-avatar" />
          작성자:
          <span class="author-name">{{
            article.nickname || article.username
          }}</span>
          <span class="dropdown-arrow">▼</span>
          <div v-if="showUserMenu" class="user-menu" @click.stop>
            <ul>
              <li @click="viewUserInfo">유저 정보 보기</li>
              <li v-if="user.userId !== article.userId" @click="reportUser">
                유저 신고하기
              </li>
            </ul>
          </div>
        </span>
        <span>작성일: {{ formatDate(article.createdAt) }}</span>
        <span>조회수: {{ article.viewCount }}</span>
      </div>
      <div v-if="article.category === 'video'" class="video-embed">
        <iframe
          v-if="isYoutubeUrl(article.url)"
          :src="youtubeEmbedUrl(article.url)"
          frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowfullscreen
          class="video-player"
        ></iframe>
        <img
          v-else
          :src="article.url"
          alt="Video Thumbnail"
          class="video-thumbnail"
        />
      </div>
      <div v-else class="content" v-html="article.content"></div>
      <div class="actions">
        <button @click="toggleLike">
          좋아요 {{ liked ? "취소" : "" }} ({{ article.likeCount }})
        </button>
        <button @click="toggleDislike">
          싫어요 {{ disliked ? "취소" : "" }} ({{ article.dislikeCount }})
        </button>
        <button v-if="isAuthor" @click="goToEdit">수정</button>
        <button v-if="isAuthor" @click="deleteArticle">삭제</button>
      </div>
    </div>
    <div v-if="isLoading" class="loading">로딩 중...</div>

    <div class="comment-section" v-if="!isLoading">
      <h3>댓글 ({{ comments.length }})</h3>
      <div v-if="comments.length === 0" class="no-comments">
        등록된 댓글이 없습니다.
      </div>
      <ul class="comment-list">
        <Comment
          v-for="comment in comments"
          :key="comment.commentId"
          :comment="comment"
          :userId="user.userId"
          :isEditing="isEditing(comment.commentId)"
          :isCommentAuthor="isCommentAuthor(comment)"
          :editContent="editContent"
          @update:editContent="editContent = $event"
          :showMenu="showCommentUserMenuId === comment.commentId"
          :formatDate="formatDate"
          @toggleMenu="toggleCommentUserMenu"
          @viewUserInfo="viewCommentUserInfo"
          @reportUser="reportCommentUser"
          @like="toggleCommentLike"
          @dislike="toggleCommentDislike"
          @edit="startEditing"
          @confirmEdit="confirmEdit"
          @delete="deleteComment"
        />
      </ul>
      <div class="new-comment">
        <textarea
          v-model="newComment"
          placeholder="댓글을 입력하세요..."
        ></textarea>
        <button @click="submitComment" :disabled="!newComment.trim()">
          등록
        </button>
      </div>
    </div>

    <!-- 신고 모달 분리 적용 -->
    <ReportModal
      :show="showReportModal"
      :target="reportTarget"
      :categories="reportCategories"
      :category="selectedReportCategory"
      :content="reportContent"
      @submit="submitReport"
      @close="closeReportModal"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useRoute, useRouter } from "vue-router";
import Header from "@/components/Header.vue";
import api from "@/api/axiosInstance";
import Comment from "@/components/CommentItem.vue";
import ReportModal from "@/components/ReportModal.vue";

const route = useRoute();
const router = useRouter();
const articleId = Number(route.params.articleId);

const user = ref(JSON.parse(localStorage.getItem("user") || "null"));

const article = ref({});
const comments = ref([]);
const newComment = ref("");
const liked = ref(false);
const disliked = ref(false);
const isLoading = ref(true);

const editingId = ref(null);
const editContent = ref("");

console.log(article.value);

const isYoutubeUrl = (url) => /youtu/gi.test(url);
const youtubeEmbedUrl = (url) => {
  const idMatch = url.match(/(?:\?v=|youtu\.be\/)([\w-]+)/);
  return idMatch ? `https://www.youtube.com/embed/${idMatch[1]}` : url;
};

const isAuthor = computed(
  () => user.value && article.value.userId === user.value.userId
);
const isCommentAuthor = (comment) =>
  user.value && comment.userId === user.value.userId;
const isEditing = (id) => editingId.value === id;

const author = ref({});
const authorProfileImage = computed(() =>
  author.value.profileImage
    ? `http://localhost:8080/images/profile/${author.value.profileImage}`
    : "/default-profile.png"
);

const fetchArticle = async () => {
  const res = await api.get(`/api_article/get/article_id/${articleId}`);
  article.value = res.data;
  liked.value = false;
  disliked.value = false;

  if (article.value.userId) {
    const res = await api.get(
      `/api_user/get/user/userId/${article.value.userId}`
    );
    author.value = res.data;
  }
};

const fetchComments = async () => {
  const res = await api.get("/api_comment/list", {
    params: { article_id: articleId },
  });
  comments.value = res.data || [];
};

const submitComment = async () => {
  await api.post(
    "/api_comment/write",
    { content: newComment.value },
    { params: { article_id: articleId } }
  );
  newComment.value = "";
  fetchComments();
};

const toggleLike = async () => {
  await api.post("/api_article/like", null, {
    params: { article_id: articleId },
  });
  await fetchArticle(); // 항상 최신 데이터로 동기화
};

const toggleDislike = async () => {
  await api.post("/api_article/disLike", null, {
    params: { article_id: articleId },
  });
  await fetchArticle(); // 항상 최신 데이터로 동기화
};

const goToEdit = () => router.push(`/board/edit/${articleId}`);
const deleteArticle = async () => {
  await api.delete(`/api_article/delete/article_id/${articleId}`);
  router.push("/board");
};

const reportCategories = ["욕설/비방", "광고", "도배", "음란물", "기타"];

const toggleCommentLike = async (comment) => {
  const res = await api.post("/api_comment/like", null, {
    params: { comment_id: comment.commentId },
  });
  await fetchComments();
};

const toggleCommentDislike = async (comment) => {
  const res = await api.post("/api_comment/dislike", null, {
    params: { comment_id: comment.commentId },
  });
  await fetchComments();
};

const startEditing = (comment) => {
  editingId.value = comment.commentId;
  editContent.value = comment.content;
};

const confirmEdit = async (comment) => {
  await api.put(`/api_comment/put/comment_id/${comment.commentId}`, {
    content: editContent.value,
  });
  editingId.value = null;
  fetchComments();
};

const deleteComment = async (commentId) => {
  await api.delete(`/api_comment/delete/comment_id/${commentId}`);
  fetchComments();
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(
    2,
    "0"
  )}-${String(date.getDate()).padStart(2, "0")} ${String(
    date.getHours()
  ).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;
};

// 게시글 작성자 옵션 메뉴
const showUserMenu = ref(false);
const authorNameRef = ref(null);

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value;
};

// 댓글 작성자 옵션 메뉴
const showCommentUserMenuId = ref(null);

const toggleCommentUserMenu = (commentId, event) => {
  showCommentUserMenuId.value =
    showCommentUserMenuId.value === commentId ? null : commentId;
  if (event) event.stopPropagation();
};

// 메뉴 외부 클릭 시 닫기
const handleClickOutside = (e) => {
  if (
    showUserMenu.value &&
    authorNameRef.value &&
    !authorNameRef.value.contains(e.target)
  ) {
    showUserMenu.value = false;
  }
  if (showCommentUserMenuId.value) {
    const menu = document.querySelector(".comment-list .user-menu");
    if (menu && !menu.contains(e.target)) {
      showCommentUserMenuId.value = null;
    }
  }
};
onMounted(async () => {
  await fetchArticle();
  await fetchComments();
  isLoading.value = false;
  document.addEventListener("click", handleClickOutside);
});
onBeforeUnmount(() => {
  document.removeEventListener("click", handleClickOutside);
});

// 메뉴 항목 동작
const viewUserInfo = () => {
  showUserMenu.value = false;
  alert(`유저 정보 보기: ${article.value.nickname || article.value.username}`);
};

const viewCommentUserInfo = (comment) => {
  showCommentUserMenuId.value = null;
  alert(`유저 정보 보기: ${comment.nickname || comment.username}`);
};

// 신고 모달 상태 및 동작
const showReportModal = ref(false);
const selectedReportCategory = ref("");
const reportContent = ref("");
const reportTarget = ref(null);

const reportUser = () => {
  showUserMenu.value = false;
  reportTarget.value = {
    userId: article.value.userId,
    nickname: article.value.nickname || article.value.username,
    articleId: article.value.articleId,
    type: "ARTICLE",
  };
  selectedReportCategory.value = "";
  reportContent.value = "";
  showReportModal.value = true;
};

const reportCommentUser = (comment) => {
  showCommentUserMenuId.value = null;
  reportTarget.value = {
    userId: comment.userId,
    nickname: comment.nickname || comment.username,
    articleId: article.value.articleId,
    commentId: comment.commentId,
    type: "COMMENT",
  };
  selectedReportCategory.value = "";
  reportContent.value = "";
  showReportModal.value = true;
};

const closeReportModal = () => {
  showReportModal.value = false;
  selectedReportCategory.value = "";
  reportContent.value = "";
};

const submitReport = async ({ category, content }) => {
  if (!category) {
    alert("신고 사유를 선택하세요.");
    return;
  }
  try {
    if (reportTarget.value.type === "ARTICLE") {
      await api.post(`/api_report/post/article`, {
        reporterId: user.value.userId,
        reporterName: user.value.nickname || user.value.username,
        reporteeId: reportTarget.value.userId,
        reporteeName: reportTarget.value.nickname,
        reportCategory: category,
        articleId: reportTarget.value.articleId,
        type: "ARTICLE",
        content,
      });
    } else if (reportTarget.value.type === "COMMENT") {
      await api.post(`/api_report/post/comment`, {
        reporterId: user.value.userId,
        reporterName: user.value.nickname || user.value.username,
        reporteeId: reportTarget.value.userId,
        reporteeName: reportTarget.value.nickname,
        reportCategory: category,
        articleId: reportTarget.value.articleId,
        commentId: reportTarget.value.commentId,
        type: "COMMENT",
        content,
      });
    }
    alert("신고가 접수되었습니다.");
    closeReportModal();
  } catch (e) {
    alert("신고 처리 중 오류가 발생했습니다.");
    closeReportModal();
  }
};
</script>

<style scoped>
.video-player {
  width: 100%;
  height: 450px;
  margin-bottom: 20px;
}
.video-thumbnail {
  width: 100%;
  height: auto;
  margin-bottom: 20px;
}
.board-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.title {
  font-size: 24px;
  margin-bottom: 10px;
}
.meta {
  display: flex;
  gap: 15px;
  color: #666;
  margin-bottom: 20px;
  position: relative;
}
.author-name-wrapper {
  cursor: pointer;
  color: #2d8cf0;
  position: relative;
  user-select: none;
  display: inline-block;
}
.author-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 8px;
}
.author-name {
  font-weight: bold;
  margin-left: 4px;
}
.dropdown-arrow {
  font-size: 10px;
  margin-left: 2px;
}
.user-menu {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  min-width: 140px;
  position: absolute;
  left: 0;
  top: 100%;
  margin-top: 4px;
  z-index: 1000;
  padding: 0;
}
.user-menu ul {
  list-style: none;
  margin: 0;
  padding: 0;
}
.user-menu li {
  padding: 10px 18px;
  cursor: pointer;
  transition: background 0.15s;
}
.user-menu li:hover {
  background: #f5f7fa;
}
.content {
  line-height: 1.6;
  margin-bottom: 20px;
}
.actions button {
  margin-right: 10px;
}
.loading {
  text-align: center;
  padding: 50px;
  font-size: 18px;
  color: #666;
}
.comment-section {
  border-top: 1px solid #ddd;
  padding-top: 20px;
}
.comment-list {
  list-style: none;
  padding: 0;
  margin: 0 0 20px;
}
.comment-list li {
  border-bottom: 1px solid #eee;
  padding: 10px 0;
}
.comment-meta {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}
.comment-actions button {
  margin-right: 5px;
}
.new-comment textarea {
  width: 100%;
  height: 80px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  margin-bottom: 10px;
}
.new-comment button {
  padding: 8px 16px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}
.modal-content {
  background: #fff;
  border-radius: 8px;
  padding: 32px 24px;
  min-width: 280px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.18);
  text-align: center;
}
.modal-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}
.modal-actions button {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  background: #42b983;
  color: #fff;
  cursor: pointer;
  font-size: 1em;
}
.modal-actions button:last-child {
  background: #aaa;
}
</style>