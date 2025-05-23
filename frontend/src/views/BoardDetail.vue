<template>
  <div class="board-detail-wrapper">
    <div class="board-detail-container">
      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>게시글을 불러오는 중...</p>
      </div>

      <!-- 게시글 내용 -->
      <div v-if="!isLoading" class="article-container">
        <!-- 게시글 헤더 -->
        <div class="article-header card">
          <div class="article-category" v-if="article.category">
            <span class="category-tag">{{ getCategoryName(article.category) }}</span>
          </div>
          
          <h1 class="article-title">{{ article.title }}</h1>
          
          <div class="article-meta">
            <div class="author-info">
              <span
                class="author-wrapper"
                @click="toggleUserMenu"
                ref="authorNameRef"
              >
                <img :src="authorProfileImage" alt="프로필" class="author-avatar" />
                <div class="author-details">
                  <span class="author-name">{{ article.nickname || article.username }}</span>

                </div>
                <svg class="dropdown-icon" viewBox="0 0 24 24" width="16" height="16">
                  <path fill="currentColor" d="M7,10L12,15L17,10H7Z"/>
                </svg>
                
                <div v-if="showUserMenu" class="user-dropdown" @click.stop>
                  <ul>
                    <li @click="viewUserInfo">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M12,4A4,4 0 0,1 16,8A4,4 0 0,1 12,12A4,4 0 0,1 8,8A4,4 0 0,1 12,4M12,14C16.42,14 20,15.79 20,18V20H4V18C4,15.79 7.58,14 12,14Z"/>
                      </svg>
                      프로필 보기
                    </li>
                    <li v-if="user.userId !== article.userId" @click="reportUser">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M13,14H11V10H13M13,18H11V16H13M1,21H23L12,2L1,21Z"/>
                      </svg>
                      신고하기
                    </li>
                  </ul>
                </div>
              </span>
            </div>
            
            <div class="meta-info">
              <span class="meta-item">
                <svg viewBox="0 0 24 24" width="16" height="16">
                  <path fill="currentColor" d="M19,3H18V1H16V3H8V1H6V3H5A2,2 0 0,0 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V5A2,2 0 0,0 19,3M19,19H5V8H19V19Z"/>
                </svg>
                {{ formatDate(article.createdAt) }}
              </span>
              <span class="meta-item">
                <svg viewBox="0 0 24 24" width="16" height="16">
                  <path fill="currentColor" d="M12,9A3,3 0 0,0 9,12A3,3 0 0,0 12,15A3,3 0 0,0 15,12A3,3 0 0,0 12,9M12,17A5,5 0 0,1 7,12A5,5 0 0,1 12,7A5,5 0 0,1 17,12A5,5 0 0,1 12,17M12,4.5C7,4.5 2.73,7.61 1,12C2.73,16.39 7,19.5 12,19.5C17,19.5 21.27,16.39 23,12C21.27,7.61 17,4.5 12,4.5Z"/>
                </svg>
                {{ article.viewCount }}회
              </span>
            </div>
          </div>
        </div>

        <!-- 게시글 본문 -->
        <div class="article-content card">
          <!-- 비디오 컨텐츠 -->
          <div v-if="article.category === 'video'" class="video-container">
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
          
          <!-- 일반 텍스트 컨텐츠 -->
          <div v-else class="text-content" v-html="article.content"></div>
        </div>

        <!-- 게시글 액션 버튼 -->
        <div class="article-actions card">
          <div class="reaction-buttons">
            <button 
              @click="toggleLike" 
              :class="['reaction-btn', 'like-btn', { active: liked }]"
            >
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M5,9V21H1V9H5M9,21A2,2 0 0,1 7,19V9C7,8.45 7.22,7.95 7.59,7.59L14.17,1L15.23,2.06C15.5,2.33 15.67,2.7 15.67,3.11L15.64,3.43L14.69,8H21C22.11,8 23,8.9 23,10V12C23,12.26 22.95,12.5 22.86,12.73L19.84,19.78C19.54,20.5 18.83,21 18,21H9M9,19H18.03L21,12V10H12.21L13.34,4.68L9,9.03V19Z"/>
              </svg>
              <span>{{ article.likeCount }}</span>
            </button>
            
            <button 
              @click="toggleDislike" 
              :class="['reaction-btn', 'dislike-btn', { active: disliked }]"
            >
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M19,15V3H23V15H19M15,3A2,2 0 0,1 17,5V15C17,15.55 16.78,16.05 16.41,16.41L9.83,23L8.77,21.94C8.5,21.67 8.33,21.3 8.33,20.89L8.36,20.57L9.31,16H3C1.89,16 1,15.1 1,14V12C1,11.74 1.05,11.5 1.14,11.27L4.16,4.22C4.46,3.5 5.17,3 6,3H15M15,5H5.97L3,12V14H11.79L10.66,19.32L15,14.97V5Z"/>
              </svg>
              <span>{{ article.dislikeCount }}</span>
            </button>
          </div>

          <div class="action-buttons" v-if="isAuthor">
            <button @click="goToEdit" class="action-btn edit-btn">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M20.71,7.04C21.1,6.65 21.1,6 20.71,5.63L18.37,3.29C18,2.9 17.35,2.9 16.96,3.29L15.12,5.12L18.87,8.87M3,17.25V21H6.75L17.81,9.93L14.06,6.18L3,17.25Z"/>
              </svg>
              수정
            </button>
            <button @click="deleteArticle" class="action-btn delete-btn">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M19,4H15.5L14.5,3H9.5L8.5,4H5V6H19M6,19A2,2 0 0,0 8,21H16A2,2 0 0,0 18,19V7H6V19Z"/>
              </svg>
              삭제
            </button>
          </div>
        </div>
      </div>

      <!-- 댓글 섹션 -->
      <div v-if="!isLoading" class="comment-section card">
        <div class="comment-header">
          <h3 class="comment-title">
            <svg viewBox="0 0 24 24" width="20" height="20">
              <path fill="currentColor" d="M9,22A1,1 0 0,1 8,21V18H4A2,2 0 0,1 2,16V4C2,2.89 2.9,2 4,2H20A2,2 0 0,1 22,4V16A2,2 0 0,1 20,18H13.9L10.2,21.71C10,21.9 9.75,22 9.5,22H9Z"/>
            </svg>
            댓글 {{ comments.length }}개
          </h3>
        </div>

        <!-- 댓글 작성 -->
        <div class="comment-write">
          <div class="write-container">
            <img :src="userProfileImage" alt="내 프로필" class="my-avatar" />
            <div class="write-input-container">
              <textarea
                v-model="newComment"
                placeholder="댓글을 남겨보세요..."
                class="comment-textarea"
                rows="3"
              ></textarea>
              <div class="write-actions">
                <button 
                  @click="submitComment" 
                  :disabled="!newComment.trim()"
                  class="submit-btn"
                >
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" d="M2,21L23,12L2,3V10L17,12L2,14V21Z"/>
                  </svg>
                  댓글 등록
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 댓글 목록 -->
        <div v-if="comments.length === 0" class="no-comments">
          <svg viewBox="0 0 24 24" width="48" height="48">
            <path fill="currentColor" d="M12,3C6.5,3 2,6.6 2,11C2,13.1 3,15 4.5,16.5C4.2,17.5 3.7,18.4 3,19C4.2,19.3 6,18.8 7.3,17.8C8.5,18.3 10.2,18.7 12,18.7C17.5,18.7 22,15.1 22,10.7C22,6.3 17.5,2.7 12,2.7M12,17C7.6,17 4,14.1 4,10.5S7.6,4 12,4 20,6.9 20,10.5 16.4,17 12,17Z"/>
          </svg>
          <p>첫 번째 댓글을 남겨보세요!</p>
        </div>
        
        <div v-else class="comment-list">
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
        </div>
      </div>

      <!-- 신고 모달 -->
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

