<template>
  <div>
    <div>
      <Header />
    </div>
    관리자 페이지

    <!-- handled가 false인 신고만 보기 체크박스 -->
    <div style="margin: 12px 0;">
      <label>
        <input type="checkbox" v-model="showOnlyUnhandled" />
        처리되지 않은 신고만 보기
      </label>
    </div>

    <div v-if="filteredReportList.length">
      <h3>신고 목록</h3>
      <ul class="report-list">
        <li
          v-for="(report, idx) in filteredReportList"
          :key="report.reportId"
          class="report-card"
          :class="{ handled: report.handled }"
        >
          <div class="report-header">
            <strong>{{ report.reporterId }}</strong>님이 <strong>{{ report.reporteeId }}</strong>님을 신고했습니다.
          </div>
          <div class="report-body">
            <div>링크: <a :href="`/article/id/${report.articleId}`" target="_blank">article/id/{{ report.articleId }}</a></div>
            <div>카테고리: {{ report.reportCategory }}</div>
            <div>신고 사유: {{ report.content }}</div>
          </div>
          <div class="report-actions">
            <button
              v-if="!report.handled"
              @click="openModal(report, reportList.findIndex(r => r.reportId === report.reportId))"
            >요청 처리</button>
            <span v-else class="handled-label">처리 완료</span>
          </div>
        </li>
      </ul>
    </div>
    <div v-else>
      신고 내역이 없습니다.
    </div>

    <!-- 팝업(모달) -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal-content">
        <h4>신고 처리</h4>
        <p>
          이 신고를 처리하시겠습니까?<br>
          <b>신고자:</b> {{ selectedReport?.reporterId }}<br>
          <b>피신고자:</b> {{ selectedReport?.reporteeId }}
        </p>
        <div style="margin: 16px 0;">
          <label for="ban-period"><b>정지 기간 선택:</b></label>
          <select id="ban-period" v-model="banPeriod">
            <option v-for="d in [1,3,5,7,14,30]" :key="d" :value="d">{{ d }}일</option>
          </select>
        </div>
        <div class="modal-actions">
          <button @click="confirmHandle">확인</button>
          <button @click="closeModal">취소</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { ref, computed, onMounted } from "vue";
import api from "@/api/axiosInstance";
import Header from "@/components/Header.vue";

const user = ref(
  localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null
);

const router = useRouter();

const reportList = ref([]);
const banPeriod = ref(1); // 기본값 1일

// 모달 상태
const showModal = ref(false);
const selectedReport = ref(null);
const selectedIdx = ref(null);

// 처리되지 않은 신고만 보기 체크박스
const showOnlyUnhandled = ref(false);

// 필터링된 리스트
const filteredReportList = computed(() =>
  showOnlyUnhandled.value
    ? reportList.value.filter(r => !r.handled)
    : reportList.value
);

const openModal = (report, idx) => {
  selectedReport.value = report;
  selectedIdx.value = idx;
  banPeriod.value = 1; // 모달 열 때마다 초기화
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  selectedReport.value = null;
  selectedIdx.value = null;
};

const confirmHandle = () => {
  api.post(`/api_admin/handle/report`, {
      reportId: selectedReport.value.reportId,
      banPeriod: banPeriod.value
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
    .catch((err) => {
      alert("신고 목록을 가져오는 중 오류가 발생했습니다.");
    });
});
</script>

<style scoped>
.report-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.report-card {
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  margin-bottom: 18px;
  padding: 18px 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: box-shadow 0.2s, opacity 0.2s;
}
.report-card.handled {
  opacity: 0.5;
  filter: grayscale(0.2);
}
.report-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
}
.report-header {
  font-size: 1.1em;
  margin-bottom: 10px;
  color: #333;
}
.report-body > div {
  margin-bottom: 6px;
  color: #555;
}
.report-body a {
  color: #42b983;
  text-decoration: underline;
}
.report-actions {
  margin-top: 10px;
  text-align: right;
}
.report-actions button {
  background: #42b983;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 7px 16px;
  cursor: pointer;
  font-size: 1em;
  transition: background 0.2s;
}
.report-actions button:hover {
  background: #369f6b;
}
.handled-label {
  color: #888;
  font-weight: bold;
  padding: 7px 16px;
  border-radius: 4px;
  background: #f0f0f0;
}
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  background: #fff;
  border-radius: 8px;
  padding: 32px 24px;
  min-width: 280px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.18);
  text-align: center;
}
.modal-actions {
  margin-top: 18px;
  display: flex;
  justify-content: center;
  gap: 12px;
}
.modal-actions button {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  background: #42b983;
  color: #fff;
  cursor: pointer;
  font-size: 1em;
}
.modal-actions button:last-child {
  background: #aaa;
}
</style>