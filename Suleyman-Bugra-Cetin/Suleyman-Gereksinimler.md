1. **Kayıt Olma**
   - **API Metodu:** `POST /auth/register`
   - **Açıklama:** Kullanıcıların yeni hesaplar oluşturarak sisteme kayıt olmasını sağlar. Kişisel bilgilerin toplanmasını ve hesap oluşturma işlemlerini içerir. Kullanıcılar email adresi ve şifre belirleyerek hesap oluşturur.

2. **Giriş Yapma**
   - **API Metodu:** `POST /auth/login`
   - **Açıklama:** Kullanıcıların mevcut hesap bilgileri ile sisteme giriş yapmasını sağlar. Kullanıcılar email ve şifre bilgilerini girerek kimlik doğrulaması yapar. Doğrulama başarılı olduğunda kullanıcı sisteme erişebilir.

3. **Profil Güncelleme**
   - **API Metodu:** `PUT /users/{userId}`
   - **Açıklama:** Kullanıcının profil bilgilerini güncellemesini sağlar. Kullanıcılar ad, soyad, email ve telefon gibi kişisel bilgilerini değiştirebilir. Güvenlik için giriş yapmış olmak gerekir ve kullanıcılar yalnızca kendi bilgilerini güncelleyebilir.

4. **Hesabı Silme**
   - **API Metodu:** `DELETE /users/{userId}`
   - **Açıklama:** Kullanıcının hesabını sistemden kalıcı olarak silmesini sağlar. Bu işlem geri alınamaz ve kullanıcının sistemdeki tüm verileri silinir.

5. **Üreticileri Listeleme**
   - **API Metodu:** `GET /producers`
   - **Açıklama:** Sistemde kayıtlı olan üreticilerin listesini görüntülemeyi sağlar. Kullanıcılar mevcut üreticiler hakkında genel bilgilere erişebilir.

6. **Üretici Detayını Görüntüleme**
   - **API Metodu:** `GET /producers/{producerId}`
   - **Açıklama:** Belirli bir üreticinin detaylı bilgilerini görüntülemeyi sağlar. Kullanıcılar üreticinin adı, iletişim bilgileri ve sunduğu ürünler gibi bilgilere erişebilir.

7. **Şifre Sıfırlama Talebi**
   - **API Metodu:** `POST /auth/reset-password-request`
   - **Açıklama:** Kullanıcıların şifrelerini unuttuklarında yeni bir şifre oluşturma talebi göndermelerini sağlar. Kullanıcı email adresini girerek şifre sıfırlama bağlantısı talep eder.
