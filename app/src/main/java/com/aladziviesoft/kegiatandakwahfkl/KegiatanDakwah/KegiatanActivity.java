package com.aladziviesoft.kegiatandakwahfkl.KegiatanDakwah;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.aladziviesoft.kegiatandakwahfkl.KegiatanDakwah.Adapter.ListKegiatanAdapter;
import com.aladziviesoft.kegiatandakwahfkl.KegiatanDakwah.Model.ListKegiatanModel;
import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.utils.AndLog;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_KEGIATAN;

public class KegiatanActivity extends AppCompatActivity {

    @BindView(R.id.rec_kegiatan)
    RecyclerView recKegiatan;
    @BindView(R.id.Swipe)
    SwipeRefreshLayout Swipe;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;
    ArrayList<ListKegiatanModel> arrayList = new ArrayList<>();
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    String level;
    private SessionManager sessionManager;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kegiatan);
        ButterKnife.bind(this);

        requestQueue = Volley.newRequestQueue(KegiatanActivity.this);
        loading = new OwnProgressDialog(KegiatanActivity.this);
        sessionManager = new SessionManager(KegiatanActivity.this);
        GridLayoutManager layoutManager = new GridLayoutManager(KegiatanActivity.this, 1,
                GridLayoutManager.VERTICAL, false);
        recKegiatan.setLayoutManager(layoutManager);

        loading.show();
        Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                ListDataKegiatan();
            }
        });

        ListDataKegiatan();

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

    private void ListDataKegiatan() {

        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_KEGIATAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ds", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        ListKegiatanModel modelMenu = new ListKegiatanModel();
                        sessionManager.setIdKegiatan(AESCrypt.encrypt("id_keg", json.getString("id_kegiatan")));
                        modelMenu.setIdKegiatan(json.getString("id_kegiatan"));
                        modelMenu.setNamaKegiatan(json.getString("nama_kegiatan"));

                        if (json.getString("jml_target").equals("null")) {
                            modelMenu.setJumlahUangKegiatan("0");
                        } else {
                            modelMenu.setJumlahUangKegiatan(json.getString("jml_target"));
                        }

                        arrayList.add(modelMenu);
                    }
                    ListKegiatanAdapter adapter = new ListKegiatanAdapter(arrayList, KegiatanActivity.this);
                    recKegiatan.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
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
                String token, id_majlis, apikey, refresh_code;
                token = sessionManager.getToken();
                apikey = sessionManager.getApikey();
                refresh_code = sessionManager.getRefreshcode();


                Map<String, String> params = new HashMap<String, String>();
                try {

                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                    params.put("jenis_kegiatan", "dakwah");

                    Log.d("params", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        }

        ;

        requestQueue.add(stringRequest);
    }

    @Override
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


    @OnClick({R.id.imgBack, R.id.floatingActionButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.floatingActionButton:
                Intent intent = new Intent(KegiatanActivity.this, com.aladziviesoft.kegiatandakwahfkl.KegiatanDakwah.TambahKegiatan.class);
                intent.putExtra("status", "0");
                startActivity(intent);
                break;
        }
    }
}
