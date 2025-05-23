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
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 64px;
  background: linear-gradient(90deg, #f7fcfa 0%, #e6f9f1 100%);
  box-shadow: 0 2px 16px rgba(66, 185, 131, 0.08);
  border-bottom: 2px solid #42b98322;
}

body, #app {
  padding-top: 64px;
}

.logo {
  font-size: 2rem;
  font-weight: 800;
  color: #42b983;
  cursor: pointer;
  letter-spacing: 2px;
  margin: 0;
  text-shadow: 0 2px 8px #42b98322;
  transition: color 0.2s, text-shadow 0.2s;
}
.logo:hover {
  color: #2e8c6a;
  text-shadow: 0 4px 16px #42b98344;
}

.nav {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-auth {
  display: flex;
  align-items: center;
  margin-left: auto;
}

.nav-link {
  position: relative;
  padding: 8px 18px;
  font-size: 1.05rem;
  color: #222;
  background: none;
  border: none;
  outline: none;
  cursor: pointer;
  text-decoration: none;
  border-radius: 22px;
  transition: color 0.2s, background 0.2s;
  font-weight: 500;
  overflow: hidden;
}

.nav-link::after {
  content: "";
  display: block;
  position: absolute;
  left: 50%;
  bottom: 7px;
  width: 0;
  height: 3px;
  background: linear-gradient(90deg, #42b983 60%, #5eead4 100%);
  border-radius: 2px;
  transition: width 0.3s cubic-bezier(.4,0,.2,1), left 0.3s cubic-bezier(.4,0,.2,1);
}

.nav-link:hover,
.nav-link:focus {
  color: #42b983;
  background: #e6f9f1;
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
  background: #e6f9f1;
  transition: color 0.2s, background 0.2s;
}

.nav-link:active {
  transform: scale(0.97);
  transition: transform 0.1s;
}

.nav-btn {
  background: linear-gradient(90deg, #42b983 60%, #5eead4 100%);
  color: #fff !important;
  border: none;
  font: inherit;
  cursor: pointer;
  padding: 8px 22px;
  border-radius: 22px;
  margin-left: 8px;
  font-weight: 600;
  box-shadow: 0 2px 8px #42b98322;
  transition: background 0.2s, color 0.2s;
}
.nav-btn:hover,
.nav-btn:focus {
  background: linear-gradient(90deg, #2e8c6a 60%, #42b983 100%);
  color: #fff !important;
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