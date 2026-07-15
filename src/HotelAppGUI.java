import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * HotelAppGUI — Colorful Professional Dashboard GUI
 * Reuses all business logic from HotelApp.java.
 * Design: Deep Navy sidebar + Amber accent + Light gray content area.
 */
public class HotelAppGUI extends JFrame {

    // ─── Backend ──────────────────────────────────────────────────────────────
    private final HotelApp backend = new HotelApp();

    // ─── Color Palette ────────────────────────────────────────────────────────
    static final Color C_NAVY        = new Color(0x1A2B4A);  // Sidebar primary
    static final Color C_NAVY_DARK   = new Color(0x111E35);  // Sidebar gradient end
    static final Color C_NAVY_HOVER  = new Color(0x253A5E);  // Nav item hover
    static final Color C_AMBER       = new Color(0xF59E0B);  // Accent / buttons
    static final Color C_AMBER_DARK  = new Color(0xD97706);  // Accent hover
    static final Color C_BG          = new Color(0xF0F2F5);  // Page background
    static final Color C_CARD        = Color.WHITE;           // Card surfaces
    static final Color C_TEXT        = new Color(0x1A1A2E);  // Primary text
    static final Color C_MUTED       = new Color(0x6B7280);  // Secondary text
    static final Color C_BORDER      = new Color(0xE5E7EB);  // Card borders
    static final Color C_VACANT      = new Color(0x10B981);  // Emerald green
    static final Color C_OCCUPIED    = new Color(0xEF4444);  // Coral red
    static final Color C_OOS         = new Color(0x9CA3AF);  // Gray out-of-service
    static final Color C_PILL_SINGLE = new Color(0x3B82F6);  // Blue — Single rooms
    static final Color C_PILL_DOUBLE = new Color(0x8B5CF6);  // Purple — Double rooms
    static final Color C_PILL_SUITE  = new Color(0xF59E0B);  // Amber — Suite rooms

    // ─── Fonts ────────────────────────────────────────────────────────────────
    static final Font F_HEADING  = new Font("Segoe UI", Font.BOLD, 22);
    static final Font F_SUBHEAD  = new Font("Segoe UI", Font.BOLD, 15);
    static final Font F_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    static final Font F_NAV      = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_BTN      = new Font("Segoe UI", Font.BOLD, 13);
    static final Font F_RECEIPT  = new Font("Segoe UI", Font.PLAIN, 13);

    // ─── Layout ───────────────────────────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     contentArea = new JPanel(cardLayout);
    private final String[] SCREENS = {
            "DASHBOARD","ROOMS","SEARCH","NEW_BOOKING","MANAGE"
    };
    private NavButton activeNav = null;

    // ─── Dynamic Models ───────────────────────────────────────────────────────
    private DefaultTableModel modelRooms;
    private DefaultTableModel modelSearch;

