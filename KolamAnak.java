import java.time.LocalTime;

class KolamAnak extends Kolam {
    private static final double TARIF_BASE = 80000;
    private static final double BIAYA_PENGAWAS = 100000;
    private static final double BIAYA_PERALATAN = 25000;
    private static final double BIAYA_PER_ANAK = 10000;

    public KolamAnak(String namaKolam, String kodeKolam) {
        super(namaKolam, 40, 15.0, kodeKolam, 60, 120, 30.0);
    }

    @Override
    public double hitungBiaya(int jumlahOrang, int durasi, boolean sewaPengawas,
                              boolean sewaPeralatan, LocalTime waktu) {
        double biaya = TARIF_BASE * durasi;
        // Multiplier waktu
        biaya *= getMultiplierWaktu(waktu);
        // Biaya per anak
        biaya += jumlahOrang * BIAYA_PER_ANAK * durasi;
        // Pengawas/instruktur anak (wajib 1 pengawas per 10 anak)
        if (sewaPengawas) {
            int jumlahPengawas = (int) Math.ceil(jumlahOrang / 10.0);
            biaya += BIAYA_PENGAWAS * jumlahPengawas * durasi;
        }
        // Peralatan anak (pelampung, arm floaties, mainan air)
        if (sewaPeralatan) {
            biaya += BIAYA_PERALATAN * jumlahOrang;
        }
        // Diskon weekend untuk keluarga
        if (waktu.getHour() >= 8 && waktu.getHour() < 16) {
            biaya *= 0.90; // 10% diskon siang hari untuk anak
        }
        // Terapkan diskon grup
        double diskon = hitungDiskon(jumlahOrang, durasi);
        biaya = biaya * (1 - diskon);
        return biaya;
    }

    @Override
    public String getJenisKolam() {
        return "Kolam Anak";
    }

    @Override
    public double hitungDiskon(int jumlahOrang, int durasi) {
        double diskon = 0;

        if (jumlahOrang >= 30) {
            diskon = 0.25; // 25% untuk grup sekolah
        } else if (jumlahOrang >= 20) {
            diskon = 0.20;
        } else if (jumlahOrang >= 15) {
            diskon = 0.15;
        } else if (jumlahOrang >= 10) {
            diskon = 0.10;
        }

        // Bonus untuk booking pagi (program sekolah)
        if (durasi >= 3) {
            diskon += 0.05;
        }

        return Math.min(diskon, 0.30);
    }

    @Override
    public String getDeskripsi() {
        return "Kolam aman dengan kedalaman bertahap khusus anak-anak (60-120cm), " +
                "dilengkapi perosotan air, mainan edukatif, dan area bermain. " +
                "Air dipanaskan untuk kenyamanan anak.";
    }

    @Override
    public String[] getFasilitasInclude() {
        return new String[] {
                "Perosotan Air Mini",
                "Mainan Air Edukatif",
                "Pelampung Gratis",
                "Area Bermain Air",
                "Shower Air Hangat",
                "Ruang Ganti Anak",
                "Area Tunggu Orangtua",
                "Lifeguard Khusus"
        };
    }
}