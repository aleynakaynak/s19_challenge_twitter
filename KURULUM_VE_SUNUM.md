# Çalıştırma ve sunuma hazırlık

## Teslim durumu

30 Ekim 2026 kontrolü:
- Tunç Kıral'ın 29 Ekim Slack yanıtına göre proje hâlâ teslim edilebilir. GitHub bağlantısını istiyor; ne kadar erken gönderilirse o kadar iyi diyor. Kesin bir gün vermiyor.
- NextGen'de değerlendirme süresi 21 Kasım 2026. Bu, hocanın teyit ettiği proje teslim günü olarak yorumlanmamalı.
- NextGen → Ekstralar → Sprint 20 → Bitirme Projesi – İsterler belgesi en fazla 5 dakikalık Loom videosu ve GitHub bağlantısı istiyor. İkisi Slack üzerinden eğitmene iletilecek.
- Kariyer oturumları da sertifika şartlarında listeleniyor. Projeyi teslim etmek tek başına sertifikanın tanımlandığını göstermez.

Kaynaklar:
- https://docs.google.com/document/d/11XY_Ow0_mValO67XCr4K_NvmdLyP-T8utQ99DZYdDn0/edit
- https://github.com/Workintech/FSWEB-s19-Challenge
- Tunç hocanın mesajı: https://workintech-talk.slack.com/archives/D0CDAH5BUBE/p1793292985077939

## 1. Mac'te ilk çalıştırma

Gerekenler: JDK 21, Node.js (Vite 5 ile uyumlu sürüm) ve yerel PostgreSQL. Maven ayrıca kurmak gerekmez; proje Maven Wrapper içerir.

Terminalde proje klasörünü aç. Önce sürümleri kontrol et:

```bash
java -version
node -v
```

PostgreSQL çalışırken pgAdmin veya psql üzerinden bir kez:

```sql
CREATE DATABASE twitter;
```

Backend terminalinde, kendi PostgreSQL kullanıcı adını ve şifreni kullan:

```bash
export DB_USERNAME=postgres
read -s DB_PASSWORD
export DB_PASSWORD
chmod +x mvnw
./mvnw test
./mvnw spring-boot:run
```

`read -s DB_PASSWORD` komutundan sonra şifreyi yazıp Enter'a bas; ekranda görünmemesi normal. Şifreyi kod veya GitHub'a yazma. Şifre istenmiyorsa kurulumuna uygun bağlantı ayarlarını kullan. Farklı port/veritabanı varsa ayrıca `DB_URL` ortam değişkenini tanımla.

Başarılı başlangıçta API http://localhost:3000 adresinde çalışır. İkinci terminal:

```bash
cd twitter-frontend
npm ci
npm run dev
```

Tarayıcıda http://localhost:3200 aç. Backend terminalini kapatma.

## 2. Ekranda denenecekler

1. Kayıt ol: en az 3 karakter kullanıcı adı, geçerli e-posta, en az 6 karakter şifre.
2. Giriş yap. Ekranda kullanıcı adın ve ID'nin görünmesini bekle.
3. Bir tweet paylaş. Kendi tweet listen otomatik yenilenmeli.
4. Beğen butonuna bas. Beğeni sayısı artmalı. Aynı tweete ikinci kez basarsan açıklayıcı hata görünmeli.
5. Başka kullanıcıyı kayıt edip giriş yaparak ilk kullanıcının ID'sini arat. Onun tweetini beğen.
6. Çıkış yap. Beğenme düğmeleri pasif olmalı. Sayfayı yenileyince yeniden giriş gerekir.
7. Postman koleksiyonuyla yorum, dislike, retweet, güncelleme ve silmeyi dene. POST /login sonrasında diğer yazma isteklerine Basic Auth kullanıcı adı ve şifresi eklenmeli; token dönmüyor.
8. Başka kullanıcının tweetini silmeyi dene: 403 beklenir. Tweeti sahibiyle sil: 204 beklenir.

## 3. Anlaman gereken kod akışı

Tweet paylaşma örneği:

