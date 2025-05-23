<template>
  <div class="search-container">
    <div class="tag-filter">
      <label for="tag">부위:</label>
      <select id="tag" v-model="searchCondition.tagLabel">
        <option value="">전체</option>
        <option>상체</option>
        <option>하체</option>
        <option>코어</option>
        <option>유산소</option>
        <option>등</option>
        <option>가슴</option>
        <option>어깨</option>
        <option>팔</option>
        <option>다리</option>
        <option>전신</option>
        <option>복부</option>
      </select>
    </div>
    <div class="search-filter">
      <select v-model="searchCondition.key">
        <option value="title">제목</option>
        <option value="content">내용</option>
        <option value="nickname">작성자</option>
      </select>
      <input
        type="text"
        v-model="searchCondition.word"
        placeholder="검색어를 입력하세요"
        @keyup.enter="$emit('search')"
      />
      <button @click="$emit('search')">검색</button>
    </div>
    <div class="sort-options">
      <select v-model="searchCondition.orderBy" @change="$emit('search')">
        <option value="created_at">작성일</option>
        <option value="view_count">조회수</option>
        <option value="like_count">좋아요수</option>
      </select>
      <select v-model="searchCondition.orderByDir" @change="$emit('search')">
        <option value="desc">내림차순</option>
        <option value="asc">오름차순</option>
      </select>
    </div>
  </div>
</template>

<script setup>
defineProps(['searchCondition']);
defineEmits(['search']);
</script>

<style scoped>
.search-container {
  display: flex;
  gap: 18px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}
.tag-filter,
.search-filter,
.sort-options {
  display: flex;
  align-items: center;
  gap: 8px;
}
select,
input[type="text"] {
  padding: 8px 12px;
  border: 1.5px solid #e9ecef;
  border-radius: 12px;
  font-size: 1rem;
  background: #fff;
  transition: border 0.2s;
}
select:focus,
input[type="text"]:focus {
  border-color: #42b983;
  outline: none;
}
.search-filter input {
  width: 180px;
}
.search-filter button {
  padding: 8px 18px;
  background: linear-gradient(135deg, #42b983, #369870);
  color: #fff;
  border: none;
  border-radius: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
  margin-left: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.search-filter button:hover {
  background: #369870;
}
@media (max-width: 600px) {
  .search-container { flex-direction: column; gap: 10px; }
}
</style>