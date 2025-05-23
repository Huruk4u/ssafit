<template>
  <div class="tabs d-flex flex-wrap gap-2 mb-4 fixed-tabs">
    <button
      @click="$emit('change', 1)"
      :class="['btn', activeTab === 1 ? 'btn-primary' : 'btn-outline-primary']"
    >
      활동 정보
    </button>
    <button
      @click="$emit('change', 2)"
      :class="['btn', activeTab === 2 ? 'btn-primary' : 'btn-outline-primary']"
    >
      챌린지
    </button>
    <button
      @click="$emit('change', 3)"
      :class="['btn', activeTab === 3 ? 'btn-primary' : 'btn-outline-primary']"
    >
      내가 쓴 글
    </button>
    <button
      @click="$emit('change', 4)"
      :class="['btn', activeTab === 4 ? 'btn-primary' : 'btn-outline-primary']"
    >
      좋아요 한 글
    </button>
    <button
      @click="$emit('change', 5)"
      :class="['btn', activeTab === 5 ? 'btn-primary' : 'btn-outline-primary']"
    >
      팔로우
    </button>
    <button @click="$emit('edit-user')" class="btn btn-secondary ms-auto">
      유저 정보 변경
    </button>
  </div>
</template>

<script setup>
defineProps({
  activeTab: Number,
});
defineEmits(['change', 'edit-user']);
</script>

<style scoped>
.tabs {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.5rem;
  margin-bottom: 2rem;
  border-bottom: 1px solid #eee;
  background: #fafbfc;
}
.tabs button {
  position: relative;
  background: none;
  border: none;
  padding: 0.75rem 1.25rem;
  font-size: 0.95rem;
  font-weight: 500;
  color: #666;
  transition: all 0.3s ease;
  cursor: pointer;
}
.tabs button::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #42b983;
  transform: scaleX(0);
  transition: transform 0.3s ease;
}
.tabs button:hover {
  color: #42b983;
}
.tabs button.btn-primary {
  background: none;
  color: #42b983;
  font-weight: 600;
}
.tabs button.btn-primary::after {
  transform: scaleX(1);
}
.tabs button.btn-secondary {
  margin-left: auto;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 0.5rem 1rem;
}
.tabs button.btn-secondary:hover {
  background: #e9ecef;
  border-color: #ced4da;
  color: #495057;
}
@media (max-width: 900px) {
  .fixed-tabs {
    top: 210px;
    border-radius: 0 0 8px 8px;
  }
}
@media (max-width: 700px) {
  .fixed-tabs {
    top: 170px;
    border-radius: 0 0 6px 6px;
    padding-bottom: 0.2rem;
  }
}
@media (max-width: 500px) {
  .fixed-tabs {
    top: 120px;
    border-radius: 0 0 4px 4px;
    padding-bottom: 0.1rem;
  }
}
</style>