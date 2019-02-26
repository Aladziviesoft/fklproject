package com.aladziviesoft.kegiatandakwahfkl.User.Model;

public class ListUserModel {
    String id_user, nama_user, level;

    public ListUserModel(String id_user, String nama_user, String level) {
        this.id_user = id_user;
        this.nama_user = nama_user;
        this.level = level;
    }

    public ListUserModel() {
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getNama_user() {
        return nama_user;
    }

    public void setNama_user(String nama_user) {
        this.nama_user = nama_user;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
