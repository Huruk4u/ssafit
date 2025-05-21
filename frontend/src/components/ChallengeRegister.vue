<template>
  <div class="challenge-status">
    <h3>오늘의 챌린지</h3>

    <div v-if="isChallengeLoading" class="loading">
      <p>로딩 중...</p>
    </div>

    <div v-else>
      <!-- 완료했을 때 -->
      <div v-if="todayChallengeCompleted" class="challenge-completed">
        <div class="success-message">
          <h4>오늘의 챌린지를 완료했습니다! 🎉</h4>
          <p>연속 {{ currentStreak }}일 달성 중입니다.</p>
        </div>
      </div>

      <!-- 완료하지 않았을 때 -->
      <div v-else class="challenge-incomplete">
        <div class="challenge-prompt">
          <h4>오늘의 챌린지를 진행해 보세요!</h4>
          <p>인바디 사진을 등록하고 맞춤형 운동 추천을 받아보세요.</p>
          <p class="streak-info">
            현재 {{ currentStreak }}일 연속 달성 중입니다.
          </p>
        </div>

        <!-- 추천 운동 부위: 완료하지 않았을 때만 노출 -->
        <div class="recommended-tags">
          <h4>나의 추천 운동 부위</h4>
          <div class="tag-buttons">
            <button
              v-for="(tag, index) in recommendedTags"
              :key="index"
              @click="goToBoardWithTag(tag.value)"
              class="tag-button"
            >
              {{ tag.label }}
            </button>
          </div>
        </div>

        <div class="upload-section">
          <div class="file-input-container">
            <label for="inbody-upload" class="file-input-label">
              인바디 사진 선택
            </label>
            <input
              type="file"
              id="inbody-upload"
              @change="handleFileChange"
              accept="image/*"
              class="file-input"
            />
            <span v-if="selectedFile" class="file-name">
              {{ selectedFile.name }}
            </span>
          </div>

          <div class="preview-container" v-if="previewUrl">
            <img
              :src="previewUrl"
              alt="인바디 미리보기"
              class="image-preview"
            />
            <button @click="clearPreview" class="clear-preview">✕</button>
          </div>

          <button
            @click="uploadInbody"
            class="upload-button"
            :disabled="!selectedFile || isUploading"
          >
            {{ isUploading ? "업로드 중..." : "인바디 등록하기" }}
          </button>
        </div>
      </div>

      <!-- 최근 인바디 정보: 언제나 노출 -->
      <div v-if="latestInbody" class="inbody-info">
        <h4>최근 인바디 정보</h4>
        <div class="inbody-grid">
          <div class="inbody-item">
            <p class="inbody-label">BMI</p>
            <p class="inbody-value">{{ latestInbody.bmi }}</p>
          </div>
          <div class="inbody-item">
            <p class="inbody-label">체중</p>
            <p class="inbody-value">{{ latestInbody.weight }}kg</p>
          </div>
          <div class="inbody-item">
            <p class="inbody-label">골격근량</p>
            <p class="inbody-value">{{ latestInbody.muscleMass }}kg</p>
          </div>
          <div class="inbody-item">
            <p class="inbody-label">체지방량</p>
            <p class="inbody-value">{{ latestInbody.bodyFat }}kg</p>
          </div>
          <div class="inbody-item">
            <p class="inbody-label">체지방률</p>
            <p class="inbody-value">{{ latestInbody.bodyFatPercentage }}%</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "@/api/axiosInstance";

const user = ref(JSON.parse(localStorage.getItem("user") || "{}"));
const router = useRouter();

// 인바디 업로드
const uploadInbody = async () => {
  if (!selectedFile.value || isUploading.value) return;
  isUploading.value = true;
  try {
    const formData = new FormData();
    formData.append("file", selectedFile.value);
     const res = await api.post("/api_challenge/post/challenge", formData, {
      headers: { "Content-Type": "multipart/form-data" },
      timeout: 30000, 
    });
    if (res.status === 200) {
      todayChallengeCompleted.value = true;
      currentStreak.value = res.data.currentStreak;
      await fetchLatestInbody();
      alert("인바디 등록이 완료되었습니다!");
      if (res.data.newBadges?.length) {
        alert(
          `축하합니다! 새로운 뱃지를 획득했습니다: ${res.data.newBadges
            .map((b) => b.name)
            .join(", ")}`
        );
      }
    }
  } catch (err) {
    console.error("인바디 업로드 실패:", err);
    alert("인바디 등록에 실패했습니다. 다시 시도해주세요.");
  } finally {
    isUploading.value = false;
  }
};

