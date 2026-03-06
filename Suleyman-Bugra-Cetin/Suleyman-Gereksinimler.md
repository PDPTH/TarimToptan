Kayıt Olma

API Metodu: POST /auth/register

Açıklama: Kullanıcıların sisteme yeni bir hesap oluşturarak kayıt olmasını sağlar. Kullanıcılar email adresi ve şifre belirleyerek hesap oluşturabilir. Kayıt işlemi sırasında gerekli kişisel bilgiler sisteme kaydedilir ve kullanıcı hesabı oluşturulur.

Giriş Yapma

API Metodu: POST /auth/login

Açıklama: Kullanıcıların mevcut hesap bilgileri ile sisteme giriş yapmasını sağlar. Kullanıcı email ve şifre bilgilerini girerek kimlik doğrulaması yapar. Doğrulama başarılı olduğunda kullanıcı sisteme giriş yapabilir ve yetkili olduğu işlemleri gerçekleştirebilir.

Profil Bilgilerini Güncelleme

API Metodu: PUT /users/{userId}

Açıklama: Kullanıcıların kendi profil bilgilerini güncelleyebilmesini sağlar. Kullanıcılar ad, soyad, email ve telefon gibi kişisel bilgilerini değiştirebilir. Güvenlik amacıyla kullanıcıların sisteme giriş yapmış olması gerekir.

Hesabı Silme

API Metodu: DELETE /users/{userId}

Açıklama: Kullanıcının hesabını sistemden kalıcı olarak silmesini sağlar. Bu işlem geri alınamaz ve kullanıcının sistemdeki tüm verileri kaldırılır. Güvenlik için kullanıcı giriş yapmış olmalıdır.

Üreticileri Listeleme

API Metodu: GET /producers

Açıklama: Sistemde kayıtlı olan üreticilerin listesini görüntülemeyi sağlar. Kullanıcılar üreticileri inceleyebilir ve üreticiler hakkında temel bilgilere erişebilir.

Üretici Detayını Görüntüleme

API Metodu: GET /producers/{producerId}

Açıklama: Belirli bir üreticinin detaylı bilgilerini görüntülemeyi sağlar. Kullanıcılar üreticinin adı, iletişim bilgileri ve sunduğu ürünler gibi detaylara erişebilir.

Şifre Sıfırlama Talebi

API Metodu: POST /auth/reset-password-request

Açıklama: Kullanıcıların şifrelerini unuttuklarında yeni bir şifre oluşturma talebi göndermelerini sağlar. Kullanıcı email adresini girerek şifre sıfırlama bağlantısı talep eder.
