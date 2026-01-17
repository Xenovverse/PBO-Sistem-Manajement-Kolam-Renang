import java.time.LocalTime;

class KolamTerapi extends Kolam {
    private static final double TARIF_BASE = 150000;
    private static final double BIAYA_TERAPIS = 200000;
    private static final double BIAYA_PERALATAN = 75000;
    private static final double BIAYA_PER_PASIEN = 50000;

    public KolamTerapi(String namaKolam, String kodeKolam) {
        super(namaKolam, 20, 10.0, kodeKolam, 100, 150, 32.0);
    }

    @Override
    public double hitungBiaya(int jumlahOrang, int durasi, boolean sewaTerapis,
                              boolean sewaPeralatan, LocalTime waktu) {
        double biaya = TARIF_BASE * durasi;

        // Multiplier waktu lebih rendah (terapi tidak bergantung waktu)
        if (waktu.getHour() >= 20 || waktu.getHour() < 6) {
            biaya *= 1.1; // Hanya 10% untuk malam
        }

        // Biaya per pasien
        biaya += jumlahOrang * BIAYA_PER_PASIEN * durasi;

        // Terapis profesional (wajib 1:5 ratio)
        if (sewaTerapis) {
            int jumlahTerapis = (int) Math.ceil(jumlahOrang / 5.0);
            biaya += BIAYA_TERAPIS * jumlahTerapis * durasi;
        }

        // Peralatan terapi (resistance bands, aqua dumbbells, kickboards)
        if (sewaPeralatan) {
            biaya += BIAYA_PERALATAN * jumlahOrang;
        }

        // Program terapi jangka panjang
        if (durasi >= 2) {
            biaya *= 0.92; // 8% diskon sesi panjang
        }

        // Terapkan diskon grup
        double diskon = hitungDiskon(jumlahOrang, durasi);
        biaya = biaya * (1 - diskon);

        return biaya;
    }

    @Override
    public String getJenisKolam() {
        return "Kolam Terapi";
    }

    @Override
    public double hitungDiskon(int jumlahOrang, int durasi) {
        double diskon = 0;

        if (jumlahOrang >= 15) {
            diskon = 0.15; // Grup rehabilitasi
        } else if (jumlahOrang >= 10) {
            diskon = 0.12;
        } else if (jumlahOrang >= 5) {
            diskon = 0.08;
        } else if (jumlahOrang >= 3) {
            diskon = 0.05;
        }

        // Program rutin (booking panjang)
        if (durasi >= 3) {
            diskon += 0.05;
        }

        return Math.min(diskon, 0.20);
    }

    @Override
    public String getDeskripsi() {
        return "Kolam terapi dengan air hangat (32°C) dan jet hidroterapi, " +
                "ideal untuk rehabilitasi fisik, terapi pasca operasi, dan relaksasi. " +
                "Dilengkapi dengan peralatan terapi aquatik profesional.";
    }

    @Override
    public String[] getFasilitasInclude() {
        return new String[] {
                "Jet Hidroterapi",
                "Air Hangat 32°C",
                "Resistance Equipment",
                "Underwater Treadmill",
                "Hydrotherapy Jets",
                "Ruang Konsultasi",
                "Area Relaksasi",
                "Medical Staff On-site"
        };
    }
}