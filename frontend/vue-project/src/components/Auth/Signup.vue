<!-- Signup.vue -->
<template>
  <div class="signup">
    <h2>회원가입</h2>
    <input v-model="userName" placeholder="아이디" />
    <input v-model="password" type="password" placeholder="비밀번호" />
    <input v-model="checkPassword" type="password" placeholder="비밀번호 확인" />
    <input v-model="nickname" placeholder="닉네임" />
    <input v-model="email" type="email" placeholder="이메일" />
    <button @click="signup">가입하기</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userName: '',
      password: '',
      checkPassword: '',
      nickname: '',
      email: ''
    }
  },
  methods: {
    validateEmail(email) {
      const re = /\S+@\S+\.\S+/
      return re.test(email)
    },
    signup() {
      if (!this.userName || !this.password || !this.nickname || !this.email) {
        alert('모든 항목을 입력하세요.')
        return
      }
      if (this.password !== this.checkPassword) {
        alert('비밀번호가 일치하지 않습니다.')
        return
      }
      if (!this.validateEmail(this.email)) {
        alert('유효한 이메일을 입력하세요.')
        return
      }
      // 실제 구현: 회원가입 API 호출
      alert('회원가입이 완료되었습니다.')
      this.$router.push({ name: 'Login' })
    }
  }
}
</script>

<style>
.signup {
  max-width: 400px;
  margin: auto;
}
.signup input {
  display: block;
  width: 100%;
  margin: 6px 0;
}
</style>