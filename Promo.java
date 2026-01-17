import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class Promo {
    private String kodePromo;
    private String namaPromo;
    private double persenDiskon;
    private double minimalTransaksi;
    private double maksimalPotongan;
    private LocalDate tanggalMulai;
    private LocalDate tanggalBerakhir;
    private int kuota;
    private int terpakai;
    private boolean aktif;
    private String deskripsi;

    public Promo(String kodePromo, String namaPromo, double persenDiskon,
                 double minimalTransaksi, double maksimalPotongan,
                 LocalDate tanggalMulai, LocalDate tanggalBerakhir,
                 int kuota, String deskripsi) {
        this.kodePromo = kodePromo;
        this.namaPromo = namaPromo;
        this.persenDiskon = persenDiskon;
        this.minimalTransaksi = minimalTransaksi;
        this.maksimalPotongan = maksimalPotongan;
        this.tanggalMulai = tanggalMulai;
        this.tanggalBerakhir = tanggalBerakhir;
        this.kuota = kuota;
        this.terpakai = 0;
        this.aktif = true;
        this.deskripsi = deskripsi;
    }

    public boolean isValid(double totalBiaya) {
        LocalDate today = LocalDate.now();

        // Cek kondisi satu per satu untuk debugging
        if (!aktif) return false;
        if (terpakai >= kuota) return false;
        if (today.isBefore(tanggalMulai)) return false;
        if (today.isAfter(tanggalBerakhir)) return false;
        // Minimal transaksi dicek saat apply, bukan saat validasi kode

        return true;
    }

    public boolean isValidForAmount(double totalBiaya) {
        return isValid(totalBiaya) && totalBiaya >= minimalTransaksi;
    }

    public double hitungPotongan(double totalBiaya) {
        if (!isValid(totalBiaya)) {
            return 0;
        }

        double potongan = totalBiaya * persenDiskon;
        return Math.min(potongan, maksimalPotongan);
    }

    public void gunakan() {
        if (terpakai < kuota) {
            terpakai++;
        }
    }

    // Getters
    public String getKodePromo() { return kodePromo; }
    public String getNamaPromo() { return namaPromo; }
    public double getPersenDiskon() { return persenDiskon; }
    public double getMinimalTransaksi() { return minimalTransaksi; }
    public double getMaksimalPotongan() { return maksimalPotongan; }
    public LocalDate getTanggalMulai() { return tanggalMulai; }
    public LocalDate getTanggalBerakhir() { return tanggalBerakhir; }
    public int getKuota() { return kuota; }
    public int getTerpakai() { return terpakai; }
    public int getSisaKuota() { return kuota - terpakai; }
    public boolean isAktif() { return aktif; }
    public String getDeskripsi() { return deskripsi; }

    public void setAktif(boolean aktif) { this.aktif = aktif; }

    public String getStatus() {
        LocalDate today = LocalDate.now();

        if (!aktif) return "❌ Nonaktif";
        if (terpakai >= kuota) return "❌ Kuota Habis";
        if (today.isBefore(tanggalMulai)) return "⏳ Belum Dimulai";
        if (today.isAfter(tanggalBerakhir)) return "❌ Expired";

        return "✅ Aktif";
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("%s | %s | Diskon %.0f%% | Min. Rp %,.0f | Kuota: %d/%d | %s",
                kodePromo, namaPromo, persenDiskon * 100, minimalTransaksi,
                terpakai, kuota, getStatus());
    }

    public String getDetailPromo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        StringBuilder sb = new StringBuilder();

        sb.append("╔══════════════════════════════════════════════╗\n");
        sb.append("║           DETAIL PROMO                       ║\n");
        sb.append("╚══════════════════════════════════════════════╝\n\n");
        sb.append("Kode Promo        : ").append(kodePromo).append("\n");
        sb.append("Nama Promo        : ").append(namaPromo).append("\n");
        sb.append("Diskon            : ").append((int)(persenDiskon * 100)).append("%\n");
        sb.append("Minimal Transaksi : Rp ").append(String.format("%,.0f", minimalTransaksi)).append("\n");
        sb.append("Maksimal Potongan : Rp ").append(String.format("%,.0f", maksimalPotongan)).append("\n");
        sb.append("Periode           : ").append(tanggalMulai.format(formatter))
                .append(" - ").append(tanggalBerakhir.format(formatter)).append("\n");
        sb.append("Kuota             : ").append(terpakai).append(" / ").append(kuota).append("\n");
        sb.append("Sisa Kuota        : ").append(getSisaKuota()).append("\n");
        sb.append("Status            : ").append(getStatus()).append("\n");
        sb.append("\nDeskripsi:\n").append(deskripsi).append("\n");
        sb.append("══════════════════════════════════════════════\n");

        return sb.toString();
    }
}