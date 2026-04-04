/**
 * Aykhan Modülü — Cart, Orders, Addresses API Servisleri
 * 
 * Endpoints:
 * - GET /cart
 * - POST /cart/items
 * - DELETE /cart/items/{itemId}
 * - POST /orders
 * - PATCH /orders/{orderId}/cancel
 * - POST /addresses
 * - PUT /addresses/{addressId}
 */

import api from '../../services/api/api'

// ========================
// CART
// ========================

export async function getCart() {
    const res = await api.get('/cart')
    return res.data
}

export async function addToCart(productId, quantity) {
    const res = await api.post('/cart/items', { productId, quantity })
    return res.data
}

export async function removeFromCart(itemId) {
    const res = await api.delete(`/cart/items/${itemId}`)
    return res.data
}

// ========================
// ORDERS
// ========================

export async function createOrder(orderData) {
    const res = await api.post('/orders', orderData)
    return res.data
}

export async function cancelOrder(orderId) {
    const res = await api.patch(`/orders/${orderId}/cancel`)
    return res.data
}

// ========================
// ADDRESSES
// ========================

export async function createAddress(addressData) {
    const res = await api.post('/addresses', addressData)
    return res.data
}

export async function updateAddress(addressId, addressData) {
    const res = await api.put(`/addresses/${addressId}`, addressData)
    return res.data
}
