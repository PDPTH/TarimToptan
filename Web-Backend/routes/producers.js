const express = require('express')
const { findAll, findById, paginate } = require('../utils/db')

const router = express.Router()

// GET /producers
router.get('/', (req, res) => {
    const producers = findAll('producers')
    const result = paginate(producers, req.query.page, req.query.limit)
    res.json(result)
})

// GET /producers/:producerId
router.get('/:producerId', (req, res) => {
    const producer = findById('producers', req.params.producerId)
    if (!producer) return res.status(404).json({ code: 'NOT_FOUND', message: 'Üretici bulunamadı' })
    res.json(producer)
})

module.exports = router
