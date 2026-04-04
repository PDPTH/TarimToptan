import { useState } from 'react'
import { Link } from 'react-router-dom'
import { resetPasswordRequest } from '../modules/suleyman/authService.js'
import toast from 'react-hot-toast'
import { FiMail } from 'react-icons/fi'

export default function ResetPasswordPage() {
    const [email, setEmail] = useState('')
    const [loading, setLoading] = useState(false)
    const [sent, setSent] = useState(false)

    const handleSubmit = async (e) => {
        e.preventDefault()
        if (!email) {
            toast.error('E-posta adresi gerekli')
            return
        }

        setLoading(true)
        try {
            await resetPasswordRequest(email)
            setSent(true)
            toast.success('Şifre sıfırlama bağlantısı gönderildi!')
        } catch (err) {
            const msg = err.response?.data?.message || 'İşlem başarısız. Lütfen tekrar deneyin.'
            toast.error(msg)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="auth-container animate-in">
            <div className="auth-card">
                <h2>Şifre Sıfırlama</h2>
                <p className="auth-subtitle">E-posta adresinize şifre sıfırlama bağlantısı göndereceğiz</p>

                {sent ? (
                    <div className="empty-state" style={{ padding: '24px 0' }}>
                        <div className="empty-icon">✉️</div>
                        <h3>Bağlantı Gönderildi!</h3>
                        <p>E-posta kutunuzu kontrol edin ve şifrenizi sıfırlayın.</p>
                        <Link to="/login" className="btn btn-primary" style={{ marginTop: '16px' }}>
                            Giriş Sayfasına Dön
                        </Link>
                    </div>
                ) : (
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label className="form-label"><FiMail /> E-posta</label>
                            <input
                                id="reset-email"
                                type="email"
                                className="form-input"
                                placeholder="ornek@email.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                        </div>

                        <button id="reset-submit" type="submit" className="btn btn-primary btn-block" disabled={loading}>
                            {loading ? 'Gönderiliyor...' : 'Sıfırlama Bağlantısı Gönder'}
                        </button>
                    </form>
                )}

                <div className="auth-footer">
                    <Link to="/login">Giriş sayfasına dön</Link>
                </div>
            </div>
        </div>
    )
}
