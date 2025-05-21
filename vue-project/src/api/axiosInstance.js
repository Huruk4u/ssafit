import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:8080',
    timeout:10000
})

api.interceptors.request.use((config) => {
    
    const whiteList = ['/api_auth/authenticate', '/api_user/post/signup', '/images/profile/', '/images/background/']
    const requestUrl = new URL(config.url, config.baseURL).pathname


    
    if (whiteList.some(path => requestUrl.startsWith(path))) {
        return config
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
