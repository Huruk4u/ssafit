<!-- src/components/MyPage/BasicTab.vue -->
<template>
  <div class="basic-tab">
    <h3>챌린지 스트릭 – {{ year }}년 {{ month + 1 }}월</h3>

    <table class="calendar">
      <thead>
        <tr>
          <th v-for="d in weekdays" :key="d">{{ d }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(week, wi) in weeks" :key="wi">
          <td
            v-for="day in week"
            :key="day.dateKey"
            :class="{
              empty: !day.date,
              done: day.active,
              today: day.isToday
            }"
          >
            {{ day.date || '' }}
          </td>
        </tr>
      </tbody>
    </table>

    <div class="streak-info">
      <div>현재 연속 스트릭: <strong>{{ currentStreak }}</strong>일</div>
      <div>월 최고 스트릭: <strong>{{ maxStreak }}</strong>일</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BasicTab',
  props: {
    // 서버에서 받아오는 날짜별 완료 여부 Map
    dateMap: {
      type: Object,
      default: () => ({
        // 예시
        '2025-05-10': true,
        '2025-05-11': true,
        '2025-05-12': false,
        // ...
      })
    }
  },
  data() {
    const today = new Date()
    return {
      year: today.getFullYear(),
      month: today.getMonth(), // 0-based index
      weekdays: ['일', '월', '화', '수', '목', '금', '토'],
      weeks: [],
      currentStreak: 0,
      maxStreak: 0
    }
  },
  created() {
    this.buildCalendar()
    this.computeStreaks()
  },
  methods: {
    buildCalendar() {
      const firstDay = new Date(this.year, this.month, 1)
      const lastDay = new Date(this.year, this.month + 1, 0)
      const startWeekday = firstDay.getDay()
      const totalDays = lastDay.getDate()
      const weeks = []
      let dayCounter = 1 - startWeekday

      for (let w = 0; w < 6; w++) {
        const week = []
        for (let d = 0; d < 7; d++, dayCounter++) {
          const dayObj = { date: null, dateKey: '', active: false, isToday: false }
          if (dayCounter > 0 && dayCounter <= totalDays) {
            const dateStr = this.formatYmd(this.year, this.month, dayCounter)
            dayObj.date = dayCounter
            dayObj.dateKey = dateStr
            dayObj.active = !!this.dateMap[dateStr]
            const todayStr = this.formatYmd(
              new Date().getFullYear(),
              new Date().getMonth(),
              new Date().getDate()
            )
            dayObj.isToday = dateStr === todayStr
          }
          week.push(dayObj)
        }
        weeks.push(week)
      }
      this.weeks = weeks
    },
    computeStreaks() {
      let curr = 0, max = 0
      const lastDay = new Date(this.year, this.month + 1, 0).getDate()
      for (let d = 1; d <= lastDay; d++) {
        const dateStr = this.formatYmd(this.year, this.month, d)
        if (this.dateMap[dateStr]) {
          curr++
          max = Math.max(max, curr)
        } else {
          curr = 0
        }
      }
      // 오늘 기준 연속
      let todayStreak = 0
      const todayNum = new Date().getDate()
      for (let d = todayNum; d >= 1; d--) {
        const dateStr = this.formatYmd(this.year, this.month, d)
        if (this.dateMap[dateStr]) todayStreak++
        else break
      }
      this.currentStreak = todayStreak
      this.maxStreak = max
    },
    formatYmd(y, m0, d) {
      const mm = String(m0 + 1).padStart(2, '0')
      const dd = String(d).padStart(2, '0')
      return `${y}-${mm}-${dd}`
    }
  }
}
</script>

<style scoped>
.basic-tab {
  max-width: 600px;
  margin: 0 auto;
  text-align: center;
}
.calendar {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}
.calendar th,
.calendar td {
  border: 1px solid #ddd;
  width: 14.28%;
  height: 50px;
  vertical-align: middle;
}
.calendar th {
  background: #f0f0f0;
}
.calendar td.empty {
  background: #fafafa;
}
.calendar td.done {
  background: #8fd5a2;
  color: #fff;
}
.calendar td.today {
  border: 2px solid #ff8c00;
}
.streak-info {
  display: flex;
  justify-content: space-around;
  margin-top: 1rem;
  font-size: 1.1rem;
}
.streak-info strong {
  color: #2c3e50;
}
</style>
