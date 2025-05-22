<template>
  <div>
    <!--제출이 일어나면, 여기서 login이라는 이벤트를 동작시킨다.-->
    <form @submit.prevent="login">
      <input v-model="username" type="text" placeholder="아이디" /> <br />
      <input v-model="password" type="password" placeholder="비밀번호" />
      <input type="submit" value="로그인" />
    </form>
  </div>
</template>

<script setup>
// vue 반응형 변수 설정 및, axios 바인딩을 위한 패키지 로드
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from '@/api/axiosInstance'

const username = ref("");
const password = ref("");
const router = useRouter();

// login함수 선언
const login = () => {
  api.post("/api_auth/authenticate", {
    username: username.value,
    password: password.value
  }).then((res) => {
    const token = res.data.token;
    localStorage.setItem("token", token);
    router.push("/mypage");
  }).catch((err) => {
    // 원래 이 단계에서 오류를 캐치해야하는데, 일단 다음으로 넘어간다.
    alert("로그인 실패");
    console.error(err);
  });
}

</script>

<style scoped>

</style>
