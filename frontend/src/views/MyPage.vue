<template>
  <div class="root">
    <div class="mypage-container container py-4">
      <!-- 상단 고정 유저 정보 영역 -->
      <UserInfoCard
        :user="user"
        :represent-badge="representBadge"
        :user-profile-image="userProfileImage"
        :user-background-image="userBackgroundImage"
        @open-badge-modal="openBadgeModal"
      />

      <!-- 탭 영역 -->
      <Tabs :active-tab="activeTab" @change="activeTab = $event" @edit-user="goEditUser" />

      <!-- 아래 컨텐츠 영역 -->
      <div class="tab-content-area">
        <div v-if="activeTab === 1" class="tab-content card p-4">
          <ActivityInfo />
        </div>
        <div v-if="activeTab === 2" class="tab-content card p-4">
          <ChallengeRegister />
        </div>
        <div v-if="activeTab === 3" class="tab-content card p-4">
          <MyArticles />
        </div>
        <div v-if="activeTab === 4" class="tab-content card p-4">
          <MyLikedArticles />
        </div>
        <div v-if="activeTab === 5" class="tab-content card p-4">
          <MyFollowing />
        </div>
        <div v-if="activeTab === 6" class="tab-content card p-4">
          <UserEdit />
        </div>
      </div>
    </div>

    <!-- 뱃지 선택 모달 -->
    <BadgeModal
      v-if="showBadgeModal"
      :badges="badges"
      @close="closeBadgeModal"
      @select="selectBadge"
      :format-date="formatDate"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import api from "@/api/axiosInstance";
import ChallengeRegister from "@/components/mypage/ChallengeRegister.vue";
import MyArticles from "@/components/mypage/MyArticles.vue";
import MyLikedArticles from "@/components/mypage/MyLikedArticles.vue";
import ActivityInfo from "@/components/mypage/ActivityInfo.vue";
import MyFollowing from "@/components/mypage/MyFollowing.vue";
import UserInfoCard from "@/components/mypage/UserInfoCard.vue";
import Tabs from "@/components/mypage/Tabs.vue";
import BadgeModal from "@/components/mypage/BadgeModal.vue";

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
@import url("https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css");

:root,
body,
.root {
  font-family: "Pretendard Variable", "Pretendard", "Noto Sans KR", Arial,
    sans-serif;
  font-size: 16px;
  color: #222;
  background: #fafbfc;
  letter-spacing: 0.01em;
}

.mypage-container {
  width: 100vw;
  max-width: 100vw;
  overflow-x: hidden;
  margin: 0 auto;
}

.tab-content-area {
  margin-top: 24px;
}
</style>