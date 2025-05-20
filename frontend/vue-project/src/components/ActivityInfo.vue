<template>
  <div class="activity-info">
    <!-- 스트릭 정보 섹션 -->
    <section class="streak-section">
      <h3>챌린지 스트릭 현황</h3>
      <div v-if="isLoading" class="loading">
        <p>로딩 중...</p>
      </div>
      <div v-else class="streak-info">
        <div class="streak-card">
          <div class="streak-value">{{ profileData.challengeSummary?.currentStreak || 0 }}</div>
          <div class="streak-label">현재 스트릭</div>
        </div>
        <div class="streak-card">
          <div class="streak-value">{{ profileData.challengeSummary?.longestStreak || 0 }}</div>
          <div class="streak-label">최장 스트릭</div>
        </div>
      </div>
      
      <!-- 스트릭 달력 표시 -->
      <div class="streak-calendar">
        <h4>최근 스트릭 기록</h4>
        <div class="calendar-grid">
          <div v-for="(month, monthIndex) in calendarMonths" :key="monthIndex" class="month-column">
            <div class="month-label">{{ month.label }}</div>
            <div class="days-grid">
              <div v-for="day in 7" :key="day" class="day-label">
                {{ getDayLabel(day) }}
              </div>
              <!-- 빈 칸 채우기 -->
              <div v-for="empty in month.firstDay" :key="`empty-${empty}`" class="day-cell empty"></div>
              <!-- 실제 날짜 -->
              <div 
                v-for="date in month.days" 
                :key="`${month.year}-${month.month}-${date}`"
                :class="[
                  'day-cell', 
                  isDateCompleted(`${month.year}-${formatTwoDigits(month.month)}-${formatTwoDigits(date)}`) ? 'completed' : '',
                  isToday(`${month.year}-${formatTwoDigits(month.month)}-${formatTwoDigits(date)}`) ? 'today' : ''
                ]"
              >
                {{ date }}
              </div>
            </div>
          </div>
        </div>
        
        <div class="streak-legend">
          <div class="legend-item">
            <div class="legend-color completed"></div>
            <span>챌린지 완료</span>
          </div>
          <div class="legend-item">
            <div class="legend-color today"></div>
            <span>오늘</span>
          </div>
        </div>
      </div>
    </section>
    
    <!-- 인바디 정보 섹션 -->
    <section class="inbody-section">
      <h3>인바디 기록</h3>
      
      <div v-if="profileData.inbody && profileData.inbody.length > 0">
        <!-- 인바디 차트 -->
        <div class="inbody-chart">
          <h4>체중 변화 추이</h4>
          <div class="chart-container">
            <!-- 차트를 그릴 캔버스 -->
            <canvas ref="weightChart"></canvas>
          </div>
        </div>
        
        <!-- 인바디 기록 표 -->
        <div class="inbody-table">
          <h4>인바디 측정 기록</h4>
          <table>
            <thead>
              <tr>
                <th>날짜</th>
                <th>체중(kg)</th>
                <th>골격근량(kg)</th>
                <th>체지방량(kg)</th>
                <th>체지방률(%)</th>
                <th>BMI</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(record, index) in sortedInbodyRecords" :key="index">
                <td>{{ record.measureDate ? formatDate(record.measureDate) : '날짜 없음' }}</td>
                <td>{{ record.weight || '-' }}</td>
                <td>{{ record.muscleMass || '-' }}</td>
                <td>{{ record.bodyFat || '-' }}</td>
                <td>{{ record.bodyFatPercentage || '-' }}</td>
                <td>{{ record.bmi || '-' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <p v-else class="no-inbody">아직 등록된 인바디 기록이 없습니다.</p>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import Chart from 'chart.js/auto';
import api from "@/api/axiosInstance";

// 로딩 상태
const isLoading = ref(true);

// 프로필 데이터 상태 관리
const profileData = ref({
  challengeSummary: {
    currentStreak: 0,
    longestStreak: 0,
    streakCalendar: {}
  },
  inbody: []
});

// 프로필 정보 가져오기
const fetchProfileData = async () => {
  try {
    const res = await api.get("/api_mypage/profile");
    console.log("프로필 데이터:", res.data);
    
    if (res.data) {
      // 기본 데이터 구조 설정
      profileData.value = {
        ...res.data,
        challengeSummary: res.data.challengeSummary || {
          currentStreak: 0,
          longestStreak: 0,
          streakCalendar: {}
        },
        inbody: res.data.inbody || []
      };
      
      // 인바디 데이터 구조 상세 로깅
      if (profileData.value.inbody && profileData.value.inbody.length > 0) {
        console.log("인바디 샘플 데이터:", profileData.value.inbody[0]);
        
        // Inbody 클래스의 필드들을 확인
        const sampleRecord = profileData.value.inbody[0];
        console.log("Inbody 객체 구조 필드:");
        for (const key in sampleRecord) {
          console.log(`- ${key}: ${typeof sampleRecord[key]} = ${JSON.stringify(sampleRecord[key])}`);
        }
        
        // 날짜 필드 찾기
        const dateFields = Object.keys(sampleRecord).filter(key => 
          key.toLowerCase().includes('date') || 
          key.toLowerCase().includes('time') ||
          key.toLowerCase().includes('created') ||
          key.toLowerCase().includes('measure')
        );
        console.log("가능한 날짜 필드들:", dateFields);
        
        // 인바디 데이터의 측정일 찾기
        profileData.value.inbody = profileData.value.inbody.map(record => {
          // 1. 측정일(measureDate) 필드가 있는지 확인
          let measureDate = null;
          
          // Java LocalDate 객체가 변환된 경우(필드들 검사)
          if (record.measurementDate) {
            measureDate = record.measurementDate;
          } else if (record.measureDate) {
            measureDate = record.measureDate;
          } else if (record.date) {
            measureDate = record.date;
          } else if (record.createdDate) {
            measureDate = record.createdDate;
          } else if (record.created) {
            measureDate = record.created;
          } else if (record.timestamp) {
            measureDate = record.timestamp;
          } else if (record.recordDate) {
            measureDate = record.recordDate;
          } else {
            // 이 외의 가능한 날짜 필드들 검사
            for (const field of dateFields) {
              if (record[field]) {
                measureDate = record[field];
                break;
              }
            }
          }
          
          // 측정일이 여전히 없으면 현재 기록으로 로그만 남기기
          if (!measureDate) {
            console.warn("날짜 필드를 찾을 수 없는 레코드:", record);
          }
          
          return {
            ...record,
            measureDate: measureDate
          };
        });
        
        console.log("전처리 후 인바디 샘플:", profileData.value.inbody[0]);
      }
      
      // 스트릭 캘린더 데이터 처리 (LocalDate -> String 변환)
      if (profileData.value.challengeSummary?.streakCalendar) {
        const originalCalendar = profileData.value.challengeSummary.streakCalendar;
        const newCalendar = {};
        
        console.log("스트릭 캘린더 원본:", originalCalendar);
        
        // 캘린더의 키(날짜)를 처리
        Object.keys(originalCalendar).forEach(dateKey => {
          let formattedDate = dateKey;
          
          try {
            // 자바 LocalDate 객체가 JSON으로 변환된 경우 처리
            if (dateKey.includes('[') || (typeof dateKey === 'string' && dateKey.includes('{'))) {
              const dateObj = JSON.parse(dateKey);
              if (Array.isArray(dateObj)) {
                // [year, month, day] 형식
                const [year, month, day] = dateObj;
                formattedDate = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
              } else if (dateObj.year && dateObj.month && dateObj.day) {
                // {year, month, day} 형식
                formattedDate = `${dateObj.year}-${String(dateObj.month).padStart(2, '0')}-${String(dateObj.day).padStart(2, '0')}`;
              }
            } else {
              // 이미 문자열 형태로 전달된 경우 형식 확인
              formattedDate = formatDate(dateKey);
            }
          } catch (e) {
            console.error("스트릭 캘린더 날짜 변환 오류:", e);
          }
          
          newCalendar[formattedDate] = originalCalendar[dateKey];
        });
        
        profileData.value.challengeSummary.streakCalendar = newCalendar;
        console.log("변환된 스트릭 캘린더:", newCalendar);
      }
    }
  } catch (err) {
    console.error("프로필 정보 로드 실패:", err);
  } finally {
    isLoading.value = false;
  }
};

// 체중 차트 참조
const weightChart = ref(null);
let chartInstance = null;

// 정렬된 인바디 기록
const sortedInbodyRecords = computed(() => {
  if (!profileData.value.inbody || profileData.value.inbody.length === 0) return [];
  
  console.log("인바디 데이터 정렬 준비:", profileData.value.inbody);
  
  // 데이터 복사 및 가공
  const records = [...profileData.value.inbody].map(record => {
    // 각 레코드의 속성 확인 및 로깅
    console.log(`레코드 ID: ${record.id || 'N/A'}, 측정일: ${record.measureDate || '날짜 없음'}`);
    
    // 각 필드가 존재하는지 확인하고 기본값 설정
    return {
      ...record,
      // 측정일이 없는 경우 null로 명시적 설정
      measureDate: record.measureDate !== undefined ? record.measureDate : null,
      // 다른 필드들도 null 체크
      weight: record.weight !== undefined ? record.weight : 0,
      muscleMass: record.muscleMass !== undefined ? record.muscleMass : 0,
      bodyFat: record.bodyFat !== undefined ? record.bodyFat : 0,
      bodyFatPercentage: record.bodyFatPercentage !== undefined ? record.bodyFatPercentage : 0,
      bmi: record.bmi !== undefined ? record.bmi : 0
    };
  });
  
  // 날짜별 정렬
  const sortedRecords = records.sort((a, b) => {
    // 날짜 필드가 없는 경우 처리
    if (!a.measureDate) return 1; // null 날짜는 맨 뒤로
    if (!b.measureDate) return -1;
    
    try {
      // 문자열 형태면 Date 객체로 변환
      const dateA = typeof a.measureDate === 'string' ? new Date(a.measureDate) : a.measureDate;
      const dateB = typeof b.measureDate === 'string' ? new Date(b.measureDate) : b.measureDate;
      
      // 객체 형태인 경우 (LocalDate)
      if (typeof a.measureDate === 'object' && a.measureDate !== null && !Array.isArray(a.measureDate) && !(a.measureDate instanceof Date)) {
        // LocalDate 형태일 가능성이 있음
        const formattedA = formatDate(a.measureDate);
        const formattedB = formatDate(b.measureDate);
        return new Date(formattedB) - new Date(formattedA);
      }
      
      // 일반적인 날짜 비교
      if (dateA instanceof Date && dateB instanceof Date) {
        return dateB - dateA; // 최신 날짜가 위로
      }
      
      // 날짜 문자열 비교
      return formatDate(b.measureDate).localeCompare(formatDate(a.measureDate));
    } catch (e) {
      console.error("날짜 정렬 오류:", e, a.measureDate, b.measureDate);
      return 0;
    }
  });
  
  console.log("정렬된 인바디 데이터:", sortedRecords);
  return sortedRecords;
});

// 두 자리 숫자 포맷팅 함수
const formatTwoDigits = (num) => {
  return num < 10 ? '0' + num : num;
};

// 달력 데이터 생성
const calendarMonths = computed(() => {
  const months = [];
  const today = new Date();
  
  // 최근 6개월 표시
  for (let i = 0; i < 6; i++) {
    const date = new Date(today.getFullYear(), today.getMonth() - i, 1);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const daysInMonth = new Date(year, month, 0).getDate();
    const firstDay = new Date(year, month - 1, 1).getDay(); // 0: 일요일, 1: 월요일, ...
    
    months.unshift({
      year,
      month,
      label: `${month}월`,
      days: Array.from({ length: daysInMonth }, (_, i) => i + 1),
      firstDay
    });
  }
  
  return months;
});

// 요일 라벨 반환
const getDayLabel = (day) => {
  const days = ['일', '월', '화', '수', '목', '금', '토'];
  return days[day - 1];
};

// 날짜가 챌린지 완료된 날인지 확인
const isDateCompleted = (dateStr) => {
  if (!profileData.value.challengeSummary?.streakCalendar) return false;
  
  return !!profileData.value.challengeSummary.streakCalendar[dateStr];
};

// 오늘 날짜인지 확인
const isToday = (dateStr) => {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const date = String(today.getDate()).padStart(2, '0');
  const todayStr = `${year}-${month}-${date}`;
  
  return dateStr === todayStr;
};

// 날짜 포맷팅
const formatDate = (dateStr) => {
  if (!dateStr) return '날짜 없음'; // 빈 값일 때 바로 '날짜 없음' 반환
  
  console.log("원본 날짜 문자열:", dateStr);
  
  // LocalDate 객체가 JSON으로 오는 경우
  // Java LocalDate는 보통 [year, month, day] 배열 형태나 특정 문자열로 변환됨
  if (Array.isArray(dateStr)) {
    try {
      // [2023, 1, 15] 형태 처리
      const [year, month, day] = dateStr;
      return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    } catch (e) {
      console.error("배열 형태 날짜 파싱 오류:", e);
    }
  }
  
  // 객체 형태로 오는 경우 ({year: 2023, month: 1, day: 15})
  if (typeof dateStr === 'object' && dateStr !== null && !Array.isArray(dateStr)) {
    try {
      const { year, month, day } = dateStr;
      if (year && month && day) {
        return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
      }
      
      // LocalDate가 다른 필드명으로 올 수도 있음
      if (dateStr.year !== undefined && dateStr.monthValue !== undefined && dateStr.dayOfMonth !== undefined) {
        return `${dateStr.year}-${String(dateStr.monthValue).padStart(2, '0')}-${String(dateStr.dayOfMonth).padStart(2, '0')}`;
      }
    } catch (e) {
      console.error("객체 형태 날짜 파싱 오류:", e);
    }
  }
  
  let date;
  
  try {
    // 타임스탬프 숫자인 경우
    if (typeof dateStr === 'number') {
      date = new Date(dateStr);
    } 
    // Date 객체인 경우
    else if (dateStr instanceof Date) {
      date = dateStr;
    }
    // 문자열인 경우
    else if (typeof dateStr === 'string') {
      // 먼저 ISO 문자열로 시도
      date = new Date(dateStr);
      
      // 유효하지 않은 날짜라면 다른 포맷 시도
      if (isNaN(date.getTime())) {
        // YYYY-MM-DD 형식 처리
        if (dateStr.includes('-')) {
          const parts = dateStr.split('-');
          if (parts.length === 3) {
            date = new Date(parseInt(parts[0]), parseInt(parts[1]) - 1, parseInt(parts[2]));
          }
        } 
        // MM/DD/YYYY 형식 처리
        else if (dateStr.includes('/')) {
          const parts = dateStr.split('/');
          if (parts.length === 3) {
            // 미국식 날짜 형식 처리
            if (parts[0].length <= 2 && parts[1].length <= 2) {
              date = new Date(parseInt(parts[2]), parseInt(parts[0]) - 1, parseInt(parts[1]));
            } else {
              // 그 외 형식
              date = new Date(parseInt(parts[0]), parseInt(parts[1]) - 1, parseInt(parts[2]));
            }
          }
        }
      }
    }
    
    // 유효한 날짜인지 확인
    if (isNaN(date?.getTime())) {
      console.error("유효하지 않은 날짜:", dateStr);
      return '날짜 없음';
    }
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    
    return `${year}-${month}-${day}`;
  } catch (e) {
    console.error("날짜 파싱 오류:", e, "원본 데이터:", dateStr);
    return '날짜 없음';
  }
};
  
// 차트 생성 함수
const createWeightChart = () => {
  if (!profileData.value.inbody || profileData.value.inbody.length === 0) return;
  
  // 이전 차트 인스턴스 제거
  if (chartInstance) {
    chartInstance.destroy();
  }
  
  // 차트 데이터 준비
  const sortedData = [...profileData.value.inbody].sort((a, b) => 
    new Date(a.measureDate) - new Date(b.measureDate)
  );
  
  const labels = sortedData.map(record => formatDate(record.measureDate));
  const weightData = sortedData.map(record => record.weight);
  const muscleData = sortedData.map(record => record.muscleMass);
  const fatData = sortedData.map(record => record.bodyFat);
  
  // 차트 생성
  const ctx = weightChart.value.getContext('2d');
  chartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels,
      datasets: [
        {
          label: '체중',
          data: weightData,
          borderColor: '#007bff',
          backgroundColor: 'rgba(0, 123, 255, 0.1)',
          tension: 0.4,
          fill: false
        },
        {
          label: '골격근량',
          data: muscleData,
          borderColor: '#28a745',
          backgroundColor: 'rgba(40, 167, 69, 0.1)',
          tension: 0.4,
          fill: false
        },
        {
          label: '체지방량',
          data: fatData,
          borderColor: '#dc3545',
          backgroundColor: 'rgba(220, 53, 69, 0.1)',
          tension: 0.4,
          fill: false
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'top',
        },
        title: {
          display: false
        },
        tooltip: {
          mode: 'index',
          intersect: false
        }
      },
      scales: {
        y: {
          beginAtZero: false
        }
      }
    }
  });
};

