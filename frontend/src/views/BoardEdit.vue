<template>
  <div class="board-edit-container">
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
          <label for="tag">태그 (부위)</label>
          <select id="tag" v-model="article.tag">
            <option value="">선택 안함</option>
            <option value="upper">상체</option>
            <option value="lower">하체</option>
            <option value="core">코어</option>
            <option value="cardio">유산소</option>
            <option value="back">등</option>
            <option value="chest">가슴</option>
            <option value="shoulder">어깨</option>
            <option value="arm">팔</option>
            <option value="leg">다리</option>
            <option value="full">전신</option>
            <option value="abs">복부</option>
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
          <label for="url">영상 URL</label>
          <input
            type="url"
            id="url"
            v-model="article.url"
            placeholder="영상 URL을 입력하세요"
            :disabled="article.category !== 'video'"
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
          <button
            class="submit-btn"
            @click="updateArticle"
            :disabled="isSubmitting"
          >
            {{ isSubmitting ? "수정 중..." : "수정하기" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import api from "@/api/axiosInstance";

const router = useRouter();
const route = useRoute();
const articleId = route.params.articleId;

const loading = ref(true);
const error = ref(null);
const isSubmitting = ref(false);

const article = reactive({
  category: "",
  title: "",
  content: "",
  tag: "",
  url: "",
});

const fetchArticle = async () => {
  try {
    loading.value = true;
    const response = await api.get(`/api_article/get/article_id/${articleId}`);
    const fetchedArticle = response.data;
    article.category = fetchedArticle.category;
    article.title = fetchedArticle.title;
    article.content = fetchedArticle.content;
    article.tag = fetchedArticle.tag || "";
    article.url = fetchedArticle.url || "";
  } catch (err) {
    const message = err.response
      ? err.response.data.message || "게시글을 불러오는 중 오류가 발생했습니다."
      : "네트워크 오류가 발생했습니다.";
    alert(message);
  } finally {
    loading.value = false;
  }
};

const updateArticle = async () => {
  if (!article.title.trim()) {
    alert("제목은 필수 입력 항목입니다.");
    return;
  }
  if (article.category === "video" && !article.url.trim()) {
    alert("영상 게시판은 URL을 입력해야 합니다.");
    return;
  }
  try {
    isSubmitting.value = true;
    const token = localStorage.getItem("token");
    if (!token) {
      alert("로그인이 필요합니다.");
      router.push("/login");
      return;
    }
    const response = await api.put(
      `/api_article/put/modify/article_id/${articleId}`,
      article
    );
    if (response.data === 1) {
      alert("게시글이 성공적으로 수정되었습니다.");
      router.push(`/board/detail/${articleId}`);
    } else {
      alert("게시글 수정에 실패했습니다.");
    }
  } catch (err) {
    const message = err.response
      ? err.response.data.message || "게시글 수정 중 오류가 발생했습니다."
      : "네트워크 오류가 발생했습니다.";
    alert(message);
    
  } finally {
    isSubmitting.value = false;
  }
};

const cancel = () => {
  const confirmLeave = confirm(
    "수정 중인 내용은 저장되지 않습니다. 정말 취소하시겠습니까?"
  );
  if (confirmLeave) {
    router.push(`/board/detail/${articleId}`);
  }
};

const goToBoard = () => {
  router.push("/board");
};

onMounted(() => {
  fetchArticle();
});
</script>

<style scoped>
@import url("https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css");

.board-edit-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 32px 0 32px 0;
  font-family: "Pretendard Variable", "Pretendard", "Noto Sans KR", Arial,
    sans-serif;
}

.form-container {
  background-color: #fff;
  padding: 32px 28px 28px 28px;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(66, 185, 131, 0.09);
  border: 2px solid #e0e7ef; /* 테두리 추가 */
}

h2 {
  margin-bottom: 28px;
  color: #42b983;
  font-size: 1.6rem;
  font-weight: 800;
  letter-spacing: -1px;
  text-align: center;
  font-family: inherit;
}

.loading,
.error {
  text-align: center;
  padding: 40px 0;
  font-family: inherit;
}

.error p {
  color: #e53935;
  margin-bottom: 20px;
  font-family: inherit;
}

.error button {
  padding: 10px 24px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 22px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  font-family: inherit;
  transition: background 0.2s;
}
.error button:hover {
  background: #2e8c6a;
}

.form-group {
  margin-bottom: 22px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #2563eb;
  font-size: 1.05rem;
  font-family: inherit;
}

input,
select,
textarea {
  width: 100%;
  padding: 12px;
  border: 1.5px solid #bcd0ee; /* 입력창 테두리 색상 강조 */
  border-radius: 8px;
  font-size: 1.05rem;
  font-family: inherit;
  background: #f8f9fa;
  transition: border 0.2s;
  color: #222;
}

input:focus,
select:focus,
textarea:focus {
  border-color: #42b983;
  outline: none;
}

input:disabled {
  background-color: #f1f3f5;
  cursor: not-allowed;
}

textarea {
  resize: vertical;
  min-height: 180px;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 28px;
}

.cancel-btn,
.submit-btn {
  padding: 10px 28px;
  border: none;
  border-radius: 22px;
  cursor: pointer;
  font-size: 1.08rem;
  font-weight: 700;
  font-family: inherit;
  transition: background 0.2s;
  border: 1.5px solid #bcd0ee; /* 버튼 테두리 추가 */
}

.cancel-btn {
  background-color: #e9ecef;
  color: #2563eb;
}
.cancel-btn:hover {
  background-color: #bcd0ee;
}

.submit-btn {
  background: linear-gradient(90deg, #42b983 60%, #5eead4 100%);
  color: white;
  border: 1.5px solid #42b983;
}
.submit-btn:hover:not(:disabled) {
  background: linear-gradient(90deg, #2e8c6a 60%, #42b983 100%);
}
.submit-btn:disabled {
  background-color: #a8e0c9;
  cursor: not-allowed;
  border: 1.5px solid #a8e0c9;
}

@media (max-width: 700px) {
  .board-edit-container {
    padding: 12px 0 12px 0;
  }
  .form-container {
    padding: 18px 6px 18px 6px;
    border-radius: 10px;
  }
  h2 {
    font-size: 1.2rem;
    margin-bottom: 18px;
  }
  .button-group {
    gap: 8px;
    margin-top: 16px;
  }
}
</style>