// 태그 매핑 (영문->한글)
const tagMapping = {
  upper: "상체",
  lower: "하체",
  core: "코어",
  cardio: "유산소",
  back: "등",
  chest: "가슴",
  shoulder: "어깨",
  arm: "팔",
  leg: "다리",
  full: "전신",
  abs: "복부",
};

// 한글->영문 매핑 (역방향 매핑)
const reverseTagMapping = {
  "상체": "upper",
  "하체": "lower",
  "코어": "core",
  "유산소": "cardio",
  "등": "back",
  "가슴": "chest",
  "어깨": "shoulder",
  "팔": "arm",
  "다리": "leg",
  "전신": "full",
  "복부": "abs"
};

// 파일 선택 처리
const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (!file) return;
  selectedFile.value = file;
  const reader = new FileReader();
  reader.onload = (e) => (previewUrl.value = e.target.result);
  reader.readAsDataURL(file);
};

// 프리뷰 초기화
const clearPreview = () => {
  selectedFile.value = null;
  previewUrl.value = null;
  const fileInput = document.getElementById("inbody-upload");
  if (fileInput) fileInput.value = "";
};

// 최근 인바디 정보 조회
const fetchLatestInbody = async () => {
  if (!user.value.userId) return;
  try {
    const res = await api.get(`/api_inbody/latest/${user.value.userId}`);
    latestInbody.value = res.data;
  } catch (err) {
    console.error("인바디 정보 조회 실패:", err);
  }
};

// 파일을 업로드했을 때 미리보기
const selectedFile = ref(null);
const previewUrl = ref(null);
const isUploading = ref(false);
const isChallengeLoading = ref(true);
const todayChallengeCompleted = ref(false);
const recommendedTags = ref([]);
const latestInbody = ref(null);

const badges = ref([]);
const representBadge = ref(null);
const currentStreak = ref(null);
const longestStreak = ref(null);
const myArticles = ref([]);

// 오늘 날짜
const today = new Date().toISOString().split("T")[0];

