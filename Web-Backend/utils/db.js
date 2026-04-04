/**
 * JSON dosya tabanlı veritabanı
 * Her koleksiyon ayrı bir .json dosyasında saklanır
 */

const fs = require('fs')
const path = require('path')

const DATA_DIR = path.join(__dirname, '..', 'data')

// Data klasörünü oluştur
if (!fs.existsSync(DATA_DIR)) {
    fs.mkdirSync(DATA_DIR, { recursive: true })
}

function getFilePath(collection) {
    return path.join(DATA_DIR, `${collection}.json`)
}

function readCollection(collection) {
    const filePath = getFilePath(collection)
    if (!fs.existsSync(filePath)) {
        fs.writeFileSync(filePath, '[]', 'utf-8')
        return []
    }
    try {
        const data = fs.readFileSync(filePath, 'utf-8')
        return JSON.parse(data)
    } catch {
        return []
    }
}

function writeCollection(collection, data) {
    const filePath = getFilePath(collection)
    fs.writeFileSync(filePath, JSON.stringify(data, null, 2), 'utf-8')
}

function findById(collection, id) {
    const data = readCollection(collection)
    return data.find(item => item.id === id)
}

function insert(collection, item) {
    const data = readCollection(collection)
    data.push(item)
    writeCollection(collection, data)
    return item
}

function update(collection, id, updates) {
    const data = readCollection(collection)
    const index = data.findIndex(item => item.id === id)
    if (index === -1) return null
    data[index] = { ...data[index], ...updates, updatedAt: new Date().toISOString() }
    writeCollection(collection, data)
    return data[index]
}

function remove(collection, id) {
    const data = readCollection(collection)
    const index = data.findIndex(item => item.id === id)
    if (index === -1) return false
    data.splice(index, 1)
    writeCollection(collection, data)
    return true
}

function findAll(collection) {
    return readCollection(collection)
}

function paginate(data, page = 1, limit = 20) {
    const p = Math.max(1, parseInt(page) || 1)
    const l = Math.min(100, Math.max(1, parseInt(limit) || 20))
    const totalItems = data.length
    const totalPages = Math.ceil(totalItems / l) || 1
    const start = (p - 1) * l
    const items = data.slice(start, start + l)
    return {
        data: items,
        pagination: { page: p, limit: l, totalPages, totalItems }
    }
}

module.exports = { readCollection, writeCollection, findById, insert, update, remove, findAll, paginate }
