import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Vector;

/**
 * Java Swing GUI version of the Hotel Room Booking App.
 * Reuses the domain logic, entities, and collections of HotelApp directly.
 */
public class HotelAppGUI extends JFrame {

    // Reuse the backend engine directly
    private final HotelApp backend = new HotelApp();

    // UI Styles
    private final Color COLOR_BG = new Color(245, 245, 247);       // Apple light gray
    private final Color COLOR_CARD = new Color(255, 255, 255);     // Plain white panel
    private final Color COLOR_ACCENT = new Color(0, 122, 255);     // Apple iOS blue
    private final Color COLOR_TEXT = new Color(28, 28, 30);        // Dark gray text
    private final Color COLOR_SECONDARY = new Color(142, 142, 147);// Gray text
    private final Color COLOR_BORDER = new Color(229, 229, 234);   // Light gray line border
    private final Color COLOR_ERROR = new Color(255, 59, 48);      // Soft Red
    private final Color COLOR_SUCCESS = new Color(52, 199, 89);    // Soft Green

    private final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 24);
    private final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 14);

    // Card Layout Container
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel containerPanel = new JPanel(cardLayout);

    // Dynamic Components for View Rooms
    private DefaultTableModel modelRooms;

    public HotelAppGUI() {
        // Initialize backend and seed sample data
        backend.seedSampleRooms();

        // Setup Frame Settings
        setTitle("Hotel Starlight Management System");
        setSize(900, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        // Add Screens to Container
        containerPanel.add(createDashboardPanel(), "DASHBOARD");
        containerPanel.add(createViewRoomsPanel(), "VIEW_ROOMS");
        containerPanel.add(createSearchPanel(), "SEARCH_ROOMS");
        containerPanel.add(createNewBookingPanel(), "NEW_BOOKING");
        containerPanel.add(createManageBookingPanel(), "MANAGE_BOOKING");

        add(containerPanel);
        cardLayout.show(containerPanel, "DASHBOARD");
    }

    // Helper to format currency
    private String formatMoney(BigDecimal amount) {
        return backend.money(amount);
    }

    // ----------- Styling Helpers -----------
    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_ACCENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(0, 102, 220));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(COLOR_ACCENT);
            }
        });
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(COLOR_TEXT);
        btn.setBackground(new Color(229, 229, 234));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(209, 209, 214));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(229, 229, 234));
            }
        });
        return btn;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(FONT_INPUT);
        f.setForeground(COLOR_TEXT);
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return f;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(36);
        table.setGridColor(COLOR_BORDER);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(0, 122, 255, 30));
        table.setSelectionForeground(COLOR_TEXT);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(245, 245, 247));
        header.setForeground(COLOR_TEXT);
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, isSel, hasFoc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (!isSel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 252));
                }
                if (col == 2) { // Status column
                    String s = String.valueOf(val);
                    if ("VACANT".equals(s)) {
                        c.setForeground(COLOR_SUCCESS);
                    } else if ("OCCUPIED".equals(s)) {
                        c.setForeground(COLOR_ERROR);
                    } else {
                        c.setForeground(COLOR_SECONDARY);
                    }
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else {
                    c.setForeground(COLOR_TEXT);
                    setFont(new Font("Segoe UI", Font.PLAIN, 14));
                }
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
        return table;
    }

    private JPanel createHeaderPanel(String title, String tagline, String backCard) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_BG);
        p.setBorder(new EmptyBorder(25, 40, 15, 40));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        textPanel.setBackground(COLOR_BG);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_HEADER);
        titleLbl.setForeground(COLOR_TEXT);
        textPanel.add(titleLbl);

        JLabel tagLbl = new JLabel(tagline);
        tagLbl.setFont(FONT_SUBTITLE);
        tagLbl.setForeground(COLOR_SECONDARY);
        textPanel.add(tagLbl);

        p.add(textPanel, BorderLayout.WEST);

        if (backCard != null) {
            JButton backBtn = createSecondaryButton("Back to Menu");
            backBtn.addActionListener(e -> cardLayout.show(containerPanel, backCard));
            JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
            btnWrapper.setBackground(COLOR_BG);
            btnWrapper.add(backBtn);
            p.add(btnWrapper, BorderLayout.EAST);
        }
        return p;
    }

    // ----------- Screen 1: Dashboard Panel -----------
    private JPanel createDashboardPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG);

        // Header Panel
        root.add(createHeaderPanel("Hotel Starlight Management", "Select a task option below to manage operations", null), BorderLayout.NORTH);

        // Grid Menu Container
        JPanel gridPanel = new JPanel(new GridLayout(2, 4, 20, 20));
        gridPanel.setBackground(COLOR_BG);
        gridPanel.setBorder(new EmptyBorder(20, 40, 40, 40));

        JButton btnViewRooms = createMenuCard("View Room Grid", "List and check real-time hotel room status.", "🏨");
        btnViewRooms.addActionListener(e -> {
            refreshRoomsData();
            cardLayout.show(containerPanel, "VIEW_ROOMS");
        });
        gridPanel.add(btnViewRooms);

        JButton btnSearch = createMenuCard("Search Vacancy", "Find available rooms by date and category.", "🔍");
        btnSearch.addActionListener(e -> cardLayout.show(containerPanel, "SEARCH_ROOMS"));
        gridPanel.add(btnSearch);

        JButton btnBook = createMenuCard("New Booking", "Create a reservation for checking in guests.", "➕");
        btnBook.addActionListener(e -> cardLayout.show(containerPanel, "NEW_BOOKING"));
        gridPanel.add(btnBook);

        JButton btnManage = createMenuCard("Manage Bookings", "View, check-in, check-out, or cancel bookings.", "⚙️");
        btnManage.addActionListener(e -> cardLayout.show(containerPanel, "MANAGE_BOOKING"));
        gridPanel.add(btnManage);

        root.add(gridPanel, BorderLayout.CENTER);

        // Exit / Info footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(COLOR_BG);
        footer.setBorder(new EmptyBorder(0, 40, 30, 40));

        JButton btnExit = createSecondaryButton("Exit Application");
        btnExit.addActionListener(e -> System.exit(0));
        footer.add(btnExit, BorderLayout.WEST);

        JLabel verLbl = new JLabel("Application Version 1.1.0 LTS  |  JDK 25 SE");
        verLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        verLbl.setForeground(COLOR_SECONDARY);
        footer.add(verLbl, BorderLayout.EAST);

        root.add(footer, BorderLayout.SOUTH);
        return root;
    }

    private JButton createMenuCard(String title, String desc, String icon) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(5, 5));
        btn.setBackground(COLOR_CARD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        iconLbl.setBorder(new EmptyBorder(0, 0, 10, 0));
        btn.add(iconLbl, BorderLayout.NORTH);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        textPanel.setBackground(COLOR_CARD);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(COLOR_TEXT);
        textPanel.add(titleLbl);

        JLabel descLbl = new JLabel("<html><body style='width: 140px;'>" + desc + "</body></html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLbl.setForeground(COLOR_SECONDARY);
        textPanel.add(descLbl);

        btn.add(textPanel, BorderLayout.CENTER);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(250, 250, 252));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_ACCENT, 1),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLOR_CARD);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDER, 1),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
            }
        });
        return btn;
    }

    // ----------- Screen 2: View Rooms Panel -----------
    private JPanel createViewRoomsPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG);
        root.add(createHeaderPanel("Rooms Inventory", "Live status updates of room occupation", "DASHBOARD"), BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COLOR_BG);
        wrapper.setBorder(new EmptyBorder(10, 40, 40, 40));

        // JTable Configuration
        modelRooms = new DefaultTableModel(new String[]{"Room Number", "Category Type", "Current Status", "Base Price / Night"}, 0);
        JTable table = createStyledTable(modelRooms);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        wrapper.add(scroll, BorderLayout.CENTER);

        root.add(wrapper, BorderLayout.CENTER);
        return root;
    }

    private void refreshRoomsData() {
        modelRooms.setRowCount(0);
        for (HotelApp.Room r : backend.rooms.values()) {
            modelRooms.addRow(new Object[]{
                    r.number,
                    r.type.label,
                    r.status.name(),
                    formatMoney(r.type.nightlyRate())
            });
        }
    }

    // ----------- Screen 3: Search Panel -----------
    private JPanel createSearchPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG);
        root.add(createHeaderPanel("Search Available Rooms", "Query vacant rooms directly using custom date inputs", "DASHBOARD"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(COLOR_BG);
        content.setBorder(new EmptyBorder(10, 40, 40, 40));

        // Form Inputs Card (Left)
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(COLOR_CARD);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        formCard.setPreferredSize(new Dimension(320, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);

        // Room Type
        JLabel lblType = new JLabel("Room Category");
        lblType.setFont(FONT_LABEL);
        lblType.setForeground(COLOR_TEXT);
        formCard.add(lblType, gbc);

        gbc.gridy++;
        JComboBox<HotelApp.RoomType> comboType = new JComboBox<>(HotelApp.RoomType.values());
        comboType.setFont(FONT_INPUT);
        comboType.setBackground(Color.WHITE);
        formCard.add(comboType, gbc);

        // Check In
        gbc.gridy++;
        JLabel lblCheckIn = new JLabel("Check-In Date (yyyy-MM-dd)");
        lblCheckIn.setFont(FONT_LABEL);
        lblCheckIn.setForeground(COLOR_TEXT);
        formCard.add(lblCheckIn, gbc);

        gbc.gridy++;
        JTextField txtCheckIn = createStyledTextField("yyyy-MM-dd");
        txtCheckIn.setText(LocalDate.now().toString());
        formCard.add(txtCheckIn, gbc);

        // Check Out
        gbc.gridy++;
        JLabel lblCheckOut = new JLabel("Check-Out Date (yyyy-MM-dd)");
        lblCheckOut.setFont(FONT_LABEL);
        lblCheckOut.setForeground(COLOR_TEXT);
        formCard.add(lblCheckOut, gbc);

        gbc.gridy++;
        JTextField txtCheckOut = createStyledTextField("yyyy-MM-dd");
        txtCheckOut.setText(LocalDate.now().plusDays(1).toString());
        formCard.add(txtCheckOut, gbc);

        // Error message label
        gbc.gridy++;
        JLabel lblErr = new JLabel();
        lblErr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblErr.setForeground(COLOR_ERROR);
        formCard.add(lblErr, gbc);

        // Search Button
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        JButton btnSearch = createPrimaryButton("Find Available Rooms");
        formCard.add(btnSearch, gbc);

        content.add(formCard, BorderLayout.WEST);

        // Results Card (Right)
        JPanel resultsCard = new JPanel(new BorderLayout());
        resultsCard.setBackground(COLOR_CARD);
        resultsCard.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));

        DefaultTableModel modelResults = new DefaultTableModel(new String[]{"Room Number", "Room Type", "Nightly Rate"}, 0);
        JTable tableResults = createStyledTable(modelResults);
        JScrollPane scrollResults = new JScrollPane(tableResults);
        scrollResults.setBorder(BorderFactory.createEmptyBorder());
        scrollResults.getViewport().setBackground(Color.WHITE);

        resultsCard.add(scrollResults, BorderLayout.CENTER);
        content.add(resultsCard, BorderLayout.CENTER);

        root.add(content, BorderLayout.CENTER);

        // Search Trigger
        btnSearch.addActionListener(e -> {
            lblErr.setText("");
            modelResults.setRowCount(0);
            try {
                HotelApp.RoomType rt = (HotelApp.RoomType) comboType.getSelectedItem();
                LocalDate inDate = LocalDate.parse(txtCheckIn.getText().trim(), HotelApp.DTF);
                LocalDate outDate = LocalDate.parse(txtCheckOut.getText().trim(), HotelApp.DTF);

                if (!backend.validateDates(inDate, outDate)) {
                    lblErr.setText("Check-out must be after check-in.");
                    return;
                }

                List<HotelApp.Room> available = backend.findAvailableRooms(rt, inDate, outDate);
                if (available.isEmpty()) {
                    lblErr.setText("No vacancy for specified inputs.");
                } else {
                    for (HotelApp.Room r : available) {
                        modelResults.addRow(new Object[]{r.number, r.type.label, formatMoney(r.type.nightlyRate())});
                    }
                }
            } catch (DateTimeParseException ex) {
                lblErr.setText("Use date format: yyyy-MM-dd");
            }
        });

        return root;
    }

    // ----------- Screen 4: New Booking Panel -----------
    private JPanel createNewBookingPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG);
        root.add(createHeaderPanel("New Reservation Entry", "Reserve rooms by entering dates, types, and choosing numbers", "DASHBOARD"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(COLOR_BG);
        content.setBorder(new EmptyBorder(10, 40, 40, 40));

        // Form panel (Left)
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(COLOR_CARD);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        formCard.setPreferredSize(new Dimension(340, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);

        // Guest Name
        JLabel lblGuest = new JLabel("Guest Full Name");
        lblGuest.setFont(FONT_LABEL);
        lblGuest.setForeground(COLOR_TEXT);
        formCard.add(lblGuest, gbc);

        gbc.gridy++;
        JTextField txtGuest = createStyledTextField("John Doe");
        formCard.add(txtGuest, gbc);

        // Room Type
        gbc.gridy++;
        JLabel lblType = new JLabel("Room Category");
        lblType.setFont(FONT_LABEL);
        lblType.setForeground(COLOR_TEXT);
        formCard.add(lblType, gbc);

        gbc.gridy++;
        JComboBox<HotelApp.RoomType> comboType = new JComboBox<>(HotelApp.RoomType.values());
        comboType.setFont(FONT_INPUT);
        comboType.setBackground(Color.WHITE);
        formCard.add(comboType, gbc);

        // Dates
        gbc.gridy++;
        JLabel lblCheckIn = new JLabel("Check-In Date (yyyy-MM-dd)");
        lblCheckIn.setFont(FONT_LABEL);
        lblCheckIn.setForeground(COLOR_TEXT);
        formCard.add(lblCheckIn, gbc);

        gbc.gridy++;
        JTextField txtCheckIn = createStyledTextField("");
        txtCheckIn.setText(LocalDate.now().toString());
        formCard.add(txtCheckIn, gbc);

        gbc.gridy++;
        JLabel lblCheckOut = new JLabel("Check-Out Date (yyyy-MM-dd)");
        lblCheckOut.setFont(FONT_LABEL);
        lblCheckOut.setForeground(COLOR_TEXT);
        formCard.add(lblCheckOut, gbc);

        gbc.gridy++;
        JTextField txtCheckOut = createStyledTextField("");
        txtCheckOut.setText(LocalDate.now().plusDays(1).toString());
        formCard.add(txtCheckOut, gbc);

        // Query Available rooms button
        gbc.gridy++;
        JButton btnCheck = createSecondaryButton("Fetch Available Room Numbers");
        formCard.add(btnCheck, gbc);

        // Selection combo box
        gbc.gridy++;
        JLabel lblRooms = new JLabel("Select Room Number");
        lblRooms.setFont(FONT_LABEL);
        lblRooms.setForeground(COLOR_TEXT);
        formCard.add(lblRooms, gbc);

        gbc.gridy++;
        JComboBox<Integer> comboRooms = new JComboBox<>();
        comboRooms.setFont(FONT_INPUT);
        comboRooms.setBackground(Color.WHITE);
        formCard.add(comboRooms, gbc);

        // Error message
        gbc.gridy++;
        JLabel lblErr = new JLabel();
        lblErr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblErr.setForeground(COLOR_ERROR);
        formCard.add(lblErr, gbc);

        // Confirm Reservation
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        JButton btnConfirm = createPrimaryButton("Confirm Booking");
        btnConfirm.setEnabled(false);
        formCard.add(btnConfirm, gbc);

        content.add(formCard, BorderLayout.WEST);

        // Confirmation Card (Right)
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setBackground(COLOR_BG);

        JPanel slipCard = new JPanel(null);
        slipCard.setBackground(COLOR_CARD);
        slipCard.setPreferredSize(new Dimension(380, 480));
        slipCard.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        
        JLabel lblSlipTitle = new JLabel("RESERVATION VOUCHER", JLabel.CENTER);
        lblSlipTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSlipTitle.setBounds(0, 30, 380, 25);
        slipCard.add(lblSlipTitle);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 70, 320, 2);
        slipCard.add(sep);

        // Layout items inside the voucher card
        JLabel vBkg = new JLabel("Booking ID:");
        vBkg.setFont(FONT_LABEL); vBkg.setBounds(40, 90, 100, 20);
        slipCard.add(vBkg);
        JLabel valBkg = new JLabel("-");
        valBkg.setFont(FONT_INPUT); valBkg.setBounds(160, 90, 180, 20);
        slipCard.add(valBkg);

        JLabel vGuest = new JLabel("Guest Name:");
        vGuest.setFont(FONT_LABEL); vGuest.setBounds(40, 120, 100, 20);
        slipCard.add(vGuest);
        JLabel valGuest = new JLabel("-");
        valGuest.setFont(FONT_INPUT); valGuest.setBounds(160, 120, 180, 20);
        slipCard.add(valGuest);

        JLabel vRoom = new JLabel("Room Selected:");
        vRoom.setFont(FONT_LABEL); vRoom.setBounds(40, 150, 100, 20);
        slipCard.add(vRoom);
        JLabel valRoom = new JLabel("-");
        valRoom.setFont(FONT_INPUT); valRoom.setBounds(160, 150, 180, 20);
        slipCard.add(valRoom);

        JLabel vDates = new JLabel("Stay Duration:");
        vDates.setFont(FONT_LABEL); vDates.setBounds(40, 180, 100, 20);
        slipCard.add(vDates);
        JLabel valDates = new JLabel("-");
        valDates.setFont(FONT_INPUT); valDates.setBounds(160, 180, 180, 20);
        slipCard.add(valDates);

        JLabel vNights = new JLabel("Total Nights:");
        vNights.setFont(FONT_LABEL); vNights.setBounds(40, 210, 100, 20);
        slipCard.add(vNights);
        JLabel valNights = new JLabel("-");
        valNights.setFont(FONT_INPUT); valNights.setBounds(160, 210, 180, 20);
        slipCard.add(valNights);

        JSeparator sep2 = new JSeparator();
        sep2.setBounds(30, 250, 320, 2);
        slipCard.add(sep2);

        JLabel vRate = new JLabel("Nightly Rate:");
        vRate.setFont(FONT_LABEL); vRate.setBounds(40, 270, 120, 20);
        slipCard.add(vRate);
        JLabel valRate = new JLabel("-");
        valRate.setFont(FONT_INPUT); valRate.setHorizontalAlignment(JLabel.RIGHT); valRate.setBounds(200, 270, 140, 20);
        slipCard.add(valRate);

        JLabel vCharge = new JLabel("Room Charge:");
        vCharge.setFont(FONT_LABEL); vCharge.setBounds(40, 300, 120, 20);
        slipCard.add(vCharge);
        JLabel valCharge = new JLabel("-");
        valCharge.setFont(FONT_INPUT); valCharge.setHorizontalAlignment(JLabel.RIGHT); valCharge.setBounds(200, 300, 140, 20);
        slipCard.add(valCharge);

        JLabel vTax = new JLabel("Service Tax (10%):");
        vTax.setFont(FONT_LABEL); vTax.setBounds(40, 330, 120, 20);
        slipCard.add(vTax);
        JLabel valTax = new JLabel("-");
        valTax.setFont(FONT_INPUT); valTax.setHorizontalAlignment(JLabel.RIGHT); valTax.setBounds(200, 330, 140, 20);
        slipCard.add(valTax);

        JSeparator sep3 = new JSeparator();
        sep3.setBounds(30, 370, 320, 2);
        slipCard.add(sep3);

        JLabel vTotal = new JLabel("TOTAL AMOUNT:");
        vTotal.setFont(new Font("Segoe UI", Font.BOLD, 15)); vTotal.setForeground(COLOR_ACCENT); vTotal.setBounds(40, 390, 140, 25);
        slipCard.add(vTotal);
        JLabel valTotal = new JLabel("-");
        valTotal.setFont(new Font("Segoe UI", Font.BOLD, 16)); valTotal.setForeground(COLOR_ACCENT); valTotal.setHorizontalAlignment(JLabel.RIGHT); valTotal.setBounds(180, 390, 160, 25);
        slipCard.add(valTotal);

        cardWrapper.add(slipCard);
        content.add(cardWrapper, BorderLayout.CENTER);

        root.add(content, BorderLayout.CENTER);

        // Fetch vacant action
        btnCheck.addActionListener(e -> {
            lblErr.setText("");
            comboRooms.removeAllItems();
            btnConfirm.setEnabled(false);
            try {
                HotelApp.RoomType rt = (HotelApp.RoomType) comboType.getSelectedItem();
                LocalDate inDate = LocalDate.parse(txtCheckIn.getText().trim(), HotelApp.DTF);
                LocalDate outDate = LocalDate.parse(txtCheckOut.getText().trim(), HotelApp.DTF);

                if (!backend.validateDates(inDate, outDate)) {
                    lblErr.setText("Check-out must be after check-in.");
                    return;
                }

                List<HotelApp.Room> available = backend.findAvailableRooms(rt, inDate, outDate);
                if (available.isEmpty()) {
                    lblErr.setText("No vacancy for specified dates.");
                } else {
                    for (HotelApp.Room r : available) {
                        comboRooms.addItem(r.number);
                    }
                    btnConfirm.setEnabled(true);
                }
            } catch (DateTimeParseException ex) {
                lblErr.setText("Use date format: yyyy-MM-dd");
            }
        });

        // Booking confirmation action
        btnConfirm.addActionListener(e -> {
            lblErr.setText("");
            String name = txtGuest.getText().trim();
            if (name.isEmpty()) {
                lblErr.setText("Guest name cannot be empty.");
                return;
            }

            try {
                Integer roomNum = (Integer) comboRooms.getSelectedItem();
                if (roomNum == null) {
                    lblErr.setText("Please check vacant rooms first.");
                    return;
                }

                LocalDate inDate = LocalDate.parse(txtCheckIn.getText().trim(), HotelApp.DTF);
                LocalDate outDate = LocalDate.parse(txtCheckOut.getText().trim(), HotelApp.DTF);

                // Double check overlap check
                List<HotelApp.Room> freeList = backend.findAvailableRooms((HotelApp.RoomType) comboType.getSelectedItem(), inDate, outDate);
                if (!freeList.stream().anyMatch(r -> r.number == roomNum)) {
                    lblErr.setText("Room is no longer available.");
                    return;
                }

                HotelApp.Room chosenRoom = backend.rooms.get(roomNum);
                int bkgId = backend.nextBookingId++;
                HotelApp.Booking booking = new HotelApp.Booking(bkgId, chosenRoom.number, name, inDate, outDate, chosenRoom.type.nightlyRate(), HotelApp.TAX_RATE);
                backend.bookings.put(bkgId, booking);

                // Populate slip card
                valBkg.setText(String.valueOf(booking.id));
                valGuest.setText(booking.guestName);
                valRoom.setText(booking.roomNumber + " (" + chosenRoom.type.label + ")");
                valDates.setText(booking.checkIn + " to " + booking.checkOut);
                valNights.setText(booking.nights + " Nights");
                valRate.setText(formatMoney(booking.nightlyRate));
                valCharge.setText(formatMoney(booking.roomCharge));
                valTax.setText(formatMoney(booking.tax));
                valTotal.setText(formatMoney(booking.total));

                lblErr.setText("Booking successful!");
                lblErr.setForeground(COLOR_SUCCESS);
                btnConfirm.setEnabled(false);
            } catch (Exception ex) {
                lblErr.setText("Execution failed. Check fields.");
                lblErr.setForeground(COLOR_ERROR);
            }
        });

        return root;
    }

    // ----------- Screen 5: Manage Bookings Panel (View/Cancel/CheckIn/CheckOut) -----------
    private JPanel createManageBookingPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG);
        root.add(createHeaderPanel("Manage Reservations", "Search bookings by ID to perform check-in, check-out, and cancellation operations", "DASHBOARD"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(COLOR_BG);
        content.setBorder(new EmptyBorder(10, 40, 40, 40));

        // Form lookup (Left)
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(COLOR_CARD);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        formCard.setPreferredSize(new Dimension(320, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);

        JLabel lblId = new JLabel("Enter Booking ID");
        lblId.setFont(FONT_LABEL);
        lblId.setForeground(COLOR_TEXT);
        formCard.add(lblId, gbc);

        gbc.gridy++;
        JTextField txtId = createStyledTextField("e.g. 1001");
        formCard.add(txtId, gbc);

        gbc.gridy++;
        JButton btnFind = createPrimaryButton("Find Booking Details");
        formCard.add(btnFind, gbc);

        gbc.gridy++;
        JSeparator sep = new JSeparator();
        formCard.add(sep, gbc);

        // Context Action Buttons
        gbc.gridy++;
        JButton btnCheckIn = createSecondaryButton("Check In Guest");
        btnCheckIn.setEnabled(false);
        formCard.add(btnCheckIn, gbc);

        gbc.gridy++;
        JButton btnCheckOut = createSecondaryButton("Check Out Guest & Print Bill");
        btnCheckOut.setEnabled(false);
        formCard.add(btnCheckOut, gbc);

        gbc.gridy++;
        JButton btnCancel = createSecondaryButton("Cancel Booking");
        btnCancel.setEnabled(false);
        formCard.add(btnCancel, gbc);

        gbc.gridy++;
        JLabel lblErr = new JLabel();
        lblErr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblErr.setForeground(COLOR_ERROR);
        formCard.add(lblErr, gbc);

        content.add(formCard, BorderLayout.WEST);

        // Details Display Card (Right)
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setBackground(COLOR_BG);

        JPanel detailCard = new JPanel(null);
        detailCard.setBackground(COLOR_CARD);
        detailCard.setPreferredSize(new Dimension(380, 480));
        detailCard.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));

        JLabel lblDetailTitle = new JLabel("BOOKING DOCUMENTATION", JLabel.CENTER);
        lblDetailTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblDetailTitle.setBounds(0, 30, 380, 25);
        detailCard.add(lblDetailTitle);

        JSeparator sepVoucher = new JSeparator();
        sepVoucher.setBounds(30, 70, 320, 2);
        detailCard.add(sepVoucher);

        // Labels
        JLabel dId = new JLabel("Booking ID:"); dId.setFont(FONT_LABEL); dId.setBounds(40, 95, 100, 20); detailCard.add(dId);
        JLabel valId = new JLabel("-"); valId.setFont(FONT_INPUT); valId.setBounds(160, 95, 180, 20); detailCard.add(valId);

        JLabel dStatus = new JLabel("Booking Status:"); dStatus.setFont(FONT_LABEL); dStatus.setBounds(40, 125, 100, 20); detailCard.add(dStatus);
        JLabel valStatus = new JLabel("-"); valStatus.setFont(new Font("Segoe UI", Font.BOLD, 14)); valStatus.setBounds(160, 125, 180, 20); detailCard.add(valStatus);

        JLabel dGuest = new JLabel("Guest Name:"); dGuest.setFont(FONT_LABEL); dGuest.setBounds(40, 155, 100, 20); detailCard.add(dGuest);
        JLabel valGuest = new JLabel("-"); valGuest.setFont(FONT_INPUT); valGuest.setBounds(160, 155, 180, 20); detailCard.add(valGuest);

        JLabel dRoom = new JLabel("Room Selected:"); dRoom.setFont(FONT_LABEL); dRoom.setBounds(40, 185, 100, 20); detailCard.add(dRoom);
        JLabel valRoom = new JLabel("-"); valRoom.setFont(FONT_INPUT); valRoom.setBounds(160, 185, 180, 20); detailCard.add(valRoom);

        JLabel dDates = new JLabel("Stay Dates:"); dDates.setFont(FONT_LABEL); dDates.setBounds(40, 215, 100, 20); detailCard.add(dDates);
        JLabel valDates = new JLabel("-"); valDates.setFont(FONT_INPUT); valDates.setBounds(160, 215, 180, 20); detailCard.add(valDates);

        JLabel dNights = new JLabel("Total Nights:"); dNights.setFont(FONT_LABEL); dNights.setBounds(40, 245, 100, 20); detailCard.add(dNights);
        JLabel valNights = new JLabel("-"); valNights.setFont(FONT_INPUT); valNights.setBounds(160, 245, 180, 20); detailCard.add(valNights);

        JSeparator sepVoucher2 = new JSeparator();
        sepVoucher2.setBounds(30, 285, 320, 2);
        detailCard.add(sepVoucher2);

        JLabel dRate = new JLabel("Nightly Rate:"); dRate.setFont(FONT_LABEL); dRate.setBounds(40, 310, 120, 20); detailCard.add(dRate);
        JLabel valRate = new JLabel("-"); valRate.setFont(FONT_INPUT); valRate.setHorizontalAlignment(JLabel.RIGHT); valRate.setBounds(200, 310, 140, 20); detailCard.add(valRate);

        JLabel dCharge = new JLabel("Room Cost:"); dCharge.setFont(FONT_LABEL); dCharge.setBounds(40, 340, 120, 20); detailCard.add(dCharge);
        JLabel valCharge = new JLabel("-"); valCharge.setFont(FONT_INPUT); valCharge.setHorizontalAlignment(JLabel.RIGHT); valCharge.setBounds(200, 340, 140, 20); detailCard.add(valCharge);

        JLabel dTax = new JLabel("Tax Charge (10%):"); dTax.setFont(FONT_LABEL); dTax.setBounds(40, 370, 120, 20); detailCard.add(dTax);
        JLabel valTax = new JLabel("-"); valTax.setFont(FONT_INPUT); valTax.setHorizontalAlignment(JLabel.RIGHT); valTax.setBounds(200, 370, 140, 20); detailCard.add(valTax);

        JSeparator sepVoucher3 = new JSeparator();
        sepVoucher3.setBounds(30, 405, 320, 2);
        detailCard.add(sepVoucher3);

        JLabel dTotal = new JLabel("NET TOTAL BILL:"); dTotal.setFont(new Font("Segoe UI", Font.BOLD, 15)); dTotal.setForeground(COLOR_ACCENT); dTotal.setBounds(40, 420, 140, 25); detailCard.add(dTotal);
        JLabel valTotal = new JLabel("-"); valTotal.setFont(new Font("Segoe UI", Font.BOLD, 16)); valTotal.setForeground(COLOR_ACCENT); valTotal.setHorizontalAlignment(JLabel.RIGHT); valTotal.setBounds(180, 420, 160, 25); detailCard.add(valTotal);

        cardWrapper.add(detailCard);
        content.add(cardWrapper, BorderLayout.CENTER);

        root.add(content, BorderLayout.CENTER);

        // Find Logic
        btnFind.addActionListener(e -> {
            lblErr.setText("");
            lblErr.setForeground(COLOR_ERROR);
            btnCheckIn.setEnabled(false);
            btnCheckOut.setEnabled(false);
            btnCancel.setEnabled(false);

            try {
                int id = Integer.parseInt(txtId.getText().trim());
                HotelApp.Booking b = backend.bookings.get(id);
                if (b == null) {
                    lblErr.setText("Booking ID not found.");
                    clearDetailsCard(valId, valStatus, valGuest, valRoom, valDates, valNights, valRate, valCharge, valTax, valTotal);
                    return;
                }

                // Render fields
                HotelApp.Room r = backend.rooms.get(b.roomNumber);
                valId.setText(String.valueOf(b.id));
                valStatus.setText(b.status.name());
                if (b.status == HotelApp.BookingStatus.RESERVED) valStatus.setForeground(Color.ORANGE);
                else if (b.status == HotelApp.BookingStatus.IN_HOUSE) valStatus.setForeground(COLOR_SUCCESS);
                else valStatus.setForeground(COLOR_SECONDARY);

                valGuest.setText(b.guestName);
                valRoom.setText(b.roomNumber + " (" + r.type.label + ")");
                valDates.setText(b.checkIn + " to " + b.checkOut);
                valNights.setText(b.nights + " Nights");
                valRate.setText(formatMoney(b.nightlyRate));
                valCharge.setText(formatMoney(b.roomCharge));
                valTax.setText(formatMoney(b.tax));
                valTotal.setText(formatMoney(b.total));

                // Configure context buttons
                if (b.status == HotelApp.BookingStatus.RESERVED) {
                    btnCheckIn.setEnabled(true);
                    btnCancel.setEnabled(true);
                } else if (b.status == HotelApp.BookingStatus.IN_HOUSE) {
                    btnCheckOut.setEnabled(true);
                }
            } catch (NumberFormatException ex) {
                lblErr.setText("Please enter a numeric ID.");
            }
        });

        // Check In logic
        btnCheckIn.addActionListener(e -> {
            int id = Integer.parseInt(valId.getText());
            HotelApp.Booking b = backend.bookings.get(id);
            HotelApp.Room r = backend.rooms.get(b.roomNumber);

            if (r.status != HotelApp.RoomStatus.VACANT) {
                lblErr.setText("Selected room is not vacant.");
                return;
            }

            b.status = HotelApp.BookingStatus.IN_HOUSE;
            r.status = HotelApp.RoomStatus.OCCUPIED;
            valStatus.setText(b.status.name());
            valStatus.setForeground(COLOR_SUCCESS);
            lblErr.setText("Checked in. Room status updated.");
            lblErr.setForeground(COLOR_SUCCESS);

            btnCheckIn.setEnabled(false);
            btnCancel.setEnabled(false);
            btnCheckOut.setEnabled(true);
        });

        // Cancel logic
        btnCancel.addActionListener(e -> {
            int id = Integer.parseInt(valId.getText());
            HotelApp.Booking b = backend.bookings.get(id);
            b.status = HotelApp.BookingStatus.CANCELLED;
            valStatus.setText(b.status.name());
            valStatus.setForeground(COLOR_SECONDARY);
            lblErr.setText("Booking cancelled.");
            lblErr.setForeground(COLOR_SUCCESS);

            btnCheckIn.setEnabled(false);
            btnCancel.setEnabled(false);
        });

        // Check out logic
        btnCheckOut.addActionListener(e -> {
            int id = Integer.parseInt(valId.getText());
            HotelApp.Booking b = backend.bookings.get(id);
            HotelApp.Room r = backend.rooms.get(b.roomNumber);

            b.status = HotelApp.BookingStatus.COMPLETED;
            r.status = HotelApp.RoomStatus.VACANT;

            valStatus.setText(b.status.name());
            valStatus.setForeground(COLOR_SECONDARY);
            lblErr.setText("Checked out successfully.");
            lblErr.setForeground(COLOR_SUCCESS);

            btnCheckOut.setEnabled(false);
        });

        return root;
    }

    private void clearDetailsCard(JLabel... labels) {
        for (JLabel l : labels) {
            l.setText("-");
            l.setForeground(COLOR_TEXT);
        }
    }

    // ----------- App Launcher -----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Apply a cleaner System look and feel if available
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new HotelAppGUI().setVisible(true);
        });
    }
}
