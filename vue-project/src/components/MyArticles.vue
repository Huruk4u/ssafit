<template>
  <div>
    <ul>
      <li v-for="article in myArticles" :key="article.articleId">
        <router-link
          :to="`/board/detail/${article.articleId}`"
          class="article-link"
        >
          <strong>{{ article.title }}</strong>
        </router-link>
        <span> - {{ article.createdAt }}</span>
      </li>
    </ul>
  </div>
</template>

<script setup>
import api from "@/api/axiosInstance";
import { ref, onMounted } from "vue";

const user = ref(JSON.parse(localStorage.getItem("user") || "{}"));
const myArticles = ref([]);

onMounted(() => {
  api
    .get(`/api_article/get/user_id/${user.value.userId}`)
    .then((res) => {
      console.log(res.data);
      myArticles.value = res.data;
    })
    .catch((err) => {
      console.error(err);
      alert("내가 쓴 글을 불러오는 데 실패했습니다.");
    });
});
</script>

<style scoped>
.article-link {
  text-decoration: none;
  color: #007bff;
}

.article-link:hover {
  text-decoration: underline;
}
</style>