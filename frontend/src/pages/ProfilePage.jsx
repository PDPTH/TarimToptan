import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { updateProfile, deleteAccount } from '../modules/suleyman/authService.js'
import toast from 'react-hot-toast'
import { FiUser, FiMail, FiPhone, FiTrash2, FiSave } from 'react-icons/fi'

export default function ProfilePage() {
    const { user, updateUser, logout } = useAuth()
    const navigate = useNavigate()

    const [form, setForm] = useState({
        firstName: user?.firstName || '',
        lastName: user?.lastName || '',
        email: user?.email || '',
        phone: user?.phone || '',
    })
    const [loading, setLoading] = useState(false)
    const [deleteConfirm, setDeleteConfirm] = useState(false)

    const handleUpdate = async (e) => {
        e.preventDefault()
        setLoading(true)
        try {
            const updated = await updateProfile(user.id, form)
            updateUser(updated)
            toast.success('Profil başarıyla güncellendi!')
        } catch (err) {
            toast.error(err.response?.data?.message || 'Güncelleme başarısız.')
        } finally {
            setLoading(false)
        }
    }

    const handleDelete = async () => {
        if (!deleteConfirm) {
            setDeleteConfirm(true)
            return
        }
        try {
            await deleteAccount(user.id)
            toast.success('Hesabınız silindi.')
            logout()
            navigate('/')
        } catch (err) {
            toast.error(err.response?.data?.message || 'Hesap silinemedi.')
        }
    }

    return (
        <div className="animate-in">
            <div className="page-header">
                <h1><FiUser /> Profilim</h1>
                <p>Kişisel bilgilerinizi görüntüleyin ve güncelleyin</p>
            </div>

            <div className="profile-grid">
                {/* Profile Update Form */}
                <div className="card">
                    <h3 style={{ marginBottom: 'var(--space-lg)', fontSize: '1.1rem' }}>Profil Bilgileri</h3>
                    <form onSubmit={handleUpdate}>
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                            <div className="form-group">
                                <label className="form-label">Ad</label>
                                <input
                                    id="profile-firstname"
                                    type="text"
                                    className="form-input"
                                    value={form.firstName}
                                    onChange={(e) => setForm({ ...form, firstName: e.target.value })}
                                />
                            </div>
                            <div className="form-group">
                                <label className="form-label">Soyad</label>
                                <input
                                    id="profile-lastname"
                                    type="text"
                                    className="form-input"
                                    value={form.lastName}
                                    onChange={(e) => setForm({ ...form, lastName: e.target.value })}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="form-label"><FiMail /> E-posta</label>
                            <input
                                id="profile-email"
                                type="email"
                                className="form-input"
                                value={form.email}
                                onChange={(e) => setForm({ ...form, email: e.target.value })}
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label"><FiPhone /> Telefon</label>
                            <input
                                id="profile-phone"
                                type="text"
                                className="form-input"
                                placeholder="+905551234567"
                                value={form.phone}
                                onChange={(e) => setForm({ ...form, phone: e.target.value })}
                            />
                        </div>

                        <button id="profile-update" type="submit" className="btn btn-primary" disabled={loading}>
                            <FiSave /> {loading ? 'Kaydediliyor...' : 'Değişiklikleri Kaydet'}
                        </button>
                    </form>
                </div>

                {/* Account Info & Danger Zone */}
                <div>
                    <div className="card" style={{ marginBottom: 'var(--space-lg)' }}>
                        <h3 style={{ marginBottom: 'var(--space-md)', fontSize: '1.1rem' }}>Hesap Bilgileri</h3>
                        <div style={{ fontSize: '0.9rem', color: 'var(--color-text-secondary)' }}>
                            <p><strong>Kullanıcı ID:</strong> {user?.id}</p>
                            <p style={{ marginTop: '8px' }}><strong>Kayıt Tarihi:</strong> {user?.createdAt ? new Date(user.createdAt).toLocaleDateString('tr-TR') : '-'}</p>
                        </div>
                    </div>

                    <div className="card" style={{ borderColor: 'rgba(239, 83, 80, 0.3)' }}>
                        <h3 style={{ marginBottom: 'var(--space-md)', fontSize: '1.1rem', color: 'var(--color-error)' }}>
                            <FiTrash2 /> Tehlikeli Bölge
                        </h3>
                        <p style={{ fontSize: '0.85rem', color: 'var(--color-text-muted)', marginBottom: 'var(--space-md)' }}>
                            Hesabınızı sildiğinizde tüm verileriniz kalıcı olarak silinir. Bu işlem geri alınamaz.
                        </p>
                        <button
                            id="profile-delete"
                            className="btn btn-danger"
                            onClick={handleDelete}
                        >
                            <FiTrash2 /> {deleteConfirm ? 'Emin misiniz? Silmek için tekrar tıklayın' : 'Hesabı Sil'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}
