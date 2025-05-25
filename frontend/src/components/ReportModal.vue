<template>
  <div v-if="show" class="modal-overlay">
    <div class="modal-content">
      <h4>유저 신고하기</h4>
      <p>
        <b>{{ target?.nickname }}</b>님을 신고합니다.<br>
        신고 사유를 선택하세요.
      </p>
      <select v-model="localCategory">
        <option value="" disabled>신고 사유 선택</option>
        <option v-for="category in categories" :key="category" :value="category">{{ category }}</option>
      </select>
      <textarea
        v-model="localContent"
        placeholder="신고 상세 내용을 입력하세요."
        style="width:100%;margin-top:12px;min-height:60px;resize:vertical;"
      ></textarea>
      <div class="modal-actions" style="margin-top:16px;">
        <button @click="onSubmit">신고</button>
        <button @click="$emit('close')">취소</button>
      </div>
    </div>
  </div>
</template>

<script setup>
// Pinia userStore는 이 컴포넌트에서 직접적으로 필요하지 않습니다.
// 신고 대상, 사유, 내용 등은 props/emit으로 처리하므로 userStore import 불필요합니다.
import { ref, watch } from 'vue';

const props = defineProps({
  show: Boolean,
  target: Object,
  categories: Array,
  category: String,
  content: String,
});
const emit = defineEmits(['submit', 'close']);

const localCategory = ref(props.category || '');
const localContent = ref(props.content || '');

watch(() => props.category, val => localCategory.value = val);
watch(() => props.content, val => localContent.value = val);

const onSubmit = () => {
  if (!localCategory.value) {
    alert('신고 사유를 선택하세요.');
    return;
  }
  emit('submit', {
    category: localCategory.value,
    content: localContent.value,
  });
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}
.modal-content {
  background: #fff;
  border-radius: 8px;
  padding: 32px 24px;
  min-width: 280px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.18);
  text-align: center;
}
.modal-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}
.modal-actions button {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  background: #42b983;
  color: #fff;
  cursor: pointer;
  font-size: 1em;
}
.modal-actions button:last-child {
  background: #aaa;
}
</style>