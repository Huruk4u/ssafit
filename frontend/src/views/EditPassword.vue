<template>
  <div class="edit-password-container">
    <form @submit.prevent="updatePassword">
      <div class="form-group">
        <label for="currentPassword">현재 비밀번호</label>
        <input id="currentPassword" v-model="currentPassword" type="password" autocomplete="current-password" />
      </div>
      <div class="form-group">
        <label for="newPassword">변경할 비밀번호</label>
        <input id="newPassword" v-model="newPassword" type="password" autocomplete="new-password" />
      </div>
      <div class="form-group">
        <label for="checkNewPassword">변경할 비밀번호 확인</label>
        <input id="checkNewPassword" v-model="checkNewPassword" type="password" autocomplete="new-password" />
      </div>
      <button type="submit" class="submit-btn">비밀번호 변경</button>
    </form>
  </div>
</template>

<script setup>
    import {ref} from 'vue'
    import api from '@/api/axiosInstance'
    import {useRouter} from 'vue-router'

    const router = useRouter()

    const rawUser = localStorage.getItem('user')
    const user = rawUser ? JSON.parse(rawUser) : null
    const userName = user?.userName || ''

    const currentPassword = ref('')
    const newPassword = ref('')
    const checkNewPassword = ref('')

    const updatePassword = () => {
        if (!currentPassword.value || !newPassword.value || !checkNewPassword.value) {
            alert("모든 정보를 입력해야합니다.")
            return
        }
        if (newPassword.value !== checkNewPassword.value) {
            alert("새 비밀번호가 일치하지 않습니다.")
            return
        }

        api.put(`/api_user/put/password/userName/${userName}`, {
            currentPassword: currentPassword.value,
            newPassword: newPassword.value,
            checkNewPassword: checkNewPassword.value
        })
        .then(() => {
            alert("비밀번호가 변경되었습니다.")
            router.replace('/mypage')
        })
        .catch(() => {
            alert("비밀번호 변경 실패. 입력 조건을 잘 확인해주세요.")
        })
    }
</script>

<style scoped>
.edit-password-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 60vh;
}

form {
  max-width: 370px;
  margin: 60px auto 0 auto;
  padding: 32px 28px 24px 28px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 6px 32px rgba(66,185,131,0.10), 0 1.5px 6px rgba(66,185,131,0.07);
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

label {
  font-size: 1rem;
  color: #369870;
  font-weight: 600;
  margin-bottom: 2px;
  letter-spacing: -0.5px;
}

input[type="password"] {
  width: 100%;
  padding: 10px 14px;
  border: 2px solid #b2dfdb;
  border-radius: 14px;
  font-size: 1.05rem;
  background: #f8f9fa;
  transition: border 0.2s, box-shadow 0.2s;
  margin-bottom: 2px;
  box-shadow: 0 2px 8px rgba(66,185,131,0.07);
}

input[type="password"]:focus {
  border-color: #42b983;
  outline: none;
  background: #fff;
  box-shadow: 0 0 0 3px #b2f2e5, 0 2px 8px rgba(66,185,131,0.13);
}

.submit-btn {
  width: 100%;
  padding: 12px 0;
  background: linear-gradient(135deg, #42b983 60%, #b2f2e5 100%);
  color: #fff;
  border: none;
  border-radius: 14px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  margin-top: 8px;
  box-shadow: 0 2px 12px rgba(66,185,131,0.13);
  transition: background 0.2s, box-shadow 0.2s;
  border: 2px solid #b2dfdb;
  letter-spacing: 0.02em;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #369870 60%, #42b983 100%);
  box-shadow: 0 4px 18px rgba(66,185,131,0.18);
  border-color: #42b983;
}

@media (max-width: 500px) {
  form {
    width: 98vw;
    min-width: 0;
    padding: 18px 4vw 18px 4vw;
  }
}
</style>