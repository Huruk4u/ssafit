<template>
  <div class="root">
    <Header />

    <div class="mypage-container">
      <section class="user-info">
        <div>
          <img :src="userProfileImage" alt="프로필이미지" class="profile-img" />
          <img
            :src="userBackgroundImage"
            alt="배경이미지"
            class="background-img"
          />
        </div>
        <p>
          <strong>{{ user.nickname }}</strong> ({{ user.userName }})
        </p>
        <p>대표 뱃지: {{ representBadge }}</p>
        <div>보유 뱃지: {{ badges }}</div>
      </section>

      <div class="tabs">
        <button @click="activeTab = 1" :class="{ active: activeTab === 1 }">
          활동 정보
        </button>
        <button @click="activeTab = 2" :class="{ active: activeTab === 2 }">
          챌린지
        </button>
        <button @click="activeTab = 3" :class="{ active: activeTab === 3 }">
          내가 쓴 글
        </button>
        <button @click="goEditUser">유저 정보 변경</button>
      </div>

      <div v-if="activeTab === 1" class="tab-content">
        <p>현재 연속일: {{ currentStreak }}일</p>
        <p>최대 연속일: {{ longestStreak }}일</p>
      </div>

      <div v-if="activeTab === 2" class="tab-content challenge-tab">
        <ChallengeRegister />
      </div>

      <div v-if="activeTab === 3" class="tab-content">
        <MyArticles />
      </div>

      <div v-if="activeTab === 4" class="tab-content">
        <UserEdit />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import api from "@/api/axiosInstance";
import Header from "@/components/Header.vue";
import ChallengeRegister from "@/components/ChallengeRegister.vue";
import MyArticles from "@/components/MyArticles.vue";

const router = useRouter();
const user = ref(JSON.parse(localStorage.getItem("user") || "{}"));

const userProfileImage = computed(() =>
  user.value.profileImage
    ? `http://localhost:8080/images/profile/${user.value.profileImage}`
    : ""
);
const userBackgroundImage = computed(() =>
  user.value.backgroundImage
    ? `http://localhost:8080/images/background/${user.value.backgroundImage}`
    : ""
);

const activeTab = ref(1);
const userProfileInfo = ref({});

const goEditUser = () => {
  router.push("/editProfile");
};

onMounted(() => {
  if (!user.value.userId) {
    alert("로그인 후 이용해주세요.");
    router.push("/login");
  }
  api
    .get(`/api_mypage/profile`)
    .then((res) => {
      console.log(res.data);
      userProfileInfo.value = res.data;
    })
    .catch((err) => {
      console.error(err);
      alert("프로필 정보를 불러오는 데 실패했습니다.");
    });
});
</script>

<style scoped>
.mypage-container {
  padding: 20px;
}
.user-info {
  background-color: #f4f4f4;
  padding: 15px;
  margin-bottom: 20px;
}
.tabs button {
  margin-right: 10px;
  padding: 8px 16px;
  cursor: pointer;
}
.tabs .active {
  background-color: #007bff;
  color: white;
}
.tab-content {
  margin-top: 20px;
}

/* 챌린지 탭 스타일 */
.challenge-tab {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

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

/* 프로필 이미지 스타일 */
.profile-img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.background-img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

/* 내가 쓴 글 스타일 */
.article-link {
  text-decoration: none;
  color: #007bff;
}

.article-link:hover {
  text-decoration: underline;
}
</style>