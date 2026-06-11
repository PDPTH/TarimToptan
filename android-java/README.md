# TarimToptan Mobil

TarimToptan mobil uygulamasi, web sitesindeki koyu yesil alisveris tasarimina benzer sekilde hazirlanmistir. Uygulama acildiginda ana sayfa, logo, urun kesfetme butonu, uye olma butonu ve istatistik kartlari gorunur.

## Ekranlar

- Ana Sayfa: tanitim, kesfetme ve uye olma alani
- Urunler: ornek urun kartlari, filtreleme alani ve urun yonetimi
- Ureticiler: uretici listesi ve detay goruntuleme
- Sepet: sepete ekleme, sepet goruntuleme ve urun cikarma
- Siparislerim: adres ve siparis islemleri
- Hesabim: kayit, giris, profil ve sifre islemleri

## Calistirma

Uygulama varsayilan olarak Render uzerindeki canli API'ye baglanir:

```txt
https://tarimtoptan-api.onrender.com/v1
```

Bu adres `android-java/app/src/main/java/com/tarimtoptan/android/net/ApiClient.java` dosyasindaki `API_BASE_URL` sabitinde tanimlidir.

1. Android Studio'da bu klasoru ac:

```txt
android-java
```

2. Emulatorden veya gercek Android cihazdan uygulamayi calistir.

## Yerel Backend ile Test

Canli Render servisi yerine bilgisayardaki backend'i kullanmak istersen once backend klasorunde bagimliliklari kurup sunucuyu baslat:

```powershell
cd Web-Backend
npm.cmd install
npm.cmd start
```

Ardindan `ApiClient.java` icindeki `API_BASE_URL` degerini Android emulator icin su adrese cevir:

```java
public static final String API_BASE_URL = "http://10.0.2.2:3000/v1";
```

Gercek telefonda yerel backend'e baglanmak icin `10.0.2.2` yerine bilgisayarin WiFi IP adresi kullanilmalidir.
