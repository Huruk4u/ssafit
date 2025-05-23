<template>
  <div class="articles-container">
    <table class="articles-table">
      <thead>
        <tr>
          <th>게시판</th>
          <th class="title-column">제목</th>
          <th>작성자</th>
          <th>
            <svg viewBox="0 0 24 24" width="16" height="16" style="vertical-align:middle;">
              <path fill="currentColor" d="M19,3H18V1H16V3H8V1H6V3H5A2,2 0 0,0 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V5A2,2 0 0,0 19,3M19,19H5V8H19V19Z"/>
            </svg>
            작성일
          </th>
          <th>
            <svg viewBox="0 0 24 24" width="16" height="16" style="vertical-align:middle;">
              <path fill="currentColor" d="M12,4.5C7,4.5 2.73,7.61 1,12C2.73,16.39 7,19.5 12,19.5C17.5,19.5 21.27,16.39 23,12C21.27,7.61 17.5,4.5 12,4.5Z"/>
            </svg>
            조회수
          </th>
          <th>
            <svg viewBox="0 0 24 24" width="16" height="16" style="vertical-align:middle;">
              <path fill="currentColor" d="M5,9V21H1V9H5M9,21A2,2 0 0,1 7,19V9C7,8.45 7.22,7.95 7.59,7.59L14.17,1L15.23,2.06C15.5,2.33 15.67,2.7 15.67,3.11L15.64,3.43L14.69,8H21C22.11,8 23,8.9 23,10V12C23,12.26 22.95,12.5 22.86,12.73L19.84,19.78C19.54,20.5 18.83,21 18,21H9M9,19H18.03L21,12V10H12.21L13.34,4.68L9,9.03V19Z"/>
            </svg>
            좋아요
          </th>
          <th>
            <svg viewBox="0 0 24 24" width="16" height="16" style="vertical-align:middle;">
              <path fill="currentColor" d="M9,22A1,1 0 0,1 8,21V18H4A2,2 0 0,1 2,16V4C2,2.89 2.9,2 4,2H20A2,2 0 0,1 22,4V16A2,2 0 0,1 20,18H13.9L10.2,21.71C10,21.9 9.75,22 9.5,22H9Z"/>
            </svg>
            댓글수
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="article in pagedArticles" :key="article.articleId">
          <td>{{ getCategoryName(article.category) }}</td>
          <td class="title-column">
            <router-link
              :to="`/board/detail/${article.articleId}`"
              class="article-link"
            >
              {{ article.title }}
            </router-link>
          </td>
          <td>{{ article.nickname }}</td>
          <td>{{ formatDate(article.createdAt) }}</td>
          <td>{{ article.viewCount }}</td>
          <td>{{ article.likeCount }}</td>
          <td>{{ article.commentCount }}</td>
        </tr>
        <tr v-if="likedArticles.length === 0">
          <td colspan="7" class="no-data">좋아요를 누른 글이 없습니다.</td>
        </tr>
      </tbody>
    </table>
    <div class="pagination" v-if="totalPages > 1">
      <button @click="prevPage" :disabled="currentPage === 1">이전</button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages">다음</button>
    </div>
  </div>
</template>

<script setup>
import api from "@/api/axiosInstance";
import { ref, onMounted, computed } from "vue";

const user = ref(JSON.parse(localStorage.getItem("user") || "{}"));
const likedArticles = ref([]);
const currentPage = ref(1);
const pageSize = 10;

const getCategoryName = (category) => {
  const categoryMap = {
    video: "영상",
    info: "정보",
    question: "질문",
  };
  return categoryMap[category] || category;
};

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(
    2,
    "0"
  )}-${String(date.getDate()).padStart(2, "0")}`;
};

const pagedArticles = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return likedArticles.value.slice(start, start + pageSize);
});
const totalPages = computed(() =>
  Math.ceil(likedArticles.value.length / pageSize)
);

const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--;
};
const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++;
};

onMounted(() => {
  if (user.value.userId) {
    api
      .get(`/api_mypage/get/like/user_id/${user.value.userId}`)
      .then((res) => {
        likedArticles.value = res.data;
      })
      .catch((err) => {
        console.error(err);
        alert("내가 좋아요 한 글을 불러오는 데 실패했습니다.");
      });
  }
});
</script>

<style scoped>
@import url("https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css");

.articles-container {
  width: 100%;
  overflow-x: auto;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.07);
  margin-top: 10px;
  font-family: "Pretendard Variable", "Pretendard", "Noto Sans KR", Arial, sans-serif;
}

.articles-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}

.articles-table th,
.articles-table td {
  padding: 16px 10px;
  text-align: center;
  border-bottom: 1px solid #f1f3f5;
  font-size: 1rem;
  font-family: inherit;
  color: #222;
}

.articles-table thead {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.articles-table th {
  color: #42b983;
  font-weight: 700;
  font-size: 1.05rem;
  border-bottom: 2px solid #e9ecef;
  font-family: inherit;
}

.articles-table tr {
  transition: background 0.15s;
  cursor: pointer;
}

.articles-table tr:hover {
  background: #f5f5f5;
}

.title-column {
  text-align: left;
  width: 40%;
}

.article-link {
  text-decoration: none;
  color: #2563eb;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
  font-family: inherit;
  transition: color 0.2s;
}

.article-link:hover {
  text-decoration: underline;
  color: #42b983;
}

.no-data {
  text-align: center;
  padding: 32px 0;
  color: #adb5bd;
  font-size: 1.1rem;
  background: #f8f9fa;
  font-family: inherit;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 18px;
  margin: 24px 0 10px 0;
  font-family: inherit;
}

.pagination button {
  background: #42b983;
  color: #fff;
  border: none;
  border-radius: 16px;
  padding: 6px 18px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.pagination button:disabled {
  background: #adb5bd;
  cursor: not-allowed;
}
.pagination span {
  font-size: 1.05rem;
  color: #222;
  font-weight: 500;
}

@media (max-width: 900px) {
  .articles-container { border-radius: 10px; }
  .articles-table th,
  .articles-table td { padding: 10px 4px; font-size: 0.95rem; }
}

@media (max-width: 768px) {
  .articles-table th,
  .articles-table td {
    padding: 8px;
  }
  .articles-table th:nth-child(3),
  .articles-table th:nth-child(5),
  .articles-table th:nth-child(6),
  .articles-table td:nth-child(3),
  .articles-table td:nth-child(5),
  .articles-table td:nth-child(6) {
    display: none;
  }
}
@media (max-width: 600px) {
  .articles-container { margin-top: 4px; }
  .articles-table th,
  .articles-table td { padding: 8px 2px; font-size: 0.9rem; }
}
</style>