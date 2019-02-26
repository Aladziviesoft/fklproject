package com.aladziviesoft.kegiatandakwahfkl.Pinjaman;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_ADD_PINJAMAN;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_EDIT_PINJAMAN;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_PINJAMANBYID;

public class TambahPinjamanActivity extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.etnamapeminjam)
    EditText etnamapeminjam;
    @BindView(R.id.etNominal)
    EditText etNominal;
    @BindView(R.id.txTanggal)
    TextView txTanggal;
    @BindView(R.id.btSimpan)
    Button btSimpan;
    @BindView(R.id.btUpdate)
    Button btUpdate;
    @BindView(R.id.pilihtanggal)
    Button pilihtanggal;
    @BindView(R.id.parent_linear_layout)
    LinearLayout parentLinearLayout;
    @BindView(R.id.txDesimal)
    TextView txDesimal;
    String id;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;
    ConnectivityManager conMgr;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pinjaman);
        ButterKnife.bind(this);
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        cekInternet();
        requestQueue = Volley.newRequestQueue(this);
        loading = new OwnProgressDialog(this);
        sessionManager = new SessionManager(this);

        Intent intent = getIntent();
        String b = intent.getStringExtra("e");

        if (b.equals("a")) {
            btSimpan.setVisibility(View.GONE);
            btUpdate.setVisibility(View.VISIBLE);
            id = intent.getStringExtra("id");
            getdata();
            loading.show();
        }

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

    private void getdata() {
        loading.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_PINJAMANBYID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ds", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        String nma = json.getString("nama");
                        String nm = json.getString("nominal");
                        String jth = json.getString("jatuh_tempo");

                        etnamapeminjam.setText(nma);
                        etNominal.setText(nm);
                        txTanggal.setText(jth);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loading.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                    params.put("id_pinjaman", id);

                    AndLog.ShowLog("params", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        }

        ;

        requestQueue.add(stringRequest);
    }


    @OnClick({R.id.imgBack, R.id.pilihtanggal, R.id.btSimpan, R.id.btUpdate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.pilihtanggal:
                showtanggal();
                break;
            case R.id.btSimpan:
                if (etnamapeminjam.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        SimpanData();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(parentLinearLayout, "No Internet Connection, Try Again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        loading.dismiss();
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLinearLayout, "Kolom tidak boleh kosong, silahkan di isi", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
            case R.id.btUpdate:
                if (etnamapeminjam.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        UpdateData();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(parentLinearLayout, "No Internet Connection, Try Again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        loading.dismiss();
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(parentLinearLayout, "Kolom tidak boleh kosong, silahkan di isi", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
        }
    }

    private void SimpanData() {
        loading.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_ADD_PINJAMAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(TambahPinjamanActivity.this, "Pinjaman berhasil ditambahkan");
                loading.dismiss();
                finish();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Ada Kesalahan", Toast.LENGTH_LONG).show();
                AndLog.ShowLog("errss", String.valueOf(error));
                loading.dismiss();

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
                    params.put("id_user", AESCrypt.decrypt("id_user", sessionManager.getIduser()));
                    params.put("nama", etnamapeminjam.getText().toString());
                    params.put("nominal", etNominal.getText().toString());
                    params.put("jatuh_tempo", txTanggal.getText().toString());


                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("params", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);

    }


    private void UpdateData() {
        loading.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_EDIT_PINJAMAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(TambahPinjamanActivity.this, "Data Pinjaman berhasil dirubah");
                loading.dismiss();
                finish();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Ada Kesalahan", Toast.LENGTH_LONG).show();
                AndLog.ShowLog("errss", String.valueOf(error));
                loading.dismiss();

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
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                    params.put("id_pinjaman", id);
                    params.put("nama", etnamapeminjam.getText().toString());
                    params.put("nominal", etNominal.getText().toString());
                    params.put("jatuh_tempo", txTanggal.getText().toString());

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

    private void showtanggal() {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(TambahPinjamanActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                txTanggal.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
}
