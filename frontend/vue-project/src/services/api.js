import axios from 'axios'

export default {
  init(baseURL) {
    axios.defaults.baseURL = baseURL
    const token = localStorage.getItem('jwt_token')
    if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
  },

  setToken(token) {
    localStorage.setItem('jwt_token', token)
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
  },

  removeToken() {
    localStorage.removeItem('jwt_token')
    delete axios.defaults.headers.common['Authorization']
  },

  login(credentials) {
    return axios.post('/api_auth/authenticate', credentials)
  },

  logout() {
    return axios.post('/api_auth/logout')
  },

  signUp(form) {
    return axios.post('/api_user/post/signup', form)
  },

  getUserProfile() {
    return axios.get('/api_mypage/profile')
  },

  getUserSummary(userId) {
    return axios.get(`/api_mypage/summary/userId/${userId}`)
  }
}