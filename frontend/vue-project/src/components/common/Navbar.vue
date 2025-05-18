<template>
  <nav class="navbar">
    <!-- 로고 혹은 브랜드 -->
    <span class="brand" @click="goMyPage">SSAFIT</span>

    <!-- 인증된 경우 네비게이션 메뉴 -->
    <div v-if="isAuthenticated" class="nav-links">
      <router-link to="/mypage/basic" class="nav-item">메인페이지</router-link>
      <router-link to="/board/video" class="nav-item">게시판</router-link>
      <router-link to="/notifications" class="nav-item">알림</router-link>
      <button class="nav-item logout-btn" @click="handleLogout">로그아웃</button>
    </div>

    <!-- 인증되지 않은 경우 로그인/회원가입 링크 -->
    <div v-else class="nav-links">
      <router-link to="/login" class="nav-item">로그인</router-link>
      <router-link to="/signup" class="nav-item">회원가입</router-link>
    </div>
  </nav>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import ApiService from '@/services/api'

export default {
  name: 'Navbar',
  setup() {
    const router = useRouter()
    const isAuthenticated = computed(() => !!localStorage.getItem('jwt_token'))

    const goMyPage = () => {
      router.push({ name: 'MyPage' })
    }

    const handleLogout = async () => {
      try {
        await ApiService.logout()
      } catch (e) {
        console.warn('logout error', e)
      }
      ApiService.removeToken()
      router.push({ name: 'Login' })
    }

    return {
      isAuthenticated,
      goMyPage,
      handleLogout
    }
  }
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: #f5f5f5;
  border-bottom: 1px solid #ddd;
}

.brand {
  font-size: 1.5rem;
  font-weight: bold;
  cursor: pointer;
}

.nav-links {
  display: flex;
  align-items: center;
}

.nav-item {
  margin-left: 1rem;
  text-decoration: none;
  color: #333;
  font-weight: 500;
}

.nav-item:hover {
  text-decoration: underline;
}

.logout-btn {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}

.logout-btn:hover {
  opacity: 0.9;
}
</style>
