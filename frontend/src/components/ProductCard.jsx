import { useNavigate } from 'react-router-dom'
import { FiShoppingCart } from 'react-icons/fi'

const categoryEmoji = {
    'Sebze': '🥬',
    'Meyve': '🍎',
    'Bakliyat': '🫘',
    'Tahıl': '🌾',
    'default': '🌿',
}

export default function ProductCard({ product }) {
    const navigate = useNavigate()
    const emoji = categoryEmoji[product.category] || categoryEmoji.default

    return (
        <div className="product-card" onClick={() => navigate(`/products/${product.id}`)}>
            <div className="product-card-image">
                <span>{emoji}</span>
            </div>
            <div className="product-card-body">
                {product.category && (
                    <span className="product-card-category">{product.category}</span>
                )}
                <h3 className="product-card-title">{product.name}</h3>
                <div className="product-card-price">
                    ₺{product.price?.toFixed(2)} <small>/ kg</small>
                </div>
            </div>
            <div className="product-card-footer">
                <span className={`stock-badge ${product.stock > 0 ? 'in-stock' : 'out-of-stock'}`}>
                    {product.stock > 0 ? `${product.stock} adet stokta` : 'Tükendi'}
                </span>
            </div>
        </div>
    )
}
