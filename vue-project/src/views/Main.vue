<!-- src/views/Main.vue -->
<template>
  <div class="main-container">
    <h2>{{ user.name }}님의 챌린지 현황</h2>
    <!-- UserBadge 컴포넌트로 사용자 배지 표시 -->
    <UserBadge :user="user" />
    <p>현재 연속 스트릭: <strong>{{ user.streakDays }}일</strong></p>
    <!-- StreakCalendar 컴포넌트로 달력 형태 표시 -->
    <StreakCalendar :days="streakCalendar" :streakDays="user.streakDays" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import UserBadge from '../components/UserBadge.vue'
import StreakCalendar from '../components/StreakCalendar.vue'

// 하드코딩된 사용자 정보 예시
const user = ref({
  id: 1,
  name: '홍길동',
  streakDays: 15
})

// 한 달(30일) 간격의 날짜 배열 예시
const streakCalendar = ref(Array.from({ length: 30 }, (_, i) => i + 1))
</script>

<style scoped>
.main-container {
  padding: 20px;
  text-align: center;
}
.main-container h2 {
  margin-bottom: 10px;
}
.main-container p {
  margin: 15px 0;
  font-size: 18px;
}
</style>