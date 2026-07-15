# Project Architecture: Hotel Room Booking Console App

This document outlines the architectural design, data structures, workflows, and diagrams for the Hotel Room Booking Console Application.

---

## 1. System Components & IPO Specification

The system is designed around the classic **Input-Processing-Output (IPO)** model to ensure separation of concerns and clear data boundaries.

```
       +-----------------------+
       |         INPUT         |
       |  (Console / Storage)  |
       +-----------+-----------+
                   |
                   v
       +-----------------------+
       |      PROCESSING       |
       |    (Business Logic)   |
       +-----------+-----------+
                   |
                   v
       +-----------------------+
       |        OUTPUT         |
       |  (Console / Storage)  |
       +-----------------------+
```

### Inputs
1. **Guest Registration**: Guest Name, Contact Number, Email Address.
2. **Booking Criteria**: Room Type (Standard, Deluxe, Suite), Check-In Date, Check-Out Date, Number of Guests.
3. **Operational Inputs**: Room ID (to select), Booking ID (for cancellation/viewing), Confirmation command.
4. **Data Persistence Input**: Reading stored database records from local CSV/text files on startup.

### Processing
1. **Input Validation**: Check that contact numbers are exactly 10 digits, names are not blank, dates are in `YYYY-MM-DD` format, and check-in date is not in the past or after the check-out date.
2. **Room Availability Search**: Filter room collections matching the requested room type and status (e.g., Unoccupied or Free of booking collisions for the specified date range).
3. **Reservation & Date Calculations**: Calculate total nights booked using Java's `java.time.LocalDate` and `java.time.temporal.ChronoUnit`.
4. **Bill Calculation**: Compute base room rates multiplied by nights, apply tax (e.g., 18% GST), and calculate final booking amount.
5. **ID Generation**: Dynamically generate unique booking and guest IDs (e.g., `BKG-1001`, `GST-5001`) to maintain clear referencing.
6. **State Management**: Update Room status (e.g., booked/occupied) and associate it with a Guest and Booking.
7. **Serialization/File Storage**: Format object attributes into CSV lines and append to physical local storage.

### Outputs
1. **Console UI Views**:
   - Main Dashboard Menu.
   - List of all Rooms with current statuses.
   - Available Rooms matching a search query.
2. **Booking Confirmation**: Printed confirmation slip with Booking ID, Guest details, Room number, and Check-in/out dates.
3. **Bill Summary**: Detailed breakdown of nights, room base rate, tax rate, and final cost.
4. **Action Confirmation**: Successful cancellation notices or failure notifications (e.g., "Room not available", "Invalid date format").
5. **Booking History**: Tabular view of all past and active bookings read from history.
6. **Data Persistence Output**: Updated CSV text files containing current rooms and bookings.

---

## 2. Text-Based Architecture Diagrams

### A. System Overview & Layered Architecture
The application uses a clean, layered architectural pattern, segregating console interactions from database/file manipulation and core business logic.

```
+-----------------------------------------------------------------+
|                          User Interface                         |
|     (HotelApp - CLI Menu, User Prompts, Table Formatters)       |
+-------------------------------++--------------------------------+
                                ||
                                v
+-----------------------------------------------------------------+
|                       Service / Control Layer                   |
|     (BookingManager - Search, Booking Logic, Cancellations)     |
+-------------------------------++--------------------------------+
                                ||
                                v
+-----------------------------------------------------------------+
|                          Domain Model                           |
|       (Room)         (Guest)         (Booking)         (Bill)   |
+-------------------------------++--------------------------------+
                                ||
                                v
+-----------------------------------------------------------------+
|                        Data Access Layer                        |
|       (FileHandler - Reads/Writes data/ to CSV files)          |
+-----------------------------------------------------------------+
```

### B. Class Relationship Diagram (OOP Model)
The diagram below shows how OOP entities interact with each other (Aggregation and Composition).

```
                      +-------------------+
                      |      HotelApp     |
                      +---------+---------+
                                |
                                v (uses)
                      +-------------------+
                      |   BookingManager  |
                      +---------+---------+
                                |
        +-----------------------+-----------------------+
        | 1                     | *                     | *
        v                       v                       v
+---------------+       +---------------+       +---------------+
|     Guest     |       |     Room      |       |    Booking    |
+---------------+       +---------------+       +---------------+
| - guestId     |       | - roomNumber  |       | - bookingId   |
| - name        |       | - roomType    |       | - guest       |<---+
| - contactNo   |       | - pricePerNight|      | - room        |    |
+---------------+       | - isAvailable |       | - checkInDate |    |
                        +---------------+       | - checkOutDate|    |
                                                | - totalAmount |    |
                                                +-------+-------+    |
                                                        |            |
                                                        v (creates)  | (cancels)
                                                +-------+-------+    |
                                                |     Bill      |----+
                                                +---------------+
                                                | - billId      |
                                                | - baseAmount  |
                                                | - taxAmount   |
                                                | - totalAmount |
                                                +---------------+
```

### C. Detailed Booking Flow
The sequential workflow from a guest selecting a room to securing the reservation.

```
 [User Dashboard]
        |
        v
 1. Selects "Book a Room"
        |
        v
 2. System Prompts details:
    - Guest Name & Phone
    - Room Type (Standard/Deluxe/Suite)
    - Check-in & Check-out Date
        |
        v
 3. Validator Checks Inputs ----------> [Invalid] -> Show Error & Return to Menu
        | (Valid)
        v
 4. Query Available Rooms for selected Dates and Room Type
        |
        +---> If None Available --------> Show "No Rooms Available" -> Return to Menu
        | (Rooms Available)
        v
 5. Display list of matching rooms
        |
        v
 6. User chooses a Room Number
        |
        v
 7. Process Booking:
    - Calculate total nights
    - Calculate total amount (Price * Nights + GST)
    - Generate unique Booking ID (e.g. BKG-1002)
    - Update Room status to Booked for these dates
        |
        v
 8. Write Booking details to CSV file (Persistence)
        |
        v
 9. Display Invoice & Booking Confirmation summary to Console
```

### D. Data Flow Diagram (DFD)
How data moves between the User, App Services, Memory Collections, and Files.

```
+------------+       User Inputs / Dates       +------------+
|            | ------------------------------> |            |
|    User    |                                 |  HotelApp  |
|            | <------------------------------ |   (UI)     |
+------------+        Printed Receipt          +-----+------+
                                                     |
                                                     | Requests / Actions
                                                     v
                                              +-------------+
                                              |   Booking   |
                                              |   Manager   |
                                              +---+-----+---+
                                                  |     ^
                          Retrieves/Saves Objects |     | Updates collections
                                                  v     |
  +------------------+     Loads on startup   +-------------+
  |    CSV Files     | ---------------------> | Memory List |
  | (data/rooms.csv) |                        |  (ArrayList/|
  | (data/bkgs.csv)  | <--------------------- |   HashMap)  |
  +------------------+     Saves changes      +-------------+
```
