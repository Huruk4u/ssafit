<template>
  <div class="article-list">
    <table>
      <thead>
        <tr>
          <th v-if="category === 'video'">썸네일</th>
          <th>작성자</th>
          <th>제목</th>
          <th>작성일</th>
          <th>조회수</th>
          <th>좋아요</th>
          <th>댓글수</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="article in articles"
          :key="article.articleId"
          @click="$emit('detail', article.articleId)"
        >
          <td v-if="category === 'video'">
            <img
              :src="getThumbnailUrl(article.url)"
              alt="thumbnail"
              style="width: 120px; height: auto; object-fit: cover"
            />
          </td>
          <td>{{ article.nickname }}</td>
          <td>{{ article.title }}</td>
          <td>{{ formatDate(article.createdAt) }}</td>
          <td>{{ article.viewCount }}</td>
          <td>{{ article.likeCount }}</td>
          <td>{{ article.commentCount }}</td>
        </tr>
        <tr v-if="articles.length === 0">
          <td :colspan="category === 'video' ? 7 : 6" class="no-data">
            게시글이 없습니다.
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
// Pinia userStore는 이 컴포넌트에서 필요하지 않습니다.
// 게시글 목록은 props/emit만 사용합니다.
defineProps(["articles", "category"]);
defineEmits(["detail"]);

function getThumbnailUrl(url) {
  if (!url) return "";
  const youtubeRegex = /(?:youtube\.com\/watch\?v=|youtu\.be\/)([^&?/]+)/;
  const match = url.match(youtubeRegex);
  if (match && match[1]) {
    return `https://img.youtube.com/vi/${match[1]}/0.jpg`;
  }
  return url;
}

function formatDate(dateString) {
  if (!dateString) return "";
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(
    2,
    "0"
  )}-${String(date.getDate()).padStart(2, "0")}`;
}
</script>

<style scoped>
.article-list {
  margin-top: 10px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.07);
  overflow: hidden;
}
table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}
th,
td {
  padding: 16px 10px;
  text-align: center;
  border-bottom: 1px solid #f1f3f5;
  font-size: 1rem;
}
thead {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}
th {
  color: #42b983;
  font-weight: 700;
  font-size: 1.05rem;
  border-bottom: 2px solid #e9ecef;
}
tbody tr {
  transition: background 0.15s;
  cursor: pointer;
}
tbody tr:hover {
  background: #f5f5f5;
}
td img {
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(66, 185, 131, 0.1);
  background: #f8f9fa;
}
.no-data {
  text-align: center;
  padding: 32px 0;
  color: #adb5bd;
  font-size: 1.1rem;
  background: #f8f9fa;
}
@media (max-width: 900px) {
  .article-list {
    border-radius: 10px;
  }
  th,
  td {
    padding: 10px 4px;
    font-size: 0.95rem;
  }
}
@media (max-width: 600px) {
  .article-list {
    margin-top: 4px;
  }
  th,
  td {
    padding: 8px 2px;
    font-size: 0.9rem;
  }
}
</style>