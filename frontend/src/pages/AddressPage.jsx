import { useState } from 'react'
import { createAddress, updateAddress } from '../modules/aykhan/cartService'
import toast from 'react-hot-toast'
import { FiMapPin, FiPlus, FiSave, FiEdit } from 'react-icons/fi'

export default function AddressPage() {
    const [addresses, setAddresses] = useState([])
    const [showForm, setShowForm] = useState(false)
    const [editId, setEditId] = useState(null)
    const [loading, setLoading] = useState(false)
    const [form, setForm] = useState({
        title: 'Ev',
        fullName: '',
        phone: '',
        city: '',
        district: '',
        addressLine: '',
        postalCode: '',
    })

    const resetForm = () => {
        setForm({ title: 'Ev', fullName: '', phone: '', city: '', district: '', addressLine: '', postalCode: '' })
        setEditId(null)
        setShowForm(false)
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        if (!form.fullName || !form.city || !form.addressLine || !form.postalCode) {
            toast.error('Lütfen zorunlu alanları doldurun.')
            return
        }

        setLoading(true)
        try {
            if (editId) {
                const updated = await updateAddress(editId, form)
                setAddresses(addresses.map((a) => (a.id === editId ? updated : a)))
                toast.success('Adres güncellendi!')
            } else {
                const created = await createAddress(form)
                setAddresses([...addresses, created])
                toast.success('Adres eklendi!')
            }
            resetForm()
        } catch (err) {
            toast.error(err.response?.data?.message || 'İşlem başarısız.')
        } finally {
            setLoading(false)
        }
    }

    const handleEdit = (address) => {
        setForm({
            title: address.title || 'Ev',
            fullName: address.fullName || '',
            phone: address.phone || '',
            city: address.city || '',
            district: address.district || '',
            addressLine: address.addressLine || '',
            postalCode: address.postalCode || '',
        })
        setEditId(address.id)
        setShowForm(true)
    }

    return (
        <div className="animate-in">
            <div className="page-header flex-between">
                <div>
                    <h1><FiMapPin /> Adreslerim</h1>
                    <p>Teslimat adreslerinizi yönetin</p>
                </div>
                <button className="btn btn-primary" onClick={() => { resetForm(); setShowForm(true) }}>
                    <FiPlus /> Yeni Adres
                </button>
            </div>

            {/* Address Form */}
            {showForm && (
                <div className="card" style={{ marginBottom: '24px' }}>
                    <h3 style={{ marginBottom: '16px', fontSize: '1.1rem' }}>
                        {editId ? 'Adresi Güncelle' : 'Yeni Adres Ekle'}
                    </h3>
                    <form onSubmit={handleSubmit}>
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                            <div className="form-group">
                                <label className="form-label">Başlık</label>
                                <select
                                    className="form-input"
                                    value={form.title}
                                    onChange={(e) => setForm({ ...form, title: e.target.value })}
                                >
                                    <option value="Ev">Ev</option>
                                    <option value="İş">İş</option>
                                    <option value="Diğer">Diğer</option>
                                </select>
                            </div>
                            <div className="form-group">
                                <label className="form-label">Ad Soyad *</label>
                                <input
                                    id="address-fullname"
                                    type="text"
                                    className="form-input"
                                    placeholder="Ahmet Yılmaz"
                                    value={form.fullName}
                                    onChange={(e) => setForm({ ...form, fullName: e.target.value })}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="form-label">Telefon</label>
                            <input
                                id="address-phone"
                                type="text"
                                className="form-input"
                                placeholder="+905551234567"
                                value={form.phone}
                                onChange={(e) => setForm({ ...form, phone: e.target.value })}
                            />
                        </div>

                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
                            <div className="form-group">
                                <label className="form-label">İl *</label>
                                <input
                                    id="address-city"
                                    type="text"
                                    className="form-input"
                                    placeholder="İstanbul"
                                    value={form.city}
                                    onChange={(e) => setForm({ ...form, city: e.target.value })}
                                />
                            </div>
                            <div className="form-group">
                                <label className="form-label">İlçe</label>
                                <input
                                    id="address-district"
                                    type="text"
                                    className="form-input"
                                    placeholder="Kadıköy"
                                    value={form.district}
                                    onChange={(e) => setForm({ ...form, district: e.target.value })}
                                />
                            </div>
                            <div className="form-group">
                                <label className="form-label">Posta Kodu *</label>
                                <input
                                    id="address-postal"
                                    type="text"
                                    className="form-input"
                                    placeholder="34710"
                                    value={form.postalCode}
                                    onChange={(e) => setForm({ ...form, postalCode: e.target.value })}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="form-label">Adres Satırı *</label>
                            <textarea
                                id="address-line"
                                className="form-input"
                                placeholder="Mahalle, sokak, bina no, daire no..."
                                value={form.addressLine}
                                onChange={(e) => setForm({ ...form, addressLine: e.target.value })}
                            />
                        </div>

                        <div style={{ display: 'flex', gap: '12px' }}>
                            <button id="address-submit" type="submit" className="btn btn-primary" disabled={loading}>
                                <FiSave /> {loading ? 'Kaydediliyor...' : (editId ? 'Güncelle' : 'Ekle')}
                            </button>
                            <button type="button" className="btn btn-secondary" onClick={resetForm}>
                                İptal
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {/* Address List */}
            {addresses.length > 0 ? (
                <div className="address-grid">
                    {addresses.map((addr) => (
                        <div key={addr.id} className="card address-card">
                            <h4>{addr.title}</h4>
                            <p>
                                <strong>{addr.fullName}</strong><br />
                                {addr.addressLine}<br />
                                {addr.district && `${addr.district}, `}{addr.city} {addr.postalCode}<br />
                                {addr.phone}
                            </p>
                            <div className="address-card-actions">
                                <button className="btn btn-secondary btn-sm" onClick={() => handleEdit(addr)}>
                                    <FiEdit /> Düzenle
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            ) : (
                !showForm && (
                    <div className="empty-state">
                        <div className="empty-icon">📍</div>
                        <h3>Henüz adres eklenmemiş</h3>
                        <p>Yeni bir teslimat adresi ekleyerek siparişlerinizi kolaylaştırın.</p>
                    </div>
                )
            )}
        </div>
    )
}
