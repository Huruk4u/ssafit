<template>
  <div id="app" class="app-container">
    <Header />
    <router-view />
  </div>
</template>

<script>
import Header from "@/components/Header.vue"; // Header 컴포넌트 import

export default {
  name: "App",
  components: {
    Header, // 등록
  },
};
</script>

<style>
body {
  font-family: "Pretendard Variable", "Pretendard", "Noto Sans KR", Arial,
    sans-serif;
  margin: 0;
  padding: 0;
}

.app-container {
  padding-top: 64px;
}
</style>