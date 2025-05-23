<template>
  <div class="root">
    <div class="mypage-container">
      <section class="user-info">
        <div class="user-images">
          <img :src="backgroundUrl" alt="배경이미지" class="background-img" />
          <img :src="profileUrl" alt="프로필이미지" class="profile-img" />
        </div>
        <div class="user-names-section">
          <div class="nickname-row">
            <span class="user-nickname">{{ summary.nickname }}</span>
            <span class="user-id">@{{ summary.userName }}</span>
          </div>
          <button
            v-if="!isMyProfile"
            @click="toggleFollow"
            :disabled="followLoading"
            :class="['follow-button', isFollowing ? 'unfollow' : 'follow']"
          >
            {{
              followLoading ? "처리중..." : isFollowing ? "언팔로우" : "팔로우"
            }}
          </button>
        </div>
        <div class="badge-section">
          <div v-if="summary.representedBadge" class="represent-badge">
            <img
              :src="badgeUrl"
              :alt="summary.representedBadge.name"
              class="badge-icon"
            />
            <div class="badge-info">
              <p class="badge-label">대표 뱃지:</p>
              <p class="badge-name">{{ summary.representedBadge.name }}</p>
            </div>
          </div>
          <div v-else class="represent-badge no-badge">
            <p>대표 뱃지가 없습니다.</p>
          </div>
        </div>
      </section>

      <section class="tab-content">
        <h3 class="section-title">작성한 글</h3>
        <ul class="article-list">
          <li v-for="art in pagedArticles" :key="art.articleId">
            <router-link
              :to="`/board/detail/${art.articleId}`"
              class="article-link"
            >
              <p class="article-title">{{ art.title }}</p>
              <p class="article-meta">
                작성일: {{ formatDate(art.createdAt) }}
              </p>
            </router-link>
          </li>
        </ul>
        <div v-if="!summary.articles.length" class="no-articles">
          작성한 글이 없습니다.
        </div>
        <div class="pagination" v-if="totalPages > 1">
          <button @click="prevPage" :disabled="currentPage === 1">이전</button>
          <span>{{ currentPage }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage === totalPages">
            다음
          </button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/api/axiosInstance";

const route = useRoute();
const router = useRouter();
const userId = Number(route.params.userId);

const currentUser = ref(JSON.parse(localStorage.getItem("user") || "{}"));

const summary = ref({
  userName: "",
  nickname: "",
  profileImage: null,
  backgroundImage: null,
  representedBadge: null,
  articles: [],
});

// 페이지네이션 관련
const currentPage = ref(1);
const pageSize = 10;
const pagedArticles = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return summary.value.articles.slice(start, start + pageSize);
});
const totalPages = computed(() =>
  Math.ceil(summary.value.articles.length / pageSize)
);
const prevPage = () => {
  if (currentPage.value > 1) currentPage.value--;
};
const nextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++;
};

// 팔로우 관련 상태
const isFollowing = ref(false);
const followLoading = ref(false);

// 내 프로필인지 확인
const isMyProfile = computed(() => {
  return currentUser.value.userId === userId;
});

const profileUrl = computed(() =>
  summary.value.profileImage
    ? `http://localhost:8080/images/profile/${summary.value.profileImage}`
    : "/default-profile.png"
);
const backgroundUrl = computed(() =>
  summary.value.backgroundImage
    ? `http://localhost:8080/images/background/${summary.value.backgroundImage}`
    : "/default-background.jpg"
);
const badgeUrl = computed(() =>
  summary.value.representedBadge
    ? `http://localhost:5173${summary.value.representedBadge.iconUrl}`
    : ""
);

// 날짜 포맷 헬퍼
const formatDate = (dateString) => {
  if (!dateString) return "정보 없음";
  const date = new Date(dateString);
  return date.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

// 팔로우 상태 확인
const checkFollowStatus = async () => {
  if (isMyProfile.value) return;

  try {
    const response = await api.get("/api_follow/get/follow");
    const followingList = response.data || [];
    isFollowing.value = followingList.some((user) => user.userId === userId);
  } catch (error) {
    console.error("팔로우 상태 확인 실패:", error);
    // 204 No Content인 경우 팔로우하지 않은 것으로 처리
    if (error.response?.status === 204) {
      isFollowing.value = false;
    }
  }
};

// 팔로우/언팔로우 토글
const toggleFollow = async () => {
  if (followLoading.value) return;

  followLoading.value = true;

  try {
    if (isFollowing.value) {
      // 언팔로우
      await api.delete(`/api_follow/delete/follow/userId/${userId}`);
      isFollowing.value = false;
      alert("언팔로우 했습니다.");
    } else {
      // 팔로우
      await api.post(`/api_follow/post/follow/userId/${userId}`);
      isFollowing.value = true;
      alert("팔로우 했습니다.");
    }
  } catch (error) {
    console.error("팔로우/언팔로우 실패:", error);
    alert("요청 처리에 실패했습니다. 다시 시도해주세요.");
  } finally {
    followLoading.value = false;
  }
};

onMounted(async () => {
  try {
    // 사용자 요약 정보 조회
    const res = await api.get(`/api_mypage/summary/userId/${userId}`);
    Object.assign(summary.value, res.data);

    // 팔로우 상태 확인 (내 프로필이 아닌 경우만)
    if (!isMyProfile.value) {
      await checkFollowStatus();
    }
  } catch (e) {
    console.error("유저 요약 정보 조회 실패:", e);
    alert("유저 정보를 불러오지 못했습니다.");
    router.back();
  }
});
</script>

<style scoped>
.root {
  background: linear-gradient(135deg, #e0f7fa 0%, #f4f4f4 100%);
  min-height: 100vh;
  padding-top: 32px;
}
.mypage-container {
  max-width: 820px;
  margin: 0 auto;
  padding: 32px 20px 28px 20px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 6px 32px rgba(66, 185, 131, 0.1),
    0 1.5px 6px rgba(66, 185, 131, 0.07);
}
.user-info {
  position: relative;
  text-align: center;
  margin-bottom: 36px;
}
.user-images {
  position: relative;
}
.background-img {
  width: 100%;
  height: 210px;
  object-fit: cover;
  border-radius: 14px 14px 0 0;
}
.profile-img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  position: absolute;
  bottom: -60px;
  left: 50%;
  transform: translateX(-50%);
  border: 5px solid #fff;
  box-shadow: 0 2px 12px rgba(66, 185, 131, 0.13);
  background: #f8f9fa;
}

/* 닉네임 @유저네임 한 줄 스타일 */
.nickname-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.3rem;
  font-weight: 700;
  color: #222;
  margin-bottom: 6px;
  justify-content: center;
}
.user-nickname {
  color: #222;
}
.user-id {
  color: #888;
  font-weight: 500;
  font-size: 1.1rem;
}

