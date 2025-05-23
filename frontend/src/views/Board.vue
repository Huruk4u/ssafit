<template>
  <div class="board-container">
    <div class="board-header">
      <BoardCategoryTabs v-model="currentCategory" />
      <button class="create-btn" @click="goToCreate">글 작성</button>
    </div>
    <BoardSearchBar :searchCondition="searchCondition" @search="searchArticles" />
    <BoardArticleTable
      :articles="paginatedArticles"
      :category="currentCategory"
      @detail="goToDetail"
    />
    <BoardPagination
      :currentPage="currentPage"
      :totalPages="totalPages"
      :pageNumbers="pageNumbers"
      @go="goToPage"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import api from '@/api/axiosInstance';

import BoardCategoryTabs from '@/components/board/BoardCategoryTabs.vue';
import BoardSearchBar from '@/components/board/BoardSearchBar.vue';
import BoardArticleTable from '@/components/board/BoardArticleTable.vue';
import BoardPagination from '@/components/board/BoardPagination.vue';

const route = useRoute();
const router = useRouter();
const articles = ref([]);
const currentCategory = ref('video');
const isLoading = ref(false);

const currentPage = ref(1);
const totalPages = ref(1);
const countPerPage = ref(10);
const naviSize = ref(10);

const searchCondition = reactive({
  category: 'video',
  key: 'title',
  word: '',
  tagLabel: '',
  tag: '',
  orderBy: 'created_at',
  orderByDir: 'desc',
  currentPage: 1,
  countPerPage: 10
});

const reverseTagMapping = {
  '상체': 'upper',
  '하체': 'lower',
  '코어': 'core',
  '유산소': 'cardio',
  '등': 'back',
  '가슴': 'chest',
  '어깨': 'shoulder',
  '팔': 'arm',
  '다리': 'leg',
  '전신': 'full',
  '복부': 'abs'
};

const paginatedArticles = computed(() => articles.value);

const pageNumbers = computed(() => {
  const result = [];
  const startNavi = Math.floor((currentPage.value - 1) / naviSize.value) * naviSize.value + 1;
  let endNavi = startNavi + naviSize.value - 1;
  if (endNavi > totalPages.value) endNavi = totalPages.value;
  for (let i = startNavi; i <= endNavi; i++) result.push(i);
  return result;
});

watch(
  () => searchCondition.tagLabel,
  (label) => {
    searchCondition.tag = reverseTagMapping[label] || '';
    router.replace({ query: { ...route.query, tag: label } });
    currentPage.value = 1;
    searchCondition.currentPage = 1;
    fetchArticles();
  }
);

const fetchArticles = async () => {
  try {
    isLoading.value = true;
    const params = {
      orderBy: searchCondition.orderBy,
      orderByDir: searchCondition.orderByDir,
      countPerPage: searchCondition.countPerPage,
      currentPage: currentPage.value
    };
    if (searchCondition.key && searchCondition.word) {
      params.key = searchCondition.key;
      params.word = searchCondition.word;
    }
    if (searchCondition.tag) {
      params.tag = searchCondition.tag;
    }
    const response = await api.get(
      `/api_article/get/category/${currentCategory.value}`,
      { params }
    );
  
    if (response.data) {
      articles.value = response.data.articles || [];
      totalPages.value = response.data.totalPages || 1;
    } else {
      articles.value = [];
      totalPages.value = 1;
    }
  } catch (error) {
    articles.value = [];
    totalPages.value = 1;
    if (error.response && error.response.status === 401) {
      alert('로그인이 필요합니다.');
      router.push('/login');
    }
  } finally {
    isLoading.value = false;
  }
};

const searchArticles = () => {
  currentPage.value = 1;
  fetchArticles();
};

watch(currentCategory, (category) => {
  searchCondition.category = category;
  currentPage.value = 1;
  fetchArticles();
});

const goToCreate = () => {
  router.push('/board/create');
};

const goToDetail = (articleId) => {
  router.push(`/board/detail/${articleId}`);
};

const goToPage = (page) => {
  if (page < 1 || page > totalPages.value || page === currentPage.value) return;
  currentPage.value = page;
  searchCondition.currentPage = page;
  router.replace({ query: { ...route.query, page: page.toString() } });
  fetchArticles();
};

onMounted(() => {
  let tagHandled = false;
  if (route.query.tag) {
    searchCondition.tagLabel = route.query.tag;
    tagHandled = true;
  }
  if (route.query.page) {
    const pageNum = parseInt(route.query.page);
    if (!isNaN(pageNum) && pageNum > 0) {
      currentPage.value = pageNum;
      searchCondition.currentPage = pageNum;
    }
  }
  if (!tagHandled) fetchArticles();
});
</script>

<style scoped>
@import url("https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css");
* { box-sizing: border-box; }
.board-container {
  font-family: "Pretendard Variable", "Pretendard", "Noto Sans KR", Arial, sans-serif;
  background: #f8f9fa;
  min-height: 100vh;
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 16px;
}
.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}
.create-btn {
  padding: 8px 20px;
  background: linear-gradient(135deg, #42b983, #369870);
  color: #fff;
  border: none;
  border-radius: 20px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(66,185,131,0.10);
  transition: background 0.2s, box-shadow 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}
.create-btn:hover {
  background: #369870;
  box-shadow: 0 4px 16px rgba(66,185,131,0.18);
}
@media (max-width: 900px) {
  .board-container { padding: 16px 4px; }
}
@media (max-width: 600px) {
  .board-header { flex-direction: column; gap: 10px; }
}
</style>