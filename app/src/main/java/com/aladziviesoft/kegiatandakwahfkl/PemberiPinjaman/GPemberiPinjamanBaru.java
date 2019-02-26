package com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_ADD_PEMBERI_PINJAMAN;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DETAIL_PP;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_EDIT_PEMBERI_PINJAMAN;

public class GPemberiPinjamanBaru extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.etNoKtp)
    EditText etNoKtp;
    @BindView(R.id.etNoHp)
    EditText etNoHp;
    @BindView(R.id.etNominal)
    EditText etNominal;
    @BindView(R.id.pilihtanggal)
    Button pilihtanggal;
    @BindView(R.id.txTanggal)
    TextView txTanggal;
    @BindView(R.id.parent_linear_layout)
    LinearLayout parentLinearLayout;
    @BindView(R.id.btSimpan)
    Button btSimpan;
    @BindView(R.id.btUpdate)
    Button btUpdate;
    String idpp, status_simpan;
    ConnectivityManager conMgr;
    StringRequest stringRequest;
    @BindView(R.id.linearlayout)
    LinearLayout linearlayout;
    SessionManager sessionManager;
    OwnProgressDialog pDialog;
    @BindView(R.id.etNamaPemberiPinjaman)
    EditText etNamaPemberiPinjaman;
    @BindView(R.id.txStatus)
    TextView txStatus;
    @BindView(R.id.txDesimal)
    TextView txDesimal;
    @BindView(R.id.ceksts)
    CheckBox ceksts;
    @BindView(R.id.status)
    LinearLayout status;
    private RequestQueue requestQueue;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpemberi_pinjaman_baru);
        ButterKnife.bind(this);

        cekInternet();

        sessionManager = new SessionManager(GPemberiPinjamanBaru.this);
        pDialog = new OwnProgressDialog(GPemberiPinjamanBaru.this);
        requestQueue = Volley.newRequestQueue(GPemberiPinjamanBaru.this);
        idpp = getIntent().getStringExtra("id_pp");

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");

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
        status_simpan = getIntent().getStringExtra("status_simpan");
        if (status_simpan.equals("1")) {
            btUpdate.setVisibility(View.VISIBLE);
            btSimpan.setVisibility(View.GONE);
            DetailPemberiPinjaman();
            etNominal.setEnabled(false);
        } else {
            btUpdate.setVisibility(View.GONE);
            btSimpan.setVisibility(View.VISIBLE);
        }
    }

    private void DetailPemberiPinjaman() {
        pDialog.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_DETAIL_PP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("detailpp", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        etNamaPemberiPinjaman.setText(json.getString("nama_peminjam"));
                        etNoKtp.setText(json.getString("ktp"));
                        etNoHp.setText(json.getString("nohp"));
                        etNominal.setText(json.getString("nominal"));
                        txTanggal.setText(json.getString("tempo"));
                        txStatus.setText(json.getString("status"));
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
                    params.put("id_pp", idpp);
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));

                    AndLog.ShowLog("paramspp", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };

        requestQueue.add(stringRequest);
    }

    private void showtanggal() {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(GPemberiPinjamanBaru.this, new DatePickerDialog.OnDateSetListener() {

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


    private void SimpanData() {
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_ADD_PEMBERI_PINJAMAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(GPemberiPinjamanBaru.this, "Kegiatan berhasil ditambahkan");
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
                    params.put("nama_peminjam", etNamaPemberiPinjaman.getText().toString());
                    params.put("ktp", etNoKtp.getText().toString());
                    params.put("nohp", etNoHp.getText().toString());
                    params.put("nominal", etNominal.getText().toString());
                    params.put("tempo", txTanggal.getText().toString());
                    params.put("status", "0");

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
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_EDIT_PEMBERI_PINJAMAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(GPemberiPinjamanBaru.this, "Data berhasil diedit");
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
                    params.put("id_pp", idpp);
                    params.put("nama_peminjam", etNamaPemberiPinjaman.getText().toString());
                    params.put("ktp", etNoKtp.getText().toString());
                    params.put("nohp", etNoHp.getText().toString());
                    params.put("nominal", etNominal.getText().toString());
                    params.put("tempo", txTanggal.getText().toString());
                    params.put("status", "0");

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
                if (etNamaPemberiPinjaman.getText().toString().trim().length() > 0 &&
                        etNoKtp.getText().toString().trim().length() > 0 &&
                        etNominal.getText().toString().trim().length() > 0 &&
                        etNoHp.getText().toString().trim().length() > 0 &&
                        txTanggal.getText().toString().trim().length() > 0) {
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
            case R.id.btUpdate:
                if (etNamaPemberiPinjaman.getText().toString().trim().length() > 0 &&
                        etNoKtp.getText().toString().trim().length() > 0 &&
                        etNominal.getText().toString().trim().length() > 0 &&
                        etNoHp.getText().toString().trim().length() > 0 &&
                        txTanggal.getText().toString().trim().length() > 0) {
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
        }
    }
}
