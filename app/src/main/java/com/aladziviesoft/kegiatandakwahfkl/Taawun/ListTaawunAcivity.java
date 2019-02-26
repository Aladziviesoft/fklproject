package com.aladziviesoft.kegiatandakwahfkl.Taawun;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.Taawun.Adapter.ListTaawunAdapter;
import com.aladziviesoft.kegiatandakwahfkl.Taawun.Model.ListTaawunModel;
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
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_COUNT;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DANA_TERKUMPUL;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_TAAWUN;

public class ListTaawunAcivity extends AppCompatActivity {

    @BindView(R.id.rec_list_taawun)
    RecyclerView recListTaawun;
    @BindView(R.id.Swipe)
    SwipeRefreshLayout Swipe;
    @BindView(R.id.tempatnamakegiatan)
    TextView tempatnamakegiatan;
    @BindView(R.id.tempatuangkegiatan)
    TextView tempatuangkegiatan;
    @BindView(R.id.switch1)
    Switch switch1;
    @BindView(R.id.txDanaTerkumpul)
    TextView txDanaTerkumpul;
    @BindView(R.id.txSisaKekurangan)
    TextView txSisaKekurangan;
    @BindView(R.id.txSudahSetor)
    TextView txSudahSetor;
    @BindView(R.id.txBelumSetor)
    TextView txBelumSetor;
    @BindView(R.id.layoutdetail)
    LinearLayout layoutdetail;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.lbTotalKekurangan)
    TextView lbTotalKekurangan;
    @BindView(R.id.txKelebihanDana)
    TextView txKelebihanDana;
    @BindView(R.id.linearKelebihan)
    LinearLayout linearKelebihan;
    @BindView(R.id.linearKekurangan)
    LinearLayout linearKekurangan;
    LinearLayoutManager layoutManager;
    ListTaawunAdapter adapter;
    List<ListTaawunModel> arrayList = new ArrayList<>();
    SessionManager sessionManager;
    String namakegd, uangkegd, id_kegiatan;
    int dibutuhkan, terkumpul, sisa, kelebihan;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    String level;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_taawun_acivity);
        ButterKnife.bind(this);

        recListTaawun.setHasFixedSize(true);
        requestQueue = Volley.newRequestQueue(ListTaawunAcivity.this);
        loading = new OwnProgressDialog(ListTaawunAcivity.this);
        sessionManager = new SessionManager(ListTaawunAcivity.this);

        id_kegiatan = getIntent().getStringExtra("id_kegiatan");
        sessionManager.setIdKegiatan(id_kegiatan);
        namakegd = getIntent().getStringExtra("nama_kegiatan");
        uangkegd = getIntent().getStringExtra("jumlah_uang_kegiatan");


        tempatnamakegiatan.setText(String.valueOf(namakegd));
        tempatuangkegiatan.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(uangkegd));

        GridLayoutManager layoutManager = new GridLayoutManager(ListTaawunAcivity.this, 1,
                GridLayoutManager.VERTICAL, false);
        recListTaawun.setLayoutManager(layoutManager);

        loading.show();

        Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                ListDataTaawun();

//                txSisaKekurangan.setText(sisa);

            }
        });


        ListDataTaawun();
//        txSisaKekurangan.setText(sisa);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layoutdetail.setVisibility(View.VISIBLE);
                } else {
                    layoutdetail.setVisibility(View.GONE);
                }
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

        } else if (level.equals("Admin")) {
            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setEnabled(true);

        }


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_exit) {
            finish();
        }
        return true;

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
                        txDanaTerkumpul.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(json.getString("total")));
                        dibutuhkan = Integer.parseInt(uangkegd);
                        if (json.getString("total").equals("null")) {
                            terkumpul = 0;
                            txDanaTerkumpul.setText("Rp. 0");
                            txSisaKekurangan.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(uangkegd));
                        } else {
                            terkumpul = Integer.parseInt(json.getString("total"));
                            sisa = dibutuhkan - terkumpul;
                            AndLog.ShowLog("totall", String.valueOf(sisa));
                            txSisaKekurangan.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(String.valueOf(sisa)));

                            if (Integer.parseInt(json.getString("total")) > dibutuhkan) {
                                linearKelebihan.setVisibility(View.VISIBLE);
                                kelebihan = terkumpul - dibutuhkan;
                                txKelebihanDana.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(String.valueOf(kelebihan)));
                                linearKekurangan.setVisibility(View.GONE);

                            } else {
                                linearKekurangan.setVisibility(View.VISIBLE);
                                linearKelebihan.setVisibility(View.GONE);
                            }
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                TotalBelumSetor();


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

    private void TotalBelumSetor() {
        stringRequest = new StringRequest(Request.Method.POST, URL_COUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("detail", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        txBelumSetor.setText(json.getString("total") + " Orang");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                TotalSudahSetor();

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
                    params.put("status", "0");
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

    private void TotalSudahSetor() {
        stringRequest = new StringRequest(Request.Method.POST, URL_COUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("detail", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        txSudahSetor.setText(json.getString("total") + " Orang");
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
                    params.put("status", "1");
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

    private void ListDataTaawun() {

        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_TAAWUN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ds", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        ListTaawunModel modelMenu = new ListTaawunModel();
                        modelMenu.setId(json.getString("id_pembayaran"));
                        modelMenu.setNama("Via : " + json.getString("nama_penyetor"));
                        modelMenu.setJumlahUang(json.getString("jumlah_uang"));
                        modelMenu.setNominal(json.getString("jumlah_uang"));
                        modelMenu.setTanggal(json.getString("created_at"));
                        modelMenu.setStatus(json.getString("status"));

                        arrayList.add(modelMenu);
                    }
                    ListTaawunAdapter adapter = new ListTaawunAdapter(arrayList, ListTaawunAcivity.this);
                    recListTaawun.setAdapter(adapter);
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
                id_majlis = sessionManager.getIdMajlis();

                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                    params.put("id_kegiatan", id_kegiatan);

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


    @OnClick({R.id.imgBack, R.id.floatingActionButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.floatingActionButton:
                Intent a = new Intent(getApplicationContext(), TambahPembayarActivity.class);
                a.putExtra("id_kegiatan", id_kegiatan);
                a.putExtra("status_simpan", "0");
                startActivity(a);
                break;
        }
    }
}
