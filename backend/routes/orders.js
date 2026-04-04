const express = require('express')
const { v4: uuidv4 } = require('uuid')
const { findAll, findById, insert, update, paginate } = require('../utils/db')
const { authMiddleware } = require('../utils/auth')

const router = express.Router()

// POST /orders
router.post('/', authMiddleware, (req, res) => {
    const { items, shippingAddress, addressId } = req.body

    if ((!items || items.length === 0) && !addressId) {
        // Sepetteki ürünlerden oluştur
        const carts = findAll('carts')
        const cart = carts.find(c => c.userId === req.user.id)
        if (!cart || cart.items.length === 0) {
            return res.status(400).json({ code: 'BAD_REQUEST', message: 'Sepet boş, sipariş oluşturulamaz' })
        }
    }

    // Sipariş öğelerini belirle
    let orderItems = items || []
    let totalAmount = 0

    if (orderItems.length === 0) {
        const carts = findAll('carts')
        const cart = carts.find(c => c.userId === req.user.id)
        if (cart) {
            orderItems = cart.items.map(item => ({
                productId: item.productId,
                productName: item.name,
                quantity: item.quantity,
                unitPrice: item.price,
                totalPrice: item.price * item.quantity,
            }))
        }
    }

    totalAmount = orderItems.reduce((sum, item) => sum + (item.totalPrice || (item.unitPrice * item.quantity) || 0), 0)

    const order = {
        id: uuidv4(),
        userId: req.user.id,
        items: orderItems,
        totalAmount,
        status: 'pending',
        shippingAddress: shippingAddress || null,
        addressId: addressId || null,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
    }

    insert('orders', order)

    // Sepeti temizle
    const carts = findAll('carts')
    const cartIndex = carts.findIndex(c => c.userId === req.user.id)
    if (cartIndex >= 0) {
        carts[cartIndex].items = []
        require('../utils/db').writeCollection('carts', carts)
    }

    res.status(201).json(order)
})

// PATCH /orders/:orderId/cancel
router.patch('/:orderId/cancel', authMiddleware, (req, res) => {
    const order = findById('orders', req.params.orderId)
    if (!order) return res.status(404).json({ code: 'NOT_FOUND', message: 'Sipariş bulunamadı' })
    if (order.userId !== req.user.id) {
        return res.status(403).json({ code: 'FORBIDDEN', message: 'Sadece kendi siparişinizi iptal edebilirsiniz' })
    }
    if (order.status === 'shipped' || order.status === 'delivered') {
        return res.status(400).json({ code: 'BAD_REQUEST', message: 'Gönderilmiş veya teslim edilmiş sipariş iptal edilemez' })
    }

    const updated = update('orders', req.params.orderId, { status: 'cancelled' })
    res.json(updated)
})

module.exports = router
