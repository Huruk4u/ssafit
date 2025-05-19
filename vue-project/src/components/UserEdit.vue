<template>
    <div>
        <form @submit="update">
            <label>유저 닉네임</label>
            <input v-model=nickname type="text" value="{{ user.nickname }}">
            <label>유저 이메일</label>
            <input v-model="email" type="text" value="{{ user.email }}">
        </form>
        <router-link to="/editPassword">비밀번호 변경하기</router-link>
    </div>
</template>

<script setup>
    import {ref} from 'vue'
    import api from '@/api/axiosInstance'
    import {useRouter} from 'vue-router'

    const router = useRouter()

    const rawUser = localStorage.getItem('user')
    const user = rawUser ? JSON.parse(rawUser) : {nickname: '', email: ''}

    const nickname = ref(user.nickname);
    const email = ref(user.email);

    const update = () => {
        api.put(`/api_user/put/userInfo/userName/${user.userName}`, {
            username: user.username,
            nickname: nickname.value,
            email: email.value
        })
        .then(() => {
            alert('정보가 수정되었습니다.')
            const updatedUser = {
                ...user,
                nickname: nickname.value,
                email: email.value
            }
            localStorage.setItem('user', JSON.stringify(updatedUser))
        })
        .catch(err => {
            console.error('정보 수정 실패 : ', err)
            alert('수정에 실패했습니다.')
        })
    }


</script>

<style scoped>

</style>