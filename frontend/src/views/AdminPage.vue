<template>
  <div>
    <div class="admin-title">관리자 페이지</div>
    <nav class="admin-nav modern-tabs">
      <button
        :class="{ active: !showOnlyHandled && !showOnlyUnhandled }"
        @click="
          showOnlyHandled = false;
          showOnlyUnhandled = false;
        "
      >
        전체 신고
      </button>
      <button
        :class="{ active: showOnlyHandled }"
        @click="
          showOnlyHandled = true;
          showOnlyUnhandled = false;
        "
      >
        처리한 신고만 보기
      </button>
      <button
        :class="{ active: showOnlyUnhandled }"
        @click="
          showOnlyUnhandled = true;
          showOnlyHandled = false;
        "
      >
        처리되지 않은 신고만 보기
      </button>
    </nav>

    <div class="report-list-wrapper">
      <div v-if="filteredReportList.length">
        <h3 class="report-list-title">신고 목록</h3>
        <ul class="report-list">
          <li
            v-for="report in filteredReportList"
            :key="report.reportId"
            class="report-card"
            :class="{ handled: report.handled }"
          >
            <div class="report-header">
              <strong>{{ report.reporterName }}</strong>
              <span class="arrow">→</span>
              <strong>{{ report.reporteeName }}</strong>
              <span class="category">[{{ report.reportCategory }}]</span>
            </div>
            <div class="report-body">
              <div class="reason"><b>사유:</b> {{ report.content }}</div>
              <div class="date">신고일: {{ report.createdAt }}</div>
            </div>
            <div class="report-actions">
              <button
                v-if="!report.handled"
                @click="
                  openModal(
                    report,
                    reportList.findIndex((r) => r.reportId === report.reportId)
                  )
                "
              >
                요청 처리
              </button>
              <span v-else class="handled-label">처리 완료</span>
            </div>
          </li>
        </ul>
      </div>
      <div v-else class="no-report">신고 내역이 없습니다.</div>
    </div>

    <!-- 신고 처리 모달 -->
    <transition name="modal-fade">
      <div v-if="showModal" class="modal-overlay">
        <div class="modal-content">
          <button class="modal-close" @click="closeModal">×</button>
          <h2>신고 처리</h2>
          <div class="modal-user-info">
            <div>
              <span class="modal-label">신고자</span>
              <a
                href="#"
                class="article-link"
                @click.prevent="
                  openUserReportModal(
                    selectedReport.reporterId,
                    selectedReport.reporterName
                  )
                "
              >
                <strong>{{ selectedReport?.reporterName }}</strong>
              </a>
            </div>
            <div>
              <span class="modal-label">피신고자</span>
              <a
                href="#"
                class="article-link"
                @click.prevent="
                  openUserReportModal(
                    selectedReport.reporteeId,
                    selectedReport.reporteeName
                  )
                "
              >
                <strong>{{ selectedReport?.reporteeName }}</strong>
              </a>
            </div>
          </div>
          <div class="modal-article-link">
            <router-link
              :to="`/board/detail/${selectedReport.articleId}`"
              class="article-link"
            >
              관련 게시글 바로가기
            </router-link>
          </div>
          <div class="modal-reason">
            <span class="modal-label">신고 사유</span>
            <div class="modal-reason-content">
              {{ selectedReport?.content }}
            </div>
          </div>
          <div class="modal-ban">
            <label for="ban-period"><b>정지 기간 선택:</b></label>
            <select id="ban-period" v-model="banPeriod">
              <option v-for="d in [1, 3, 5, 7, 14, 30]" :key="d" :value="d">
                {{ d }}일
              </option>
            </select>
          </div>
          <div class="modal-actions">
            <button @click="confirmHandle">확인</button>
            <button @click="closeModal" class="cancel">취소</button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 유저 신고내역 모달 -->
    <transition name="modal-fade">
      <div v-if="showUserReportModal" class="modal-overlay">
        <div class="modal-content report-modal-content">
          <button class="modal-close" @click="closeUserReportModal">×</button>
          <h2 class="report-modal-title">
            <span class="report-modal-username">{{
              userReportTarget.userName
            }}</span
            >님의 신고내역
          </h2>
          <div v-if="userReportLoading" class="loading">불러오는 중...</div>
          <div v-else>
            <div v-if="userReportList.length" class="report-card-list">
              <div
                v-for="report in userReportList"
                :key="report.reportId"
                class="user-report-card"
              >
                <div class="user-report-card-header">
                  <b>{{ report.reporterName }}</b>
                  <span class="arrow">→</span>
                  <b>{{ report.reporteeName }}</b>
                  <span class="category">[{{ report.reportCategory }}]</span>
                </div>
                <div class="user-report-card-body">
                  <div class="reason"><b>사유:</b> {{ report.content }}</div>
                  <div class="date">{{ report.createdAt }}</div>
                </div>
                <router-link
                  :to="`/board/detail/${report.articleId}`"
                  class="article-link"
                >
                  게시글 바로가기
                </router-link>
              </div>
            </div>
            <div v-else class="no-report">신고 내역이 없습니다.</div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { ref, computed, onMounted, watch } from "vue";
