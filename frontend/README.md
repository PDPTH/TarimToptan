# 🌾 TarımToptan — Frontend

Tarım ürünleri e-ticaret platformu frontend uygulaması.

## 🔗 Linkler

| Kaynak | URL |
|--------|-----|
| **Frontend** | [frontend.yazmuh.com](https://frontend.yazmuh.com) |
| **REST API** | [api.yazmuh.com](https://api.yazmuh.com) |
| **API Base URL** | `https://api.yazmuh.com/v1` |

## 🛠 Teknolojiler

- **React 18** + **Vite 5** (SPA)
- **React Router v6** (Client-side routing)
- **Axios** (HTTP client, interceptors)
- **React Icons** (Feather Icons, Game Icons)
- **React Hot Toast** (Bildirimler)

## 🚀 Kurulum & Çalıştırma

```bash
# Bağımlılıkları yükle
cd frontend
npm install

# Development server başlat
npm run dev

# Production build
npm run build

# Önizleme
npm run preview
```

## 📁 Proje Yapısı

```
src/
├── components/          # Paylaşılan UI bileşenleri
│   ├── Navbar.jsx
│   ├── Footer.jsx
│   ├── ProtectedRoute.jsx
│   ├── ProductCard.jsx
│   └── LoadingSpinner.jsx
├── pages/               # Sayfa bileşenleri
│   ├── HomePage.jsx
│   ├── LoginPage.jsx
│   ├── RegisterPage.jsx
│   ├── ResetPasswordPage.jsx
│   ├── ProfilePage.jsx
│   ├── ProductsPage.jsx
│   ├── ProductDetailPage.jsx
│   ├── ProductManagePage.jsx
│   ├── ProducersPage.jsx
│   ├── ProducerDetailPage.jsx
│   ├── CartPage.jsx
│   ├── CheckoutPage.jsx
│   └── AddressPage.jsx
├── services/api/        # API client (Axios)
│   └── api.js
├── modules/             # Modüler servisler (kişiye göre)
│   ├── suleyman/        # Auth, User, Producers
│   │   └── authService.js
│   ├── umut/            # Products, Stock, Reviews
│   │   └── productService.js
│   └── aykhan/          # Cart, Orders, Addresses
│       └── cartService.js
├── context/             # React Context
│   └── AuthContext.jsx
├── App.jsx              # Router yapısı
├── App.css              # Global stiller
└── main.jsx             # Entry point
```

## 👥 Görev Dağılımı

### Süleyman Buğra Çetin
| # | Gereksinim | Endpoint | Sayfa |
|---|-----------|----------|-------|
| 1 | Kayıt olma | `POST /auth/register` | RegisterPage |
| 2 | Giriş yapma | `POST /auth/login` | LoginPage |
| 3 | Profil güncelleme | `PUT /users/{userId}` | ProfilePage |
| 4 | Hesap silme | `DELETE /users/{userId}` | ProfilePage |
| 5 | Üreticileri listeleme | `GET /producers` | ProducersPage |
| 6 | Üretici detay | `GET /producers/{producerId}` | ProducerDetailPage |
| 7 | Şifre sıfırlama | `POST /auth/reset-password-request` | ResetPasswordPage |

### Umut Can Bayar
| # | Gereksinim | Endpoint | Sayfa |
|---|-----------|----------|-------|
| 8 | Ürün ekleme | `POST /products` | ProductManagePage |
| 9 | Ürün güncelleme | `PUT /products/{productId}` | ProductManagePage |
| 10 | Ürün silme | `DELETE /products/{productId}` | ProductManagePage |
| 11 | Ürün listeleme | `GET /products` | ProductsPage |
| 12 | Ürün detay | `GET /products/{productId}` | ProductDetailPage |
| 13 | Stok güncelleme | `PATCH /products/{productId}/stock` | ProductManagePage |
| 14 | Değerlendirme | `POST /products/{productId}/reviews` | ProductDetailPage |

### Aykhan Bayramov
| # | Gereksinim | Endpoint | Sayfa |
|---|-----------|----------|-------|
| 15 | Sepete ekleme | `POST /cart/items` | ProductDetailPage |
| 16 | Sepetten çıkarma | `DELETE /cart/items/{itemId}` | CartPage |
| 17 | Sepet görüntüleme | `GET /cart` | CartPage |
| 18 | Sipariş oluşturma | `POST /orders` | CheckoutPage |
| 19 | Sipariş iptal | `PATCH /orders/{orderId}/cancel` | CheckoutPage |
| 20 | Adres ekleme | `POST /addresses` | AddressPage / CheckoutPage |
| 21 | Adres güncelleme | `PUT /addresses/{addressId}` | AddressPage |

## 🌍 Deployment (Vercel)

1. [Vercel](https://vercel.com) hesabı oluştur
2. GitHub reposunu bağla
3. Root Directory: `frontend`
4. Framework: Vite
5. Environment Variables:
   ```
   VITE_API_URL=https://api.yazmuh.com/v1
   ```
6. Deploy et

## 🔀 Branch Stratejisi

```
main                     ← production
├── feature/suleyman-auth    ← Auth, User, Producers
├── feature/umut-products    ← Products, Stock, Reviews
└── feature/aykhan-cart      ← Cart, Orders, Addresses
```

## 📮 Postman Test Uyumu

Tüm endpoint'ler standart request/response formatına uyumludur:
- `Authorization: Bearer {token}` header'ı
- `Content-Type: application/json`
- Standart hata formatı: `{ code, message, details }`

## 📄 Lisans

MIT © 2026 USA Grubu
