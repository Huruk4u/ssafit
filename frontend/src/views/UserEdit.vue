<template>
  <div class="root">
    <div class="useredit-container container py-4">
      <!-- 헤더 섹션 -->
      <section class="page-header card p-4 mb-4">
        <h2 class="page-title">프로필 편집</h2>
        <p class="page-subtitle text-muted">프로필 이미지, 배경 이미지 및 개인정보를 수정할 수 있습니다.</p>
      </section>

      <!-- 이미지 편집 섹션 -->
      <section class="image-edit-section card p-4 mb-4">
        <h3 class="section-title">이미지 설정</h3>
        
        <div class="image-edit-grid">
          <!-- 배경 이미지 편집 -->
          <div class="image-edit-item">
            <label class="image-label">배경 이미지</label>
            <div class="background-image-container">
              <img
                :src="userBackgroundImage || '/default-background.jpg'"
                alt="배경이미지"
                class="background-img-preview"
                @click="triggerBackgroundImageInput"
              />
              <div class="image-overlay" @click="triggerBackgroundImageInput">
                <div class="overlay-content">
                  <svg class="upload-icon" viewBox="0 0 24 24" width="32" height="32">
                    <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
                  </svg>
                  <span class="overlay-text">클릭하여 변경</span>
                </div>
              </div>
              <input
                type="file"
                ref="backgroundImageInput"
                accept="image/*"
                @change="handleBackgroundImageChange"
                style="display: none"
              />
            </div>
          </div>

          <!-- 프로필 이미지 편집 -->
          <div class="image-edit-item">
            <label class="image-label">프로필 이미지</label>
            <div class="profile-image-container">
              <img
                :src="userProfileImage || '/default-profile.png'"
                alt="프로필이미지"
                class="profile-img-preview"
                @click="triggerProfileImageInput"
              />
              <div class="profile-overlay" @click="triggerProfileImageInput">
                <svg class="upload-icon" viewBox="0 0 24 24" width="24" height="24">
                  <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
                </svg>
              </div>
              <input
                type="file"
                ref="profileImageInput"
                accept="image/*"
                @change="handleProfileImageChange"
                style="display: none"
              />
            </div>
          </div>
        </div>
      </section>

      <!-- 개인정보 편집 섹션 -->
      <section class="info-edit-section card p-4 mb-4">
        <h3 class="section-title">개인정보 수정</h3>
        
        <form @submit.prevent="userStringInfoUpdate" class="edit-form">
          <div class="form-group">
            <label for="nickname" class="form-label">닉네임</label>
            <input 
              id="nickname"
              v-model="nickname" 
              type="text" 
              class="form-control"
              placeholder="닉네임을 입력하세요"
              required
            />
          </div>
          
          <div class="form-group">
            <label for="email" class="form-label">이메일</label>
            <input 
              id="email"
              v-model="email" 
              type="email" 
              class="form-control"
              placeholder="이메일을 입력하세요"
              required
            />
          </div>
          
          <div class="form-actions">
            <button type="submit" class="btn btn-primary">
              <svg class="btn-icon" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M21,7L9,19L3.5,13.5L4.91,12.09L9,16.17L19.59,5.59L21,7Z"/>
              </svg>
              정보 수정하기
            </button>
          </div>
        </form>
      </section>

      <!-- 비밀번호 변경 섹션 -->
      <section class="password-section card p-4">
        <h3 class="section-title">보안 설정</h3>
        <p class="section-description text-muted">계정 보안을 위해 주기적으로 비밀번호를 변경해 주세요.</p>
        
        <div class="password-actions">
          <router-link to="/editPassword" class="btn btn-outline-primary">
            <svg class="btn-icon" viewBox="0 0 24 24" width="16" height="16">
              <path fill="currentColor" d="M12,17A2,2 0 0,0 14,15C14,13.89 13.1,13 12,13A2,2 0 0,0 10,15A2,2 0 0,0 12,17M18,8A2,2 0 0,1 20,10V20A2,2 0 0,1 18,22H6A2,2 0 0,1 4,20V10C4,8.89 4.9,8 6,8H7V6A5,5 0 0,1 12,1A5,5 0 0,1 17,6V8H18M12,3A3,3 0 0,0 9,6V8H15V6A3,3 0 0,0 12,3Z"/>
            </svg>
            비밀번호 변경하기
          </router-link>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import api from "@/api/axiosInstance";
import { useRouter } from "vue-router";

const router = useRouter();

const rawUser = localStorage.getItem("user");
const parsedUser = rawUser
  ? JSON.parse(rawUser)
  : {
      userName: "",
      nickname: "",
      email: "",
      profileImage: "",
      backgroundImage: "",
    };
const user = ref(parsedUser);

const nickname = ref(user.value.nickname);
const email = ref(user.value.email);

// user 프로필 이미지를 로드하기 위한 computed 변수
const userProfileImage = computed(() =>
  user.value?.profileImage
    ? `http://localhost:8080/images/profile/${user.value.profileImage}`
    : ""
);

const userBackgroundImage = computed(() =>
  user.value?.backgroundImage
    ? `http://localhost:8080/images/background/${user.value.backgroundImage}`
    : ""
);

// 입력된 프로필 이미지와 배경 이미지를 저장하기 위한 ref 변수
const profileImageInput = ref(null);
const backgroundImageInput = ref(null);

