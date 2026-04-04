const express = require('express')
const bcrypt = require('bcryptjs')
const { v4: uuidv4 } = require('uuid')
const { findAll, insert } = require('../utils/db')
const { generateToken } = require('../utils/auth')

const router = express.Router()

// POST /auth/register
router.post('/register', async (req, res) => {
    try {
        const { email, password, firstName, lastName } = req.body

        if (!email || !password || !firstName || !lastName) {
            return res.status(400).json({ code: 'BAD_REQUEST', message: 'Tüm alanlar zorunludur (email, password, firstName, lastName)' })
        }
        if (password.length < 8) {
            return res.status(400).json({ code: 'BAD_REQUEST', message: 'Şifre en az 8 karakter olmalıdır' })
        }

        const users = findAll('users')
        if (users.find(u => u.email === email)) {
            return res.status(409).json({ code: 'CONFLICT', message: 'Bu email adresi zaten kullanımda' })
        }

        const hashedPassword = await bcrypt.hash(password, 10)
        const user = {
            id: uuidv4(),
            email,
            password: hashedPassword,
            firstName,
            lastName,
            role: 'user',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
        }

        insert('users', user)

        // Aynı zamanda producers koleksiyonuna da ekle
        insert('producers', {
            id: user.id,
            name: `${firstName} ${lastName}`,
            email: user.email,
            phone: '',
            address: 'Türkiye',
            createdAt: user.createdAt,
        })

        const { password: _, ...safeUser } = user
        res.status(201).json(safeUser)
    } catch (err) {
        console.error('Register error:', err)
        res.status(500).json({ code: 'INTERNAL_ERROR', message: 'Kayıt işlemi başarısız' })
    }
})

// POST /auth/login
router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body

        if (!email || !password) {
            return res.status(400).json({ code: 'BAD_REQUEST', message: 'Email ve şifre zorunludur' })
        }

        const users = findAll('users')
        const user = users.find(u => u.email === email)

        if (!user) {
            return res.status(401).json({ code: 'UNAUTHORIZED', message: 'Email veya şifre hatalı' })
        }

        const isValidPassword = await bcrypt.compare(password, user.password)
        if (!isValidPassword) {
            return res.status(401).json({ code: 'UNAUTHORIZED', message: 'Email veya şifre hatalı' })
        }

        const token = generateToken(user)
        const { password: _, ...safeUser } = user

        res.json({ token, expiresIn: 604800, user: safeUser })
    } catch (err) {
        console.error('Login error:', err)
        res.status(500).json({ code: 'INTERNAL_ERROR', message: 'Giriş işlemi başarısız' })
    }
})

// POST /auth/reset-password-request
router.post('/reset-password-request', (req, res) => {
    const { email } = req.body
    if (!email) {
        return res.status(400).json({ code: 'BAD_REQUEST', message: 'Email zorunludur' })
    }
    // Demo: sadece success döndür
    res.json({ message: 'Şifre sıfırlama bağlantısı e-posta adresinize gönderildi' })
})

module.exports = router
