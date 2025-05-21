<!-- src/components/UserBadge.vue -->
<template>
  <div class="user-badge">
    <span class="initial">{{ initial }}</span>
  </div>
</template>

<script>
export default {
  name: 'UserBadge',
  props: {
    user: { type: Object, required: true }
  },
  computed: {
    // 사용자 이름의 첫 글자를 이니셜로 사용
    initial() {
      return this.user.name.charAt(0).toUpperCase();
    }
  }
};
</script>

<style scoped>
.user-badge {
  width: 50px;
  height: 50px;
  background-color: #c0e4ff;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 10px;
}
.initial {
  font-size: 24px;
  font-weight: bold;
  color: #ffffff;
}
</style>