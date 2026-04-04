import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { getProducers } from '../modules/suleyman/authService.js'
import { sampleProducers } from '../utils/sampleData.js'
import LoadingSpinner from '../components/LoadingSpinner.jsx'
import { FiArrowRight } from 'react-icons/fi'

export default function ProducersPage() {
    const [producers, setProducers] = useState([])
    const [loading, setLoading] = useState(true)
    const [page, setPage] = useState(1)
    const [totalPages, setTotalPages] = useState(1)

    useEffect(() => {
        async function fetchProducers() {
            setLoading(true)
            try {
                const data = await getProducers({ page, limit: 12 })
                const items = data.data || data || []
                setProducers(items.length > 0 ? items : sampleProducers)
                if (data.pagination) setTotalPages(data.pagination.totalPages || 1)
            } catch (err) {
                console.error('Üreticiler yüklenemedi, örnek veriler gösteriliyor:', err)
                setProducers(sampleProducers)
            } finally {
                setLoading(false)
            }
        }
        fetchProducers()
    }, [page])

    return (
        <div className="animate-in">
            <div className="page-header">
                <h1>👨‍🌾 Üreticilerimiz</h1>
                <p>Güvenilir tarım üreticilerimizi keşfedin</p>
            </div>

            {loading ? (
                <LoadingSpinner text="Üreticiler yükleniyor..." />
            ) : producers.length > 0 ? (
                <>
                    <div className="product-grid">
                        {producers.map((producer) => (
                            <Link key={producer.id} to={`/producers/${producer.id}`} style={{ textDecoration: 'none' }}>
                                <div className="card producer-card" style={{ padding: '24px' }}>
                                    <div className="producer-avatar" style={{ width: '64px', height: '64px', fontSize: '1.5rem' }}>
                                        {producer.name?.charAt(0).toUpperCase()}
                                    </div>
                                    <div className="producer-info" style={{ flex: 1 }}>
                                        <h4 style={{ fontSize: '1.05rem', marginBottom: '4px' }}>{producer.name}</h4>
                                        <p>{producer.address || 'Türkiye'}</p>
                                        {producer.email && (
                                            <p style={{ fontSize: '0.75rem', marginTop: '4px' }}>{producer.email}</p>
                                        )}
                                    </div>
                                    <FiArrowRight style={{ color: 'var(--color-text-muted)' }} />
                                </div>
                            </Link>
                        ))}
                    </div>

                    {totalPages > 1 && (
                        <div className="pagination">
                            <button disabled={page <= 1} onClick={() => setPage(page - 1)}>‹</button>
                            {Array.from({ length: totalPages }, (_, i) => i + 1).map((p) => (
                                <button key={p} className={p === page ? 'active' : ''} onClick={() => setPage(p)}>
                                    {p}
                                </button>
                            ))}
                            <button disabled={page >= totalPages} onClick={() => setPage(page + 1)}>›</button>
                        </div>
                    )}
                </>
            ) : (
                <div className="empty-state">
                    <div className="empty-icon">👨‍🌾</div>
                    <h3>Henüz üretici eklenmemiş</h3>
                    <p>API bağlantısı aktif olduğunda üreticiler burada listelenecektir.</p>
                </div>
            )}
        </div>
    )
}
