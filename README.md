# Twitter API — FSWEB-s19-Challenge

Workintech Full Stack programı **Sprint 19/20 Twitter Clone** projesi.
Spring Boot ve PostgreSQL ile yazılmış bir Twitter backend'i, yanında kayıt/giriş, tweet oluşturma, listeleme ve beğenme işlemleri için basit bir React ön yüzü var.

- Gereksinimler: [Workintech/FSWEB-s19-Challenge](https://github.com/Workintech/FSWEB-s19-Challenge)
- Başlangıç iskeleti: [swoopertr/s19_challenge_twitter](https://github.com/swoopertr/s19_challenge_twitter)

## Kullanılan teknolojiler

| Katman | Teknoloji |
|---|---|
| Backend | Java 21, Spring Boot 4 (Web MVC, Data JPA, Security, Validation), Lombok |
| Veritabanı | PostgreSQL |
| Test | JUnit 5, Mockito, AssertJ |
| Ön yüz | React 18, Vite, Axios |

## Proje yapısı

```
src/main/java/com/example/twitter_challenge
├── config/        SecurityConfig: Spring Security, BCrypt, CORS
├── controller/    Auth, Tweet, Comment, Like, Retweet controller'ları
├── dto/           İstek ve cevap record'ları (entity dışarı açılmaz)
├── entity/        User, Tweet, Comment, Likes, Retweet + EntityBase
├── exceptions/    TwitterException, ErrorResponse, GlobalExceptionHandler
├── repository/    Spring Data JPA repository'leri
├── service/       Interface + Impl (iş kuralları, sahiplik kontrolleri)
└── util/          DtoConverter
twitter-frontend/  React ön yüzü (port 3200)
```

## Veritabanı tasarımı

```
users (id, user_name UNIQUE, email UNIQUE, password[BCrypt], bio, created_at, updated_at)
  │ 1
  │ N
tweets (id, content ≤280, user_id FK NOT NULL, created_at, updated_at)
  │ 1
  ├── N comments (id, content, user_id FK, tweet_id FK)
  ├── N likes    (id, user_id FK, tweet_id FK, UNIQUE(user_id, tweet_id))
  └── N retweets (id, user_id FK, tweet_id FK, UNIQUE(user_id, tweet_id))
```

- Tweet'in `user_id` alanı zorunlu, bu yüzden anonim tweet atılamaz.
- Bir kullanıcı aynı tweeti yalnızca bir kez beğenebilir ve bir kez retweet edebilir (unique constraint).
- Bir tweet silinince ona ait yorumlar, beğeniler ve retweetler de silinir (`cascade + orphanRemoval`).

## Kurulum ve çalıştırma

### 1. Veritabanı

```sql
CREATE DATABASE twitter;
```

Bağlantı bilgileri `src/main/resources/application.properties` dosyasında. Varsayılanlar `postgres/postgres@localhost:5432/twitter`.
Farklıysa dosyayı düzenleyin ya da ortam değişkeni verin:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/twitter
export DB_USERNAME=postgres
export DB_PASSWORD=sifreniz
```

Tablolar ilk çalıştırmada otomatik oluşur (`ddl-auto=update`).

### 2. Backend (port 3000)

```bash
./mvnw spring-boot:run
```

### 3. Testler

```bash
./mvnw test
```

### 4. React ön yüzü (port 3200)

```bash
cd twitter-frontend
npm install
npm run dev
```

`http://localhost:3200` adresini açın. **Kayıt ol**, ardından **Giriş yap** adımlarını izleyin.
Tweet paylaşınca kendi listeniz; beğenince görüntülenen liste API'den yeniden çekilir.
Kullanıcı ID alanından başka bir kullanıcının tweetlerini de listeleyebilirsiniz.
Kimlik bilgileri yalnızca açık sekmenin belleğinde tutulur; sayfayı yenileyince tekrar giriş gerekir.
Ekran sade tutulmuştur; yorum, dislike, retweet ve silme/güncelleme API'de ve Postman koleksiyonunda bulunur.

Mac kurulumu ve 5 dakikalık Loom hazırlığı için [KURULUM_VE_SUNUM.md](KURULUM_VE_SUNUM.md) dosyasını okuyun.

## Güvenlik

- `POST /register` ve `POST /login` herkese açıktır. Şifreler BCrypt ile hash'lenerek saklanır.
- Tweet okuma uçları (`GET /tweet/**`) herkese açıktır.
- Diğer tüm uçlar **HTTP Basic Auth** ister. Kullanıcı adı ve şifre her istekte `Authorization` başlığında gönderilir.
- İşlemi yapan kullanıcı her zaman giriş yapmış kullanıcıdır. Gövdeye başka birinin `userId`'si yazılarak onun adına tweet atılamaz.
- Sahiplik kuralları:
  - Tweet güncelleme ve silme: yalnızca tweet sahibi
  - Yorum güncelleme: yalnızca yorum sahibi
  - Yorum silme: yorum sahibi **veya** tweet sahibi
  - Retweet geri alma: yalnızca retweeti yapan kullanıcı

## Endpointler

Controller ve servis hataları tek merkezden (`GlobalExceptionHandler`) aynı formatta döner. Security filtrelerinin kimlik doğrulama hataları controller öncesinde oluşabilir:

```json
{ "message": "Tweet bulunamadı: 5", "status": 404, "timestamp": 1793276088837 }
```

### Auth

| Metot | URL | Gövde | Cevap |
|---|---|---|---|
| POST | `/register` | `{"userName":"aleyna","email":"aleyna@mail.com","password":"123456"}` | 201 + kullanıcı |
| POST | `/login` | `{"userName":"aleyna","password":"123456"}` | 200 + kullanıcı, yanlış şifrede 401 |

### Tweet (EASY)

| Metot | URL | Açıklama |
|---|---|---|
| POST | `/tweet` | `{"content":"..."}` tweet oluşturur (giriş zorunlu) |
| GET | `/tweet/findByUserId?userId=1` | Kullanıcının tüm tweetleri, en yeni en üstte |
| GET | `/tweet/findById?id=1` | Tek tweet ve beğeni, yorum, retweet sayıları |
| PUT | `/tweet/{id}` | `{"content":"..."}` günceller (sadece sahibi) |
| DELETE | `/tweet/{id}` | Siler (sadece sahibi), 204 döner |

### Yorum ve beğeni (MEDIUM)

| Metot | URL | Gövde |
|---|---|---|
| POST | `/comment/` | `{"tweetId":1,"content":"..."}` |
| PUT | `/comment/{id}` | `{"content":"..."}` |
| DELETE | `/comment/{id}` | yok |
| POST | `/like/` | `{"tweetId":1}` |
| POST | `/dislike/` | `{"tweetId":1}`: atılmış beğeniyi geri alır |

### Retweet (HARD)

| Metot | URL | Gövde |
|---|---|---|
| POST | `/retweet/` | `{"tweetId":1}`. Aynı tweet ikinci kez retweet edilirse 409 döner |
| DELETE | `/retweet/{id}` | Retweeti geri alır |

### Örnek akış (curl)

```bash
curl -X POST localhost:3000/register -H "Content-Type: application/json" \
  -d '{"userName":"aleyna","email":"aleyna@mail.com","password":"123456"}'

curl -u aleyna:123456 -X POST localhost:3000/tweet -H "Content-Type: application/json" \
  -d '{"content":"İlk tweetim!"}'

curl "localhost:3000/tweet/findByUserId?userId=1"

curl -u aleyna:123456 -X POST localhost:3000/like/ -H "Content-Type: application/json" -d '{"tweetId":1}'
```

Hazır istekler için `postman/Twitter-API.postman_collection.json` dosyasını Postman'e import edebilirsiniz.

## CORS hatası ve çözümü

React uygulaması `http://localhost:3200` adresinde, API ise `http://localhost:3000` adresinde çalışıyor. Port farklı olduğu için tarayıcı bu iki adresi **farklı origin** sayar.
API cevabında `Access-Control-Allow-Origin` başlığı yoksa tarayıcı cevabı React koduna vermez ve konsolda şu hatayı gösterir:

```
Access to XMLHttpRequest at 'http://localhost:3000/tweet/findByUserId?userId=1' from origin
'http://localhost:3200' has been blocked by CORS policy:
No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

İstek aslında sunucuya ulaşır. Engelleyen sunucu değil, tarayıcının güvenlik kuralıdır. Bu yüzden çözüm backend tarafında yapılır: `SecurityConfig` içinde bir `CorsConfigurationSource` tanımlanıp `http.cors(...)` ile Spring Security'ye bağlanır.

```java
config.setAllowedOrigins(List.of("http://localhost:3200"));
config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
config.setAllowedHeaders(List.of("*"));
```

CORS ayarı Spring Security'ye bağlandığı için tarayıcının attığı `OPTIONS` (preflight) istekleri de giriş istenmeden cevaplanır.
Tüm origin'lere (`*`) izin vermek yerine yalnızca ön yüzün adresine izin verilir.

## Testler

`src/test` altında servis katmanı ve hata yönetimi için Mockito ile yazılmış **31 unit test** ve tam Spring context üzerinde **1 entegrasyon testi** var.

| Test sınıfı | Test edilen |
|---|---|
| `TweetServiceImplTest` | Oluşturma, listeleme, bulunamama (404), sahiplik (403) |
| `CommentServiceImplTest` | Oluşturma, güncelleme, yorum/tweet sahibinin silmesi, yabancının silememesi |
| `LikeServiceImplTest` | Beğenme, iki kez beğenme (409), dislike |
| `RetweetServiceImplTest` | Retweet, iki kez retweet (409), geri alma, başkasının retweetini silememe |
| `AuthServiceImplTest` | Şifre hash'leme, tekrar eden kullanıcı adı, login başarılı ve başarısız |
| `UserServiceImplTest` | Spring Security kullanıcı yükleme |
| `GlobalExceptionHandlerTest` | Hata formatı ve durum kodları |

`ApiIntegrationTest`, kayıt/giriş, validation, yetki, tweet/yorum/beğeni/retweet ve CORS akışını JPA ile birlikte H2'nin PostgreSQL modunda doğrular. H2 yalnızca test bağımlılığıdır; normal uygulama PostgreSQL kullanır.
Mockito, modern Java sürümlerinde test başlangıcında javaagent olarak yüklenir.

## Doğrulama — 30 Ekim 2026

- JDK 21 ile `./mvnw clean test`: **32 test, 0 başarısızlık, 0 hata**.
- React `npm ci` ve `npm run build`: başarılı.
- Chromium üzerinde gerçek Spring Boot HTTP API + H2 test veritabanı ile kayıt, hatalı/doğru giriş, tweet paylaşma, beğenme, tekrar beğenme hatası, liste yenileme, çıkış, sayfa yenileme ve 375 px mobil taşma kontrolü: başarılı.
- CORS kontrolü Security entegrasyon testinde ve localhost:3200 → localhost:3000 tarayıcı akışında geçti.
- Gerçek PostgreSQL üzerinde yerel çalıştırma ve Loom kaydı teslimden önce tamamlanmalıdır.
- Test sayısı kod kapsamı yüzdesiyle aynı şey değildir; ölçülmüş satır kapsamı iddiası yoktur.
