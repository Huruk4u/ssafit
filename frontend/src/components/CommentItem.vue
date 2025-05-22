<!-- filepath: c:\Users\sungm\Desktop\Spring\ssafit\frontend\src\components\CommentItem.vue -->
<template>
  <li>
    <div class="comment-meta">
      <span
        class="author-name-wrapper"
        @click="onToggleMenu"
      >
        {{ comment.nickname || comment.username }}
        <span class="dropdown-arrow">▼</span>
        <div
          v-if="showMenu"
          class="user-menu"
          @click.stop
        >
          <ul>
            <li @click="onViewUserInfo">유저 정보 보기</li>
            <li v-if="userId !== comment.userId" @click="onReportUser">유저 신고하기</li>
          </ul>
        </div>
      </span>
      <span>{{ formatDate(comment.createdAt) }}</span>
    </div>
    <div class="comment-content" v-if="!isEditing">{{ comment.content }}</div>
    <textarea
      :value="editContent"
      @input="$emit('update:editContent', $event.target.value)"
      v-if="isEditing"
    />
    <div class="comment-actions">
      <button @click="$emit('like', comment)">좋아요 ({{ comment.likeCount }})</button>
      <button @click="$emit('dislike', comment)">싫어요 ({{ comment.dislikeCount }})</button>
      <button v-if="isCommentAuthor && !isEditing" @click="$emit('edit', comment)">수정</button>
      <button v-if="isEditing" @click="$emit('confirmEdit', comment, editContent)">저장</button>
      <button v-if="isCommentAuthor" @click="$emit('delete', comment.commentId)">삭제</button>
    </div>
  </li>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  comment: Object,
  isEditing: Boolean,
  isCommentAuthor: Boolean,
  editContent: String,
  showMenu: Boolean,
  formatDate: Function,
  userId: Number, // 부모에서 로그인 유저 id 넘겨주기
});
const emit = defineEmits([
  'toggleMenu',
  'viewUserInfo',
  'reportUser',
  'like',
  'dislike',
  'edit',
  'confirmEdit',
  'delete',
  'update:editContent'
]);

const onToggleMenu = (e) => {
  emit('toggleMenu', props.comment.commentId, e);
};
const onViewUserInfo = () => {
  emit('viewUserInfo', props.comment);
};
const onReportUser = () => {
  emit('reportUser', props.comment);
};

</script>

<style scoped>
.author-name-wrapper {
  cursor: pointer;
  color: #2d8cf0;
  position: relative;
  user-select: none;
  display: inline-block;
}
.dropdown-arrow {
  font-size: 10px;
  margin-left: 2px;
}
.user-menu {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  min-width: 140px;
  position: absolute;
  left: 0;
  top: 100%;
  margin-top: 4px;
  z-index: 1000;
  padding: 0;
}
.user-menu ul {
  list-style: none;
  margin: 0;
  padding: 0;
}
.user-menu li {
  padding: 10px 18px;
  cursor: pointer;
  transition: background 0.15s;
}
.user-menu li:hover {
  background: #f5f7fa;
}
</style>