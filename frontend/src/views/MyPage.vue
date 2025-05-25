<template>
  <div class="root">
    <div class="mypage-container container py-4">
      <!-- 상단 고정 유저 정보 영역 -->
      <section class="user-info card p-4 fixed-user-info">
        <div class="user-visual position-relative mb-5">
          <img
            :src="userBackgroundImage"
            alt="배경이미지"
            class="background-img card-img-top"
          />
          <div class="profile-row-wrap">
            <img
              :src="userProfileImage"
              alt="프로필이미지"
              class="profile-img-overlap rounded-circle border border-white shadow"
            />
            <div class="profile-meta-side">
              <div class="nickname-badge-row">
                <div class="nickname-id-col">
                  <span class="user-nickname">{{ user.nickname }}</span>
                  <span class="user-id">@{{ user.userName }}</span>
                </div>
                <div
                  class="represent-badge-inline-large"
                  @click="openBadgeModal"
                  title="대표 뱃지 변경"
                >
                  <img
                    v-if="representBadge"
                    :src="representBadge.iconUrl"
                    :alt="representBadge.name"
                    class="badge-icon-inline-large"
                  />
                  <span v-if="representBadge" class="badge-name-inline-large">
                    {{ representBadge.name }}
                  </span>
                  <span v-else class="badge-name-inline-large" style="color:#bbb;">
                    대표 뱃지가 없습니다
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="badge-section mt-3"></div>
      </section>

      <!-- 탭 영역 -->
      <div class="tabs d-flex flex-wrap gap-2 mb-4 fixed-tabs">
        <button
          @click="activeTab = 1"
          :class="[
            'btn',
            activeTab === 1 ? 'btn-primary' : 'btn-outline-primary',
          ]"
        >
          활동 정보
        </button>
        <button
          @click="activeTab = 2"
          :class="[
            'btn',
            activeTab === 2 ? 'btn-primary' : 'btn-outline-primary',
          ]"
        >
          챌린지
        </button>
        <button
          @click="activeTab = 3"
          :class="[
            'btn',
            activeTab === 3 ? 'btn-primary' : 'btn-outline-primary',
          ]"
        >
          내가 쓴 글
        </button>
        <button
          @click="activeTab = 4"
          :class="[
            'btn',
            activeTab === 4 ? 'btn-primary' : 'btn-outline-primary',
          ]"
        >
          좋아요 한 글
        </button>
        <button
          @click="activeTab = 5"
          :class="[
            'btn',
            activeTab === 5 ? 'btn-primary' : 'btn-outline-primary',
          ]"
        >
          팔로우
        </button>
        <button @click="goEditUser" class="btn btn-secondary ms-auto">
          유저 정보 변경
        </button>
      </div>

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
            <img :src="badge.iconUrl" :alt="badge.name" class="badge-icon" />
            <p class="badge-name">{{ badge.name }}</p>
            <p class="badge-description">{{ badge.description }}</p>
            <p class="badge-earned">획득일: {{ formatDate(badge.earnedAt) }}</p>
          </div>
        </div>
        <div class="modal-actions d-flex justify-content-end gap-2">
          <button @click="closeBadgeModal" class="btn btn-secondary">
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
import ChallengeRegister from "@/components/mypage/ChallengeRegister.vue";
import MyArticles from "@/components/mypage/MyArticles.vue";
import MyLikedArticles from "@/components/mypage/MyLikedArticles.vue";
import ActivityInfo from "@/components/mypage/ActivityInfo.vue";
import MyFollowing from "@/components/mypage/MyFollowing.vue";
import { useUserImage } from "@/composables/useUserImage";

const router = useRouter();
const user = ref(JSON.parse(localStorage.getItem("user") || "{}"));
const { getProfileImage, getBackgroundImage } = useUserImage(user.value);

const userProfileImage = computed(() =>
  user.value.profileImage
    ? `http://localhost:8080/images/profile/${user.value.profileImage}`
    : new URL('@/assets/image/default_user.png', import.meta.url).href
);

const userBackgroundImage = computed(() =>
  user.value.backgroundImage
    ? `http://localhost:8080/images/background/${user.value.backgroundImage}`
    : new URL('@/assets/image/default_background.png', import.meta.url).href
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

/* 흔들림 방지: 컨테이너 가로 고정 */
.mypage-container {
  width: 100vw;
  max-width: 100vw;
  overflow-x: hidden;
  margin: 0 auto;
}

/* 상단 카드 */
.user-info {
  background-color: #f4f4f4;
  padding: 15px;
  margin-bottom: 20px;
  border: none;
  border-radius: 0;
  box-shadow: none;
  z-index: 100;
}

/* 고정 유저 정보 */
.fixed-user-info {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #f4f4f4;
  margin-bottom: 0;
  border-radius: 0 0 16px 16px;
  box-shadow: 0 2px 12px rgba(66, 185, 131, 0.07);
}

/* 고정 탭 */
.fixed-tabs {
  position: sticky;
  top: 220px;
  z-index: 99;
  background: #fafbfc;
  border-radius: 0 0 12px 12px;
  box-shadow: 0 2px 8px rgba(66, 185, 131, 0.04);
  margin-bottom: 2rem;
  padding-bottom: 0.5rem;
}

/* 컨텐츠 영역 */
.tab-content-area {
  margin-top: 24px;
}

/* 배경/프로필 이미지 */
.user-visual {
  position: relative;
  width: 100%;
  height: 300px;
  margin-bottom: 60px;
}
.background-img {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: 12px;
  display: block;
}
.profile-row-wrap {
  position: absolute;
  left: 5%;
  bottom: -50px;
  display: flex;
  flex-direction: row;
  align-items: flex-end;
  z-index: 2;
}
.profile-img-overlap {
  width: 110px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  background: #fff;
}

/* 닉네임/유저네임/뱃지 한 줄 배치 */
.profile-meta-side {
  position: absolute;
  left: 120px;
  bottom: -20px;
  min-width: 260px;
  background: rgba(255,255,255,0.92);
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.07);
  padding: 10px 22px 10px 18px;
  display: flex;
  flex-direction: row; 
  align-items: center;  
  justify-content: flex-start;
  height: 90px; 
  border: 1.5px solid #42b983;
  transition: border-color 0.2s;
}

