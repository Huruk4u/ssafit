<!-- src/components/MyPage/MyPage.vue -->
<template>
  <div class="mypage">
    <!-- 공통 네비게이션 바 -->
    <Navbar />

    <!-- 프로필 영역 -->
    <div class="profile-header">
      <div class="background-wrap">
        <img class="background-img" src="@/assets/bg.jpg" alt="배경사진" />
      </div>
      <img class="avatar" src="@/assets/bg.jpg" alt="프로필사진" />
      <button class="edit-btn" @click="goEditProfile">프로필 수정</button>
    </div>

    <!-- 하단 탭 -->
    <div class="tabs">
      <router-link to="/mypage/basic" active-class="active-tab">기본</router-link>
      <router-link to="/mypage/register" active-class="active-tab">챌린지 등록</router-link>
      <router-link to="/mypage/myposts" active-class="active-tab">내가 쓴 글</router-link>
    </div>

    <!-- 탭 내용 -->
    <router-view />
  </div>
</template>

<script>
import Navbar from '@/components/common/Navbar.vue'

export default {
  name: 'MyPage',
  components: { Navbar },
  mounted() {
    // /mypage만 접속 시 기본 탭으로 리다이렉트
    if (this.$route.path === '/mypage') {
      this.$router.replace({ name: 'BasicTab' })
    }
  },
  methods: {
    goEditProfile() {
      // 프로필 수정 페이지로 이동 (별도 라우트 필요)
      this.$router.push({ name: 'ProfileEdit' })
    },
    logout() {
      // 토큰 삭제 후 로그인 페이지로 이동
      localStorage.removeItem('jwt_token')
      this.$router.push({ name: 'Login' })
    }
  }
}
</script>

<style scoped>
.profile-header {
  text-align: center;
  position: relative;
}
.background-wrap {
  width: 100%;
  height: 200px;
  overflow: hidden;
}
.background-img {
  width: 100%;
  height: auto;
}
.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-top: -50px;
  border: 3px solid white;
}
.edit-btn {
  display: block;
  margin: 10px auto;
}
.tabs {
  margin-top: 20px;
  display: flex;
  justify-content: space-around;
  border-bottom: 1px solid #ccc;
}
.tabs a {
  text-decoration: none;
  padding: 8px 16px;
  color: #555;
}
.active-tab {
  border-bottom: 2px solid #333;
  color: #333;
}
</style>