import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Console Hotel Room Booking App
 * Single file for easy compilation in interviews
 */
public class HotelApp {

    // ----------- Domain -----------
    enum RoomType {
        SINGLE("Single", new BigDecimal("2000.00"), new BigDecimal("1.0")),
        DOUBLE("Double", new BigDecimal("2000.00"), new BigDecimal("1.2")),
        SUITE("Suite", new BigDecimal("2000.00"), new BigDecimal("1.6"));

        final String label;
        final BigDecimal base;
        final BigDecimal multiplier;

        RoomType(String label, BigDecimal base, BigDecimal multiplier) {
            this.label = label;
            this.base = base;
            this.multiplier = multiplier;
        }

        BigDecimal nightlyRate() {
            return base.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        }

        @Override public String toString() { return label; }
    }

    enum RoomStatus { VACANT, OCCUPIED, OUT_OF_SERVICE }

    static class Room {
        final int number;
        final RoomType type;
        RoomStatus status = RoomStatus.VACANT;
        Room(int number, RoomType type) {
            this.number = number;
            this.type = type;
        }
    }

    enum BookingStatus { RESERVED, IN_HOUSE, COMPLETED, CANCELLED }

    static class Booking {
        final int id;
        final int roomNumber;
        final String guestName;
        final LocalDate checkIn;
        final LocalDate checkOut;
        BookingStatus status = BookingStatus.RESERVED;
        final BigDecimal nightlyRate;
        final long nights;
        final BigDecimal roomCharge;
        final BigDecimal tax;
        final BigDecimal total;

        Booking(int id, int roomNumber, String guestName, LocalDate checkIn,
                LocalDate checkOut, BigDecimal nightlyRate, BigDecimal taxRate) {
            this.id = id;
            this.roomNumber = roomNumber;
            this.guestName = guestName;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.nightlyRate = nightlyRate.setScale(2, RoundingMode.HALF_UP);
            this.nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            this.roomCharge = this.nightlyRate.multiply(new BigDecimal(nights))
                    .setScale(2, RoundingMode.HALF_UP);
            this.tax = this.roomCharge.multiply(taxRate)
                    .setScale(2, RoundingMode.HALF_UP);
            this.total = this.roomCharge.add(this.tax)
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }

    // ----------- Storage -----------
    // Package-private to allow access from HotelAppGUI
    final Map<Integer, Room> rooms = new LinkedHashMap<>();
    final Map<Integer, Booking> bookings = new LinkedHashMap<>();
    int nextBookingId = 1001;

    // ----------- Config -----------
    static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ----------- ANSI Colors -----------
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String WHITE_BOLD = "\u001B[1;37m";

    // ----------- App entry -----------
    public static void main(String[] args) {
        HotelApp app = new HotelApp();
        app.seedSampleRooms();
        app.printWelcomeBanner();
        app.menuLoop();
    }

    void seedSampleRooms() {
        rooms.put(101, new Room(101, RoomType.SINGLE));
        rooms.put(102, new Room(102, RoomType.SINGLE));
        rooms.put(103, new Room(103, RoomType.SINGLE));
        rooms.put(201, new Room(201, RoomType.DOUBLE));
        rooms.put(202, new Room(202, RoomType.DOUBLE));
        rooms.put(203, new Room(203, RoomType.DOUBLE));
        rooms.put(301, new Room(301, RoomType.SUITE));
        rooms.put(302, new Room(302, RoomType.SUITE));
        rooms.put(303, new Room(303, RoomType.SUITE));
        rooms.put(304, new Room(304, RoomType.SUITE));
    }

    private void printWelcomeBanner() {
        System.out.println(GREEN + "┌──────────────────────────────────────────────────┐" + RESET);
        System.out.println(GREEN + "│        Welcome to Hotel Starlight System         │" + RESET);
        System.out.println(GREEN + "│           Your premium booking partner           │" + RESET);
        System.out.println(GREEN + "└──────────────────────────────────────────────────┘" + RESET);
    }

    private void printMenuHeader() {
        System.out.println(CYAN + "┌──────────────────────────────────────────────────┐" + RESET);
        System.out.println(CYAN + "│" + WHITE_BOLD + "           HOTEL ROOM BOOKING SYSTEM              " + CYAN + "│" + RESET);
        System.out.println(CYAN + "└──────────────────────────────────────────────────┘" + RESET);
    }

