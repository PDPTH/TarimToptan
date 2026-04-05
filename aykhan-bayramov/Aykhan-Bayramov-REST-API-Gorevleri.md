## 1. Sepete Ürün Ekleme
**API Test Videosu:** [Link](https://1drv.ms/v/c/b587c82f474385b7/IQAc2n-qZyd9R7D_1ZxXpmFqAW_rzZUCBT0qBu1TkRQVUag?e=fmlbfC)

- **Endpoint:** `POST /cart/items`
- **Request Body:** 
```json
{
  "productId": "prd_123",
  "quantity": 2
}
```
- **Authentication:** Bearer Token gerekli
- **Response:** `201 Created` - Ürün sepete başarıyla eklendi

## 2. Sepetten Ürün Çıkarma
- **Endpoint:** `DELETE /cart/items/{itemId}`
- **Path Parameters:** 
  - `itemId` (string, required) - Sepet öğesi ID'si
- **Authentication:** Bearer Token gerekli
- **Response:** `204 No Content` - Ürün sepetten başarıyla çıkarıldı

## 3. Sepeti Görüntüleme
- **Endpoint:** `GET /cart`
- **Authentication:** Bearer Token gerekli
- **Response:** `200 OK` - Sepet bilgileri başarıyla getirildi

## 4. Sipariş Oluşturma
- **Endpoint:** `POST /orders`
- **Request Body:** 
```json
{
  "addressId": "addr_123",
  "paymentMethod": "credit_card",
  "notes": "Kapıya bırakılabilir."
}
```
- **Authentication:** Bearer Token gerekli
- **Response:** `201 Created` - Sipariş başarıyla oluşturuldu

## 5. Sipariş İptal Etme
- **Endpoint:** `DELETE /orders/{orderId}`
- **Path Parameters:** 
  - `orderId` (string, required) - Sipariş ID'si
- **Authentication:** Bearer Token gerekli
- **Response:** `200 OK` - Sipariş başarıyla iptal edildi

## 6. Teslimat Adresi Ekleme
- **Endpoint:** `POST /addresses`
- **Request Body:** 
```json
{
  "title": "Ev",
  "fullName": "Ahmet Yılmaz",
  "phone": "+905551234567",
  "city": "İstanbul",
  "district": "Kadıköy",
  "addressLine": "Örnek Mah. Örnek Sok. No:12 Daire:4",
  "postalCode": "34710"
}
```
- **Authentication:** Bearer Token gerekli
- **Response:** `201 Created` - Teslimat adresi başarıyla eklendi

## 7. Teslimat Adresi Güncelleme
- **Endpoint:** `PUT /addresses/{addressId}`
- **Path Parameters:** 
  - `addressId` (string, required) - Adres ID'si
- **Request Body:** 
```json
{
  "title": "İş Yeri",
  "fullName": "Ahmet Yılmaz",
  "phone": "+905551234567",
  "city": "İstanbul",
  "district": "Beşiktaş",
  "addressLine": "Barbaros Bulvarı No:55",
  "postalCode": "34353"
}
```
- **Authentication:** Bearer Token gerekli
- **Response:** `200 OK` - Teslimat adresi başarıyla güncellendi

