<template>
  <div class="board-container">
    <div class="board-header">
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
            <th>
              <template v-if="currentCategory === 'video'">썸네일</template>
              <template v-else></template>
            </th>
            <th>제목</th>
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
            </td>
            <td>{{ article.title }}</td>
            <td>{{ article.nickname }}</td>
            <td>
              <svg viewBox="0 0 24 24" width="14" height="14" style="vertical-align:middle; margin-right:2px;">
                <path fill="currentColor" d="M19,3H18V1H16V3H8V1H6V3H5A2,2 0 0,0 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V5A2,2 0 0,0 19,3M19,19H5V8H19V19Z"/>
              </svg>
              {{ formatDate(article.createdAt) }}
            </td>
            <td>
              <svg viewBox="0 0 24 24" width="14" height="14" style="vertical-align:middle; margin-right:2px;">
                <path fill="currentColor" d="M12,4.5C7,4.5 2.73,7.61 1,12C2.73,16.39 7,19.5 12,19.5C17.5,19.5 21.27,16.39 23,12C21.27,7.61 17.5,4.5 12,4.5Z"/>
              </svg>
              {{ article.viewCount }}
            </td>
            <td>
              <svg viewBox="0 0 24 24" width="14" height="14" style="vertical-align:middle; margin-right:2px;">
                <path fill="currentColor" d="M5,9V21H1V9H5M9,21A2,2 0 0,1 7,19V9C7,8.45 7.22,7.95 7.59,7.59L14.17,1L15.23,2.06C15.5,2.33 15.67,2.7 15.67,3.11L15.64,3.43L14.69,8H21C22.11,8 23,8.9 23,10V12C23,12.26 22.95,12.5 22.86,12.73L19.84,19.78C19.54,20.5 18.83,21 18,21H9M9,19H18.03L21,12V10H12.21L13.34,4.68L9,9.03V19Z"/>
              </svg>
              {{ article.likeCount }}
            </td>
            <td>
              <svg viewBox="0 0 24 24" width="14" height="14" style="vertical-align:middle; margin-right:2px;">
                <path fill="currentColor" d="M9,22A1,1 0 0,1 8,21V18H4A2,2 0 0,1 2,16V4C2,2.89 2.9,2 4,2H20A2,2 0 0,1 22,4V16A2,2 0 0,1 20,18H13.9L10.2,21.71C10,21.9 9.75,22 9.5,22H9Z"/>
              </svg>
              {{ article.commentCount }}
            </td>
          </tr>
          <tr v-if="paginatedArticles.length === 0">
            <td :colspan="currentCategory === 'video' ? 7 : 6" class="no-data">게시글이 없습니다.</td>
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
    currentPage.value = 1;
    searchCondition.currentPage = 1;
    fetchArticles();
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

  // tagLabel이 세팅되면 watch가 fetchArticles를 호출하므로 여기서 중복 호출하지 않음
  if (!tagHandled) {
    fetchArticles();
  }
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

.category-tabs {
  display: flex;
  gap: 10px;
}

.category-tabs button {
  padding: 8px 20px;
  border: none;
  background: #fff;
  color: #42b983;
  font-weight: 600;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(66,185,131,0.08);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 6px;
}

.category-tabs button.active {
  background: linear-gradient(135deg, #42b983, #369870);
  color: #fff;
  box-shadow: 0 4px 16px rgba(66,185,131,0.15);
}

.category-tabs button:hover:not(.active) {
  background: #f2f2f2;
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

.search-container {
  display: flex;
  gap: 18px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.tag-filter,
.search-filter,
.sort-options {
  display: flex;
  align-items: center;
  gap: 8px;
}

select,
input[type="text"] {
  padding: 8px 12px;
  border: 1.5px solid #e9ecef;
  border-radius: 12px;
  font-size: 1rem;
  background: #fff;
  transition: border 0.2s;
}

select:focus,
input[type="text"]:focus {
  border-color: #42b983;
  outline: none;
}

.search-filter input {
  width: 180px;
}

.search-filter button {
  padding: 8px 18px;
  background: linear-gradient(135deg, #42b983, #369870);
  color: #fff;
  border: none;
  border-radius: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
  margin-left: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.search-filter button:hover {
  background: #369870;
}

.article-list {
  margin-top: 10px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.07);
  overflow: hidden;
}

table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}

th, td {
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
  box-shadow: 0 2px 8px rgba(66,185,131,0.10);
  background: #f8f9fa;
}

.no-data {
  text-align: center;
  padding: 32px 0;
  color: #adb5bd;
  font-size: 1.1rem;
  background: #f8f9fa;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  gap: 5px;
}

.pagination-control,
.page-number {
  padding: 8px 14px;
  border: 1.5px solid #e9ecef;
  background: #fff;
  cursor: pointer;
  border-radius: 12px;
  min-width: 40px;
  font-size: 1rem;
  color: #42b983;
  font-weight: 600;
  transition: background 0.2s, color 0.2s, border 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination-control:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-number.current-page {
  background: linear-gradient(135deg, #42b983, #369870);
  color: #fff;
  border-color: #42b983;
}

.pagination button:hover:not(:disabled):not(.current-page) {
  background: #f5f5f5;
  color: #369870;
}

@media (max-width: 900px) {
  .board-container { padding: 16px 4px; }
  .article-list { border-radius: 10px; }
  th, td { padding: 10px 4px; font-size: 0.95rem; }
}

@media (max-width: 600px) {
  .board-header { flex-direction: column; gap: 10px; }
  .search-container { flex-direction: column; gap: 10px; }
  .article-list { margin-top: 4px; }
  th, td { padding: 8px 2px; font-size: 0.9rem; }
}
</style>