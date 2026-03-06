**1.Ürün Ekleme**

  API Metodu: POST /products
  Açıklama: Üreticilerin veya yetkili kullanıcıların sisteme yeni bir ürün eklemesini sağlar. Ürün adı, açıklama, fiyat ve stok bilgileri girilerek ürün sisteme kaydedilir.

**2.Ürün Bilgilerini Güncelleme**

  API Metodu: PUT /products/{productId}
  Açıklama: Mevcut bir ürünün bilgilerinin güncellenmesini sağlar. Ürün adı, açıklama, fiyat ve diğer detaylar değiştirilebilir. Bu işlem yalnızca yetkili kullanıcılar tarafından yapılabilir.

Ürün Yayından Kaldırma

API Metodu: DELETE /products/{productId}

Açıklama: Bir ürünün sistemden kaldırılmasını veya satıştan çekilmesini sağlar. Ürün artık kullanıcılar tarafından görüntülenemez veya satın alınamaz.

Ürünleri Listeleme

API Metodu: GET /products

Açıklama: Sistemde bulunan tüm ürünlerin listesini görüntülemeyi sağlar. Kullanıcılar mevcut ürünleri inceleyebilir ve istedikleri ürünü seçebilir.

Ürün Detayını Görüntüleme

API Metodu: GET /products/{productId}

Açıklama: Seçilen bir ürünün detaylı bilgilerini görüntülemeyi sağlar. Ürün açıklaması, fiyatı, stok durumu ve diğer özellikler kullanıcıya gösterilir.

Ürün Stok Miktarını Güncelleme

API Metodu: PUT /products/{productId}/stock

Açıklama: Bir ürünün stok miktarını güncellemeyi sağlar. Bu işlem genellikle üreticiler veya yöneticiler tarafından yapılır ve ürünün mevcut stok durumu sisteme yansıtılır.

Ürün Değerlendirmesi Yapma

API Metodu: POST /products/{productId}/reviews

Açıklama: Kullanıcıların satın aldıkları ürünler hakkında değerlendirme yapmasını sağlar. Kullanıcılar puan verebilir ve yorum yazarak diğer kullanıcılara geri bildirim sağlayabilir.

