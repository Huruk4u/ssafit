<!-- src/components/MyPage/MyPostsTab.vue -->
<template>
  <div class="my-posts">
    <h3>내가 쓴 글</h3>

    <!-- 검색 바 -->
    <div class="search-bar">
      <select v-model="searchType">
        <option value="title">제목</option>
        <option value="content">내용</option>
      </select>
      <input
        v-model="searchQuery"
        :placeholder="searchType === 'title' ? '제목 검색' : '내용 검색'"
      />
      <button @click="clearSearch">초기화</button>
    </div>

    <!-- 글 리스트 -->
    <ul class="post-list">
      <li v-for="post in filteredPosts" :key="post.id" class="post-card">
        <div class="post-info">
          <router-link
            class="post-title"
            :to="{ name: 'PostDetail', params: { id: post.id } }"
          >
            {{ post.title }}
          </router-link>
          <div class="meta">
            <span class="date">{{ post.date }}</span>
            <span class="category">{{ post.categoryLabel }}</span>
          </div>
        </div>
        <div class="post-actions">
          <button @click="editPost(post.id)">수정</button>
          <button @click="deletePost(post.id)">삭제</button>
        </div>
      </li>
    </ul>

    <p v-if="!filteredPosts.length" class="empty-state">
      작성된 글이 없습니다.
    </p>
  </div>
</template>

<script>
export default {
  name: 'MyPostsTab',
  data() {
    return {
      posts: [
        // 예시 데이터 (실제 API 호출 결과를 사용하세요)
        {
          id: 1,
          title: '첫 번째 글',
          content: '내용 예시',
          date: '2025-05-15',
          category: 'info',
          categoryLabel: '정보'
        },
        {
          id: 2,
          title: '운동 후기',
          content: '운동 후 느낌 공유',
          date: '2025-05-16',
          category: 'question',
          categoryLabel: '질문'
        },
        {
          id: 3,
          title: '식단 공유',
          content: '내 식단 계획',
          date: '2025-05-17',
          category: 'video',
          categoryLabel: '영상'
        }
      ],
      searchType: 'title',
      searchQuery: ''
    }
  },
  computed: {
    filteredPosts() {
      return this.posts.filter(post => {
        const field = this.searchType === 'title' ? post.title : post.content
        return field.includes(this.searchQuery)
      })
    }
  },
  methods: {
    clearSearch() {
      this.searchQuery = ''
    },
    editPost(id) {
      this.$router.push({ name: 'PostEdit', params: { id } })
    },
    deletePost(id) {
      if (confirm('정말 삭제하시겠습니까?')) {
        // 실제 API 호출 후 목록 갱신
        this.posts = this.posts.filter(p => p.id !== id)
      }
    }
  }
}
</script>

<style scoped>
.my-posts {
  max-width: 700px;
  margin: 0 auto;
  padding: 1rem;
}

.search-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}
.search-bar select,
.search-bar input,
.search-bar button {
  padding: 6px 10px;
  font-size: 0.9rem;
}

.post-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.post-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 12px;
  transition: box-shadow 0.2s;
}
.post-card:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.post-info {
  flex: 1;
}
.post-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  text-decoration: none;
}
.post-title:hover {
  text-decoration: underline;
}
.meta {
  margin-top: 4px;
  font-size: 0.85rem;
  color: #666;
}
.meta .date {
  margin-right: 12px;
}
.meta .category {
  background: #eef;
  padding: 2px 6px;
  border-radius: 4px;
}

.post-actions button {
  margin-left: 8px;
  padding: 4px 10px;
  font-size: 0.9rem;
  border: 1px solid #aaa;
  background: #fff;
  cursor: pointer;
  border-radius: 4px;
}
.post-actions button:hover {
  background: #f4f4f4;
}

.empty-state {
  text-align: center;
  color: #888;
  margin-top: 20px;
}
</style>
