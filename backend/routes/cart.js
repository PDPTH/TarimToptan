const express = require('express')
const { v4: uuidv4 } = require('uuid')
const { findAll, insert, remove, writeCollection } = require('../utils/db')
const { authMiddleware } = require('../utils/auth')

const router = express.Router()

// Kullanıcının sepetini bul
function getUserCart(userId) {
    const carts = findAll('carts')
    let cart = carts.find(c => c.userId === userId)
    if (!cart) {
        cart = { id: uuidv4(), userId, items: [], createdAt: new Date().toISOString() }
        insert('carts', cart)
    }
    return cart
}

function saveCart(cart) {
    const carts = findAll('carts')
    const index = carts.findIndex(c => c.userId === cart.userId)
    if (index >= 0) carts[index] = cart
    else carts.push(cart)
    writeCollection('carts', carts)
}

// GET /cart
router.get('/', authMiddleware, (req, res) => {
    const cart = getUserCart(req.user.id)
    // Toplam fiyat hesapla
    const totalPrice = cart.items.reduce((sum, item) => sum + (item.price * item.quantity), 0)
    res.json({ ...cart, totalPrice })
})

// POST /cart/items
router.post('/items', authMiddleware, (req, res) => {
    const { productId, quantity } = req.body

    if (!productId) {
        return res.status(400).json({ code: 'BAD_REQUEST', message: 'Ürün ID zorunludur' })
    }

    const product = require('../utils/db').findById('products', productId)

    const cart = getUserCart(req.user.id)
    const existingIndex = cart.items.findIndex(i => i.productId === productId)

    if (existingIndex >= 0) {
        cart.items[existingIndex].quantity += parseInt(quantity) || 1
    } else {
        cart.items.push({
            id: uuidv4(),
            productId,
            name: product?.name || 'Ürün',
            price: product?.price || 0,
            quantity: parseInt(quantity) || 1,
            addedAt: new Date().toISOString(),
        })
    }

    saveCart(cart)
    res.status(201).json(cart)
})

// DELETE /cart/items/:itemId
router.delete('/items/:itemId', authMiddleware, (req, res) => {
    const cart = getUserCart(req.user.id)
    const initialLength = cart.items.length
    cart.items = cart.items.filter(i => i.id !== req.params.itemId)

    if (cart.items.length === initialLength) {
        return res.status(404).json({ code: 'NOT_FOUND', message: 'Sepet öğesi bulunamadı' })
    }

    saveCart(cart)
    res.json(cart)
})

module.exports = router
