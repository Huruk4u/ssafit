// main.js
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

createApp(App)
  .use(router)      // Vue Router를 플러그인으로 추가
  .mount('#app')    // index.html의 <div id="app">에 마운트