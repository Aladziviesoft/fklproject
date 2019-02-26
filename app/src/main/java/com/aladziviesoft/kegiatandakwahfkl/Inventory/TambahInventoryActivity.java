package com.aladziviesoft.kegiatandakwahfkl.Inventory;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.R;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_ADD_INVENTORY;

public class TambahInventoryActivity extends AppCompatActivity {

    @BindView(R.id.namabarang)
    EditText namabarang;
    @BindView(R.id.hargabarang)
    EditText hargabarang;
    @BindView(R.id.banyakbarang)
    EditText banyakbarang;
    @BindView(R.id.simpaninven)
    Button simpaninven;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.linearlayout)
    LinearLayout linearlayout;
    ConnectivityManager conMgr;
    @BindView(R.id.txDesimal)
    TextView txDesimal;
    private RequestQueue requestQueue;
    AppCompatButton btRegister;
    OwnProgressDialog pDialog;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_inventory);
        ButterKnife.bind(this);

        cekInternet();

        sessionManager = new SessionManager(TambahInventoryActivity.this);
        pDialog = new OwnProgressDialog(TambahInventoryActivity.this);
        requestQueue = Volley.newRequestQueue(TambahInventoryActivity.this);

        hargabarang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txDesimal.setText("Rp" + DecimalsFormat.priceWithoutDecimal(hargabarang.getText().toString()));

            }

            @Override
            public void afterTextChanged(Editable s) {
//                DecimalsFormat.priceWithoutDecimal(txSaldo.getText().toString());
            }
        });

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

    private void SimpanData() {
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_ADD_INVENTORY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(TambahInventoryActivity.this, "Inventory berhasil ditambahkan");
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
                id_majlis = sessionManager.getIdMajlis();

                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("id_majlis", AESCrypt.decrypt("id_majlis", id_majlis));
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                    params.put("id_users", AESCrypt.decrypt("id_user", sessionManager.getIduser()));
                    params.put("nama_barang", namabarang.getText().toString());
                    params.put("harga", hargabarang.getText().toString());
                    params.put("qty", banyakbarang.getText().toString());

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("params", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);

    }

    @OnClick({R.id.imgBack, R.id.simpaninven})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.simpaninven:
                if (namabarang.getText().toString().trim().length() > 0 &&
                        hargabarang.getText().toString().trim().length() > 0 &&
                        banyakbarang.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        SimpanData();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(linearlayout, "No Internet Connection, Try Again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        pDialog.dismiss();
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(linearlayout, "Kolom tidak boleh kosong, silahkan di isi", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
        }
    }
}
