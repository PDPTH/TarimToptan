/**
 * Süleyman Modülü — Auth, User, Producers API Servisleri
 * 
 * Endpoints:
 * - POST /auth/register
 * - POST /auth/login
 * - POST /auth/reset-password-request
 * - PUT /users/{userId}
 * - DELETE /users/{userId}
 * - GET /producers
 * - GET /producers/{producerId}
 */

import api from '../../services/api/api.js'

// Demo kullanıcı veritabanı (localStorage'da saklanır)
function getDemoUsers() {
    try {
        return JSON.parse(localStorage.getItem('demo_users') || '[]')
    } catch { return [] }
}

function saveDemoUsers(users) {
    localStorage.setItem('demo_users', JSON.stringify(users))
}

// ========================
// AUTH
// ========================

export async function register(userData) {
    try {
        const res = await api.post('/auth/register', userData)
        return res.data
    } catch (err) {
        // API erişilemezse demo mod
        if (!err.response || err.code === 'ERR_NETWORK' || err.message?.includes('Network Error')) {
            console.warn('⚠️ API erişilemedi — Demo mod ile kayıt yapılıyor')
            const users = getDemoUsers()
            const exists = users.find(u => u.email === userData.email)
            if (exists) {
                throw { response: { data: { message: 'Bu e-posta adresi zaten kayıtlı.' } } }
            }
            const newUser = {
                id: 'demo-' + Date.now(),
                email: userData.email,
                password: userData.password,
                firstName: userData.firstName,
                lastName: userData.lastName,
                createdAt: new Date().toISOString(),
            }
            users.push(newUser)
            saveDemoUsers(users)
            return { message: 'Kayıt başarılı (Demo Mod)', user: { ...newUser, password: undefined } }
        }
        throw err
    }
}

export async function login(credentials) {
    try {
        const res = await api.post('/auth/login', credentials)
        return res.data
    } catch (err) {
        // API erişilemezse demo mod
        if (!err.response || err.code === 'ERR_NETWORK' || err.message?.includes('Network Error')) {
            console.warn('⚠️ API erişilemedi — Demo mod ile giriş yapılıyor')
            const users = getDemoUsers()
            const user = users.find(u => u.email === credentials.email && u.password === credentials.password)
            if (!user) {
                throw { response: { data: { message: 'E-posta veya şifre hatalı. Önce kayıt olun.' } } }
            }
            const { password, ...safeUser } = user
            return {
                token: 'demo-token-' + Date.now(),
                user: safeUser,
            }
        }
        throw err
    }
}

export async function resetPasswordRequest(email) {
    try {
        const res = await api.post('/auth/reset-password-request', { email })
        return res.data
    } catch (err) {
        if (!err.response || err.code === 'ERR_NETWORK') {
            console.warn('⚠️ API erişilemedi — Demo mod')
            return { message: 'Şifre sıfırlama bağlantısı gönderildi (Demo Mod)' }
        }
        throw err
    }
}

// ========================
// USER
// ========================

export async function updateProfile(userId, data) {
    const res = await api.put(`/users/${userId}`, data)
    return res.data
}

export async function deleteAccount(userId) {
    const res = await api.delete(`/users/${userId}`)
    return res.data
}

// ========================
// PRODUCERS
// ========================

export async function getProducers(params = {}) {
    const res = await api.get('/producers', { params })
    return res.data
}

export async function getProducerById(producerId) {
    const res = await api.get(`/producers/${producerId}`)
    return res.data
}
