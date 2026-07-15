# GitHub Strategy & Day-Wise Proof-Building Plan

This document outlines the professional GitHub repository layout, git best practices, and a structured, day-wise plan to construct your project commit-by-commit to present a stellar portfolio to recruiters.

---

## 1. Repository Configuration & Metadata

To make your repository highly discoverable and professional, use these configurations:

*   **Best Repository Name**: `hotel-room-booking-console-app` (use lowercase with hyphens for clean URL styling).
*   **Best Project Description**: 
    > "An industry-ready, object-oriented Java console application designed for hotel room reservation, booking management, and local file storage. Implements OOP principles, input validation, and date-handling libraries."
*   **Best GitHub Tags / Topics**:
    `java`, `oop`, `console-application`, `hotel-management`, `backend-development`, `file-handling`, `data-persistence`, `software-architecture`, `java-se`, `clean-code`

---

## 2. Professional Commit Guidelines

### Commits Best Practices
*   **Write Atomic Commits**: Commit one logical change at a time (e.g., separating domain models from storage handlers).
*   **Use Gitignore**: Never commit compiled `.class` files, IntelliJ `.idea/` folders, Eclipse `.metadata/`, or user settings. Our `.gitignore` is already set up to filter these.
*   **No Sensitive/Personal Data**: Use sample data (e.g., guest names like "John Doe", fake contact numbers like "9876543210") in your CSV seed files. Never check in personal phone numbers or credentials.
*   **Follow Conventional Commits**:
    *   `feat: ...` for new features (e.g., implementing booking logic).
    *   `fix: ...` for bug fixes (e.g., fixing date parsing issues).
    *   `docs: ...` for changes to documentation (e.g., README updates).
    *   `chore: ...` for general tasks (e.g., gitignore setup, folder initialization).
    *   `test: ...` for adding verification runs or mock tests.

---

## 3. Day-Wise Proof-Building Plan

Follow this day-wise schedule to structure your commits. Each day requires committing specific files, using a clean commit message, and capturing key screenshots to build a visually engaging "Proof of Work" section in your repository.

```
  Day 1        Day 2         Day 3         Day 4          Day 5         Day 6        Day 7
+-------+    +-------+     +-------+     +-------+      +-------+     +-------+    +-------+
| Setup | -> |Models | --> |Booking| --> |Booking| ---> |Persistence| | CLI   | -> |Testing|
| Docs  |    |Room/Gst|    |Manager|     |Logic  |      | CSV File  | | Menu  |    |Final  |
+-------+    +-------+     +-------+     +-------+      +-------+     +-------+    +-------+
```

### Day 1: Project Setup & Repository Architecture
*   **Goal**: Establish folders, configure Git, write structural architecture documents, and set up placeholders.
*   **Files to Commit**:
    - `.gitignore`
    - `README.md`
    - `docs/architecture.md`
    - `docs/github_guide.md`
    - `docs/interview_prep.md`
    - `src/HotelApp.java` (placeholder)
*   **Commit Message**: `chore: initialize directory structure, gitignore, and project documentation`
*   **Screenshots/Proof to Capture**:
    1.  Terminal tree output showing the empty folders and placeholder files.
    2.  Github repository creation view with description and tags.

### Day 2: Core Domain Models (Encapsulation)
*   **Goal**: Implement the model classes `Room.java` and `Guest.java` with encapsulation, private fields, constructors, getters, setters, and `toString()` helpers.
*   **Files to Commit**:
    - `src/Room.java`
    - `src/Guest.java`
*   **Commit Message**: `feat: implement Room and Guest domain models with encapsulation`
*   **Screenshots/Proof to Capture**:
    3.  Code snippet showing encapsulated variables and JavaDoc headers in `Room.java`.

### Day 3: Booking Model & BookingManager Skeleton
*   **Goal**: Design the composite `Booking.java` entity (linking Room and Guest) and initialize the `BookingManager.java` class which acts as the control layer using `ArrayList` and `HashMap` in memory.
*   **Files to Commit**:
    - `src/Booking.java`
    - `src/BookingManager.java`
