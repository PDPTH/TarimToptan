import { createContext, useContext, useState, useEffect } from 'react'
import { login as loginApi, register as registerApi } from '../modules/suleyman/authService.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null)
    const [token, setToken] = useState(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const savedToken = localStorage.getItem('auth_token')
        const savedUser = localStorage.getItem('auth_user')
        if (savedToken && savedUser) {
            try {
                setToken(savedToken)
                setUser(JSON.parse(savedUser))
            } catch {
                localStorage.removeItem('auth_token')
                localStorage.removeItem('auth_user')
            }
        }
        setLoading(false)
    }, [])

    const login = async (email, password) => {
        const data = await loginApi({ email, password })
        const tkn = data.token
        const usr = data.user
        setToken(tkn)
        setUser(usr)
        localStorage.setItem('auth_token', tkn)
        localStorage.setItem('auth_user', JSON.stringify(usr))
        return data
    }

    const register = async (userData) => {
        const data = await registerApi(userData)
        return data
    }

    const logout = () => {
        setToken(null)
        setUser(null)
        localStorage.removeItem('auth_token')
        localStorage.removeItem('auth_user')
    }

    const updateUser = (updatedUser) => {
        setUser(updatedUser)
        localStorage.setItem('auth_user', JSON.stringify(updatedUser))
    }

    const value = {
        user,
        token,
        loading,
        isAuthenticated: !!token,
        login,
        register,
        logout,
        updateUser,
    }

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
    const context = useContext(AuthContext)
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider')
    }
    return context
}

export default AuthContext
