import axios from 'axios'
// Pinia store는 이 파일에서 직접 접근하지 않습니다.
// localStorage 접근은 여기서만 허용(스토어 동기화는 App.vue 등에서 처리)

const api = axios.create({
    baseURL: 'http://localhost:8080',
    timeout:999999999
})

api.interceptors.request.use((config) => {
    
    const whiteList = ['/api_auth/authenticate', '/api_user/post/signup', '/images/profile/', '/images/background/']
    const requestUrl = new URL(config.url, config.baseURL).pathname

    if (whiteList.some(path => requestUrl.startsWith(path))) {
        return config
    }

    // 정지 기간 체크
    const userStr = localStorage.getItem('user')
    if (userStr) {
        try {
            const user = JSON.parse(userStr)
            if (user.suspendStart && user.suspendEnd) {
                const now = new Date()
                const start = new Date(user.suspendStart)
                const end = new Date(user.suspendEnd)
                if (
                    requestUrl.startsWith('/api_article') &&
                    now >= start && now <= end
                ) {
                    alert('정지 기간 중에는 게시판 관련 기능을 이용할 수 없습니다.')
                    throw new axios.Cancel("정지 기간 중 접근 불가")
                }
            }
        } catch (e) {
            // 파싱 에러 무시
        }
    }

    const token = localStorage.getItem('token')
    
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    } else {
        window.location.href = '/login'

        throw new axios.Cancel("로그인하지 않으면 해당 서비스를 이용할 수 없습니다.")
    }

    return config
})

api.interceptors.response.use(
    res => res,
    
    err => {
        if (err.response && err.response.status === 401) {
            const message = err.response.data?.message || '로그인이 만료되었습니다.'
            alert(message)

            localStorage.clear()
            window.location.href = '/login'
        }

        return Promise.reject(err)
    }
)

export default api
