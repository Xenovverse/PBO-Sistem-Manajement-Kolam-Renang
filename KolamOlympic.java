import java.time.LocalTime;

class KolamOlympic extends Kolam { // 'extends' menandakan pewarisan dari kelas Kolam
    private static final double TARIF_BASE = 200000;
    private static final double BIAYA_PELATIH = 150000;
    private static final double BIAYA_PERALATAN = 50000;
    private static final double BIAYA_PER_ORANG_EXTRA = 5000;

    public KolamOlympic(String namaKolam, String kodeKolam) {
        // 'super' mengirim data ke konstruktor kelas induk
        // Set default: Kapasitas 100, Suhu 27.0 derajat
        super(namaKolam, 100, 50.0, kodeKolam, 180, 200, 27.0);
    }

    @Override
    public double hitungBiaya(int jumlahOrang, int durasi, boolean sewaPelatih,
                              boolean sewaPeralatan, LocalTime waktu) {
        double biaya = TARIF_BASE * durasi;
        // Multiplier waktu (peak/off-peak)
        biaya *= getMultiplierWaktu(waktu);
        // Biaya tambahan per orang jika melebihi 50 orang
        if (jumlahOrang > 50) {
            biaya += (jumlahOrang - 50) * BIAYA_PER_ORANG_EXTRA * durasi;
        }
        // Biaya pelatih profesional
        if (sewaPelatih) {
            int jumlahPelatih = (int) Math.ceil(jumlahOrang / 20.0);
            biaya += BIAYA_PELATIH * jumlahPelatih * durasi;
        }
        // Biaya peralatan (fins, goggles, snorkel)
        if (sewaPeralatan) {
            biaya += BIAYA_PERALATAN * jumlahOrang;
        }
        // Bonus durasi panjang
        if (durasi >= 4) {
            biaya *= 0.95; // 5% discount untuk booking ≥4 jam
        }
        // Terapkan diskon grup
        double diskon = hitungDiskon(jumlahOrang, durasi);
        biaya = biaya * (1 - diskon);
        return biaya;
    }

    @Override
    public String getJenisKolam() {
        return "Kolam Olympic";
    }

    @Override
    public double hitungDiskon(int jumlahOrang, int durasi) {
        double diskon = 0;

        if (jumlahOrang >= 80) {
            diskon = 0.20; // 20% untuk grup sangat besar
        } else if (jumlahOrang >= 50) {
            diskon = 0.15;
        } else if (jumlahOrang >= 30) {
            diskon = 0.10;
        } else if (jumlahOrang >= 20) {
            diskon = 0.05;
        }

        // Tambahan diskon untuk durasi panjang
        if (durasi >= 6) {
            diskon += 0.05;
        }

        return Math.min(diskon, 0.25); // Max 25% total diskon
    }

    @Override
    public String getDeskripsi() {
        return "Kolam standar olimpik dengan 8 lajur, ideal untuk latihan profesional, " +
                "perlombaan, dan pelatihan intensif. Dilengkapi dengan sistem timing otomatis.";
    }

    @Override
    public String[] getFasilitasInclude() {
        return new String[] {
                "8 Lajur Standard Olympic",
                "Sistem Timing Otomatis",
                "Starting Block",
                "Papan Skor Digital",
                "Ruang Ganti Premium",
                "Loker Pribadi",
                "Area Pemanasan"
        };
    }
}