.nickname-badge-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 18px;
}

.nickname-id-col {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-right: 10px;
}

.user-nickname {
  font-size: 1.35rem;
  font-weight: 700;
  color: #222;
  margin: 0 0 2px 0;
  line-height: 1.1;
  white-space: nowrap;
}

.user-id {
  display: block;
  font-size: 1rem;
  color: #888;
  font-weight: 500;
  margin: 0;
  line-height: 1.1;
  white-space: nowrap;
}

.represent-badge-inline-large {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  background: none;
  border: none;
  padding: 0;
}

.badge-icon-inline-large {
  width: 80px;   
  height: 80px;  
  border-radius: 50%;
  object-fit: contain;
  margin-right: 4px;
  border: 2px solid #eee;
  background: #f8f9fa;
}
.badge-name-inline-large {
  font-size: 1.08rem !important;
  font-weight: 600;
  color: #42b983;
  vertical-align: middle;
  white-space: nowrap;
}
.badge-name-inline {
  font-size: 0.93rem;
  font-weight: 500;
  color: #888;
  vertical-align: middle;
}

/* 뱃지 모달 등 기존 스타일 유지 ... */
.badge-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.badge-modal {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  width: 90%;
  max-width: 900px;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.badge-modal h3 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.badges-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
  margin-bottom: 20px;
}

.badge-item {
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.badge-item:hover {
  border-color: #42b983;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.12);
}

.badge-item.represented {
  border: 2px solid #42b983;
  background: linear-gradient(to bottom, #f7fcfa, #fff);
}

.badge-item img {
  width: 80px;
  height: 80px;
  margin-bottom: 8px;
  border-radius: 50%;
  object-fit: contain;
  background: #f8f9fa;
  border: 2px solid #eee;
  box-shadow: 0 2px 8px rgba(66,185,131,0.08);
}
.badge-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: #333;
  margin: 4px 0;
  line-height: 1.2;
}

.badge-description {
  font-size: 0.8rem;
  color: #666;
  margin: 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.badge-earned {
  font-size: 0.75rem;
  color: #888;
  margin-top: 6px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.modal-actions button {
  padding: 6px 16px;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

/* 탭 스타일 */
.tabs {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.5rem;
  margin-bottom: 2rem;
  border-bottom: 1px solid #eee;
  background: #fafbfc;
}

.tabs button {
  position: relative;
  background: none;
  border: none;
  padding: 0.75rem 1.25rem;
  font-size: 0.95rem;
  font-weight: 500;
  color: #666;
  transition: all 0.3s ease;
  cursor: pointer;
}

.tabs button::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #42b983;
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.tabs button:hover {
  color: #42b983;
}

.tabs button.btn-primary {
  background: none;
  color: #42b983;
  font-weight: 600;
}

.tabs button.btn-primary::after {
  transform: scaleX(1);
}

.tabs button.btn-secondary {
  margin-left: auto;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 0.5rem 1rem;
}

.tabs button.btn-secondary:hover {
  background: #e9ecef;
  border-color: #ced4da;
  color: #495057;
}

@media (max-width: 900px) {
  .fixed-user-info {
    top: 0;
    border-radius: 0 0 10px 10px;
  }
  .fixed-tabs {
    top: 210px;
    border-radius: 0 0 8px 8px;
  }
  .profile-meta-side {
    left: 100px;
    min-width: 180px;
    height: 80px;
    padding: 8px 10px 8px 10px;
  }
  .profile-img-overlap {
    width: 80px;
    height: 90px;
  }
}

@media (max-width: 700px) {
  .fixed-user-info {
    top: 0;
    border-radius: 0 0 8px 8px;
    padding: 10px 2px 10px 2px;
  }
  .fixed-tabs {
    top: 170px;
    border-radius: 0 0 6px 6px;
    padding-bottom: 0.2rem;
  }
  .profile-meta-side {
    left: 80px;
    min-width: 120px;
    height: 60px;
    padding: 6px 6px 6px 6px;
  }
  .profile-img-overlap {
    width: 60px;
    height: 65px;
  }
  .user-nickname {
    font-size: 1.05rem;
  }
  .user-id {
    font-size: 0.85rem;
  }
}

@media (max-width: 500px) {
  .fixed-user-info {
    top: 0;
    border-radius: 0 0 4px 4px;
    padding: 6px 0 6px 0;
  }
  .fixed-tabs {
    top: 120px;
    border-radius: 0 0 4px 4px;
    padding-bottom: 0.1rem;
  }
  .profile-meta-side {
    left: 60px;
    min-width: 80px;
    height: 40px;
    padding: 2px 2px 2px 2px;
  }
  .profile-img-overlap {
    width: 38px;
    height: 40px;
  }
  .user-nickname {
    font-size: 0.9rem;
  }
  .user-id {
    font-size: 0.7rem;
  }
}
</style>