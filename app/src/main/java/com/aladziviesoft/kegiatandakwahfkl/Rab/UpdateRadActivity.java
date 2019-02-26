package com.aladziviesoft.kegiatandakwahfkl.Rab;

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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_ADD_RAD;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DETAIL_RAD;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_EDIT_RAD;

public class UpdateRadActivity extends AppCompatActivity {

    @BindView(R.id.etNamaRencana)
    EditText etNamaRencana;
    @BindView(R.id.etNominal)
    EditText etNominal;
    @BindView(R.id.btAdd)
    Button btAdd;
    @BindView(R.id.parent_linear_layout)
    LinearLayout parentLinearLayout;
    String id_Rad, status, id_kegiatan;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;
    ConnectivityManager conMgr;
    OwnProgressDialog pDialog;
    @BindView(R.id.linearlayout)
    LinearLayout linearlayout;
    @BindView(R.id.btUpdate)
    Button btUpdate;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txDesimal)
    TextView txDesimal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_rad);
        ButterKnife.bind(this);

        cekInternet();

        requestQueue = Volley.newRequestQueue(UpdateRadActivity.this);
        pDialog = new OwnProgressDialog(UpdateRadActivity.this);
        sessionManager = new SessionManager(UpdateRadActivity.this);

        id_Rad = getIntent().getStringExtra("id_rad");
        status = getIntent().getStringExtra("status");
        id_kegiatan = getIntent().getStringExtra("id_kegiatan");

        if (status.equals("0")) {
            btAdd.setVisibility(View.VISIBLE);
            btUpdate.setVisibility(View.GONE);
        } else if (status.equals("1")) {
            btAdd.setVisibility(View.GONE);
            btUpdate.setVisibility(View.VISIBLE);
        }
        DetailRad();

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

    private void TambahRad() {
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_ADD_RAD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(UpdateRadActivity.this, "Rencana Anggaran Tertambahkan");
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
                    params.put("id_users", AESCrypt.decrypt("id_user", sessionManager.getIduser()));
                    params.put("id_kegiatan", id_kegiatan);
                    params.put("nama_rad", etNamaRencana.getText().toString());
                    params.put("biaya", etNominal.getText().toString());

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("params", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);

    }


    private void DetailRad() {
        stringRequest = new StringRequest(Request.Method.POST, URL_DETAIL_RAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("detail", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        etNamaRencana.setText(json.getString("nama_rad"));
                        etNominal.setText(json.getString("biaya"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                    params.put("id_rad", id_Rad);
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

    private void UpdateData() {
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_EDIT_RAD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(UpdateRadActivity.this, "Data RAD berhasil dirubah");
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
                    params.put("id_rad", id_Rad);
                    params.put("biaya", etNominal.getText().toString());
                    params.put("nama_rad", etNamaRencana.getText().toString());
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("params", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);

    }

    @OnClick({R.id.btUpdate, R.id.btAdd, R.id.imgBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btUpdate:
                if (etNamaRencana.getText().toString().trim().length() > 0 && etNamaRencana.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        UpdateData();
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
            case R.id.btAdd:
                if (etNamaRencana.getText().toString().trim().length() > 0 && etNamaRencana.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        TambahRad();
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
        }
    }
}
