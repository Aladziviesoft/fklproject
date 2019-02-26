package com.aladziviesoft.kegiatandakwahfkl.Pengeluaran;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_ADD_PENGELUARAN;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_PENGELUARAN;

public class TambahPengeluaranActivity extends AppCompatActivity {

    @BindView(R.id.btSimpan)
    Button btSimpan;
    @BindView(R.id.btUpdate)
    Button btUpdate;
    @BindView(R.id.etKeperluan)
    EditText etKeperluan;
    @BindView(R.id.etNominal)
    EditText etNominal;
    @BindView(R.id.parent_linear_layout)
    LinearLayout parentLinearLayout;
    @BindView(R.id.linearlayout)
    LinearLayout linearlayout;
    SessionManager sessionManager;
    OwnProgressDialog pDialog;
    String idkeg;
    ConnectivityManager conMgr;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txDesimal)
    TextView txDesimal;
    StringRequest stringRequest;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengeluaran);
        ButterKnife.bind(this);
        cekInternet();

        sessionManager = new SessionManager(TambahPengeluaranActivity.this);
        pDialog = new OwnProgressDialog(TambahPengeluaranActivity.this);
        requestQueue = Volley.newRequestQueue(TambahPengeluaranActivity.this);

        idkeg = getIntent().getStringExtra("id_kegiatan");
        etNominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txDesimal.setText("Rp" + DecimalsFormat.priceWithoutDecimal(etNominal.getText().toString()));

            }

            @Override
            public void afterTextChanged(Editable s) {
//                DecimalsFormat.priceWithoutDecimal(txSaldo.getText().toString());
            }
        });

//        DetailDataPengeluaran();
    }

    private void TambahPengeluaran() {
        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_ADD_PENGELUARAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("respengeluaran", response);
                GlobalToast.ShowToast(TambahPengeluaranActivity.this, "Pengeluaran ditambahkan");
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
                finish();
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
                    params.put("id_users", AESCrypt.decrypt("id_user", sessionManager.getIduser()));
                    params.put("id_kegiatan", idkeg);
                    params.put("keperluan", etKeperluan.getText().toString());
                    params.put("nominal", etNominal.getText().toString());

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("params_saldo_akhir", String.valueOf(params));
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

    private void DetailDataPengeluaran() {
        pDialog.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_PENGELUARAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("detail", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        etKeperluan.setText(json.getString("keperluan"));
                        etNominal.setText(json.getString("nominal"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AndLog.ShowLog("error detail taawun", String.valueOf(error));
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

                    AndLog.ShowLog("params", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };

        requestQueue.add(stringRequest);
    }

    @OnClick({R.id.btSimpan, R.id.btUpdate, R.id.imgBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btSimpan:
                if (etKeperluan.getText().toString().trim().length() > 0 && etNominal.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        TambahPengeluaran();
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
            case R.id.imgBack:
                finish();
                break;
            case R.id.btUpdate:

                break;
        }
    }
}
