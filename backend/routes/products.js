const express = require('express')
const { v4: uuidv4 } = require('uuid')
const { findAll, findById, insert, update, remove, paginate } = require('../utils/db')
const { authMiddleware, optionalAuth } = require('../utils/auth')

const router = express.Router()

// GET /products
router.get('/', (req, res) => {
    let products = findAll('products')
    const { category, minPrice, maxPrice, producerId } = req.query

    if (category) products = products.filter(p => p.category === category)
    if (minPrice) products = products.filter(p => p.price >= parseFloat(minPrice))
    if (maxPrice) products = products.filter(p => p.price <= parseFloat(maxPrice))
    if (producerId) products = products.filter(p => p.producerId === producerId)

    const result = paginate(products, req.query.page, req.query.limit)
    res.json(result)
})

// POST /products
router.post('/', authMiddleware, (req, res) => {
    const { name, description, price, category, stock, producerId } = req.body

    if (!name || price === undefined) {
        return res.status(400).json({ code: 'BAD_REQUEST', message: 'Ürün adı ve fiyat zorunludur' })
    }

    const product = {
        id: uuidv4(),
        name,
        description: description || '',
        price: parseFloat(price),
        category: category || 'Diğer',
        stock: parseInt(stock) || 0,
        producerId: producerId || req.user.id,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
    }

    insert('products', product)
    res.status(201).json(product)
})

// GET /products/:productId
router.get('/:productId', (req, res) => {
    const product = findById('products', req.params.productId)
    if (!product) return res.status(404).json({ code: 'NOT_FOUND', message: 'Ürün bulunamadı' })
    res.json(product)
})

// PUT /products/:productId
router.put('/:productId', authMiddleware, (req, res) => {
    const { name, description, price, category, stock, producerId } = req.body
    const updates = {}
    if (name !== undefined) updates.name = name
    if (description !== undefined) updates.description = description
    if (price !== undefined) updates.price = parseFloat(price)
    if (category !== undefined) updates.category = category
    if (stock !== undefined) updates.stock = parseInt(stock)
    if (producerId !== undefined) updates.producerId = producerId

    const updated = update('products', req.params.productId, updates)
    if (!updated) return res.status(404).json({ code: 'NOT_FOUND', message: 'Ürün bulunamadı' })
    res.json(updated)
})

// DELETE /products/:productId
router.delete('/:productId', authMiddleware, (req, res) => {
    const removed = remove('products', req.params.productId)
    if (!removed) return res.status(404).json({ code: 'NOT_FOUND', message: 'Ürün bulunamadı' })
    res.status(204).send()
})

// PATCH /products/:productId/stock
router.patch('/:productId/stock', authMiddleware, (req, res) => {
    const { stock } = req.body
    if (stock === undefined) {
        return res.status(400).json({ code: 'BAD_REQUEST', message: 'Stok miktarı zorunludur' })
    }
    const updated = update('products', req.params.productId, { stock: parseInt(stock) })
    if (!updated) return res.status(404).json({ code: 'NOT_FOUND', message: 'Ürün bulunamadı' })
    res.json(updated)
})

// POST /products/:productId/reviews
router.post('/:productId/reviews', authMiddleware, (req, res) => {
    const product = findById('products', req.params.productId)
    if (!product) return res.status(404).json({ code: 'NOT_FOUND', message: 'Ürün bulunamadı' })

    const { rating, comment } = req.body
    if (!comment) {
        return res.status(400).json({ code: 'BAD_REQUEST', message: 'Yorum zorunludur' })
    }

    const review = {
        id: uuidv4(),
        productId: req.params.productId,
        userId: req.user.id,
        rating: parseInt(rating) || 5,
        comment,
        createdAt: new Date().toISOString(),
    }

    insert('reviews', review)
    res.status(201).json(review)
})

module.exports = router