import api from "@/api/axiosInstance";
import Header from "@/components/Header.vue";

const user = ref(
  localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null
);
const router = useRouter();

const reportList = ref([]);
const banPeriod = ref(1);

const showModal = ref(false);
const selectedReport = ref(null);
const selectedIdx = ref(null);

const showOnlyUnhandled = ref(false);
const showOnlyHandled = ref(false);

watch(showOnlyHandled, (val) => {
  if (val) showOnlyUnhandled.value = false;
});
watch(showOnlyUnhandled, (val) => {
  if (val) showOnlyHandled.value = false;
});

const filteredReportList = computed(() => {
  let list = reportList.value.slice();
  list.sort((a, b) => {
    if (a.handled === b.handled) return 0;
    return a.handled ? -1 : 1;
  });
  if (showOnlyUnhandled.value) {
    return list.filter((r) => !r.handled);
  }
  if (showOnlyHandled.value) {
    return list.filter((r) => r.handled);
  }
  return list;
});

const openModal = (report, idx) => {
  selectedReport.value = report;
  selectedIdx.value = idx;
  banPeriod.value = 1;
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  selectedReport.value = null;
  selectedIdx.value = null;
};

const confirmHandle = () => {
  api
    .put(`/api_admin/suspend/reportId/${selectedReport.value.reportId}`, {
      userId: selectedReport.value.reporteeId,
      userName: selectedReport.value.reporteeName,
      durationDays: banPeriod.value,
    })
    .then(() => {
      alert("신고가 처리되었습니다.");
      reportList.value[selectedIdx.value].handled = true;
      closeModal();
    })
    .catch(() => {
      alert("신고 처리 중 오류가 발생했습니다.");
      closeModal();
    });
};

// 유저 신고내역 모달 관련
const showUserReportModal = ref(false);
const userReportList = ref([]);
const userReportLoading = ref(false);
const userReportTarget = ref({ userId: null, userName: "" });

const openUserReportModal = async (userId, userName) => {
  userReportTarget.value = { userId, userName };
  userReportLoading.value = true;
  showUserReportModal.value = true;
  try {
    const res = await api.get(`/api_admin/get/report/userId/${userId}`);
    userReportList.value = res.data;
  } catch (e) {
    userReportList.value = [];
    alert("신고내역을 불러오지 못했습니다.");
  } finally {
    userReportLoading.value = false;
  }
};

const closeUserReportModal = () => {
  showUserReportModal.value = false;
  userReportList.value = [];
  userReportTarget.value = { userId: null, userName: "" };
};

onMounted(() => {
  if (user.value?.role !== "ROLE_ADMIN") {
    router.push("/mypage");
    return;
  }
  api
    .get("/api_admin/get/report")
    .then((res) => {
      reportList.value = res.data;
    })
    .catch(() => {
      alert("신고 목록을 가져오는 중 오류가 발생했습니다.");
    });
});
</script>

<style scoped>
.admin-title {
  font-size: 1.5em;
  font-weight: bold;
  margin: 24px 0 12px 0;
  color: #222;
  letter-spacing: -1px;
}

.admin-nav.modern-tabs {
  display: flex;
  gap: 0;
  border-bottom: 2px solid #e0e0e0;
  background: #f8f9fa;
  padding: 0 18px;
  margin-bottom: 24px;
}

.admin-nav.modern-tabs button {
  background: none;
  border: none;
  outline: none;
  padding: 16px 32px 12px 32px;
  font-size: 1.08em;
  color: #888;
  font-weight: 500;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  transition: color 0.2s, border-bottom 0.2s;
  margin-bottom: -2px;
}

.admin-nav.modern-tabs button.active {
  color: #42b983;
  border-bottom: 3px solid #42b983;
  background: none;
}

.admin-nav.modern-tabs button:not(.active):hover {
  color: #369f6b;
  border-bottom: 3px solid #b7e3d0;
}

.report-list-wrapper {
  background: #f8fafc;
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(66, 185, 131, 0.06),
    0 1.5px 6px rgba(0, 0, 0, 0.03);
  padding: 32px 24px 24px 24px;
  margin-bottom: 32px;
  min-height: 320px;
}

.report-list-title {
  font-size: 1.18em;
  font-weight: 600;
  color: #333;
  margin-bottom: 18px;
  letter-spacing: -0.5px;
}

.report-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.report-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(66, 185, 131, 0.08),
    0 1.5px 6px rgba(0, 0, 0, 0.04);
  padding: 20px 24px 16px 24px;
  border: 1px solid #e6f2ec;
  display: flex;
  flex-direction: column;
  gap: 10px;
  transition: box-shadow 0.2s, transform 0.2s;
  position: relative;
}

.report-card.handled {
  opacity: 0.6;
  filter: grayscale(0.15);
}

