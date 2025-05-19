<template>
  <div class="board-container">
    <Header />
    
    <div class="board-header">
      <h2>게시판</h2>
      <div class="category-tabs">
        <button 
          :class="{ active: currentCategory === 'video' }" 
          @click="changeCategory('video')">영상</button>
        <button 
          :class="{ active: currentCategory === 'question' }" 
          @click="changeCategory('question')">질문</button>
        <button 
          :class="{ active: currentCategory === 'info' }" 
          @click="changeCategory('info')">정보</button>
      </div>
      <button class="create-btn" @click="goToCreate">글 작성</button>
    </div>

    <!-- 검색창 (탭 바로 아래) -->
    <div class="search-container">
      <div class="tag-filter">
        <label for="tag">부위:</label>
        <select id="tag" v-model="searchCondition.tag">
          <option value="">전체</option>
          <option value="upper">상체</option>
          <option value="lower">하체</option>
          <option value="core">코어</option>
          <option value="cardio">유산소</option>
        </select>
      </div>
      
      <div class="search-filter">
        <select v-model="searchCondition.key">
          <option value="title">제목</option>
          <option value="content">내용</option>
          <option value="nickname">작성자</option>
        </select>
        <input 
          type="text" 
          v-model="searchCondition.word" 
          placeholder="검색어를 입력하세요" 
          @keyup.enter="searchArticles"
        />
        <button @click="searchArticles">검색</button>
      </div>
      
      <div class="sort-options">
        <select v-model="searchCondition.orderBy" @change="searchArticles">
          <option value="created_at">작성일</option>
          <option value="view_count">조회수</option>
          <option value="like_count">좋아요수</option>
        </select>
        <select v-model="searchCondition.orderByDir" @change="searchArticles">
          <option value="desc">내림차순</option>
          <option value="asc">오름차순</option>
        </select>
      </div>
    </div>
    
    <div class="article-list">
      <table>
        <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            <th>조회수</th>
            <th>좋아요</th>
            <th>댓글수</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="article in articles" :key="article.articleId" @click="goToDetail(article.articleId)">
            <td>{{ article.articleId }}</td>
            <td>{{ article.title }}</td>
            <td>{{ article.nickname }}</td>
            <td>{{ formatDate(article.createdAt) }}</td>
            <td>{{ article.viewCount }}</td>
            <td>{{ article.likeCount }}</td>
            <td>{{ article.commentCount }}</td>
          </tr>
          <tr v-if="articles.length === 0">
            <td colspan="7" class="no-data">게시글이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Header from '../components/Header.vue';
import api from '@/api/axiosInstance'

const router = useRouter();
const articles = ref([]);
const currentCategory = ref('video');
const isLoading = ref(false);

const searchCondition = reactive({
  category: 'video',
  key: '',
  word: '',
  tag: '',
  orderBy: 'created_at',
  orderByDir: 'desc',
});

const fetchArticles = async () => {
  try {
    isLoading.value = true;
    const params = {
      orderBy: searchCondition.orderBy,
      orderByDir: searchCondition.orderByDir,
      countPerPage: 1000  
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
    console.log('RAW RESPONSE:', response.data);

    // 응답 데이터 처리
    articles.value = response.data.articles || response.data || [];
  } catch (error) {
    console.error('게시글 조회 오류:', error);
    articles.value = [];
    if (error.response && error.response.status === 401) {
      alert('로그인이 필요합니다.');
      router.push('/login');
    }
  } finally {
    isLoading.value = false;
  }
};

const searchArticles = () => {
  fetchArticles();
};

const changeCategory = (category) => {
  currentCategory.value = category;
  searchCondition.category = category; 
  fetchArticles();
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

const goToCreate = () => {
  router.push('/board/create');
};

const goToDetail = (articleId) => {
  router.push(`/board/detail/${articleId}`);
};

onMounted(fetchArticles);
</script>

<style scoped>
.board-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.category-tabs {
  display: flex;
  gap: 10px;
}

.category-tabs button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background-color: #f8f8f8;
  cursor: pointer;
  border-radius: 4px;
}

.category-tabs button.active {
  background-color: #42b983;
  color: white;
  border-color: #42b983;
}

.create-btn {
  padding: 8px 16px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.search-container {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.tag-filter,
.search-filter,
.sort-options {
  display: flex;
  align-items: center;
  gap: 8px;
}

select, input, button {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-filter input {
  width: 200px;
}

.article-list {
  margin-top: 10px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px;
  text-align: center;
  border-bottom: 1px solid #ddd;
}

thead {
  background-color: #f2f2f2;
}

tbody tr:hover {
  background-color: #f5f5f5;
}

.no-data {
  text-align: center;
  padding: 20px;
}
</style>