// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/components/Auth/Login.vue'
import Signup from '@/components/Auth/Signup.vue'
import MyPage from '@/components/MyPage/MyPage.vue'
import BasicTab from '@/components/MyPage/BasicTab.vue'
import RegisterChallengeTab from '@/components/MyPage/RegisterChallengeTab.vue'
import MyPostsTab from '@/components/MyPage/MyPostsTab.vue'
import BoardView from '@/components/Board/BoardView.vue'
import PostDetail from '@/components/Board/PostDetail.vue'
import PostEdit from '@/components/Board/PostEdit.vue'
import Notifications from '@/components/Notifications.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/signup',
    name: 'Signup',
    component: Signup
  },
  {
    path: '/mypage',
    name: 'MyPage',
    component: MyPage,
    children: [
      { path: '', redirect: 'basic' },
      { path: 'basic', name: 'BasicTab', component: BasicTab },
      { path: 'register', name: 'RegisterTab', component: RegisterChallengeTab },
      { path: 'myposts', name: 'MyPostsTab', component: MyPostsTab }
    ]
  },
  {
    path: '/board/:category',
    name: 'Board',
    component: BoardView,
    props: true
  },
  {
    path: '/post/:id',
    name: 'PostDetail',
    component: PostDetail,
    props: true
  },
  {
    path: '/post/:id/edit',
    name: 'PostEdit',
    component: PostEdit,
    props: true
  },
  {
    path: '/notifications',
    name: 'Notifications',
    component: Notifications
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 전역 네비게이션 가드: 로그인 여부에 따른 리디렉션 처리
router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('jwt_token')  // Navbar와 동일 키 사용
  if (!isLoggedIn && to.name !== 'Login' && to.name !== 'Signup') {
    next({ name: 'Login' })
  } else if (isLoggedIn && (to.name === 'Login' || to.name === 'Signup')) {
    next({ name: 'MyPage' })
  } else {
    next()
  }
})

export default router