    // ----------- Menu -----------
    private final Scanner sc = new Scanner(System.in);

    private void menuLoop() {
        while (true) {
            System.out.println();
            printMenuHeader();
            System.out.println(CYAN + "  1. " + RESET + "List rooms");
            System.out.println(CYAN + "  2. " + RESET + "Search available rooms");
            System.out.println(CYAN + "  3. " + RESET + "Create booking");
            System.out.println(CYAN + "  4. " + RESET + "View booking by id");
            System.out.println(CYAN + "  5. " + RESET + "Cancel booking");
            System.out.println(CYAN + "  6. " + RESET + "Check in");
            System.out.println(CYAN + "  7. " + RESET + "Check out and print bill");
            System.out.println(CYAN + "  8. " + RED + "Exit" + RESET);
            System.out.println(CYAN + "────────────────────────────────────────────────────" + RESET);
            int choice = readInt(YELLOW + "Choose an option" + RESET, 1, 8);
            switch (choice) {
                case 1 -> listRooms();
                case 2 -> searchAvailable();
                case 3 -> createBookingFlow();
                case 4 -> viewBooking();
                case 5 -> cancelBooking();
                case 6 -> checkInFlow();
                case 7 -> checkOutFlow();
                case 8 -> {
                    System.out.println(GREEN + "\n┌──────────────────────────────────────────────────┐" + RESET);
                    System.out.println(GREEN + "│      Thank you for using Hotel Starlight!        │" + RESET);
                    System.out.println(GREEN + "│                   Goodbye!                       │" + RESET);
                    System.out.println(GREEN + "└──────────────────────────────────────────────────┘\n" + RESET);
                    return;
                }
            }
        }
    }

    // ----------- Features -----------
    private void listRooms() {
        System.out.println();
        System.out.println(CYAN + "┌──────────┬────────────┬──────────────┬──────────────┐" + RESET);
        System.out.printf(CYAN + "│" + WHITE_BOLD + " %-8s " + CYAN + "│" + WHITE_BOLD + " %-10s " + CYAN + "│" + WHITE_BOLD + " %-12s " + CYAN + "│" + WHITE_BOLD + " %-12s " + CYAN + "│%n" + RESET,
                "Number", "Type", "Status", "Rate");
        System.out.println(CYAN + "├──────────┼────────────┼──────────────┼──────────────┤" + RESET);
        for (Room r : rooms.values()) {
            String statusColor = r.status == RoomStatus.VACANT ? GREEN : (r.status == RoomStatus.OCCUPIED ? RED : YELLOW);
            System.out.printf(CYAN + "│" + RESET + " %-8d " + CYAN + "│" + RESET + " %-10s " + CYAN + "│" + statusColor + " %-12s " + CYAN + "│" + RESET + " %12s " + CYAN + "│%n" + RESET,
                    r.number, r.type, r.status, money(r.type.nightlyRate()));
        }
        System.out.println(CYAN + "└──────────┴────────────┴──────────────┴──────────────┘" + RESET);
    }

    private void searchAvailable() {
        RoomType type = pickRoomType();
        LocalDate in = readDate(YELLOW + "Enter check in date (yyyy-mm-dd):" + RESET);
        LocalDate out = readDate(YELLOW + "Enter check out date (yyyy-mm-dd):" + RESET);
        if (!validateDates(in, out)) return;
        List<Room> free = findAvailableRooms(type, in, out);
        if (free.isEmpty()) {
            System.out.println(RED + "No rooms available for given dates and type" + RESET);
        } else {
            System.out.println(GREEN + "\nAvailable rooms:" + RESET);
            System.out.println(CYAN + "┌──────────┬────────────┬──────────────┬──────────────┐" + RESET);
            System.out.printf(CYAN + "│" + WHITE_BOLD + " %-8s " + CYAN + "│" + WHITE_BOLD + " %-10s " + CYAN + "│" + WHITE_BOLD + " %-12s " + CYAN + "│" + WHITE_BOLD + " %-12s " + CYAN + "│%n" + RESET,
                    "Number", "Type", "Status", "Rate");
            System.out.println(CYAN + "├──────────┼────────────┼──────────────┼──────────────┤" + RESET);
            for (Room r : free) {
                System.out.printf(CYAN + "│" + RESET + " %-8d " + CYAN + "│" + RESET + " %-10s " + CYAN + "│" + GREEN + " %-12s " + CYAN + "│" + RESET + " %12s " + CYAN + "│%n" + RESET,
                        r.number, r.type, r.status, money(r.type.nightlyRate()));
            }
            System.out.println(CYAN + "└──────────┴────────────┴──────────────┴──────────────┘" + RESET);
        }
    }