.report-card:hover {
  box-shadow: 0 6px 24px rgba(66, 185, 131, 0.13), 0 2px 8px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px) scale(1.01);
}

.report-header {
  font-size: 1.08em;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.report-header .arrow {
  color: #bbb;
  font-size: 1.1em;
  margin: 0 2px;
}

.report-header .category {
  color: #42b983;
  font-size: 0.97em;
  margin-left: 8px;
  font-weight: 500;
}

.report-body {
  font-size: 0.98em;
  color: #444;
  margin-bottom: 6px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.report-body .reason {
  margin-bottom: 2px;
}

.report-body .date {
  color: #aaa;
  font-size: 0.93em;
}

.report-actions {
  margin-top: 8px;
  text-align: right;
}

.report-actions button {
  background: #42b983;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 7px 18px;
  cursor: pointer;
  font-size: 1em;
  font-weight: 500;
  transition: background 0.2s;
}

.report-actions button:hover {
  background: #369f6b;
}

.handled-label {
  color: #888;
  font-weight: bold;
  padding: 7px 18px;
  border-radius: 4px;
  background: #f0f0f0;
}

.no-report {
  color: #aaa;
  text-align: center;
  margin: 40px 0;
  font-size: 1.1em;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  background: #fff;
  border-radius: 14px;
  padding: 36px 28px 28px 28px;
  min-width: 320px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
  text-align: left;
  position: relative;
  animation: modal-pop-in 0.35s cubic-bezier(0.4, 2, 0.6, 1) both;
}
@keyframes modal-pop-in {
  0% {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
.modal-close {
  position: absolute;
  top: 18px;
  right: 18px;
  background: none;
  border: none;
  font-size: 1.6em;
  color: #aaa;
  cursor: pointer;
  transition: color 0.2s;
}
.modal-close:hover {
  color: #42b983;
}
.modal-user-info {
  display: flex;
  gap: 24px;
  margin-bottom: 12px;
}
.modal-label {
  display: inline-block;
  min-width: 60px;
  color: #888;
  font-size: 0.98em;
  margin-right: 6px;
}
.modal-user-info a {
  color: #42b983;
  text-decoration: underline;
  cursor: pointer;
}
.modal-article-link {
  margin: 10px 0 14px 0;
}
.modal-reason {
  margin-bottom: 18px;
}
.modal-reason-content {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 10px 14px;
  color: #333;
  margin-top: 4px;
  font-size: 1.05em;
}
.modal-ban {
  margin-bottom: 18px;
}
.modal-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
.modal-actions button {
  padding: 8px 22px;
  border: none;
  border-radius: 4px;
  background: #42b983;
  color: #fff;
  cursor: pointer;
  font-size: 1em;
  transition: background 0.2s;
}
.modal-actions .cancel {
  background: #aaa;
}
.modal-actions button:hover:not(.cancel) {
  background: #369f6b;
}
.modal-actions .cancel:hover {
  background: #888;
}
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s;
}
.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}
.loading {
  color: #888;
  text-align: center;
  margin: 20px 0;
}

/* 신고내역 모달 스타일 */
.report-modal-content {
  max-width: 600px;
  width: 96vw;
  max-height: 80vh;
  overflow-y: auto;
  padding: 32px 24px 24px 24px;
  box-sizing: border-box;
  background: #f8fafc;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
  position: relative;
}

.report-modal-title {
  margin-top: 0;
  margin-bottom: 18px;
  font-size: 1.25em;
  color: #222;
  text-align: left;
  font-weight: 600;
  letter-spacing: -1px;
}

.report-modal-username {
  color: #42b983;
  font-weight: bold;
}

.report-card-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 4px;
}

.user-report-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(66, 185, 131, 0.08),
    0 1.5px 6px rgba(0, 0, 0, 0.04);
  padding: 18px 20px 14px 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  transition: box-shadow 0.2s, transform 0.2s;
  border: 1px solid #e6f2ec;
}

.user-report-card:hover {
  box-shadow: 0 6px 24px rgba(66, 185, 131, 0.13), 0 2px 8px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px) scale(1.01);
}

.user-report-card-header {
  font-size: 1.08em;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.user-report-card-header .arrow {
  color: #bbb;
  font-size: 1.1em;
  margin: 0 2px;
}

.user-report-card-header .category {
  color: #42b983;
  font-size: 0.97em;
  margin-left: 8px;
  font-weight: 500;
}

.user-report-card-body {
  font-size: 0.98em;
  color: #444;
  margin-bottom: 6px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-report-card .reason {
  margin-bottom: 2px;
}

.user-report-card .date {
  color: #aaa;
  font-size: 0.93em;
}

.user-report-card .article-link {
  color: #42b983;
  text-decoration: underline;
  font-size: 0.98em;
  align-self: flex-end;
  margin-top: 2px;
}

.user-report-card .article-link:hover {
  color: #2c8c6d;
}

.no-report {
  color: #888;
  text-align: center;
  margin: 32px 0;
}
</style>