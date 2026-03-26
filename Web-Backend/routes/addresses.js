const express = require('express')
const { v4: uuidv4 } = require('uuid')
const { findAll, findById, insert, update, paginate } = require('../utils/db')
const { authMiddleware } = require('../utils/auth')

const router = express.Router()

// POST /addresses
router.post('/', authMiddleware, (req, res) => {
    const { title, fullName, phone, city, district, street, postalCode } = req.body

    if (!city || !street) {
        return res.status(400).json({ code: 'BAD_REQUEST', message: 'İl ve adres satırı zorunludur' })
    }

    const address = {
        id: uuidv4(),
        userId: req.user.id,
        title: title || 'Ev',
        fullName: fullName || '',
        phone: phone || '',
        city,
        district: district || '',
        street,
        postalCode: postalCode || '',
        country: 'Türkiye',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
    }

    insert('addresses', address)
    res.status(201).json(address)
})

// PUT /addresses/:addressId
router.put('/:addressId', authMiddleware, (req, res) => {
    const address = findById('addresses', req.params.addressId)
    if (!address) return res.status(404).json({ code: 'NOT_FOUND', message: 'Adres bulunamadı' })
    if (address.userId !== req.user.id) {
        return res.status(403).json({ code: 'FORBIDDEN', message: 'Sadece kendi adresinizi güncelleyebilirsiniz' })
    }

    const { title, fullName, phone, city, district, street, postalCode } = req.body
    const updates = {}
    if (title !== undefined) updates.title = title
    if (fullName !== undefined) updates.fullName = fullName
    if (phone !== undefined) updates.phone = phone
    if (city !== undefined) updates.city = city
    if (district !== undefined) updates.district = district
    if (street !== undefined) updates.street = street
    if (postalCode !== undefined) updates.postalCode = postalCode

    const updated = update('addresses', req.params.addressId, updates)
    res.json(updated)
})

// GET /addresses (bonus: kullanıcının adreslerini listele)
router.get('/', authMiddleware, (req, res) => {
    const addresses = findAll('addresses').filter(a => a.userId === req.user.id)
    res.json({ data: addresses })
})

module.exports = router
