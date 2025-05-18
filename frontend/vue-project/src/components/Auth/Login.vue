<!-- Login.vue -->
<template>
  <div class="login">
    <h2>로그인</h2>
    <input v-model="userName" placeholder="아이디" />
    <input v-model="password" type="password" placeholder="비밀번호" />
    <button @click="login">로그인</button>
    <p>
      계정이 없으신가요?
      <router-link to="/signup">회원가입</router-link>
    </p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userName: '',
      password: ''
    }
  },
  methods: {
    login() {
      // 실제 구현: API 호출로 로그인 검증
      if (this.userName && this.password) {
        // 로그인 성공 시 토큰 저장 (예시)
        localStorage.setItem('jwt_token', 'dummy-token')
        this.$router.push({ name: 'MyPage' })
      } else {
        alert('아이디와 비밀번호를 입력하세요.')
      }
    }
  }
}
</script>

<style>
/* 기본 CSS 스타일 예시 */
.login {
  max-width: 300px;
  margin: auto;
}
.login input {
  display: block;
  width: 100%;
  margin: 8px 0;
}
</style>
