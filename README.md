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
- `GlsLabelController` — REST sučelje za lokalno testiranje bez Pantheona.
- DTO-i vjerni GLS API dokumentaciji (`Parcel`, `Address`, `GlsService`, `ErrorInfo`, ...).
- SHA-512 hashiranje lozinke, GLS `/Date(ms)/` format, konverzija byte-niza labela u PDF.

## Što još nedostaje (namjerno, izvan trenutnog opsega)

- Pantheon konektor koji puni `LabelRequest` iz stvarne narudžbe (open pitanja u
  dokumentaciji: koji Pantheon API, fiksna pickup adresa, `WebshopEngine` vrijednost).
- Podrška za PSD (dostava na paketomat) — trenutno `GlsService` nosi samo `code`,
  bez `*Parameter` polja.
- Perzistencija `ParcelId`/`ParcelNumber` natrag na Pantheon dokument (sprječavanje
  dupliciranja naljepnice).

## Konfiguracija

Popuni `src/main/resources/application.yml` (`gls.username`, `gls.password`,
`gls.client-number`, `gls.webshop-engine`, `gls.pickup-address.*`) — kredencijale
zatraži od GLS-a za test i produkcijsko okruženje odvojeno. Ne commit-ati stvarne
vrijednosti u git; za lokalni rad koristi npr. `application-local.yml` (već je u
`.gitignore` obuhvaćeno samo `target/`/`.idea/`, po potrebi dodaj i taj file).

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
