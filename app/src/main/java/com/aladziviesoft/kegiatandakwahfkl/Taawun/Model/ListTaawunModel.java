package com.aladziviesoft.kegiatandakwahfkl.Taawun.Model;

public class ListTaawunModel {
    String Id, Nama, JumlahUang, Tanggal, nominal, Status, IdKegiatan;

    public ListTaawunModel() {
    }

    public ListTaawunModel(String id, String nama, String jumlahUang, String tanggal, String status, String idKegiatan) {
        this.Id = id;
        this.Nama = nama;
        this.JumlahUang = jumlahUang;
        this.Tanggal = tanggal;
        this.Status = status;
        this.IdKegiatan = idKegiatan;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getJumlahUang() {
        return JumlahUang;
    }

    public void setJumlahUang(String jumlahUang) {
        JumlahUang = jumlahUang;
    }

    public String getTanggal() {
        return Tanggal;
    }

    public void setTanggal(String tanggal) {
        Tanggal = tanggal;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getIdKegiatan(){
        return IdKegiatan;
    }

    public void setIdKegiatan(String idKegiatan){
        IdKegiatan = idKegiatan;
    }
}
