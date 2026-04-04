const express = require('express')
const { findById, update, remove, findAll } = require('../utils/db')
const { authMiddleware } = require('../utils/auth')

const router = express.Router()

// GET /users/:userId
router.get('/:userId', authMiddleware, (req, res) => {
    const user = findById('users', req.params.userId)
    if (!user) return res.status(404).json({ code: 'NOT_FOUND', message: 'Kullanıcı bulunamadı' })
    const { password, ...safeUser } = user
    res.json(safeUser)
})

// PUT /users/:userId
router.put('/:userId', authMiddleware, (req, res) => {
    if (req.user.id !== req.params.userId) {
        return res.status(403).json({ code: 'FORBIDDEN', message: 'Sadece kendi profilinizi güncelleyebilirsiniz' })
    }
    const { firstName, lastName, email, phone } = req.body
    const updated = update('users', req.params.userId, { firstName, lastName, email, phone })
    if (!updated) return res.status(404).json({ code: 'NOT_FOUND', message: 'Kullanıcı bulunamadı' })
    const { password, ...safeUser } = updated
    res.json(safeUser)
})

// DELETE /users/:userId
router.delete('/:userId', authMiddleware, (req, res) => {
    if (req.user.id !== req.params.userId) {
        return res.status(403).json({ code: 'FORBIDDEN', message: 'Sadece kendi hesabınızı silebilirsiniz' })
    }
    const removed = remove('users', req.params.userId)
    if (!removed) return res.status(404).json({ code: 'NOT_FOUND', message: 'Kullanıcı bulunamadı' })
    res.status(204).send()
})

module.exports = router
