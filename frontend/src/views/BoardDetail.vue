<template>
  <div class="board-detail-container">
    <Header />
    <div class="detail-content" v-if="!isLoading">
      <h2 class="title">{{ article.title }}</h2>
      <div class="meta">
        <span>작성자: {{ article.nickname || article.username }}</span>
        <span>작성일: {{ formatDate(article.createdAt) }}</span>
        <span>조회수: {{ article.viewCount }}</span>
      </div>
      <!-- Video Embed / Thumbnail -->
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
          좋아요 {{ liked ? '취소' : '' }} ({{ article.likeCount }})
        </button>
        <button @click="toggleDislike">
          싫어요 {{ disliked ? '취소' : '' }} ({{ article.dislikeCount }})
        </button>
        <button v-if="isAuthor" @click="goToEdit">수정</button>
        <button v-if="isAuthor" @click="deleteArticle">삭제</button>
      </div>
    </div>
    <div v-if="isLoading" class="loading">로딩 중...</div>

    <div class="comment-section" v-if="!isLoading">
      <h3>댓글 ({{ comments.length }})</h3>
      <div v-if="comments.length === 0" class="no-comments">등록된 댓글이 없습니다.</div>
      <ul class="comment-list">
        <li v-for="comment in comments" :key="comment.commentId">
          <div class="comment-meta">
            <span>{{ comment.nickname || comment.username }}</span>
            <span>{{ formatDate(comment.createdAt) }}</span>
          </div>
          <div class="comment-content" v-if="!isEditing(comment.commentId)">{{ comment.content }}</div>
          <textarea v-else v-model="editContent" />
          <div class="comment-actions">
            <button @click="toggleCommentLike(comment)">
              좋아요 ({{ comment.likeCount }})
            </button>
            <button @click="toggleCommentDislike(comment)">
              싫어요 ({{ comment.dislikeCount }})
            </button>
            <button v-if="isCommentAuthor(comment) && !isEditing(comment.commentId)" @click="startEditing(comment)">수정</button>
            <button v-if="isEditing(comment.commentId)" @click="confirmEdit(comment)">저장</button>
            <button v-if="isCommentAuthor(comment)" @click="deleteComment(comment.commentId)">삭제</button>
          </div>
        </li>
      </ul>
      <div class="new-comment">
        <textarea v-model="newComment" placeholder="댓글을 입력하세요..."></textarea>
        <button @click="submitComment" :disabled="!newComment.trim()">등록</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Header from '@/components/Header.vue';
import api from '@/api/axiosInstance';

const route = useRoute();
const router = useRouter();
const articleId = Number(route.params.articleId);
const user = ref(JSON.parse(localStorage.getItem('user') || 'null'));

const article = ref({});
const comments = ref([]);
const newComment = ref('');
const liked = ref(false);
const disliked = ref(false);
const isLoading = ref(true);

const editingId = ref(null);
const editContent = ref('');

const isYoutubeUrl = (url) => /youtu/gi.test(url);
const youtubeEmbedUrl = (url) => {
  const idMatch = url.match(/(?:\?v=|youtu\.be\/)([\w-]+)/);
  return idMatch ? `https://www.youtube.com/embed/${idMatch[1]}` : url;
};

const isAuthor = computed(() => {
  return user.value && article.value.userId === user.value.userId;
});
const isCommentAuthor = (comment) => {
  return user.value && comment.userId === user.value.userId;
};
const isEditing = (id) => editingId.value === id;

const fetchArticle = async () => {
  const res = await api.get(`/api_article/get/article_id/${articleId}`);
  article.value = res.data;
  liked.value = false;
  disliked.value = false;
};

const fetchComments = async () => {
  const res = await api.get('/api_comment/list', { params: { article_id: articleId } });
  comments.value = res.data || [];
};

const submitComment = async () => {
  await api.post('/api_comment/write', { content: newComment.value }, { params: { article_id: articleId } });
  newComment.value = '';
  fetchComments();
};

const toggleLike = async () => {
  const res = await api.post('/api_article/like', null, { params: { article_id: articleId } });
  liked.value = res.data;
  article.value.likeCount += liked.value ? 1 : -1;
};

const toggleDislike = async () => {
  const res = await api.post('/api_article/disLike', null, { params: { article_id: articleId } });
  disliked.value = res.data;
  article.value.dislikeCount += disliked.value ? 1 : -1;
};

const goToEdit = () => {
  router.push(`/board/edit/${articleId}`);
};
const deleteArticle = async () => {
  await api.delete(`/api_article/delete/article_id/${articleId}`);
  router.push('/board');
};

const toggleCommentLike = async (comment) => {
  const res = await api.post('/api_comment/like', null, { params: { comment_id: comment.commentId } });
  comment.likeCount += res.data ? 1 : -1;
};

const toggleCommentDislike = async (comment) => {
  const res = await api.post('/api_comment/dislike', null, { params: { comment_id: comment.commentId } });
  comment.dislikeCount += res.data ? 1 : -1;
};

const startEditing = (comment) => {
  editingId.value = comment.commentId;
  editContent.value = comment.content;
};

const confirmEdit = async (comment) => {
  await api.put(`/api_comment/put/comment_id/${comment.commentId}`, { content: editContent.value });
  editingId.value = null;
  fetchComments();
};

const deleteComment = async (commentId) => {
  await api.delete(`/api_comment/delete/comment_id/${commentId}`);
  fetchComments();
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth()+1).padStart(2,'0')}-${String(date.getDate()).padStart(2,'0')} ${String(date.getHours()).padStart(2,'0')}:${String(date.getMinutes()).padStart(2,'0')}`;
};

onMounted(async () => {
  await fetchArticle();
  await fetchComments();
  isLoading.value = false;
});
</script>

<style scoped>
.video-player { width: 100%; height: 450px; margin-bottom: 20px; }
.video-thumbnail { width: 100%; height: auto; margin-bottom: 20px; }
.board-detail-container { max-width: 800px; margin: 0 auto; padding: 20px; }
.title { font-size: 24px; margin-bottom: 10px; }
.meta { display: flex; gap: 15px; color: #666; margin-bottom: 20px; }
.content { line-height: 1.6; margin-bottom: 20px; }
.actions button { margin-right: 10px; }
.loading { text-align: center; padding: 50px; font-size: 18px; color: #666; }
.comment-section { border-top: 1px solid #ddd; padding-top: 20px; }
.comment-list { list-style: none; padding: 0; margin: 0 0 20px; }
.comment-list li { border-bottom: 1px solid #eee; padding: 10px 0; }
.comment-meta { font-size: 12px; color: #999; margin-bottom: 5px; }
.comment-actions button { margin-right: 5px; }
.new-comment textarea { width: 100%; height: 80px; padding: 8px; border: 1px solid #ddd; border-radius: 4px; resize: vertical; margin-bottom: 10px; }
.new-comment button { padding: 8px 16px; background-color: #42b983; color: white; border: none; border-radius: 4px; cursor: pointer; }
</style>