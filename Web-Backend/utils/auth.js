const jwt = require('jsonwebtoken')

const JWT_SECRET = process.env.JWT_SECRET || 'tarimtoptan-secret-key-2026'

function generateToken(user) {
    return jwt.sign(
        { id: user.id, email: user.email, role: user.role || 'user' },
        JWT_SECRET,
        { expiresIn: '7d' }
    )
}

function verifyToken(token) {
    return jwt.verify(token, JWT_SECRET)
}

// Auth middleware
function authMiddleware(req, res, next) {
    const authHeader = req.headers.authorization
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({ code: 'UNAUTHORIZED', message: 'Token gerekli' })
    }
    try {
        const token = authHeader.split(' ')[1]
        const decoded = verifyToken(token)
        req.user = decoded
        next()
    } catch (err) {
        return res.status(401).json({ code: 'UNAUTHORIZED', message: 'Geçersiz veya süresi dolmuş token' })
    }
}

// Optional auth (token varsa decode et, yoksa devam et)
function optionalAuth(req, res, next) {
    const authHeader = req.headers.authorization
    if (authHeader && authHeader.startsWith('Bearer ')) {
        try {
            const token = authHeader.split(' ')[1]
            req.user = verifyToken(token)
        } catch { /* ignore invalid tokens */ }
    }
    next()
}

module.exports = { generateToken, verifyToken, authMiddleware, optionalAuth, JWT_SECRET }
