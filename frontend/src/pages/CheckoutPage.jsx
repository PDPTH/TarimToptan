import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getCart } from '../modules/aykhan/cartService'
import { createOrder, cancelOrder } from '../modules/aykhan/cartService'
import { createAddress } from '../modules/aykhan/cartService'
import LoadingSpinner from '../components/LoadingSpinner.jsx'
import toast from 'react-hot-toast'
import { FiCheck, FiMapPin } from 'react-icons/fi'

export default function CheckoutPage() {
    const navigate = useNavigate()
    const [cart, setCart] = useState(null)
    const [loading, setLoading] = useState(true)
    const [submitting, setSubmitting] = useState(false)
    const [orderCreated, setOrderCreated] = useState(null)

    const [addressForm, setAddressForm] = useState({
        title: 'Ev',
        fullName: '',
        phone: '',
        city: '',
        district: '',
        addressLine: '',
        postalCode: '',
    })

    useEffect(() => {
        async function fetchCart() {
            try {
                const data = await getCart()
                setCart(data)
            } catch (err) {
                console.error('Sepet yüklenemedi:', err)
            } finally {
                setLoading(false)
            }
        }
        fetchCart()
    }, [])

    const handleSubmit = async (e) => {
        e.preventDefault()
        if (!addressForm.fullName || !addressForm.city || !addressForm.addressLine) {
            toast.error('Lütfen adres bilgilerini doldurun.')
            return
        }

        setSubmitting(true)
        try {
            // 1. Adres oluştur
            const address = await createAddress(addressForm)
            const addressId = address.id

            // 2. Sipariş oluştur
            const order = await createOrder({ addressId })
            setOrderCreated(order)
            toast.success('Siparişiniz başarıyla oluşturuldu! 🎉')
        } catch (err) {
            toast.error(err.response?.data?.message || 'Sipariş oluşturulamadı.')
        } finally {
            setSubmitting(false)
        }
    }

    const handleCancelOrder = async () => {
        if (!orderCreated?.id) return
        try {
            await cancelOrder(orderCreated.id)
            toast.success('Sipariş iptal edildi.')
            setOrderCreated(null)
            navigate('/cart')
        } catch (err) {
            toast.error('Sipariş iptal edilemedi.')
        }
    }

    if (loading) return <LoadingSpinner text="Sipariş bilgileri yükleniyor..." />

    // Order success
    if (orderCreated) {
        return (
            <div className="animate-in" style={{ textAlign: 'center', padding: '64px 0' }}>
                <div style={{ fontSize: '4rem', marginBottom: '16px' }}>✅</div>
                <h1 style={{ fontSize: '2rem', marginBottom: '8px' }}>Siparişiniz Alındı!</h1>
                <p style={{ color: 'var(--color-text-secondary)', marginBottom: '24px' }}>
                    Sipariş No: <strong>{orderCreated.id}</strong><br />
                    Durum: <span className="stock-badge in-stock">{orderCreated.status || 'pending'}</span>
                </p>
                <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
                    <button className="btn btn-primary" onClick={() => navigate('/products')}>
                        Alışverişe Devam Et
                    </button>
                    <button className="btn btn-danger" onClick={handleCancelOrder}>
                        Siparişi İptal Et
                    </button>
                </div>
            </div>
        )
    }

    const items = cart?.items || []

    return (
        <div className="animate-in">
            <div className="page-header">
                <h1><FiCheck /> Siparişi Tamamla</h1>
                <p>Teslimat adresinizi girin ve siparişinizi oluşturun</p>
            </div>

            <form onSubmit={handleSubmit}>
                <div className="checkout-layout">
                    {/* Address Form */}
                    <div className="card">
                        <h3 style={{ marginBottom: '20px', fontSize: '1.1rem' }}>
                            <FiMapPin /> Teslimat Adresi
                        </h3>

                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                            <div className="form-group">
                                <label className="form-label">Adres Başlığı</label>
                                <select
                                    className="form-input"
                                    value={addressForm.title}
                                    onChange={(e) => setAddressForm({ ...addressForm, title: e.target.value })}
                                >
                                    <option value="Ev">Ev</option>
                                    <option value="İş">İş</option>
                                    <option value="Diğer">Diğer</option>
                                </select>
                            </div>
                            <div className="form-group">
                                <label className="form-label">Ad Soyad *</label>
                                <input
                                    id="checkout-fullname"
                                    type="text"
                                    className="form-input"
                                    placeholder="Ahmet Yılmaz"
                                    value={addressForm.fullName}
                                    onChange={(e) => setAddressForm({ ...addressForm, fullName: e.target.value })}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="form-label">Telefon</label>
                            <input
                                id="checkout-phone"
                                type="text"
                                className="form-input"
                                placeholder="+905551234567"
                                value={addressForm.phone}
                                onChange={(e) => setAddressForm({ ...addressForm, phone: e.target.value })}
                            />
                        </div>

                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                            <div className="form-group">
                                <label className="form-label">İl *</label>
                                <input
                                    id="checkout-city"
                                    type="text"
                                    className="form-input"
                                    placeholder="İstanbul"
                                    value={addressForm.city}
                                    onChange={(e) => setAddressForm({ ...addressForm, city: e.target.value })}
                                />
                            </div>
                            <div className="form-group">
                                <label className="form-label">İlçe</label>
                                <input
                                    id="checkout-district"
                                    type="text"
                                    className="form-input"
                                    placeholder="Kadıköy"
                                    value={addressForm.district}
                                    onChange={(e) => setAddressForm({ ...addressForm, district: e.target.value })}
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label className="form-label">Adres Satırı *</label>
                            <textarea
                                id="checkout-address"
                                className="form-input"
                                placeholder="Mahalle, sokak, bina no, daire no..."
                                value={addressForm.addressLine}
                                onChange={(e) => setAddressForm({ ...addressForm, addressLine: e.target.value })}
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">Posta Kodu</label>
                            <input
                                id="checkout-postal"
                                type="text"
                                className="form-input"
                                placeholder="34710"
                                value={addressForm.postalCode}
                                onChange={(e) => setAddressForm({ ...addressForm, postalCode: e.target.value })}
                            />
                        </div>
                    </div>

                    {/* Order Summary */}
                    <div className="card cart-summary">
                        <h3 style={{ marginBottom: '16px', fontSize: '1.1rem' }}>Sipariş Özeti</h3>

                        {items.map((item) => (
                            <div key={item.id} className="cart-summary-row">
                                <span>{item.productName} ×{item.quantity}</span>
                                <span>₺{item.totalPrice?.toFixed(2)}</span>
                            </div>
                        ))}

                        <div className="cart-summary-row">
                            <span>Kargo</span>
                            <span style={{ color: 'var(--color-success)' }}>Ücretsiz</span>
                        </div>

                        <div className="cart-summary-total">
                            <span>Toplam</span>
                            <span style={{ color: 'var(--color-accent)' }}>₺{cart?.totalAmount?.toFixed(2) || '0.00'}</span>
                        </div>

                        <button
                            id="checkout-submit"
                            type="submit"
                            className="btn btn-accent btn-block btn-lg"
                            disabled={submitting}
                            style={{ marginTop: '16px' }}
                        >
                            <FiCheck /> {submitting ? 'Sipariş oluşturuluyor...' : 'Siparişi Onayla'}
                        </button>
                    </div>
                </div>
            </form>
        </div>
    )
}
