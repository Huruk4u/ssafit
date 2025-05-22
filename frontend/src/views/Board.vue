<template>
  <div class="board-container">
    <Header />

    <div class="board-header">
      <h2>게시판</h2>
      <div class="category-tabs">
        <button
          :class="{ active: currentCategory === 'video' }"
          @click="changeCategory('video')"
        >영상</button>
        <button
          :class="{ active: currentCategory === 'question' }"
          @click="changeCategory('question')"
        >질문</button>
        <button
          :class="{ active: currentCategory === 'info' }"
          @click="changeCategory('info')"
        >정보</button>
      </div>
      <button class="create-btn" @click="goToCreate">글 작성</button>
    </div>

    <div class="search-container">
      <div class="tag-filter">
        <label for="tag">부위:</label>
        <select id="tag" v-model="searchCondition.tagLabel">
          <option value="">전체</option>
          <option>상체</option>
          <option>하체</option>
          <option>코어</option>
          <option>유산소</option>
          <option>등</option>
          <option>가슴</option>
          <option>어깨</option>
          <option>팔</option>
          <option>다리</option>
          <option>전신</option>
          <option>복부</option>
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
          <tr
            v-for="article in paginatedArticles"
            :key="article.articleId"
            @click="goToDetail(article.articleId)"
          >
            <td>
            <template v-if="currentCategory === 'video'">
              <img
                :src="getThumbnailUrl(article.url)"
                alt="thumbnail"
                style="width: 120px; height: auto; object-fit: cover;"
              />
            </template>
            <template v-else>
              {{ article.articleId }}
            </template>
          </td>
            <td>{{ article.title }}</td>
            <td>{{ article.nickname }}</td>
            <td>{{ formatDate(article.createdAt) }}</td>
            <td>{{ article.viewCount }}</td>
            <td>{{ article.likeCount }}</td>
            <td>{{ article.commentCount }}</td>
          </tr>
          <tr v-if="paginatedArticles.length === 0">
            <td colspan="7" class="no-data">게시글이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 페이지네이션 추가 -->
    <div class="pagination">
      <button 
        class="pagination-control"
        :disabled="currentPage <= 1" 
        @click="goToPage(1)"
      >
        &laquo;&laquo;
      </button>
      <button 
        class="pagination-control"
        :disabled="currentPage <= 1" 
        @click="goToPage(currentPage - 1)"
      >
        &laquo;
      </button>

      <template v-for="page in pageNumbers" :key="page">
        <button 
          class="page-number" 
          :class="{ 'current-page': currentPage === page }"
          @click="goToPage(page)"
        >
          {{ page }}
        </button>
      </template>

      <button 
        class="pagination-control"
        :disabled="currentPage >= totalPages" 
        @click="goToPage(currentPage + 1)"
      >
        &raquo;
      </button>
      <button 
        class="pagination-control"
        :disabled="currentPage >= totalPages" 
        @click="goToPage(totalPages)"
      >
        &raquo;&raquo;
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import Header from '@/components/Header.vue';
import api from '@/api/axiosInstance';

const route = useRoute();
const router = useRouter();
const articles = ref([]);
const currentCategory = ref('video');
const isLoading = ref(false);

// 페이지네이션 관련 상태 추가
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

// 현재 페이지에 표시할 게시글 계산
const paginatedArticles = computed(() => {
  return articles.value;
});

// 화면에 표시할 페이지 번호 배열 계산
const pageNumbers = computed(() => {
  const result = [];
  
  // 시작 페이지와 끝 페이지 계산
  const startNavi = Math.floor((currentPage.value - 1) / naviSize.value) * naviSize.value + 1;
  let endNavi = startNavi + naviSize.value - 1;
  
  if (endNavi > totalPages.value) {
    endNavi = totalPages.value;
  }
  
  for (let i = startNavi; i <= endNavi; i++) {
    result.push(i);
  }
  
  return result;
});

watch(
  () => searchCondition.tagLabel,
  (label) => {
    searchCondition.tag = reverseTagMapping[label] || '';
    router.replace({ query: { ...route.query, tag: label } });
    goToPage(1); // 태그 변경 시 1페이지로 이동
  }
);

const getThumbnailUrl = (url) => {
  if (!url) return '';

  try {
    const youtubeRegex = /(?:youtube\.com\/watch\?v=|youtu\.be\/)([^&?/]+)/;
    const match = url.match(youtubeRegex);
    if (match && match[1]) {
      return `https://img.youtube.com/vi/${match[1]}/0.jpg`;
    }
  } catch (e) {
    console.warn('URL 파싱 실패:', e);
  }

  return url;
};

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
    
    // 백엔드에서 반환한 데이터 처리
    if (response.data) {
      articles.value = response.data.articles || [];
      totalPages.value = response.data.totalPages || 1;
    } else {
      articles.value = [];
      totalPages.value = 1;
    }
  } catch (error) {
    console.error('게시글 조회 오류:', error);
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
  currentPage.value = 1; // 검색 시 1페이지로 초기화
  fetchArticles();
};

const changeCategory = (category) => {
  currentCategory.value = category;
  searchCondition.category = category;
  currentPage.value = 1; // 카테고리 변경 시 1페이지로 초기화
  fetchArticles();
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(
    date.getMonth() + 1
  ).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

const goToCreate = () => {
  router.push('/board/create');
};

const goToDetail = (articleId) => {
  router.push(`/board/detail/${articleId}`);
};

// 페이지 이동 함수
const goToPage = (page) => {
  if (page < 1 || page > totalPages.value || page === currentPage.value) return;
  
  currentPage.value = page;
  searchCondition.currentPage = page;
  
  // URL에 페이지 정보 추가
  router.replace({ 
    query: { 
      ...route.query, 
      page: page.toString() 
    } 
  });
  
  fetchArticles();
};

onMounted(() => {
  // URL에서 초기 파라미터 가져오기
  if (route.query.tag) {
    searchCondition.tagLabel = route.query.tag;
  }
  
  if (route.query.page) {
    const pageNum = parseInt(route.query.page);
    if (!isNaN(pageNum) && pageNum > 0) {
      currentPage.value = pageNum;
      searchCondition.currentPage = pageNum;
    }
  }
  
  fetchArticles();
});
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
.category-tabs { display: flex; gap: 10px; }
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
.search-container { display: flex; gap: 20px; margin-bottom: 20px; }
.tag-filter,
.search-filter,
.sort-options { display: flex; align-items: center; gap: 8px; }
select,
input,
button { padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
.search-filter input { width: 200px; }
.article-list { margin-top: 10px; }
table { width: 100%; border-collapse: collapse; }
th,
td { padding: 12px; text-align: center; border-bottom: 1px solid #ddd; }
thead { background-color: #f2f2f2; }
tbody tr:hover { background-color: #f5f5f5; }
.no-data { text-align: center; padding: 20px; }

/* 페이지네이션 스타일 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  gap: 5px;
}

.pagination-control,
.page-number {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background-color: #fff;
  cursor: pointer;
  border-radius: 4px;
  min-width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination-control:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-number.current-page {
  background-color: #42b983;
  color: white;
  border-color: #42b983;
}

.pagination button:hover:not(:disabled):not(.current-page) {
  background-color: #f5f5f5;
}
</style>