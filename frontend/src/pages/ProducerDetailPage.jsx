import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getProducerById } from '../modules/suleyman/authService'
import { getProducts } from '../modules/umut/productService'
import { sampleProducers, sampleProducts } from '../utils/sampleData'
import ProductCard from '../components/ProductCard.jsx'
import LoadingSpinner from '../components/LoadingSpinner.jsx'
import { FiMail, FiPhone, FiMapPin } from 'react-icons/fi'

export default function ProducerDetailPage() {
    const { producerId } = useParams()
    const [producer, setProducer] = useState(null)
    const [products, setProducts] = useState([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        async function fetchData() {
            setLoading(true)
            try {
                const [prodRes, prodListRes] = await Promise.allSettled([
                    getProducerById(producerId),
                    getProducts({ producerId, limit: 20 }),
                ])
                if (prodRes.status === 'fulfilled') {
                    setProducer(prodRes.value)
                } else {
                    const sample = sampleProducers.find(p => p.id === producerId)
                    if (sample) setProducer(sample)
                }
                if (prodListRes.status === 'fulfilled') {
                    const data = prodListRes.value
                    const items = data.data || data || []
                    setProducts(items.length > 0 ? items : sampleProducts.filter(p => p.producerId === producerId))
                } else {
                    setProducts(sampleProducts.filter(p => p.producerId === producerId))
                }
            } catch (err) {
                console.error('Üretici bilgileri yüklenemedi:', err)
                const sample = sampleProducers.find(p => p.id === producerId)
                if (sample) setProducer(sample)
                setProducts(sampleProducts.filter(p => p.producerId === producerId))
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [producerId])

    if (loading) return <LoadingSpinner text="Üretici bilgileri yükleniyor..." />

    if (!producer) {
        return (
            <div className="empty-state">
                <div className="empty-icon">😕</div>
                <h3>Üretici bulunamadı</h3>
                <Link to="/producers" className="btn btn-primary" style={{ marginTop: '16px' }}>
                    Üreticilere Dön
                </Link>
            </div>
        )
    }

    return (
        <div className="animate-in">
            {/* Producer Header */}
            <div className="card" style={{ display: 'flex', gap: '24px', alignItems: 'center', marginBottom: '32px', padding: '32px' }}>
                <div className="producer-avatar" style={{ width: '80px', height: '80px', fontSize: '2rem' }}>
                    {producer.name?.charAt(0).toUpperCase()}
                </div>
                <div style={{ flex: 1 }}>
                    <h1 style={{ fontSize: '1.5rem', marginBottom: '8px' }}>{producer.name}</h1>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '16px', color: 'var(--color-text-secondary)', fontSize: '0.9rem' }}>
                        {producer.address && <span><FiMapPin /> {producer.address}</span>}
                        {producer.email && <span><FiMail /> {producer.email}</span>}
                        {producer.phone && <span><FiPhone /> {producer.phone}</span>}
                    </div>
                </div>
            </div>

            {/* Producer's Products */}
            <section className="section">
                <h2 className="section-title" style={{ marginBottom: '24px' }}>
                    📦 Üreticinin Ürünleri
                </h2>
                {products.length > 0 ? (
                    <div className="product-grid">
                        {products.map((product) => (
                            <ProductCard key={product.id} product={product} />
                        ))}
                    </div>
                ) : (
                    <div className="empty-state">
                        <div className="empty-icon">📦</div>
                        <h3>Henüz ürün eklenmemiş</h3>
                    </div>
                )}
            </section>
        </div>
    )
}
