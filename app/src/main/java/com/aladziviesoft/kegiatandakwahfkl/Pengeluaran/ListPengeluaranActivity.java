package com.aladziviesoft.kegiatandakwahfkl.Pengeluaran;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aladziviesoft.kegiatandakwahfkl.Pengeluaran.Adapter.ListPengeluaranAdapter;
import com.aladziviesoft.kegiatandakwahfkl.Pengeluaran.Model.ListPengeluaranModel;
import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.utils.AndLog;
import com.aladziviesoft.kegiatandakwahfkl.utils.DecimalsFormat;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DANA_TERKUMPUL;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_OUT;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_TOTAL_PENGELUARAN;

public class ListPengeluaranActivity extends AppCompatActivity {


    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.tempatnamakegiatan)
    TextView tempatnamakegiatan;
    @BindView(R.id.tempatuangkegiatan)
    TextView tempatuangkegiatan;
    @BindView(R.id.txDanaTerkumpul)
    TextView txDanaTerkumpul;
    @BindView(R.id.txJumlahPengeluaran)
    TextView txJumlahPengeluaran;
    @BindView(R.id.linearJmlPengeluaran)
    LinearLayout linearJmlPengeluaran;
    @BindView(R.id.txKelebihanDana)
    TextView txKelebihanDana;
    @BindView(R.id.linearKelebihan)
    LinearLayout linearKelebihan;
    @BindView(R.id.layoutdetail)
    LinearLayout layoutdetail;
    @BindView(R.id.rec_list_pengeluaran)
    RecyclerView recListPengeluaran;
    @BindView(R.id.Swipe)
    SwipeRefreshLayout Swipe;
    @BindView(R.id.linearlayout)
    RelativeLayout linearlayout;
    ListPengeluaranAdapter adapter;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    String id_kegiatan, nama_kegiatan, biaya;
    OwnProgressDialog pDialog;
    ConnectivityManager conMgr;
    StringRequest stringRequest;
    SessionManager sessionManager;
    String result = "result";
    int pendapatan, pengluaran, kasbendahara, sisasaldo;
    String level;
    private RequestQueue requestQueue;
    private ArrayList<ListPengeluaranModel> arrayList = new ArrayList<>();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pengeluaran);
        ButterKnife.bind(this);

        recListPengeluaran.setHasFixedSize(true);

        sessionManager = new SessionManager(ListPengeluaranActivity.this);
        pDialog = new OwnProgressDialog(ListPengeluaranActivity.this);
        requestQueue = Volley.newRequestQueue(ListPengeluaranActivity.this);

        id_kegiatan = getIntent().getStringExtra("id_kegiatan");
        nama_kegiatan = getIntent().getStringExtra("nama_kegiatan");
        biaya = getIntent().getStringExtra("jumlah_uang_kegiatan");

        tempatnamakegiatan.setText(nama_kegiatan);
        tempatuangkegiatan.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(biaya));

        GridLayoutManager layoutManager = new GridLayoutManager(ListPengeluaranActivity.this, 1,
                LinearLayoutManager.VERTICAL, false);
        recListPengeluaran.setLayoutManager(layoutManager);

        try {
            level = AESCrypt.decrypt("lev", sessionManager.getLevel());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        if (level.equals("Umum")) {
            floatingActionButton.setVisibility(View.GONE);
        } else if (level.equals("Admin")) {
            floatingActionButton.setVisibility(View.VISIBLE);
        }

        Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                ListDataPengeluaran();


            }
        });
        ListDataPengeluaran();

    }

    private void DanaTerkumpul() {
        stringRequest = new StringRequest(Request.Method.POST, URL_DANA_TERKUMPUL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("detail", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        if (json.getString("total").equals("null")) {
                            txDanaTerkumpul.setText("Rp. 0");
                        } else {
                            txDanaTerkumpul.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(json.getString("total")));
                        }

                    }

                    TotalPengeluaran();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
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

    private void TotalPengeluaran() {
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_TOTAL_PENGELUARAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("detail", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        if (json.getString("total").equals("null")) {
                            txJumlahPengeluaran.setText("Rp. 0");
                        } else {
                            txJumlahPengeluaran.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(json.getString("total")));
                        }

                    }

                    pDialog.dismiss();
                    if (Swipe != null) {
                        Swipe.setRefreshing(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
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

    private void ListDataPengeluaran() {
        pDialog.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_OUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("pengeluaran", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        ListPengeluaranModel modelMenu = new ListPengeluaranModel();
                        modelMenu.setId_pengeluaran(json.getString("id_pengeluaran"));
                        modelMenu.setKeperluan("Keperluan : " + json.getString("keperluan"));
                        modelMenu.setNominalset(json.getString("nominal"));
                        modelMenu.setNominal("Rp" + DecimalsFormat.priceWithoutDecimal(json.getString("nominal")));
                        modelMenu.setCreated_at(json.getString("created_at"));

                        arrayList.add(modelMenu);
                    }
                    ListPengeluaranAdapter adapter = new ListPengeluaranAdapter(ListPengeluaranActivity.this, arrayList);
                    recListPengeluaran.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                DanaTerkumpul();
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

                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                    params.put("id_kegiatan", id_kegiatan);

                    AndLog.ShowLog("paramspengeluaran", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };

        requestQueue.add(stringRequest);
    }


    @OnClick({R.id.imgBack, R.id.floatingActionButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.floatingActionButton:
                Intent intent = new Intent(ListPengeluaranActivity.this, TambahPengeluaranActivity.class);
                intent.putExtra("id_kegiatan", id_kegiatan);
                startActivity(intent);
                break;
        }
    }
}
