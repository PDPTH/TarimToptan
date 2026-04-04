import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getCart, removeFromCart } from '../modules/aykhan/cartService'
import LoadingSpinner from '../components/LoadingSpinner.jsx'
import toast from 'react-hot-toast'
import { FiTrash2, FiShoppingCart, FiArrowRight } from 'react-icons/fi'

export default function CartPage() {
    const navigate = useNavigate()
    const [cart, setCart] = useState(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        fetchCart()
    }, [])

    async function fetchCart() {
        setLoading(true)
        try {
            const data = await getCart()
            setCart(data)
        } catch (err) {
            console.error('Sepet yüklenemedi:', err)
        } finally {
            setLoading(false)
        }
    }

    const handleRemove = async (itemId) => {
        try {
            await removeFromCart(itemId)
            toast.success('Ürün sepetten çıkarıldı.')
            fetchCart()
        } catch (err) {
            toast.error('Ürün çıkarılamadı.')
        }
    }

    if (loading) return <LoadingSpinner text="Sepetiniz yükleniyor..." />

    const items = cart?.items || []

    return (
        <div className="animate-in">
            <div className="page-header">
                <h1><FiShoppingCart /> Sepetim</h1>
                <p>{items.length} ürün sepetinizde</p>
            </div>

            {items.length === 0 ? (
                <div className="empty-state">
                    <div className="empty-icon">🛒</div>
                    <h3>Sepetiniz boş</h3>
                    <p>Hemen ürünleri keşfetmeye başlayın!</p>
                    <Link to="/products" className="btn btn-primary" style={{ marginTop: '16px' }}>
                        Ürünleri Keşfet
                    </Link>
                </div>
            ) : (
                <div className="cart-layout">
                    {/* Cart Items */}
                    <div className="card">
                        {items.map((item) => (
                            <div key={item.id} className="cart-item">
                                <div className="cart-item-image">🌿</div>
                                <div className="cart-item-info">
                                    <h4>{item.productName || 'Ürün'}</h4>
                                    <p>Miktar: {item.quantity} × ₺{item.unitPrice?.toFixed(2)}</p>
                                </div>
                                <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                                    <span className="cart-item-price">₺{item.totalPrice?.toFixed(2)}</span>
                                    <button
                                        className="btn btn-danger btn-sm"
                                        onClick={() => handleRemove(item.id)}
                                        title="Sepetten çıkar"
                                    >
                                        <FiTrash2 />
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>

                    {/* Cart Summary */}
                    <div className="card cart-summary">
                        <h3 style={{ marginBottom: '16px', fontSize: '1.1rem' }}>Sipariş Özeti</h3>

                        <div className="cart-summary-row">
                            <span>Ürün Toplamı</span>
                            <span>₺{cart?.totalAmount?.toFixed(2) || '0.00'}</span>
                        </div>
                        <div className="cart-summary-row">
                            <span>Kargo</span>
                            <span style={{ color: 'var(--color-success)' }}>Ücretsiz</span>
                        </div>

                        <div className="cart-summary-total">
                            <span>Toplam</span>
                            <span style={{ color: 'var(--color-accent)' }}>₺{cart?.totalAmount?.toFixed(2) || '0.00'}</span>
                        </div>

                        <button
                            id="go-to-checkout"
                            className="btn btn-accent btn-block btn-lg"
                            onClick={() => navigate('/checkout')}
                            style={{ marginTop: '16px' }}
                        >
                            Siparişi Tamamla <FiArrowRight />
                        </button>
                    </div>
                </div>
            )}
        </div>
    )
}
