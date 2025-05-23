<template>
  <div class="category-tabs">
    <button
      :class="{ active: modelValue === 'video' }"
      @click="$emit('update:modelValue', 'video')"
    >영상</button>
    <button
      :class="{ active: modelValue === 'question' }"
      @click="$emit('update:modelValue', 'question')"
    >질문</button>
    <button
      :class="{ active: modelValue === 'info' }"
      @click="$emit('update:modelValue', 'info')"
    >정보</button>
  </div>
</template>

<script setup>
defineProps(['modelValue']);
defineEmits(['update:modelValue']);
</script>

<style scoped>
.category-tabs {
  display: flex;
  gap: 10px;
}
.category-tabs button {
  padding: 8px 20px;
  border: none;
  background: #fff;
  color: #42b983;
  font-weight: 600;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(66,185,131,0.08);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 6px;
}
.category-tabs button.active {
  background: linear-gradient(135deg, #42b983, #369870);
  color: #fff;
  box-shadow: 0 4px 16px rgba(66,185,131,0.15);
}
.category-tabs button:hover:not(.active) {
  background: #f2f2f2;
}
</style>