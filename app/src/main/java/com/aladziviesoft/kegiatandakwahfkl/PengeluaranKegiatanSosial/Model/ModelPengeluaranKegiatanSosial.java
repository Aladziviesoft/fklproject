package com.aladziviesoft.kegiatandakwahfkl.PengeluaranKegiatanSosial.Model;

public class ModelPengeluaranKegiatanSosial {
    String id_pengeluaran,keperluan, nominal, nominalset, created_at;

    public ModelPengeluaranKegiatanSosial() {
    }

    public ModelPengeluaranKegiatanSosial(String id_pengeluaran, String keperluan, String nominal, String created_at) {
        this.id_pengeluaran = id_pengeluaran;
        this.keperluan = keperluan;
        this.nominal = nominal;
        this.created_at = created_at;
    }

    public String getNominalset() {
        return nominalset;
    }

    public void setNominalset(String nominalset) {
        this.nominalset = nominalset;
    }

    public String getId_pengeluaran() {
        return id_pengeluaran;
    }

    public void setId_pengeluaran(String id_pengeluaran) {
        this.id_pengeluaran = id_pengeluaran;
    }

    public String getKeperluan() {
        return keperluan;
    }

    public void setKeperluan(String keperluan) {
        this.keperluan = keperluan;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
