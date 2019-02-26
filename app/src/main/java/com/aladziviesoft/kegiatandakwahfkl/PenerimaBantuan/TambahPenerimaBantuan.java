package com.aladziviesoft.kegiatandakwahfkl.PenerimaBantuan;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.Taawun.TambahPembayarActivity;
import com.aladziviesoft.kegiatandakwahfkl.utils.AndLog;
import com.aladziviesoft.kegiatandakwahfkl.utils.DecimalsFormat;
import com.aladziviesoft.kegiatandakwahfkl.utils.GlobalToast;
import com.aladziviesoft.kegiatandakwahfkl.utils.OwnProgressDialog;
import com.aladziviesoft.kegiatandakwahfkl.utils.SessionManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_ADD_PENERIMAB;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_EDIT_PENERIMAB;

public class TambahPenerimaBantuan extends AppCompatActivity {

    @BindView(R.id.etnama)
    EditText etnama;
    @BindView(R.id.etNominal)
    EditText etNominal;
    @BindView(R.id.btSimpan)
    Button btSimpan;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.uang)
    TextView uang;
    @BindView(R.id.textView5)
    TextView textView5;
    @BindView(R.id.danariba)
    RadioButton danariba;
    @BindView(R.id.zakat)
    RadioButton zakat;
    @BindView(R.id.rgrub)
    RadioGroup rgrub;
    @BindView(R.id.parent_linear_layout)
    LinearLayout parentLinearLayout;
    @BindView(R.id.btUpdate)
    Button btUpdate;
    SessionManager sessionManager;
    OwnProgressDialog pDialog;
    ConnectivityManager conMgr;
    StringRequest stringRequest;
    private RequestQueue requestQueue;
    String id_penerima, ids, status_byr, status_simpan, nominal, namapenerima;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_penerima_bantuan);
        ButterKnife.bind(this);
        cekInternet();

        sessionManager = new SessionManager(TambahPenerimaBantuan.this);
        pDialog = new OwnProgressDialog(TambahPenerimaBantuan.this);
        requestQueue = Volley.newRequestQueue(TambahPenerimaBantuan.this);

        intent = getIntent();
        status_simpan = intent.getStringExtra("status_simpan");


        if (status_simpan.equals("1")) {
            id_penerima =intent.getStringExtra("id_penerimab");
            ids = intent.getStringExtra("jenis");
            nominal = intent.getStringExtra("nomi");
            namapenerima = intent.getStringExtra("nama");
            if (ids.equals("2")) {
                danariba.setChecked(true);
                zakat.setChecked(false);
            } else {
                danariba.setChecked(false);
                zakat.setChecked(true);
            }
            etnama.setText(namapenerima);
            etNominal.setText(nominal);
            btUpdate.setVisibility(View.VISIBLE);
            btSimpan.setVisibility(View.GONE);
        } else {
            btUpdate.setVisibility(View.GONE);
            btSimpan.setVisibility(View.VISIBLE);
        }

        etNominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                uang.setText("Rp" + DecimalsFormat.priceWithoutDecimal(etNominal.getText().toString()));

            }

            @Override
            public void afterTextChanged(Editable s) {
//                DecimalsFormat.priceWithoutDecimal(txSaldo.getText().toString());
            }
        });


    }

    public void onRadioButtonClicked() {
        // Is the button now checked?
        int id = rgrub.getCheckedRadioButtonId();
        switch (id) {
            case R.id.danariba:
                status_byr = "2";
                break;
            case R.id.zakat:
                status_byr = "6";
                break;
        }
    }

    private void SimpanData() {
        onRadioButtonClicked();
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_ADD_PENERIMAB, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(TambahPenerimaBantuan.this, "berhasil ditambahkan");

                pDialog.dismiss();
                finish();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Ada Kesalahan", Toast.LENGTH_LONG).show();
                AndLog.ShowLog("errss", String.valueOf(error));
                pDialog.dismiss();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                String token, id_majlis, apikey, refresh_code;
                token = sessionManager.getToken();
                apikey = sessionManager.getApikey();
                refresh_code = sessionManager.getRefreshcode();


                Map<String, String> params = new HashMap<String, String>();
                try {

                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                    params.put("id_user", AESCrypt.decrypt("id_user", sessionManager.getIduser()));
                    params.put("id_saldo", status_byr);
                    params.put("nama", etnama.getText().toString());
                    params.put("nominal", etNominal.getText().toString());
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("params", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);

    }

    public void cekInternet() {
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void UpdateData() {
        onRadioButtonClicked();
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_EDIT_PENERIMAB, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(TambahPenerimaBantuan.this, "Data berhasil dirubah");
                pDialog.dismiss();
                finish();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Ada Kesalahan", Toast.LENGTH_LONG).show();
                AndLog.ShowLog("errss", String.valueOf(error));
                pDialog.dismiss();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                String token, apikey, refresh_code;
                token = sessionManager.getToken();
                apikey = sessionManager.getApikey();
                refresh_code = sessionManager.getRefreshcode();

                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                    params.put("id_penerimabantuan", id_penerima);
                    params.put("id_saldo", status_byr);
                    params.put("nama", etnama.getText().toString());
                    params.put("nominal", etNominal.getText().toString());
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("params", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);

    }


    @OnClick({R.id.imgBack, R.id.btSimpan, R.id.btUpdate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.btSimpan:
                if (etnama.getText().toString().trim().length() > 0 && uang.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        SimpanData();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(parentLinearLayout, "No Internet Connection, Try Again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        pDialog.dismiss();
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLinearLayout, "Kolom tidak boleh kosong, silahkan di isi", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
            case R.id.btUpdate:
                if (etnama.getText().toString().trim().length() > 0 && uang.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        UpdateData();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(parentLinearLayout, "No Internet Connection, Try Again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        pDialog.dismiss();
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLinearLayout, "Kolom tidak boleh kosong, silahkan di isi", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
        }
    }
}