    // ─── Constructor ──────────────────────────────────────────────────────────
    public HotelAppGUI() {
        backend.seedSampleRooms();

        setTitle("Hotel Starlight  —  Management System");
        setSize(1050, 710);
        setMinimumSize(new Dimension(900, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.add(buildSidebar(), BorderLayout.WEST);

        contentArea.setBackground(C_BG);
        contentArea.add(buildDashboard(),   "DASHBOARD");
        contentArea.add(buildRoomsPanel(),  "ROOMS");
        contentArea.add(buildSearchPanel(), "SEARCH");
        contentArea.add(buildNewBookingPanel(), "NEW_BOOKING");
        contentArea.add(buildManagePanel(), "MANAGE");
        root.add(contentArea, BorderLayout.CENTER);

        setContentPane(root);
        showScreen("DASHBOARD");
    }

    // ─── Navigation ───────────────────────────────────────────────────────────
    private void showScreen(String name) {
        cardLayout.show(contentArea, name);
        if (name.equals("ROOMS")) refreshRoomTable();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SIDEBAR
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        // Gradient sidebar using custom paint
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, C_NAVY, 0, getHeight(), C_NAVY_DARK));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setOpaque(false);

        // Logo / App Name
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setBorder(new EmptyBorder(28, 20, 20, 20));
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));

        JLabel logoIcon = new JLabel("🏨");
        logoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        logoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.add(logoIcon);
        logoPanel.add(Box.createVerticalStrut(6));

        JLabel appName = new JLabel("Hotel Starlight");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.add(appName);

        JLabel tagline = new JLabel("Management System");
        tagline.setFont(F_SMALL);
        tagline.setForeground(new Color(0xA0AEC0));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.add(tagline);

        sidebar.add(logoPanel, BorderLayout.NORTH);

        // Nav Items
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel sectionLbl = new JLabel("NAVIGATION");
        sectionLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        sectionLbl.setForeground(new Color(0x718096));
        sectionLbl.setBorder(new EmptyBorder(0, 8, 8, 0));
        navPanel.add(sectionLbl);

        NavButton btnDash   = new NavButton("⬛  Dashboard",     "DASHBOARD");
        NavButton btnRooms  = new NavButton("🏠  View Rooms",    "ROOMS");
        NavButton btnSearch = new NavButton("🔍  Search Rooms",  "SEARCH");
        NavButton btnNew    = new NavButton("➕  New Booking",   "NEW_BOOKING");
        NavButton btnManage = new NavButton("⚙   Manage Booking","MANAGE");

        for (NavButton nb : new NavButton[]{btnDash, btnRooms, btnSearch, btnNew, btnManage}) {
            nb.addActionListener(e -> {
                setActive(nb);
                showScreen(nb.screen);
            });
            navPanel.add(nb);
            navPanel.add(Box.createVerticalStrut(4));
        }
        setActive(btnDash);
        sidebar.add(navPanel, BorderLayout.CENTER);

        // Exit at bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 12, 24, 12));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x2D4A6B));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        bottomPanel.add(sep);
        bottomPanel.add(Box.createVerticalStrut(12));

        NavButton btnExit = new NavButton("🚪  Exit", null);
        btnExit.addActionListener(e -> System.exit(0));
        bottomPanel.add(btnExit);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);
        return sidebar;
    }

    private void setActive(NavButton btn) {
        if (activeNav != null) activeNav.setActive(false);
        activeNav = btn;
        btn.setActive(true);
    }

    // ─── Custom Nav Button ─────────────────────────────────────────────────
    class NavButton extends JButton {
        final String screen;
        private boolean isActive = false;

        NavButton(String text, String screen) {
            super(text);
            this.screen = screen;
            setFont(F_NAV);
            setForeground(new Color(0xCBD5E0));
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 12, 10, 12));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    if (!isActive) setForeground(Color.WHITE);
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (!isActive) setForeground(new Color(0xCBD5E0));
                }
            });
        }

        void setActive(boolean active) {
            this.isActive = active;
            setForeground(active ? C_AMBER : new Color(0xCBD5E0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (isActive) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xF59E0B, false) {
                    { /* Transparent amber background */ }
                });
                g2.setColor(new Color(245, 158, 11, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Accent left bar
                g2.setColor(C_AMBER);
                g2.fillRoundRect(0, 6, 3, getHeight() - 12, 3, 3);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SCREEN 1 — DASHBOARD
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildDashboard() {
        JPanel root = screenWrapper();

        // Header
        root.add(pageHeader("Welcome back! 👋", "Manage your hotel operations from one place."), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(0, 32, 32, 32));

        // ── Stat Cards Row ──
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        long totalRooms    = backend.rooms.size();
        long vacantRooms   = backend.rooms.values().stream()
                .filter(r -> r.status == HotelApp.RoomStatus.VACANT).count();
        long occupiedRooms = backend.rooms.values().stream()
                .filter(r -> r.status == HotelApp.RoomStatus.OCCUPIED).count();

        statsRow.add(statCard("🏠", "Total Rooms",    String.valueOf(totalRooms),    C_NAVY));
        statsRow.add(statCard("✅", "Rooms Vacant",   String.valueOf(vacantRooms),   C_VACANT));
        statsRow.add(statCard("🔴", "Rooms Occupied", String.valueOf(occupiedRooms), C_OCCUPIED));

        body.add(Box.createVerticalStrut(8));
        body.add(statsRow);
        body.add(Box.createVerticalStrut(28));

        // ── Quick Action Label ──
        JLabel quickLbl = new JLabel("Quick Actions");
        quickLbl.setFont(F_SUBHEAD);
        quickLbl.setForeground(C_TEXT);
        quickLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(quickLbl);
        body.add(Box.createVerticalStrut(12));

        // ── Quick Action Cards ──
        JPanel actionRow = new JPanel(new GridLayout(1, 3, 16, 0));
        actionRow.setOpaque(false);
        actionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        actionRow.add(quickAction("🔍", "Search Rooms",   "Check room availability by date",    () -> { showScreen("SEARCH");      if(activeNav!=null){/*update nav*/}}));
        actionRow.add(quickAction("➕", "New Booking",    "Reserve a room for a guest",          () -> showScreen("NEW_BOOKING")));
        actionRow.add(quickAction("⚙",  "Manage Booking", "Check-in, check-out, or cancel",      () -> showScreen("MANAGE")));

        body.add(actionRow);

        root.add(body, BorderLayout.CENTER);
        return root;
    }

    private JPanel statCard(String icon, String label, String value, Color accent) {
        JPanel card = card();
        card.setLayout(new BorderLayout(10, 0));

        JPanel left = new JPanel(new GridLayout(3, 1, 2, 2));
        left.setOpaque(false);
        left.setBorder(new EmptyBorder(16, 20, 16, 0));

        JLabel icn = new JLabel(icon);
        icn.setFont(new Font("Segoe UI", Font.PLAIN, 26));
        left.add(icn);

        JLabel lbl = new JLabel(label);
        lbl.setFont(F_SMALL);
        lbl.setForeground(C_MUTED);
        left.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 28));
        val.setForeground(accent);
        left.add(val);

        card.add(left, BorderLayout.CENTER);
        return card;
    }

    private JPanel quickAction(String icon, String title, String desc, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(C_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icnLbl = new JLabel(icon);
        icnLbl.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        card.add(icnLbl, BorderLayout.WEST);

        JPanel txt = new JPanel(new GridLayout(2, 1, 2, 2));
        txt.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setForeground(C_TEXT);
        JLabel d = new JLabel(desc);
        d.setFont(F_SMALL);
        d.setForeground(C_MUTED);
        txt.add(t); txt.add(d);
        card.add(txt, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(new Color(0xFFFBEB)); }
            @Override public void mouseExited(MouseEvent e)  { card.setBackground(C_CARD); }
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
        });
        return card;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SCREEN 2 — VIEW ROOMS
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildRoomsPanel() {
        JPanel root = screenWrapper();
        root.add(pageHeader("Room Inventory", "Live status of all 10 hotel rooms."), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(0, 32, 32, 32));

        modelRooms = new DefaultTableModel(
                new String[]{"  Room No.", "  Room Type", "  Status", "  Rate / Night"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = styledTable(modelRooms);
        // Custom renderer for status and type pills
        table.getColumnModel().getColumn(1).setCellRenderer(new TypePillRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new StatusPillRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);

        JScrollPane scroll = styledScroll(table);
        JPanel card = card();
        card.setLayout(new BorderLayout());
        card.add(scroll, BorderLayout.CENTER);

        body.add(card, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
        return root;
    }

    private void refreshRoomTable() {
        if (modelRooms == null) return;
        modelRooms.setRowCount(0);
        for (HotelApp.Room r : backend.rooms.values()) {
            modelRooms.addRow(new Object[]{
                    "  " + r.number,
                    r.type.label,
                    r.status.name(),
                    "  " + backend.money(r.type.nightlyRate())
            });
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SCREEN 3 — SEARCH AVAILABILITY
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildSearchPanel() {
        JPanel root = screenWrapper();
        root.add(pageHeader("Search Availability", "Find vacant rooms for specific dates and room type."), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(0, 32, 32, 32));

        // ── Left form card ──
        JPanel formCard = card();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setPreferredSize(new Dimension(300, 0));
        formCard.setBorder(new CompoundBorder(formCard.getBorder(), new EmptyBorder(24, 24, 24, 24)));

        formCard.add(sectionLabel("Room Type"));
        formCard.add(Box.createVerticalStrut(6));
        JComboBox<HotelApp.RoomType> cboType = styledCombo(HotelApp.RoomType.values());
        cboType.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(cboType);

        formCard.add(Box.createVerticalStrut(16));
        formCard.add(sectionLabel("Check-In Date  (yyyy-MM-dd)"));
        formCard.add(Box.createVerticalStrut(6));
        JTextField txtIn = styledField(LocalDate.now().toString());
        txtIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(txtIn);

        formCard.add(Box.createVerticalStrut(16));
        formCard.add(sectionLabel("Check-Out Date  (yyyy-MM-dd)"));
        formCard.add(Box.createVerticalStrut(6));
        JTextField txtOut = styledField(LocalDate.now().plusDays(1).toString());
        txtOut.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(txtOut);

        formCard.add(Box.createVerticalStrut(8));
        JLabel errLbl = errorLabel();
        formCard.add(errLbl);

        formCard.add(Box.createVerticalStrut(16));
        JButton btnSearch = accentButton("🔍  Search Rooms");
        btnSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnSearch.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(btnSearch);
        formCard.add(Box.createVerticalGlue());

        // ── Right results card ──
        JPanel resultsCard = card();
        resultsCard.setLayout(new BorderLayout());

        modelSearch = new DefaultTableModel(
                new String[]{"  Room No.", "  Room Type", "  Rate / Night"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tResults = styledTable(modelSearch);
        tResults.getColumnModel().getColumn(1).setCellRenderer(new TypePillRenderer());
        resultsCard.add(styledScroll(tResults), BorderLayout.CENTER);

        JLabel resultsHdr = new JLabel("  Available Rooms");
        resultsHdr.setFont(F_SUBHEAD);
        resultsHdr.setForeground(C_TEXT);
        resultsHdr.setBorder(new EmptyBorder(14, 16, 14, 0));
        resultsCard.add(resultsHdr, BorderLayout.NORTH);

        body.add(formCard,   BorderLayout.WEST);
        body.add(resultsCard, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> {
            errLbl.setText(" ");
            modelSearch.setRowCount(0);
            try {
                HotelApp.RoomType rt = (HotelApp.RoomType) cboType.getSelectedItem();
                LocalDate in  = LocalDate.parse(txtIn.getText().trim(),  HotelApp.DTF);
                LocalDate out = LocalDate.parse(txtOut.getText().trim(), HotelApp.DTF);
                if (!backend.validateDates(in, out)) { errLbl.setText("Check-out must be after check-in."); return; }
                List<HotelApp.Room> avail = backend.findAvailableRooms(rt, in, out);
                if (avail.isEmpty()) { errLbl.setText("No rooms available for those dates."); return; }
                for (HotelApp.Room r : avail) {
                    modelSearch.addRow(new Object[]{"  " + r.number, r.type.label, "  " + backend.money(r.type.nightlyRate())});
                }
            } catch (DateTimeParseException ex) { errLbl.setText("Use date format: yyyy-MM-dd"); }
        });

        return root;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SCREEN 4 — NEW BOOKING
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildNewBookingPanel() {
        JPanel root = screenWrapper();
        root.add(pageHeader("New Booking", "Reserve a room for your guest."), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(0, 32, 32, 32));

        // ── Form Card ──
        JPanel formCard = card();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setPreferredSize(new Dimension(310, 0));
        formCard.setBorder(new CompoundBorder(formCard.getBorder(), new EmptyBorder(24, 24, 24, 24)));

        formCard.add(sectionLabel("Guest Full Name"));
        formCard.add(Box.createVerticalStrut(6));
        JTextField txtGuest = styledField("e.g. John Doe");
        txtGuest.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(txtGuest);

        formCard.add(Box.createVerticalStrut(16));
        formCard.add(sectionLabel("Room Category"));
        formCard.add(Box.createVerticalStrut(6));
        JComboBox<HotelApp.RoomType> cboType = styledCombo(HotelApp.RoomType.values());
        cboType.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(cboType);

        formCard.add(Box.createVerticalStrut(16));
        formCard.add(sectionLabel("Check-In Date  (yyyy-MM-dd)"));
        formCard.add(Box.createVerticalStrut(6));
        JTextField txtIn = styledField(LocalDate.now().toString());
        txtIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(txtIn);

        formCard.add(Box.createVerticalStrut(16));
        formCard.add(sectionLabel("Check-Out Date  (yyyy-MM-dd)"));
        formCard.add(Box.createVerticalStrut(6));
        JTextField txtOut = styledField(LocalDate.now().plusDays(1).toString());
        txtOut.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(txtOut);

        formCard.add(Box.createVerticalStrut(12));
        JButton btnFetch = ghostButton("Check Available Rooms");
        btnFetch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnFetch.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(btnFetch);

        formCard.add(Box.createVerticalStrut(12));
        formCard.add(sectionLabel("Select Room Number"));
        formCard.add(Box.createVerticalStrut(6));
        JComboBox<Integer> cboRooms = new JComboBox<>();
        cboRooms.setFont(F_BODY);
        cboRooms.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(cboRooms);

        formCard.add(Box.createVerticalStrut(8));
        JLabel errLbl = errorLabel();
        formCard.add(errLbl);

        formCard.add(Box.createVerticalStrut(12));
        JButton btnConfirm = accentButton("✅  Confirm Booking");
        btnConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnConfirm.setEnabled(false);
        btnConfirm.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(btnConfirm);
        formCard.add(Box.createVerticalGlue());

        // ── Voucher Card ──
        JPanel voucherCard = card();
        voucherCard.setLayout(new BorderLayout());

        JPanel voucherHeader = new JPanel();
        voucherHeader.setBackground(C_NAVY);
        voucherHeader.setBorder(new EmptyBorder(18, 20, 18, 20));
        voucherHeader.setLayout(new BoxLayout(voucherHeader, BoxLayout.Y_AXIS));

        JLabel vTitle = new JLabel("BOOKING CONFIRMATION");
        vTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        vTitle.setForeground(Color.WHITE);
        JLabel vSub = new JLabel("Hotel Starlight — Room Reservation");
        vSub.setFont(F_SMALL);
        vSub.setForeground(new Color(0xA0AEC0));
        voucherHeader.add(vTitle);
        voucherHeader.add(Box.createVerticalStrut(4));
        voucherHeader.add(vSub);
        voucherCard.add(voucherHeader, BorderLayout.NORTH);

        // Voucher body
        JPanel vBody = new JPanel();
        vBody.setBackground(C_CARD);
        vBody.setLayout(new BoxLayout(vBody, BoxLayout.Y_AXIS));
        vBody.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Voucher data rows
        JLabel[] vLabels = new JLabel[7];
        String[] vKeys = {"Booking ID", "Guest Name", "Room No.", "Check-In", "Check-Out", "Nights", ""};
        JLabel[] vVals  = new JLabel[7];
        for (int i = 0; i < 6; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(4, 0, 4, 0));
            vLabels[i] = new JLabel(vKeys[i]);
            vLabels[i].setFont(F_SMALL);
            vLabels[i].setForeground(C_MUTED);
            vVals[i] = new JLabel("—");
            vVals[i].setFont(F_BODY);
            vVals[i].setForeground(C_TEXT);
            row.add(vLabels[i], BorderLayout.WEST);
            row.add(vVals[i],   BorderLayout.EAST);
            vBody.add(row);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        }

        // Separator
        JSeparator hSep = new JSeparator();
        hSep.setForeground(C_BORDER);
        hSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        vBody.add(Box.createVerticalStrut(8));
        vBody.add(hSep);
        vBody.add(Box.createVerticalStrut(8));

        // Bill rows
        String[] billKeys = {"Nightly Rate", "Room Charge", "Tax (10%)"};
        JLabel[] billVals = new JLabel[3];
        for (int i = 0; i < 3; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(3, 0, 3, 0));
            JLabel k = new JLabel(billKeys[i]);
            k.setFont(F_SMALL);
            k.setForeground(C_MUTED);
            billVals[i] = new JLabel("—");
            billVals[i].setFont(F_BODY);
            billVals[i].setForeground(C_TEXT);
            row.add(k,           BorderLayout.WEST);
            row.add(billVals[i], BorderLayout.EAST);
            vBody.add(row);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        }

        vBody.add(Box.createVerticalStrut(10));

        // Total row
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setBackground(new Color(0xFFFBEB));
        totalRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xFDE68A), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));
        JLabel totalKey = new JLabel("TOTAL AMOUNT");
        totalKey.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalKey.setForeground(C_NAVY);
        JLabel totalVal = new JLabel("—");
        totalVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalVal.setForeground(C_AMBER);
        totalRow.add(totalKey, BorderLayout.WEST);
        totalRow.add(totalVal, BorderLayout.EAST);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        vBody.add(totalRow);

        voucherCard.add(vBody, BorderLayout.CENTER);

        body.add(formCard,    BorderLayout.WEST);
        body.add(voucherCard, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        // ── Actions ──
        btnFetch.addActionListener(e -> {
            errLbl.setText(" ");
            cboRooms.removeAllItems();
            btnConfirm.setEnabled(false);
            try {
                HotelApp.RoomType rt = (HotelApp.RoomType) cboType.getSelectedItem();
                LocalDate in  = LocalDate.parse(txtIn.getText().trim(),  HotelApp.DTF);
                LocalDate out = LocalDate.parse(txtOut.getText().trim(), HotelApp.DTF);
                if (!backend.validateDates(in, out)) { errLbl.setText("Check-out must be after check-in."); return; }
                List<HotelApp.Room> avail = backend.findAvailableRooms(rt, in, out);
                if (avail.isEmpty()) { errLbl.setText("No rooms vacant for those dates."); return; }
                avail.forEach(r -> cboRooms.addItem(r.number));
                btnConfirm.setEnabled(true);
            } catch (DateTimeParseException ex) { errLbl.setText("Date format: yyyy-MM-dd"); }
        });

        btnConfirm.addActionListener(e -> {
            errLbl.setText(" ");
            String name = txtGuest.getText().trim();
            if (name.isEmpty()) { errLbl.setText("Guest name cannot be empty."); return; }
            Integer roomNum = (Integer) cboRooms.getSelectedItem();
            if (roomNum == null) { errLbl.setText("Please fetch available rooms first."); return; }
            try {
                HotelApp.RoomType rt = (HotelApp.RoomType) cboType.getSelectedItem();
                LocalDate in  = LocalDate.parse(txtIn.getText().trim(),  HotelApp.DTF);
                LocalDate out = LocalDate.parse(txtOut.getText().trim(), HotelApp.DTF);
                // Re-validate overlap before confirming
                List<HotelApp.Room> avail = backend.findAvailableRooms(rt, in, out);
                if (avail.stream().noneMatch(r -> r.number == roomNum)) {
                    errLbl.setText("Room no longer available. Please re-fetch.");
                    return;
                }
                HotelApp.Room chosen = backend.rooms.get(roomNum);
                int bId = backend.nextBookingId++;
                HotelApp.Booking b = new HotelApp.Booking(bId, chosen.number, name, in, out,
                        chosen.type.nightlyRate(), HotelApp.TAX_RATE);
                backend.bookings.put(bId, b);

                // Populate voucher
                vVals[0].setText(String.valueOf(b.id));
                vVals[1].setText(b.guestName);
                vVals[2].setText(String.valueOf(b.roomNumber));
                vVals[3].setText(b.checkIn.format(HotelApp.DTF));
                vVals[4].setText(b.checkOut.format(HotelApp.DTF));
                vVals[5].setText(b.nights + " nights");
                billVals[0].setText(backend.money(b.nightlyRate));
                billVals[1].setText(backend.money(b.roomCharge));
                billVals[2].setText(backend.money(b.tax));
                totalVal.setText(backend.money(b.total));

                errLbl.setForeground(C_VACANT);
                errLbl.setText("✓  Booking " + bId + " confirmed!");
                btnConfirm.setEnabled(false);
            } catch (Exception ex) {
                errLbl.setForeground(C_OCCUPIED);
                errLbl.setText("Booking failed — check your inputs.");
            }
        });

        return root;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SCREEN 5 — MANAGE BOOKING (View / Cancel / Check-In / Check-Out)
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildManagePanel() {
        JPanel root = screenWrapper();
        root.add(pageHeader("Manage Booking", "View, cancel, check-in or check-out a reservation by Booking ID."), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(20, 0));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(0, 32, 32, 32));

        // ── Control Card ──
        JPanel ctrlCard = card();
        ctrlCard.setLayout(new BoxLayout(ctrlCard, BoxLayout.Y_AXIS));
        ctrlCard.setPreferredSize(new Dimension(290, 0));
        ctrlCard.setBorder(new CompoundBorder(ctrlCard.getBorder(), new EmptyBorder(24, 24, 24, 24)));

        ctrlCard.add(sectionLabel("Booking ID"));
        ctrlCard.add(Box.createVerticalStrut(6));
        JTextField txtId = styledField("e.g. 1001");
        txtId.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        ctrlCard.add(txtId);

        ctrlCard.add(Box.createVerticalStrut(14));
        JButton btnFind = accentButton("🔍  Find Booking");
        btnFind.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnFind.setAlignmentX(Component.LEFT_ALIGNMENT);
        ctrlCard.add(btnFind);

        ctrlCard.add(Box.createVerticalStrut(20));
        JSeparator sep = new JSeparator();
        sep.setForeground(C_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        ctrlCard.add(sep);
        ctrlCard.add(Box.createVerticalStrut(16));

        JLabel actionsLbl = new JLabel("ACTIONS");
        actionsLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        actionsLbl.setForeground(C_MUTED);
        ctrlCard.add(actionsLbl);
        ctrlCard.add(Box.createVerticalStrut(10));

        JButton btnCheckIn  = ghostButton("✈  Check In Guest");
        JButton btnCheckOut = ghostButton("🏁  Check Out & Bill");
        JButton btnCancel   = dangerButton("✖  Cancel Booking");

        for (JButton b : new JButton[]{btnCheckIn, btnCheckOut, btnCancel}) {
            b.setEnabled(false);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            ctrlCard.add(b);
            ctrlCard.add(Box.createVerticalStrut(8));
        }

        ctrlCard.add(Box.createVerticalStrut(4));
        JLabel errLbl = errorLabel();
        ctrlCard.add(errLbl);
        ctrlCard.add(Box.createVerticalGlue());

        // ── Detail / Receipt Card ──
        JPanel detailCard = card();
        detailCard.setLayout(new BorderLayout());

        JPanel detailHeader = new JPanel();
        detailHeader.setBackground(C_NAVY);
        detailHeader.setBorder(new EmptyBorder(18, 20, 18, 20));
        detailHeader.setLayout(new BoxLayout(detailHeader, BoxLayout.Y_AXIS));
        JLabel dtTitle = new JLabel("BOOKING RECORD");
        dtTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dtTitle.setForeground(Color.WHITE);
        JLabel dtSub = new JLabel("Reservation details and billing summary");
        dtSub.setFont(F_SMALL);
        dtSub.setForeground(new Color(0xA0AEC0));
        detailHeader.add(dtTitle);
        detailHeader.add(Box.createVerticalStrut(4));
        detailHeader.add(dtSub);
        detailCard.add(detailHeader, BorderLayout.NORTH);

        // Detail body
        JPanel dtBody = new JPanel();
        dtBody.setBackground(C_CARD);
        dtBody.setLayout(new BoxLayout(dtBody, BoxLayout.Y_AXIS));
        dtBody.setBorder(new EmptyBorder(20, 24, 20, 24));

        String[] dtKeys = {"Booking ID","Status","Guest Name","Room No.","Check-In","Check-Out","Nights"};
        JLabel[] dtVals = new JLabel[dtKeys.length];
        for (int i = 0; i < dtKeys.length; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(4, 0, 4, 0));
            JLabel k = new JLabel(dtKeys[i]);
            k.setFont(F_SMALL);
            k.setForeground(C_MUTED);
            dtVals[i] = new JLabel("—");
            dtVals[i].setFont(i == 1 ? new Font("Segoe UI", Font.BOLD, 13) : F_BODY);
            dtVals[i].setForeground(C_TEXT);
            row.add(k, BorderLayout.WEST);
            row.add(dtVals[i], BorderLayout.EAST);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            dtBody.add(row);
        }

        dtBody.add(Box.createVerticalStrut(8));
        JSeparator sep2 = new JSeparator(); sep2.setForeground(C_BORDER);
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        dtBody.add(sep2);
        dtBody.add(Box.createVerticalStrut(8));

        String[] billKeys = {"Nightly Rate","Room Charge","Tax (10%)"};
        JLabel[] billVals = new JLabel[3];
        for (int i = 0; i < 3; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(3, 0, 3, 0));
            JLabel k = new JLabel(billKeys[i]);
            k.setFont(F_SMALL);
            k.setForeground(C_MUTED);
            billVals[i] = new JLabel("—");
            billVals[i].setFont(F_BODY);
            billVals[i].setForeground(C_TEXT);
            row.add(k, BorderLayout.WEST);
            row.add(billVals[i], BorderLayout.EAST);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
            dtBody.add(row);
        }
        dtBody.add(Box.createVerticalStrut(10));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setBackground(new Color(0xFFFBEB));
        totalRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xFDE68A), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));
        JLabel totalKey = new JLabel("TOTAL AMOUNT");
        totalKey.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalKey.setForeground(C_NAVY);
        JLabel totalVal = new JLabel("—");
        totalVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalVal.setForeground(C_AMBER);
        totalRow.add(totalKey, BorderLayout.WEST);
        totalRow.add(totalVal, BorderLayout.EAST);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        dtBody.add(totalRow);

        detailCard.add(dtBody, BorderLayout.CENTER);

        body.add(ctrlCard,   BorderLayout.WEST);
        body.add(detailCard, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        // ── Find logic ──
        btnFind.addActionListener(e -> {
            errLbl.setText(" "); errLbl.setForeground(C_OCCUPIED);
            btnCheckIn.setEnabled(false); btnCheckOut.setEnabled(false); btnCancel.setEnabled(false);
            for (JLabel l : dtVals)   l.setText("—");
            for (JLabel l : billVals) l.setText("—");
            totalVal.setText("—");
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                HotelApp.Booking b = backend.bookings.get(id);
                if (b == null) { errLbl.setText("No booking found for ID " + id + "."); return; }
                HotelApp.Room r = backend.rooms.get(b.roomNumber);

                dtVals[0].setText(String.valueOf(b.id));
                dtVals[1].setText(b.status.name());
                dtVals[1].setForeground(
                        b.status == HotelApp.BookingStatus.RESERVED  ? C_AMBER  :
                        b.status == HotelApp.BookingStatus.IN_HOUSE  ? C_VACANT :
                        b.status == HotelApp.BookingStatus.COMPLETED ? C_MUTED  : C_OCCUPIED);
                dtVals[2].setText(b.guestName);
                dtVals[3].setText(String.valueOf(b.roomNumber));
                dtVals[4].setText(b.checkIn.format(HotelApp.DTF));
                dtVals[5].setText(b.checkOut.format(HotelApp.DTF));
                dtVals[6].setText(b.nights + " nights");
                billVals[0].setText(backend.money(b.nightlyRate));
                billVals[1].setText(backend.money(b.roomCharge));
                billVals[2].setText(backend.money(b.tax));
                totalVal.setText(backend.money(b.total));

                if (b.status == HotelApp.BookingStatus.RESERVED)  { btnCheckIn.setEnabled(true);  btnCancel.setEnabled(true); }
                if (b.status == HotelApp.BookingStatus.IN_HOUSE)  { btnCheckOut.setEnabled(true); }
                errLbl.setForeground(C_VACANT);
                errLbl.setText("✓  Booking loaded.");
            } catch (NumberFormatException ex) { errLbl.setText("Please enter a numeric booking ID."); }
        });

        btnCheckIn.addActionListener(e -> {
            int id = Integer.parseInt(txtId.getText().trim());
            HotelApp.Booking b = backend.bookings.get(id);
            HotelApp.Room    r = backend.rooms.get(b.roomNumber);
            if (r.status != HotelApp.RoomStatus.VACANT) { errLbl.setText("Room is not vacant."); return; }
            b.status = HotelApp.BookingStatus.IN_HOUSE;
            r.status = HotelApp.RoomStatus.OCCUPIED;
            dtVals[1].setText("IN_HOUSE"); dtVals[1].setForeground(C_VACANT);
            errLbl.setForeground(C_VACANT); errLbl.setText("✓  Guest checked in!");
            btnCheckIn.setEnabled(false); btnCancel.setEnabled(false); btnCheckOut.setEnabled(true);
        });

        btnCancel.addActionListener(e -> {
            int id = Integer.parseInt(txtId.getText().trim());
            HotelApp.Booking b = backend.bookings.get(id);
            b.status = HotelApp.BookingStatus.CANCELLED;
            dtVals[1].setText("CANCELLED"); dtVals[1].setForeground(C_OCCUPIED);
            errLbl.setForeground(C_VACANT); errLbl.setText("✓  Booking cancelled.");
            btnCheckIn.setEnabled(false); btnCancel.setEnabled(false);
        });

        btnCheckOut.addActionListener(e -> {
            int id = Integer.parseInt(txtId.getText().trim());
            HotelApp.Booking b = backend.bookings.get(id);
            HotelApp.Room    r = backend.rooms.get(b.roomNumber);
            b.status = HotelApp.BookingStatus.COMPLETED;
            r.status = HotelApp.RoomStatus.VACANT;
            dtVals[1].setText("COMPLETED"); dtVals[1].setForeground(C_MUTED);
            errLbl.setForeground(C_VACANT); errLbl.setText("✓  Checked out. Bill shown on the right.");
            btnCheckOut.setEnabled(false);
        });

        return root;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // COMPONENT HELPERS
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel screenWrapper() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(C_BG);
        return p;
    }

    private JPanel pageHeader(String title, String subtitle) {
        JPanel hdr = new JPanel();
        hdr.setBackground(C_BG);
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.setBorder(new EmptyBorder(32, 32, 20, 32));

        JLabel t = new JLabel(title);
        t.setFont(F_HEADING);
        t.setForeground(C_TEXT);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel s = new JLabel(subtitle);
        s.setFont(F_BODY);
        s.setForeground(C_MUTED);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        hdr.add(t);
        hdr.add(Box.createVerticalStrut(4));
        hdr.add(s);
        hdr.add(Box.createVerticalStrut(8));

        JSeparator sep = new JSeparator();
        sep.setForeground(C_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        hdr.add(sep);

        return hdr;
    }

    private JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(C_CARD);
        p.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));
        return p;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(C_TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel errorLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(F_SMALL);
        l.setForeground(C_OCCUPIED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setFont(F_BODY);
        f.setForeground(C_TEXT);
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setFont(F_BODY);
        cb.setBackground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(C_BORDER, 1));
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cb;
    }

    /** Solid amber accent button */
    private JButton accentButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? C_AMBER_DARK : C_AMBER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_BTN);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    /** Outlined ghost button (navy border, no fill) */
    private JButton ghostButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0xEFF6FF) : C_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_NAVY);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_BTN);
        btn.setForeground(C_NAVY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    /** Red danger button for cancel action */
    private JButton dangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0xDC2626) : C_OCCUPIED);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_BTN);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    /** Clean styled JTable */
    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setFont(F_BODY);
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(C_BORDER);
        table.setBackground(C_CARD);
        table.setSelectionBackground(new Color(0xFFF3CD));
        table.setSelectionForeground(C_TEXT);
        table.setFillsViewportHeight(true);

        JTableHeader hdr = table.getTableHeader();
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hdr.setBackground(new Color(0xF8F9FA));
        hdr.setForeground(C_MUTED);
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDER));
        hdr.setReorderingAllowed(false);

        // Default cell renderer (alternating rows + padding)
        DefaultTableCellRenderer dcr = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!sel) setBackground(row % 2 == 0 ? C_CARD : new Color(0xFAFAFC));
                setForeground(C_TEXT);
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, dcr);
        return table;
    }

    /** Styled scroll pane */
    private JScrollPane styledScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(C_CARD);
        return scroll;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // CUSTOM CELL RENDERERS — Status & Type Pills
    // ═════════════════════════════════════════════════════════════════════════
    static class StatusPillRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected, boolean focused, int row, int col) {
            JLabel lbl = new JLabel(String.valueOf(value));
            lbl.setHorizontalAlignment(JLabel.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
            lbl.setOpaque(true);

            String s = String.valueOf(value).trim();
            switch (s) {
                case "VACANT"          -> { lbl.setBackground(new Color(0xD1FAE5)); lbl.setForeground(new Color(0x065F46)); }
                case "OCCUPIED"        -> { lbl.setBackground(new Color(0xFEE2E2)); lbl.setForeground(new Color(0x991B1B)); }
                case "OUT_OF_SERVICE"  -> { lbl.setBackground(new Color(0xF3F4F6)); lbl.setForeground(new Color(0x6B7280)); }
                default                -> { lbl.setBackground(new Color(0xF3F4F6)); lbl.setForeground(C_MUTED); }
            }

            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(selected ? new Color(0xFFF3CD) : (row % 2 == 0 ? Color.WHITE : new Color(0xFAFAFC)));
            wrapper.add(lbl);
            return wrapper;
        }
    }

    static class TypePillRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected, boolean focused, int row, int col) {
            JLabel lbl = new JLabel(String.valueOf(value));
            lbl.setHorizontalAlignment(JLabel.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
            lbl.setOpaque(true);

            String s = String.valueOf(value).trim();
            switch (s) {
                case "Single" -> { lbl.setBackground(new Color(0xDBEAFE)); lbl.setForeground(new Color(0x1E40AF)); }
                case "Double" -> { lbl.setBackground(new Color(0xEDE9FE)); lbl.setForeground(new Color(0x5B21B6)); }
                case "Suite"  -> { lbl.setBackground(new Color(0xFEF3C7)); lbl.setForeground(new Color(0x92400E)); }
                default       -> { lbl.setBackground(new Color(0xF3F4F6)); lbl.setForeground(C_MUTED); }
            }

            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(selected ? new Color(0xFFF3CD) : (row % 2 == 0 ? Color.WHITE : new Color(0xFAFAFC)));
            wrapper.add(lbl);
            return wrapper;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ENTRY POINT
    // ═════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new HotelAppGUI().setVisible(true);
        });
    }
}
