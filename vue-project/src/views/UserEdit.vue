<template>
  <div>
    <div>
      <div>
        <img
          :src="userProfileImage"
          alt="프로필이미지"
          class="profile-img"
          @click="triggerProfileImageInput"
        />
        <input
          type="file"
          ref="profileImageInput"
          accept="image/*"
          @change="handleProfileImageChange"
          style="display: none"
        />
      </div>
      <div>
        <img
          :src="userBackgroundImage"
          alt="배경이미지"
          class="background-img"
          @click="triggerBackgroundImageInput"
        />
        <input
          type="file"
          ref="backgroundImageInput"
          accept="image/*"
          @change="handleBackgroundImageChange"
          style="display: none"
        />
      </div>
    </div>
    <div>
      <form @submit.prevent="userStringInfoUpdate">
        <div>
          <label>유저 닉네임</label>
          <input v-model="nickname" type="text" />
        </div>
        <div>
          <label>유저 이메일</label>
          <input v-model="email" type="text" />
        </div>
        <div>
          <input type="submit" value="수정하기" />
        </div>
      </form>
      <router-link to="/editPassword">비밀번호 변경하기</router-link>
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
</style>