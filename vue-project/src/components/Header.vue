<template>
    <header class="header">
        <h1 @click="goHome">SSAFIT</h1>
    </header>
    <nav>
        <router-link to="/mypage">홈</router-link>
        <router-link to="/board">게시판</router-link>
        <router-link to="/notification">알림</router-link>
        <button v-if="isLogin" @click="logout">로그아웃</button>
        <router-link v-else to="/login">로그인</router-link>
    </nav>

</template>

<script setup>
    import {computed} from 'vue'
    import {useRouter} from 'vue-router'

    const router = useRouter()

    const isLogin = computed(() => !!localStorage.getItem('token'))

    // user 로그아웃
    const logout = () => {
        localStorage.clear()
        router.push('/login')
    }

    const goHome = () => {
        router.push('/mypage')
    }

</script>

<style scoped>
    .header {
        display: flex;
        justify-content: space-between;
        padding: 12px;
        background: #42b983;
        color: white;
        }
    nav a, nav button {
        margin-left: 12px;
        color: black;
        text-decoration: none;
    }
</style>