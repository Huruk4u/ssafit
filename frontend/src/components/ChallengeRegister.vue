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
      
      <!-- 추천 운동 부위 -->
        <div class="recommended-tags">
          <h4>나의 추천 운동 부위</h4>
          <div class="tag-buttons">
             <button
                v-for="(tag, index) in recommendedTags"
                :key="index"
                @click="goToBoardWithTag(tag)"
                class="tag-button"
              >
                #{{ tag.label }}
              </button>
          </div>
        </div>
      <!-- 최근 인바디 정보 -->
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

const selectedFile = ref(null);
const previewUrl = ref(null);
const isUploading = ref(false);
const isChallengeLoading = ref(true);
const todayChallengeCompleted = ref(false);
const recommendedTags = ref([]);
const latestInbody = ref(null);
const badges = ref([]);
const representBadge = ref(null);
const currentStreak = ref(0);
const longestStreak = ref(0);
const myArticles = ref([]);

const today = new Date().toISOString().split("T")[0];

const uploadInbody = async () => {
  if (!selectedFile.value || isUploading.value) return;
  isUploading.value = true;
  try {
    const formData = new FormData();
    formData.append("file", selectedFile.value);
    const res = await api.post("/api_challenge/post/challenge", formData, {
      headers: { "Content-Type": "multipart/form-data" },
      timeout: 30000
    });
    if (res.status === 200) {
      todayChallengeCompleted.value = true;
      currentStreak.value = res.data.currentStreak;
      if (Array.isArray(res.data.recommendedParts)) {
        recommendedTags.value = res.data.recommendedParts.map(p => ({ value: p, label: p }));
      }
      await fetchLatestInbody();
      alert("인바디 등록이 완료되었습니다!");
      if (res.data.newBadges?.length) {
        alert(
          `축하합니다! 새로운 뱃지를 획득했습니다: ${res.data.newBadges
            .map(b => b.name)
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

const handleFileChange = event => {
  const file = event.target.files[0];
  if (!file) return;
  selectedFile.value = file;
  const reader = new FileReader();
  reader.onload = e => (previewUrl.value = e.target.result);
  reader.readAsDataURL(file);
};

const clearPreview = () => {
  selectedFile.value = null;
  previewUrl.value = null;
  const fileInput = document.getElementById("inbody-upload");
  if (fileInput) fileInput.value = "";
};

const fetchLatestInbody = async () => {
  if (!user.value.userId) return;
  try {
    const res = await api.get(`/api_inbody/latest/${user.value.userId}`);
    latestInbody.value = res.data;
  } catch (err) {
    console.error("인바디 정보 조회 실패:", err);
  }
};

onMounted(async () => {
  try {
    user.value = JSON.parse(localStorage.getItem("user") || "{}");
    const res = await api.get("/api_mypage/profile");
    const data = res.data;

    badges.value = data.badges || [];
    representBadge.value = data.representedBadge;
    currentStreak.value = data.challengeSummary?.currentStreak || 0;
    longestStreak.value = data.challengeSummary?.longestStreak || 0;
    todayChallengeCompleted.value =
      data.challengeSummary?.streakCalendar?.[today] === true;

    if (Array.isArray(data.recommendedParts) && data.recommendedParts.length === 3) {
      recommendedTags.value = data.recommendedParts.map(p => ({ value: p, label: p }));
    } else if (data.firstExercise) {
      recommendedTags.value = [
        data.firstExercise,
        data.secondExercise,
        data.thirdExercise
      ].map(p => ({ value: p, label: p }));
    } else {
      recommendedTags.value = [
        { value: "복부", label: "복부" },
        { value: "하체", label: "하체" },
        { value: "유산소", label: "유산소" }
      ];
    }

    const articlesRes = await api.get(
      `/api_article/get/user_id/${user.value.userId}`
    );
    myArticles.value = articlesRes.data;
  } catch (err) {
    recommendedTags.value = [
      { value: "복부", label: "복부" },
      { value: "하체", label: "하체" },
      { value: "유산소", label: "유산소" }
    ];
  } finally {
    isChallengeLoading.value = false;
    fetchLatestInbody();
  }
});

const goToBoardWithTag = tag => {
  router.push({ path: "/board", query: { tag: tag.value } });
};
</script>


<style scoped>
@import url("https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css");

.challenge-status {
  font-family: "Pretendard Variable", "Pretendard", "Noto Sans KR", Arial, sans-serif;
  background: #f8f9fa;
  border-radius: 16px;
  padding: 32px 20px 28px 20px;
  box-shadow: 0 4px 24px rgba(66, 185, 131, 0.09);
  max-width: 600px;
  margin: 0 auto;
  border: 2px solid #e0e7ef;
}

h3 {
  margin-top: 0;
  color: #42b983;
  border-bottom: 2.5px solid #42b983;
  padding-bottom: 10px;
  margin-bottom: 28px;
  font-size: 1.7rem;
  font-weight: 800;
  letter-spacing: -1px;
  text-align: center;
  font-family: inherit;
  background: linear-gradient(90deg, #e0f7fa 60%, #fff 100%);
  border-radius: 12px 12px 0 0;
  box-shadow: 0 2px 8px rgba(66,185,131,0.06);
}

.challenge-completed,
.challenge-incomplete {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
  font-family: inherit;
}

.success-message {
  background: linear-gradient(90deg, #e6f9f1 0%, #fff 100%);
  border: 1.5px solid #42b98344;
  border-radius: 10px;
  padding: 18px 24px;
  color: #1a7f5a;
  text-align: center;
  font-size: 1.13rem;
  font-weight: 600;
  box-shadow: 0 2px 8px #42b98311;
  font-family: inherit;
}

.success-message h4 {
  font-size: 1.18rem;
  font-weight: 700;
  color: #1a7f5a;
  margin-bottom: 8px;
  font-family: inherit;
}
.success-message p {
  font-size: 1.05rem;
  color: #1a7f5a;
  font-family: inherit;
}
.challenge-prompt {
  background: linear-gradient(90deg, #e0f7fa 0%, #f8f9fa 100%);
  border: 1.5px solid #42b98322;
  border-radius: 10px;
  padding: 18px 24px;
  color: #229d6a;
  text-align: center;
  font-size: 1.13rem;
  font-weight: 600;
  box-shadow: 0 2px 8px #42b98311;
  font-family: inherit;
}
.challenge-prompt h4 {
  font-size: 1.18rem;
  font-weight: 700;
  color: #229d6a;
  margin-bottom: 8px;
  font-family: inherit;
}
.challenge-prompt p {
  font-size: 1.05rem;
  color: #229d6a;
  font-family: inherit;
}
.streak-info {
  font-weight: bold;
  margin-top: 10px;
  color: #42b983;
  font-size: 1.08rem;
  font-family: inherit;
}

.upload-section {
  margin-top: 18px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  align-items: center;
}

.file-input-container {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: center;
}

.file-input {
  display: none;
}

.file-input-label {
  background: linear-gradient(90deg, #42b983 60%, #5eead4 100%);
  color: white;
  padding: 8px 18px;
  border-radius: 22px;
  cursor: pointer;
  font-weight: 600;
  font-size: 1rem;
  transition: background 0.2s;
  border: none;
  box-shadow: 0 2px 8px #42b98322;
  font-family: inherit;
}
.file-input-label:hover {
  background: linear-gradient(90deg, #2e8c6a 60%, #42b983 100%);
}

.file-name {
  color: #495057;
  font-size: 0.98rem;
  font-family: inherit;
}

.preview-container {
  position: relative;
  width: 100%;
  max-width: 260px;
  margin: 10px 0;
}

.image-preview {
  width: 100%;
  border-radius: 8px;
  border: 1.5px solid #e9ecef;
  box-shadow: 0 2px 8px #42b98311;
}

.clear-preview {
  position: absolute;
  top: 5px;
  right: 5px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 50%;
  width: 26px;
  height: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 1.1rem;
  box-shadow: 0 2px 8px #42b98322;
  transition: background 0.2s;
}
.clear-preview:hover {
  background: #2e8c6a;
}

.upload-button {
  background: linear-gradient(90deg, #42b983 60%, #5eead4 100%);
  color: white;
  border: none;
  border-radius: 22px;
  padding: 10px 32px;
  cursor: pointer;
  transition: background 0.2s;
  font-weight: bold;
  font-size: 1.08rem;
  box-shadow: 0 2px 8px #42b98322;
  font-family: inherit;
}
.upload-button:hover:not(:disabled) {
  background: linear-gradient(90deg, #2e8c6a 60%, #42b983 100%);
}
.upload-button:disabled {
  background: #adb5bd;
  cursor: not-allowed;
}

.recommended-tags {
  margin-top: 30px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 2px solid #e0e7ef; 
  border-radius: 12px;
  padding: 18px 10px 14px 10px;
  background: #fafdff;
}
.recommended-tags h4 {
  font-size: 1.18rem;
  font-weight: 700;
  color: #222;
  margin-bottom: 12px;
  letter-spacing: -0.5px;
  font-family: inherit;
}

.tag-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
}

.tag-button {
  background: linear-gradient(90deg, #42b983 60%, #5eead4 100%);
  color: white;
  border: none;
  border-radius: 22px;
  padding: 8px 22px;
  cursor: pointer;
  font-weight: 600;
  font-size: 1.05rem;
  transition: background 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px #42b98322;
  font-family: inherit;
}
.tag-button:hover {
  background: linear-gradient(90deg, #2e8c6a 60%, #42b983 100%);
}

/* 추천 태그, 인바디 정보 박스도 통일 */
.recommended-tags,
.inbody-info {
  border: 2px solid #e0e7ef;
  border-radius: 12px;
  padding: 18px 10px 14px 10px;
  background: #fafdff;
}

.recommended-tags h4,
.inbody-info h4 {
  font-size: 1.13rem;
  font-weight: 700;
  color: #229d6a;
  margin-bottom: 12px;
  letter-spacing: -0.5px;
  text-align: center;
  font-family: inherit;
}

.inbody-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 18px;
  margin-top: 10px;
}

.inbody-item {
  background: #f1f1f1;
  border-radius: 10px;
  padding: 16px 8px;
  text-align: center;
  box-shadow: 0 2px 8px #42b98311;
  font-family: inherit;
}

.inbody-label {
  font-size: 15px;
  color: #666;
  margin-bottom: 5px;
  font-weight: 500;
  font-family: inherit;
}

.inbody-value {
  font-size: 20px;
  font-weight: bold;
  color: #222;
  margin: 0;
  letter-spacing: 0.5px;
  font-family: inherit;
}

@media (max-width: 768px) {
  .challenge-status {
    padding: 16px 4px 16px 4px;
    border-radius: 10px;
  }
  .inbody-info,
  .recommended-tags {
    padding: 10px 2px 10px 2px;
    border-radius: 8px;
  }
  .inbody-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  .recommended-tags {
    margin-top: 18px;
  }
}
</style>