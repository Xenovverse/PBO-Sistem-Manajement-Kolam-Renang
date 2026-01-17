import java.time.LocalTime;

// Abstract class dengan enkapsulasi lengkap
abstract class Kolam {
    protected String namaKolam;
    protected int kapasitas;
    protected double luasKolam;
    protected String kodeKolam;
    protected int kedalamanMin;
    protected int kedalamanMax;
    protected double suhuAir;

    public Kolam(String namaKolam, int kapasitas, double luasKolam, String kodeKolam,
                 int kedalamanMin, int kedalamanMax, double suhuAir) {
        this.namaKolam = namaKolam;
        this.kapasitas = kapasitas;
        this.luasKolam = luasKolam;
        this.kodeKolam = kodeKolam;
        this.kedalamanMin = kedalamanMin;
        this.kedalamanMax = kedalamanMax;
        this.suhuAir = suhuAir;
    }

    // Polymorphism - method abstract
    public abstract double hitungBiaya(int jumlahOrang, int durasi, boolean sewaPelatih,
                                       boolean sewaPeralatan, LocalTime waktu);

    public abstract String getJenisKolam();

    public abstract double hitungDiskon(int jumlahOrang, int durasi);

    public abstract String getDeskripsi();

    public abstract String[] getFasilitasInclude();

    // Method untuk hitung biaya berdasarkan waktu (peak/off-peak)
    protected double getMultiplierWaktu(LocalTime waktu) {
        int jam = waktu.getHour();
        // Peak hours: 08:00-12:00 dan 16:00-20:00 = multiplier 1.3
        // Off-peak: waktu lainnya = multiplier 1.0
        if ((jam >= 8 && jam < 12) || (jam >= 16 && jam < 20)) {
            return 1.3;
        } else if (jam >= 5 && jam < 8) {
            return 0.8; // Early bird discount
        }
        return 1.0;
    }

    // Enkapsulasi - getter methods
    public String getNamaKolam() { return namaKolam; }
    public int getKapasitas() { return kapasitas; }
    public double getLuasKolam() { return luasKolam; }
    public String getKodeKolam() { return kodeKolam; }
    public int getKedalamanMin() { return kedalamanMin; }
    public int getKedalamanMax() { return kedalamanMax; }
    public double getSuhuAir() { return suhuAir; }

    @Override
    public String toString() {
        return String.format("%s (%s) - Kapasitas: %d orang", namaKolam, kodeKolam, kapasitas);
    }
}