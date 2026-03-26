/**
 * Seed script — veritabanını örnek tarım ürünleri ve üreticilerle doldurur
 * Kullanım: node seed.js
 */

const { writeCollection } = require('./utils/db')
const { v4: uuidv4 } = require('uuid')
const bcrypt = require('bcryptjs')

async function seed() {
    console.log('🌱 Seed verileri oluşturuluyor...')

    // Üretici IDleri
    const producerIds = {
        ege: uuidv4(),
        karadeniz: uuidv4(),
        guneydogu: uuidv4(),
        akdeniz: uuidv4(),
        anadolu: uuidv4(),
        dogaAna: uuidv4(),
    }

    // Üreticiler
    const producers = [
        { id: producerIds.ege, name: 'Ege Tarım Kooperatifi', email: 'info@egetarim.com', phone: '+902321234567', address: 'İzmir, Türkiye', createdAt: '2024-01-10T10:00:00Z' },
        { id: producerIds.karadeniz, name: 'Karadeniz Organik', email: 'info@karadenizorganik.com', phone: '+904621234567', address: 'Trabzon, Türkiye', createdAt: '2024-01-15T10:00:00Z' },
        { id: producerIds.guneydogu, name: 'Güneydoğu Bakliyat', email: 'satis@guneydogubakliyat.com', phone: '+903421234567', address: 'Gaziantep, Türkiye', createdAt: '2024-02-01T10:00:00Z' },
        { id: producerIds.akdeniz, name: 'Akdeniz Sera Üretim', email: 'info@akdenizsera.com', phone: '+902421234567', address: 'Antalya, Türkiye', createdAt: '2024-02-10T10:00:00Z' },
        { id: producerIds.anadolu, name: 'Anadolu Meyvecilik', email: 'siparis@anadolumeyve.com', phone: '+902461234567', address: 'Isparta, Türkiye', createdAt: '2024-03-01T10:00:00Z' },
        { id: producerIds.dogaAna, name: 'Doğa Ana Organik Çiftlik', email: 'ciftlik@dogaana.com', phone: '+903741234567', address: 'Bolu, Türkiye', createdAt: '2024-03-15T10:00:00Z' },
    ]

    // Ürünler
    const products = [
        { id: uuidv4(), name: 'Kırmızı Mercimek', description: 'Güneydoğu Anadolu bölgesinden taze öğütülmüş kırmızı mercimek. Yüksek protein içerikli, çorba ve köfte için ideal.', price: 54.90, category: 'Bakliyat', stock: 500, producerId: producerIds.guneydogu, createdAt: '2024-06-01T10:00:00Z', updatedAt: '2024-06-01T10:00:00Z' },
        { id: uuidv4(), name: 'Osmancık Pirinç', description: 'Tosya ovalarından hasat edilen birinci sınıf Osmancık pirinci. Pilav ve çorba için mükemmel.', price: 189.00, category: 'Tahıl', stock: 300, producerId: producerIds.ege, createdAt: '2024-06-02T10:00:00Z', updatedAt: '2024-06-02T10:00:00Z' },
        { id: uuidv4(), name: 'Sarı Nohut', description: 'Konya ovasından organik sarı nohut. Sertifikalı organik, pilaki ve hummus için harika.', price: 72.50, category: 'Bakliyat', stock: 400, producerId: producerIds.guneydogu, createdAt: '2024-06-03T10:00:00Z', updatedAt: '2024-06-03T10:00:00Z' },
        { id: uuidv4(), name: 'Köftelik Bulgur', description: 'İnce çekilmiş köftelik bulgur. Çiğ köfte, mercimek köftesi ve salata için ideal.', price: 42.00, category: 'Tahıl', stock: 600, producerId: producerIds.guneydogu, createdAt: '2024-06-04T10:00:00Z', updatedAt: '2024-06-04T10:00:00Z' },
        { id: uuidv4(), name: 'Organik Domates', description: 'Antalya seralarından taze organik domates. İlaçsız, doğal yetiştirme.', price: 34.90, category: 'Sebze', stock: 200, producerId: producerIds.akdeniz, createdAt: '2024-06-05T10:00:00Z', updatedAt: '2024-06-05T10:00:00Z' },
        { id: uuidv4(), name: 'Yeşil Biber', description: 'Çarliston ve sivri biber çeşitleri. Taze, çıtır ve lezzetli.', price: 29.90, category: 'Sebze', stock: 350, producerId: producerIds.akdeniz, createdAt: '2024-06-06T10:00:00Z', updatedAt: '2024-06-06T10:00:00Z' },
        { id: uuidv4(), name: 'Kuru Fasulye (Dermason)', description: 'İspir dermason fasulyesi. Kuru fasulye pilav için vazgeçilmez, yumuşak pişen cins.', price: 94.00, category: 'Bakliyat', stock: 450, producerId: producerIds.guneydogu, createdAt: '2024-06-07T10:00:00Z', updatedAt: '2024-06-07T10:00:00Z' },
        { id: uuidv4(), name: 'Antep Fıstığı', description: 'Gaziantep\'in meşhur yeşil iç fıstığı. Taze kavrulmuş, atıştırmalık ve tatlı için.', price: 249.90, category: 'Kuruyemiş', stock: 150, producerId: producerIds.guneydogu, createdAt: '2024-06-08T10:00:00Z', updatedAt: '2024-06-08T10:00:00Z' },
        { id: uuidv4(), name: 'Kırmızı Elma (Starking)', description: 'Isparta yaylalarından taze kırmızı elma. Sulu, tatlı ve çıtır.', price: 45.00, category: 'Meyve', stock: 250, producerId: producerIds.anadolu, createdAt: '2024-06-09T10:00:00Z', updatedAt: '2024-06-09T10:00:00Z' },
        { id: uuidv4(), name: 'Organik Bal (Çam Balı)', description: 'Bolu yaylalarından saf çam balı. Organik sertifikalı, doğal antibiyotik.', price: 320.00, category: 'Süt Ürünleri', stock: 80, producerId: producerIds.dogaAna, createdAt: '2024-06-10T10:00:00Z', updatedAt: '2024-06-10T10:00:00Z' },
        { id: uuidv4(), name: 'Yeşil Mercimek', description: 'Yozgat bölgesinden yeşil mercimek. Çorba, salata ve pilav için ideal.', price: 62.00, category: 'Bakliyat', stock: 550, producerId: producerIds.guneydogu, createdAt: '2024-06-11T10:00:00Z', updatedAt: '2024-06-11T10:00:00Z' },
        { id: uuidv4(), name: 'Patlıcan (Kemer)', description: 'Antalya sera patlıcanı. Kızartma, kebap ve musakka için ideal.', price: 27.50, category: 'Sebze', stock: 300, producerId: producerIds.akdeniz, createdAt: '2024-06-12T10:00:00Z', updatedAt: '2024-06-12T10:00:00Z' },
        { id: uuidv4(), name: 'Ceviz İçi', description: 'Niksar ceviz içi. Taze kırım, omega-3 deposu.', price: 179.00, category: 'Kuruyemiş', stock: 120, producerId: producerIds.karadeniz, createdAt: '2024-06-13T10:00:00Z', updatedAt: '2024-06-13T10:00:00Z' },
        { id: uuidv4(), name: 'Trabzon Fındığı', description: 'Karadeniz\'in meşhur tombul fındığı. Taze hasat, kavrulmamış.', price: 210.00, category: 'Kuruyemiş', stock: 200, producerId: producerIds.karadeniz, createdAt: '2024-06-14T10:00:00Z', updatedAt: '2024-06-14T10:00:00Z' },
        { id: uuidv4(), name: 'Portakal (Washington)', description: 'Finike portakalı. İnce kabuklu, bol sulu, vitamin C deposu.', price: 39.90, category: 'Meyve', stock: 400, producerId: producerIds.akdeniz, createdAt: '2024-06-15T10:00:00Z', updatedAt: '2024-06-15T10:00:00Z' },
        { id: uuidv4(), name: 'Kuru Kayısı', description: 'Malatya\'nın dünyaca ünlü kuru kayısısı. Güneşte kurutulmuş, katkısız.', price: 89.90, category: 'Meyve', stock: 350, producerId: producerIds.anadolu, createdAt: '2024-06-16T10:00:00Z', updatedAt: '2024-06-16T10:00:00Z' },
    ]

    // Demo kullanıcı
    const hashedPass = await bcrypt.hash('12345678', 10)
    const users = [
        { id: uuidv4(), email: 'demo@tarimtoptan.com', password: hashedPass, firstName: 'Demo', lastName: 'Kullanıcı', role: 'user', createdAt: '2024-01-01T10:00:00Z', updatedAt: '2024-01-01T10:00:00Z' },
        { id: uuidv4(), email: 'admin@tarimtoptan.com', password: hashedPass, firstName: 'Admin', lastName: 'Yönetici', role: 'admin', createdAt: '2024-01-01T10:00:00Z', updatedAt: '2024-01-01T10:00:00Z' },
    ]

    // Veritabanına yaz
    writeCollection('users', users)
    writeCollection('producers', producers)
    writeCollection('products', products)
    writeCollection('reviews', [])
    writeCollection('carts', [])
    writeCollection('orders', [])
    writeCollection('addresses', [])

    console.log(`✅ ${users.length} kullanıcı oluşturuldu`)
    console.log(`✅ ${producers.length} üretici oluşturuldu`)
    console.log(`✅ ${products.length} ürün oluşturuldu`)
    console.log('🎉 Seed tamamlandı!')
    console.log('')
    console.log('Demo hesap: demo@tarimtoptan.com / 12345678')
    console.log('Admin hesap: admin@tarimtoptan.com / 12345678')
}

seed().catch(console.error)
