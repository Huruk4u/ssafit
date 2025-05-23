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
  width: 350px;
  margin: 80px auto 0 auto;
  padding: 36px 32px 28px 32px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 6px 32px rgba(66,185,131,0.10), 0 1.5px 6px rgba(66,185,131,0.07);
  display: flex;
  flex-direction: column;
  align-items: center;
}
h2 {
  margin-bottom: 28px;
  color: #42b983;
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: 0.02em;
}
form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.form-group {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
label {
  font-size: 1rem;
  color: #369870;
  font-weight: 600;
  margin-bottom: 2px;
}
input {
  width: 100%;
  padding: 10px 14px;
  border: 2px solid #b2dfdb;
  border-radius: 18px;
  font-size: 1.05rem;
  background: #f8f9fa;
  transition: border 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(66,185,131,0.07);
}
input:focus {
  border-color: #42b983;
  outline: none;
  background: #fff;
  box-shadow: 0 0 0 3px #b2f2e5, 0 2px 8px rgba(66,185,131,0.13);
}
button[type="submit"], button {
  width: 100%;
  padding: 12px 0;
  background: linear-gradient(135deg, #42b983 60%, #b2f2e5 100%);
  color: #fff;
  border: none;
  border-radius: 18px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  margin-top: 8px;
  box-shadow: 0 2px 12px rgba(66,185,131,0.13);
  transition: background 0.2s, box-shadow 0.2s;
  border: 2.5px solid #b2dfdb;
}
button[type="submit"]:hover, button:hover {
  background: linear-gradient(135deg, #369870 60%, #42b983 100%);
  box-shadow: 0 4px 18px rgba(66,185,131,0.18);
  border-color: #42b983;
}
p {
  text-align: center;
  margin-top: 18px;
  color: #888;
  font-size: 0.98rem;
}
a, .router-link-active {
  color: #42b983;
  text-decoration: underline;
  font-weight: 600;
  transition: color 0.2s;
}
a:hover, .router-link-active:hover {
  color: #369870;
}
@media (max-width: 500px) {
  .auth-container {
    width: 98vw;
    min-width: 0;
    padding: 18px 4vw 18px 4vw;
  }
  h2 {
    font-size: 1.3rem;
  }
}
</style>