// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Signup from '../views/Signup.vue';
import Main from '../views/Main.vue';
import MyPage from '../views/MyPage.vue';
import Board from '../views/Board.vue';
import BoardDetail from '../views/BoardDetail.vue';
import BoardCreate from '../views/BoardCreate.vue';
import BoardEdit from '../views/BoardEdit.vue';
import Notification from '../views/Notification.vue';
import EditPassword from '@/views/EditPassword.vue';

const routes = [
  { path: '/', redirect: '/login' },    // 기본 경로는 로그인 페이지로 리디렉션
  { path: '/login', component: Login},
  { path: '/signup', component: Signup},
  { path: '/main', component: Main},
  { path: '/mypage', component: MyPage},
  { path: '/board', component: Board},
  { path: '/board', component: Board},
  { path: '/board/create', component: BoardCreate},
  { path: '/board/detail/:articleId', component: BoardDetail, props: true },
  { path: '/board/edit/:articleId', component: BoardEdit, props: true },
  { path: '/notification', component: Notification },
  { path: '/editPassword', component: EditPassword}
]

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const token=localStorage.getItem('token')
  const publicPages = ['/', '/api_auth/authenticate', '/api_auth/signup']
  
  const authRequired = !publicPages.includes(to.path)
  if (authRequired && !token &&to.path !== '/login') {
    return next('/login')
  }

  next()
})

export default router;