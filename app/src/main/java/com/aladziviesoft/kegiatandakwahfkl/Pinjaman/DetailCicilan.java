package com.aladziviesoft.kegiatandakwahfkl.Pinjaman;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.Pinjaman.Adapter.DetailCicilanAdapter;
import com.aladziviesoft.kegiatandakwahfkl.Pinjaman.Model.DetailCicilanModel;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_ADD_CICILAN;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_CICILANBYID;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_PINJAMANBYID;

public class DetailCicilan extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.parent_linear_layout)
    LinearLayout parentLinearLayout;
    @BindView(R.id.recyview)
    RecyclerView recyview;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    ArrayList<DetailCicilanModel> arrayList = new ArrayList<>();
    DetailCicilanAdapter adapter;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;
    String id_pinjam;
    String result = "result";
    String messages;
    @BindView(R.id.txsisahutang)
    TextView txsisahutang;
    Intent intent;
    @BindView(R.id.txTanggal)
    TextView txTanggal;
    String nm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cicilan);
        ButterKnife.bind(this);
        recyview.setHasFixedSize(true);
        requestQueue = Volley.newRequestQueue(this);
        loading = new OwnProgressDialog(this);
        sessionManager = new SessionManager(this);

        intent = getIntent();
        id_pinjam = intent.getStringExtra("idpinjamn");


        GridLayoutManager layoutManager = new GridLayoutManager(this, 1,
                LinearLayoutManager.VERTICAL, false);
        recyview.setLayoutManager(layoutManager);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                getdata();
                getdatasisa();
            }
        });


        getdata();
        getdatasisa();


    }

    private void getdatasisa() {
        loading.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_PINJAMANBYID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("dss", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        String nma = json.getString("nama");
                        nm = json.getString("sisa_pinjaman");
                        String jth = json.getString("jatuh_tempo");

                        txsisahutang.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(nm));
                        txTanggal.setText(jth);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loading.dismiss();

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
                    params.put("id_pinjaman", id_pinjam);

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

    private void getdata() {
        loading.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_CICILANBYID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("datacicil", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        DetailCicilanModel modelMenu = new DetailCicilanModel();
                        modelMenu.setId(json.getString("id_pinjaman"));
                        modelMenu.setNominalpinjam(json.getString("nominal"));
                        modelMenu.setTanggalbayar(json.getString("created_at"));
                        modelMenu.setIdcicilan(json.getString("id_cicilan"));

                        arrayList.add(modelMenu);
                    }
                    DetailCicilanAdapter adapter = new DetailCicilanAdapter(arrayList, DetailCicilan.this);
                    recyview.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loading.dismiss();
                swipe.setRefreshing(false);

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
                    params.put("id_pinjaman", id_pinjam);

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
                if(nm.equals("0")){
                    Toast.makeText(getApplicationContext(), "Pinjaman Sudah Lunas", Toast.LENGTH_SHORT).show();
                }else {
                bayar(id_pinjam);
                }
                break;
        }
    }

    private void bayar(final String idpinjam) {
        final EditText text = new EditText(DetailCicilan.this);
        text.setInputType(InputType.TYPE_CLASS_NUMBER);
        text.setGravity(Gravity.RIGHT);
        new AlertDialog.Builder(DetailCicilan.this)
                .setTitle("Bayar Cicilan")
                .setView(text)
                .setPositiveButton("Bayar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nominalbayar = text.getText().toString();
                        int a = Integer.parseInt(nominalbayar);
                        int tt = Integer.parseInt(nm);
                        if(a > tt){
                            Toast.makeText(getApplicationContext(), "Masukkan jumlah uang dengan benar", Toast.LENGTH_SHORT).show();
                        }else {
                            bayarcicilan(idpinjam, nominalbayar);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void bayarcicilan(final String idpinjaman, final String nominal) {

        loading.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_ADD_CICILAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("notes: ", response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    result = jObj.getString("result");
                    if (result.equals("true")) {
                        messages = jObj.getString("message");
                        GlobalToast.ShowToast(DetailCicilan.this, messages);
                        loading.dismiss();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), messages, Toast.LENGTH_LONG).show();

                loading.dismiss();

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
                    params.put("id_pinjaman", idpinjaman);
                    params.put("nominal", nominal);


                    AndLog.ShowLog("paramscicilan", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };
        requestQueue.add(strReq);

    }
}
