package com.aladziviesoft.kegiatandakwahfkl.utils;


public class AppConf {
    //    public static final String BASE_URL = "http://iqbal.gapoktanmajumapan.com/";
    //    public static final String BASE_URL = "http://aladziviesoft.000webhostapp.com/";
    public static final String BASE_URL = "http://192.168.43.92/";
//    public static final String BASE_URL = "http://192.168.56.1/";
//    public static final String BASE_URL = "http://192.168.1.8/";

    public static final String httpTag = "KillHttp";
    public static final String BASE_API = "api_kegiatan_dakwah_fkl/";
    private static final String BASE_INDEX_PHP = "index.php/";

    //    BASED
//    private static final String BASE_INDEX = BASE_URL + "api_majlis_taklim_mobile/index.php/";
    private static final String BASE_INDEX = BASE_URL + BASE_API + BASE_INDEX_PHP;
    //    LOGIN
    public static final String URL_LOGIN = BASE_INDEX + "auth/login";
    //    CREATE
    public static final String URL_ADD_KAS_BENDAHARA = BASE_INDEX + "kasbendahara_c/tambah_kasbendahara";
    public static final String URL_ADD_SALDO_AKHIR = BASE_INDEX + "saldo_c/tambah_saldo_akhir";
    public static final String URL_ADD_RAD = BASE_INDEX + "rad_c/tambah_rad";
    public static final String URL_ADD_KEGIATAN = BASE_INDEX + "kegiatan_c/tambah_kegiatan";
    public static final String URL_ADD_PEMBERI_PINJAMAN = BASE_INDEX + "pemberipinjaman_c/tambah_pp";
    public static final String URL_ADD_INVENTORY = BASE_INDEX + "inventory_c/tambah_inventory";
    public static final String URL_ADD_DAKWAH = BASE_INDEX + "pembayaran_c/tambah_pembayaran_dakwah";
    public static final String URL_ADD_SOSIAL = BASE_INDEX + "pembayaran_c/tambah_pembayaran_sosial";
    public static final String URL_ADD_ZAKAT = BASE_INDEX + "pembayaran_c/tambah_pembayaran_zakat";
    public static final String URL_ADD_MENABUNG = BASE_INDEX + "menabung_c/tambah_menabung";
    public static final String URL_ADD_DANAR = BASE_INDEX + "danar_c/tambah_danar";
    public static final String URL_ADD_DASIMAN = BASE_INDEX + "pembayaran_c/tambah_pembayaran_dasiman";
    public static final String URL_ADD_PENGELUARAN = BASE_INDEX + "pengeluaran_c/tambah_pengeluaran_dakwah";
    public static final String URL_ADD_PENGELUARAN_SOSIAL = BASE_INDEX + "pengeluaran_c/tambah_pengeluaran_sosial";
    public static final String URL_REGISTER = BASE_INDEX + "auth/add";
    public static final String URL_ADD_CICILAN = BASE_INDEX + "cicilan_c/tambah_cicilan";
    public static final String URL_ADD_PINJAMAN = BASE_INDEX + "pinjaman_c/tambah_pinjaman";
    public static final String URL_ADD_PENERIMAB = BASE_INDEX + "penerimabantuan_c/tambah_bantuan";
    public static final String URL_ADD_CICILPP = BASE_INDEX + "pemberipinjaman_c/tambah_cicilan";


