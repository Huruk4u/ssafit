<template>
  <div class="pagination">
    <button 
      class="pagination-control"
      :disabled="currentPage <= 1" 
      @click="$emit('go', 1)"
    >&laquo;&laquo;</button>
    <button 
      class="pagination-control"
      :disabled="currentPage <= 1" 
      @click="$emit('go', currentPage - 1)"
    >&laquo;</button>
    <template v-for="page in pageNumbers" :key="page">
      <button 
        class="page-number" 
        :class="{ 'current-page': currentPage === page }"
        @click="$emit('go', page)"
      >{{ page }}</button>
    </template>
    <button 
      class="pagination-control"
      :disabled="currentPage >= totalPages" 
      @click="$emit('go', currentPage + 1)"
    >&raquo;</button>
    <button 
      class="pagination-control"
      :disabled="currentPage >= totalPages" 
      @click="$emit('go', totalPages)"
    >&raquo;&raquo;</button>
  </div>
</template>

<script setup>
defineProps(['currentPage', 'totalPages', 'pageNumbers']);
defineEmits(['go']);
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  gap: 5px;
}
.pagination-control,
.page-number {
  padding: 8px 14px;
  border: 1.5px solid #e9ecef;
  background: #fff;
  cursor: pointer;
  border-radius: 12px;
  min-width: 40px;
  font-size: 1rem;
  color: #42b983;
  font-weight: 600;
  transition: background 0.2s, color 0.2s, border 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.pagination-control:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.page-number.current-page {
  background: linear-gradient(135deg, #42b983, #369870);
  color: #fff;
  border-color: #42b983;
}
.pagination button:hover:not(:disabled):not(.current-page) {
  background: #f5f5f5;
  color: #369870;
}
</style>