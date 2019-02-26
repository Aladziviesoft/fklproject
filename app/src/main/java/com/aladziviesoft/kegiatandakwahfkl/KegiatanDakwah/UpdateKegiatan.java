package com.aladziviesoft.kegiatandakwahfkl.KegiatanDakwah;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_EDIT_KEGIATAN;

public class UpdateKegiatan extends AppCompatActivity {


    String id, nama, uang;
    @BindView(R.id.txNamaKegiatan)
    EditText txNamaKegiatan;
    @BindView(R.id.txJumlahUang)
    EditText txJumlahUang;
    @BindView(R.id.btUpdate)
    Button btUpdate;
    SessionManager sessionManager;
    OwnProgressDialog pDialog;
    @BindView(R.id.linearlayout)
    LinearLayout linearlayout;
    ConnectivityManager conMgr;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_kegiatan);
        ButterKnife.bind(this);
        cekInternet();

        sessionManager = new SessionManager(UpdateKegiatan.this);
        pDialog = new OwnProgressDialog(UpdateKegiatan.this);
        requestQueue = Volley.newRequestQueue(UpdateKegiatan.this);

        id = getIntent().getStringExtra("id_kegiatan");
        nama = getIntent().getStringExtra("nama_kegiatan");
        uang = getIntent().getStringExtra("jumlah_uang_kegiatan");
        txNamaKegiatan.setText(nama);
        txJumlahUang.setText(uang);



    }


    private void UpdateData() {
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_EDIT_KEGIATAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(UpdateKegiatan.this, "Data Kegiatan berhasil dirubah");
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
                    params.put("id_kegiatan", id);
                    params.put("nama_kegiatan", txNamaKegiatan.getText().toString());
                    params.put("jml_target", txJumlahUang.getText().toString());
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

    @OnClick(R.id.btUpdate)
    public void onViewClicked() {
        if (txNamaKegiatan.getText().toString().trim().length() > 0 && txJumlahUang.getText().toString().trim().length() > 0) {
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

    }
}