1. `App.jsx`: form gönderilince `createTweet(content)` çağrılır.
2. `api.js`: `/tweet` adresine JSON ve Basic Auth başlığı gönderir. Kimlik bilgileri localStorage'a yazılmaz; yalnızca sekme belleğinde tutulur.
3. `SecurityConfig`: kimlik doğrulamayı zorunlu tutar. `UserServiceImpl` kullanıcıyı veritabanından yükler; BCrypt hash'i karşılaştırılır.
4. `TweetController`: JSON'u `TweetRequest` olarak alır, `@Valid` ile doğrular; oturumdaki kullanıcı adını servise aktarır.
5. `TweetServiceImpl`: kullanıcıyı bulur, yeni tweeti o kullanıcıya bağlar ve repository'ye kaydettirir.
6. `TweetRepository`: JPA üzerinden SQL işlemini yapar.
7. `DtoConverter`: entity'yi güvenli cevap nesnesine dönüştürür. Şifreyi ve döngüsel entity ilişkilerini JSON'a açmaz.
8. Ön yüz tekrar `/tweet/findByUserId` çağırarak güncel listeyi gösterir.

Tablo ilişkileri:
- User → Tweet: bir kullanıcının birden fazla tweeti olabilir.
- Tweet → Comment / Likes / Retweet: her tweetin birden fazla ilişkili kaydı olabilir.
- Comment / Likes / Retweet → User ve Tweet: her kayıt bir kullanıcıya ve bir tweete bağlıdır.
- Like ve Retweet tablolarında `(user_id, tweet_id)` benzersizdir.
- Tweet silinince ilişkili yorum, beğeni ve retweetler de silinir.

## 4. Beş dakikalık sunum akışı

Ezberlenmiş bir metin okumak yerine aşağıdaki ekranları kendi cümlelerinle anlat.

**0:00–1:00 — Çalışan uygulama**
Kayıt/giriş, tweet paylaşma, listeleme ve beğenmeyi göster. Tarayıcının Network sekmesinden bir isteği aç.

**1:00–2:00 — Mimari ve veritabanı**
Entity ilişkilerini ve Controller → Service → Repository akışını göster. Tweet sahibini neden istek gövdesinden almadığını açıkla.

**2:00–3:00 — Security, validation, hata yönetimi**
Basic Auth, BCrypt hash'i, sahiplik kontrolü ve `GlobalExceptionHandler` örneğini göster. 401 ile 403 farkını açıkla. BCrypt geri çözülen bir şifreleme değildir; hash işlemidir.

**3:00–4:10 — Test ve CORS**
`TweetServiceImplTest` içinden başka kullanıcının tweet silememesi testini aç. Mock repository ile servis kuralının gerçek veritabanına gerek olmadan sınandığını açıkla.
`SecurityConfig` içinde yalnızca localhost:3200 origin'ine izin verilen CORS ayarını göster. 3000 ve 3200 farklı origin'dir. `OPTIONS` isteğinin rolünü açıkla. CORS kimlik doğrulamasının yerine geçmez.

**4:10–4:50 — Kapanış**
Gerçekten zorlandığın kısmı söyle. Sonraki geliştirme için örneğin sayfalama veya daha kapsamlı React işlemlerini tartış. Yapmadığın şeyi yapılmış gibi anlatma.

## 5. Kendini kontrol et

Bu sorulara kendi sözlerinle cevap vermeden videoyu kaydetme:
- Controller ile Service niçin ayrı?
- Tweet sahibi hangi bilgiden belirleniyor?
- Parola veritabanında nasıl saklanıyor?
- Neden entity yerine DTO dönüyor?
- 401, 403, 404 ve 409 hangi durumlarda oluşuyor?
- Bir tweet silinince yorumlara ne oluyor?
- Unit test ile entegrasyon testinin farkı ne?
- React 3200'den API 3000'e bağlanırken CORS niçin gerekiyor?

## 6. Doğrulama kapsamı

Güncel çalışma sonucunu README'nin doğrulama bölümünden takip et. H2 PostgreSQL modu test içindir; gerçek PostgreSQL sunucusunun yerini aldığı iddia edilmez. Teslim öncesi kendi PostgreSQL'inle uygulamayı açıp yukarıdaki akışı ve Loom kaydını tamamla.
