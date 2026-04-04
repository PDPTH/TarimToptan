import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import toast from 'react-hot-toast'
import { FiMail, FiLock, FiUser } from 'react-icons/fi'

export default function RegisterPage() {
    const { register } = useAuth()
    const navigate = useNavigate()
    const [form, setForm] = useState({
        email: '',
        password: '',
        confirmPassword: '',
        firstName: '',
        lastName: '',
    })
    const [loading, setLoading] = useState(false)
    const [errors, setErrors] = useState({})

    const validate = () => {
        const errs = {}
        if (!form.firstName || form.firstName.length < 2) errs.firstName = 'Ad en az 2 karakter olmalı'
        if (!form.lastName || form.lastName.length < 2) errs.lastName = 'Soyad en az 2 karakter olmalı'
        if (!form.email) errs.email = 'E-posta adresi gerekli'
        else if (!/\S+@\S+\.\S+/.test(form.email)) errs.email = 'Geçerli bir e-posta girin'
        if (!form.password || form.password.length < 8) errs.password = 'Şifre en az 8 karakter olmalı'
        if (form.password !== form.confirmPassword) errs.confirmPassword = 'Şifreler eşleşmiyor'
        setErrors(errs)
        return Object.keys(errs).length === 0
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        if (!validate()) return

        setLoading(true)
        try {
            await register({
                email: form.email,
                password: form.password,
                firstName: form.firstName,
                lastName: form.lastName,
            })
            toast.success('Kayıt başarılı! Giriş yapabilirsiniz.')
            navigate('/login')
        } catch (err) {
            const msg = err.response?.data?.message || 'Kayıt başarısız. Lütfen tekrar deneyin.'
            toast.error(msg)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="auth-container animate-in">
            <div className="auth-card">
                <h2>Kayıt Ol</h2>
                <p className="auth-subtitle">Hemen üye olun, tarım ürünlerini toptan fiyatlarla alın</p>

                <form onSubmit={handleSubmit}>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                        <div className="form-group">
                            <label className="form-label"><FiUser /> Ad</label>
                            <input
                                id="register-firstname"
                                type="text"
                                className={`form-input ${errors.firstName ? 'error' : ''}`}
                                placeholder="Ahmet"
                                value={form.firstName}
                                onChange={(e) => setForm({ ...form, firstName: e.target.value })}
                            />
                            {errors.firstName && <p className="form-error">{errors.firstName}</p>}
                        </div>
                        <div className="form-group">
                            <label className="form-label">Soyad</label>
                            <input
                                id="register-lastname"
                                type="text"
                                className={`form-input ${errors.lastName ? 'error' : ''}`}
                                placeholder="Yılmaz"
                                value={form.lastName}
                                onChange={(e) => setForm({ ...form, lastName: e.target.value })}
                            />
                            {errors.lastName && <p className="form-error">{errors.lastName}</p>}
                        </div>
                    </div>

                    <div className="form-group">
                        <label className="form-label"><FiMail /> E-posta</label>
                        <input
                            id="register-email"
                            type="email"
                            className={`form-input ${errors.email ? 'error' : ''}`}
                            placeholder="ornek@email.com"
                            value={form.email}
                            onChange={(e) => setForm({ ...form, email: e.target.value })}
                        />
                        {errors.email && <p className="form-error">{errors.email}</p>}
                    </div>

                    <div className="form-group">
                        <label className="form-label"><FiLock /> Şifre</label>
                        <input
                            id="register-password"
                            type="password"
                            className={`form-input ${errors.password ? 'error' : ''}`}
                            placeholder="En az 8 karakter"
                            value={form.password}
                            onChange={(e) => setForm({ ...form, password: e.target.value })}
                        />
                        {errors.password && <p className="form-error">{errors.password}</p>}
                    </div>

                    <div className="form-group">
                        <label className="form-label"><FiLock /> Şifre Tekrar</label>
                        <input
                            id="register-confirm-password"
                            type="password"
                            className={`form-input ${errors.confirmPassword ? 'error' : ''}`}
                            placeholder="Şifrenizi tekrar girin"
                            value={form.confirmPassword}
                            onChange={(e) => setForm({ ...form, confirmPassword: e.target.value })}
                        />
                        {errors.confirmPassword && <p className="form-error">{errors.confirmPassword}</p>}
                    </div>

                    <button id="register-submit" type="submit" className="btn btn-primary btn-block" disabled={loading}>
                        {loading ? 'Kayıt yapılıyor...' : 'Kayıt Ol'}
                    </button>
                </form>

                <div className="auth-footer">
                    Zaten hesabınız var mı? <Link to="/login">Giriş Yapın</Link>
                </div>
            </div>
        </div>
    )
}
