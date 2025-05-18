<!-- src/components/Board/PostList.vue -->
<template>
  <div class="post-list">
    <div
      v-for="post in posts"
      :key="post.id"
      class="post-item"
    >
      <div class="post-main">
        <router-link
          class="post-title"
          :to="{ name: 'PostDetail', params: { id: post.id } }"
        >
          {{ post.title }}
        </router-link>
        <div class="post-meta">
          <span class="author">작성자: {{ post.author }}</span>
          <span>👍 {{ post.likes }}</span>
          <span>👎 {{ post.dislikes }}</span>
          <span>💬 {{ post.commentCount }}</span>
          <span>👁️ {{ post.views }}</span>
        </div>
      </div>
      <div class="post-actions">
        <button @click="editPost(post.id)">수정</button>
        <button @click="deletePost(post.id)">삭제</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PostList',
  props: ['category', 'tag', 'search', 'sortKey'],
  data() {
    return {
      posts: [
        // 예시 데이터: 실제 API 결과를 할당하세요
        {
          id: 1,
          title: '가슴 운동 루틴 공유',
          author: '헬스고수',
          likes: 5,
          dislikes: 0,
          commentCount: 2,
          views: 20
        },
        {
          id: 2,
          title: '등 근육 스트레칭 팁',
          author: '운동매니아',
          likes: 3,
          dislikes: 1,
          commentCount: 5,
          views: 35
        }
      ]
    }
  },
  methods: {
    editPost(id) {
      this.$router.push({ name: 'PostEdit', params: { id } })
    },
    deletePost(id) {
      if (confirm('정말 이 글을 삭제하시겠습니까?')) {
        // 실제 삭제 API 호출
        this.posts = this.posts.filter(p => p.id !== id)
      }
    }
  }
}
</script>

<style scoped>
.post-list {
  max-width: 800px;
  margin: auto;
}
.post-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ddd;
  padding: 12px 0;
}
.post-main {
  flex: 1;
}
.post-title {
  font-size: 1.1rem;
  font-weight: bold;
  text-decoration: none;
  color: #333;
}
.post-title:hover {
  text-decoration: underline;
}
.post-meta {
  margin-top: 4px;
  font-size: 0.9rem;
  color: #666;
}
.post-meta .author {
  margin-right: 12px;
}
.post-actions button {
  margin-left: 8px;
  padding: 4px 8px;
}
</style>