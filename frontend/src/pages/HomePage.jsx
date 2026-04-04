import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { FiTruck, FiShield, FiStar, FiArrowRight } from 'react-icons/fi'
import { getProducts } from '../modules/umut/productService.js'
import { getProducers } from '../modules/suleyman/authService.js'
import { sampleProducts, sampleProducers } from '../utils/sampleData.js'
import ProductCard from '../components/ProductCard.jsx'

export default function HomePage() {
    const [featuredProducts, setFeaturedProducts] = useState([])
    const [producers, setProducers] = useState([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        async function fetchData() {
            try {
                const [prodRes, producerRes] = await Promise.allSettled([
                    getProducts({ limit: 8 }),
                    getProducers({ limit: 4 }),
                ])

                if (prodRes.status === 'fulfilled') {
                    const data = prodRes.value
                    const items = data.data || data || []
                    setFeaturedProducts(items.length > 0 ? items : sampleProducts.slice(0, 8))
                } else {
                    setFeaturedProducts(sampleProducts.slice(0, 8))
                }

                if (producerRes.status === 'fulfilled') {
                    const data = producerRes.value
                    const items = data.data || data || []
                    setProducers(items.length > 0 ? items : sampleProducers.slice(0, 4))
                } else {
                    setProducers(sampleProducers.slice(0, 4))
                }
            } catch (err) {
                console.error('Ana sayfa verileri yüklenemedi:', err)
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [])

    return (
        <div className="animate-in">
            {/* Hero */}
            <section className="hero">
                <h1>Tarladan Sofranıza<br />Taze Tarım Ürünleri</h1>
                <p>
                    Nohut, pirinç, mercimek, bulgur ve daha fazlası — doğrudan üreticilerden,
                    toptan fiyatlarla kapınıza kadar. Güvenilir, hızlı ve kaliteli alışveriş deneyimi.
                </p>
                <div className="hero-actions">
                    <Link to="/products" className="btn btn-primary btn-lg">
                        <FiArrowRight /> Ürünleri Keşfet
                    </Link>
                    <Link to="/register" className="btn btn-secondary btn-lg">
                        Hemen Üye Ol
                    </Link>
                </div>
            </section>

            {/* Stats */}
            <section className="stats-row">
                <div className="card stat-card">
                    <div className="stat-icon">🌾</div>
                    <div className="stat-value">500+</div>
                    <div className="stat-label">Ürün Çeşidi</div>
                </div>
                <div className="card stat-card">
                    <div className="stat-icon">👨‍🌾</div>
                    <div className="stat-value">120+</div>
                    <div className="stat-label">Üretici</div>
                </div>
                <div className="card stat-card">
                    <div className="stat-icon">📦</div>
                    <div className="stat-value">10K+</div>
                    <div className="stat-label">Teslim Edilen Sipariş</div>
                </div>
                <div className="card stat-card">
                    <div className="stat-icon">⭐</div>
                    <div className="stat-value">4.8</div>
                    <div className="stat-label">Müşteri Puanı</div>
                </div>
            </section>

            {/* Featured Products */}
            <section className="section">
                <div className="section-header">
                    <h2 className="section-title">🔥 Öne Çıkan Ürünler</h2>
                    <Link to="/products" className="btn btn-secondary btn-sm">
                        Tümünü Gör <FiArrowRight />
                    </Link>
                </div>

                {loading ? (
                    <div className="loading-container">
                        <div className="spinner" />
                        <p className="loading-text">Ürünler yükleniyor...</p>
                    </div>
                ) : featuredProducts.length > 0 ? (
                    <div className="product-grid">
                        {featuredProducts.map((product) => (
                            <ProductCard key={product.id} product={product} />
                        ))}
                    </div>
                ) : (
                    <div className="empty-state">
                        <div className="empty-icon">🌱</div>
                        <h3>Henüz ürün eklenmemiş</h3>
                        <p>API bağlantısı aktif olduğunda ürünler burada görünecektir.</p>
                    </div>
                )}
            </section>

            {/* Features */}
            <section className="section">
                <div className="section-header">
                    <h2 className="section-title">Neden TarımToptan?</h2>
                </div>
                <div className="stats-row">
                    <div className="card" style={{ textAlign: 'center', padding: '32px' }}>
                        <FiTruck style={{ fontSize: '2rem', color: 'var(--color-primary-light)', marginBottom: '12px' }} />
                        <h3 style={{ fontSize: '1rem', marginBottom: '8px' }}>Hızlı Teslimat</h3>
                        <p style={{ color: 'var(--color-text-muted)', fontSize: '0.85rem' }}>
                            Siparişleriniz en kısa sürede kapınıza ulaşır.
                        </p>
                    </div>
                    <div className="card" style={{ textAlign: 'center', padding: '32px' }}>
                        <FiShield style={{ fontSize: '2rem', color: 'var(--color-primary-light)', marginBottom: '12px' }} />
                        <h3 style={{ fontSize: '1rem', marginBottom: '8px' }}>Güvenli Alışveriş</h3>
                        <p style={{ color: 'var(--color-text-muted)', fontSize: '0.85rem' }}>
                            Ödeme ve teslimat süreçleri güvenle korunur.
                        </p>
                    </div>
                    <div className="card" style={{ textAlign: 'center', padding: '32px' }}>
                        <FiStar style={{ fontSize: '2rem', color: 'var(--color-primary-light)', marginBottom: '12px' }} />
                        <h3 style={{ fontSize: '1rem', marginBottom: '8px' }}>Kaliteli Ürünler</h3>
                        <p style={{ color: 'var(--color-text-muted)', fontSize: '0.85rem' }}>
                            Doğrudan üreticiden, taze ve doğal tarım ürünleri.
                        </p>
                    </div>
                </div>
            </section>

            {/* Producers */}
            {producers.length > 0 && (
                <section className="section">
                    <div className="section-header">
                        <h2 className="section-title">👨‍🌾 Üreticilerimiz</h2>
                        <Link to="/producers" className="btn btn-secondary btn-sm">
                            Tümünü Gör <FiArrowRight />
                        </Link>
                    </div>
                    <div className="product-grid">
                        {producers.map((producer) => (
                            <Link key={producer.id} to={`/producers/${producer.id}`} style={{ textDecoration: 'none' }}>
                                <div className="card producer-card">
                                    <div className="producer-avatar">
                                        {producer.name?.charAt(0).toUpperCase()}
                                    </div>
                                    <div className="producer-info">
                                        <h4>{producer.name}</h4>
                                        <p>{producer.address || 'Türkiye'}</p>
                                    </div>
                                </div>
                            </Link>
                        ))}
                    </div>
                </section>
            )}
        </div>
    )
}
