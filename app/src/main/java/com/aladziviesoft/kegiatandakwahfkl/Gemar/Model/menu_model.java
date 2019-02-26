package com.aladziviesoft.kegiatandakwahfkl.Gemar.Model;

public class menu_model {
    String id,namajudul;

    public menu_model(String id, String namajudul) {
        this.id = id;
        this.namajudul = namajudul;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamajudul() {
        return namajudul;
    }

    public void setNamajudul(String namajudul) {
        this.namajudul = namajudul;
    }
}