const userProfileImage = computed(() =>
  user.value?.profileImage
    ? `http://localhost:8080/images/profile/${user.value.profileImage}`
    : "/default-profile.png"
);

const getCategoryName = (category) => {
  const categoryMap = {
    video: '영상게시판',
    info: '정보게시판',
    question: '질문게시판'
  };
  return categoryMap[category] || category;
};

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
  if (confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
    await api.delete(`/api_article/delete/article_id/${articleId}`);
    router.push("/board");
  }
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
  if (confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
    await api.delete(`/api_comment/delete/comment_id/${commentId}`);
    fetchComments();
  }
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  const now = new Date();
  const diff = now - date;
  
  const minutes = Math.floor(diff / (1000 * 60));
  const hours = Math.floor(diff / (1000 * 60 * 60));
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (minutes < 1) return '방금 전';
  if (minutes < 60) return `${minutes}분 전`;
  if (hours < 24) return `${hours}시간 전`;
  if (days < 7) return `${days}일 전`;
  
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
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

// 유저 정보 보기
const viewUserInfo = () => {
  showUserMenu.value = false;
  router.push(`/summary/userId/${article.value.userId}`);
};
const viewCommentUserInfo = (comment) => {
  showCommentUserMenuId.value = null;
  router.push(`/summary/userId/${comment.userId}`);
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
/* 폰트 및 전체 배경 */
@import url("https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css");

* {
  box-sizing: border-box;
}

.board-detail-wrapper {
  font-family: "Pretendard Variable", "Pretendard", "Noto Sans KR", Arial, sans-serif;
  background: #f8f9fa;
  min-height: 100vh;
}

.board-detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* 카드 공통 스타일 */
.card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #e9ecef;
  overflow: visible;
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

/* 로딩 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #42b983;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 게시글 헤더 */
.article-header {
  padding: 2rem;
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
}

.article-category {
  margin-bottom: 1rem;
}

.category-tag {
  display: inline-block;
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #42b983, #369870);
  color: white;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.article-title {
  font-size: 2rem;
  font-weight: 700;
  color: #212529;
  margin: 0 0 1.5rem 0;
  line-height: 1.3;
  word-break: keep-all;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.author-info {
  position: relative;   
  overflow: visible;     
  z-index: auto;         
}




.author-wrapper {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 12px;
  transition: all 0.2s ease;
  user-select: none;
}

.author-wrapper:hover {
  background: rgba(66, 185, 131, 0.08);
}

.author-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.author-name {
  font-weight: 600;
  color: #212529;
  font-size: 1rem;
}

.author-id {
  font-size: 0.85rem;
  color: #6c757d;
}

.dropdown-icon {
  color: #6c757d;
  transition: transform 0.2s ease;
}

.author-wrapper:hover .dropdown-icon {
  transform: rotate(180deg);
}

.user-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  background: #fff;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  min-width: 180px;
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
  transition: background 0.2s ease;
  font-size: 0.9rem;
}

.user-dropdown li:hover {
  background: #f8f9fa;
}

.user-dropdown svg {
  color: #6c757d;
  flex-shrink: 0;
}

.meta-info {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #6c757d;
}

.meta-item svg {
  color: #adb5bd;
}

/* 게시글 본문 */
.article-content {
  padding: 2rem;
}

.video-container {
  margin-bottom: 1rem;
}

.video-player {
  width: 100%;
  height: 500px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.video-thumbnail {
  width: 100%;
  height: auto;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.text-content {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #212529;
  word-break: break-word;
}

.text-content h1, .text-content h2, .text-content h3,
.text-content h4, .text-content h5, .text-content h6 {
  margin: 1.5rem 0 1rem 0;
  color: #212529;
}

.text-content p {
  margin-bottom: 1rem;
}

.text-content img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 1rem 0;
}

/* 게시글 액션 버튼 */
.article-actions {
  padding: 1.5rem 2rem;
  background: #f8f9fa;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.reaction-buttons {
  display: flex;
  gap: 0.75rem;
}

.reaction-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.25rem;
  border: 2px solid #e9ecef;
  background: #fff;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  font-size: 0.9rem;
  min-width: 80px;
  justify-content: center;
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

.like-btn.active {
  color: #fff;
  background: linear-gradient(135deg, #42b983, #369870);
  border-color: #42b983;
  box-shadow: 0 4px 15px rgba(66, 185, 131, 0.3);
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

.dislike-btn.active {
  color: #fff;
  background: linear-gradient(135deg, #dc3545, #c82333);
  border-color: #dc3545;
  box-shadow: 0 4px 15px rgba(220, 53, 69, 0.3);
}

.action-buttons {
  display: flex;
  gap: 0.75rem;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.25rem;
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

/* 댓글 섹션 */
.comment-section {
  padding: 0;
}

.comment-header {
  padding: 1.5rem 2rem;
  border-bottom: 1px solid #e9ecef;
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
}

.comment-title {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin: 0;
  font-size: 1.3rem;
  font-weight: 600;
  color: #212529;
}

.comment-title svg {
  color: #42b983;
}

/* 댓글 작성 */
.comment-write {
  padding: 2rem;
  border-bottom: 1px solid #e9ecef;
  background: #f8f9fa;
}

.write-container {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
}

.my-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.write-input-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.comment-textarea {
  width: 100%;
  padding: 1rem;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  font-family: inherit;
  font-size: 1rem;
  line-height: 1.5;
  resize: vertical;
  transition: all 0.3s ease;
  background: #fff;
}

.comment-textarea:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.1);
}

.comment-textarea::placeholder {
  color: #adb5bd;
}

.write-actions {
  display: flex;
  justify-content: flex-end;
}

.submit-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #42b983, #369870);
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  font-size: 0.9rem;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(66, 185, 131, 0.3);
}

.submit-btn:disabled {
  background: #adb5bd;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 댓글 없음 */
.no-comments {
  padding: 3rem 2rem;
  text-align: center;
  color: #6c757d;
}

.no-comments svg {
  color: #adb5bd;
  margin-bottom: 1rem;
}

.no-comments p {
  margin: 0;
  font-size: 1.1rem;
}

/* 댓글 목록 */
.comment-list {
  padding: 1rem 0;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .board-detail-container {
    padding: 1rem 0.5rem;
    gap: 1rem;
  }

  .article-header,
  .article-content {
    padding: 1.5rem;
  }

  .article-title {
    font-size: 1.5rem;
  }

  .article-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }

  .meta-info {
    gap: 1rem;
  }

  .article-actions {
    padding: 1rem 1.5rem;
    flex-direction: column;
    align-items: stretch;
  }

  .reaction-buttons {
    justify-content: center;
  }

  .action-buttons {
    justify-content: center;
  }

  .video-player {
    height: 250px;
  }

  .write-container {
    flex-direction: column;
    gap: 0.75rem;
  }

  .my-avatar {
    align-self: flex-start;
  }

  .comment-write,
  .comment-header {
    padding: 1.5rem;
  }

  .user-dropdown {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 90%;
    max-width: 300px;
  }
}

@media (max-width: 480px) {
  .article-title {
    font-size: 1.3rem;
  }

  .reaction-btn,
  .action-btn {
    padding: 0.5rem 1rem;
    font-size: 0.85rem;
  }

  .meta-item {
    font-size: 0.8rem;
  }

  .comment-header,
  .comment-write {
    padding: 1rem;
  }
}
</style>