# Render Deploy Notu

Bu dosya `Web-Backend` klasorundeki Node.js/Express API'yi Render uzerinde calistirmak icindir.

## Render Ayarlari

Render Dashboard'da `New > Web Service` secilir ve GitHub reposu baglanir.

Manuel kurulumda su degerleri kullan:

- Language / Runtime: `Node`
- Root Directory: `Web-Backend`
- Build Command: `npm install`
- Start Command: `npm start`
- Environment Variable: `NODE_ENV=production`

Repo kokunde bulunan `render.yaml` dosyasi da ayni ayarlari Blueprint olarak tanimlar.

## Kontrol

Deploy bittikten sonra canli API su adresten test edilir:

```text
https://tarimtoptan-api.onrender.com/v1
```

Basarili olursa API endpoint listesini JSON olarak doner.

## Mobil Uygulama API Adresi

Android tarafinda canli API adresi su dosyada tanimlidir:

```text
android-java/app/src/main/java/com/tarimtoptan/android/net/ApiClient.java
```

Kullanilan adres:

```java
public static final String API_BASE_URL = "https://tarimtoptan-api.onrender.com/v1";
```

## Not

Bu backend su anda JSON dosyalariyla calisir. Render uzerinde demo ve kanit videosu icin uygundur; ancak kalici veritabanina gecilmezse servis yeniden deploy/restart oldugunda test verileri sifirlanabilir.
