package com.aladziviesoft.kegiatandakwahfkl.DaftarSaldo;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.aladziviesoft.kegiatandakwahfkl.DaftarSaldo.Adapter.DaftarSaldoAdapter;
import com.aladziviesoft.kegiatandakwahfkl.DaftarSaldo.Model.DaftarSaldoModel;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_SALDO;

public class DaftarSaldo extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.recyview)
    RecyclerView recyview;
    @BindView(R.id.Swipe)
    SwipeRefreshLayout Swipe;
    ArrayList<DaftarSaldoModel> arrayList = new ArrayList<>();
    DaftarSaldoAdapter adapter;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_saldo);
        ButterKnife.bind(this);
        recyview.setHasFixedSize(true);
        requestQueue = Volley.newRequestQueue(DaftarSaldo.this);
        loading = new OwnProgressDialog(DaftarSaldo.this);
        sessionManager = new SessionManager(DaftarSaldo.this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1,
                LinearLayoutManager.VERTICAL, false);
        recyview.setLayoutManager(layoutManager);

        Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                getdata();
            }
        });


        getdata();


    }

    private void getdata() {
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_SALDO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ds", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        DaftarSaldoModel modelMenu = new DaftarSaldoModel();
                        modelMenu.setJenis(json.getString("nama_saldo"));
                        modelMenu.setTotalsaldo(json.getString("nominal"));

                        arrayList.add(modelMenu);
                    }
                    DaftarSaldoAdapter adapter = new DaftarSaldoAdapter(arrayList, DaftarSaldo.this);
                    recyview.setAdapter(adapter);
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

    @OnClick(R.id.imgBack)
    public void onViewClicked() {
        finish();
    }
}
