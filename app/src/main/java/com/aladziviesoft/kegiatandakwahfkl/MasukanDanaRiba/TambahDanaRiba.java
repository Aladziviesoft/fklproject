package com.aladziviesoft.kegiatandakwahfkl.MasukanDanaRiba;

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
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_ADD_DANAR;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DETAIL_DANAR;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_EDIT_DANAR;

public class TambahDanaRiba extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.etnama)
    EditText etnama;
    @BindView(R.id.etNominal)
    EditText etNominal;
    @BindView(R.id.btSimpan)
    Button btSimpan;
    SessionManager sessionManager;
    OwnProgressDialog pDialog;
    ConnectivityManager conMgr;
    SimpleDateFormat dateFormat;
    @BindView(R.id.parent_linear_layout)
    LinearLayout parentLinearLayout;
    @BindView(R.id.linearlayout)
    LinearLayout linearlayout;
    @BindView(R.id.txDesimal)
    TextView txDesimal;
    @BindView(R.id.btUpdate)
    Button btUpdate;
    String status_simpan, id_danar;
    StringRequest stringRequest;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_dana_riba);
        ButterKnife.bind(this);

        cekInternet();

        sessionManager = new SessionManager(TambahDanaRiba.this);
        pDialog = new OwnProgressDialog(TambahDanaRiba.this);
        requestQueue = Volley.newRequestQueue(TambahDanaRiba.this);

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

        id_danar = getIntent().getStringExtra("id_danar");
        status_simpan = getIntent().getStringExtra("status_simpan");


        if (status_simpan.equals("1")) {
            btUpdate.setVisibility(View.VISIBLE);
            btSimpan.setVisibility(View.GONE);
            etNominal.setEnabled(false);
            DetailDanar();
        } else {
            btUpdate.setVisibility(View.GONE);
            btSimpan.setVisibility(View.VISIBLE);
        }


//        dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    }

    private void DetailDanar() {
        pDialog.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_DETAIL_DANAR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("detail", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        etnama.setText(json.getString("nama"));
                        etNominal.setText(json.getString("nominal"));
                    }

                    pDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                AndLog.ShowLog("errTambahDanaRiba", "er" + error);
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
                    params.put("id_danar", id_danar);
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

    private void TambahDanar() {
        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_ADD_DANAR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("menabung :", response);
                GlobalToast.ShowToast(TambahDanaRiba.this, "Dana Riba ditambahkan");
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
                    params.put("nama", etnama.getText().toString());
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

    private void UpdateDanar() {
        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_EDIT_DANAR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("menabung :", response);
                GlobalToast.ShowToast(TambahDanaRiba.this, "Dana Riba Dirubah");
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
                    params.put("nama", etnama.getText().toString());
                    params.put("id_danar", id_danar);
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

//    private void showtanggal() {
//        Calendar newCalendar = Calendar.getInstance();
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(TambahMenabung.this, new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//
//                Calendar newDate = Calendar.getInstance();
//                newDate.set(year, monthOfYear, dayOfMonth);
//                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//                txTanggal.setText(dateFormatter.format(newDate.getTime()));
//            }
//
//        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//
//        datePickerDialog.show();
//    }

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

    @OnClick({R.id.imgBack, R.id.btSimpan, R.id.btUpdate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.btSimpan:
                if (etnama.getText().toString().trim().length() > 0 && etNominal.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        TambahDanar();
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
            case R.id.btUpdate:
                if (etnama.getText().toString().trim().length() > 0 && etNominal.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        UpdateDanar();
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
