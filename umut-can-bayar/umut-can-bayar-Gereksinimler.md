1. **Ürün Ekleme**
   - **API Metodu:** `POST /products`
   - **Açıklama:** Yetkili kullanıcıların sisteme yeni bir ürün eklemesini sağlar. Ürün adı, açıklama, fiyat ve stok bilgileri girilerek ürün sisteme kaydedilir.

2. **Ürün Bilgilerini Güncelleme**
   - **API Metodu:** `PUT /products/{productId}`
   - **Açıklama:** Mevcut bir ürünün bilgilerinin güncellenmesini sağlar. Ürün adı, açıklama, fiyat ve diğer özellikler değiştirilebilir.

3. **Ürün Yayından Kaldırma**
    - **API Metodu:** `DELETE /products/{productId}`
    - **Açıklama:** Bir ürünün sistemden kaldırılmasını veya satıştan çekilmesini sağlar. Ürün artık kullanıcılar tarafından görüntülenemez veya satın alınamaz.

4. **Ürünleri Listeleme**
    - **API Metodu:** `GET /products`
    - **Açıklama:** Sistemde bulunan tüm ürünlerin listesini görüntülemeyi sağlar. Kullanıcılar mevcut ürünleri inceleyebilir.

5. **Ürün Detayını Görüntüleme**
    - **API Metodu:** `GET /products/{productId}`
    - **Açıklama:** Seçilen bir ürünün detaylı bilgilerini görüntülemeyi sağlar. Ürün açıklaması, fiyatı, stok durumu ve diğer özellikler kullanıcıya gösterilir.

6. **Ürün Stok Miktarını Güncelleme**
    - **API Metodu:** `PUT /products/{productId}/stock`
    - **Açıklama:** Bir ürünün stok miktarını güncellemeyi sağlar. Bu işlem genellikle üreticiler veya yöneticiler tarafından yapılır.

7. **Ürün Değerlendirmesi Yapma**
    - **API Metodu:** `POST /products/{productId}/reviews`
    - **Açıklama:** Kullanıcıların satın aldıkları ürünler hakkında değerlendirme yapmasını sağlar. Kullanıcılar puan verebilir ve yorum yazarak diğer kullanıcılara geri bildirim sağlayabilir.








