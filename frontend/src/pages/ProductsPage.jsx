import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { getProducts } from '../modules/umut/productService'
import { sampleProducts } from '../utils/sampleData'
import ProductCard from '../components/ProductCard.jsx'
import LoadingSpinner from '../components/LoadingSpinner.jsx'
import { FiSearch, FiFilter } from 'react-icons/fi'

export default function ProductsPage() {
    const [searchParams, setSearchParams] = useSearchParams()
    const [products, setProducts] = useState([])
    const [loading, setLoading] = useState(true)
    const [pagination, setPagination] = useState({ page: 1, totalPages: 1 })
    const [filters, setFilters] = useState({
        search: searchParams.get('search') || '',
        minPrice: searchParams.get('minPrice') || '',
        maxPrice: searchParams.get('maxPrice') || '',
        producerId: searchParams.get('producerId') || '',
    })

    const page = parseInt(searchParams.get('page')) || 1

    useEffect(() => {
        fetchProducts()
    }, [page])

    async function fetchProducts() {
        setLoading(true)
        try {
            const params = { page, limit: 12 }
            if (filters.minPrice) params.minPrice = parseFloat(filters.minPrice)
            if (filters.maxPrice) params.maxPrice = parseFloat(filters.maxPrice)
            if (filters.producerId) params.producerId = filters.producerId

            const data = await getProducts(params)
            const items = data.data || data || []
            setProducts(items.length > 0 ? items : sampleProducts)
            if (data.pagination) setPagination(data.pagination)
        } catch (err) {
            console.error('Ürünler yüklenemedi, örnek veriler gösteriliyor:', err)
            setProducts(sampleProducts)
        } finally {
            setLoading(false)
        }
    }

    const handleFilter = () => {
        const params = new URLSearchParams()
        params.set('page', '1')
        if (filters.minPrice) params.set('minPrice', filters.minPrice)
        if (filters.maxPrice) params.set('maxPrice', filters.maxPrice)
        if (filters.producerId) params.set('producerId', filters.producerId)
        setSearchParams(params)
        fetchProducts()
    }

    const goToPage = (p) => {
        const params = new URLSearchParams(searchParams)
        params.set('page', String(p))
        setSearchParams(params)
    }

    return (
        <div className="animate-in">
            <div className="page-header">
                <h1>🌿 Ürünlerimiz</h1>
                <p>Taze ve doğal tarım ürünlerini keşfedin</p>
            </div>

            {/* Filters */}
            <div className="filters-bar">
                <input
                    type="number"
                    className="form-input"
                    placeholder="Min Fiyat (₺)"
                    value={filters.minPrice}
                    onChange={(e) => setFilters({ ...filters, minPrice: e.target.value })}
                    style={{ maxWidth: '140px' }}
                />
                <input
                    type="number"
                    className="form-input"
                    placeholder="Max Fiyat (₺)"
                    value={filters.maxPrice}
                    onChange={(e) => setFilters({ ...filters, maxPrice: e.target.value })}
                    style={{ maxWidth: '140px' }}
                />
                <button className="btn btn-secondary btn-sm" onClick={handleFilter}>
                    <FiFilter /> Filtrele
                </button>
            </div>

            {loading ? (
                <LoadingSpinner text="Ürünler yükleniyor..." />
            ) : products.length > 0 ? (
                <>
                    <div className="product-grid">
                        {products.map((product) => (
                            <ProductCard key={product.id} product={product} />
                        ))}
                    </div>

                    {/* Pagination */}
                    {pagination.totalPages > 1 && (
                        <div className="pagination">
                            <button disabled={page <= 1} onClick={() => goToPage(page - 1)}>‹</button>
                            {Array.from({ length: pagination.totalPages }, (_, i) => i + 1).map((p) => (
                                <button key={p} className={p === page ? 'active' : ''} onClick={() => goToPage(p)}>
                                    {p}
                                </button>
                            ))}
                            <button disabled={page >= pagination.totalPages} onClick={() => goToPage(page + 1)}>›</button>
                        </div>
                    )}
                </>
            ) : (
                <div className="empty-state">
                    <div className="empty-icon">🔍</div>
                    <h3>Ürün bulunamadı</h3>
                    <p>Filtreleri değiştirin veya API bağlantısını kontrol edin.</p>
                </div>
            )}
        </div>
    )
}
