package com.aladziviesoft.kegiatandakwahfkl.Inventory;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_EDIT_KAS_INVEN;

public class UpdateInventoryActivity extends AppCompatActivity {

    @BindView(R.id.upnamabarang)
    EditText upnamabarang;
    @BindView(R.id.uphargabarang)
    EditText uphargabarang;
    @BindView(R.id.upbanyakbarang)
    EditText upbanyakbarang;
    @BindView(R.id.updateinven)
    Button updateinven;
    String id, nama, harga, qty;
    ConnectivityManager conMgr;
    SessionManager sessionManager;
    OwnProgressDialog pDialog;
    @BindView(R.id.linearlayout)
    LinearLayout linearlayout;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txDesimal)
    TextView txDesimal;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_inventory);
        ButterKnife.bind(this);

        cekInternet();

        sessionManager = new SessionManager(UpdateInventoryActivity.this);
        pDialog = new OwnProgressDialog(UpdateInventoryActivity.this);
        requestQueue = Volley.newRequestQueue(UpdateInventoryActivity.this);


        id = getIntent().getStringExtra("id_inventory");
        nama = getIntent().getStringExtra("nama_barang");
        harga = getIntent().getStringExtra("harga");
        qty = getIntent().getStringExtra("qty");

        upnamabarang.setText(nama);
        upbanyakbarang.setText(qty);
        uphargabarang.setText(harga);

        uphargabarang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txDesimal.setText("Rp" + DecimalsFormat.priceWithoutDecimal(uphargabarang.getText().toString()));

            }

            @Override
            public void afterTextChanged(Editable s) {
//                DecimalsFormat.priceWithoutDecimal(txSaldo.getText().toString());
            }
        });

    }

    private void UpdateData() {
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_EDIT_KAS_INVEN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(UpdateInventoryActivity.this, "Data Inventory berhasil dirubah");
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
                    params.put("id_inventory", id);
                    params.put("nama_barang", upnamabarang.getText().toString());
                    params.put("harga", uphargabarang.getText().toString());
                    params.put("qty", upbanyakbarang.getText().toString());

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("paramsedit", String.valueOf(params));
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

    @OnClick(R.id.updateinven)
    public void onViewClicked() {
        if (upnamabarang.getText().toString().trim().length() > 0 && upbanyakbarang.getText().toString().trim().length() > 0) {
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

    @OnClick(R.id.imgBack)
    public void onViewClicked2() {
        finish();
    }
}
