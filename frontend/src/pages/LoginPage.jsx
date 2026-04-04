import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import toast from 'react-hot-toast'
import { FiMail, FiLock } from 'react-icons/fi'

export default function LoginPage() {
    const { login } = useAuth()
    const navigate = useNavigate()
    const [form, setForm] = useState({ email: '', password: '' })
    const [loading, setLoading] = useState(false)
    const [errors, setErrors] = useState({})

    const validate = () => {
        const errs = {}
        if (!form.email) errs.email = 'E-posta adresi gerekli'
        else if (!/\S+@\S+\.\S+/.test(form.email)) errs.email = 'Geçerli bir e-posta girin'
        if (!form.password) errs.password = 'Şifre gerekli'
        setErrors(errs)
        return Object.keys(errs).length === 0
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        if (!validate()) return

        setLoading(true)
        try {
            await login(form.email, form.password)
            toast.success('Giriş başarılı!')
            navigate('/')
        } catch (err) {
            const msg = err.response?.data?.message || 'Giriş başarısız. Bilgilerinizi kontrol edin.'
            toast.error(msg)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="auth-container animate-in">
            <div className="auth-card">
                <h2>Giriş Yap</h2>
                <p className="auth-subtitle">Hesabınıza giriş yaparak alışverişe başlayın</p>

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label"><FiMail /> E-posta</label>
                        <input
                            id="login-email"
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
                            id="login-password"
                            type="password"
                            className={`form-input ${errors.password ? 'error' : ''}`}
                            placeholder="••••••••"
                            value={form.password}
                            onChange={(e) => setForm({ ...form, password: e.target.value })}
                        />
                        {errors.password && <p className="form-error">{errors.password}</p>}
                    </div>

                    <button id="login-submit" type="submit" className="btn btn-primary btn-block" disabled={loading}>
                        {loading ? 'Giriş yapılıyor...' : 'Giriş Yap'}
                    </button>
                </form>

                <div className="auth-divider">veya</div>

                <Link to="/reset-password" style={{ display: 'block', textAlign: 'center', fontSize: '0.85rem' }}>
                    Şifremi Unuttum
                </Link>

                <div className="auth-footer">
                    Hesabınız yok mu? <Link to="/register">Kayıt Olun</Link>
                </div>
            </div>
        </div>
    )
}
