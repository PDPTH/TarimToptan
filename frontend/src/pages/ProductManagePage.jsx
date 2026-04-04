import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { createProduct, updateProduct, getProductById, deleteProduct, updateStock } from '../modules/umut/productService'
import toast from 'react-hot-toast'
import LoadingSpinner from '../components/LoadingSpinner.jsx'
import { FiSave, FiTrash2, FiPackage } from 'react-icons/fi'

export default function ProductManagePage() {
    const { productId } = useParams()
    const navigate = useNavigate()
    const isEdit = !!productId

    const [form, setForm] = useState({
        name: '',
        description: '',
        price: '',
        stock: '',
        category: 'Bakliyat',
        producerId: '',
    })
    const [loading, setLoading] = useState(false)
    const [fetching, setFetching] = useState(isEdit)

    useEffect(() => {
        if (isEdit) {
            async function fetchProduct() {
                try {
                    const data = await getProductById(productId)
                    setForm({
                        name: data.name || '',
                        description: data.description || '',
                        price: data.price?.toString() || '',
                        stock: data.stock?.toString() || '',
                        category: data.category || 'Bakliyat',
                        producerId: data.producerId || '',
                    })
                } catch (err) {
                    toast.error('Ürün yüklenemedi.')
                } finally {
                    setFetching(false)
                }
            }
            fetchProduct()
        }
    }, [productId])

    const handleSubmit = async (e) => {
        e.preventDefault()
        if (!form.name || !form.price) {
            toast.error('Ürün adı ve fiyat zorunludur.')
            return
        }

        setLoading(true)
        try {
            const payload = {
                name: form.name,
                description: form.description,
                price: parseFloat(form.price),
                stock: parseInt(form.stock) || 0,
                category: form.category,
                producerId: form.producerId,
            }

            if (isEdit) {
                await updateProduct(productId, payload)
                toast.success('Ürün güncellendi!')
            } else {
                await createProduct(payload)
                toast.success('Ürün oluşturuldu!')
            }
            navigate('/products')
        } catch (err) {
            toast.error(err.response?.data?.message || 'İşlem başarısız.')
        } finally {
            setLoading(false)
        }
    }

    const handleDelete = async () => {
        if (!window.confirm('Bu ürünü silmek istediğinize emin misiniz?')) return
        try {
            await deleteProduct(productId)
            toast.success('Ürün silindi!')
            navigate('/products')
        } catch (err) {
            toast.error('Ürün silinemedi.')
        }
    }

    const handleStockUpdate = async () => {
        if (!form.stock) return
        try {
            await updateStock(productId, parseInt(form.stock))
            toast.success('Stok güncellendi!')
        } catch (err) {
            toast.error('Stok güncellenemedi.')
        }
    }

    if (fetching) return <LoadingSpinner text="Ürün bilgileri yükleniyor..." />

    return (
        <div className="animate-in">
            <div className="page-header">
                <h1><FiPackage /> {isEdit ? 'Ürün Güncelle' : 'Yeni Ürün Ekle'}</h1>
                <p>{isEdit ? 'Mevcut ürünün bilgilerini düzenleyin' : 'Sisteme yeni bir ürün ekleyin'}</p>
            </div>

            <div className="manage-form card">
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">Ürün Adı *</label>
                        <input
                            id="product-name"
                            type="text"
                            className="form-input"
                            placeholder="Organik Domates"
                            value={form.name}
                            onChange={(e) => setForm({ ...form, name: e.target.value })}
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Açıklama</label>
                        <textarea
                            id="product-description"
                            className="form-input"
                            placeholder="Ürün açıklaması..."
                            value={form.description}
                            onChange={(e) => setForm({ ...form, description: e.target.value })}
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">Fiyat (₺) *</label>
                            <input
                                id="product-price"
                                type="number"
                                step="0.01"
                                className="form-input"
                                placeholder="79.90"
                                value={form.price}
                                onChange={(e) => setForm({ ...form, price: e.target.value })}
                            />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Stok Miktarı</label>
                            <input
                                id="product-stock"
                                type="number"
                                className="form-input"
                                placeholder="150"
                                value={form.stock}
                                onChange={(e) => setForm({ ...form, stock: e.target.value })}
                            />
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">Kategori</label>
                            <select
                                id="product-category"
                                className="form-input"
                                value={form.category}
                                onChange={(e) => setForm({ ...form, category: e.target.value })}
                            >
                                <option value="Bakliyat">Bakliyat</option>
                                <option value="Tahıl">Tahıl</option>
                                <option value="Sebze">Sebze</option>
                                <option value="Meyve">Meyve</option>
                                <option value="Süt Ürünleri">Süt Ürünleri</option>
                                <option value="Diğer">Diğer</option>
                            </select>
                        </div>
                        <div className="form-group">
                            <label className="form-label">Üretici ID</label>
                            <input
                                id="product-producerid"
                                type="text"
                                className="form-input"
                                placeholder="Üretici UUID"
                                value={form.producerId}
                                onChange={(e) => setForm({ ...form, producerId: e.target.value })}
                            />
                        </div>
                    </div>

                    <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
                        <button id="product-submit" type="submit" className="btn btn-primary" disabled={loading}>
                            <FiSave /> {loading ? 'Kaydediliyor...' : (isEdit ? 'Güncelle' : 'Ürün Ekle')}
                        </button>

                        {isEdit && (
                            <>
                                <button type="button" className="btn btn-secondary" onClick={handleStockUpdate}>
                                    📦 Stok Güncelle
                                </button>
                                <button type="button" className="btn btn-danger" onClick={handleDelete}>
                                    <FiTrash2 /> Ürünü Sil
                                </button>
                            </>
                        )}
                    </div>
                </form>
            </div>
        </div>
    )
}
