import axios from 'axios'

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'https://api.yazmuh.com/v1',
    timeout: 30000,
    headers: {
        'Content-Type': 'application/json',
    },
})

// Request interceptor — JWT token ekleme
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('auth_token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

// Response interceptor — hata yakalama
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            const { status } = error.response

            if (status === 401) {
                localStorage.removeItem('auth_token')
                localStorage.removeItem('auth_user')
                window.location.href = '/login'
            }
        }

        return Promise.reject(error)
    }
)

export default api
