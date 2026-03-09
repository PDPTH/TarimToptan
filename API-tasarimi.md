# API Tasarımı - OpenAPI Specification

**OpenAPI Spesifikasyon Dosyası:** [openapi.yaml](lamine.yml)

Bu doküman, OpenAPI Specification (OAS) 3.0.3 standardına göre hazırlanmış tarım ürünleri e-ticaret sistemi API tasarımını içermektedir.

## OpenAPI Specification

```yaml
openapi: 3.0.3
info:
  title: Tarım Ürünleri E-Ticaret API
  description: |
    Tarım ürünleri e-ticaret platformu için RESTful API.

    ## Özellikler
    - Kullanıcı kayıt ve giriş işlemleri
    - Profil ve hesap yönetimi
    - Üretici listeleme ve görüntüleme
    - Ürün yönetimi
    - Ürün değerlendirme sistemi
    - Sepet işlemleri
    - Sipariş işlemleri
    - Teslimat adresi yönetimi
    - JWT tabanlı kimlik doğrulama
  version: 1.0.0
  contact:
    name: API Destek Ekibi
    email: destek@tarimtoptan.com
    url: https://api.tarimtoptan.com/support
  license:
    name: MIT
    url: https://opensource.org/licenses/MIT

servers:
  - url: https://api.tarimtoptan.com/v1
    description: Production server
  - url: https://staging-api.tarimtoptan.com/v1
    description: Staging server
  - url: http://localhost:8080/v1
    description: Development server

tags:
  - name: auth
    description: Kimlik doğrulama işlemleri
  - name: users
    description: Kullanıcı profil ve hesap işlemleri
  - name: producers
    description: Üretici işlemleri
  - name: products
    description: Ürün işlemleri
  - name: reviews
    description: Ürün değerlendirme işlemleri
  - name: cart
    description: Sepet işlemleri
  - name: orders
    description: Sipariş işlemleri
  - name: addresses
    description: Teslimat adresi işlemleri

paths:
  /auth/register:
    post:
      tags:
        - auth
      summary: Kayıt olma
      description: Sisteme yeni bir kullanıcı kaydeder
      operationId: registerUser
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserRegistration'
            examples:
              example1:
                summary: Örnek kayıt isteği
                value:
                  email: kullanici@example.com
                  password: Guvenli123!
                  firstName: Ahmet
                  lastName: Yılmaz
      responses:
        '201':
          description: Kullanıcı başarıyla oluşturuldu
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/User'
        '400':
          $ref: '#/components/responses/BadRequest'
        '409':
          description: Email adresi zaten kullanımda
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Error'

  /auth/login:
    post:
      tags:
        - auth
      summary: Giriş yapma
      description: Email ve şifre ile giriş yapar, JWT token döner
      operationId: loginUser
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/LoginCredentials'
            examples:
              example1:
                value:
                  email: kullanici@example.com
                  password: Guvenli123!
      responses:
        '200':
          description: Giriş başarılı
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/AuthToken'
        '401':
          $ref: '#/components/responses/Unauthorized'

  /auth/reset-password-request:
    post:
      tags:
        - auth
      summary: Şifre sıfırlama talebi
      description: Kullanıcının şifre sıfırlama bağlantısı istemesini sağlar
      operationId: requestPasswordReset
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PasswordResetRequest'
            examples:
              example1:
                value:
                  email: kullanici@example.com
      responses:
        '200':
          description: Şifre sıfırlama bağlantısı gönderildi
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/MessageResponse'
        '404':
          $ref: '#/components/responses/NotFound'

  /users/{userId}:
    put:
      tags:
        - users
      summary: Profil bilgilerini güncelleme
      description: Giriş yapmış kullanıcının kendi profil bilgilerini günceller
      operationId: updateUserProfile
      security:
        - bearerAuth: []
      parameters:
        - $ref: '#/components/parameters/UserIdParam'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserUpdate'
            examples:
              example1:
                value:
                  firstName: Ahmet
                  lastName: Yılmaz
                  email: yeniemail@example.com
                  phone: "+905551234567"
      responses:
        '200':
          description: Profil başarıyla güncellendi
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/User'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '404':
          $ref: '#/components/responses/NotFound'

    delete:
      tags:
        - users
      summary: Hesabı silme
      description: Kullanıcının hesabını sistemden siler
      operationId: deleteUserAccount
      security:
        - bearerAuth: []
      parameters:
        - $ref: '#/components/parameters/UserIdParam'
      responses:
        '204':
          description: Hesap başarıyla silindi
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
        '404':
          $ref: '#/components/responses/NotFound'

  /producers:
    get:
      tags:
        - producers
      summary: Üreticileri listeleme
      description: Sistemdeki üreticileri sayfalı olarak listeler
      operationId: listProducers
      parameters:
        - $ref: '#/components/parameters/PageParam'
        - $ref: '#/components/parameters/LimitParam'
      responses:
        '200':
          description: Üretici listesi başarıyla getirildi
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProducerList'

  /producers/{producerId}:
    get:
      tags:
        - producers
      summary: Üretici detayını görüntüleme
      description: Belirli bir üreticinin detay bilgilerini getirir
      operationId: getProducerById
      parameters:
        - $ref: '#/components/parameters/ProducerIdParam'
      responses:
        '200':
          description: Üretici bilgileri başarıyla getirildi
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Producer'
        '404':
          $ref: '#/components/responses/NotFound'

  /products:
    get:
      tags:
        - products
      summary: Ürünleri listeleme
      description: Sistemdeki tüm ürünleri listeler
      operationId: listProducts
      parameters:
        - $ref: '#/components/parameters/PageParam'
        - $ref: '#/components/parameters/LimitParam'
        - name: producerId
          in: query
          description: Üreticiye göre filtreleme
          schema:
            type: string
        - name: minPrice
          in: query
          description: Minimum fiyat
          schema:
            type: number
            format: float
        - name: maxPrice
          in: query
          description: Maksimum fiyat
          schema:
            type: number
            format: float
      responses:
        '200':
          description: Ürün listesi başarıyla getirildi
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProductList'

    post:
      tags:
        - products
      summary: Ürün ekleme
      description: Sisteme yeni bir ürün ekler
      operationId: createProduct
      security:
        - bearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProductCreate'
            examples:
              example1:
                value:
                  name: Organik Domates
                  description: Doğal yöntemlerle yetiştirilmiş taze domates
                  price: 79.90
                  stock: 150
                  category: Sebze
                  producerId: "123e4567-e89b-12d3-a456-426614174111"
      responses:
        '201':
          description: Ürün başarıyla oluşturuldu
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Product'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'

  /products/{productId}:
    get:
      tags:
        - products
      summary: Ürün detayını görüntüleme
      description: Belirli bir ürünün detay bilgilerini getirir
      operationId: getProductById
      parameters:
        - $ref: '#/components/parameters/ProductIdParam'
      responses:
        '200':
          description: Ürün detayları başarıyla getirildi
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Product'
        '404':
          $ref: '#/components/responses/NotFound'

    put:
      tags:
        - products
      summary: Ürün bilgilerini güncelleme
      description: Mevcut bir ürünün bilgilerini günceller
      operationId: updateProduct
      security:
        - bearerAuth: []
      parameters:
        - $ref: '#/components/parameters/ProductIdParam'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProductUpdate'
      responses:
        '200':
          description: Ürün başarıyla güncellendi
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Product'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '404':
          $ref: '#/components/responses/NotFound'

    delete:
      tags:
        - products
      summary: Ürün yayından kaldırma
      description: Ürünü sistemden kaldırır veya pasif duruma getirir
      operationId: deleteProduct
      security:
        - bearerAuth: []
      parameters:
        - $ref: '#/components/parameters/ProductIdParam'
      responses:
        '204':
          description: Ürün başarıyla yayından kaldırıldı
        '401':
          $ref: '#/components/responses/Unauthorized'
        '404':
          $ref: '#/components/responses/NotFound'

  /products/{productId}/stock:
    patch:
      tags:
        - products
      summary: Ürün stok miktarını güncelleme
      description: Ürünün stok bilgisini günceller
      operationId: updateProductStock
      security:
        - bearerAuth: []
      parameters:
        - $ref: '#/components/parameters/ProductIdParam'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/StockUpdate'
            examples:
              example1:
                value:
                  stock: 200
      responses:
        '200':
          description: Ürün stoku başarıyla güncellendi
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Product'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'

  /products/{productId}/reviews:
    post:
      tags:
        - reviews
      summary: Ürün değerlendirmesi yapma
      description: Kullanıcının bir ürüne puan ve yorum eklemesini sağlar
      operationId: createReview
      security:
        - bearerAuth: []
      parameters:
        - $ref: '#/components/parameters/ProductIdParam'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ReviewCreate'
            examples:
              example1:
                value:
                  rating: 5
                  comment: Ürün çok taze ve kaliteli geldi.
      responses:
        '201':
          description: Değerlendirme başarıyla oluşturuldu
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Review'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'

  /cart:
    get:
      tags:
        - cart
      summary: Sepeti görüntüleme
      description: Kullanıcının aktif sepetini getirir
      operationId: getCart
      security:
        - bearerAuth: []
      responses:
        '200':
          description: Sepet başarıyla getirildi
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Cart'
        '401':
          $ref: '#/components/responses/Unauthorized'

  /cart/items:
    post:
      tags:
        - cart
      summary: Sepete ürün ekleme
      description: Kullanıcının sepetine ürün ekler
      operationId: addItemToCart
      security:
        - bearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CartItemCreate'
            examples:
              example1:
                value:
                  productId: "987e6543-e21b-12d3-a456-426614174000"
                  quantity: 2
      responses:
        '201':
          description: Ürün sepete başarıyla eklendi
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Cart'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'

  /cart/items/{itemId}:
    delete:
      tags:
        - cart
      summary: Sepetten ürün çıkarma
      description: Sepetteki bir ürünü kaldırır
      operationId: removeItemFromCart
      security:
        - bearerAuth: []
      parameters:
        - $ref: '#/components/parameters/CartItemIdParam'
      responses:
        '204':
          description: Ürün sepetten başarıyla çıkarıldı
        '401':
          $ref: '#/components/responses/Unauthorized'
        '404':
          $ref: '#/components/responses/NotFound'

  /orders:
    post:
      tags:
        - orders
      summary: Sipariş oluşturma
      description: Sepetteki ürünlerden yeni sipariş oluşturur
      operationId: createOrder
      security:
        - bearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/OrderCreate'
      responses:
        '201':
          description: Sipariş başarıyla oluşturuldu
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Order'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'

  /orders/{orderId}/cancel:
    patch:
      tags:
        - orders
      summary: Sipariş iptal etme
      description: Var olan siparişi iptal eder
      operationId: cancelOrder
      security:
        - bearerAuth: []
      parameters:
        - $ref: '#/components/parameters/OrderIdParam'
      responses:
        '200':
          description: Sipariş başarıyla iptal edildi
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Order'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '404':
          $ref: '#/components/responses/NotFound'

  /addresses:
    post:
      tags:
        - addresses
      summary: Teslimat adresi ekleme
      description: Kullanıcıya yeni bir teslimat adresi ekler
      operationId: createAddress
      security:
        - bearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/AddressCreate'
      responses:
        '201':
          description: Adres başarıyla oluşturuldu
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Address'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'

  /addresses/{addressId}:
    put:
      tags:
        - addresses
      summary: Teslimat adresi güncelleme
      description: Var olan teslimat adresini günceller
      operationId: updateAddress
      security:
        - bearerAuth: []
      parameters:
        - $ref: '#/components/parameters/AddressIdParam'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/AddressUpdate'
      responses:
        '200':
          description: Adres başarıyla güncellendi
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Address'
        '400':
          $ref: '#/components/responses/BadRequest'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '404':
          $ref: '#/components/responses/NotFound'

components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
      description: JWT token ile kimlik doğrulama

  parameters:
    UserIdParam:
      name: userId
      in: path
      required: true
      description: Kullanıcı ID'si
      schema:
        type: string
        format: uuid

    ProducerIdParam:
      name: producerId
      in: path
      required: true
      description: Üretici ID'si
      schema:
        type: string
        format: uuid

    ProductIdParam:
      name: productId
      in: path
      required: true
      description: Ürün ID'si
      schema:
        type: string
        format: uuid

    OrderIdParam:
      name: orderId
      in: path
      required: true
      description: Sipariş ID'si
      schema:
        type: string
        format: uuid

    AddressIdParam:
      name: addressId
      in: path
      required: true
      description: Adres ID'si
      schema:
        type: string
        format: uuid

    CartItemIdParam:
      name: itemId
      in: path
      required: true
      description: Sepet öğesi ID'si
      schema:
        type: string
        format: uuid

    PageParam:
      name: page
      in: query
      description: Sayfa numarası
      schema:
        type: integer
        minimum: 1
        default: 1

    LimitParam:
      name: limit
      in: query
      description: Sayfa başına kayıt sayısı
      schema:
        type: integer
        minimum: 1
        maximum: 100
        default: 20

  schemas:
    User:
      type: object
      required:
        - id
        - email
        - firstName
        - lastName
        - createdAt
      properties:
        id:
          type: string
          format: uuid
          example: "123e4567-e89b-12d3-a456-426614174000"
        email:
          type: string
          format: email
          example: "kullanici@example.com"
        firstName:
          type: string
          example: "Ahmet"
        lastName:
          type: string
          example: "Yılmaz"
        phone:
          type: string
          example: "+905551234567"
        createdAt:
          type: string
          format: date-time
          example: "2026-03-09T10:30:00Z"
        updatedAt:
          type: string
          format: date-time
          example: "2026-03-09T12:00:00Z"

    UserRegistration:
      type: object
      required:
        - email
        - password
        - firstName
        - lastName
      properties:
        email:
          type: string
          format: email
          example: "kullanici@example.com"
        password:
          type: string
          format: password
          minLength: 8
          example: "Guvenli123!"
        firstName:
          type: string
          minLength: 2
          example: "Ahmet"
        lastName:
          type: string
          minLength: 2
          example: "Yılmaz"

    UserUpdate:
      type: object
      properties:
        firstName:
          type: string
          minLength: 2
          example: "Ahmet"
        lastName:
          type: string
          minLength: 2
          example: "Yılmaz"
        email:
          type: string
          format: email
          example: "yeniemail@example.com"
        phone:
          type: string
          example: "+905551234567"

    LoginCredentials:
      type: object
      required:
        - email
        - password
      properties:
        email:
          type: string
          format: email
          example: "kullanici@example.com"
        password:
          type: string
          format: password
          example: "Guvenli123!"

    PasswordResetRequest:
      type: object
      required:
        - email
      properties:
        email:
          type: string
          format: email
          example: "kullanici@example.com"

    AuthToken:
      type: object
      required:
        - token
        - expiresIn
        - user
      properties:
        token:
          type: string
          example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        expiresIn:
          type: integer
          example: 3600
        user:
          $ref: '#/components/schemas/User'

    Producer:
      type: object
      required:
        - id
        - name
      properties:
        id:
          type: string
          format: uuid
          example: "123e4567-e89b-12d3-a456-426614174111"
        name:
          type: string
          example: "Ege Tarım Kooperatifi"
        email:
          type: string
          format: email
          example: "iletisim@egetarim.com"
        phone:
          type: string
          example: "+902321112233"
        address:
          type: string
          example: "İzmir, Türkiye"

    ProducerList:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/Producer'
        pagination:
          $ref: '#/components/schemas/Pagination'

    Product:
      type: object
      required:
        - id
        - name
        - price
        - stock
        - category
        - producerId
      properties:
        id:
          type: string
          format: uuid
          example: "987e6543-e21b-12d3-a456-426614174000"
        name:
          type: string
          example: "Organik Domates"
        description:
          type: string
          example: "Doğal yöntemlerle yetiştirilmiş taze domates"
        price:
          type: number
          format: float
          example: 79.90
        stock:
          type: integer
          example: 150
        category:
          type: string
          example: "Sebze"
        producerId:
          type: string
          format: uuid
          example: "123e4567-e89b-12d3-a456-426614174111"
        createdAt:
          type: string
          format: date-time
        updatedAt:
          type: string
          format: date-time

    ProductCreate:
      type: object
      required:
        - name
        - description
        - price
        - stock
        - category
        - producerId
      properties:
        name:
          type: string
          minLength: 3
          example: "Organik Domates"
        description:
          type: string
          example: "Doğal yöntemlerle yetiştirilmiş taze domates"
        price:
          type: number
          format: float
          minimum: 0
          example: 79.90
        stock:
          type: integer
          minimum: 0
          example: 150
        category:
          type: string
          example: "Sebze"
        producerId:
          type: string
          format: uuid
          example: "123e4567-e89b-12d3-a456-426614174111"

    ProductUpdate:
      type: object
      properties:
        name:
          type: string
          example: "Organik Domates XL"
        description:
          type: string
          example: "Daha büyük boy organik domates"
        price:
          type: number
          format: float
          example: 89.90
        category:
          type: string
          example: "Sebze"

    StockUpdate:
      type: object
      required:
        - stock
      properties:
        stock:
          type: integer
          minimum: 0
          example: 200

    Review:
      type: object
      required:
        - id
        - productId
        - rating
        - comment
      properties:
        id:
          type: string
          format: uuid
        productId:
          type: string
          format: uuid
        rating:
          type: integer
          minimum: 1
          maximum: 5
          example: 5
        comment:
          type: string
          example: "Ürün çok taze ve kaliteli geldi."
        createdAt:
          type: string
          format: date-time

    ReviewCreate:
      type: object
      required:
        - rating
        - comment
      properties:
        rating:
          type: integer
          minimum: 1
          maximum: 5
          example: 5
        comment:
          type: string
          example: "Ürün çok taze ve kaliteli geldi."

    Cart:
      type: object
      properties:
        userId:
          type: string
          format: uuid
        items:
          type: array
          items:
            $ref: '#/components/schemas/CartItem'
        totalAmount:
          type: number
          format: float
          example: 159.80

    CartItem:
      type: object
      properties:
        id:
          type: string
          format: uuid
        productId:
          type: string
          format: uuid
        productName:
          type: string
          example: "Organik Domates"
        quantity:
          type: integer
          example: 2
        unitPrice:
          type: number
          format: float
          example: 79.90
        totalPrice:
          type: number
          format: float
          example: 159.80

    CartItemCreate:
      type: object
      required:
        - productId
        - quantity
      properties:
        productId:
          type: string
          format: uuid
          example: "987e6543-e21b-12d3-a456-426614174000"
        quantity:
          type: integer
          minimum: 1
          example: 2

    Order:
      type: object
      required:
        - id
        - items
        - totalAmount
        - status
      properties:
        id:
          type: string
          format: uuid
        items:
          type: array
          items:
            $ref: '#/components/schemas/OrderItem'
        totalAmount:
          type: number
          format: float
          example: 159.80
        status:
          type: string
          enum: [pending, confirmed, preparing, shipped, delivered, cancelled]
          example: pending
        addressId:
          type: string
          format: uuid
        createdAt:
          type: string
          format: date-time

    OrderItem:
      type: object
      properties:
        productId:
          type: string
          format: uuid
        productName:
          type: string
          example: "Organik Domates"
        quantity:
          type: integer
          example: 2
        unitPrice:
          type: number
          format: float
          example: 79.90

    OrderCreate:
      type: object
      required:
        - addressId
      properties:
        addressId:
          type: string
          format: uuid
          example: "123e4567-e89b-12d3-a456-426614174222"
        note:
          type: string
          example: "Kapıya bırakabilirsiniz."

    Address:
      type: object
      required:
        - id
        - title
        - fullName
        - phone
        - city
        - district
        - addressLine
        - postalCode
      properties:
        id:
          type: string
          format: uuid
        title:
          type: string
          example: "Ev"
        fullName:
          type: string
          example: "Ahmet Yılmaz"
        phone:
          type: string
          example: "+905551234567"
        city:
          type: string
          example: "İstanbul"
        district:
          type: string
          example: "Kadıköy"
        addressLine:
          type: string
          example: "Örnek Mah. Örnek Sok. No:12 Daire:4"
        postalCode:
          type: string
          example: "34710"

    AddressCreate:
      type: object
      required:
        - title
        - fullName
        - phone
        - city
        - district
        - addressLine
        - postalCode
      properties:
        title:
          type: string
          example: "Ev"
        fullName:
          type: string
          example: "Ahmet Yılmaz"
        phone:
          type: string
          example: "+905551234567"
        city:
          type: string
          example: "İstanbul"
        district:
          type: string
          example: "Kadıköy"
        addressLine:
          type: string
          example: "Örnek Mah. Örnek Sok. No:12 Daire:4"
        postalCode:
          type: string
          example: "34710"

    AddressUpdate:
      type: object
      properties:
        title:
          type: string
          example: "İş"
        fullName:
          type: string
          example: "Ahmet Yılmaz"
        phone:
          type: string
          example: "+905551234567"
        city:
          type: string
          example: "İstanbul"
        district:
          type: string
          example: "Beşiktaş"
        addressLine:
          type: string
          example: "Barbaros Bulvarı No:45"
        postalCode:
          type: string
          example: "34353"

    ProductList:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/Product'
        pagination:
          $ref: '#/components/schemas/Pagination'

    Pagination:
      type: object
      properties:
        page:
          type: integer
          example: 1
        limit:
          type: integer
          example: 20
        totalPages:
          type: integer
          example: 5
        totalItems:
          type: integer
          example: 100

    MessageResponse:
      type: object
      properties:
        message:
          type: string
          example: "İşlem başarılı"

    Error:
      type: object
      required:
        - code
        - message
      properties:
        code:
          type: string
          example: "VALIDATION_ERROR"
        message:
          type: string
          example: "İstek verileri geçersiz"
        details:
          type: array
          items:
            type: object
            properties:
              field:
                type: string
              message:
                type: string

  responses:
    BadRequest:
      description: Geçersiz istek
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'
          example:
            code: "BAD_REQUEST"
            message: "İstek parametreleri geçersiz"

    Unauthorized:
      description: Yetkisiz erişim
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'
          example:
            code: "UNAUTHORIZED"
            message: "Kimlik doğrulama başarısız"

    NotFound:
      description: Kaynak bulunamadı
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'
          example:
            code: "NOT_FOUND"
            message: "İstenen kaynak bulunamadı"

    Forbidden:
      description: Erişim reddedildi
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'
          example:
            code: "FORBIDDEN"
            message: "Bu işlem için yetkiniz bulunmamaktadır"
