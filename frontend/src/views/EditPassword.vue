<template>
    <div>
        <form @submit.prevent="updatePassword">
            현재 비밀번호 : <input v-model="currentPassword" type="password"><br>
            변경할 비밀번호 : <input v-model="newPassword" type="password"><br>
            변경할 비밀번호 확인 : <input v-model="checkNewPassword" type="password"><br>
            <input type="submit">
        </form>
    </div>
</template>

<script setup>
    import {ref} from 'vue'
    import api from '@/api/axiosInstance'
    import {useRouter} from 'vue-router'

    const router = useRouter()

    const rawUser = localStorage.getItem('user')
    const user = rawUser ? JSON.parse(rawUser) : null
    const userName = user?.userName || ''

    const currentPassword = ref('')
    const newPassword = ref('')
    const checkNewPassword = ref('')

    const updatePassword = () => {
        if (!currentPassword.value || !newPassword.value || !checkNewPassword.value) {
            alert("모든 정보를 입력해야합니다.")
            return
        }
        if (newPassword.value !== checkNewPassword.value) {
            alert("새 비밀번호가 일치하지 않습니다.")
            return
        }

        api.put(`/api_user/put/password/userName/${userName}`, {
            currentPassword: currentPassword.value,
            newPassword: newPassword.value,
            checkNewPassword: checkNewPassword.value
        })
        .then(() => {
            alert("비밀번호가 변경되었습니다.")
            router.replace('/mypage')
        })
        .catch(() => {
            alert("비밀번호 변경 실패. 입력 조건을 잘 확인해주세요.")
        })
    }
</script>

<style lang="scss" scoped>

</style>