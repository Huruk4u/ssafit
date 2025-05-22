<template>
  <div class="root">
    <Header />

    <div class="mypage-container">
      <section class="user-info">
        <div class="user-images">
          <img :src="backgroundUrl" alt="배경이미지" class="background-img" />
          <img :src="profileUrl" alt="프로필이미지" class="profile-img" />
        </div>
        <p class="user-names">
          <strong>{{ summary.nickname }}</strong>
          <span>({{ summary.userName }})</span>
        </p>
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
          <li v-for="art in summary.articles" :key="art.articleId">
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
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "@/api/axiosInstance";
import Header from "@/components/Header.vue";

const route = useRoute();
const router = useRouter();
const userId = Number(route.params.userId);

const summary = ref({
  userName: "",
  nickname: "",
  profileImage: null,
  backgroundImage: null,
  representedBadge: null,
  articles: [],
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

onMounted(async () => {
  try {
    const res = await api.get(`/api_mypage/summary/userId/${userId}`);
    Object.assign(summary.value, res.data);
  } catch (e) {
    console.error("유저 요약 정보 조회 실패:", e);
    alert("유저 정보를 불러오지 못했습니다.");
    router.back();
  }
});
</script>

<style scoped>
.root {
  background-color: #f4f4f4;
  min-height: 100vh;
}
.mypage-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background: white;
  border-radius: 8px;
}
.user-info {
  position: relative;
  text-align: center;
  margin-bottom: 32px;
}
.user-images {
  position: relative;
}
.background-img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px 8px 0 0;
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
  border: 4px solid white;
}
.user-names {
  margin-top: 70px;
  font-size: 20px;
  color: #333;
}
.badge-section {
  margin-top: 16px;
}

/* 대표 뱃지 스타일 수정: 둥글지 않게 */
.represent-badge .badge-icon {
  width: 100px;
  height: 100px;
  margin-right: 12px;
  object-fit: contain;
  border-radius: 8px; /* 둥근 사각형 */
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}
.represent-badge {
  display: flex;
  align-items: center;
  background-color: #fff;
  padding: 12px 16px;
  border-radius: 8px;
  border: 1px solid #ddd;
}

/* 내가 쓴 글: 한 줄씩, 제목/작성일 */
.section-title {
  font-size: 18px;
  margin-bottom: 12px;
  border-bottom: 2px solid #42b983;
  padding-bottom: 4px;
  color: #333;
}
.article-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.article-list li {
  padding: 12px;
  border-bottom: 1px solid #e0e0e0;
}
.article-title {
  font-weight: 500;
  margin: 0;
  color: #007bff;
}
.article-meta {
  font-size: 12px;
  color: #777;
  margin: 4px 0 0;
}
.article-link {
  display: block;
  text-decoration: none;
}
.article-link:hover .article-title {
  text-decoration: underline;
}

.no-articles {
  text-align: center;
  color: #777;
  padding: 20px 0;
}
</style>