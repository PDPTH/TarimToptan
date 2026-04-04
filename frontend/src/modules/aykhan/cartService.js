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

import api from '../../services/api/api.js'

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
    // Eski backend 'street', yeni backend 'addressLine' bekler — ikisini de gönder
    const payload = { ...addressData, street: addressData.addressLine || addressData.street }
    const res = await api.post('/addresses', payload)
    return res.data
}

export async function updateAddress(addressId, addressData) {
    const payload = { ...addressData, street: addressData.addressLine || addressData.street }
    const res = await api.put(`/addresses/${addressId}`, payload)
    return res.data
}

export async function getAddresses() {
    const res = await api.get('/addresses')
    return res.data
}

// ========================
// ORDER HISTORY
// ========================

export async function getOrders() {
    const res = await api.get('/orders')
    return res.data
}
