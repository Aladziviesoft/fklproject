package com.aladziviesoft.kegiatandakwahfkl.KegiatanSosial;

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

import com.aladziviesoft.kegiatandakwahfkl.KegiatanDakwah.KegiatanActivity;
import com.aladziviesoft.kegiatandakwahfkl.KegiatanSosial.Adapter.ListKegiatanSoialAdapter;
import com.aladziviesoft.kegiatandakwahfkl.KegiatanSosial.Model.ListKegiatanSoialModel;
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

public class SosialActivity extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.rec_kegiatansosial)
    RecyclerView recKegiatansosial;
    @BindView(R.id.Swipe)
    SwipeRefreshLayout Swipe;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    OwnProgressDialog loading;
    ArrayList<ListKegiatanSoialModel> arrayList = new ArrayList<>();
    String level;
    ListKegiatanSoialModel model;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    SessionManager sessionManager;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sosial);
        ButterKnife.bind(this);
        requestQueue = Volley.newRequestQueue(this);
        loading = new OwnProgressDialog(this);
        sessionManager = new SessionManager(this);
        recKegiatansosial.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(SosialActivity.this, 1,
                GridLayoutManager.VERTICAL, false);
        recKegiatansosial.setLayoutManager(layoutManager);


        loading.show();
        Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                getdata();
            }
        });

        getdata();

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

    private void getdata() {


        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_KEGIATAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ds", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        ListKegiatanSoialModel modelMenu = new ListKegiatanSoialModel();
                        sessionManager.setIdKegiatan(AESCrypt.encrypt("id_keg", json.getString("id_kegiatan")));
                        modelMenu.setIdKegiatanSosial(json.getString("id_kegiatan"));
                        modelMenu.setNamaKegiatanSosial(json.getString("nama_kegiatan"));

                        if (json.getString("jml_target").equals("null")) {
                            modelMenu.setJumlahUangKegiatanSosial("0");
                        } else {
                            modelMenu.setJumlahUangKegiatanSosial(json.getString("jml_target"));
                        }

                        arrayList.add(modelMenu);
                    }
                    ListKegiatanSoialAdapter adapter = new ListKegiatanSoialAdapter(arrayList, SosialActivity.this);
                    recKegiatansosial.setAdapter(adapter);
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
                    params.put("jenis_kegiatan", "sosial");

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
                Intent intent = new Intent(SosialActivity.this, TambahKegiatanSosial.class);
                intent.putExtra("status", "0");
                startActivity(intent);
                break;
        }
    }
}