    //    READ
    public static final String URL_LIHAT_SALDO_AKHIR = BASE_INDEX + "saldo_c/data_saldo_akhir";
    public static final String URL_EDIT_DANAR = BASE_INDEX + "danar_c/edit";
    public static final String URL_EDIT_PEMBERI_PINJAMAN = BASE_INDEX + "pemberipinjaman_c/edit";
    public static final String URL_MAJLIS_BY_ID = BASE_INDEX + "majlis_c/data_by_id";
    public static final String URL_LIST_RAD = BASE_INDEX + "rad_c/data";
    public static final String URL_PROFILE = BASE_INDEX + "profile_c/data_by_id";
    public static final String URL_LIST_USER = BASE_INDEX + "profile_c/data";
    public static final String URL_CARI_USER = BASE_INDEX + "profile_c/cari_nama_user";
    public static final String URL_PROFILE_BY_ID = BASE_INDEX + "profile_c/data_by_id";
    public static final String URL_LIST_OUT = BASE_INDEX + "pengeluaran_c/data_by_id";
    public static final String URL_DATA_KAS_BENDAHARA = BASE_INDEX + "kasbendahara_c/data";
    public static final String URL_LIST_TOTAL_PENGELUARAN = BASE_INDEX + "pengeluaran_c/total_pengeluaran";
    public static final String URL_LIST_KEGIATAN = BASE_INDEX + "kegiatan_c/data";
    public static final String URL_CARI_KEGIATAN = BASE_INDEX + "kegiatan_c/cari_nama_kegiatan";
    public static final String URL_LIST_TAAWUN = BASE_INDEX + "Pembayaran_c/data_kegiatan";
    public static final String URL_LIST_PEMBERI_PINJAMAN = BASE_INDEX + "pemberipinjaman_c/data";
    public static final String URL_DANA_TERKUMPUL = BASE_INDEX + "Pembayaran_c/dana_terkumpul";
    public static final String URL_COUNT = BASE_INDEX + "Pembayaran_c/data_status";
    public static final String URL_DETAIL_TAAWUN = BASE_INDEX + "Pembayaran_c/data_by_id";
    public static final String URL_DETAIL_PP = BASE_INDEX + "Pemberipinjaman_c/data_by_id";
    public static final String URL_DETAIL_RAD = BASE_INDEX + "rad_c/data_by_id";
    public static final String URL_DETAIL_DANAR = BASE_INDEX + "danar_c/data_by_id";
    public static final String URL_JUMLAH_DANA_TERKUMPUL_DARI_RAD_TO_KEGIATAN = BASE_INDEX + "rad_c/dana_terkumpul";
    public static final String URL_LIST_INVENTORY = BASE_INDEX + "inventory_c/data";
    public static final String URL_LIST_PENGELUARAN = BASE_INDEX + "pengeluaran_c/data";
    public static final String URL_LIST_SALDO = BASE_INDEX + "saldo_c/lihat_saldo";
    public static final String URL_LIST_MENABUNG = BASE_INDEX + "menabung_c/data";
    public static final String URL_LIST_DANAR = BASE_INDEX + "danar_c/data";
    public static final String URL_LIST_PINJAMAN = BASE_INDEX + "pinjaman_c/data";
    public static final String URL_LIST_CICILANBYID = BASE_INDEX + "cicilan_c/data_by_id";
    public static final String URL_LIST_PINJAMANBYID = BASE_INDEX + "pinjaman_c/data_by_id";
    public static final String URL_LIST_PENERIMAB = BASE_INDEX + "penerimabantuan_c/data";
    public static final String URL_LIS_CICILANPP = BASE_INDEX + "pemberipinjaman_c/cicilandata";

    //    UPDATE
    public static final String URL_EDIT_PENGELUARAN = BASE_INDEX + "pengeluaran_c/edit";
    public static final String URL_EDIT_USER = BASE_INDEX + "profile_c/edit_profile";
    public static final String URL_EDIT_FOTO = BASE_INDEX + "profile_c/edit_gambar";
    public static final String URL_EDIT_DANA_KEGIATAN = BASE_INDEX + "kegiatan_c/edit_dana_kegiatan";
    public static final String URL_EDIT_RAD = BASE_INDEX + "rad_c/edit";
    public static final String URL_EDIT_TAAWUN = BASE_INDEX + "pembayaran_c/edit";
    public static final String URL_EDIT_MENABUNG = BASE_INDEX + "menabung_c/edit";
    public static final String URL_EDIT_KAS_BENDAHARA = BASE_INDEX + "kasbendahara_c/edit";
    public static final String URL_EDIT_KAS_INVEN = BASE_INDEX + "inventory_c/edit";
    public static final String URL_EDIT_KEGIATAN = BASE_INDEX + "kegiatan_c/edit";
    public static final String URL_EDIT_PINJAMAN = BASE_INDEX + "pinjaman_c/edit";
    public static final String URL_LOGOUT = BASE_INDEX + "auth/logout";
    public static final String URL_EDIT_PENERIMAB = BASE_INDEX + "penerimabantuan_c/edit";
    public static final String URL_BAYAR_LUNAS = BASE_INDEX + "pemberipinjaman_c/bayar_lunas";


    //    DELETE
    public static final String URL_DELETE_RAD = BASE_INDEX + "rad_c/delete";
    public static final String URL_DELETE_MENABUNG = BASE_INDEX + "menabung_c/delete";
    public static final String URL_DELETE_DANAR = BASE_INDEX + "danar_c/delete";
    public static final String URL_DELETE_TAAWUN = BASE_INDEX + "pembayaran_c/delete";
    public static final String URL_DELETE_TAAWUN_SOSIAL = BASE_INDEX + "pembayaran_c/delete_taawun_sosial";
    public static final String URL_DELETE_PEMBERI_PINJAMAN = BASE_INDEX + "pemberipinjaman_c/delete";
    public static final String URL_DELETE_PENGELUARAN = BASE_INDEX + "pengeluaran_c/delete";
    public static final String URL_DELETE_PENGELUARAN_SOSIAL = BASE_INDEX + "pengeluaran_c/delete_saldo_sosial";
    public static final String URL_DELETE_KEGIATAN = BASE_INDEX + "kegiatan_c/delete";
    public static final String URL_DELETE_USER = BASE_INDEX + "profile_c/delete";
    public static final String URL_DELETE_INVENTORY = BASE_INDEX + "inventory_c/delete";
    public static final String URL_DELETE_PINJAMAN = BASE_INDEX + "pinjaman_c/delete";
    public static final String URL_DELETE_CICILAN = BASE_INDEX + "cicilan_c/delete";
    public static final String URL_DELETE_PENERIMAB = BASE_INDEX + "penerimabantuan_c/delete";
}