    private void createBookingFlow() {
        RoomType type = pickRoomType();
        LocalDate in = readDate(YELLOW + "Enter check in date (yyyy-mm-dd):" + RESET);
        LocalDate out = readDate(YELLOW + "Enter check out date (yyyy-mm-dd):" + RESET);
        if (!validateDates(in, out)) return;
        List<Room> free = findAvailableRooms(type, in, out);
        if (free.isEmpty()) {
            System.out.println(RED + "No rooms available. Try different dates or type" + RESET);
            return;
        }
        System.out.println(GREEN + "\nPick a room number from the available list:" + RESET);
        System.out.println(CYAN + "┌──────────┬────────────┬──────────────┬──────────────┐" + RESET);
        System.out.printf(CYAN + "│" + WHITE_BOLD + " %-8s " + CYAN + "│" + WHITE_BOLD + " %-10s " + CYAN + "│" + WHITE_BOLD + " %-12s " + CYAN + "│" + WHITE_BOLD + " %-12s " + CYAN + "│%n" + RESET,
                "Number", "Type", "Status", "Rate");
        System.out.println(CYAN + "├──────────┼────────────┼──────────────┼──────────────┤" + RESET);
        for (Room r : free) {
            System.out.printf(CYAN + "│" + RESET + " %-8d " + CYAN + "│" + RESET + " %-10s " + CYAN + "│" + GREEN + " %-12s " + CYAN + "│" + RESET + " %12s " + CYAN + "│%n" + RESET,
                    r.number, r.type, r.status, money(r.type.nightlyRate()));
        }
        System.out.println(CYAN + "└──────────┴────────────┴──────────────┴──────────────┘" + RESET);

        int chosen = readInt(YELLOW + "Room number:" + RESET, free.stream().map(r -> r.number).toList());
        Room room = rooms.get(chosen);
        String guest = readNonEmpty(YELLOW + "Enter guest full name:" + RESET);
        int id = nextBookingId++;
        Booking b = new Booking(id, room.number, guest, in, out, room.type.nightlyRate(), TAX_RATE);
        bookings.put(id, b);
        System.out.println(GREEN + "Booking created with id " + id + RESET);
        printBooking(b);
    }

    private void viewBooking() {
        int id = readInt(YELLOW + "Enter booking id:" + RESET, 1, Integer.MAX_VALUE);
        Booking b = bookings.get(id);
        if (b == null) {
            System.out.println(RED + "No booking found" + RESET);
            return;
        }
        printBooking(b);
    }

    private void cancelBooking() {
        int id = readInt(YELLOW + "Enter booking id:" + RESET, 1, Integer.MAX_VALUE);
        Booking b = bookings.get(id);
        if (b == null) { System.out.println(RED + "No booking found" + RESET); return; }
        if (b.status != BookingStatus.RESERVED) {
            System.out.println(RED + "Only reserved bookings can be cancelled" + RESET);
            return;
        }
        b.status = BookingStatus.CANCELLED;
        System.out.println(GREEN + "Booking cancelled successfully" + RESET);
    }

    private void checkInFlow() {
        int id = readInt(YELLOW + "Enter booking id:" + RESET, 1, Integer.MAX_VALUE);
        Booking b = bookings.get(id);
        if (b == null) { System.out.println(RED + "No booking found" + RESET); return; }
        Room r = rooms.get(b.roomNumber);
        if (b.status != BookingStatus.RESERVED) {
            System.out.println(RED + "Only reserved bookings can be checked in" + RESET);
            return;
        }
        if (r.status != RoomStatus.VACANT) {
            System.out.println(RED + "Room is not vacant" + RESET);
            return;
        }
        b.status = BookingStatus.IN_HOUSE;
        r.status = RoomStatus.OCCUPIED;
        System.out.println(GREEN + "Checked in. Enjoy your stay!" + RESET);
    }

