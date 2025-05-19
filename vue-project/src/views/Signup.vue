<!-- src/views/Signup.vue -->
<template>
  <div class="auth-container">
    <h2>회원가입</h2>
    <form @submit.prevent="signUp">
      <div class="form-group">
        <label>아이디</label>
        <input v-model="username" type="text" required />
      </div>
      <div class="form-group">
        <label>이메일</label>
        <input v-model="email" type="email" required />
      </div>
      <div class="form-group">
        <label>비밀번호</label>
        <input v-model="password" type="password" required />
      </div>
      <div class="form-group">
        <label>비밀번호 확인</label>
        <input v-model="checkPassword" type="password" required>
      </div>
      <div class="form-group">
        <label>유저 닉네임</label>
        <input v-model="nickname" type="text" required>
      </div>
      <div class="form-group">
        <label></label>
      </div>
      <button type="submit">회원가입</button>
    </form>
    <p>
      이미 계정이 있으신가요?
      <router-link to="/login">로그인</router-link>
    </p>
  </div>
</template>

<script setup>
  import {ref} from 'vue'
  import {useRouter} from 'vue-router'
  import api from '@/api/axiosInstance'

  const username = ref('')
  const email = ref('')
  const password = ref('')
  const checkPassword = ref('')
  const nickname = ref('')

  const router = useRouter()

  const signUp = () => {
    if(password.value !== checkPassword.value) {
      alert('비밀번호가 일치하지 않습니다.')
      return
    }

    const signUpData = {
      userName: username.value,
      email: email.value,
      password: password.value,
      checkPassword: checkPassword.value,
      nickname: nickname.value
    }

    api.post('/api_user/post/signup', signUpData)
      .then(() => {
        alert('회원가입 성공')
        router.push('/login')
      })
      .catch(err => {
        alert('회원가입 실패')
        console.error(err)
        router.push('/signUp')
      })
  }


</script>

<style scoped>
.auth-container {
  width: 320px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
}
.form-group {
  margin-bottom: 15px;
}
label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}
input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}
button {
  width: 100%;
  padding: 10px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover {
  background-color: #369f6b;
}
p {
  text-align: center;
  margin-top: 15px;
}
</style>