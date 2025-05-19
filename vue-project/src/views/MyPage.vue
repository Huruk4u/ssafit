<template>
  <Header />

  <div class="mypage-container">
    <section class="user-info">
      <p><strong>{{ user.nickname }}</strong> ({{ user.userName }})</p>
      <p>대표 뱃지: {{ representBadge }}</p>
      <div>보유 뱃지: {{ badges }}</div>
    </section>

    <div class="tabs">
      <button @click="activeTab = 1" :class="{ active: activeTab === 1 }">활동 정보</button>
      <button @click="activeTab = 2" :class="{ active: activeTab === 2 }">빈 탭</button>
      <button @click="activeTab = 3" :class="{ active: activeTab === 3 }">내가 쓴 글</button>
    </div>

    <div v-if="activeTab === 1" class="tab-content">
      <p>현재 연속일: {{ currentStreak }}일</p>
      <p>최대 연속일: {{ longestStreak }}일</p>
      <!-- 향후 streakCalendar 표시 가능 -->
    </div>

    <div v-if="activeTab === 2" class="tab-content">
      <p>추후 콘텐츠 추가 예정</p>
    </div>

    <div v-if="activeTab === 3" class="tab-content">
    <div v-if="myArticles.length === 0">
        <p>작성한 글이 없습니다.</p>
    </div>
    <ul v-else>
        <li v-for="article in myArticles" :key="article.articleId">
        <router-link :to="`/board/detail/${article.articleId}`" class="article-link">
            <strong>{{ article.title }}</strong>
        </router-link>
        <span> - {{ article.createdAt }}</span>
        </li>
    </ul>
    </div>
  </div>
</template>

<script setup>
    import {ref, onMounted, computed} from 'vue'
    import {useRouter} from 'vue-router'
    import api from '@/api/axiosInstance'

    // 하위 컴포넌트
    import StreakCalendarVue from '../components/StreakCalendar.vue'
    import Header from '@/components/Header.vue'
    import UserProfile from '@/components/UserProfile.vue'
    import ChallengeRegister from '@/components/ChallengeRegister.vue'
    import UserEdit from '@/components/UserEdit.vue'

    const router = useRouter()

    const token = ref(localStorage.getItem('token'))
    const user = ref(null)
    const rawUser = localStorage.getItem('user')    
    if (rawUser) {
        try {
            user.value = JSON.parse(rawUser)
        } catch(e) {
            console.error('user 파싱 실패:', e)
            router.push('/login')
        }
    }

    const activeTab = ref(1)
    
    const isLoading = ref(true)
    const currentStreak = ref('')
    const longestStreak = ref('')
    const streakCalendar = ref('')
    const badges = ref('')
    const representBadge = ref('')
    const myArticles = ref([])
    
    onMounted(() => {
    api.get('/api_mypage/profile')
        .then(res => {
        currentStreak.value = res.data.currentStreak
        longestStreak.value = res.data.longestStreak
        streakCalendar.value = res.data.streakCalendar
        badges.value = res.data.badges
        representBadge.value = res.data.representBadge
        })
        .catch(err => {
        console.error('유저 정보 불러오기 실패:', err)
        })

    if (user.value?.userId) {
        api.get(`/api_article/get/user_id/${user.value.userId}`)
        .then(res => {
            myArticles.value = res.data
        })
        .catch(err => {
            console.error('내 글 목록 불러오기 실패:', err)
        })
        .finally(() => {
            isLoading.value = false
        })
    
    }
})
        

</script>

<style scoped>
    .mypage-container {
    padding: 20px;
    }
    .user-info {
    background-color: #f4f4f4;
    padding: 15px;
    margin-bottom: 20px;
    }
    .tabs button {
    margin-right: 10px;
    padding: 8px 16px;
    cursor: pointer;
    }
    .tabs .active {
    background-color: #007bff;
    color: white;
    }
    .tab-content {
    margin-top: 20px;
    }
</style>