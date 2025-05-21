// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Signup from '@/views/Signup.vue';
import Main from '../views/Main.vue';
import MyPage from '../views/MyPage.vue';
import Board from '../views/Board.vue';
import BoardDetail from '../views/BoardDetail.vue';
import BoardCreate from '../views/BoardCreate.vue';
import BoardEdit from '../views/BoardEdit.vue';
import Notification from '../views/Notification.vue';
import EditPassword from '@/views/EditPassword.vue';
import UserEdit from '@/views/UserEdit.vue';
import AdminPage from '@/views/AdminPage.vue';
import MyArticles from '@/components/MyArticles.vue';
import MyLikedArticles from '@/components/MyLikedArticles.vue';
import ActivityInfo from '@/components/ActivityInfo.vue';

const routes = [
  { path: '/', redirect: '/login' },    // 기본 경로는 로그인 페이지로 리디렉션
  { path: '/login', component: Login},
  { path: '/signup', component: Signup},
  { path: '/main', component: Main},
  { path: '/mypage', component: MyPage},
  { path: '/board', component: Board},
  { path: '/board/create', component: BoardCreate},
  { path: '/board/detail/:articleId', component: BoardDetail, props: true },
  { path: '/board/edit/:articleId', component: BoardEdit, props: true },
  { path: '/notification', component: Notification },
  { path: '/editPassword', component: EditPassword},
  { path: '/editProfile', component: UserEdit},
  { path: '/admin', component: AdminPage},
  { path: '/myarticles', component: MyArticles},
  { path: '/mylikedarticles', component: MyLikedArticles},
  { path: '/activityInfo', component: ActivityInfo}
]

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const user = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : null;
  const token = localStorage.getItem('token');
  const publicPages = ['/signup', '/login'];
  const authRequired = !publicPages.includes(to.path);

  // 인증 필요시 토큰 없으면 로그인으로
  if (authRequired && !token && to.path !== '/login') {
    return next('/login');
  }

  console.log('user', user);

  // 게시판 관련 경로 접근 시 정지기간 체크 (suspendStart ~ suspendEnd)
  if (
    to.path.startsWith('/board') &&
    user &&
    user.suspendStart &&
    user.suspendEnd
  ) {
    const now = new Date();
    const start = new Date(user.suspendStart);
    const end = new Date(user.suspendEnd);
    if (start <= now && now < end) {
      alert('정지 기간 중에는 게시판을 이용할 수 없습니다.');
      return next('/mypage');
    }
  }

  next();
});

export default router;