onMounted(async () => {
  try {
    // 먼저 로컬 스토리지의 유저 정보를 최신 상태로 다시 읽어옵니다
    user.value = JSON.parse(localStorage.getItem("user") || "{}");
    console.log("로컬스토리지 유저 데이터:", user.value);
    
    const res = await api.get("/api_mypage/profile");
    console.log("프로필 API 응답:", res.data);
    const data = res.data;

    badges.value = data.badges ? data.badges : [];
    representBadge.value = data.representedBadge;
    currentStreak.value = data.challengeSummary?.currentStreak || 0;
    longestStreak.value = data.challengeSummary?.longestStreak || 0;

    console.log("현재 스트릭:", currentStreak.value);

    // 스트릭 달력으로 오늘 완료 여부
    todayChallengeCompleted.value =
      data.challengeSummary?.streakCalendar?.[today] === true;

    // 추천 운동 부위 설정 (여러 소스에서 시도)
    const setupRecommendedTags = () => {
      // 유저 데이터에서 운동 부위를 가져오기 위한 다양한 소스 확인
      const sources = [
        // 1. 프로필 API 응답 데이터에서 직접
        {
          first: data.firstExercise,
          second: data.secondExercise,
          third: data.thirdExercise
        },
        // 2. API로 가져온 user 객체에서
        {
          first: data.user?.firstExercise,
          second: data.user?.secondExercise,
          third: data.user?.thirdExercise
        },
        // 3. localStorage의 user 객체에서
        {
          first: user.value.firstExercise,
          second: user.value.secondExercise,
          third: user.value.thirdExercise
        }
      ];

      // 유효한 소스 찾기
      for (const source of sources) {
        if (source.first && source.second && source.third) {
          console.log("유효한 태그 소스 발견:", source);
          recommendedTags.value = [
            {
              value: reverseTagMapping[source.first] || source.first,
              label: source.first
            },
            {
              value: reverseTagMapping[source.second] || source.second,
              label: source.second
            },
            {
              value: reverseTagMapping[source.third] || source.third,
              label: source.third
            }
          ];
          break; // 유효한 소스를 찾으면 루프 종료
        }
      }
    };

    // 태그 설정 함수 호출
    setupRecommendedTags();
    
    // 태그가 성공적으로 설정되었는지 확인
    if (recommendedTags.value.length === 0) {
      console.warn("추천 태그를 설정할 수 없습니다. 유저 데이터 확인 필요");
      // 하드코딩된 기본값으로 설정 (최후의 수단)
      recommendedTags.value = [
        { value: "abs", label: "복부" },
        { value: "lower", label: "하체" },
        { value: "cardio", label: "유산소" }
      ];
    }
    
    console.log("최종 추천 태그:", recommendedTags.value);

    // 내 글 목록
    try {
      const articlesRes = await api.get(
        `/api_article/get/user_id/${user.value.userId}`
      );
      myArticles.value = articlesRes.data;
    } catch (err) {
      console.error("내 글 목록 조회 실패:", err);
    }
  } catch (err) {
    console.error("유저 정보 불러오기 실패:", err);
    // 오류 발생 시에도 태그를 설정합니다 (최후의 수단)
    if (recommendedTags.value.length === 0 && user.value.firstExercise) {
      recommendedTags.value = [
        {
          value: reverseTagMapping[user.value.firstExercise] || "abs",
          label: user.value.firstExercise || "복부"
        },
        {
          value: reverseTagMapping[user.value.secondExercise] || "lower",
          label: user.value.secondExercise || "하체"
        },
        {
          value: reverseTagMapping[user.value.thirdExercise] || "cardio",
          label: user.value.thirdExercise || "유산소"
        }
      ];
    }
  } finally {
    isChallengeLoading.value = false;
    // 아직 태그가 설정되지 않았다면 기본값 적용
    if (recommendedTags.value.length === 0) {
      console.warn("마지막 수단: 기본 태그 적용");
      recommendedTags.value = [
        { value: "abs", label: "복부" },
        { value: "lower", label: "하체" },
        { value: "cardio", label: "유산소" }
      ];
    }
    // 최근 인바디 정보 조회
    fetchLatestInbody();
  }
});

// 태그로 게시판 이동
const goToBoardWithTag = (tag) => {
  router.push({ path: "/board", query: { tag } });
};
</script>

<style scoped>
.challenge-status {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

h3 {
  margin-top: 0;
  color: #333;
  border-bottom: 2px solid #42b983;
  padding-bottom: 8px;
  margin-bottom: 20px;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.challenge-completed,
.challenge-incomplete {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.success-message {
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 6px;
  padding: 15px;
  color: #155724;
}

.challenge-prompt {
  background-color: #cce5ff;
  border: 1px solid #b8daff;
  border-radius: 6px;
  padding: 15px;
  color: #004085;
}

.streak-info {
  font-weight: bold;
  margin-top: 10px;
}

.recommended-tags {
  margin-top: 10px;
}

.tag-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.tag-button {
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 8px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.tag-button:hover {
  background-color: #3a9d70;
}

.inbody-info {
  margin-top: 20px;
}

.inbody-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
  margin-top: 10px;
}

.inbody-item {
  background-color: #f1f1f1;
  border-radius: 6px;
  padding: 12px;
  text-align: center;
}

.inbody-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.inbody-value {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.upload-section {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.file-input-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.file-input {
  display: none;
}

.file-input-label {
  background-color: #6c757d;
  color: white;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.file-input-label:hover {
  background-color: #5a6268;
}

.file-name {
  color: #495057;
  font-size: 14px;
}

.preview-container {
  position: relative;
  width: 100%;
  max-width: 300px;
  margin: 10px 0;
}

.image-preview {
  width: 100%;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.clear-preview {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.upload-button {
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 10px 20px;
  cursor: pointer;
  transition: background-color 0.2s;
  font-weight: bold;
}

.upload-button:hover:not(:disabled) {
  background-color: #0069d9;
}

.upload-button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .inbody-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>