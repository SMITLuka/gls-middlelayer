# gls-middlelayer

Middleware modul za generiranje MyGLS otpremnih naljepnica (Hrvatska), pripremljen za
kasnije spajanje na Pantheon MW integraciju. Vidi prateću dokumentaciju toka i
mapiranja polja (artefakt "GLS Naljepnice — Integracija").

## Što je gotovo

- `GlsApiClient` — poziva MyGLS REST/JSON `ParcelService`: `PrintLabels`, `DeleteLabels`,
  `ModifyCOD`, `GetParcelStatuses`.
- `GlsLabelService` — middleware metode nad tim pozivima:
  - `createLabel(LabelRequest)` — kreira i odmah printa naljepnicu, vraća PDF + broj pošiljke.
  - `cancelLabel(parcelId)` — storniranje pri otkazanoj narudžbi.
  - `updateCod(parcelId, amount)` — izmjena otkupnine.
  - `getStatus(parcelNumber)` — status pošiljke.
- `GlsLabelController` — REST sučelje za lokalno testiranje bez Pantheona, s `@Valid`
  provjerom ulaznih polja.
- `GlsExceptionHandler` — pretvara `GlsApiException`/validacijske/neočekivane greške u
  čiste HTTP odgovore (502/400/500) umjesto Spring Bootovog generičkog 500-icа.
- DTO-i vjerni GLS API dokumentaciji (`Parcel`, `Address`, `GlsService`, `ErrorInfo`, ...).
- SHA-512 hashiranje lozinke, GLS `/Date(ms)/` format, konverzija byte-niza labela u PDF.
- Fail-fast provjera kredencijala (`GlsLabelService.requireGlsCredentials`) — jasna
  greška umjesto NPE-a kad `gls.username`/`gls.password` nisu popunjeni.
- Connect/read timeout na `RestTemplate` (5s / 30s) i SLF4j logging poziva/grešaka
  (bez logiranja payloada — adrese i password hash se ne pišu u log).
- Testovi: `GlsLabelServiceTest` (poslovna logika — COD, mapiranje adresa, error
  handling) i `GlsApiClientTest` (JSON wire format prema MyGLS-u, PascalCase nazivi
  polja) uz postojeći `GlsPasswordEncoderTest`.

## Što još nedostaje (namjerno, izvan trenutnog opsega)

- Pantheon konektor koji puni `LabelRequest` iz stvarne narudžbe (open pitanja u
  dokumentaciji: koji Pantheon API, fiksna pickup adresa, `WebshopEngine` vrijednost).
  Bez njega servis radi samo preko ručnog curl/Postman testiranja.
- Podrška za PSD (dostava na paketomat) — trenutno `GlsService` nosi samo `code`,
  bez `*Parameter` polja.
- Idempotencija — dvostruki poziv `createLabel` za istu narudžbu kreira dvije
  naljepnice/dvije GLS naplate. Nije sigurno izložiti ovo retry logici dok ne postoji
  perzistencija `ParcelId`/`ParcelNumber` natrag na Pantheon dokument (vezano uz
  konektor iznad).

## Konfiguracija

Popuni `src/main/resources/application.yml` (`gls.username`, `gls.password`,
`gls.client-number`, `gls.webshop-engine`, `gls.pickup-address.*`) — kredencijale
zatraži od GLS-a za test i produkcijsko okruženje odvojeno. **Bez toga servis se
pokreće, ali svaki poziv na `/api/gls/*` vraća 503** (fail-fast provjera), umjesto
starog ponašanja gdje je pucalo s NPE duboko unutra.

Za lokalne kredencijale koristi `application-local.yml` pored `application.yml` —
`.gitignore` sad stvarno isključuje `application-local.yml`/`application-local.yaml`
i `.env` (prije je README to tvrdio, a `.gitignore` file to nije pokrivao — ispravljeno).
Ne commit-ati stvarne vrijednosti u `application.yml` samom.

## Pokretanje lokalno

```bash
mvn spring-boot:run
```

Servis sluša na `:8081`.

### Ručno testiranje (test okruženje)

```bash
curl -X POST http://localhost:8081/api/gls/labels \
  -H "Content-Type: application/json" \
  -d '{
    "clientReference": "NAR-2026-00001",
    "recipient": {
      "name": "Ana Anić",
      "street": "Ilica",
      "houseNumber": "12",
      "city": "Zagreb",
      "zipCode": "10000",
      "countryIsoCode": "HR",
      "contactPhone": "+385911234567"
    },
    "codAmount": 42.90,
    "content": "Kozmetički proizvodi"
  }' --output label.pdf
```

## Build i testovi

```bash
mvn test
```
