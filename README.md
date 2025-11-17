## Flight Booking System – Assignment Summary

This repository contains my solution for a **Flight Booking System** backend assignment, built with **Spring Boot 3** and **Java 17**.  
The goal is to model a small airline booking platform, design the data model, expose REST APIs, and cover key business rules with tests.

The code implements:

- Flight inventory management for airlines.
- Flight search (one-way and round-trip).
- Booking creation (with passengers, seats, and meals).
- Ticket retrieval, booking history, and cancellation.
- Validation and error handling aligned with the assignment test cases, using **Mockito** unit tests.

---

## Domain Model (ER Diagram)

![Flight Booking ER Diagram](https://raw.githubusercontent.com/kanchanraiii/FlightBookingJava/refs/heads/master/ER%20Diagram%20-%20FlightBookingSys.drawio.png)

---

## REST APIs

All endpoints are under the base path: `/api/v1.0/flight`

### 1. Flight Inventory – Add Schedule

**POST** `/api/v1.0/flight/airline/inventory/add`  
Controller: `FlightInventoryController`  
Request body: `AddInventory`

Key fields:

- `airlineId` (Long, required)
- `flightNumber` (String, required)
- `sourceCity` / `destinationCity` (enum `CityEnum`, required, cannot be same)
- `departureDate`, `departureTime` (future date/time)
- `arrivalDate`, `arrivalTime` (after departure)
- `totalSeats` (> 0)
- `price` (> 0)
- `mealAvailable` (boolean)

Service: `FlightInventoryService.addInventory(AddInventory req)`

Business rules:

- Airline must exist, otherwise `ResourceNotFoundException("Airline not found")`.
- Required field checks for flight number, cities, dates, times, seats, price.
- Source and destination cannot be the same city.
- Departure must be in the **future**.
- Arrival must be **after** departure.
- Duplicate check: if a flight with the same `flightNumber` and `departureDate` already exists, reject.

Errors are mapped to JSON via `GlobalErrorHandler`.

### 2. Flight Search

**POST** `/api/v1.0/flight/search`  
Controller: `FlightSearchController`  
Request body: `FlightSearchRequest`

Fields:

- `sourceCity`, `destinationCity` (required, `CityEnum`)
- `travelDate` (required, `LocalDate`)
- `tripType` (`ONE_WAY` or `ROUND_TRIP`)
- `returnDate` (required only if `tripType` = `ROUND_TRIP`)

Service: `FlightSearchService.searchFlights(FlightSearchRequest req)`

Business rules:

- `sourceCity` and `destinationCity` required and must be different.
- `travelDate` required and cannot be in the past.
- For round-trip:
  - `returnDate` required.
  - `returnDate` cannot be before `travelDate`.
- Outbound flights are fetched with  
  `findBySourceCityAndDestinationCityAndDepartureDate(source, dest, travelDate)`.
- If no outbound flights are found, a `ResourceNotFoundException("No outbound flights found")` is thrown (current implementation uses 404 instead of `200 []`).

### 3. Booking Creation

**POST** `/api/v1.0/flight/booking/{flightId}`  
Controller: `BookingController`  
Path variable: `flightId` (present for API consistency; logic uses `outboundFlightId` from body)  
Request body: `BookingRequest`

Important `BookingRequest` fields:

- `outboundFlightId` (Long, required)
- `returnFlightId` (Long, optional, required if round-trip)
- `contactName` (String, required)
- `contactEmail` (String, required, `@Email`)
- `tripType` (`TripType`, required)
- `passengers` – list of `PassengerRequest` (required, at least 1)

`PassengerRequest` fields:

- `name` (required)
- `age` (required, > 0)
- `gender` (required)
- `seatOutbound` (required)
- `seatReturn` (required for round-trip)
- `meal` (optional string, must match `MealType` when present)

Service: `BookingService.bookFlight(Long flightId, BookingRequest req)`

Business rules:

- Must have at least one passenger.
- Trip type:
  - If `ROUND_TRIP`, `returnFlightId` is required.
- Flights:
  - Outbound flight must exist; else `ResourceNotFoundException("Outbound flight not found")`.
  - Return flight (if provided) must exist; else `ResourceNotFoundException("Return flight not found")`.
- Seat availability:
  - `availableSeats` on each flight must be >= number of passengers; otherwise `ValidationException("Not enough seats available in outbound/return flight")`.
- Passenger validation:
  - `age` > 0.
  - `seatOutbound` non-blank.
  - `seatReturn` required for round-trip.
  - `meal`, if provided, must map to `MealType` (`VEG` / `NON_VEG`) ignoring case.
- On success:
  - Generates a 6-character random PNR (letters/digits).
  - Creates a `Booking` with `BookingStatus.CONFIRMED`.
  - Persists `Passenger` rows and updates `availableSeats` on outbound (and return) flights.

### 4. Ticket Retrieval

**GET** `/api/v1.0/flight/ticket/{pnr}`  
Controller: `TicketController` (method `getTicket`)  
Service: `BookingService.getTicket(String pnr)`

Behaviour:

- Looks up booking by `pnrOutbound`.
- If found, returns the `Booking` with all details.
- If not found, throws `ResourceNotFoundException("PNR not found")`.
- Invalid formats (e.g. `"123"`) are treated the same as a non-existent PNR at the service layer.

### 5. Booking History

**GET** `/api/v1.0/flight/booking/history/{email}`  
Controller: `TicketController` (method `getHistory`)  
Service: `BookingService.getHistory(String email)`

Behaviour:

- Fetches bookings with `bookingRepository.findByContactEmail(email)`.
- If the list is **empty**, throws `ResourceNotFoundException("No bookings found for this email")`.
- If there are one or more bookings, returns them as a list.

In a full system this would typically be secured (only logged-in owner can view), but security is out of scope of this assignment code.

### 6. Booking Cancellation

**DELETE** `/api/v1.0/flight/booking/cancel/{pnr}`  
Controller: `TicketController` (method `cancelTicket`)  
Service: `BookingService.cancelTicket(String pnr)`

Behaviour:

- Fetches booking by PNR using `getTicket(pnr)`.
- If booking status is already `CANCELLED`, throws `ValidationException("Ticket is already cancelled")`.
- Otherwise:
  - Sets booking status to `CANCELLED` and saves it.
  - Adds `totalPassengers` back to `availableSeats` of the outbound flight.
  - If a return flight exists, restores its seats as well.

Business rules about **24-hour cancellation windows** are described in the assignment, but not implemented in this version; cancellation purely depends on current status and existence of the booking.

---

## Validation & Error Handling

Custom exceptions:

- `ValidationException` – business rule violations (invalid fields, dates, seats, etc.).
- `ResourceNotFoundException` – missing airline/flight/booking/history/PNR.

Global handler: `GlobalErrorHandler`  
Maps exceptions and JSON parsing errors into simple JSON responses:

- `MethodArgumentNotValidException` – bean validation on DTOs (`@Valid`, `@NotNull`, `@NotBlank`, `@Email`).
- `ValidationException` – `{ "error": "<message>" }`.
- `ResourceNotFoundException` – `{ "error": "<message>" }`.
- `HttpMessageNotReadableException` – special handling for:
  - Invalid `CityEnum`, `TripType`, `MealType` values.
  - Invalid boolean for `mealAvailable`.
  - Invalid date format (`yyyy-MM-dd`).
- Generic `Exception` – fallback `{ "error": "<message>" }`.

The behaviour is covered by `GlobalErrorHandlerTest`.

---

## Tests and Mapping to Assignment Scenarios

All tests are under `src/test/java/com/flightapp`. They are **unit tests** using **JUnit 5 + Mockito**, focusing on service/business logic and error handler, not on HTTP wiring or authentication.

### Inventory Creation (Add Flight)

File: `BookingServicesTests.java` (inventory and booking tests are grouped here)  
Service under test: `FlightInventoryService`

Covers scenarios corresponding to:

- Valid inventory creation with all required fields and optional fields.
- Missing mandatory fields (flight number, cities, dates).
- Invalid logical values (negative seats, non-positive price, arrival before departure, departure in the past).
- Duplicate flights for same `flightNumber` + `departureDate`.

### Flight Search

File: `BookingServicesTests.java` (flight search tests)  
Service under test: `FlightSearchService`

Covers scenarios aligned with:

- One-way search with valid `from`, `to`, `date`.
- Round-trip search with travel/return dates.
- Ensuring returned `FlightInventory` has date/time, airline, and price.
- Missing required fields, same source/destination, past travel dates.
- Route with no scheduled flights (current implementation: 404 via `ResourceNotFoundException`).
- Search for flights on “today”.

### Booking Creation

File: `BookingServicesTests.java`  
Service under test: `BookingService.bookFlight`

Covers:

- Booking a single seat:
  - Generates a 6-character PNR.
  - Confirms status and decreases available seats.
- Booking multiple seats:
  - Reduces available seats by passenger count.
- Booking with passenger details, Veg meal, and seat selection.
- Negative cases:
  - Flight not found.
  - Passenger list empty (0 seats equivalent).
  - Not enough seats available.
  - Booking the last available seat on a flight (edge case: availability goes to 0).

Security-oriented scenarios (unauthenticated user, booking as another user) would be covered at the controller/security layer and are not implemented in this assignment.

### Ticket Retrieval

File: `TicketRetrievalServiceTests.java`  
Service under test: `BookingService.getTicket`

Scenarios:

- Retrieve an existing PNR and verify contact details.
- Ensure retrieved ticket details match what was stored at booking.
- Non-existent PNR → `ResourceNotFoundException`.
- “Invalid” PNR formats behave the same as non-existent PNRs at the service layer.

### Booking History

File: `BookingHistoryServiceTests.java`  
Service under test: `BookingService.getHistory`

Scenarios:

- User with multiple bookings → list of 2 bookings.
- User with a single booking → list of size 1.
- No bookings for email → `ResourceNotFoundException`.
- Invalid email formats are treated like emails with no bookings (also `ResourceNotFoundException`).

### Booking Cancellation

File: `BookingCancellationServiceTests.java`  
Service under test: `BookingService.cancelTicket`

Scenarios:

- Cancelling a confirmed booking:
  - Status changes to `CANCELLED`.
  - Available seats on outbound flight increase by passenger count.
- Cancelling a non-existent PNR → `ResourceNotFoundException`.
- Cancelling an already cancelled booking → `ValidationException("Ticket is already cancelled")`.

Time-window-based cancellation rules (24 hours before flight, etc.) are described in the assignment but are not yet implemented; currently, only the existence and status of the booking are validated.

### Models and Enums

File: `ModelTests.java`  
Verifies:

- Basic getters/setters for `Airline`, `FlightInventory`, `Passenger`, `Booking`.
- Enum values: `CityEnum`, `MealType`, `BookingStatus`, `TripType`.

---

## Running Tests

```bash
./mvnw test
```
