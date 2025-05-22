<!-- src/components/StreakCalendar.vue -->
<template>
  <div class="calendar-grid">
    <div
      v-for="day in days"
      :key="day"
      :class="['calendar-cell', { active: day <= streakDays }]"
    >
      {{ day }}
    </div>
  </div>
</template>

<script>
export default {
  name: 'StreakCalendar',
  props: {
    days: { type: Array, required: true },
    streakDays: { type: Number, required: true }
  }
};
</script>

<style scoped>
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 30px);
  gap: 5px;
  justify-content: center;
}
.calendar-cell {
  width: 30px;
  height: 30px;
  background-color: #f0f0f0;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 12px;
  border-radius: 4px;
  color: #666666;
}
.calendar-cell.active {
  background-color: #42b983;
  color: #ffffff;
}
</style>