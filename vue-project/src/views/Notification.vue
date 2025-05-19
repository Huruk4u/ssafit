<template>
    <div>
        유저 알림 처리      
    </div>
</template>

<script setup>
    import {ref, onMounted} from 'vue'
    import {useRouter} from 'vue-router'
    import api from '@/api/axiosInstance'

    const user = JSON.parse(localStorage.getItem('user') || {})
    const notificationList = ref([])

    onMounted(() => {
        api.get('/api_notification/list')
        .then(res => {
            console.log(res.data)
            notificationList.value = res.data
        })
        .catch(err => {
            console.error(err)
            alert("알림 목록을 가져오는데 실패했습니다.")
        })
    })

</script>

<style scoped>

</style>