// 프로필 정보 변경 시 차트 다시 그리기
watch(() => profileData.value, () => {
  if (!isLoading.value) {
    // DOM이 업데이트된 후 차트 생성
    setTimeout(() => {
      createWeightChart();
    }, 0);
  }
}, { deep: true });

onMounted(() => {
  // 컴포넌트 마운트 시 데이터 로드
  fetchProfileData();
});
</script>

<style scoped>
.activity-info {
  margin-top: 20px;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.streak-section, .inbody-section {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

h3 {
  color: #333;
  border-bottom: 2px solid #42b983;
  padding-bottom: 8px;
  margin-bottom: 20px;
}

h4 {
  color: #495057;
  margin-top: 15px;
  margin-bottom: 10px;
  font-weight: 600;
}

/* 스트릭 정보 스타일 */
.streak-info {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.streak-card {
  flex: 1;
  background-color: white;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.streak-value {
  font-size: 32px;
  font-weight: bold;
  color: #007bff;
}

.streak-label {
  font-size: 14px;
  color: #6c757d;
  margin-top: 5px;
}

/* 스트릭 달력 스타일 */
.streak-calendar {
  margin-top: 20px;
}

.calendar-grid {
  display: flex;
  overflow-x: auto;
  gap: 10px;
  padding-bottom: 10px;
}

.month-column {
  min-width: 200px;
}

.month-label {
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
  color: #495057;
}

.days-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}

.day-label {
  text-align: center;
  font-size: 12px;
  color: #6c757d;
  padding: 4px 0;
}

.day-cell {
  text-align: center;
  padding: 6px 0;
  font-size: 12px;
  border-radius: 4px;
  background-color: #e9ecef;
}

.day-cell.empty {
  background-color: transparent;
}

.day-cell.completed {
  background-color: #42b983;
  color: white;
}

.day-cell.today {
  border: 2px solid #007bff;
  font-weight: bold;
}

.streak-legend {
  display: flex;
  gap: 20px;
  margin-top: 15px;
  justify-content: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.legend-color.completed {
  background-color: #42b983;
}

.legend-color.today {
  border: 2px solid #007bff;
  background-color: #e9ecef;
}

/* 인바디 차트 스타일 */
.inbody-chart {
  margin-bottom: 30px;
}

.chart-container {
  height: 300px;
  position: relative;
}

/* 인바디 테이블 스타일 */
.inbody-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

th, td {
  padding: 12px 15px;
  text-align: center;
  border-bottom: 1px solid #dee2e6;
}

th {
  background-color: #e9ecef;
  font-weight: bold;
  color: #495057;
}

tr:hover {
  background-color: #f1f3f5;
}

.no-inbody {
  text-align: center;
  padding: 40px 0;
  color: #6c757d;
  font-style: italic;
}

@media (max-width: 768px) {
  .streak-info {
    flex-direction: column;
    gap: 10px;
  }
  
  .calendar-grid {
    flex-direction: column;
  }
  
  .month-column {
    min-width: 100%;
    margin-bottom: 20px;
  }
}
</style>