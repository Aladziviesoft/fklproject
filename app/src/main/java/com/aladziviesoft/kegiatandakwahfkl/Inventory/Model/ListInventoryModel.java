package com.aladziviesoft.kegiatandakwahfkl.Inventory.Model;

public class ListInventoryModel {

    String idBarang, namaBarang, hargaBarang, banyakBarang;

    public ListInventoryModel() {
    }

    public ListInventoryModel(String idBarang, String namaBarang, String hargaBarang, String banyakBarang){
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.hargaBarang = hargaBarang;
        this.banyakBarang = banyakBarang;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getHargaBarang() {
        return hargaBarang;
    }

    public void setHargaBarang(String hargaBarang) {
        this.hargaBarang = hargaBarang;
    }

    public String getBanyakBarang() {
        return banyakBarang;
    }

    public void setBanyakBarang(String banyakBarang) {
        this.banyakBarang = banyakBarang;
    }


}
