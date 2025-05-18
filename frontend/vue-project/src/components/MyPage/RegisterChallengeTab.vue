<!-- src/components/MyPage/RegisterChallengeTab.vue -->
<template>
  <div class="register-challenge">
    <h3 class="title">챌린지 등록</h3>

    <div class="card upload-card">
      <label class="upload-label" for="inbody">인바디 사진 업로드</label>
      <div class="upload-controls">
        <input id="inbody" type="file" @change="onSelectInbody" />
        <button :disabled="!inbodyFile" @click="uploadImage">
          제출
        </button>
      </div>
    </div>

    <div v-if="recommendedParts.length" class="card recommend-card">
      <h4 class="subtitle">추천 운동 부위</h4>
      <div class="parts-buttons">
        <button
          v-for="part in recommendedParts"
          :key="part"
          class="part-btn"
          @click="goToTagBoard(part)"
        >
          {{ part }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RegisterChallengeTab',
  data() {
    return {
      inbodyFile: null,
      recommendedParts: ['가슴', '하체', '어깨'] // 실제 API 데이터로 교체
    }
  },
  methods: {
    onSelectInbody(e) {
      this.inbodyFile = e.target.files[0] || null
    },
    uploadImage() {
      if (!this.inbodyFile) return
      // API 호출로 업로드 처리
      console.log('인바디 사진 업로드:', this.inbodyFile.name)
      this.inbodyFile = null
    },
    goToTagBoard(part) {
      this.$router.push({ name: 'Board', query: { tag: part } })
    }
  }
}
</script>

<style scoped>
.register-challenge {
  max-width: 600px;
  margin: 0 auto;
  padding: 1rem;
  font-family: sans-serif;
}
.title {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  text-align: center;
}

/* 카드 컨테이너 공통 */
.card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
}

/* 업로드 카드 */
.upload-card .upload-label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}
.upload-controls {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}
.upload-controls input[type="file"] {
  flex: 1;
}
.upload-controls button {
  padding: 0.5rem 1rem;
  border: none;
  background: #007bff;
  color: #fff;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}
.upload-controls button:disabled {
  background: #aaa;
  cursor: not-allowed;
}
.upload-controls button:not(:disabled):hover {
  background: #0056b3;
}

/* 추천 부위 카드 */
.recommend-card .subtitle {
  font-size: 1.25rem;
  margin-bottom: 0.75rem;
}
.parts-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}
.part-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #007bff;
  background: #fff;
  color: #007bff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}
.part-btn:hover {
  background: #007bff;
  color: #fff;
}
</style>