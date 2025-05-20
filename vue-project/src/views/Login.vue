<!-- src/views/Login.vue -->
<template>
  <div class="auth-container">
    <h2>로그인</h2>
    <form @submit.prevent="login">
      <div class="form-group">
        <label>아이디</label>
        <input v-model="username" type="text" required />
      </div>
      <div class="form-group">
        <label>비밀번호</label>
        <input v-model="password" type="password" required />
      </div>
      <button type="submit">로그인</button>
    </form>
    <p>
      계정이 없으신가요?
      <router-link to="/signup">회원가입</router-link>
    </p>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import api from '@/api/axiosInstance'
import {useRouter} from 'vue-router'

const username = ref("")
const password = ref("")

const router = useRouter()

const login = () => {
  api.post("/api_auth/authenticate", {
    username: username.value,
    password: password.value
  }).then(res => {
    console.log(res)
    console.log(res.data.user)
    
    const token = res.data.token
    const user = res.data.user

    localStorage.setItem("token", token)
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`
    
    localStorage.setItem("user", JSON.stringify(user))
    console.log("user", user)
    
    router.push("/mypage")

  }).catch(err => {
    alert("로그인 실패")
    console.error(err)
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