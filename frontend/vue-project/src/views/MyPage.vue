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
        <div class="badge-section">
          <div
            v-if="representBadge"
            class="represent-badge"
            @click="openBadgeModal"
          >
            <img
              :src="representBadge.iconUrl"
              :alt="representBadge.name"
              class="badge-icon"
            />
            <p>대표 뱃지: {{ representBadge.name }}</p>
            <span class="change-badge-text">(변경하려면 클릭하세요)</span>
          </div>
          <div
            v-else
            class="represent-badge no-badge"
            @click="openBadgeModal"
          >
            <p>대표 뱃지가 없습니다. 클릭하여 선택하세요.</p>
          </div>
        </div>
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
        <button @click="activeTab = 4" :class="{ active: activeTab === 4 }">
          좋아요 한 글
        </button>
        <button @click="goEditUser">유저 정보 변경</button>
      </div>

      <div v-if="activeTab === 1" class="tab-content">
        <ActivityInfo />
      </div>

      <div v-if="activeTab === 2" class="tab-content challenge-tab">
        <ChallengeRegister />
      </div>

      <div v-if="activeTab === 3" class="tab-content">
        <MyArticles />
      </div>

      <div v-if="activeTab === 4" class="tab-content">
        <MyLikedArticles />
      </div>

      <div v-if="activeTab === 5" class="tab-content">
        <UserEdit />
      </div>
    </div>

    <!-- 뱃지 선택 모달 -->
    <div
      v-if="showBadgeModal"
      class="badge-modal-overlay"
      @click="closeBadgeModal"
    >
      <div class="badge-modal" @click.stop>
        <h3>대표 뱃지 선택</h3>
        <div class="badges-grid">
          <div
            v-for="badge in badges"
            :key="badge.badgeId"
            class="badge-item"
            :class="{ represented: badge.isRepresented }"
            @click="selectBadge(badge)"
          >
            <img
              :src="badge.iconUrl"
              :alt="badge.name"
              class="badge-icon"
            />
            <p class="badge-name">{{ badge.name }}</p>
            <p class="badge-description">{{ badge.description }}</p>
            <p class="badge-earned">획득일: {{ formatDate(badge.earnedAt) }}</p>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeBadgeModal" class="cancel-button">
            취소
          </button>
        </div>
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
import MyLikedArticles from "@/components/MyLikedArticles.vue";
import ActivityInfo from "@/components/ActivityInfo.vue";

const router = useRouter();
const user = ref(JSON.parse(localStorage.getItem("user") || "{}"));

const userProfileImage = computed(() =>
  user.value.profileImage
    ? `http://localhost:8080/images/profile/${user.value.profileImage}`
    : "/default-profile.png"
);
const userBackgroundImage = computed(() =>
  user.value.backgroundImage
    ? `http://localhost:8080/images/background/${user.value.backgroundImage}`
    : "/default-background.jpg"
);

const activeTab = ref(1);
const userProfileInfo = ref({});
const representBadge = ref(null);
const badges = ref([]);
const showBadgeModal = ref(false);

const goEditUser = () => {
  router.push("/editProfile");
};

const openBadgeModal = () => {
  showBadgeModal.value = true;
};

const closeBadgeModal = () => {
  showBadgeModal.value = false;
};

const formatDate = (dateString) => {
  if (!dateString) return "정보 없음";
  const date = new Date(dateString);
  return date.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
};

const selectBadge = (badge) => {
  api
    .put(`/api_mypage/badges/represent/${badge.badgeId}`)
    .then((res) => {
      if (res.data.success) {
        badges.value.forEach((b) => {
          b.isRepresented = b.badgeId === badge.badgeId;
        });
        representBadge.value = badge;
        closeBadgeModal();
      } else {
        alert(res.data.message || "대표 뱃지 변경에 실패했습니다.");
      }
    })
    .catch((err) => {
      console.error(err);
      alert("대표 뱃지 변경에 실패했습니다.");
    });
};

onMounted(() => {
  if (!user.value.userId) {
    alert("로그인 후 이용해주세요.");
    router.push("/login");
    return;
  }

  api
    .get(`/api_mypage/profile`)
    .then((res) => {
      userProfileInfo.value = res.data;
      badges.value = Array.isArray(res.data.badges) ? res.data.badges : [];
      representBadge.value = res.data.badge || null;
    })
    .catch((err) => {
      console.error(err);
      alert("프로필 정보를 불러오는 데 실패했습니다.");
    });
});
</script>


<style scoped>
/* 기존 스타일 코드는 동일하게 유지 */
.mypage-container {
  padding: 20px;
}
.user-info {
  background-color: #f4f4f4;
  padding: 15px;
  margin-bottom: 20px;
}
.tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}
.tabs button {
  padding: 8px 16px;
  cursor: pointer;
  border: 1px solid #ddd;
  background-color: #f8f8f8;
  border-radius: 4px;
  transition: all 0.3s ease;
}
.tabs .active {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}
.tab-content {
  margin-top: 20px;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* 뱃지 관련 스타일 */
.badge-section {
  margin-top: 15px;
}

.represent-badge {
  display: flex;
  align-items: center;
  background-color: #fff;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #ddd;
  cursor: pointer;
  transition: all 0.3s ease;
}

.represent-badge:hover {
  background-color: #f0f7ff;
  border-color: #b8daff;
}

.no-badge {
  text-align: center;
  color: #6c757d;
  padding: 15px;
}

.badge-icon {
  width: 100px;
  height: 100px;
  margin-right: 15px;
  object-fit: contain;
  border-radius: 50%; 
  box-shadow: 0 2px 5px rgba(0,0,0,0.2); 
}

.change-badge-text {
  margin-left: auto;
  font-size: 12px;
  color: #007bff;
}

/* 뱃지 모달 스타일 */
.badge-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.badge-modal {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  width: 90%;
  max-width: 800px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.badge-modal h3 {
  margin-top: 0;
  border-bottom: 2px solid #42b983;
  padding-bottom: 10px;
  margin-bottom: 20px;
}

.badges-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.badge-item {
  background-color: #f8f9fa;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  padding: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.badge-item:hover {
  border-color: #007bff;
  transform: translateY(-3px);
}

.badge-item.represented {
  border-color: #28a745;
  background-color: #d4edda;
}

.badge-name {
  font-weight: bold;
  margin: 10px 0 5px;
}

.badge-description {
  font-size: 14px;
  color: #6c757d;
  margin-bottom: 10px;
}

.badge-earned {
  font-size: 12px;
  color: #495057;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-button {
  padding: 8px 16px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
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

@media (max-width: 768px) {
  .badges-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .tabs {
    flex-direction: column;
  }
  
  .tabs button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .badges-grid {
    grid-template-columns: 1fr;
  }
}
</style>