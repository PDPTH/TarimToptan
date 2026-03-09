# Suleyman'ın REST API Metotları

**API Test Videosu:** [Link buraya eklenecek](https://example.com)

## 1. Kayıt Olma
- **Endpoint:** `POST /auth/register`
- **Request Body:** 
  ```json
  {
    "email": "kullanici@example.com",
    "password": "Guvenli123!",
    "firstName": "Ahmet",
    "lastName": "Yılmaz"
  }

Response: 201 Created - Kullanıcı başarıyla oluşturuldu

2. Giriş Yapma

Endpoint: POST /auth/login

Request Body:

{
  "email": "kullanici@example.com",
  "password": "Guvenli123!"
}

Response: 200 OK - Giriş başarılı, erişim anahtarı döndürüldü

3. Profil Bilgilerini Güncelleme

Endpoint: PUT /users/{userId}

Path Parameters:

userId (string, required) - Kullanıcı ID'si

Request Body:

{
  "firstName": "Ahmet",
  "lastName": "Yılmaz",
  "email": "yeniemail@example.com",
  "phone": "+905551234567"
}

Authentication: Bearer Token gerekli

Response: 200 OK - Profil bilgileri başarıyla güncellendi

4. Hesabı Silme

Endpoint: DELETE /users/{userId}

Path Parameters:

userId (string, required) - Kullanıcı ID'si

Authentication: Bearer Token gerekli

Response: 204 No Content - Hesap başarıyla silindi

5. Üreticileri Listeleme

Endpoint: GET /producers

Query Parameters:

page (integer, optional) - Sayfa numarası

limit (integer, optional) - Sayfa başına kayıt sayısı

Response: 200 OK - Üretici listesi başarıyla getirildi

6. Üretici Detayını Görüntüleme

Endpoint: GET /producers/{producerId}

Path Parameters:

producerId (string, required) - Üretici ID'si

Response: 200 OK - Üretici bilgileri başarıyla getirildi

7. Şifre Sıfırlama Talebi

Endpoint: POST /auth/reset-password-request

Request Body:

{
  "email": "kullanici@example.com"
}

Response: 200 OK - Şifre sıfırlama bağlantısı gönderildi