// 유저의 닉네임, 이메일 수정 메서드
const userStringInfoUpdate = () => {
  api
    .put(`/api_user/put/userInfo/userName/${user.value.userName}`, {
      userName: user.value.userName,
      nickname: nickname.value,
      email: email.value,
    })
    .then(() => {
      alert("정보가 수정되었습니다.");
      user.value = {
        ...user.value,
        nickname: nickname.value,
        email: email.value,
      };
      localStorage.setItem("user", JSON.stringify(user.value));
    })
    .catch((err) => {
      console.error("정보 수정 실패 : ", err);
      alert("수정에 실패했습니다.");
    });
};

// 프로필 이미지 수정 메서드
const triggerProfileImageInput = () => {
  profileImageInput.value.click();
};

const handleProfileImageChange = (event) => {
  const file = event.target.files[0];
  // 선택된 파일이 있을 때만 진행한다.
  if (file) {
    const formData = new FormData();
    formData.append("userName", user.value.userName);
    formData.append("file", file);
    api
      .post("/api_user/post/profileImage", formData, {
        headers: {
          "Content-type": "multipart/form-data",
        },
      })
      .then((res) => {
        alert("프로필 이미지가 수정되었습니다.");
        user.value = {
          ...user.value,
          profileImage: res.data,
        };
        localStorage.setItem("user", JSON.stringify(user.value));
      })
      .catch((err) => {
        console.error("프로필 이미지 수정 실패 : ", err);
        alert("수정에 실패했습니다.");
      });
  }
};

// 배경 이미지 수정 메서드
const triggerBackgroundImageInput = () => {
  backgroundImageInput.value.click();
};
const handleBackgroundImageChange = (event) => {
  const file = event.target.files[0];
  // 선택된 파일이 있을 때만 진행한다.
  if (file) {
    const formData = new FormData();
    formData.append("userName", user.value.userName);
    formData.append("file", file);
    api
      .post("/api_user/post/backgroundImage", formData, {
        headers: {
          "Content-type": "multipart/form-data",
        },
      })
      .then((res) => {
        alert("배경 이미지가 수정되었습니다.");
        console.log(res.backgroundImage);
        user.value = {
          ...user.value,
          backgroundImage: res.data,
        };
        localStorage.setItem("user", JSON.stringify(user.value));
      })
      .catch((err) => {
        console.error("배경 이미지 수정 실패 : ", err);
        alert("수정에 실패했습니다.");
      });
  }
};
</script>

<style scoped>
/* 폰트 및 전체 배경 */
@import url("https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/variable/pretendardvariable.css");

:root,
body,
.root {
  font-family: "Pretendard Variable", "Pretendard", "Noto Sans KR", Arial,
    sans-serif;
  font-size: 16px;
  color: #222;
  background: #fafbfc;
  letter-spacing: 0.01em;
}

.useredit-container {
  max-width: 800px;
  margin: 0 auto;
}

/* 카드 스타일 */
.card {
  background-color: #fff;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.2s ease;
}

.card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

/* 페이지 헤더 */
.page-header {
  text-align: center;
  background: linear-gradient(135deg, #42b983 0%, #369870 100%);
  color: white;
  border: none;
}

.page-title {
  font-size: 1.8rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
}

.page-subtitle {
  font-size: 1rem;
  margin: 0;
  opacity: 0.9;
}

/* 섹션 타이틀 */
.section-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 1.5rem 0;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #42b983;
}

.section-description {
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
}

/* 이미지 편집 섹션 */
.image-edit-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
  align-items: start;
}

.image-edit-item {
  display: flex;
  flex-direction: column;
}

.image-label {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.75rem;
}

/* 배경 이미지 */
.background-image-container {
  position: relative;
  width: 100%;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 2px dashed #dee2e6;
  transition: all 0.3s ease;
}

.background-image-container:hover {
  border-color: #42b983;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.15);
}

.background-img-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  cursor: pointer;
}

.background-image-container:hover .image-overlay {
  opacity: 1;
}

.overlay-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: white;
  text-align: center;
}

.overlay-text {
  font-size: 0.9rem;
  font-weight: 500;
  margin-top: 0.5rem;
}

.upload-icon {
  color: white;
  margin-bottom: 0.25rem;
}

/* 프로필 이미지 */
.profile-image-container {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto;
  cursor: pointer;
}

.profile-img-preview {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.profile-image-container:hover .profile-img-preview {
  transform: scale(1.05);
}

.profile-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  cursor: pointer;
}

.profile-image-container:hover .profile-overlay {
  opacity: 1;
}

/* 폼 스타일 */
.edit-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
}

.form-control {
  padding: 0.75rem 1rem;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: #fff;
}

.form-control:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.1);
}

.form-control::placeholder {
  color: #adb5bd;
}

/* 버튼 스타일 */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.btn-icon {
  flex-shrink: 0;
}

.btn-primary {
  background: linear-gradient(135deg, #42b983 0%, #369870 100%);
  color: white;
  box-shadow: 0 2px 4px rgba(66, 185, 131, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.4);
}

.btn-outline-primary {
  background: #fff;
  color: #42b983;
  border: 2px solid #42b983;
}

.btn-outline-primary:hover {
  background: #42b983;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.form-actions {
  display: flex;
  justify-content: center;
  margin-top: 1rem;
}

.password-actions {
  display: flex;
  justify-content: center;
}

/* 반응형 */
@media (max-width: 768px) {
  .image-edit-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .profile-image-container {
    width: 100px;
    height: 100px;
  }
  
  .profile-img-preview {
    width: 100px;
    height: 100px;
  }
  
  .background-image-container {
    height: 150px;
  }
  
  .page-title {
    font-size: 1.5rem;
  }
  
  .section-title {
    font-size: 1.1rem;
  }
}
</style>