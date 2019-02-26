package com.aladziviesoft.kegiatandakwahfkl.PenerimaBantuan;

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

import com.aladziviesoft.kegiatandakwahfkl.PenerimaBantuan.Adapter.PenerimaBantuanAdapter;
import com.aladziviesoft.kegiatandakwahfkl.PenerimaBantuan.Model.PenerimaBantuanModel;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_PENERIMAB;

public class GPenerimaBantuan extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.recPenerimaBantuan)
    RecyclerView recPenerimaBantuan;
    @BindView(R.id.Swipe)
    SwipeRefreshLayout Swipe;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    private ArrayList<PenerimaBantuanModel> arrayList = new ArrayList<>();
    private PenerimaBantuanAdapter adapter;
    SessionManager sessionManager;
    OwnProgressDialog pDialog;
    ConnectivityManager conMgr;
    StringRequest stringRequest;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpenerima_bantuan);
        ButterKnife.bind(this);
        recPenerimaBantuan.setHasFixedSize(true);

        sessionManager = new SessionManager(GPenerimaBantuan.this);
        pDialog = new OwnProgressDialog(GPenerimaBantuan.this);
        requestQueue = Volley.newRequestQueue(GPenerimaBantuan.this);

        GridLayoutManager layoutManager = new GridLayoutManager(GPenerimaBantuan.this, 1, LinearLayoutManager.VERTICAL, false);
        recPenerimaBantuan.setLayoutManager(layoutManager);






        Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                datalist();
                arrayList.clear();
            }
        });
        datalist();

    }

    private void datalist() {
        pDialog.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_PENERIMAB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ds", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        PenerimaBantuanModel modelMenu = new PenerimaBantuanModel();
                        modelMenu.setId(json.getString("id_penerimabantuan"));
                        modelMenu.setNama(json.getString("nama"));
                        modelMenu.setIdSumberDana(json.getString("id_saldo"));
                        modelMenu.setNominal(json.getString("nominal"));
                        modelMenu.setTanggal(json.getString("created_at"));

                        arrayList.add(modelMenu);
                    }
                    PenerimaBantuanAdapter adapter = new PenerimaBantuanAdapter(GPenerimaBantuan.this,arrayList);
                    recPenerimaBantuan.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
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
                Intent intent = new Intent(getApplicationContext(), TambahPenerimaBantuan.class);
                intent.putExtra("status_simpan", "0");
                startActivity(intent);
                break;
        }
    }
}
