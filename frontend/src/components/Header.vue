<template>
  <div>
    <header class="header">
      <h1 class="logo" @click="goHome">SSAFIT</h1>
      <nav class="nav">
        <router-link to="/mypage" class="nav-link" active-class="active-link"
          >홈</router-link
        >
        <router-link to="/board" class="nav-link" active-class="active-link"
          >게시판</router-link
        >
        <router-link
          to="/notification"
          class="nav-link"
          active-class="active-link"
          >알림</router-link
        >
        <router-link
          v-if="user.role === 'ROLE_ADMIN'"
          to="/admin"
          class="nav-link"
          active-class="active-link"
          >관리자 페이지</router-link
        >
      </nav>
      <div class="nav-auth">
        <button v-if="isLogin" class="nav-link nav-btn" @click="logout">
          로그아웃
        </button>
        <router-link
          v-else
          to="/login"
          class="nav-link"
          active-class="active-link"
          >로그인</router-link
        >
      </div>
    </header>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import api from "@/api/axiosInstance";

const router = useRouter();

const isLogin = computed(() => !!localStorage.getItem("token"));
const user = computed(() => {
  const rawUser = localStorage.getItem("user");
  return rawUser ? JSON.parse(rawUser) : null;
});
// user 로그아웃
const logout = () => {
  api.post("/api_auth/logout").then(() => {
    localStorage.clear();
    router.push("/login").catch((err) => {
      console.error("로그아웃 실패", err);
      alert("로그아웃 중 문제가 발생했습니다.");
    });
  });
};

const goHome = () => {
  router.push("/mypage");
};
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 64px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(60, 60, 60, 0.06);
}

.logo {
  font-size: 2rem;
  font-weight: 700;
  color: #42b983;
  cursor: pointer;
  letter-spacing: 2px;
  margin: 0;
}

.nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-auth {
  display: flex;
  align-items: center;
  margin-left: auto;
}

.nav-link {
  position: relative;
  padding: 8px 16px;
  font-size: 1rem;
  color: #222;
  background: none;
  border: none;
  outline: none;
  cursor: pointer;
  text-decoration: none;
  transition: color 0.2s;
  /* 강조선 애니메이션 준비 */
  overflow: hidden;
}

.nav-link::after {
  content: "";
  display: block;
  position: absolute;
  left: 50%;
  bottom: 0;
  width: 0;
  height: 3px;
  background: #42b983;
  border-radius: 2px;
  transition: width 0.3s cubic-bezier(.4,0,.2,1), left 0.3s cubic-bezier(.4,0,.2,1);
}

.nav-link:hover,
.nav-link:focus {
  color: #42b983;
}

.nav-link:hover::after,
.nav-link:focus::after,
.active-link::after,
.nav-link.router-link-exact-active::after {
  width: 60%;
  left: 20%;
}

.active-link,
.nav-link.router-link-exact-active {
  color: #42b983;
  transition: color 0.2s;
}

/* 버튼 클릭시 약간의 scale 효과 */
.nav-link:active {
  transform: scale(0.96);
  transition: transform 0.1s;
}

.nav-btn {
  background: none;
  border: none;
  font: inherit;
  cursor: pointer;
  padding: 8px 16px;
}

.nav-btn:active,
.nav-btn:focus {
  color: #42b983;
}

@media (max-width: 600px) {
  .header {
    flex-direction: column;
    height: auto;
    padding: 12px;
  }
  .nav,
  .nav-auth {
    flex-wrap: wrap;
    gap: 4px;
  }
  .nav-auth {
    margin-left: 0;
    margin-top: 8px;
  }
}
</style>