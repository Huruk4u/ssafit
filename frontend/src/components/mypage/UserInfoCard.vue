<template>
  <section class="user-info card p-4 fixed-user-info">
    <div class="user-visual position-relative mb-5">
      <img
        :src="userBackgroundImage"
        alt="배경이미지"
        class="background-img card-img-top"
      />
      <div class="profile-row-wrap">
        <img
          :src="userProfileImage"
          alt="프로필이미지"
          class="profile-img-overlap rounded-circle border border-white shadow"
        />
        <div class="profile-meta-side">
          <div class="nickname-badge-row">
            <div class="nickname-id-col">
              <span class="user-nickname">{{ user.nickname }}</span>
              <span class="user-id">@{{ user.userName }}</span>
            </div>
            <div
              class="represent-badge-inline-large"
              @click="$emit('open-badge-modal')"
              title="대표 뱃지 변경"
            >
              <img
                v-if="representBadge"
                :src="representBadge.iconUrl"
                :alt="representBadge.name"
                class="badge-icon-inline-large"
              />
              <span v-if="representBadge" class="badge-name-inline-large">
                {{ representBadge.name }}
              </span>
              <span v-else class="badge-name-inline-large" style="color:#bbb;">
                대표 뱃지가 없습니다
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="badge-section mt-3"></div>
  </section>
</template>

<script setup>
defineProps({
  user: Object,
  representBadge: Object,
  userProfileImage: String,
  userBackgroundImage: String,
});
defineEmits(['open-badge-modal']);
</script>

<style scoped>
.user-info {
  background-color: #f4f4f4;
  padding: 15px;
  margin-bottom: 20px;
  border: none;
  border-radius: 0;
  box-shadow: none;
  z-index: 100;
}
.fixed-user-info {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #f4f4f4;
  margin-bottom: 0;
  border-radius: 0 0 16px 16px;
  box-shadow: 0 2px 12px rgba(66, 185, 131, 0.07);
}
.user-visual {
  position: relative;
  width: 100%;
  height: 700px;
  margin-bottom: 60px;
}
.background-img {
  width: 100%;
  height: 700px;
  object-fit: cover;
  border-radius: 12px;
  display: block;
}
.profile-row-wrap {
  position: absolute;
  left: 5%;
  bottom: -50px;
  display: flex;
  flex-direction: row;
  align-items: flex-end;
  z-index: 2;
}
.profile-img-overlap {
  width: 110px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  background: #fff;
}
.profile-meta-side {
  position: absolute;
  left: 120px;
  bottom: -20px;
  min-width: 260px;
  background: rgba(255,255,255,0.92);
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.07);
  padding: 10px 22px 10px 18px;
  display: flex;
  flex-direction: row; 
  align-items: center;  
  justify-content: flex-start;
  height: 90px; 
  border: 1.5px solid #42b983;
  transition: border-color 0.2s;
}
.nickname-badge-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 18px;
}
.nickname-id-col {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-right: 10px;
}
.user-nickname {
  font-size: 1.35rem;
  font-weight: 700;
  color: #222;
  margin: 0 0 2px 0;
  line-height: 1.1;
  white-space: nowrap;
}
.user-id {
  display: block;
  font-size: 1rem;
  color: #888;
  font-weight: 500;
  margin: 0;
  line-height: 1.1;
  white-space: nowrap;
}
.represent-badge-inline-large {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  background: none;
  border: none;
  padding: 0;
}
.badge-icon-inline-large {
  width: 80px;   
  height: 80px;  
  border-radius: 50%;
  object-fit: contain;
  margin-right: 4px;
  border: 2px solid #eee;
  background: #f8f9fa;
}
.badge-name-inline-large {
  font-size: 1.08rem !important;
  font-weight: 600;
  color: #42b983;
  vertical-align: middle;
  white-space: nowrap;
}
@media (max-width: 900px) {
  .fixed-user-info {
    top: 0;
    border-radius: 0 0 10px 10px;
  }
  .profile-meta-side {
    left: 100px;
    min-width: 180px;
    height: 80px;
    padding: 8px 10px 8px 10px;
  }
  .profile-img-overlap {
    width: 80px;
    height: 90px;
  }
}
@media (max-width: 700px) {
  .fixed-user-info {
    top: 0;
    border-radius: 0 0 8px 8px;
    padding: 10px 2px 10px 2px;
  }
  .profile-meta-side {
    left: 80px;
    min-width: 120px;
    height: 60px;
    padding: 6px 6px 6px 6px;
  }
  .profile-img-overlap {
    width: 60px;
    height: 65px;
  }
  .user-nickname {
    font-size: 1.05rem;
  }
  .user-id {
    font-size: 0.85rem;
  }
}
@media (max-width: 500px) {
  .fixed-user-info {
    top: 0;
    border-radius: 0 0 4px 4px;
    padding: 6px 0 6px 0;
  }
  .profile-meta-side {
    left: 60px;
    min-width: 80px;
    height: 40px;
    padding: 2px 2px 2px 2px;
  }
  .profile-img-overlap {
    width: 38px;
    height: 40px;
  }
  .user-nickname {
    font-size: 0.9rem;
  }
  .user-id {
    font-size: 0.7rem;
  }
}
</style>