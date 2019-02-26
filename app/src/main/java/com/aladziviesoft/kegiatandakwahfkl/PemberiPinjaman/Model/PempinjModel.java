package com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman.Model;

public class PempinjModel {
    String IdPp, NamaPeminjam, NoKtp, Nominal, NominalDua, NoHP, JatuhTempo, status, sisa;

    public PempinjModel() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSisa() {
        return sisa;
    }

    public void setSisa(String sisa) {
        this.sisa = sisa;
    }

    public String getNominalDua() {
        return NominalDua;
    }

    public void setNominalDua(String nominalDua) {
        NominalDua = nominalDua;
    }

    public String getIdPp() {
        return IdPp;
    }

    public void setIdPp(String idPp) {
        IdPp = idPp;
    }

    public String getNamaPeminjam() {
        return NamaPeminjam;
    }

    public void setNamaPeminjam(String namaPeminjam) {
        NamaPeminjam = namaPeminjam;
    }

    public String getNoKtp() {
        return NoKtp;
    }

    public void setNoKtp(String noKtp) {
        NoKtp = noKtp;
    }

    public String getNominal() {
        return Nominal;
    }

    public void setNominal(String nominal) {
        Nominal = nominal;
    }

    public String getNoHP() {
        return NoHP;
    }

    public void setNoHP(String noHP) {
        NoHP = noHP;
    }

    public String getJatuhTempo() {
        return JatuhTempo;
    }

    public void setJatuhTempo(String jatuhTempo) {
        JatuhTempo = jatuhTempo;
    }
}
