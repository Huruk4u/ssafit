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
        v-for="user in followingList" 
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
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "@/api/axiosInstance";

const router = useRouter();
const followingList = ref([]);
const loading = ref(true);

const getUserProfileImage = (profileImage) => {
  return profileImage
    ? `http://localhost:8080/images/profile/${profileImage}`
    : "/default-profile.png";
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
      // No Content - 팔로우한 사람이 없음
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
  color: #333;
  border-bottom: 2px solid #42b983;
  padding-bottom: 8px;
  margin-bottom: 20px;
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
  border: 2px solid #dee2e6;
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
  background-color: #28a745;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
}

@media (max-width: 768px) {
  .following-item {
    padding: 12px;
  }
  
  .user-profile-img {
    width: 50px;
    height: 50px;
    margin-right: 12px;
  }
  
  .user-nickname {
    font-size: 16px;
  }
  
  .user-username {
    font-size: 13px;
  }
}
</style>