    private void checkOutFlow() {
        int id = readInt(YELLOW + "Enter booking id:" + RESET, 1, Integer.MAX_VALUE);
        Booking b = bookings.get(id);
        if (b == null) { System.out.println(RED + "No booking found" + RESET); return; }
        Room r = rooms.get(b.roomNumber);
        if (b.status != BookingStatus.IN_HOUSE) {
            System.out.println(RED + "Only in house bookings can be checked out" + RESET);
            return;
        }
        b.status = BookingStatus.COMPLETED;
        r.status = RoomStatus.VACANT;
        System.out.println(GREEN + "Checked out successfully. Bill summary:" + RESET);
        printBill(b);
    }

    // ----------- Helpers -----------
    private void printBooking(Booking b) {
        System.out.println();
        System.out.println(CYAN + "┌──────────────────────────────────────────────────┐" + RESET);
        System.out.println(CYAN + "│" + WHITE_BOLD + "                  BOOKING DETAILS                 " + CYAN + "│" + RESET);
        System.out.println(CYAN + "├──────────────────────────────────────────────────┤" + RESET);
        System.out.printf(CYAN + "│" + RESET + " Booking ID:    %-34s " + CYAN + "│%n" + RESET, String.valueOf(b.id));
        System.out.printf(CYAN + "│" + RESET + " Guest Name:    %-34s " + CYAN + "│%n" + RESET, b.guestName);
        System.out.printf(CYAN + "│" + RESET + " Room Number:   %-34s " + CYAN + "│%n" + RESET, String.valueOf(b.roomNumber));
        
        String statusColor = b.status == BookingStatus.RESERVED ? YELLOW : (b.status == BookingStatus.IN_HOUSE ? GREEN : BLUE);
        System.out.print(CYAN + "│" + RESET + " Status:        ");
        System.out.print(statusColor + String.format("%-34s", b.status) + RESET);
        System.out.println(CYAN + " │" + RESET);

        System.out.printf(CYAN + "│" + RESET + " Stay Dates:    %-34s " + CYAN + "│%n" + RESET, b.checkIn.format(DTF) + " to " + b.checkOut.format(DTF));
        System.out.printf(CYAN + "│" + RESET + " Total Nights:  %-34d " + CYAN + "│%n" + RESET, b.nights);
        System.out.println(CYAN + "├──────────────────────────────────────────────────┤" + RESET);
        System.out.printf(CYAN + "│" + RESET + " Nightly Rate:  %-34s " + CYAN + "│%n" + RESET, money(b.nightlyRate));
        System.out.printf(CYAN + "│" + RESET + " Room Charge:   %-34s " + CYAN + "│%n" + RESET, money(b.roomCharge));
        System.out.printf(CYAN + "│" + RESET + " Service Tax:   %-34s " + CYAN + "│%n" + RESET, money(b.tax));
        
        System.out.print(CYAN + "│" + GREEN + " Total Net:     ");
        System.out.print(GREEN + String.format("%-34s", money(b.total)) + RESET);
        System.out.println(CYAN + " │" + RESET);
        System.out.println(CYAN + "└──────────────────────────────────────────────────┘" + RESET);
    }

