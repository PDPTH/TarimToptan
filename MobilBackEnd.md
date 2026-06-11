# Mobil Back-End

Bu bölüm TarımToptan mobil uygulamasının REST API bağlantı görevlerini içerir. Mobil uygulama `android-java/` klasöründe Android Studio + Java ile geliştirilmiştir ve mevcut Node.js/Express API'ye bağlanır.

## REST API Adresi

- Canlı Render API: `https://tarimtoptan-api.onrender.com/v1`
- Yerel Android emülatör testi: `http://10.0.2.2:3000/v1`
- Yerel bilgisayar tarayıcısı testi: `http://localhost:3000/v1`

Mobil uygulama varsayılan olarak canlı Render API adresine bağlanacak şekilde ayarlanmıştır. Yerel backend ile test yapılacaksa `android-java/app/src/main/java/com/tarimtoptan/android/net/ApiClient.java` dosyasındaki `API_BASE_URL` değeri geçici olarak yerel adrese çevrilebilir.

## Grup Üyelerinin Mobil Back-End Görevleri

1. [Süleyman Buğra Çetin Mobil Back-End Görevleri](Suleyman-Bugra-Cetin/Suleyman-Mobil-Backend-Gorevleri.md)
2. [Umut Can Bayar Mobil Back-End Görevleri](umut-can-bayar/umut-can-bayar-Mobil-Backend-Gorevleri.md)
3. [Aykhan Bayramov Mobil Back-End Görevleri](aykhan-bayramov/Aykhan-Bayramov-Mobil-Backend-Gorevleri.md)

## API Bağlantı Yapısı

| Alan | Java Dosyası | API Kapsamı |
| --- | --- | --- |
| Hesap | `android-java/app/src/main/java/com/tarimtoptan/android/features/account/AccountFeature.java` | Auth, kullanıcı, üretici |
| Ürünler | `android-java/app/src/main/java/com/tarimtoptan/android/features/products/ProductsFeature.java` | Ürün, stok, yorum |
| Sepet ve sipariş | `android-java/app/src/main/java/com/tarimtoptan/android/features/checkout/CheckoutFeature.java` | Sepet, sipariş, adres |

## Kanıt Videosu Notu

Her üye kendi sayfasına mobil back-end kanıt videosu eklemelidir. Videoda mobil uygulamadan REST API isteğinin gönderildiği ve işlemin gerçekleştiği net olarak gösterilmelidir.
