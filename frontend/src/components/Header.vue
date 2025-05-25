<template>
  <div>
    <header class="header">
      <h1 class="logo" @click="goHome">SSAFIT</h1>
      <nav class="nav">
        <router-link to="/mypage" class="nav-link" active-class="active-link">홈</router-link>
        <router-link to="/board" class="nav-link" active-class="active-link">게시판</router-link>
        <router-link
          to="/notification"
          class="nav-link"
          active-class="active-link"
          :class="{ 'has-unread': unreadCount > 0 }"
        >
          알림
          <span
            v-if="unreadCount > 0"
            class="dot"
          ></span>
        </router-link>
        <router-link
          v-if="user && user.role === 'ROLE_ADMIN'"
          to="/admin"
          class="nav-link"
          active-class="active-link"
          :class="{ 'has-unread': adminUnreadCount > 0 }"
        >
          관리자 페이지
          <span v-if="adminUnreadCount > 0" class="dot"></span>
        </router-link>
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
        >로그인</router-link>
      </div>
    </header>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import api from "@/api/axiosInstance";

const router = useRouter();

const isLogin = ref(!!localStorage.getItem("token"));
const user = ref(localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null);

// 일반 알림
const notifications = ref([]);
const unreadCount = computed(() =>
  notifications.value.filter(n => n.isRead === false).length
);

// 관리자 알림
const adminNotifications = ref([]);
const adminUnreadCount = computed(() =>
  adminNotifications.value.filter(r => !r.isHandled).length
);

function syncAuthState() {
  isLogin.value = !!localStorage.getItem("token");
  user.value = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null;
  fetchNotifications();
  fetchAdminNotifications();
}

function handleAuthChanged() {
  syncAuthState();
}

async function fetchNotifications() {
  if (!isLogin.value) return;
  try {
    const res = await api.get("/api_notification/list");
    notifications.value = res.data;
  } catch (err) {
    console.error("알림 목록 불러오기 실패", err);
  }
}

async function fetchAdminNotifications() {
  if (!isLogin.value || !user.value || user.value.role !== "ROLE_ADMIN") return;
  try {
    const res = await api.get("/api_admin/get/report/notHandled");
    adminNotifications.value = res.data;
  } catch (err) {
    console.error("관리자 알림 목록 불러오기 실패", err);
  }
}

onMounted(() => {
  window.addEventListener("auth-changed", handleAuthChanged);
  window.addEventListener("storage", handleAuthChanged);
  fetchNotifications();
  fetchAdminNotifications();
});

onUnmounted(() => {
  window.removeEventListener("auth-changed", handleAuthChanged);
  window.removeEventListener("storage", handleAuthChanged);
});

const logout = () => {
  api.post("/api_auth/logout").then(() => {
    localStorage.clear();
    syncAuthState();
    window.dispatchEvent(new Event("auth-changed"));
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

.dot {
  display: inline-block;
  width: 9px;
  height: 9px;
  margin-left: 6px;
  background: #e53935;
  border-radius: 50%;
  vertical-align: middle;
  box-shadow: 0 1px 4px #e5393533;
}

.badge {
  display: inline-block;
  min-width: 18px;
  padding: 0 6px;
  margin-left: 6px;
  font-size: 0.85em;
  background: #e53935;
  color: #fff;
  border-radius: 12px;
  font-weight: bold;
  vertical-align: top;
  line-height: 18px;
  text-align: center;
  box-shadow: 0 1px 4px #e5393533;
}

.has-unread {
  color: #e53935 !important;
  font-weight: 700;
}

.nav-link.has-unread {
  color: #e53935 !important;
  font-weight: 700 !important;
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