    private void printBill(Booking b) {
        System.out.println();
        System.out.println(GREEN + "┌──────────────────────────────────────────────────┐" + RESET);
        System.out.println(GREEN + "│" + WHITE_BOLD + "                  CHECKOUT BILL                   " + GREEN + "│" + RESET);
        System.out.println(GREEN + "├──────────────────────────────────────────────────┤" + RESET);
        System.out.printf(GREEN + "│" + RESET + " Room Number:   %-34d " + GREEN + "│%n" + RESET, b.roomNumber);
        System.out.printf(GREEN + "│" + RESET + " Guest Name:    %-34s " + GREEN + "│%n" + RESET, b.guestName);
        System.out.printf(GREEN + "│" + RESET + " Stay Duration: %-34s " + GREEN + "│%n" + RESET, b.checkIn.format(DTF) + " to " + b.checkOut.format(DTF));
        System.out.printf(GREEN + "│" + RESET + " Total Nights:  %-34d " + GREEN + "│%n" + RESET, b.nights);
        System.out.println(GREEN + "├──────────────────────────────────────────────────┤" + RESET);
        System.out.printf(GREEN + "│" + RESET + " Nightly Rate:  %34s " + GREEN + "│%n" + RESET, money(b.nightlyRate));
        System.out.printf(GREEN + "│" + RESET + " Room Charge:   %34s " + GREEN + "│%n" + RESET, money(b.roomCharge));
        System.out.printf(GREEN + "│" + RESET + " Tax (10%%):     %34s " + GREEN + "│%n" + RESET, money(b.tax));
        System.out.println(GREEN + "├──────────────────────────────────────────────────┤" + RESET);
        System.out.print(GREEN + "│" + WHITE_BOLD + " TOTAL AMOUNT: ");
        System.out.print(GREEN + String.format("%34s", money(b.total)) + RESET);
        System.out.println(GREEN + " │" + RESET);
        System.out.println(GREEN + "└──────────────────────────────────────────────────┘" + RESET);
    }

    BigDecimal round2(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    String money(BigDecimal v) {
        return "₹" + round2(v).toPlainString();
    }

    boolean validateDates(LocalDate in, LocalDate out) {
        if (!out.isAfter(in)) {
            System.out.println(RED + "Check out must be after check in" + RESET);
            return false;
        }
        return true;
    }

    List<Room> findAvailableRooms(RoomType type, LocalDate in, LocalDate out) {
        List<Room> candidates = new ArrayList<>();
        for (Room r : rooms.values()) {
            if (r.type == type && r.status != RoomStatus.OUT_OF_SERVICE) {
                candidates.add(r);
            }
        }
        List<Room> free = new ArrayList<>();
        outer:
        for (Room r : candidates) {
            for (Booking b : bookings.values()) {
                if (b.roomNumber == r.number && b.status != BookingStatus.CANCELLED &&
                        b.status != BookingStatus.COMPLETED) {
                    if (rangesOverlap(in, out, b.checkIn, b.checkOut)) {
                        continue outer;
                    }
                }
            }
            free.add(r);
        }
        return free;
    }

    boolean rangesOverlap(LocalDate aStart, LocalDate aEnd, LocalDate bStart, LocalDate bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    // ----------- Input readers -----------
    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " ");
            String s = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    System.out.println(RED + "Enter a number between " + min + " and " + max + RESET);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println(RED + "Enter a valid integer" + RESET);
            }
        }
    }

    private int readInt(String prompt, List<Integer> allowed) {
        Set<Integer> set = new HashSet<>(allowed);
        while (true) {
            System.out.print(prompt + " ");
            String s = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (!set.contains(v)) {
                    System.out.println(RED + "Pick one of " + allowed + RESET);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println(RED + "Enter a valid integer" + RESET);
            }
        }
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println(RED + "Value cannot be empty" + RESET);
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String s = sc.nextLine().trim();
            try {
                return LocalDate.parse(s, DTF);
            } catch (DateTimeParseException e) {
                System.out.println(RED + "Enter date in format yyyy-mm-dd like 2026-07-20" + RESET);
            }
        }
    }

    private RoomType pickRoomType() {
        System.out.println();
        System.out.println(CYAN + "┌──────────────────────────────────────────────────┐" + RESET);
        System.out.println(CYAN + "│" + WHITE_BOLD + "                CHOOSE ROOM TYPE                  " + CYAN + "│" + RESET);
        System.out.println(CYAN + "├──────────────────────────────────────────────────┤" + RESET);
        RoomType[] types = RoomType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.printf(CYAN + "│" + RESET + "  %d. %-15s Rate: %-21s " + CYAN + "│%n" + RESET,
                    i + 1, types[i], money(types[i].nightlyRate()));
        }
        System.out.println(CYAN + "└──────────────────────────────────────────────────┘" + RESET);
        int idx = readInt(YELLOW + "Option:" + RESET, 1, types.length);
        return types[idx - 1];
    }
}
