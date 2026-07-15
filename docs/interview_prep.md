# Interview Preparation Guide: Hotel Room Booking Console App

This guide contains 10 predicted technical interview questions based on the design, architecture, and implementation details of this project.

---

### Question 1: Can you explain your project?
**Answer:**
"I designed and built a **Hotel Room Booking Console Application** in Java to simulate real-world Property Management Systems (PMS) used by hospitality providers. 
The system addresses core hotel workflows: room classification, search filters by dates, guest profiling, booking calculations, persistence, and reservation cancellations.

Architecturally, I structured it using a **3-Layer Architecture** pattern:
1.  **Presentation Layer (`HotelApp.java`)**: Manages CLI interactions, takes inputs, validates terminal commands, and formats tabular data displays.
2.  **Service Layer (`BookingManager.java`)**: Handles core business logic, including calculating room nights, computing pricing with taxes, checking availability dates for collisions, and updating room reservation records.
3.  **Data Access Layer (`FileHandler.java`)**: Reads and writes data to local CSV files (`rooms.csv` and `bookings.csv`) to achieve data persistence without a database server.

OOP principles are at the heart of this system: I encapsulated entities like `Room` and `Guest`, and used **Composition** inside `Booking` to link rooms and guests together. This ensures the app is modular, testable, and clean."

---

### Question 2: How did you handle date calculations and prevent date overlaps (double booking) in Java?
**Answer:**
"I avoided the outdated `java.util.Date` and `Calendar` APIs and instead used the modern, thread-safe **Java Time API (`java.time.LocalDate`)**.
To calculate the total booking nights, I used `ChronoUnit.DAYS.between(checkIn, checkOut)`.

For double-booking prevention, the core logic checks dates in the `BookingManager`. When a user requests a room for a date range `(newStart, newEnd)`, the system retrieves all active bookings for that room. A collision occurs if the dates overlap. The overlap condition is defined as:
```
(newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart))
```
If any existing booking overlaps, the room is flagged as unavailable for that duration, preventing duplicate bookings."

---

### Question 3: What OOP principles did you apply in this project, and how?
**Answer:**
"I applied the core pillars of Object-Oriented Programming:
*   **Encapsulation**: Classes like `Room`, `Guest`, and `Booking` have private properties (such as `roomNumber` and `pricePerNight`). They are exposed only through controlled public getters, setters, and constructors. I validated parameters inside setters (e.g., preventing negative room rates).
*   **Abstraction**: The presentation layer (`HotelApp`) interacts only with the public interface of `BookingManager`. It does not know *how* the booking is saved or *how* availability is calculated.
*   **Composition**: The `Booking` class contains references to `Guest` and `Room` instances rather than plain strings. This forms a HAS-A relationship (`Booking` HAS A `Room` and HAS A `Guest`), representing real-world business dependencies."

---

### Question 4: How did you implement local storage/data persistence without a SQL database?
**Answer:**
"I implemented file persistence using standard Java I/O classes. I created a custom `FileHandler` class that manages reading and writing data to flat files (`data/rooms.csv` and `data/bookings.csv`).
*   **Reading**: On application startup, the system uses a `BufferedReader` wrapped in a `FileReader`. It parses the file line-by-line, splits values using commas (`,`), instantiates the domain objects (`Room` and `Guest`), and populates the in-memory collections.
*   **Writing**: When a user confirms a booking, or when the application exits safely, the system uses `BufferedWriter` wrapped in a `FileWriter` to serialize the objects back into standard comma-separated text lines.
This approach introduces files as local databases, preparing the project boundary for JDBC or JPA integrations later."

---

### Question 5: Why did you choose ArrayList/HashMap collections, and when would you use one over the other?
**Answer:**
"I used an `ArrayList<Room>` because we frequently need to iterate through all rooms sequentially, search for matches, or filter them based on conditions (e.g., listing all available Deluxe rooms). `ArrayList` provides $O(1)$ lookup time by index and dynamic sizing, making it ideal for sequential iterations.
For booking lookups, I chose to use a `Map<String, Booking>` (using a `HashMap` implementation) where the key is the unique `BookingID` (e.g., `BKG-1001`). This provides near $O(1)$ constant time lookups when a customer wants to fetch or cancel a booking, which is much faster than doing an $O(n)$ linear scan of a list."

---

### Question 6: How did you make the command line user inputs robust against crashes?
**Answer:**
"Console inputs are highly error-prone because users can enter arbitrary values. I handled this using three main steps:
1.  **Data Type Validation**: If a user enters non-numeric text (like 'abc') into a prompt expecting a menu choice or number of nights, Java's `Scanner.nextInt()` throws an `InputMismatchException`. I wrapped scanner calls in a utility input method with `try-catch` blocks and called `scanner.nextLine()` to clear the scanner buffer on catch.
2.  **Date Format Validation**: When reading dates, I catch `DateTimeParseException` to ensure check-in and check-out dates conform strictly to `YYYY-MM-DD`.
3.  **Business Logic Checking**: I checked that the number of guests does not exceed the room's maximum capacity, check-in dates are not in the past, and check-out is after check-in."

---

### Question 7: How does the pricing and billing module work?
**Answer:**
"The pricing logic is encapsulated inside the `Booking` (or `Bill`) module.
*   First, we determine the number of nights using `ChronoUnit.DAYS.between(checkIn, checkOut)`.
*   We multiply this duration by the room's base rate (`pricePerNight`).
*   We then calculate a standard tax surcharge (e.g., 18% GST).
*   The system formats all final calculations to two decimal places using `System.out.printf()` or `String.format("%.2f", totalAmount)` to ensure standard currency presentation.
This structured breakdown is printed to the console as a clear invoice receipt and stored in the database."

---

### Question 8: How do you handle room cancellations and state updates in memory and disk?
**Answer:**
"When a cancellation request is made with a `BookingID`:
1.  The `BookingManager` lookups the ID in the bookings map. If not found, it prints an error.
2.  If found, it modifies the booking status to `CANCELLED` (or removes the booking record from the active list, depending on configuration).
3.  The associated room is marked as unoccupied for that date range.
4.  The system calls the `FileHandler` to overwrite the current state of bookings and rooms back to the CSV database.
This synchronizes in-memory changes with physical disk records immediately, preventing data loss."

---

### Question 9: If this app needed to scale to support millions of bookings globally, what architectural changes would you recommend?
**Answer:**
"To scale this application for enterprise-level demands, I would transition it from a console-based, single-user system to a distributed backend:
1.  **Persistence Layer**: Replace CSV files with an Enterprise Relational Database (like PostgreSQL or MySQL) and manage connections using connection pools (e.g., HikariCP) and an ORM framework like Hibernate/JPA.
2.  **Concurrency Management**: Implement optimistic or pessimistic locking at the database transaction level to prevent race conditions when two users book the same room at the exact same millisecond.
3.  **API Layer**: Wrap the business service logic inside a RESTful API service using **Spring Boot**.
4.  **Scaling and Performance**: Introduce a cache (e.g., Redis) for rapid reads of room catalog data and deploy microservices behind a load balancer on cloud systems."

---

### Question 10: What was the most challenging part of developing this application, and how did you solve it?
**Answer:**
"The most challenging part was ensuring accurate date overlap checking when searching for available rooms. Originally, I only marked a room's boolean status as `isOccupied`. However, in real life, a room can be occupied *next week* but available *today*. 
To solve this, I decoupled the room's transient status from its static details. I created a relationship where room availability is determined dynamically by matching the searched dates against all active bookings stored in memory. Refining this logical formula and resolving dates using the `java.time` methods was challenging but resulted in a highly realistic reservation engine."
