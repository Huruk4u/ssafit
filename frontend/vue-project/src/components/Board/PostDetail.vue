<!-- src/components/Board/PostDetail.vue -->
<template>
  <div class="post-detail">
    <Navbar />

    <div class="post-content">
      <h2>{{ post.title }}</h2>
      <p>{{ post.content }}</p>
    </div>

    <!-- 글 액션 -->
    <div class="post-actions">
      <button @click="likePost">👍 {{ post.likes }}</button>
      <button @click="dislikePost">👎 {{ post.dislikes }}</button>
      <button @click="editPost">수정</button>
      <button @click="deletePost">삭제</button>
      <button @click="reportPost">신고</button>
    </div>
    <hr />

    <!-- 댓글 섹션 -->
    <div class="comments">
      <h3>댓글</h3>
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="comment-item"
      >
        <div class="comment-main">
          <span>{{ comment.text }}</span>
          <div class="comment-info">
            <button @click="likeComment(comment)">👍 {{ comment.likes }}</button>
            <button @click="dislikeComment(comment)">👎 {{ comment.dislikes }}</button>
          </div>
        </div>
        <div class="comment-actions">
          <button @click="editComment(comment)">수정</button>
          <button @click="deleteComment(comment)">삭제</button>
          <button @click="reportComment(comment)">신고</button>
        </div>
      </div>

      <!-- 댓글 입력 -->
      <div class="comment-input">
        <input v-model="newComment" placeholder="댓글을 입력하세요" />
        <button @click="addComment">작성</button>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from '@/components/common/Navbar.vue'

export default {
  name: 'PostDetail',
  components: { Navbar },
  data() {
    return {
      post: {
        id: 1,
        title: '게시글 제목 예시',
        content: '게시글 본문 예시가 여기에 표시됩니다.',
        likes: 10,
        dislikes: 2
      },
      comments: [
        { id: 1, text: '좋은 정보 감사합니다', likes: 3, dislikes: 0 },
        { id: 2, text: '화이팅!', likes: 1, dislikes: 0 }
      ],
      newComment: ''
    }
  },
  methods: {
    likePost() { this.post.likes++ },
    dislikePost() { this.post.dislikes++ },
    editPost() { this.$router.push({ name: 'PostEdit', params: { id: this.post.id } }) },
    deletePost() {
      if (confirm('정말 이 글을 삭제하시겠습니까?')) {
        // API 호출 후 라우팅
        this.$router.push({ name: 'Board', params: { category: this.$route.params.category } })
      }
    },
    reportPost() { alert('게시글이 신고되었습니다.') },

    likeComment(c) { c.likes++ },
    dislikeComment(c) { c.dislikes++ },
    editComment(c) { /* 댓글 수정 폼 띄우기 */ },
    deleteComment(c) {
      if (confirm('댓글을 삭제하시겠습니까?')) {
        this.comments = this.comments.filter(x => x.id !== c.id)
      }
    },
    reportComment(c) { alert('댓글이 신고되었습니다.') },

    addComment() {
      if (!this.newComment) return
      this.comments.push({ id: Date.now(), text: this.newComment, likes: 0, dislikes: 0 })
      this.newComment = ''
    }
  }
}
</script>

<style>
.post-detail {
  max-width: 800px;
  margin: auto;
}
.post-actions button {
  margin-right: 8px;
  padding: 6px 12px;
}
.comments {
  margin-top: 20px;
}
.comment-item {
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #ddd;
  padding: 8px 0;
}
.comment-main {
  flex: 1;
}
.comment-info button {
  margin-left: 4px;
}
.comment-actions button {
  margin-left: 6px;
  padding: 4px 8px;
}
.comment-input {
  margin-top: 10px;
  display: flex;
}
.comment-input input {
  flex: 1;
  padding: 6px;
  margin-right: 6px;
}
</style>
