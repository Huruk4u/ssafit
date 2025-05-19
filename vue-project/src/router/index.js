// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Signup from '../views/Signup.vue';
import Main from '../views/Main.vue';
import MyPage from '../views/MyPage.vue';
import Board from '../components/Header.vue';
import Notification from '../views/Notification.vue';

const routes = [
  { path: '/', redirect: '/login' },    // 기본 경로는 로그인 페이지로 리디렉션
  { path: '/login', component: Login},
  { path: '/signup', component: Signup},
  { path: '/main', component: Main},
  { path: '/mypage', component: MyPage},
  { path: '/board', component: Board},
  { path: '/notification', comopnent: Notification }
]

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;