<!-- src/components/Board/PostEdit.vue -->
<template>
  <div class="post-edit">
    <Navbar />

    <h2>글 수정</h2>
    <form @submit.prevent="updatePost">
      <div>
        <label>제목</label>
        <input v-model="form.title" required />
      </div>
      <div>
        <label>내용</label>
        <textarea v-model="form.content" rows="8" required></textarea>
      </div>
      <div>
        <label>태그 (콤마로 구분)</label>
        <input v-model="form.tags" placeholder="예: 가슴,등,하체" />
      </div>
      <button type="submit">저장</button>
      <button type="button" @click="cancel">취소</button>
    </form>
  </div>
</template>

<script>
import Navbar from '@/components/common/Navbar.vue'
export default {
  name: 'PostEdit',
  components: { Navbar },
  data() {
    return {
      form: {
        title: '',
        content: '',
        tags: ''
      }
    }
  },
  created() {
    const id = this.$route.params.id
    // 실제 구현: API 호출해서 기존 데이터 불러오기
    // axios.get(`/api/post/${id}`).then(res => { this.form = res.data })
    // 예시 세팅:
    this.form = {
      title: '기존 제목 예시',
      content: '기존 본문 예시',
      tags: '가슴,등'
    }
  },
  methods: {
    updatePost() {
      const id = this.$route.params.id
      // 실제 구현: axios.put(`/api/post/${id}`, this.form)
      console.log('update', id, this.form)
      this.$router.push({ name: 'PostDetail', params: { id } })
    },
    cancel() {
      this.$router.back()
    }
  }
}
</script>

<style>
.post-edit {
  max-width: 600px;
  margin: auto;
}
.post-edit form > div {
  margin-bottom: 12px;
}
.post-edit input,
.post-edit textarea {
  width: 100%;
  padding: 6px;
}
.post-edit button {
  margin-right: 8px;
  padding: 6px 12px;
}
</style>