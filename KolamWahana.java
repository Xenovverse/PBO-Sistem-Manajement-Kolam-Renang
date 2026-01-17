import java.time.LocalTime;

class KolamWahana extends Kolam {
    private static final double TARIF_BASE = 120000;
    private static final double BIAYA_LIFEGUARD = 120000;
    private static final double BIAYA_PERALATAN = 35000;
    private static final double BIAYA_PER_ORANG = 15000;

    public KolamWahana(String namaKolam, String kodeKolam) {
        super(namaKolam, 60, 25.0, kodeKolam, 120, 180, 28.0);
    }

    @Override
    public double hitungBiaya(int jumlahOrang, int durasi, boolean sewaLifeguard,
                              boolean sewaPeralatan, LocalTime waktu) {
        double biaya = TARIF_BASE * durasi;

        biaya *= getMultiplierWaktu(waktu);

        biaya += jumlahOrang * BIAYA_PER_ORANG * durasi;

        if (sewaLifeguard) {
            int jumlahLifeguard = (int) Math.ceil(jumlahOrang / 15.0);
            biaya += BIAYA_LIFEGUARD * jumlahLifeguard * durasi;
        }

        if (sewaPeralatan) {
            biaya += BIAYA_PERALATAN * jumlahOrang;
        }

        if (jumlahOrang >= 5 && jumlahOrang <= 8) {
            biaya *= 0.88; // 12% diskon paket keluarga
        }

        double diskon = hitungDiskon(jumlahOrang, durasi);
        biaya = biaya * (1 - diskon);

        return biaya;
    }

    @Override
    public String getJenisKolam() {
        return "Kolam Wahana";
    }

    @Override
    public double hitungDiskon(int jumlahOrang, int durasi) {
        double diskon = 0;

        if (jumlahOrang >= 40) {
            diskon = 0.18;
        } else if (jumlahOrang >= 25) {
            diskon = 0.12;
        } else if (jumlahOrang >= 15) {
            diskon = 0.08;
        }

        if (durasi >= 4) {
            diskon += 0.05;
        }

        return Math.min(diskon, 0.23);
    }

    @Override
    public String getDeskripsi() {
        return "Kolam rekreasi dengan berbagai wahana air seperti water slide, " +
                "lazy river, dan wave pool. Perfect untuk keluarga dan gathering perusahaan.";
    }

    @Override
    public String[] getFasilitasInclude() {
        return new String[] {
                "Water Slide 3 Jalur",
                "Lazy River",
                "Wave Pool",
                "Rain Dance Area",
                "Ember Tumpah Raksasa",
                "Life Vest Gratis",
                "Gazebo & Cabana",
                "Food Court Area"
        };
    }
}