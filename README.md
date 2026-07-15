# Hotel Room Booking Console App 🏨

> An industry-oriented Java project simulating a real-world hotel Property Management System (PMS).
> Built in two versions — a **coloured terminal console app** and a **professional Java Swing desktop GUI** —
> both demonstrating core OOP, date handling, overlap-detection, and billing logic.

This project serves as a portfolio-grade **Proof of Work** for Java Developer, Backend Developer,
Software Engineer, and Application Developer roles.

---

## 📖 Table of Contents
1. [Project Overview](#-project-overview)
2. [Problem Statement](#-problem-statement)
3. [Industry Relevance](#-industry-relevance)
4. [Two Versions — Console and GUI](#-two-versions)
5. [Core Features](#-core-features)
6. [Java Concepts Used](#-java-concepts-used)
7. [Architecture and System Flow](#-architecture-and-system-flow)
8. [Directory Structure](#-directory-structure)
9. [How to Run](#-how-to-run)
10. [Console Sample Output](#-console-sample-output)
11. [GUI Screenshots](#-gui-screenshots)
12. [Known Limitations](#-known-limitations)
13. [Future Improvements](#-future-improvements)
14. [Learning Outcomes](#-learning-outcomes)
15. [Author](#-author)

---

## 🌟 Project Overview

The **Hotel Room Booking Console App** simulates the front-desk operations of a hotel Property
Management System. It allows staff to search room availability, create guest bookings, perform
check-in and check-out operations, compute tax-inclusive bills, and cancel reservations — all
managed in memory with clean, validated console interactions.

A bonus **Java Swing GUI version** (HotelAppGUI) provides the same full functionality through an
Apple-inspired desktop interface with styled tables, form panels, and inline validation.

---

## ⚠️ Problem Statement

Small and medium hotels often manage reservations using spreadsheets or manual registers. This causes:

- **Double Bookings** — the same room reserved for overlapping dates by two guests.
- **Billing Errors** — wrong night counts, missing taxes, or manual calculation mistakes.
- **No Cancellation Tracking** — no record of which bookings were cancelled and when.
- **No Status Visibility** — staff cannot instantly see which rooms are vacant or occupied.

This application solves each of these problems with automated overlap detection, dynamic billing,
status tracking, and clean data management.

---

## 💼 Industry Relevance

Systems like this power the hospitality and travel tech industry:

| Company / Platform | Use Case |
|---|---|
| Oracle Opera PMS | Full hotel front-desk management |
| Booking.com / Airbnb | Guest reservation and availability engines |
| Marriott / Hilton Systems | Room status, billing, and check-in/out tracking |
| Hostel Management Tools | Lightweight local booking systems |

### Why Java?
- **Enterprise Standard**: Java powers backends at banks, airlines, and hospitality chains worldwide.
- **Scalable Architecture**: Console apps like this can be lifted directly into Spring Boot REST APIs.
- **Strong Typing + OOP**: Prevents entire categories of booking logic bugs at compile time.
- **Platform Independent**: Runs on Windows, macOS, and Linux with no changes.

---

## 🖥️ Two Versions

This project ships two fully working, independent applications that share the same business logic.

### Version 1 — Console App (`src/HotelApp.java`)
The **primary, original version**. Runs in any terminal. Demonstrates pure Java OOP without
any external libraries. Uses ANSI colour codes for green/red/yellow output, box-drawing characters
for tables and receipts, and a numbered menu for navigation.

**Run it with:**
```bash
javac src/HotelApp.java
java -cp src HotelApp
```

### Version 2 — Desktop GUI App (`src/HotelAppGUI.java`)
A **bonus Java Swing desktop application** built on top of the console version's logic. Uses
`CardLayout` for screen navigation, `JTable` for room grids, and custom-styled panels and buttons
with an Apple-inspired light gray palette and blue accent colour `#007AFF`.

**Run it with:**
```bash
javac src/HotelApp.java src/HotelAppGUI.java
java -cp src HotelAppGUI
```

> Both versions share the same `Room`, `Booking`, `RoomType`, and business logic classes.
> The GUI directly calls the console version's availability checker and billing calculator —
> there is zero duplication of business logic.

---

## ⚙️ Core Features

Both versions support all of the following features:

| Feature | Console | GUI |
|---|:---:|:---:|
| List all rooms with type, status, and rate | ✅ | ✅ |
| Search available rooms by type and date range | ✅ | ✅ |
| Create a booking with guest name and room selection | ✅ | ✅ |
| Auto-generate unique Booking ID (starting at 1001) | ✅ | ✅ |
| View booking details by Booking ID | ✅ | ✅ |
| Cancel a reserved booking | ✅ | ✅ |
| Check-in a guest (marks room OCCUPIED) | ✅ | ✅ |
| Check-out and generate itemised bill | ✅ | ✅ |
| Block overlapping bookings for the same room | ✅ | ✅ |
| Calculate bill: nights × rate + 10% tax | ✅ | ✅ |
| Input validation (dates, empty fields, invalid IDs) | ✅ | ✅ |
| Inline error messages (red text / red prompts) | ✅ | ✅ |
| Colour-coded room status (green=VACANT, red=OCCUPIED) | ✅ | ✅ |
| Clean exit / goodbye message | ✅ | ✅ |

---

## 🛠️ Java Concepts Used

### Core OOP Concepts
| Concept | How It Is Used |
|---|---|
| **Classes and Objects** | `Room`, `Booking`, `RoomType`, `RoomStatus`, `BookingStatus` model real entities |
| **Enums** | `RoomType`, `RoomStatus`, `BookingStatus` — type-safe state representation |
| **Encapsulation** | All fields are `final` or package-private; logic is contained in constructors |
| **Static Nested Classes** | `Room` and `Booking` defined as `static class` inside `HotelApp` |
| **Constructor Logic** | `Booking` constructor computes nights, room charge, tax, and total on creation |

### Collections and Algorithms
| Concept | How It Is Used |
|---|---|
| **LinkedHashMap** | Stores rooms and bookings by ID; preserves insertion order for display |
| **ArrayList** | Builds filtered lists of available rooms to show and pick from |
| **HashSet** | Fast O(1) membership check when validating room number choices |
| **Stream API** | `free.stream().map(r -> r.number).toList()` to extract room numbers |

### Date and Number Handling
| Concept | How It Is Used |
|---|---|
| **java.time.LocalDate** | Parses, stores, and compares check-in and check-out dates |
| **ChronoUnit.DAYS.between** | Calculates total nights stayed |
| **DateTimeFormatter** | Enforces `yyyy-MM-dd` date input format |
| **BigDecimal + RoundingMode** | Precise currency arithmetic — no floating-point errors in billing |

### Error Handling and Validation
| Concept | How It Is Used |
|---|---|
| **DateTimeParseException** | Caught to re-prompt user when date format is wrong |
| **NumberFormatException** | Caught to re-prompt when non-numeric input is typed |
| **Loop-based Validation** | All inputs are in `while(true)` loops — app never crashes on bad input |

### GUI Concepts (HotelAppGUI.java only)
| Concept | How It Is Used |
|---|---|
| **JFrame / JPanel** | Main window and layout containers |
| **CardLayout** | Switches between screens without opening new windows |
| **JTable + DefaultTableModel** | Displays room grid with sortable, styled columns |
| **JComboBox** | Room type and room number selection dropdowns |
| **GridBagLayout** | Precise form field alignment |
| **MouseAdapter** | Hover colour effects on cards and buttons |
| **SwingUtilities.invokeLater** | Ensures GUI initialises on the Event Dispatch Thread |
| **Custom CellRenderer** | Alternating row colours and status-based text colouring |

---

## 📐 Architecture and System Flow

### Class Relationships
```
HotelApp (main controller)
  ├── RoomType (enum)  — SINGLE, DOUBLE, SUITE with rates
  ├── RoomStatus (enum) — VACANT, OCCUPIED, OUT_OF_SERVICE
  ├── Room (static class) — number, type, status
  ├── BookingStatus (enum) — RESERVED, IN_HOUSE, COMPLETED, CANCELLED
  └── Booking (static class) — id, roomNumber, guestName, dates, bill fields
        └── Computes: nights, roomCharge, tax, total in constructor

HotelAppGUI (Swing frontend)
  └── Holds a HotelApp instance (backend)
      └── Calls backend.findAvailableRooms(), backend.validateDates(), backend.bookings, etc.
```

### Booking Flow
```
User picks room type
    → Enters check-in and check-out dates
        → Dates validated (out must be after in)
            → System finds available rooms (overlap check for each room)
                → User picks a room number from filtered list
                    → Enters guest name
                        → Booking created, bill calculated
                            → Booking ID assigned (starting 1001)
```

### Overlap Detection Logic
A room is marked unavailable for dates `[A, B)` if any active booking exists with dates
`[X, Y)` where `A < Y AND X < B`. Cancelled and completed bookings are excluded from this check.

---

## 📁 Directory Structure

```
Hotel-Room-Booking-Console-App/
│
├── src/
│   ├── HotelApp.java          ← Console version (main project)
│   └── HotelAppGUI.java       ← Swing GUI version (bonus)
│
├── data/
│   └── .gitkeep               ← Placeholder (future: rooms.csv, bookings.csv)
│
├── outputs/
│   ├── scenario1_list_rooms.txt
│   ├── scenario2_search_rooms.txt
│   ├── scenario3_create_booking.txt
│   ├── scenario4_overlapping_booking.txt
│   ├── scenario5_view_booking.txt
│   ├── scenario6_cancel_booking.txt
│   ├── scenario7_check_in.txt
│   ├── scenario8_check_out_bill.txt
│   ├── scenario9_invalid_date_format.txt
│   ├── scenario10_checkout_before_checkin.txt
│   └── scenario11_invalid_booking_id.txt
│
├── screenshots/
│   └── .gitkeep               ← Folder for terminal and GUI screenshots
│
├── docs/
│   ├── architecture.md        ← Detailed system diagrams and IPO spec
│   ├── github_guide.md        ← 7-day commit plan and Git best practices
│   ├── interview_prep.md      ← 10 Q&As for technical interviews
│   └── screenshots_mapping.md ← Map of what to screenshot and when
│
├── README.md                  ← This file
└── .gitignore                 ← Excludes .class, IDE metadata, OS files
```

---

## 🚀 How to Run

### Prerequisites
- **JDK 8 or higher** installed (JDK 17 or 25 recommended)
- `javac` and `java` available in your system PATH
- Open a terminal in the project root folder

---

### Console Version — `HotelApp.java`

**Step 1 — Compile:**
```bash
javac src/HotelApp.java
```

**Step 2 — Run:**
```bash
java -cp src HotelApp
```

The menu will appear. Type a number (`1` to `8`) and press **Enter**.

---

### GUI Version — `HotelAppGUI.java`

**Step 1 — Compile both files:**
```bash
javac src/HotelApp.java src/HotelAppGUI.java
```

**Step 2 — Run the GUI:**
```bash
java -cp src HotelAppGUI
```

A desktop window opens. Click the cards on the dashboard to navigate.

---

### Running via IntelliJ IDEA
1. Open IntelliJ IDEA → **File > Open** → select the project root folder.
2. Go to **File > Project Structure > Project** and set SDK to JDK 17 or 25.
3. In the `src/` folder, right-click `HotelApp.java` → **Run 'HotelApp.main()'** for console.
4. Right-click `HotelAppGUI.java` → **Run 'HotelAppGUI.main()'** for the GUI window.

---

### Running via Eclipse
1. **File > Import > Existing Projects into Workspace** → select the project root.
2. Right-click `HotelApp.java` in `src/` → **Run As > Java Application** for console.
3. Right-click `HotelAppGUI.java` → **Run As > Java Application** for GUI.

---

## 🖥️ Console Sample Output

### Welcome Banner
```
┌──────────────────────────────────────────────────┐
│        Welcome to Hotel Starlight System         │
│           Your premium booking partner           │
└──────────────────────────────────────────────────┘
```

### Main Menu
```
┌──────────────────────────────────────────────────┐
│           HOTEL ROOM BOOKING SYSTEM              │
└──────────────────────────────────────────────────┘
  1. List rooms
  2. Search available rooms
  3. Create booking
  4. View booking by id
  5. Cancel booking
  6. Check in
  7. Check out and print bill
  8. Exit
────────────────────────────────────────────────────
Choose an option:
```

### Room Listing Table
```
┌──────────┬────────────┬──────────────┬──────────────┐
│ Number   │ Type       │ Status       │ Rate         │
├──────────┼────────────┼──────────────┼──────────────┤
│ 101      │ Single     │ VACANT       │     ₹2000.00 │
│ 201      │ Double     │ OCCUPIED     │     ₹2400.00 │
│ 301      │ Suite      │ VACANT       │     ₹3200.00 │
└──────────┴────────────┴──────────────┴──────────────┘
```

### Booking Details Card
```
┌──────────────────────────────────────────────────┐
│                  BOOKING DETAILS                 │
├──────────────────────────────────────────────────┤
│ Booking ID:    1001                              │
│ Guest Name:    John Doe                          │
│ Room Number:   201                               │
│ Status:        RESERVED                          │
│ Stay Dates:    2026-07-20 to 2026-07-25          │
│ Total Nights:  5                                 │
├──────────────────────────────────────────────────┤
│ Nightly Rate:  ₹2400.00                          │
│ Room Charge:   ₹12000.00                         │
│ Service Tax:   ₹1200.00                          │
│ Total Net:     ₹13200.00                         │
└──────────────────────────────────────────────────┘
```

### Checkout Bill Receipt
```
┌──────────────────────────────────────────────────┐
│                  CHECKOUT BILL                   │
├──────────────────────────────────────────────────┤
│ Room Number:   201                               │
│ Guest Name:    John Doe                          │
│ Stay Duration: 2026-07-20 to 2026-07-25          │
│ Total Nights:  5                                 │
├──────────────────────────────────────────────────┤
│ Nightly Rate:                          ₹2400.00  │
│ Room Charge:                          ₹12000.00  │
│ Tax (10%):                             ₹1200.00  │
├──────────────────────────────────────────────────┤
│ TOTAL AMOUNT:                         ₹13200.00  │
└──────────────────────────────────────────────────┘
```

---

## 📸 GUI Screenshots

> Screenshots are stored in the `screenshots/` folder.
> Capture them while running `java -cp src HotelAppGUI`.

| Screen | File | Description |
|---|---|---|
| Dashboard | `screenshots/ss_gui_01_dashboard.png` | Home with 4 animated menu cards |
| Room Grid | `screenshots/ss_gui_02_room_grid.png` | JTable with colour-coded room status |
| Search | `screenshots/ss_gui_03_search.png` | Date and type search with results table |
| New Booking | `screenshots/ss_gui_04_new_booking.png` | Booking form and voucher slip side-by-side |
| Booking Voucher | `screenshots/ss_gui_05_booking_voucher.png` | Filled voucher after confirmation |
| Manage Bookings | `screenshots/ss_gui_06_manage_booking.png` | ID lookup with detail card |
| Check In | `screenshots/ss_gui_07_check_in.png` | Status updated to IN_HOUSE |
| Checkout Bill | `screenshots/ss_gui_08_bill_checkout.png` | Total highlighted in blue accent |
| Inline Error | `screenshots/ss_gui_09_inline_error.png` | Red validation text under form field |
| Overlap Block | `screenshots/ss_gui_10_overlap_block.png` | Empty room list when dates are taken |

---

## ⚠️ Known Limitations

- **No persistent storage**: All data is in-memory only. Restarting the app resets all bookings.
- **Single user**: No multi-user support or authentication — one operator at a time.
- **No network access**: Entirely local; no API calls or remote database connections.
- **Terminal encoding**: Console box-drawing characters require a UTF-8 terminal to display correctly.
- **No room editing**: Room catalog is seeded at startup; rooms cannot be added or removed at runtime.

---

## 🔮 Future Improvements

1. **JDBC + MySQL Integration** — Replace in-memory maps with a relational database. Each booking persists across sessions.
2. **Admin and Clerk Login System** — Role-based access: admins manage rooms and pricing; clerks handle bookings.
3. **Payment Gateway Simulation** — Mock payment flows: credit card entry, payment confirmation, receipt printing.
4. **Web Version with Spring Boot** — Expose booking logic as REST APIs; build a frontend in React or plain HTML.
5. **JavaFX Migration** — Replace Swing with JavaFX for modern animations, CSS styling, and FXML layouts.
6. **CSV/JSON File Persistence** — Save and reload bookings from `data/bookings.csv` so data survives restarts.

---

## 📈 Learning Outcomes

By building and studying this project you will develop skills in:

1. **OOP Design** — Modelling real-world entities as encapsulated Java classes with clear responsibilities.
2. **Date Arithmetic** — Using `java.time` APIs to calculate durations and detect calendar overlaps.
3. **Precise Billing** — Using `BigDecimal` for financial calculations that are free of floating-point errors.
4. **Defensive Input Handling** — Writing loops that never crash on bad user input.
5. **Algorithm Design** — Implementing an interval overlap algorithm for double-booking prevention.
6. **Swing GUI Development** — Building a multi-screen desktop app using `CardLayout`, `JTable`, and custom renderers.
7. **Code Organisation** — Keeping a single-file app clean and readable for interview demonstrations.

---

## 👤 Author

- **Your Name** — Sufiyan Mangalgatti
- GitHub: https://github.com/sufiyanx25
- LinkedIn: www.linkedin.com/in/sufiyan-mangalgatti-73115a386


---

> **Repository Tags:** `java` `oop` `console-application` `hotel-management` `swing-gui`
> `backend-development` `java-se` `clean-code` `portfolio-project` `interview-ready`
