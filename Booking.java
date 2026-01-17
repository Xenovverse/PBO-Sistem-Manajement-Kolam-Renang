import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class Booking {
    private String idBooking;
    private String namaPemesan;
    private String kontakPemesan;
    private String email;
    private Kolam kolam;
    private LocalDate tanggalBooking;
    private LocalTime waktuMulai;
    private LocalTime waktuSelesai;
    private int jumlahOrang;
    private int durasi;
    private boolean sewaPelatih;
    private boolean sewaPeralatan;
    private double biayaKolam;
    private double biayaPelatih;
    private double biayaPeralatan;
    private double diskonGrup;
    private double diskonMember;
    private double totalBiaya;
    private double pajak;
    private double biayaAdmin;
    private StatusBooking status;
    private LocalDateTime waktuPemesanan;
    private String catatan;

    public enum StatusBooking {
        PENDING("Pending", "⏳"),
        CONFIRMED("Confirmed", "✅"),
        CANCELLED("Cancelled", "❌"),
        COMPLETED("Completed", "✔️");

        private final String nama;
        private final String icon;

        StatusBooking(String nama, String icon) {
            this.nama = nama;
            this.icon = icon;
        }

        public String getNama() { return nama; }
        public String getIcon() { return icon; }
    }

    public Booking(String idBooking, String namaPemesan, String kontakPemesan, String email,
                   Kolam kolam, LocalDate tanggalBooking, LocalTime waktuMulai,
                   int jumlahOrang, int durasi, boolean sewaPelatih,
                   boolean sewaPeralatan, String catatan) {
        this.idBooking = idBooking;
        this.namaPemesan = namaPemesan;
        this.kontakPemesan = kontakPemesan;
        this.email = email;
        this.kolam = kolam;
        this.tanggalBooking = tanggalBooking;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuMulai.plusHours(durasi);
        this.jumlahOrang = jumlahOrang;
        this.durasi = durasi;
        this.sewaPelatih = sewaPelatih;
        this.sewaPeralatan = sewaPeralatan;
        this.status = StatusBooking.PENDING;
        this.waktuPemesanan = LocalDateTime.now();
        this.catatan = catatan;

        hitungBiaya();
    }

    private void hitungBiaya() {
        // Hitung biaya dasar dari kolam
        double biayaTotal = kolam.hitungBiaya(jumlahOrang, durasi, sewaPelatih, sewaPeralatan, waktuMulai);

        // Simpan breakdown biaya
        this.biayaKolam = biayaTotal;

        // Hitung diskon grup
        this.diskonGrup = kolam.hitungDiskon(jumlahOrang, durasi);

        // Biaya admin 2%
        this.biayaAdmin = biayaTotal * 0.02;

        // Pajak 10%
        this.pajak = biayaTotal * 0.10;

        // Total akhir
        this.totalBiaya = biayaTotal + biayaAdmin + pajak;
    }

    // Getter methods
    public String getIdBooking() { return idBooking; }
    public String getNamaPemesan() { return namaPemesan; }
    public String getKontakPemesan() { return kontakPemesan; }
    public String getEmail() { return email; }
    public Kolam getKolam() { return kolam; }
    public LocalDate getTanggalBooking() { return tanggalBooking; }
    public LocalTime getWaktuMulai() { return waktuMulai; }
    public LocalTime getWaktuSelesai() { return waktuSelesai; }
    public int getJumlahOrang() { return jumlahOrang; }
    public int getDurasi() { return durasi; }
    public boolean isSewaPelatih() { return sewaPelatih; }
    public boolean isSewaPeralatan() { return sewaPeralatan; }
    public double getTotalBiaya() { return totalBiaya; }
    public StatusBooking getStatus() { return status; }
    public LocalDateTime getWaktuPemesanan() { return waktuPemesanan; }
    public String getCatatan() { return catatan; }

    public void setStatus(StatusBooking status) {
        this.status = status;
    }

    public void konfirmasi() {
        if (this.status == StatusBooking.PENDING) {
            this.status = StatusBooking.CONFIRMED;
        }
    }

    public void batalkan() {
        this.status = StatusBooking.CANCELLED;
    }

    public void selesai() {
        if (this.status == StatusBooking.CONFIRMED) {
            this.status = StatusBooking.COMPLETED;
        }
    }

    public String generateStruk() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder struk = new StringBuilder();

        struk.append("╔══════════════════════════════════════════════════╗\n");
        struk.append("║          STRUK BOOKING KOLAM RENANG              ║\n");
        struk.append("║           AQUA PARADISE SWIMMING POOL           ║\n");
        struk.append("╚══════════════════════════════════════════════════╝\n\n");

        struk.append("Booking ID    : ").append(idBooking).append("\n");
        struk.append("Status        : ").append(status.getIcon()).append(" ").append(status.getNama()).append("\n");
        struk.append("Waktu Pesan   : ").append(waktuPemesanan.format(datetimeFormatter)).append("\n");
        struk.append("──────────────────────────────────────────────────\n");

        struk.append("Nama Pemesan  : ").append(namaPemesan).append("\n");
        struk.append("Kontak        : ").append(kontakPemesan).append("\n");
        struk.append("Email         : ").append(email != null ? email : "-").append("\n");

        struk.append("\n┌─ DETAIL KOLAM ─────────────────────────────────┐\n");
        struk.append("│ Jenis         : ").append(kolam.getJenisKolam()).append("\n");
        struk.append("│ Nama Kolam    : ").append(kolam.getNamaKolam()).append("\n");
        struk.append("│ Kode          : ").append(kolam.getKodeKolam()).append("\n");
        struk.append("│ Kapasitas     : ").append(kolam.getKapasitas()).append(" orang\n");
        struk.append("│ Luas          : ").append(kolam.getLuasKolam()).append(" m²\n");
        struk.append("│ Kedalaman     : ").append(kolam.getKedalamanMin()).append("-").append(kolam.getKedalamanMax()).append(" cm\n");
        struk.append("│ Suhu Air      : ").append(kolam.getSuhuAir()).append("°C\n");
        struk.append("└────────────────────────────────────────────────┘\n");

        struk.append("\n┌─ JADWAL & LAYANAN ─────────────────────────────┐\n");
        struk.append("│ Tanggal       : ").append(tanggalBooking.format(dateFormatter)).append("\n");
        struk.append("│ Waktu Mulai   : ").append(waktuMulai.format(timeFormatter)).append(" WIB\n");
        struk.append("│ Waktu Selesai : ").append(waktuSelesai.format(timeFormatter)).append(" WIB\n");
        struk.append("│ Durasi        : ").append(durasi).append(" jam\n");
        struk.append("│ Jumlah Orang  : ").append(jumlahOrang).append(" orang\n");
        struk.append("│ Sewa Pelatih  : ").append(sewaPelatih ? "✓ Ya" : "✗ Tidak").append("\n");
        struk.append("│ Sewa Peralatan: ").append(sewaPeralatan ? "✓ Ya" : "✗ Tidak").append("\n");
        struk.append("└────────────────────────────────────────────────┘\n");

        if (catatan != null && !catatan.trim().isEmpty()) {
            struk.append("\n┌─ CATATAN ──────────────────────────────────────┐\n");
            struk.append("│ ").append(catatan).append("\n");
            struk.append("└────────────────────────────────────────────────┘\n");
        }

        struk.append("\n╔══════════════════════════════════════════════════╗\n");
        struk.append("║              RINCIAN BIAYA                       ║\n");
        struk.append("╠══════════════════════════════════════════════════╣\n");
        struk.append("║ Biaya Kolam              : Rp ").append(String.format("%,15.0f", biayaKolam)).append(" ║\n");

        if (diskonGrup > 0) {
            struk.append("║ Diskon Grup (").append((int)(diskonGrup * 100)).append("%)        : Rp ")
                    .append(String.format("%,15.0f", -biayaKolam * diskonGrup)).append(" ║\n");
        }

        if (diskonMember > 0) {
            struk.append("║ Diskon Member (").append((int)(diskonMember * 100)).append("%)      : Rp ")
                    .append(String.format("%,15.0f", -(biayaKolam * (1 - diskonGrup)) * diskonMember)).append(" ║\n");
        }

        struk.append("║ Biaya Admin (2%)         : Rp ").append(String.format("%,15.0f", biayaAdmin)).append(" ║\n");
        struk.append("║ Pajak (10%)              : Rp ").append(String.format("%,15.0f", pajak)).append(" ║\n");
        struk.append("╠══════════════════════════════════════════════════╣\n");
        struk.append("║ TOTAL BAYAR              : Rp ").append(String.format("%,15.0f", totalBiaya)).append(" ║\n");
        struk.append("╚══════════════════════════════════════════════════╝\n");

        struk.append("\n┌─ FASILITAS TERMASUK ───────────────────────────┐\n");
        String[] fasilitas = kolam.getFasilitasInclude();
        for (String f : fasilitas) {
            struk.append("│ ✓ ").append(f).append("\n");
        }
        struk.append("└────────────────────────────────────────────────┘\n");

        struk.append("\n          Terima kasih atas kepercayaan Anda!\n");
        struk.append("          Hubungi: 021-12345678 | info@aqua.com\n");
        struk.append("══════════════════════════════════════════════════\n");

        return struk.toString();
    }

    public String generateInvoice() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("INVOICE-%s | %s | %s | Rp %,.0f | %s",
                idBooking, tanggalBooking.format(formatter),
                namaPemesan, totalBiaya, status.getNama());
    }
}