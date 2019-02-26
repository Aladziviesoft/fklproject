package com.aladziviesoft.kegiatandakwahfkl.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.aladziviesoft.kegiatandakwahfkl.R;


public class OwnProgressDialog {

    public AlertDialog.Builder builder;
    public Dialog dialog;
    public Context context;

    public OwnProgressDialog(Context context) {
        this.context = context;
        this.builder = new AlertDialog.Builder(context);
        this.builder.setView(R.layout.progress);
        this.dialog = builder.create();
        this.dialog.setCancelable(true);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
