package com.aladziviesoft.kegiatandakwahfkl.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

    SharedPreferences pref;

    Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "com.aladziviesoft.data.taawun";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String FIRSTLOOK = "firstLook";

    public static final String KEY_IDUSER = "id_users";

    public static final String KEY_NAMA = "name";

    public static final String TOKEN = "token";

    public static final String APIKEY = "apikey";

    public static final String REFRESHCODE = "refresh_code";

    public static final String LEVEL = "level";

    public static final String ID_KEGIATAN = "id_kegiatan";

    public static final String IMAGE = "image";

    public static final String ID_MAJLIS = "id_majlis";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String iduser, String nama, String apikey, String token, String refresh_code) {

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_IDUSER, iduser);

        editor.putString(KEY_NAMA, nama);

        editor.putString(APIKEY, apikey);

        editor.putString(TOKEN, token);

        editor.putString(REFRESHCODE, refresh_code);

        editor.commit();
    }

    public boolean checkLogin() {
        // Check login status

        boolean stLogin = true;

        if (!this.isLoggedIn()) {

            stLogin = false;
        }

        return stLogin;

    }

    public void setLogin() {
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }

    public void setFirstlook() {

        editor.putBoolean(FIRSTLOOK, true);
        editor.commit();
    }

    public boolean checkFirstLook() {
        // Check login status

        boolean stLook = true;

        if (!this.isFirstLook()) {

            stLook = false;
        }

        return stLook;

    }

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        boolean look = false;
        if (this.checkFirstLook()) {
            look = true;
        }

        String iduser = this.getIduser();

        editor.clear();
        editor.commit();


        this.setIduser(iduser);

        if (look) {
            this.setFirstlook();
        }


    }





    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean isFirstLook() {
        return pref.getBoolean(FIRSTLOOK, false);
    }

    public String getIduser() {
        return pref.getString(KEY_IDUSER, null);
    }

    public String getNama() {
        return pref.getString(KEY_NAMA, null);
    }

    public String getLevel() {
        return pref.getString(LEVEL, null);
    }

    public String getRefreshcode() {
        return pref.getString(REFRESHCODE, null);
    }

    public String getApikey() {
        return pref.getString(APIKEY, null);
    }

    public String getToken() {
        return pref.getString(TOKEN, null);
    }


    public String getImage() {
        return pref.getString(IMAGE, null);
    }

    public String getIdMajlis() {
        return pref.getString(ID_MAJLIS, null);
    }

    public void setImage(String val) {
        editor.putString(IMAGE, val);
        editor.commit();
    }

    public void setIdMajlis(String val) {
        editor.putString(ID_MAJLIS, val);
        editor.commit();
    }

    public String getIdKegiatan() {
        return pref.getString(ID_KEGIATAN, null);
    }

    public void setIdKegiatan(String val) {
        editor.putString(ID_KEGIATAN, val);
        editor.commit();
    }

    public void setApiKey(String val) {
        editor.putString(APIKEY, val);
        editor.commit();
    }

    public void setNama(String nama) {
        editor.putString(KEY_NAMA, nama);
        editor.commit();
    }

    public void setIduser(String iduser) {
        editor.putString(KEY_IDUSER, iduser);
        editor.commit();
    }

    public void setToken(String value) {
        editor.putString(TOKEN, value);
        editor.commit();
    }

    public void setRefreshcode(String value) {
        editor.putString(REFRESHCODE, value);
        editor.commit();
    }

    public void setLevel(String level) {
        editor.putString(LEVEL, level);
        editor.commit();
    }
}