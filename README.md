# DSY – Slagteri-sporbarhedssystem

Kursusprojekt i Distribuerede systemer (DSY), VIA University College.
Systemet simulerer et slagteri med tre stationer og gør det muligt at spore,
hvilke dyr der indgår i hvilke produkter, så produkter kan kaldes tilbage (recall).

## Indhold

| Del | Hvad | Hvor |
|---|---|---|
| Part 1 | Domænemodel og C1 (System Context) | `docs/diagrams/` |
| Part 2 | gRPC-service: dyr i et produkt og produkter for et dyr | `src/main/proto`, `org.example.traceability` |
| Part 3 | REST-service til registrering af dyr | `org.example.animal` |
| C4 | C1, C2 og C3 (PlantUML-kilde og SVG) | `docs/diagrams/` |

## Teknologi

- Java 24, Spring Boot 3.5, Maven
- Spring Data JPA og H2 (fil-database i `data/`, oprettes automatisk)
- gRPC og Protocol Buffers
- springdoc OpenAPI (Swagger UI)
- JUnit 5 og MockMvc

## Kør projektet

```bash
mvn spring-boot:run
```

eller kør `Main` i IntelliJ. Ved første opstart indlæser `DataLoader` testdata.

| Service | Adresse |
|---|---|
| REST API | http://localhost:8081/animals |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| H2-konsol | http://localhost:8081/h2-console (JDBC URL: `jdbc:h2:file:./data/slaughterhouse`, bruger `sa`) |
| gRPC | `localhost:9090` (kontrakt: `src/main/proto/traceability.proto`) |

## REST API (Part 3)

| Metode | URI | Svar |
|---|---|---|
| `POST` | `/animals` | 201 Created og Location-header, 400 ved ugyldige data, 409 hvis nummeret findes |
| `GET` | `/animals/{registrationNumber}` | 200 eller 404 |
| `GET` | `/animals?date=2026-10-22` | Dyr ankommet på datoen |
| `GET` | `/animals?origin=Hansens Gård` | Dyr fra gården |

Eksempel på body:

```json
{"registrationNumber":"DK-1001","arrivalDate":"2026-10-22","weight":112.5,"origin":"Hansens Gård"}
```

## gRPC (Part 2)

| RPC | Request | Svar |
|---|---|---|
| `GetAnimalsForProduct` | `{ "product_id": 2 }` | Registreringsnumre på alle dyr i produktet |
| `GetProductsForAnimal` | `{ "registration_number": "A-1" }` | Alle produkter, dyret kan indgå i (recall) |

Sporing sker gennem kæden Produkt → Bakke → Del → Dyr. Et produkt refererer til
bakker, og en bakke kan indeholde dele fra flere dyr. Derfor finder recall alle
produkter, der *kan* indeholde dyret. Ukendte id'er giver `NOT_FOUND`.

Kan afprøves i Postman (New → gRPC → importér `traceability.proto`) eller BloomRPC.

## Tests

```bash
mvn test
```

- `AnimalControllerTest`: 7 tests af REST API'et (201, 400, 404, 409 og filtrering)
- `TraceabilityServiceTest`: 5 tests, der kalder gRPC-serveren gennem en rigtig klient-stub

Testene bruger en database i hukommelsen og gRPC-port 9091 (`src/test/resources/application.properties`).

## Projektstruktur

```
docs/diagrams/            C1, C2 og C3 (.puml og .svg)
src/main/proto/           gRPC-kontrakt (traceability.proto)
src/main/java/org/example/
  animal/                 REST: entity, repository, controller
  traceability/           gRPC: entities (Part, Tray, Product), repositories, service, server
src/test/                 JUnit-tests og testkonfiguration
```
