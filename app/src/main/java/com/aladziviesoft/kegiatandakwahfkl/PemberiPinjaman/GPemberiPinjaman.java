package com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman.Adapter.PmPinjamanAdapter;
import com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman.Model.PempinjModel;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_PEMBERI_PINJAMAN;

public class GPemberiPinjaman extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.rcPemberiPinjaman)
    RecyclerView rcPemberiPinjaman;
    @BindView(R.id.Swipe)
    SwipeRefreshLayout Swipe;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;
    private PmPinjamanAdapter adapter;
    private ArrayList<PempinjModel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpemberi_pinjaman);
        ButterKnife.bind(this);
        rcPemberiPinjaman.setHasFixedSize(true);

        sessionManager = new SessionManager(GPemberiPinjaman.this);
        requestQueue = Volley.newRequestQueue(GPemberiPinjaman.this);
        loading = new OwnProgressDialog(GPemberiPinjaman.this);

        GridLayoutManager manager = new GridLayoutManager(GPemberiPinjaman.this, 1, LinearLayoutManager.VERTICAL, false);
        rcPemberiPinjaman.setLayoutManager(manager);


        Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                datalist();
                Swipe.setRefreshing(false);
            }
        });

        datalist();
    }

    private void datalist() {

        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_PEMBERI_PINJAMAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ds", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        PempinjModel modelMenu = new PempinjModel();
                        modelMenu.setIdPp(json.getString("id_pp"));
                        modelMenu.setNamaPeminjam(json.getString("nama_peminjam"));
                        modelMenu.setNominal(json.getString("nominal"));
                        modelMenu.setNominalDua(json.getString("nominal"));
                        modelMenu.setJatuhTempo(json.getString("tempo"));
                        modelMenu.setNoKtp(json.getString("ktp"));
                        modelMenu.setNoHP(json.getString("nohp"));
                        modelMenu.setStatus(json.getString("status"));
                        modelMenu.setSisa(json.getString("sisa_hari"));

                        arrayList.add(modelMenu);
                    }
                    adapter = new PmPinjamanAdapter(arrayList, GPemberiPinjaman.this);
                    rcPemberiPinjaman.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
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
                Intent intent = new Intent(GPemberiPinjaman.this, GPemberiPinjamanBaru.class);
                intent.putExtra("status_simpan", "0");
                startActivity(intent);
                break;
        }
    }
}
