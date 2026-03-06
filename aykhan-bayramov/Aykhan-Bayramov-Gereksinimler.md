1. **Sepete Ürün Ekleme**
    - **API Metodu:** `POST /cart/items`
    - **Açıklama:** Kullanıcıların seçtikleri ürünleri alışveriş sepetine eklemesini sağlar. Kullanıcılar satın almak istedikleri ürünleri sepete ekleyebilir.

2. **Sepetten Ürün Çıkarma**
    - **API Metodu:** `DELETE /cart/items/{itemId}`
    - **Açıklama:** Kullanıcının alışveriş sepetinde bulunan bir ürünü kaldırmasını sağlar.

3. **Sepeti Görüntüleme**
    - **API Metodu:** `GET /cart`
    - **Açıklama:** Kullanıcının alışveriş sepetinde bulunan tüm ürünleri görüntülemesini sağlar. Sepetteki ürünlerin toplam fiyatı da kullanıcıya gösterilebilir.

4. **Sipariş Oluşturma**
    - **API Metodu:** `POST /orders`
    - **Açıklama:** Kullanıcıların alışveriş sepetindeki ürünleri satın alarak sipariş oluşturmasını sağlar. Sipariş oluşturulduktan sonra ödeme ve teslimat işlemleri başlatılır.

5. **Sipariş İptal Etme**
    - **API Metodu:** `DELETE /orders/{orderId}`
    - **Açıklama:** Kullanıcının daha önce oluşturduğu bir siparişi iptal etmesini sağlar. Sipariş henüz gönderilmemişse iptal işlemi gerçekleştirilebilir.

6. **Teslimat Adresi Ekleme**
    - **API Metodu:** `POST /addresses`
    - **Açıklama:** Kullanıcıların siparişlerinde kullanmak üzere yeni bir teslimat adresi eklemesini sağlar.

7. **Teslimat Adresi Güncelleme**
    - **API Metodu:** `PUT /addresses/{addressId}`
    - **Açıklama:** Kullanıcının daha önce eklediği teslimat adresini güncellemesini sağlar.
