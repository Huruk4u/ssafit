<template>
  <div class="board-create-container">
    <div class="form-container">
      <h2>게시글 작성</h2>

      <div class="form-group">
        <label for="category">카테고리</label>
        <select id="category" v-model="article.category" required>
          <option
            v-for="opt in filteredCategoryOptions"
            :key="opt.value"
            :value="opt.value"
          >
            {{ opt.label }}
          </option>
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
          <option value="back">등</option>
          <option value="chest">가슴</option>
          <option value="shoulder">어깨</option>
          <option value="arm">팔</option>
          <option value="leg">다리</option>
          <option value="full">전신</option>
          <option value="abs">복부</option>
        </select>
      </div>

      <div class="form-group" v-if="article.category === 'video'">
        <label for="url">유튜브 URL</label>
        <input
          type="text"
          id="url"
          v-model="article.url"
          placeholder="유튜브 영상 URL을 입력하세요"
          required
        />
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
        <button
          class="submit-btn"
          @click="submitArticle"
          :disabled="isSubmitting"
        >
          {{ isSubmitting ? "등록 중..." : "등록하기" }}
        </button>
      </div>

      <!-- 디버깅용 상태 표시 -->
      <div v-if="debugMode" class="debug-info">
        <p>API 요청 URL: {{ apiUrl }}</p>
        <p>토큰 존재: {{ !!getToken() }}</p>
        <p>전송 상태: {{ submitStatus }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import Header from "../components/Header.vue";
import axios from "axios";

const user = ref(JSON.parse(localStorage.getItem("user") || "null"));

const router = useRouter();
const isSubmitting = ref(false);
const debugMode = ref(false); // 디버깅 모드 토글
const submitStatus = ref("준비됨");
const apiUrl = import.meta.env.VITE_API_BASE_URL || ""; // .env에서 API 기본 URL 가져오기

const categoryOptions = [
  { value: "video", label: "영상", adminOnly: true },
  { value: "question", label: "질문" },
  { value: "info", label: "정보" },
];

const filteredCategoryOptions = computed(() => {
  return categoryOptions.filter(
    (option) => !option.adminOnly || user.value?.role === "ROLE_ADMIN"
  );
});

const article = reactive({
  category: user.value?.role === "ROLE_ADMIN" ? "video" : "question", // 기본값
  title: "",
  content: "",
  tag: "",
});

// 토큰 가져오는 함수 (재사용을 위해 분리)
const getToken = () => {
  return localStorage.getItem("token");
};

// 컴포넌트 마운트 시 토큰 확인
onMounted(() => {
  const token = getToken();
  if (!token) {
    alert("로그인이 필요합니다.");
    router.push("/login");
  }
});

const submitArticle = async () => {
  // 제목 필수 검증
  if (!article.title.trim()) {
    alert("제목은 필수 입력 항목입니다.");
    return;
  }

  if (article.category === "video" && !article.url.trim()) {
    alert("영상 URL을 입력해주세요.");
    return;
  }

  try {
    isSubmitting.value = true;
    submitStatus.value = "요청 시작";

    // 토큰 가져오기
    const token = getToken();
    if (!token) {
      alert("로그인이 필요합니다.");
      router.push("/login");
      return;
    }

    // 요청 전 로그
    console.log("API 요청 시작:", article);
    submitStatus.value = "요청 전송 중";
    const endpointUrl = "http://localhost:8080/api_article/post/write";

    const response = await axios.post(endpointUrl, article, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    console.log("API 응답:", response.data);
    submitStatus.value = "응답 수신: " + JSON.stringify(response.data);

    if (response.data && response.data.result === 1) {
      // 새로운 배지 획득 여부 확인
      if (response.data.newBadges && response.data.newBadges.length > 0) {
        // 배지 획득 메시지 표시
        alert(response.data.message || "새로운 배지를 획득하셨습니다!");
      } else {
        alert("게시글이 성공적으로 등록되었습니다.");
      }

      // 게시판 목록 페이지로 이동
      router.push("/board");
    } else {
      alert("게시글 등록에 실패했습니다.");
    }
  } catch (error) {
    console.error("게시글 등록 중 오류 발생:", error);
    submitStatus.value =
      "오류 발생: " + (error.message || JSON.stringify(error));

    // 오류 응답 상태 코드에 따른 처리
    if (error.response) {
      console.error("오류 응답:", error.response);

      if (error.response.status === 401) {
        alert("로그인이 필요하거나 세션이 만료되었습니다.");
        router.push("/login");
      } else if (error.response.status === 400) {
        alert(
          "잘못된 요청입니다: " +
            (error.response.data || "요청 형식을 확인해주세요")
        );
      } else if (error.response.status === 500) {
        alert("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
      } else {
        alert(
          `게시글 등록 중 오류가 발생했습니다(${error.response.status}). 다시 시도해주세요.`
        );
      }
    } else if (error.request) {
      // 요청은 보냈지만 응답을 받지 못한 경우
      alert("서버 응답이 없습니다. 네트워크 연결을 확인해주세요.");
    } else {
      alert("게시글 등록 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
  } finally {
    isSubmitting.value = false;
  }
};

const cancel = () => {
  const confirmLeave = confirm(
    "작성 중인 내용은 저장되지 않습니다. 정말 취소하시겠습니까?"
  );
  if (confirmLeave) {
    router.push("/board");
  }
};

// 디버그 모드 토글 (개발 중에만 사용)
const toggleDebug = () => {
  debugMode.value = !debugMode.value;
};
</script>

<style scoped>
@import url("https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css");

.board-create-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 32px 0 32px 0;
  font-family: "Pretendard Variable", "Pretendard", "Noto Sans KR", Arial, sans-serif;
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

/* 디버깅 정보 스타일 */
.debug-info {
  margin-top: 20px;
  padding: 10px;
  background-color: #f8f8f8;
  border: 1.5px solid #bcd0ee;
  border-radius: 8px;
  font-family: monospace;
  font-size: 12px;
}

@media (max-width: 700px) {
  .board-create-container {
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