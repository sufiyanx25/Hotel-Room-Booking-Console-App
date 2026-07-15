# Screenshots to Scenario Outputs Mapping

This guide maps each generated terminal output file in the `outputs/` folder to the visual screenshot
checklist. It also defines what to capture for the new Java Swing GUI interface.

Save captured images into the `screenshots/` directory using the names in the table below.

---

## Part A — Console Version Screenshots

These screenshots should be taken while running `java -Dfile.encoding=UTF-8 -cp src HotelApp`
in a terminal that supports ANSI color (e.g., Windows Terminal, Git Bash, VS Code integrated terminal).

| File Name to Save               | Description                                             | Corresponding Output File                                       |
|---------------------------------|---------------------------------------------------------|-----------------------------------------------------------------|
| `ss_01_folder_tree.png`         | Directory tree showing all folders and files            | *(Run `tree /F` in the project root)*                           |
| `ss_02_welcome_banner.png`      | Green welcome banner printed on startup                 | [`outputs/scenario1_list_rooms.txt`](../outputs/scenario1_list_rooms.txt) |
| `ss_03_main_menu.png`           | Styled cyan HOTEL ROOM BOOKING SYSTEM menu panel        | [`outputs/scenario1_list_rooms.txt`](../outputs/scenario1_list_rooms.txt) |
| `ss_04_room_listing.png`        | Boxed table of all rooms with color-coded status        | [`outputs/scenario1_list_rooms.txt`](../outputs/scenario1_list_rooms.txt) |
| `ss_05_search_rooms.png`        | Room type picker + date inputs + available rooms table  | [`outputs/scenario2_search_rooms.txt`](../outputs/scenario2_search_rooms.txt) |
| `ss_06_create_booking.png`      | Booking creation flow + BOOKING DETAILS receipt box     | [`outputs/scenario3_create_booking.txt`](../outputs/scenario3_create_booking.txt) |
| `ss_07_overlap_blocked.png`     | Second attempt on same room showing "Pick one of"       | [`outputs/scenario4_overlapping_booking.txt`](../outputs/scenario4_overlapping_booking.txt) |
| `ss_08_view_booking.png`        | BOOKING DETAILS box when looking up booking ID 1001     | [`outputs/scenario5_view_booking.txt`](../outputs/scenario5_view_booking.txt) |
| `ss_09_cancel_booking.png`      | Green cancellation message + status flipped to CANCELLED| [`outputs/scenario6_cancel_booking.txt`](../outputs/scenario6_cancel_booking.txt) |
| `ss_10_check_in.png`            | Check-in success + room status OCCUPIED in room list    | [`outputs/scenario7_check_in.txt`](../outputs/scenario7_check_in.txt) |
| `ss_11_checkout_bill.png`       | Green boxed CHECKOUT BILL receipt with total ₹13200.00  | [`outputs/scenario8_check_out_bill.txt`](../outputs/scenario8_check_out_bill.txt) |
| `ss_12_invalid_date.png`        | Red error message when date format is wrong             | [`outputs/scenario9_invalid_date_format.txt`](../outputs/scenario9_invalid_date_format.txt) |
| `ss_13_date_logic_check.png`    | Red rejection when checkout is same or before check-in  | [`outputs/scenario10_checkout_before_checkin.txt`](../outputs/scenario10_checkout_before_checkin.txt) |
| `ss_14_invalid_id.png`          | "No booking found" error for ID 9999                    | [`outputs/scenario11_invalid_booking_id.txt`](../outputs/scenario11_invalid_booking_id.txt) |
| `ss_15_goodbye_banner.png`      | Green thank-you banner printed on exit                  | [`outputs/scenario8_check_out_bill.txt`](../outputs/scenario8_check_out_bill.txt) |
| `ss_16_github_repo.png`         | GitHub repository page showing README preview + topics  | *(Browser screenshot of your GitHub repo URL)*                  |

---

## Part B — GUI Version Screenshots (HotelAppGUI)

These screenshots should be taken while running `java -cp src HotelAppGUI`.

| File Name to Save               | Description                                             | How to Reach This Screen                                        |
|---------------------------------|---------------------------------------------------------|-----------------------------------------------------------------|
| `ss_gui_01_dashboard.png`       | Home dashboard with 4 menu cards                        | App launches here by default                                    |
| `ss_gui_02_room_grid.png`       | JTable showing all rooms with alternating rows          | Click "View Room Grid" card                                     |
| `ss_gui_03_search.png`          | Search form with date inputs and results table          | Click "Search Vacancy" card                                     |
| `ss_gui_04_new_booking.png`     | New booking form with room picker and voucher slip      | Click "New Booking" card and create a reservation               |
| `ss_gui_05_booking_voucher.png` | Filled confirmation card after booking is confirmed     | Complete the New Booking form and click "Confirm Booking"       |
| `ss_gui_06_manage_booking.png`  | Manage Bookings panel with details card on the right    | Click "Manage Bookings" card and enter a booking ID             |
| `ss_gui_07_check_in.png`        | Booking status updated to IN_HOUSE in green             | Find booking then click "Check In Guest"                        |
| `ss_gui_08_bill_checkout.png`   | Net total highlighted in blue accent after check-out    | Find an IN_HOUSE booking then click "Check Out & Print Bill"    |
| `ss_gui_09_inline_error.png`    | Red validation text shown below a form field            | Try submitting New Booking form with empty name or bad date     |
| `ss_gui_10_overlap_block.png`   | Room combo box stays empty when all rooms are taken     | Try searching overlapping dates for a fully booked room type    |
