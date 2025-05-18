<!-- src/components/Notifications.vue -->
<template>
  <div class="notifications">
    <Navbar />

    <h2>알림</h2>
    <ul>
      <li v-for="note in notifications" :key="note.id">
        <span>{{ note.message }}</span>
        <small>{{ note.date }}</small>
      </li>
    </ul>
    <button v-if="notifications.length" @click="markAllRead">
      모두 읽음 처리
    </button>
    <p v-else>새로운 알림이 없습니다.</p>
  </div>
</template>

<script>
import Navbar from '@/components/common/Navbar.vue'
export default {
  name: 'Notifications',
  components: { Navbar },
  data() {
    return {
      notifications: []
    }
  },
  created() {
    // 실제 구현: API 호출로 알림 데이터 불러오기
    // axios.get('/api/notifications').then(res => this.notifications = res.data)
    this.notifications = [
      { id: 1, message: '새 댓글이 달렸습니다.', date: '2025-05-18 10:30' },
      { id: 2, message: '좋아요를 받았습니다.', date: '2025-05-18 09:15' }
    ]
  },
  methods: {
    markAllRead() {
      // 실제 구현: API 호출 후
      this.notifications = []
    }
  }
}
</script>

<style>
.notifications {
  max-width: 600px;
  margin: auto;
}
.notifications ul {
  list-style: none;
  padding: 0;
}
.notifications li {
  padding: 10px;
  border-bottom: 1px solid #ddd;
}
.notifications li small {
  display: block;
  color: #888;
  margin-top: 4px;
}
.notifications button {
  margin-top: 12px;
  padding: 6px 12px;
}
</style>