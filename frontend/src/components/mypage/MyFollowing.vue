<template>
  <div class="my-following">
    <h3>내가 팔로우한 사람들</h3>

    <div v-if="loading" class="loading">
      <p>로딩 중...</p>
    </div>

    <div v-else-if="followingList.length === 0" class="no-following">
      <p>팔로우한 사람이 없습니다.</p>
    </div>

    <div v-else class="following-list">
      <div
        v-for="user in pagedFollowing"
        :key="user.userId"
        class="following-item"
        @click="goToUserProfile(user.userId)"
      >
        <img
          :src="getUserProfileImage(user.profileImage)"
          :alt="user.nickname + ' 프로필'"
          class="user-profile-img"
        />
        <div class="user-info">
          <p class="user-nickname">{{ user.nickname }}</p>
          <p class="user-username">@{{ user.userName }}</p>
        </div>
        <div class="follow-indicator">
          <span class="following-badge">팔로잉</span>
        </div>
      </div>
      <div class="pagination" v-if="totalPages > 1">
        <button @click="prevPage" :disabled="currentPage === 1">이전</button>
        <span>{{ currentPage }} / {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages">
          다음
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import api from "@/api/axiosInstance";
import { useUserImage } from "@/composables/useUserImage"; // 추가

const router = useRouter();
const followingList = ref([]);
const loading = ref(true);

const currentPage = ref(1);
const pageSize = 8;

const pagedFollowing = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return followingList.value.slice(start, start + pageSize);
});
const totalPages = computed(() =>
  Math.ceil(followingList.value.length / pageSize)
);

const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--;
};
const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++;
};

// useUserImage 활용: 각 유저 객체를 넘겨서 프로필 이미지 URL 반환
const getUserProfileImage = (profileImage, backgroundImage = null) => {
  // useUserImage는 유저 객체를 받으므로 임시 객체 생성
  const { getProfileImage } = useUserImage({
    profileImage,
    backgroundImage,
  });
  return getProfileImage();
};

const goToUserProfile = (userId) => {
  router.push(`/summary/userId/${userId}`);
};

const fetchFollowingList = async () => {
  try {
    loading.value = true;
    const response = await api.get("/api_follow/get/follow");
    followingList.value = response.data || [];
  } catch (error) {
    console.error("팔로우 목록 조회 실패:", error);
    if (error.response?.status === 204) {
      followingList.value = [];
    } else {
      alert("팔로우 목록을 불러오는데 실패했습니다.");
    }
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchFollowingList();
});
</script>

<style scoped>
.my-following {
  padding: 20px;
}

h3 {
  margin-top: 0;
  color: #42b983;
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: 0.01em;
  border-bottom: 2.5px solid #42b983;
  padding-bottom: 10px;
  margin-bottom: 24px;
  background: linear-gradient(90deg, #e0f7fa 60%, #fff 100%);
  border-radius: 12px 12px 0 0;
  box-shadow: 0 2px 8px rgba(66, 185, 131, 0.06);
}

.loading {
  text-align: center;
  padding: 40px 0;
  color: #666;
}

.no-following {
  text-align: center;
  padding: 40px 0;
  color: #888;
  font-size: 16px;
}

.following-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.following-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.following-item:hover {
  background-color: #e9ecef;
  border-color: #007bff;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.user-profile-img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 16px;
  border: 4px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  background: #fff;
}

.user-info {
  flex: 1;
}

.user-nickname {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0 0 4px 0;
}

.user-username {
  font-size: 14px;
  color: #6c757d;
  margin: 0;
}

.follow-indicator {
  margin-left: auto;
}

.following-badge {
  background: linear-gradient(90deg, #42b983 60%, #b2f2e5 100%);
  color: #fff;
  padding: 5px 18px;
  border-radius: 18px;
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  box-shadow: 0 2px 8px rgba(66, 185, 131, 0.1);
  border: 2px solid #b2dfdb;
  transition: background 0.2s, border 0.2s;
  display: inline-block;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 18px;
  margin: 24px 0 10px 0;
}

.pagination button {
  background: #42b983;
  color: #fff;
  border: none;
  border-radius: 16px;
  padding: 6px 18px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.pagination button:disabled {
  background: #adb5bd;
  cursor: not-allowed;
}
.pagination span {
  font-size: 1.05rem;
  color: #222;
  font-weight: 500;
}

@media (max-width: 768px) {
  .user-profile-img {
    width: 50px;
    height: 50px;
    margin-right: 12px;
  }
}
</style>