.user-names-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 18px;
  margin-top: 74px;
  flex-wrap: wrap;
}

.follow-button {
  padding: 9px 26px;
  border: none;
  border-radius: 22px;
  font-size: 15px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 100px;
  box-shadow: 0 2px 8px rgba(66, 185, 131, 0.07);
}

.follow-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.follow-button.follow {
  background: linear-gradient(135deg, #42b983 60%, #b2f2e5 100%);
  color: #fff;
  border: 2px solid #b2dfdb;
}

.follow-button.follow:hover:not(:disabled) {
  background: linear-gradient(135deg, #369870 60%, #42b983 100%);
  border-color: #42b983;
}

.follow-button.unfollow {
  background: linear-gradient(135deg, #dc3545 60%, #f8bbd0 100%);
  color: #fff;
  border: 2px solid #f8bbd0;
}

.follow-button.unfollow:hover:not(:disabled) {
  background: linear-gradient(135deg, #c82333 60%, #dc3545 100%);
  border-color: #dc3545;
}

.badge-section {
  margin-top: 18px;
}

/* 대표 뱃지 스타일 */
.represent-badge .badge-icon {
  width: 90px;
  height: 90px;
  margin-right: 16px;
  object-fit: contain;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(66, 185, 131, 0.13);
  background: #f8f9fa;
}
.represent-badge {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  padding: 14px 18px;
  border-radius: 12px;
  border: 1.5px solid #b2dfdb;
  box-shadow: 0 2px 8px rgba(66, 185, 131, 0.07);
  margin: 0 auto;
  max-width: 340px;
  justify-content: center;
}
.represent-badge.no-badge {
  background: #f8f9fa;
  border: 1.5px dashed #b2dfdb;
  color: #aaa;
  padding: 18px 0;
  border-radius: 12px;
  margin: 0 auto;
  max-width: 340px;
  text-align: center;
}
.badge-info {
  text-align: left;
}
.badge-label {
  font-size: 13px;
  color: #369870;
  margin: 0 0 2px 0;
  font-weight: 600;
}
.badge-name {
  font-size: 16px;
  color: #333;
  margin: 0;
  font-weight: 500;
}

/* 내가 쓴 글: 한 줄씩, 제목/작성일 */
.section-title {
  font-size: 19px;
  margin-bottom: 14px;
  border-bottom: 2px solid #42b983;
  padding-bottom: 4px;
  color: #333;
  font-weight: 700;
  letter-spacing: -0.5px;
}
.article-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.article-list li {
  padding: 13px 0 13px 0;
  border-bottom: 1px solid #e0e0e0;
}
.article-title {
  font-weight: 500;
  margin: 0;
  color: #42b983;
  font-size: 16px;
  letter-spacing: -0.2px;
}
.article-meta {
  font-size: 12px;
  color: #777;
  margin: 4px 0 0;
}
.article-link {
  display: block;
  text-decoration: none;
  transition: background 0.15s;
  border-radius: 8px;
  padding: 2px 8px;
}
.article-link:hover .article-title {
  text-decoration: underline;
  color: #369870;
  background: #e0f7fa;
}

.no-articles {
  text-align: center;
  color: #aaa;
  padding: 22px 0;
  font-size: 15px;
}

/* 반응형 디자인 */
@media (max-width: 600px) {
  .mypage-container {
    padding: 10px 2vw 18px 2vw;
  }
  .user-names-section {
    flex-direction: column;
    gap: 12px;
  }
  .nickname-row {
    font-size: 1.05rem;
    flex-direction: column;
    gap: 2px;
  }
  .user-nickname {
    font-size: 1.1rem;
  }
  .user-id {
    font-size: 0.95rem;
  }
  .follow-button {
    width: 100%;
    max-width: 220px;
  }
  .represent-badge,
  .represent-badge.no-badge {
    max-width: 98vw;
    padding: 10px 0;
  }
}
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 18px;
  margin: 24px 0 10px 0;
  font-family: inherit;
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
</style>