/**
 * Umut Modülü — Products, Stock, Reviews API Servisleri
 * 
 * Endpoints:
 * - GET /products
 * - GET /products/{productId}
 * - POST /products
 * - PUT /products/{productId}
 * - DELETE /products/{productId}
 * - PATCH /products/{productId}/stock
 * - POST /products/{productId}/reviews
 */

import api from '../../services/api/api'

// ========================
// PRODUCTS
// ========================

export async function getProducts(params = {}) {
    const res = await api.get('/products', { params })
    return res.data
}

export async function getProductById(productId) {
    const res = await api.get(`/products/${productId}`)
    return res.data
}

export async function createProduct(productData) {
    const res = await api.post('/products', productData)
    return res.data
}

export async function updateProduct(productId, productData) {
    const res = await api.put(`/products/${productId}`, productData)
    return res.data
}

export async function deleteProduct(productId) {
    const res = await api.delete(`/products/${productId}`)
    return res.data
}

// ========================
// STOCK
// ========================

export async function updateStock(productId, stock) {
    const res = await api.patch(`/products/${productId}/stock`, { stock })
    return res.data
}

// ========================
// REVIEWS
// ========================

export async function createReview(productId, reviewData) {
    const res = await api.post(`/products/${productId}/reviews`, reviewData)
    return res.data
}
