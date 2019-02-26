package com.aladziviesoft.kegiatandakwahfkl.MasukanDanaRiba;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.aladziviesoft.kegiatandakwahfkl.MasukanDanaRiba.Adapter.DanaRiba_Adapter;
import com.aladziviesoft.kegiatandakwahfkl.MasukanDanaRiba.Model.DanaRiba_Model;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_DANAR;

public class GMasukanDanaRiba extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.rec_riba)
    RecyclerView recRiba;
    @BindView(R.id.Swipe)
    SwipeRefreshLayout Swipe;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    ArrayList<DanaRiba_Model> arrayList = new ArrayList<>();
    DanaRiba_Model model;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmasukan_dana_riba);
        ButterKnife.bind(this);
        recRiba.setHasFixedSize(true);

        requestQueue = Volley.newRequestQueue(GMasukanDanaRiba.this);
        loading = new OwnProgressDialog(GMasukanDanaRiba.this);
        sessionManager = new SessionManager(GMasukanDanaRiba.this);


        GridLayoutManager layoutManager = new GridLayoutManager(GMasukanDanaRiba.this, 1,
                GridLayoutManager.VERTICAL, false);
        recRiba.setLayoutManager(layoutManager);

        Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                Swipe.setRefreshing(false);
                datalist();

            }
        });

        datalist();

    }

    private void datalist() {
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_DANAR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ds", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        DanaRiba_Model modelMenu = new DanaRiba_Model();
                        modelMenu.setNama("Nama :" + "\n" + json.getString("nama"));
                        modelMenu.setNominal("Nominal :" + "\n" + "Rp. " + DecimalsFormat.priceWithoutDecimal(json.getString("nominal")));
                        modelMenu.setTanggal(json.getString("created_at"));
                        modelMenu.setId(json.getString("id_danar"));
                        modelMenu.setNominaldua(json.getString("nominal"));
                        arrayList.add(modelMenu);
                    }
                    DanaRiba_Adapter adapter = new DanaRiba_Adapter(arrayList, GMasukanDanaRiba.this);
                    recRiba.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loading.dismiss();
                Swipe.setRefreshing(false);

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
                Intent intent = new Intent(getApplicationContext(), TambahDanaRiba.class);
                intent.putExtra("status_simpan","0");
                startActivity(intent);
                break;
        }
    }
}
