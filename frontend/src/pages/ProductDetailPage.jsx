import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { getProductById, createReview, getReviews, deleteProduct } from '../modules/umut/productService.js'
import { addToCart } from '../modules/aykhan/cartService.js'
import { sampleProducts } from '../utils/sampleData.js'
import { useAuth } from '../context/AuthContext.jsx'
import LoadingSpinner from '../components/LoadingSpinner.jsx'
import toast from 'react-hot-toast'
import { FiShoppingCart, FiStar, FiSend, FiEdit, FiTrash2 } from 'react-icons/fi'

const categoryEmoji = {
    'Sebze': '🥬', 'Meyve': '🍎', 'Bakliyat': '🫘', 'Tahıl': '🌾', 'default': '🌿',
}

export default function ProductDetailPage() {
    const { productId } = useParams()
    const { isAuthenticated } = useAuth()
    const navigate = useNavigate()
    const [product, setProduct] = useState(null)
    const [loading, setLoading] = useState(true)
    const [quantity, setQuantity] = useState(1)
    const [addingToCart, setAddingToCart] = useState(false)

    // Review form
    const [reviewForm, setReviewForm] = useState({ rating: 5, comment: '' })
    const [submittingReview, setSubmittingReview] = useState(false)
    const [reviews, setReviews] = useState([])
    const [reviewsLoading, setReviewsLoading] = useState(false)

    useEffect(() => {
        async function fetchProduct() {
            setLoading(true)
            try {
                const data = await getProductById(productId)
                setProduct(data)
            } catch (err) {
                console.error('Ürün yüklenemedi, örnek veri aranıyor:', err)
                const sample = sampleProducts.find(p => p.id === productId)
                if (sample) setProduct(sample)
            } finally {
                setLoading(false)
            }
        }
        fetchProduct()
    }, [productId])

    useEffect(() => {
        fetchReviews()
    }, [productId])

    async function fetchReviews() {
        setReviewsLoading(true)
        try {
            const data = await getReviews(productId)
            setReviews(data.data || data || [])
        } catch (err) {
            console.log('Değerlendirmeler yüklenemedi:', err)
        } finally {
            setReviewsLoading(false)
        }
    }

    const handleAddToCart = async () => {
        if (!isAuthenticated) {
            toast.error('Sepete eklemek için giriş yapmalısınız.')
            return
        }
        setAddingToCart(true)
        try {
            await addToCart(productId, quantity)
            toast.success('Ürün sepete eklendi!')
        } catch (err) {
            toast.error(err.response?.data?.message || 'Sepete eklenemedi.')
        } finally {
            setAddingToCart(false)
        }
    }

    const handleReviewSubmit = async (e) => {
        e.preventDefault()
        if (!reviewForm.comment.trim()) {
            toast.error('Yorum yazmalısınız.')
            return
        }
        setSubmittingReview(true)
        try {
            await createReview(productId, reviewForm)
            toast.success('Değerlendirmeniz eklendi!')
            setReviewForm({ rating: 5, comment: '' })
            fetchReviews() // Listeyi yenile
        } catch (err) {
            toast.error(err.response?.data?.message || 'Değerlendirme eklenemedi.')
        } finally {
            setSubmittingReview(false)
        }
    }

    if (loading) return <LoadingSpinner text="Ürün detayı yükleniyor..." />

    if (!product) {
        return (
            <div className="empty-state">
                <div className="empty-icon">😕</div>
                <h3>Ürün bulunamadı</h3>
                <Link to="/products" className="btn btn-primary" style={{ marginTop: '16px' }}>
                    Ürünlere Dön
                </Link>
            </div>
        )
    }

    const emoji = categoryEmoji[product.category] || categoryEmoji.default

    return (
        <div className="animate-in">
            <div className="detail-layout">
                {/* Image */}
                <div className="detail-image">
                    <span>{emoji}</span>
                </div>

                {/* Info */}
                <div className="detail-info">
                    {product.category && (
                        <span className="product-card-category">{product.category}</span>
                    )}
                    <h1>{product.name}</h1>
                    <div className="detail-price">₺{product.price?.toFixed(2)}</div>

                    <p className="detail-description">
                        {product.description || 'Bu ürün hakkında henüz açıklama eklenmemiş.'}
                    </p>

                    <span className={`stock-badge ${product.stock > 0 ? 'in-stock' : 'out-of-stock'}`}>
                        {product.stock > 0 ? `${product.stock} adet stokta` : 'Tükendi'}
                    </span>

                    {/* Ürün Yönetim Butonları */}
                    {isAuthenticated && (
                        <div style={{ display: 'flex', gap: '10px', marginTop: '16px' }}>
                            <Link to={`/products/manage/${productId}`} className="btn btn-secondary btn-sm">
                                <FiEdit /> Ürünü Düzenle
                            </Link>
                            <button
                                className="btn btn-danger btn-sm"
                                onClick={async () => {
                                    if (!window.confirm('Bu ürünü silmek istediğinize emin misiniz?')) return
                                    try {
                                        await deleteProduct(productId)
                                        toast.success('Ürün silindi!')
                                        navigate('/products')
                                    } catch (err) {
                                        toast.error(err.response?.data?.message || 'Ürün silinemedi.')
                                    }
                                }}
                            >
                                <FiTrash2 /> Ürünü Sil
                            </button>
                        </div>
                    )}

                    {product.stock > 0 && (
                        <div className="detail-actions" style={{ marginTop: '24px' }}>
                            <div className="quantity-control">
                                <button onClick={() => setQuantity(Math.max(1, quantity - 1))}>−</button>
                                <span>{quantity}</span>
                                <button onClick={() => setQuantity(Math.min(product.stock, quantity + 1))}>+</button>
                            </div>
                            <button
                                id="add-to-cart"
                                className="btn btn-accent btn-lg"
                                onClick={handleAddToCart}
                                disabled={addingToCart}
                            >
                                <FiShoppingCart /> {addingToCart ? 'Ekleniyor...' : 'Sepete Ekle'}
                            </button>
                        </div>
                    )}
                </div>
            </div>

            {/* Reviews Section */}
            {isAuthenticated && (
                <section className="section">
                    <h2 className="section-title" style={{ marginBottom: '16px' }}>
                        <FiStar /> Değerlendirme Yap
                    </h2>
                    <div className="card">
                        <form onSubmit={handleReviewSubmit}>
                            <div className="form-group">
                                <label className="form-label">Puan</label>
                                <div style={{ display: 'flex', gap: '8px' }}>
                                    {[1, 2, 3, 4, 5].map((star) => (
                                        <button
                                            key={star}
                                            type="button"
                                            onClick={() => setReviewForm({ ...reviewForm, rating: star })}
                                            style={{
                                                background: 'none',
                                                border: 'none',
                                                fontSize: '1.5rem',
                                                cursor: 'pointer',
                                                color: star <= reviewForm.rating ? 'var(--color-accent)' : 'var(--color-text-muted)',
                                            }}
                                        >
                                            ★
                                        </button>
                                    ))}
                                </div>
                            </div>
                            <div className="form-group">
                                <label className="form-label">Yorumunuz</label>
                                <textarea
                                    id="review-comment"
                                    className="form-input"
                                    placeholder="Bu ürün hakkında düşüncelerinizi yazın..."
                                    value={reviewForm.comment}
                                    onChange={(e) => setReviewForm({ ...reviewForm, comment: e.target.value })}
                                />
                            </div>
                            <button id="review-submit" type="submit" className="btn btn-primary" disabled={submittingReview}>
                                <FiSend /> {submittingReview ? 'Gönderiliyor...' : 'Değerlendirmeyi Gönder'}
                            </button>
                        </form>
                    </div>
                </section>
            )}

            {/* Existing Reviews */}
            <section className="section">
                <h2 className="section-title" style={{ marginBottom: '16px' }}>
                    <FiStar /> Değerlendirmeler {reviews.length > 0 && `(${reviews.length})`}
                </h2>
                {reviewsLoading ? (
                    <LoadingSpinner text="Değerlendirmeler yükleniyor..." />
                ) : reviews.length === 0 ? (
                    <div className="card" style={{ textAlign: 'center', padding: '32px' }}>
                        <p style={{ color: 'var(--color-text-secondary)' }}>Bu ürün için henüz değerlendirme yapılmamış.</p>
                    </div>
                ) : (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                        {reviews.map((review) => (
                            <div key={review.id} className="card" style={{ padding: '16px' }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                                    <strong style={{ fontSize: '0.95rem' }}>{review.userName || 'Anonim'}</strong>
                                    <span style={{ fontSize: '0.8rem', color: 'var(--color-text-secondary)' }}>
                                        {review.createdAt ? new Date(review.createdAt).toLocaleDateString('tr-TR', { year: 'numeric', month: 'long', day: 'numeric' }) : ''}
                                    </span>
                                </div>
                                <div style={{ marginBottom: '8px' }}>
                                    {[1, 2, 3, 4, 5].map((star) => (
                                        <span key={star} style={{
                                            color: star <= (review.rating || 0) ? 'var(--color-accent)' : 'var(--color-text-muted)',
                                            fontSize: '1.1rem',
                                        }}>★</span>
                                    ))}
                                </div>
                                <p style={{ color: 'var(--color-text-secondary)', margin: 0, lineHeight: '1.5' }}>{review.comment}</p>
                            </div>
                        ))}
                    </div>
                )}
            </section>
        </div>
    )
}
