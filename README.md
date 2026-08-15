# CarShop Backend - Dokumentacja

## Spis treści

1. [Opis](#opis)
2. [Wymagania systemowe](#wymagania-systemowe)
3. [Instalacja i uruchomienie](#instalacja-i-uruchomienie)
4. [Dostęp do API](#dostęp-do-api)
5. [Dokumentacja REST API](#dokumentacja-rest-api)
6. [Modele danych](#modele-danych)
7. [Autentykacja](#autentykacja)
8. [Zmienne środowiskowe](#zmienne-środowiskowe)

---

## Opis

**CarShop Backend** to aplikacja backendowa dla platformy e-commerce dedykowanej sprzedaży samochodów. Aplikacja obsługuje:

- 🔐 **Zarządzanie użytkownikami** - rejestracja, logowanie, weryfikacja e-mail
- 🚗 **Katalog samochodów** - tworzenie, edycja, usuwanie ogłoszeń o sprzedaży
- 📸 **Obsługa zdjęć** - upload i zarządzanie zdjęciami samochodów
- ⭐ **Oceny profili** - system oceniania sprzedawców
- ❤️ **Ulubione samochody** - dodawanie samochodów do listy ulubionych
- 🔍 **Wyszukiwanie** - sugerowanie i wyszukiwanie samochodów
- 🎨 **Metadane** - producenci, kolory, typy paliwa

Backend został zaimplementowany w technologii **Java Spring Boot** (v3.5.6) z **PostgreSQL** jako bazą danych.

---

## Wymagania systemowe

Do poprawnego uruchomienia projektu wymagane są:

- **Java** - 21 lub wyższa
- **Maven** - 3.8+
- **Docker** - 20.10+
- **Docker Compose** - 2.0+
- **Git** (opcjonalnie, do klonowania repozytorium)

### Zalecane narzędzia

- **Postman** lub **curl** - do testowania API
- **DBeaver** - do zarządzania bazą danych PostgreSQL
- **IntelliJ IDEA** - IDE dla Java

---

## Instalacja i uruchomienie

### Krok 1: Uruchomienie bazy danych

Projekt wykorzystuje **PostgreSQL** uruchamianą w kontenerze Docker za pomocą Docker Compose.

Przejdź do **głównego katalogu projektu** (gdzie znajduje się plik `docker-compose.yml`):

```bash
cd /path/to/CarShopBackend
```

Uruchom kontenery:

```bash
docker compose up
```

**Koniguracja bazy danych:**
- **Host**: localhost
- **Port**: 5432
- **Baza danych**: carshopdb
- **Użytkownik**: admin
- **Hasło**: admin123

> **⚠️ Uwaga (tryb developerski):**  
> Upewnij się, że port **5432** nie jest zajęty przez inny proces.  
> Baza danych tworzy się automatycznie podczas uruchamiania kontenerów.

### Krok 2: Uruchomienie aplikacji backendowej

Po poprawnym uruchomieniu PostgreSQL, uruchom aplikację Spring Boot:

```bash
mvn spring-boot:run
```

Alternatywnie w IDE (np. IntelliJ IDEA):
- Otwórz projekt
- Kliknij prawy przycisk na `CarShopBackendApplication.java`
- Wybierz "Run"

**Aplikacja będzie dostępna pod adresem:**
```
http://localhost:8080
```

### Krok 3: Weryfikacja działania

Sprawdź, czy aplikacja działa prawidłowo:

```bash
curl http://localhost:8080/api/v1/ping
```

Odpowiedź poinna być:
```
pong!
```

---

## Dostęp do API

### Swagger UI (dokumentacja interaktywna)

Dostęp do interaktywnej dokumentacji API przez Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON

Specyfikacja OpenAPI dostępna jest pod adresem:

```
http://localhost:8080/api-docs
```

---

## Dokumentacja REST API

### 1. Endpointy testowe

#### Ping (publiczny)
```http
GET /api/v1/ping
```
**Opis**: Prosty endpoint do testowania połączenia z API  
**Odpowiedź**: `"pong!"`  
**Autoryzacja**: Nie wymagana

#### Secured Ping (wymagana autentykacja)
```http
GET /api/secured/ping
```
**Opis**: Test autentykacji, zwraca powitanie dla zalogowanego użytkownika  
**Nagłówki**: `Authorization: Bearer {TOKEN}`  
**Odpowiedź**: `"secured_pong, hi! {username}"`  
**Autoryzacja**: Wymagana (JWT Token)

---

### 2. Autentykacja (`/api/auth`)

#### Rejestracja
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```
**Odpowiedź (201 Created)**:
```json
"User registered successfully. Verification email sent to john@example.com"
```

#### Logowanie
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```
**Odpowiedź (200 OK)**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 3600000
}
```

#### Weryfikacja e-maila
```http
POST /api/auth/verify
Content-Type: application/json

{
  "email": "john@example.com",
  "token": "123456"
}
```
**Odpowiedź (200 OK)**: `"Email confirmed!"`

#### Prośba o nowy kod weryfikacyjny
```http
POST /api/auth/reset-verify
Content-Type: application/json

{
  "email": "john@example.com"
}
```
**Odpowiedź (200 OK)**: `"New token sent"`

#### Odświeżenie tokenu dostępu
```http
POST /api/auth/refresh-token
Authorization: Bearer {REFRESH_TOKEN}
```
**Odpowiedź (200 OK)**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 3600000
}
```

#### Zmiana hasła
```http
POST /api/auth/change-password
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "oldPassword": "OldPassword123!",
  "newPassword": "NewPassword456!"
}
```
**Autoryzacja**: Wymagana

#### Zmiana e-maila
```http
POST /api/auth/change-email
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "newEmail": "newemail@example.com"
}
```
**Autoryzacja**: Wymagana

---

### 3. Samochody (`/api/v1/cars`)

#### Tworzenie samochodu (z zdjęciami)
```http
POST /api/v1/cars
Authorization: Bearer {TOKEN}
Content-Type: multipart/form-data

Form data:
- car: (JSON object)
  {
    "name": "BMW 3 Series",
    "description": "Excellent condition",
    "price": 45000,
    "productionYear": 2022,
    "mileage": 15000,
    "colorId": 1,
    "petrolId": 2,
    "carProducerId": 3
  }
- photos: (files) - opcjonalne, max 50MB na plik
```
**Odpowiedź (200 OK)**:
```json
{
  "id": 1,
  "name": "BMW 3 Series",
  "description": "Excellent condition",
  "price": 45000,
  "productionYear": 2022,
  "mileage": 15000,
  "color": { "id": 1, "name": "Black" },
  "petrol": { "id": 2, "name": "Diesel" },
  "carProducer": { "id": 3, "name": "BMW" },
  "photos": [
    {
      "id": 101,
      "url": "https://storage.example.com/photo1.jpg"
    }
  ],
  "profile": { ... }
}
```
**Autoryzacja**: Wymagana

#### Pobranie wszystkich samochodów
```http
GET /api/v1/cars
```
**Odpowiedź (200 OK)**:
```json
[
  {
    "id": 1,
    "name": "BMW 3 Series",
    "price": 45000,
    ...
  },
  ...
]
```

#### Pobranie samochodu po ID
```http
GET /api/v1/cars/{id}
```
**Parametry**: 
- `id` (path) - ID samochodu

**Odpowiedź (200 OK)**: Pełny obiekt samochodu

#### Edycja samochodu (PATCH)
```http
PATCH /api/v1/cars/{id}
Content-Type: application/json

{
  "name": "BMW 3 Series Updated",
  "price": 44000
}
```
**Parametry**: 
- `id` (path) - ID samochodu

**Odpowiedź (200 OK)**: Zaktualizowany samochód

#### Usunięcie samochodu
```http
DELETE /api/v1/cars/{id}
```
**Parametry**: 
- `id` (path) - ID samochodu

**Odpowiedź**: 204 No Content

#### Sugestie samochodów (wyszukiwanie)
```http
GET /api/v1/cars/suggestions?query=BMW
```
**Parametry**: 
- `query` (query) - Phrase wyszukiwania (min. 2 znaki)

**Odpowiedź (200 OK)**:
```json
[
  {
    "id": 1,
    "name": "BMW 3 Series",
    "price": 45000,
    "photos": [
      {
        "id": 101,
        "url": "https://..."
      }
    ]
  },
  ...
]
```

---

### 4. Profile (`/api/v1/profiles`)

#### Pobranie profilu zalogowanego użytkownika
```http
GET /api/v1/profiles
Authorization: Bearer {TOKEN}
```
**Odpowiedź (200 OK)**:
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+48123456789",
  "city": "Warsaw",
  "rating": 4.5,
  "ratingCount": 10,
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```
**Autoryzacja**: Wymagana

#### Aktualizacja profilu
```http
PATCH /api/v1/profiles
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "phoneNumber": "+48987654321",
  "city": "Krakow"
}
```
**Odpowiedź (200 OK)**: Zaktualizowany profil  
**Autoryzacja**: Wymagana

#### Pobranie samochodów profilu
```http
GET /api/v1/profiles/{profileId}/cars
```
**Parametry**: 
- `profileId` (path) - ID profilu

**Odpowiedź (200 OK)**: Lista samochodów użytkownika

#### Ocena profilu
```http
POST /api/v1/profiles/rate/{profileId}
Authorization: Bearer {TOKEN}
Content-Type: application/json

5.0
```
**Parametry**: 
- `profileId` (path) - ID profilu do oceny
- Body: Wartość oceny (1-5)

**Odpowiedź (200 OK)**: `4.5` - Średnia ocena  
**Autoryzacja**: Wymagana

#### Pobranie oceny profilu
```http
GET /api/v1/profiles/rate/{profileId}
Authorization: Bearer {TOKEN}
```
**Parametry**: 
- `profileId` (path) - ID profilu

**Odpowiedź (200 OK)**: `4.5` - Bieżąca ocena  
**Autoryzacja**: Wymagana

#### Dodanie samochodu do ulubionych
```http
POST /api/v1/profiles/liked-cars/{carId}
Authorization: Bearer {TOKEN}
```
**Parametry**: 
- `carId` (path) - ID samochodu

**Autoryzacja**: Wymagana

#### Usunięcie samochodu z ulubionych
```http
DELETE /api/v1/profiles/liked-cars/{carId}
Authorization: Bearer {TOKEN}
```
**Parametry**: 
- `carId` (path) - ID samochodu

**Autoryzacja**: Wymagana

#### Pobranie listy ulubionych samochodów
```http
GET /api/v1/profiles/liked-cars/{profileId}
Authorization: Bearer {TOKEN}
```
**Parametry**: 
- `profileId` (path) - ID profilu

**Odpowiedź (200 OK)**: Lista ulubionych samochodów  
**Autoryzacja**: Wymagana

---

### 5. Metadane (`/api/v1/lookups`)

#### Pobranie metadanych
```http
GET /api/v1/lookups/metadata
```
**Odpowiedź (200 OK)**:
```json
{
  "producers": [
    { "id": 1, "name": "BMW" },
    { "id": 2, "name": "Audi" },
    { "id": 3, "name": "Mercedes" },
    ...
  ],
  "colors": [
    { "id": 1, "name": "Black" },
    { "id": 2, "name": "White" },
    { "id": 3, "name": "Blue" },
    ...
  ],
  "petrols": [
    { "id": 1, "name": "Petrol" },
    { "id": 2, "name": "Diesel" },
    { "id": 3, "name": "Hybrid" },
    ...
  ]
}
```

---

## Modele danych

### Car (Samochód)
```json
{
  "id": 1,
  "name": "BMW 3 Series",
  "description": "Excellent condition, full service history",
  "price": 45000,
  "productionYear": 2022,
  "mileage": 15000,
  "color": { "id": 1, "name": "Black" },
  "petrol": { "id": 2, "name": "Diesel" },
  "carProducer": { "id": 3, "name": "BMW" },
  "photos": [ { "id": 101, "url": "..." } ],
  "profile": { "id": 5, "firstName": "John", ... }
}
```

### Profile (Profil użytkownika)
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+48123456789",
  "city": "Warsaw",
  "rating": 4.5,
  "ratingCount": 10,
  "user": { "id": 1, "username": "john_doe", "email": "john@example.com" }
}
```

### User (Użytkownik)
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "password": "hashed_password",
  "enabled": true
}
```

---

## Autentykacja

### JWT Token

Aplikacja używa **JWT (JSON Web Tokens)** do autentykacji. Każdy token dostępu jest ważny przez **1 godzinę**, a token odświeżający przez **7 dni**.

### Jak używać tokenu

1. **Zarejestruj się** - `POST /api/auth/register`
2. **Zaloguj się** - `POST /api/auth/login` (otrzymaj `accessToken`)
3. **Użyj tokenu** w każdym żądaniu wymagającym autentykacji:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

### Odświeżanie tokenu

Gdy token dostępu wygaśnie, użyj token odświeżający:

```http
POST /api/auth/refresh-token
Authorization: Bearer {REFRESH_TOKEN}
```

---

## Zmienne środowiskowe

Aplikacja konfigurowana jest za pomocą pliku `application.properties`:

```properties
# Baza danych
spring.datasource.url=jdbc:postgresql://localhost:5432/carshopdb
spring.datasource.username=admin
spring.datasource.password=admin123

# JWT
jwt.secret-base64=U29tZVJhbmRvbVdvcmRzRm9ySnd0S2V5QW5kRm9yTXlQcm9qZWN0VG9kYXlJc1NhdHVyZGF5U3VuSXNTb0JyaWdodERhdGUyMDI1MTEwMUNhYmJhZ2VPcmFuZ2U
jwt.lifetime=3600000           # 1 godzina
jwt.refresh-token.lifetime=604800000  # 7 dni

# Upload plików
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# E-mail (wymagane dla weryfikacji e-mail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${SPRING_EMAIL_USERNAME}
spring.mail.password=${SPRING_EMAIL_PASSWORD}

# Frontend
car-shop-webpage-url=https://f1vus.github.io,http://localhost:5173
```

### Zmienne środowiskowe wymagane

Aby wysyłać e-mail weryfikacyjne, ustaw zmienne środowiskowe:

```bash
export SPRING_EMAIL_USERNAME=your-email@gmail.com
export SPRING_EMAIL_PASSWORD=your-app-password
```

Następnie uruchom aplikację:

Windows:
```bash
mvnw.cmd spring-boot:run
```
Linux / macOS:
```bash
./mvnw spring-boot:run
```

---

## Przykłady użycia (CURL)

### Rejestracja i logowanie

```bash
# Rejestracja
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePassword123!"
  }'

# Logowanie
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePassword123!"
  }'
```

### Tworzenie samochodu z zdjęciami

```bash
curl -X POST http://localhost:8080/api/v1/cars \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -F 'car={
    "name":"BMW 3 Series",
    "description":"Excellent condition",
    "price":45000,
    "productionYear":2022,
    "mileage":15000,
    "colorId":1,
    "petrolId":2,
    "carProducerId":3
  };type=application/json' \
  -F 'photos=@photo1.jpg' \
  -F 'photos=@photo2.jpg'
```

### Pobranie wszystkich samochodów

```bash
curl http://localhost:8080/api/v1/cars
```

### Wyszukiwanie samochodów

```bash
curl http://localhost:8080/api/v1/cars/suggestions?query=BMW
```

---

## Kody błędów HTTP

| Kod | Znaczenie |
|-----|-----------|
| 200 | OK - Żądanie pomyślne |
| 201 | Created - Zasób utworzony |
| 204 | No Content - Pomyślnie usunięto |
| 400 | Bad Request - Niepoprawne dane |
| 401 | Unauthorized - Brak lub nieprawidłowy token |
| 403 | Forbidden - Brak dostępu |
| 404 | Not Found - Zasób nie znaleziony |
| 500 | Server Error - Błąd serwera |

---

## Rozwiązywanie problemów

### Problem: Baza danych nie uruchamia się

```bash
# Usuń kontenery i volumeny, a następnie uruchom ponownie
docker compose down -v
docker compose up
```

### Problem: Port 5432 jest już zajęty

```bash
# Sprawdź, jaki proces używa portu
lsof -i :5432

# Albo zmień port w docker-compose.yml
```

### Problem: Błąd "FATAL: database "carshopdb" does not exist"

Migracje Flyway powinny się uruchomić automatycznie. Jeśli tak się nie stanie, sprawdź logi:

```bash
docker compose logs postgres
```

### Problem: Token JWT wygasł

Użyj endpoint do odświeżenia tokenu:

```bash
curl -X POST http://localhost:8080/api/auth/refresh-token \
  -H "Authorization: Bearer YOUR_REFRESH_TOKEN"
```

---

## Struktura projektu

```
CarShopBackend/
├── src/
│   ├── main/
│   │   ├── java/edu/team/carshopbackend/
│   │   │   ├── controller/          # REST Controllers
│   │   │   ├── service/             # Business logic
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── entity/              # Database entities
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── mapper/              # Entity-DTO mappers
│   │   │   ├── config/              # Configuration classes
│   │   │   ├── error/               # Error handling
│   │   │   └── client/              # External clients
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/        # Flyway migrations
│   └── test/                        # Tests
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## Kontakt i wsparcie

Jeśli napotkasz problemy lub masz pytania dotyczące tego projektu:

1. Sprawdź [Swagger UI](http://localhost:8080/swagger-ui.html)
2. Przejrzyj logi aplikacji
3. Sprawdź sekcję "Rozwiązywanie problemów"

---

## Licencja

Projekt jest częścią zadania akademickiego.
