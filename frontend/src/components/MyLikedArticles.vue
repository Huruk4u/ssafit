<template>
  <div class="articles-container">
    <table class="articles-table">
      <thead>
        <tr>
          <th>번호</th>
          <th class="title-column">제목</th>
          <th>작성자</th>
          <th>작성일</th>
          <th>조회수</th>
          <th>좋아요</th>
          <th>댓글수</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="article in likedArticles" :key="article.articleId">
          <td>{{ article.articleId }}</td>
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
  </div>
</template>

<script setup>
import api from "@/api/axiosInstance";
import { ref, onMounted } from "vue";

const user = ref(JSON.parse(localStorage.getItem("user") || "{}"));
const likedArticles = ref([]);

const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(
    2,
    "0"
  )}-${String(date.getDate()).padStart(2, "0")}`;
};

onMounted(() => {
  if (user.value.userId) {
    api
      .get(`/api_article/get/like/user_id/${user.value.userId}`)
      .then((res) => {
        console.log(res.data);
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
.articles-container {
  width: 100%;
  overflow-x: auto;
}

.articles-table {
  width: 100%;
  border-collapse: collapse;
  border-spacing: 0;
  margin-bottom: 20px;
}

.articles-table th,
.articles-table td {
  padding: 12px;
  text-align: center;
  border-bottom: 1px solid #ddd;
}

.articles-table th {
  background-color: #f2f2f2;
  font-weight: bold;
  color: #333;
}

.articles-table tr:hover {
  background-color: #f5f5f5;
}

.title-column {
  text-align: left;
  width: 40%;
}

.article-link {
  text-decoration: none;
  color: #007bff;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.article-link:hover {
  text-decoration: underline;
}

.no-data {
  text-align: center;
  padding: 20px;
  color: #666;
  font-style: italic;
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
</style>