# Umut'un REST API Metotları
## 8. Ürün Ekleme
- **Endpoint:** `POST /products`
- **Request Body:** 
```json
{
  "name": "Organik Domates",
  "description": "Doğal yöntemlerle yetiştirilmiş organik domates",
  "price": 79.90,
  "stock": 150,
  "categoryId": "cat_123",
  "producerId": "prod_456"
}
```
- **Authentication:** Bearer Token gerekli
- **Response:** `201 Created` - Ürün başarıyla eklendi

## 9. Ürün Bilgilerini Güncelleme
- **Endpoint:** `PUT /products/{productId}`
- **Path Parameters:** 
  - `productId` (string, required) - Ürün ID'si
- **Request Body:** 
```json
{
  "name": "Organik Domates XL",
  "description": "Daha büyük boy organik domates",
  "price": 89.90
}
```
- **Authentication:** Bearer Token gerekli
- **Response:** `200 OK` - Ürün bilgileri başarıyla güncellendi

## 10. Ürün Yayından Kaldırma
- **Endpoint:** `DELETE /products/{productId}`
- **Path Parameters:** 
  - `productId` (string, required) - Ürün ID'si
- **Authentication:** Bearer Token gerekli
- **Response:** `204 No Content` - Ürün başarıyla yayından kaldırıldı

## 11. Ürünleri Listeleme
- **Endpoint:** `GET /products`
- **Query Parameters:** 
  - `page` (integer, optional) - Sayfa numarası
  - `limit` (integer, optional) - Sayfa başına kayıt sayısı
  - `categoryId` (string, optional) - Kategori filtresi
  - `producerId` (string, optional) - Üretici filtresi
- **Response:** `200 OK` - Ürün listesi başarıyla getirildi

## 12. Ürün Detayını Görüntüleme
- **Endpoint:** `GET /products/{productId}`
- **Path Parameters:** 
  - `productId` (string, required) - Ürün ID'si
- **Response:** `200 OK` - Ürün detayları başarıyla getirildi

## 13. Ürün Stok Miktarını Güncelleme
- **Endpoint:** `PATCH /products/{productId}/stock`
- **Path Parameters:** 
  - `productId` (string, required) - Ürün ID'si
- **Request Body:** 
```json
{
  "stock": 200
}
```
- **Authentication:** Bearer Token gerekli
- **Response:** `200 OK` - Ürün stok miktarı başarıyla güncellendi

## 14. Ürün Değerlendirmesi Yapma
- **Endpoint:** `POST /products/{productId}/reviews`
- **Path Parameters:** 
  - `productId` (string, required) - Ürün ID'si
- **Request Body:** 
```json
{
  "rating": 5,
  "comment": "Ürün çok taze ve kaliteli geldi."
}
```
- **Authentication:** Bearer Token gerekli
- **Response:** `201 Created` - Ürün değerlendirmesi başarıyla eklendi

