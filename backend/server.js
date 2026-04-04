const express = require('express')
const cors = require('cors')
const path = require('path')

const authRoutes = require('./routes/auth')
const userRoutes = require('./routes/users')
const productRoutes = require('./routes/products')
const producerRoutes = require('./routes/producers')
const cartRoutes = require('./routes/cart')
const orderRoutes = require('./routes/orders')
const addressRoutes = require('./routes/addresses')

const app = express()
const PORT = process.env.PORT || 3000

// Middleware
app.use(cors())
app.use(express.json())

// Request logger
app.use((req, res, next) => {
    console.log(`${new Date().toISOString()} | ${req.method} ${req.url}`)
    next()
})

// API Routes - /v1 prefix
app.use('/v1/auth', authRoutes)
app.use('/v1/users', userRoutes)
app.use('/v1/products', productRoutes)
app.use('/v1/producers', producerRoutes)
app.use('/v1/cart', cartRoutes)
app.use('/v1/orders', orderRoutes)
app.use('/v1/addresses', addressRoutes)

// Health check
app.get('/', (req, res) => {
    res.json({
        name: 'TarımToptan API',
        version: '1.0.0',
        status: 'running',
        endpoints: '/v1',
        docs: 'https://github.com/PDPTH/TarimToptan',
    })
})

app.get('/v1', (req, res) => {
    res.json({
        message: 'TarımToptan API v1',
        endpoints: {
            auth: '/v1/auth (POST /register, POST /login, POST /reset-password-request)',
            users: '/v1/users (GET /:userId, PUT /:userId, DELETE /:userId)',
            products: '/v1/products (GET, POST, GET /:id, PUT /:id, DELETE /:id, PATCH /:id/stock, POST /:id/reviews)',
            producers: '/v1/producers (GET, GET /:id)',
            cart: '/v1/cart (GET, POST /items, DELETE /items/:id)',
            orders: '/v1/orders (POST, PATCH /:id/cancel)',
            addresses: '/v1/addresses (POST, PUT /:id)',
        }
    })
})

// 404 handler
app.use((req, res) => {
    res.status(404).json({ code: 'NOT_FOUND', message: `Route ${req.method} ${req.url} bulunamadı` })
})

// Error handler
app.use((err, req, res, next) => {
    console.error('Server Error:', err)
    res.status(500).json({ code: 'INTERNAL_ERROR', message: 'Sunucu hatası' })
})

app.listen(PORT, () => {
    console.log(`🌾 TarımToptan API çalışıyor: http://localhost:${PORT}`)
    console.log(`📄 API Bilgisi: http://localhost:${PORT}/v1`)
})
