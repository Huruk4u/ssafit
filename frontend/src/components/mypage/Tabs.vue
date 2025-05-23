<template>
  <div class="badge-modal-overlay" @click="$emit('close')">
    <div class="badge-modal" @click.stop>
      <h3>대표 뱃지 선택</h3>
      <div class="badges-grid">
        <div
          v-for="badge in badges"
          :key="badge.badgeId"
          class="badge-item"
          :class="{ represented: badge.isRepresented }"
          @click="$emit('select', badge)"
        >
          <img :src="badge.iconUrl" :alt="badge.name" class="badge-icon" />
          <p class="badge-name">{{ badge.name }}</p>
          <p class="badge-description">{{ badge.description }}</p>
          <p class="badge-earned">획득일: {{ formatDate(badge.earnedAt) }}</p>
        </div>
      </div>
      <div class="modal-actions d-flex justify-content-end gap-2">
        <button @click="$emit('close')" class="btn btn-secondary">
          취소
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  badges: Array,
  formatDate: Function,
});
defineEmits(['close', 'select']);
</script>

<style scoped>
.badge-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}
.badge-modal {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  width: 90%;
  max-width: 900px;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}
.badge-modal h3 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}
.badges-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
  margin-bottom: 20px;
}
.badge-item {
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s ease;
}
.badge-item:hover {
  border-color: #42b983;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.12);
}
.badge-item.represented {
  border: 2px solid #42b983;
  background: linear-gradient(to bottom, #f7fcfa, #fff);
}
.badge-item img {
  width: 80px;
  height: 80px;
  margin-bottom: 8px;
  border-radius: 50%;
  object-fit: contain;
  background: #f8f9fa;
  border: 2px solid #eee;
  box-shadow: 0 2px 8px rgba(66,185,131,0.08);
}
.badge-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: #333;
  margin: 4px 0;
  line-height: 1.2;
}
.badge-description {
  font-size: 0.8rem;
  color: #666;
  margin: 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.badge-earned {
  font-size: 0.75rem;
  color: #888;
  margin-top: 6px;
}
.modal-actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #eee;
}
.modal-actions button {
  padding: 6px 16px;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
}
</style>