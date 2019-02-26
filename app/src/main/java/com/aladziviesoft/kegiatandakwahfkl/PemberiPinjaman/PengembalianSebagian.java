package com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman.Adapter.CicilanPmPinjamanAdapter;
import com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman.Model.CicilanpinjModel;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_ADD_CICILPP;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DETAIL_PP;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIS_CICILANPP;

public class PengembalianSebagian extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txsisapinjaman)
    TextView txsisapinjaman;
    @BindView(R.id.txTanggal)
    TextView txTanggal;
    @BindView(R.id.parent_linear_layout)
    LinearLayout parentLinearLayout;
    @BindView(R.id.recyview)
    RecyclerView recyview;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;
    ArrayList<CicilanpinjModel> arrayList = new ArrayList<>();
    CicilanPmPinjamanAdapter adapter;
    String idpp;
    String nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengembalian_sebagian);
        ButterKnife.bind(this);
        recyview.setHasFixedSize(true);
        requestQueue = Volley.newRequestQueue(this);
        loading = new OwnProgressDialog(this);
        sessionManager = new SessionManager(this);

        idpp = getIntent().getStringExtra("idpp");

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
        stringRequest = new StringRequest(Request.Method.POST, URL_DETAIL_PP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("detailpp", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        txsisapinjaman.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(json.getString("sisa_pengembalian")));
                        nm = json.getString("sisa_pengembalian");
                        txTanggal.setText(json.getString("tempo"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                loading.dismiss();
                swipe.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AndLog.ShowLog("error detail taawun", String.valueOf(error));
                loading.dismiss();
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
                    params.put("id_pp", idpp);
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));

                    AndLog.ShowLog("paramspp", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };

        requestQueue.add(stringRequest);
    }

    private void getdata() {
        loading.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_LIS_CICILANPP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("listcicil", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        CicilanpinjModel modelMenu = new CicilanpinjModel();
                        modelMenu.setIdcicil(json.getString("id_cicilanpp"));
                        modelMenu.setIdpinj(json.getString("idpp"));
                        modelMenu.setNominal("Rp. "+DecimalsFormat.priceWithoutDecimal(json.getString("nominal")));
                        modelMenu.setTanggal(json.getString("created_at"));

                        arrayList.add(modelMenu);
                    }
                    adapter = new CicilanPmPinjamanAdapter(arrayList, PengembalianSebagian.this);
                    recyview.setAdapter(adapter);
                    loading.dismiss();
                    swipe.setRefreshing(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
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
                    params.put("idpp", idpp);


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
                bayar();
                break;
        }
    }

    private void bayar() {
        final EditText text = new EditText(PengembalianSebagian.this);
        text.setInputType(InputType.TYPE_CLASS_NUMBER);
        text.setGravity(Gravity.RIGHT);
        new AlertDialog.Builder(PengembalianSebagian.this)
                .setTitle("Bayar Sebagian")
                .setView(text)
                .setPositiveButton("Bayar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nominalbayar = text.getText().toString();
                        int a = Integer.parseInt(nominalbayar);
                        int tt = Integer.parseInt(nm);
                        if (a > tt) {
                            Toast.makeText(getApplicationContext(), "Masukkan jumlah uang dengan benar", Toast.LENGTH_SHORT).show();
                        } else {
                            bayarcicil(nominalbayar);
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

    private void bayarcicil(final String nominal) {
        loading.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_ADD_CICILPP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(PengembalianSebagian.this, "Data berhasil ditambahkan");
                loading.dismiss();
                finish();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Ada Kesalahan", Toast.LENGTH_LONG).show();
                AndLog.ShowLog("errss", String.valueOf(error));
                loading.dismiss();

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
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                    params.put("idpp", idpp);
                    params.put("nominal", nominal);

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("paramstes", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);
    }


}
