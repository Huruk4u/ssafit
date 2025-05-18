<!-- src/components/Board/BoardView.vue -->
<template>
  <div class="board-view">
    <Navbar />

    <!-- 카테고리 서브 탭 (영상, 정보, 질문) 그대로 -->

    <!-- 태그 선택 · 검색 유형 선택 · 검색어 입력 · 정렬 -->
    <div class="filter-bar">
      <!-- 운동 부위 태그 선택 -->
      <select v-model="selectedTag">
        <option value="">전체 태그</option>
        <option v-for="tag in allTags" :key="tag" :value="tag">
          {{ tag }}
        </option>
      </select>

      <!-- 검색 유형 선택 -->
      <select v-model="searchType">
        <option value="author">작성자</option>
        <option value="title">제목</option>
        <option value="content">내용</option>
      </select>

      <!-- 검색어 입력 -->
      <input
        v-model="searchQuery"
        type="text"
        :placeholder="`“${searchTypeLabel}” 검색`"
      />

      <!-- 정렬 -->
      <select v-model="sortKey">
        <option value="recent">최신순</option>
        <option value="likes">좋아요순</option>
        <option value="views">조회순</option>
      </select>

      <button @click="fetchPosts">검색</button>
    </div>

    <!-- 글 목록 -->
    <PostList
      :category="category"
      :tag="selectedTag"
      :search-type="searchType"
      :search-query="searchQuery"
      :sort-key="sortKey"
    />
  </div>
</template>

<script>
import Navbar from '@/components/common/Navbar.vue'
import PostList from './PostList.vue'

export default {
  name: 'BoardView',
  components: { Navbar, PostList },
  props: ['category'],
  data() {
    return {
      allTags: ['가슴', '등', '하체', '어깨', '팔', '코어'],
      selectedTag: '',
      // 검색 유형: author / title / content
      searchType: 'title',
      searchQuery: '',
      sortKey: 'recent'
    }
  },
  computed: {
    searchTypeLabel() {
      const map = { author: '작성자', title: '제목', content: '내용' }
      return map[this.searchType] || '검색'
    }
  },
  watch: {
    category() {
      this.fetchPosts()
    }
  },
  created() {
    this.fetchPosts()
  },
  methods: {
    fetchPosts() {
      // 실제 구현 시, API에 아래 파라미터를 전달하세요.
      console.log('fetchPosts →', {
        category: this.category,
        tag: this.selectedTag,
        searchType: this.searchType,
        searchQuery: this.searchQuery,
        sort: this.sortKey
      })
      // axios.get('/api/posts', {
      //   params: {
      //     category: this.category,
      //     tag: this.selectedTag,
      //     [`${this.searchType}`]: this.searchQuery,
      //     sort: this.sortKey
      //   }
      // }).then(res => { this.posts = res.data })
    }
  }
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
.filter-bar select,
.filter-bar input,
.filter-bar button {
  padding: 6px;
}
.filter-bar input {
  width: 200px;
}
</style>