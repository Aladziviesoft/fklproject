package com.aladziviesoft.kegiatandakwahfkl.Rab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.Rab.Adapter.RabAdapter;
import com.aladziviesoft.kegiatandakwahfkl.Rab.Model.RabModel;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_EDIT_DANA_KEGIATAN;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_JUMLAH_DANA_TERKUMPUL_DARI_RAD_TO_KEGIATAN;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_RAD;

public class RencanaAnggaranAcitivty extends AppCompatActivity {

    RabAdapter adapter;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.etNamaKEgiatan)
    TextView etNamaKEgiatan;
    @BindView(R.id.txJumlahUang)
    TextView txJumlahUang;
    @BindView(R.id.rec_list_rad)
    RecyclerView recListRad;
    @BindView(R.id.Swipe)
    SwipeRefreshLayout Swipe;
    @BindView(R.id.linearlayout)
    RelativeLayout linearlayout;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;
    String id_kegiatan, nama_kegiatan, dana_kegiatan, level;
    ConnectivityManager conMgr;
    @BindView(R.id.btAnggarkanDana)
    Button btAnggarkanDana;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    private ArrayList<RabModel> arrayList = new ArrayList<>();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rencana_anggaran);
        ButterKnife.bind(this);

        cekInternet();

        id_kegiatan = getIntent().getStringExtra("id_kegiatan");
        nama_kegiatan = getIntent().getStringExtra("nama_kegiatan");
        etNamaKEgiatan.setText(nama_kegiatan);

        recListRad.setHasFixedSize(true);
        requestQueue = Volley.newRequestQueue(RencanaAnggaranAcitivty.this);
        loading = new OwnProgressDialog(RencanaAnggaranAcitivty.this);
        sessionManager = new SessionManager(RencanaAnggaranAcitivty.this);

        GridLayoutManager layoutManager = new GridLayoutManager(RencanaAnggaranAcitivty.this, 1, LinearLayoutManager.VERTICAL, false);
        recListRad.setLayoutManager(layoutManager);

        loading.show();
        Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                ListDataRad();

            }

        });

        try {
            level = AESCrypt.decrypt("lev", sessionManager.getLevel());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        if (level.equals("Umum")) {
            floatingActionButton.setVisibility(View.GONE);
            floatingActionButton.setEnabled(false);
            btAnggarkanDana.setVisibility(View.GONE);
        } else if (level.equals("Admin")) {
            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setEnabled(true);
            btAnggarkanDana.setVisibility(View.VISIBLE);
        }
        ListDataRad();

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

    private void DanaTerkumpul() {
        stringRequest = new StringRequest(Request.Method.POST, URL_JUMLAH_DANA_TERKUMPUL_DARI_RAD_TO_KEGIATAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("detail", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        if (json.getString("total").equals("null")) {
                            txJumlahUang.setText("Rp. 0");
                            dana_kegiatan = json.getString("total");
                        } else {
                            txJumlahUang.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(json.getString("total")));
                            dana_kegiatan = json.getString("total");
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                loading.dismiss();
                if (Swipe != null) {
                    Swipe.setRefreshing(false);
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
                    params.put("id_kegiatan", id_kegiatan);
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

    private void ListDataRad() {
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_RAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("rad", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        RabModel modelMenu = new RabModel();
                        modelMenu.setId_rad(json.getString("id_rad"));
                        modelMenu.setNamaRab(json.getString("nama_rad"));
                        modelMenu.setJumlahUang("Rp. " + DecimalsFormat.priceWithoutDecimal(json.getString("biaya")));
                        arrayList.add(modelMenu);
                    }
                    adapter = new RabAdapter(RencanaAnggaranAcitivty.this, arrayList);
                    recListRad.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                DanaTerkumpul();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                if (Swipe != null) {
                    Swipe.setRefreshing(false);
                }
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
                    params.put("id_kegiatan", id_kegiatan);
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));

                    Log.d("paramslist", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        }

        ;

        requestQueue.add(stringRequest);
    }

    private void UpdateDanaKegiatan() {
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_EDIT_DANA_KEGIATAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("responeupdatedanakegiatan", response);
                GlobalToast.ShowToast(RencanaAnggaranAcitivty.this, "Dana Telah Dianggarkan : Rp." + dana_kegiatan);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Ada Kesalahan", Toast.LENGTH_LONG).show();
                AndLog.ShowLog("errss", String.valueOf(error));

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
                    params.put("id_users", AESCrypt.decrypt("id_user", sessionManager.getIduser()));
                    params.put("id_kegiatan", id_kegiatan);
                    params.put("jml_target", dana_kegiatan);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("params", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);

    }

    @OnClick({R.id.imgBack, R.id.btAnggarkanDana, R.id.floatingActionButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.btAnggarkanDana:
                UpdateDanaKegiatan();
                break;
            case R.id.floatingActionButton:
                Intent intent = new Intent(RencanaAnggaranAcitivty.this, UpdateRadActivity.class);
                intent.putExtra("status", "0");
                intent.putExtra("id_kegiatan", id_kegiatan);
                startActivity(intent);
                break;
        }
    }
}
