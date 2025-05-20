<template>
  <div></div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { ref, onMounted } from "vue";
import api from "@/api/axiosInstance";

const user = ref(
  localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null
);
const router = useRouter();

const reportList = ref([]);

onMounted(() => {
  if (!user.value.role !== "ROLE_ADMIN") {
    router.push("/mypage");
    return;
  }
  api
    .get("/api_admin/get/report")
    .then((res) => {
      reportList.value = res.data;
    })
    .catch((err) => {
      console.error("Error fetching report list:", err);
      alert("신고 목록을 가져오는 중 오류가 발생했습니다.");
    });
});
</script>

<style scoped>
</style>