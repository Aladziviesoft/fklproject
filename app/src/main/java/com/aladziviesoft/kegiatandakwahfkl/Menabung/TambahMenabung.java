package com.aladziviesoft.kegiatandakwahfkl.Menabung;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.utils.AndLog;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_ADD_MENABUNG;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_EDIT_MENABUNG;

public class TambahMenabung extends AppCompatActivity {

    @BindView(R.id.etnamasjid)
    EditText etnamasjid;
    @BindView(R.id.pilihtanggal)
    Button pilihtanggal;
    @BindView(R.id.txTanggal)
    TextView txTanggal;
    @BindView(R.id.btSimpan)
    Button btSimpan;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.parent_linear_layout)
    LinearLayout parentLinearLayout;
    @BindView(R.id.linearlayout)
    LinearLayout linearlayout;
    @BindView(R.id.btUpdate)
    Button btUpdate;
    SessionManager sessionManager;
    OwnProgressDialog pDialog;
    ConnectivityManager conMgr;
    StringRequest stringRequest;
    String id_menabung, status_simpan, nama_masjid, tgl;
    @BindView(R.id.txTgl)
    TextView txTgl;
    @BindView(R.id.textView5)
    TextView textView5;
    @BindView(R.id.rbRencana)
    RadioButton rbRencana;
    @BindView(R.id.rbSukses)
    RadioButton rbSukses;
    @BindView(R.id.rgrub)
    RadioGroup rgrub;
    String status;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormat;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_menabung);
        ButterKnife.bind(this);
        cekInternet();

        sessionManager = new SessionManager(TambahMenabung.this);
        pDialog = new OwnProgressDialog(TambahMenabung.this);
        requestQueue = Volley.newRequestQueue(TambahMenabung.this);


        id_menabung = getIntent().getStringExtra("id_menabung");
        status_simpan = getIntent().getStringExtra("status_simpan");
        nama_masjid = getIntent().getStringExtra("nama_masjid");
        tgl = getIntent().getStringExtra("tgl");
        status = getIntent().getStringExtra("status");


        etnamasjid.setText(nama_masjid);
        txTanggal.setText(tgl);

        if (status_simpan.equals("1")) {
            btUpdate.setVisibility(View.VISIBLE);
            btSimpan.setVisibility(View.GONE);
            if (status.equals("0")) {
                rbRencana.setChecked(true);
                rbSukses.setChecked(false);
            } else {
                rbRencana.setChecked(false);
                rbSukses.setChecked(true);
            }
        } else {
            btUpdate.setVisibility(View.GONE);
            btSimpan.setVisibility(View.VISIBLE);
        }

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");


    }

    public void onRadioButtonClicked() {
        // Is the button now checked?
        int id = rgrub.getCheckedRadioButtonId();
        switch (id) {
            case R.id.rbSukses:
                status = "1";
                break;
            case R.id.rbRencana:
                status = "0";
                break;
        }
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

    private void UpdateMenabung() {
        onRadioButtonClicked();
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_EDIT_MENABUNG, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(TambahMenabung.this, "Menabung berhasil dirubah");
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
                    params.put("id_users", AESCrypt.decrypt("id_user", sessionManager.getIduser()));
                    params.put("id_menabung", id_menabung);
                    params.put("nama_masjid", etnamasjid.getText().toString());
                    params.put("tgl", txTanggal.getText().toString());
                    params.put("status", status);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("paramsmenabung", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);

    }

    @OnClick({R.id.pilihtanggal, R.id.btSimpan, R.id.imgBack, R.id.btUpdate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pilihtanggal:
                showtanggal();
                break;

            case R.id.btUpdate:
                if (etnamasjid.getText().toString().trim().length() > 0 && txTanggal.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        UpdateMenabung();
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
            case R.id.btSimpan:
                if (etnamasjid.getText().toString().trim().length() > 0 && txTanggal.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        TambahMenabung();
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


    private void TambahMenabung() {
        onRadioButtonClicked();
        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_ADD_MENABUNG, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("menabung :", response);
                GlobalToast.ShowToast(TambahMenabung.this, "Nama Masjid ditambahkan");
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
                    params.put("nama_masjid", etnamasjid.getText().toString());
                    params.put("tgl", txTanggal.getText().toString());
                    params.put("status", status);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("params_saldo_akhir", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);

    }

    private void showtanggal() {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(TambahMenabung.this, new DatePickerDialog.OnDateSetListener() {

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
