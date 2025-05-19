<template>
    <Header />
    <div>
        {{token}}
        <br>
        {{user.userName}}
        {{user.nickname}}
        <br>
        
        {{currentStreak}}<br>
        {{longestStreak}}<br>
        <!-- {{streakCalendar}}<br> -->
        {{badges}}<br>
        {{representBadge}}
    </div>
</template>

<script setup>
    import {ref, onMounted} from 'vue'
    import {useRouter} from 'vue-router'
    import Header from '@/components/Header.vue'
    import api from '@/api/axiosInstance'
    import StreakCalendarVue from '../components/StreakCalendar.vue'

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
    
    const isLoading = ref(true)
    const currentStreak = ref('')
    const longestStreak = ref('')
    const streakCalendar = ref('')
    const badges = ref('')
    const representBadge = ref('')
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
            console.error('유저 정보 불리오기 실패 : ', err)
        })
        .finally(() => {
            isLoading.value = false
        })
    })

</script>

<style scoped>

</style>