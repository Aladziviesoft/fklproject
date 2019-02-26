package com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.utils.OwnProgressDialog;
import com.aladziviesoft.kegiatandakwahfkl.utils.SessionManager;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OptionPengembalian extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.btKontan)
    Button btKontan;
    @BindView(R.id.btSebagian)
    Button btSebagian;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_pengembalian);
        ButterKnife.bind(this);
        requestQueue = Volley.newRequestQueue(this);
        loading = new OwnProgressDialog(this);
        sessionManager = new SessionManager(this);
    }

    @OnClick({R.id.imgBack, R.id.btKontan, R.id.btSebagian})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.btKontan:
                bayar();
                break;
            case R.id.btSebagian:
                intent = new Intent(getApplicationContext(), PengembalianSebagian.class);
                startActivity(intent);
                break;
        }
    }

    private void bayar() {
        final CheckBox checkBox = new CheckBox(OptionPengembalian.this);
        checkBox.setText("Bayar Lunas Pinjaman");
        new AlertDialog.Builder(OptionPengembalian.this)
                .setTitle("Bayar Cicilan")
                .setView(checkBox)
                .setPositiveButton("Bayar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkBox.isChecked()) {
//                            bayarpinjaman(idpinjam, nominalbayar);
                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
