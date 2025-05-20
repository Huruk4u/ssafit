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

      <!-- 유저 정보 변경 페이지로 이동 -->
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