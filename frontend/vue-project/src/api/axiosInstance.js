import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:8080',
    timeout:5000
})

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
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
