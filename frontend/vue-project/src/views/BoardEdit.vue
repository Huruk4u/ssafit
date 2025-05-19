<template>
  <div class="board-edit-container">
    <Header />
    
    <div class="form-container">
      <h2>게시글 수정</h2>
      
      <div v-if="loading" class="loading">
        <p>게시글을 불러오는 중...</p>
      </div>
      
      <div v-else-if="error" class="error">
        <p>{{ error }}</p>
        <button @click="goToBoard">게시판으로 돌아가기</button>
      </div>
      
      <div v-else class="edit-form">
        <div class="form-group">
          <label for="category">카테고리</label>
          <select id="category" v-model="article.category" required>
            <option value="video">영상</option>
            <option value="question">질문</option>
            <option value="info">정보</option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="tag">태그 (부위)</label>
          <select id="tag" v-model="article.tag">
            <option value="">선택 안함</option>
            <option value="upper">상체</option>
            <option value="lower">하체</option>
            <option value="core">코어</option>
            <option value="cardio">유산소</option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="title">제목</label>
          <input 
            type="text" 
            id="title" 
            v-model="article.title" 
            placeholder="제목을 입력하세요" 
            required
          />
        </div>
        
        <div class="form-group">
          <label for="content">내용</label>
          <textarea 
            id="content" 
            v-model="article.content" 
            placeholder="내용을 입력하세요" 
            rows="10"
          ></textarea>
        </div>
        
        <div class="button-group">
          <button class="cancel-btn" @click="cancel">취소</button>
          <button class="submit-btn" @click="updateArticle" :disabled="isSubmitting">
            {{ isSubmitting ? '수정 중...' : '수정하기' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import Header from '../components/Header.vue';
import api from '@/api/axiosInstance';

const router = useRouter();
const route = useRoute();
const articleId = route.params.articleId;

const loading = ref(true);
const error = ref(null);
const isSubmitting = ref(false);

const article = reactive({
  category: '',
  title: '',
  content: '',
  tag: ''
});

// 게시글 정보 가져오기
const fetchArticle = async () => {
  try {
    loading.value = true;
    
    const response = await api.get(`/api_article/get/article_id/${articleId}`);
    const fetchedArticle = response.data;
    
    // 응답 데이터를 article 객체에 설정
    article.category = fetchedArticle.category;
    article.title = fetchedArticle.title;
    article.content = fetchedArticle.content;
    article.tag = fetchedArticle.tag || '';
    
  } catch (err) {
    console.error('게시글을 불러오는데 실패했습니다:', err);
    
    if (err.response && err.response.status === 404) {
      error.value = '게시글을 찾을 수 없습니다.';
    } else if (err.response && err.response.status === 403) {
      error.value = '수정 권한이 없습니다.';
    } else {
      error.value = '게시글을 불러오는데 오류가 발생했습니다.';
    }
  } finally {
    loading.value = false;
  }
};

// 게시글 수정
const updateArticle = async () => {
  // 제목 필수 검증
  if (!article.title.trim()) {
    alert('제목은 필수 입력 항목입니다.');
    return;
  }
  
  try {
    isSubmitting.value = true;
    
    // 토큰 가져오기
    const token = localStorage.getItem('token');
    if (!token) {
      alert('로그인이 필요합니다.');
      router.push('/login');
      return;
    }
    
    const response = await api.put(`/api_article/put/modify/article_id/${articleId}`, article);
    
    if (response.data === 1) {
      alert('게시글이 성공적으로 수정되었습니다.');
      router.push(`/board/detail/${articleId}`);
    } else {
      alert('게시글 수정에 실패했습니다.');
    }
  } catch (err) {
    console.error('게시글 수정 중 오류 발생:', err);
    
    if (err.response && err.response.status === 401) {
      alert('로그인이 필요하거나 세션이 만료되었습니다.');
      router.push('/login');
    } else if (err.response && err.response.status === 403) {
      alert('수정 권한이 없습니다.');
      router.push('/board');
    } else {
      alert('게시글 수정 중 오류가 발생했습니다.');
    }
  } finally {
    isSubmitting.value = false;
  }
};

const cancel = () => {
  const confirmLeave = confirm('수정 중인 내용은 저장되지 않습니다. 정말 취소하시겠습니까?');
  if (confirmLeave) {
    router.push(`/board/detail/${articleId}`);
  }
};

const goToBoard = () => {
  router.push('/board');
};

onMounted(() => {
  fetchArticle();
});
</script>

<style scoped>
.board-edit-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.form-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

.loading, .error {
  text-align: center;
  padding: 40px 0;
}

.error p {
  color: #e53935;
  margin-bottom: 20px;
}

.error button {
  padding: 10px 20px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
}

input, select, textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

textarea {
  resize: vertical;
  min-height: 200px;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn, .submit-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
}

.cancel-btn {
  background-color: #f2f2f2;
  color: #333;
}

.submit-btn {
  background-color: #42b983;
  color: white;
}

.submit-btn:disabled {
  background-color: #a8e0c9;
  cursor: not-allowed;
}
</style>