*   **Commit Message**: `feat: create Booking model and initialize BookingManager collection structures`
*   **Screenshots/Proof to Capture**:
    4.  Compilation check screen showing zero build errors for classes created so far.

### Day 4: Core Business Logic (Dates & Calculations)
*   **Goal**: Write date handling calculations (`ChronoUnit.DAYS.between`) and pricing algorithms (base rates, tax computations). Code the room search engine filter in `BookingManager.java`.
*   **Files to Commit**:
    - `src/BookingManager.java` (updated logic)
*   **Commit Message**: `feat: implement room availability search and booking price calculation logic`
*   **Screenshots/Proof to Capture**:
    5.  A small console diagnostic run (or a temporary test main execution) printing a calculated bill layout.

### Day 5: Local Data Persistence (File Handling)
*   **Goal**: Implement the file read/write logic in `FileHandler.java` to parse lists from CSV files (`rooms.csv`, `bookings.csv`) on startup and write changes upon exit.
*   **Files to Commit**:
    - `src/FileHandler.java`
    - `data/rooms.csv` (pre-seeded with standard rooms)
    - `data/bookings.csv` (initialized empty)
*   **Commit Message**: `feat: add FileHandler persistence layer for rooms and bookings CSV data`
*   **Screenshots/Proof to Capture**:
    6.  The seeded `data/rooms.csv` file structure open in the text editor.

### Day 6: Console UI Dashboard Integration
*   **Goal**: Replace the placeholder in `HotelApp.java` with the core console dashboard menu loop. Handle standard inputs, map menu choices to service controllers, and implement safe scanner closing.
*   **Files to Commit**:
    - `src/HotelApp.java` (implemented)
*   **Commit Message**: `feat: integrate CLI menu loop dashboard in HotelApp main class`
*   **Screenshots/Proof to Capture**:
    7.  Terminal capture showing the running dashboard console menu.
    8.  Terminal capture of the Room Search output.
    9.  Terminal capture of the Booking Confirmation invoice receipt.

### Day 7: Edge-Case Handling, Testing, and Final Release
*   **Goal**: Secure input validation (exception handling for dates, letters in number prompts) and write negative check tests (e.g., booking check-out before check-in). Run compilation checks.
*   **Files to Commit**:
    - `src/BookingManager.java` (robust exceptions added)
    - `docs/walkthrough.md` (new verification document)
*   **Commit Message**: `test: implement robust input exception handlers and complete manual validation test plan`
*   **Screenshots/Proof to Capture**:
    10. Terminal capture showing successful validation rejection (e.g. typing "ABC" for phone number or choosing a booked room).
    11. GitHub landing page showing full documentation, complete folder structure, and commit logs.

---

## 4. Visual Evidence / Screenshot Checklist
For the project README or portfolio write-up, you will capture the following:
1.  **Folder Structure**: Directory tree showing `src/`, `data/`, `outputs/`, `screenshots/`, `docs/`.
2.  **Main CLI Menu**: Beautiful ASCII welcome board and the enumerated list of actions.
3.  **Room Listing**: Active layout of pre-seeded hotel rooms (Room Number, Category, Price, Status).
4.  **Room Search**: Input parameters for dates and types, followed by filtered unoccupied rooms.
5.  **Guest Input Form**: CLI console prompts collecting guest metadata.
6.  **Invoice Summary**: Detailed cost layout including base price, calculated nights, tax breakdown, and total cost.
7.  **Booking History**: Stored bookings read from CSV database displayed in a clean tabular view.
8.  **Cancellation Success**: Proof of selecting a Booking ID, cancelling it, and seeing room status revert to "Available".
9.  **Error Validations**: Capture of the console catching invalid date inputs or letters typed into numeric inputs.
10. **Repository Page**: Preview of your GitHub repository displaying the project directory, README, and code topics.
