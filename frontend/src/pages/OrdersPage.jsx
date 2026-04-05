import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getOrders, cancelOrder } from '../modules/aykhan/cartService.js'
import LoadingSpinner from '../components/LoadingSpinner.jsx'
import toast from 'react-hot-toast'
import { FiPackage, FiClock, FiCheckCircle, FiXCircle, FiTruck, FiShoppingBag } from 'react-icons/fi'

const statusMap = {
    pending: { label: 'Beklemede', icon: <FiClock />, className: 'status-pending' },
    confirmed: { label: 'Onaylandı', icon: <FiCheckCircle />, className: 'status-confirmed' },
    shipped: { label: 'Kargoya Verildi', icon: <FiTruck />, className: 'status-shipped' },
    delivered: { label: 'Teslim Edildi', icon: <FiCheckCircle />, className: 'status-delivered' },
    cancelled: { label: 'İptal Edildi', icon: <FiXCircle />, className: 'status-cancelled' },
}

export default function OrdersPage() {
    const navigate = useNavigate()
    const [orders, setOrders] = useState([])
    const [loading, setLoading] = useState(true)
    const [cancellingId, setCancellingId] = useState(null)

    useEffect(() => {
        fetchOrders()
    }, [])

    async function fetchOrders() {
        try {
            const data = await getOrders()
            setOrders(data.data || data || [])
        } catch (err) {
            console.error('Siparişler yüklenemedi:', err)
            toast.error('Siparişler yüklenirken hata oluştu.')
        } finally {
            setLoading(false)
        }
    }

    async function handleCancel(orderId) {
        if (!window.confirm('Bu siparişi iptal etmek istediğinize emin misiniz?')) return
        setCancellingId(orderId)
        try {
            await cancelOrder(orderId)
            toast.success('Sipariş iptal edildi.')
            setOrders(orders.map(o => o.id === orderId ? { ...o, status: 'cancelled' } : o))
        } catch (err) {
            toast.error(err.response?.data?.message || 'Sipariş iptal edilemedi.')
        } finally {
            setCancellingId(null)
        }
    }

    function formatDate(dateStr) {
        if (!dateStr) return '-'
        const date = new Date(dateStr)
        return date.toLocaleDateString('tr-TR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
        })
    }

    if (loading) return <LoadingSpinner text="Siparişleriniz yükleniyor..." />

    return (
        <div className="animate-in">
            <div className="page-header">
                <h1><FiPackage /> Sipariş Geçmişi</h1>
                <p>Geçmiş ve aktif siparişlerinizi görüntüleyin</p>
            </div>

            {orders.length === 0 ? (
                <div className="empty-state">
                    <div className="empty-icon">📦</div>
                    <h3>Henüz siparişiniz yok</h3>
                    <p>İlk siparişinizi oluşturmak için ürünleri keşfedin.</p>
                    <button className="btn btn-primary" onClick={() => navigate('/products')}>
                        <FiShoppingBag /> Ürünleri Keşfet
                    </button>
                </div>
            ) : (
                <div className="orders-list">
                    {orders.map((order) => {
                        const status = statusMap[order.status] || statusMap.pending
                        const canCancel = order.status === 'pending' || order.status === 'confirmed'

                        return (
                            <div key={order.id} className="card order-card" style={{ marginBottom: '16px' }}>
                                {/* Order Header */}
                                <div style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                    marginBottom: '16px',
                                    paddingBottom: '12px',
                                    borderBottom: '1px solid var(--color-border)',
                                    flexWrap: 'wrap',
                                    gap: '8px',
                                }}>
                                    <div>
                                        <span style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)' }}>
                                            Sipariş No:
                                        </span>{' '}
                                        <strong style={{ fontSize: '0.95rem' }}>{order.id?.slice(0, 8)}...</strong>
                                    </div>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '12px', flexWrap: 'wrap' }}>
                                        <span style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)' }}>
                                            {formatDate(order.createdAt)}
                                        </span>
                                        <span className={`stock-badge ${order.status === 'cancelled' ? 'out-of-stock' : 'in-stock'}`}
                                            style={{ display: 'inline-flex', alignItems: 'center', gap: '4px' }}>
                                            {status.icon} {status.label}
                                        </span>
                                    </div>
                                </div>

                                {/* Order Items */}
                                <div style={{ marginBottom: '16px' }}>
                                    {(order.items || []).map((item, idx) => (
                                        <div key={idx} style={{
                                            display: 'flex',
                                            justifyContent: 'space-between',
                                            alignItems: 'center',
                                            padding: '8px 0',
                                            borderBottom: idx < order.items.length - 1 ? '1px solid var(--color-border)' : 'none',
                                        }}>
                                            <span style={{ color: 'var(--color-text-primary)' }}>
                                                {item.productName || item.name || 'Ürün'} <span style={{ color: 'var(--color-text-secondary)' }}>×{item.quantity}</span>
                                            </span>
                                            <span style={{ fontWeight: '600' }}>
                                                ₺{(item.totalPrice || (item.unitPrice * item.quantity) || 0).toFixed(2)}
                                            </span>
                                        </div>
                                    ))}
                                </div>

                                {/* Order Footer */}
                                <div style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                    paddingTop: '12px',
                                    borderTop: '1px solid var(--color-border)',
                                    flexWrap: 'wrap',
                                    gap: '8px',
                                }}>
                                    <div style={{ fontSize: '1.1rem', fontWeight: '700' }}>
                                        Toplam: <span style={{ color: 'var(--color-accent)' }}>₺{(order.totalAmount || 0).toFixed(2)}</span>
                                    </div>
                                    {canCancel && (
                                        <button
                                            className="btn btn-danger btn-sm"
                                            onClick={() => handleCancel(order.id)}
                                            disabled={cancellingId === order.id}
                                        >
                                            <FiXCircle /> {cancellingId === order.id ? 'İptal ediliyor...' : 'Siparişi İptal Et'}
                                        </button>
                                    )}
                                </div>
                            </div>
                        )
                    })}
                </div>
            )}
        </div>
    )
}
