import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class KolamRenangGUI extends JFrame {
    private Map<String, BookingSimple> daftarBooking = new LinkedHashMap<>();
    private Map<String, Promo> daftarPromo = new LinkedHashMap<>();
    private Map<String, Kolam> daftarKolam = new LinkedHashMap<>();

    private int counterBooking = 1;

    // Color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color WARNING_COLOR = new Color(241, 196, 15);

    public KolamRenangGUI() {
        setTitle("🏊 Sistem Manajemen Kolam Renang");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeData();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📅 Booking Kolam", createBookingPanel());
        tabbedPane.addTab("🎟️ Promo", createPromoPanel());
        tabbedPane.addTab("📋 Daftar Booking", createDaftarBookingPanel());
        tabbedPane.addTab("📊 Laporan", createLaporanPanel());

        add(tabbedPane);
        setVisible(true);
    }

    private void initializeData() {
        // Inisialisasi kolam
        daftarKolam.put("OLY-A", new KolamOlympic("Kolam Olympic A", "OLY-A"));
        daftarKolam.put("CHD-1", new KolamAnak("Kolam Anak", "CHD-1"));
        daftarKolam.put("TRP-1", new KolamTerapi("Kolam Terapi", "TRP-1"));
        daftarKolam.put("WHN-1", new KolamWahana("Kolam Wahana", "WHN-1"));

        // Inisialisasi promo - PENTING: set tanggal yang masih valid!
        LocalDate today = LocalDate.now();

        daftarPromo.put("NEWMEM25", new Promo("NEWMEM25", "Promo Member Baru", 0.25,
                200000, 100000, today.minusDays(1), today.plusMonths(3), 50,
                "Diskon 25% untuk member baru minimal transaksi Rp 200.000"));

        daftarPromo.put("WKND15", new Promo("WKND15", "Weekend Special", 0.15,
                300000, 75000, today.minusDays(1), today.plusMonths(1), 100,
                "Diskon 15% untuk booking di akhir pekan minimal Rp 300.000"));

        daftarPromo.put("EARLYBIRD", new Promo("EARLYBIRD", "Early Bird", 0.20,
                150000, 50000, today.minusDays(1), today.plusMonths(2), 75,
                "Diskon 20% untuk booking jam pagi minimal Rp 150.000"));
    }

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("📝 Form Booking"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField namaField = new JTextField(20);
        JTextField kontakField = new JTextField(20);

        JComboBox<String> kolamCombo = new JComboBox<>();
        for (Kolam kolam : daftarKolam.values()) {
            kolamCombo.addItem(kolam.getKodeKolam() + " - " + kolam.getNamaKolam());
        }

        JTextField tanggalField = new JTextField(LocalDate.now().plusDays(1).toString(), 15);
        JComboBox<String> waktuCombo = new JComboBox<>();
        for (int i = 5; i <= 21; i++) {
            waktuCombo.addItem(String.format("%02d:00", i));
        }

        JSpinner jumlahOrangSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 200, 1));
        JSpinner durasiSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 12, 1));
        JCheckBox pelatihCheckBox = new JCheckBox();
        JCheckBox peralatanCheckBox = new JCheckBox();

        JTextField promoField = new JTextField(15);
        JButton cekPromoButton = new JButton("🔍 Cek");
        JLabel promoStatusLabel = new JLabel("");

        // Tambahkan komponen
        int row = 0;
        addFormRow(formPanel, gbc, row++, "Nama Penyewa*:", namaField);
        addFormRow(formPanel, gbc, row++, "No. Telepon*:", kontakField);
        addFormRow(formPanel, gbc, row++, "Pilih Kolam*:", kolamCombo);
        addFormRow(formPanel, gbc, row++, "Tanggal (YYYY-MM-DD)*:", tanggalField);
        addFormRow(formPanel, gbc, row++, "Waktu Mulai*:", waktuCombo);
        addFormRow(formPanel, gbc, row++, "Jumlah Orang*:", jumlahOrangSpinner);
        addFormRow(formPanel, gbc, row++, "Durasi (jam)*:", durasiSpinner);
        addFormRow(formPanel, gbc, row++, "Sewa Pelatih:", pelatihCheckBox);
        addFormRow(formPanel, gbc, row++, "Sewa Peralatan:", peralatanCheckBox);

        JPanel promoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        promoPanel.add(promoField);
        promoPanel.add(cekPromoButton);
        promoPanel.add(promoStatusLabel);
        addFormRow(formPanel, gbc, row++, "Kode Promo:", promoPanel);

        // Info Area
        JTextArea infoArea = new JTextArea(20, 40);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane infoScroll = new JScrollPane(infoArea);
        infoScroll.setBorder(BorderFactory.createTitledBorder("ℹ️ Info Kolam"));

        kolamCombo.addActionListener(e -> {
            String selected = (String) kolamCombo.getSelectedItem();
            if (selected != null) {
                String kode = selected.split(" - ")[0];
                Kolam kolam = daftarKolam.get(kode);
                if (kolam != null) {
                    updateInfoKolam(infoArea, kolam);
                }
            }
        });

        if (kolamCombo.getItemCount() > 0) {
            String kode = ((String)kolamCombo.getItemAt(0)).split(" - ")[0];
            updateInfoKolam(infoArea, daftarKolam.get(kode));
        }

        // Cek Promo
        cekPromoButton.addActionListener(e -> {
            String kode = promoField.getText().trim().toUpperCase();
            promoField.setText(kode); // Auto uppercase

            if (kode.isEmpty()) {
                promoStatusLabel.setText("⚠️ Kosong");
                promoStatusLabel.setForeground(WARNING_COLOR);
                return;
            }

            Promo promo = daftarPromo.get(kode);

            if (promo == null) {
                promoStatusLabel.setText("❌ Tidak Ditemukan");
                promoStatusLabel.setForeground(DANGER_COLOR);

                StringBuilder availPromo = new StringBuilder("Kode promo tersedia:\n\n");
                for (Promo p : daftarPromo.values()) {
                    if (p.isValid(0)) {
                        availPromo.append("• ").append(p.getKodePromo())
                                .append(" - ").append(p.getNamaPromo())
                                .append(" (").append((int)(p.getPersenDiskon()*100))
                                .append("%)\n");
                    }
                }

                JOptionPane.showMessageDialog(this, availPromo.toString(),
                        "Kode Promo Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
            } else if (!promo.isValid(0)) {
                promoStatusLabel.setText("❌ Tidak Valid");
                promoStatusLabel.setForeground(DANGER_COLOR);

                String reason = "Promo tidak valid";
                if (promo.getTerpakai() >= promo.getKuota()) {
                    reason = "Kuota sudah habis";
                } else if (LocalDate.now().isAfter(promo.getTanggalBerakhir())) {
                    reason = "Promo sudah expired";
                } else if (!promo.isAktif()) {
                    reason = "Promo tidak aktif";
                }

                JOptionPane.showMessageDialog(this, reason,
                        "Promo Tidak Valid", JOptionPane.WARNING_MESSAGE);
            } else {
                promoStatusLabel.setText("✅ Valid!");
                promoStatusLabel.setForeground(SUCCESS_COLOR);

                JTextArea textArea = new JTextArea(promo.getDetailPromo());
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                textArea.setEditable(false);

                JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                        "Detail Promo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton hitungButton = new JButton("💰 Hitung Biaya");
        JButton bookButton = new JButton("✅ Booking");
        JButton resetButton = new JButton("🔄 Reset");

        hitungButton.setBackground(PRIMARY_COLOR);
        hitungButton.setForeground(Color.WHITE);
        bookButton.setBackground(SUCCESS_COLOR);
        bookButton.setForeground(Color.WHITE);
        resetButton.setBackground(WARNING_COLOR);
        resetButton.setForeground(Color.WHITE);

        hitungButton.addActionListener(e -> hitungEstimasi(kolamCombo, jumlahOrangSpinner,
                durasiSpinner, pelatihCheckBox, peralatanCheckBox, waktuCombo, promoField));

        bookButton.addActionListener(e -> prosesBooking(namaField, kontakField, kolamCombo,
                tanggalField, waktuCombo, jumlahOrangSpinner, durasiSpinner, pelatihCheckBox,
                peralatanCheckBox, promoField));

        resetButton.addActionListener(e -> {
            namaField.setText("");
            kontakField.setText("");
            tanggalField.setText(LocalDate.now().plusDays(1).toString());
            waktuCombo.setSelectedIndex(0);
            jumlahOrangSpinner.setValue(1);
            durasiSpinner.setValue(2);
            pelatihCheckBox.setSelected(false);
            peralatanCheckBox.setSelected(false);
            promoField.setText("");
            promoStatusLabel.setText("");
        });

        buttonPanel.add(hitungButton);
        buttonPanel.add(bookButton);
        buttonPanel.add(resetButton);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, infoScroll);
        splitPane.setDividerLocation(550);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private void hitungEstimasi(JComboBox<String> kolamCombo, JSpinner jumlahOrangSpinner,
                                JSpinner durasiSpinner, JCheckBox pelatihCB, JCheckBox peralatanCB,
                                JComboBox<String> waktuCombo, JTextField promoField) {
        try {
            String kode = ((String)kolamCombo.getSelectedItem()).split(" - ")[0];
            Kolam kolam = daftarKolam.get(kode);

            int jumlahOrang = (Integer) jumlahOrangSpinner.getValue();
            int durasi = (Integer) durasiSpinner.getValue();
            LocalTime waktu = LocalTime.parse((String)waktuCombo.getSelectedItem());

            double biaya = kolam.hitungBiaya(jumlahOrang, durasi,
                    pelatihCB.isSelected(), peralatanCB.isSelected(), waktu);

            double diskonGrup = kolam.hitungDiskon(jumlahOrang, durasi);
            double subtotal = biaya;

            // Promo
            double diskonPromo = 0;
            String kodePromo = promoField.getText().trim().toUpperCase();
            Promo promo = null;
            if (!kodePromo.isEmpty()) {
                promo = daftarPromo.get(kodePromo);
                if (promo != null && promo.isValid(0) && subtotal >= promo.getMinimalTransaksi()) {
                    diskonPromo = promo.hitungPotongan(subtotal);
                    subtotal -= diskonPromo;
                }
            }

            double admin = subtotal * 0.02;
            double pajak = subtotal * 0.10;
            double total = subtotal + admin + pajak;

            StringBuilder info = new StringBuilder();
            info.append("═══════ ESTIMASI BIAYA ═══════\n\n");
            info.append("Kolam: ").append(kolam.getJenisKolam()).append("\n");
            info.append("Jumlah: ").append(jumlahOrang).append(" orang\n");
            info.append("Durasi: ").append(durasi).append(" jam\n");
            info.append("Waktu: ").append(waktu).append("\n\n");
            info.append("Biaya Dasar: Rp ").append(String.format("%,.0f", biaya)).append("\n");
            info.append("Diskon Grup (").append((int)(diskonGrup*100)).append("%): -Rp ")
                    .append(String.format("%,.0f", biaya * diskonGrup)).append("\n");

            if (diskonPromo > 0) {
                info.append("Diskon Promo (").append(promo.getKodePromo()).append("): -Rp ")
                        .append(String.format("%,.0f", diskonPromo)).append("\n");
            }

            info.append("Biaya Admin (2%): +Rp ").append(String.format("%,.0f", admin)).append("\n");
            info.append("Pajak (10%): +Rp ").append(String.format("%,.0f", pajak)).append("\n\n");
            info.append("═══════════════════════════════\n");
            info.append("TOTAL: Rp ").append(String.format("%,.0f", total)).append("\n");
            info.append("═══════════════════════════════");

            JTextArea textArea = new JTextArea(info.toString());
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setEditable(false);

            JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                    "Estimasi Biaya", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prosesBooking(JTextField namaField, JTextField kontakField,
                               JComboBox<String> kolamCombo, JTextField tanggalField,
                               JComboBox<String> waktuCombo, JSpinner jumlahOrangSpinner,
                               JSpinner durasiSpinner, JCheckBox pelatihCB, JCheckBox peralatanCB,
                               JTextField promoField) {
        try {
            String nama = namaField.getText().trim();
            String kontak = kontakField.getText().trim();

            if (nama.isEmpty() || kontak.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nama dan kontak harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String kode = ((String)kolamCombo.getSelectedItem()).split(" - ")[0];
            Kolam kolam = daftarKolam.get(kode);

            LocalDate tanggal = LocalDate.parse(tanggalField.getText());
            LocalTime waktu = LocalTime.parse((String)waktuCombo.getSelectedItem());

            int jumlahOrang = (Integer) jumlahOrangSpinner.getValue();
            int durasi = (Integer) durasiSpinner.getValue();

            // Validasi kapasitas
            if (jumlahOrang > kolam.getKapasitas()) {
                JOptionPane.showMessageDialog(this,
                        "Jumlah melebihi kapasitas (" + kolam.getKapasitas() + ")!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String idBooking = String.format("BK%05d", counterBooking++);

            // Promo
            String kodePromo = promoField.getText().trim().toUpperCase();
            Promo promoUsed = null;
            if (!kodePromo.isEmpty()) {
                Promo promo = daftarPromo.get(kodePromo);
                if (promo != null && promo.isValid(0)) {
                    promoUsed = promo;
                }
            }

            BookingSimple booking = new BookingSimple(idBooking, nama, kontak,
                    kolam, tanggal, waktu, jumlahOrang, durasi,
                    pelatihCB.isSelected(), peralatanCB.isSelected(), promoUsed);

            // Gunakan promo
            if (promoUsed != null) {
                promoUsed.gunakan();
            }

            daftarBooking.put(idBooking, booking);

            // Save
            saveBookingToFile(booking);

            // Tampilkan struk
            JTextArea strukArea = new JTextArea(booking.generateStruk());
            strukArea.setEditable(false);
            strukArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

            JScrollPane scrollPane = new JScrollPane(strukArea);
            scrollPane.setPreferredSize(new Dimension(600, 500));

            JOptionPane.showMessageDialog(this, scrollPane,
                    "✅ Booking Berhasil!", JOptionPane.INFORMATION_MESSAGE);

            // Reset
            namaField.setText("");
            kontakField.setText("");
            jumlahOrangSpinner.setValue(1);
            durasiSpinner.setValue(2);
            pelatihCB.setSelected(false);
            peralatanCB.setSelected(false);
            promoField.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JPanel createPromoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {"Kode", "Nama", "Diskon", "Min. Transaksi",
                "Maks. Potongan", "Kuota", "Terpakai", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        updatePromoTable(model);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDaftarBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {"ID", "Nama", "Kolam", "Tanggal", "Waktu",
                "Jumlah", "Durasi", "Total Biaya"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        updateBookingTable(model);

        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> updateBookingTable(model));

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createLaporanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextArea laporanArea = new JTextArea();
        laporanArea.setEditable(false);
        laporanArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JButton generateButton = new JButton("📊 Generate Laporan");
        generateButton.addActionListener(e -> {
            laporanArea.setText(generateLaporan());
        });

        panel.add(new JScrollPane(laporanArea), BorderLayout.CENTER);
        panel.add(generateButton, BorderLayout.SOUTH);
        return panel;
    }

    // Helper methods
    private void updateInfoKolam(JTextArea infoArea, Kolam kolam) {
        StringBuilder info = new StringBuilder();
        info.append("╔══════════════════════════════════════╗\n");
        info.append("  ").append(kolam.getJenisKolam()).append("\n");
        info.append("╚══════════════════════════════════════╝\n\n");
        info.append("Nama: ").append(kolam.getNamaKolam()).append("\n");
        info.append("Kode: ").append(kolam.getKodeKolam()).append("\n");
        info.append("Kapasitas: ").append(kolam.getKapasitas()).append(" orang\n");
        info.append("Luas: ").append(kolam.getLuasKolam()).append(" m²\n");
        info.append("Kedalaman: ").append(kolam.getKedalamanMin())
                .append("-").append(kolam.getKedalamanMax()).append(" cm\n");
        info.append("Suhu: ").append(kolam.getSuhuAir()).append("°C\n\n");
        info.append("Deskripsi:\n").append(kolam.getDeskripsi()).append("\n\n");
        info.append("Fasilitas:\n");
        for (String fas : kolam.getFasilitasInclude()) {
            info.append("✓ ").append(fas).append("\n");
        }
        infoArea.setText(info.toString());
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row,
                            String label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(component, gbc);
    }

    private void updatePromoTable(DefaultTableModel model) {
        model.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Promo promo : daftarPromo.values()) {
            model.addRow(new Object[] {
                    promo.getKodePromo(),
                    promo.getNamaPromo(),
                    String.format("%.0f%%", promo.getPersenDiskon() * 100),
                    String.format("Rp %,.0f", promo.getMinimalTransaksi()),
                    String.format("Rp %,.0f", promo.getMaksimalPotongan()),
                    promo.getKuota(),
                    promo.getTerpakai(),
                    promo.getStatus()
            });
        }
    }

    private void updateBookingTable(DefaultTableModel model) {
        model.setRowCount(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (BookingSimple booking : daftarBooking.values()) {
            model.addRow(new Object[] {
                    booking.getIdBooking(),
                    booking.getNamaPemesan(),
                    booking.getKolam().getNamaKolam(),
                    booking.getTanggalBooking().format(dateFormatter),
                    booking.getWaktuMulai().format(timeFormatter),
                    booking.getJumlahOrang() + " orang",
                    booking.getDurasi() + " jam",
                    String.format("Rp %,.0f", booking.getTotalBiaya())
            });
        }
    }

    private String generateLaporan() {
        StringBuilder laporan = new StringBuilder();
        laporan.append("╔═══════════════════════════════════════╗\n");
        laporan.append("║    LAPORAN KOLAM RENANG              ║\n");
        laporan.append("╚═══════════════════════════════════════╝\n\n");

        laporan.append("Total Booking: ").append(daftarBooking.size()).append("\n\n");

        double totalRevenue = 0;
        for (BookingSimple booking : daftarBooking.values()) {
            totalRevenue += booking.getTotalBiaya();
        }

        laporan.append("Total Pendapatan: Rp ").append(String.format("%,.0f", totalRevenue)).append("\n");

        return laporan.toString();
    }

    private void saveBookingToFile(BookingSimple booking) {
        try (FileWriter writer = new FileWriter("booking_kolam.txt", true)) {
            writer.write(booking.generateStruk() + "\n\n");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KolamRenangGUI());
    }
}

// Inner class BookingSimple
class BookingSimple {
    private String idBooking;
    private String namaPemesan;
    private String kontakPemesan;
    private Kolam kolam;
    private LocalDate tanggalBooking;
    private LocalTime waktuMulai;
    private int jumlahOrang;
    private int durasi;
    private boolean sewaPelatih;
    private boolean sewaPeralatan;
    private double totalBiaya;
    private Promo promoUsed;

    public BookingSimple(String idBooking, String namaPemesan, String kontakPemesan,
                         Kolam kolam, LocalDate tanggalBooking, LocalTime waktuMulai,
                         int jumlahOrang, int durasi, boolean sewaPelatih,
                         boolean sewaPeralatan, Promo promoUsed) {
        this.idBooking = idBooking;
        this.namaPemesan = namaPemesan;
        this.kontakPemesan = kontakPemesan;
        this.kolam = kolam;
        this.tanggalBooking = tanggalBooking;
        this.waktuMulai = waktuMulai;
        this.jumlahOrang = jumlahOrang;
        this.durasi = durasi;
        this.sewaPelatih = sewaPelatih;
        this.sewaPeralatan = sewaPeralatan;
        this.promoUsed = promoUsed;

        hitungBiaya();
    }

    private void hitungBiaya() {
        double biaya = kolam.hitungBiaya(jumlahOrang, durasi, sewaPelatih, sewaPeralatan, waktuMulai);

        // Promo
        if (promoUsed != null && promoUsed.isValidForAmount(biaya)) {
            biaya -= promoUsed.hitungPotongan(biaya);
        }

        double admin = biaya * 0.02;
        double pajak = biaya * 0.10;

        this.totalBiaya = biaya + admin + pajak;
    }

    public String getIdBooking() { return idBooking; }
    public String getNamaPemesan() { return namaPemesan; }
    public String getKontakPemesan() { return kontakPemesan; }
    public Kolam getKolam() { return kolam; }
    public LocalDate getTanggalBooking() { return tanggalBooking; }
    public LocalTime getWaktuMulai() { return waktuMulai; }
    public int getJumlahOrang() { return jumlahOrang; }
    public int getDurasi() { return durasi; }
    public double getTotalBiaya() { return totalBiaya; }

    public String generateStruk() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        StringBuilder struk = new StringBuilder();
        struk.append("╔══════════════════════════════════════════════════╗\n");
        struk.append("║       STRUK BOOKING KOLAM RENANG                ║\n");
        struk.append("╚══════════════════════════════════════════════════╝\n\n");

        struk.append("ID Booking    : ").append(idBooking).append("\n");
        struk.append("Nama          : ").append(namaPemesan).append("\n");
        struk.append("Kontak        : ").append(kontakPemesan).append("\n\n");

        struk.append("Kolam         : ").append(kolam.getJenisKolam()).append("\n");
        struk.append("Nama Kolam    : ").append(kolam.getNamaKolam()).append("\n");
        struk.append("Tanggal       : ").append(tanggalBooking.format(dateFormatter)).append("\n");
        struk.append("Waktu         : ").append(waktuMulai.format(timeFormatter)).append(" WIB\n");
        struk.append("Durasi        : ").append(durasi).append(" jam\n");
        struk.append("Jumlah Orang  : ").append(jumlahOrang).append(" orang\n");
        struk.append("Sewa Pelatih  : ").append(sewaPelatih ? "Ya" : "Tidak").append("\n");
        struk.append("Sewa Peralatan: ").append(sewaPeralatan ? "Ya" : "Tidak").append("\n");

        if (promoUsed != null) {
            struk.append("Promo         : ").append(promoUsed.getKodePromo()).append("\n");
        }

        struk.append("\n──────────────────────────────────────────────────\n");
        struk.append("TOTAL BAYAR   : Rp ").append(String.format("%,.0f", totalBiaya)).append("\n");
        struk.append("══════════════════════════════════════════════════\n");

        return struk.toString();
    }
}