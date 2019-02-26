package com.aladziviesoft.kegiatandakwahfkl.Taawun;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_ADD_DAKWAH;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DETAIL_TAAWUN;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_EDIT_TAAWUN;

public class TambahPembayarActivity extends AppCompatActivity {

    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.nama)
    EditText nama;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.uang)
    EditText uang;
    @BindView(R.id.textView5)
    TextView textView5;
    @BindView(R.id.lunas)
    RadioButton lunas;
    @BindView(R.id.tidaklunas)
    RadioButton tidaklunas;
    @BindView(R.id.rgrub)
    RadioGroup rgrub;
    @BindView(R.id.simpan)
    Button simpan;
    @BindView(R.id.linearlayout)
    LinearLayout linearlayout;
    SessionManager sessionManager;
    OwnProgressDialog pDialog;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txDesimal)
    TextView txDesimal;
    @BindView(R.id.btUpdate)
    Button btUpdate;
    String id_penyetor, idkeg, status_byr, status_byr_2, status_simpan, nominal, nama_penyetor;
    ConnectivityManager conMgr;
    StringRequest stringRequest;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pembayar);
        ButterKnife.bind(this);
        cekInternet();

        sessionManager = new SessionManager(TambahPembayarActivity.this);
        pDialog = new OwnProgressDialog(TambahPembayarActivity.this);
        requestQueue = Volley.newRequestQueue(TambahPembayarActivity.this);

        id_penyetor = getIntent().getStringExtra("id_pembayaran");
        idkeg = getIntent().getStringExtra("id_kegiatan");
        status_simpan = getIntent().getStringExtra("status_simpan");

        DetailDataTaawun();

        if (status_simpan.equals("1")) {
            btUpdate.setVisibility(View.VISIBLE);
            uang.setEnabled(false);
            simpan.setVisibility(View.GONE);
        } else {
            btUpdate.setVisibility(View.GONE);
            simpan.setVisibility(View.VISIBLE);
        }

        uang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txDesimal.setText("Rp" + DecimalsFormat.priceWithoutDecimal(uang.getText().toString()));

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
            case R.id.lunas:
                status_byr = "1";
                break;
            case R.id.tidaklunas:
                status_byr = "0";
                break;
        }
    }

    private void SimpanData() {
        onRadioButtonClicked();
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_ADD_DAKWAH, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(TambahPembayarActivity.this, "Taawun berhasil ditambahkan");

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
                    params.put("id_kegiatan", idkeg);
                    params.put("nama_penyetor", nama.getText().toString());
                    params.put("jumlah_uang", uang.getText().toString());
                    params.put("status", status_byr);
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

    private void DetailDataTaawun() {
        pDialog.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_DETAIL_TAAWUN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("detail", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        nama.setText(json.getString("nama_penyetor"));
                        uang.setText(json.getString("jumlah_uang"));
                        if (json.getString("status").equals("1")) {
                            lunas.setChecked(true);
                            tidaklunas.setChecked(false);
                        } else {
                            tidaklunas.setChecked(true);
                            lunas.setChecked(false);
                        }
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
                    params.put("id_pembayaran", id_penyetor);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_exit) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void UpdateDataTaawun() {
        onRadioButtonClicked();
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_EDIT_TAAWUN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(TambahPembayarActivity.this, "Kegiatan berhasil dirubah");
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
                    params.put("id_kegiatan", sessionManager.getIdKegiatan());
                    params.put("id_pembayaran", id_penyetor);
                    params.put("nama_penyetor", nama.getText().toString());
                    params.put("jumlah_uang", uang.getText().toString());
                    params.put("status", status_byr);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("params", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);

    }

    @OnClick({R.id.imgBack, R.id.simpan, R.id.btUpdate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.simpan:
                if (nama.getText().toString().trim().length() > 0 && uang.getText().toString().trim().length() > 0) {
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
                if (nama.getText().toString().trim().length() > 0 && uang.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        UpdateDataTaawun();
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


