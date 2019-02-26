package com.aladziviesoft.kegiatandakwahfkl.ExportTaawun;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.ExportTaawun.Adapter.ExportAdapter;
import com.aladziviesoft.kegiatandakwahfkl.KegiatanDakwah.Model.ListKegiatanModel;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_CARI_KEGIATAN;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DANA_TERKUMPUL;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_KEGIATAN;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_TAAWUN;

public class ExportDataTaawunActivity extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.rec_export)
    RecyclerView recExport;
    @BindView(R.id.Swipe)
    SwipeRefreshLayout Swipe;
    @BindView(R.id.txOutput)
    TextView txOutput;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;
    ArrayList<ListKegiatanModel> arrayList = new ArrayList<>();
    @BindView(R.id.btPreview)
    Button btPreview;
    String id_kegiatan, getNama, getJumlahUang;
    @BindView(R.id.etCariNamakegiatan)
    EditText etCariNamakegiatan;
    @BindView(R.id.btCari)
    Button btCari;
    ConnectivityManager conMgr;
    @BindView(R.id.linearlayout)
    LinearLayout linearlayout;
    @BindView(R.id.txDanaTerkumpul)
    TextView txDanaTerkumpul;
    @BindView(R.id.txCopy)
    EditText txCopy;
    int dibutuhkan, terkumpul, sisa, kelebihan;

    private SessionManager sessionManager;
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            id_kegiatan = intent.getStringExtra("id_kegiatan");
            getNama = intent.getStringExtra("nama_kegiatan");
            getJumlahUang = intent.getStringExtra("jml_target");
            ListDataTaawun();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data_taawun);
        ButterKnife.bind(this);

        cekInternet();

        requestQueue = Volley.newRequestQueue(ExportDataTaawunActivity.this);
        loading = new OwnProgressDialog(ExportDataTaawunActivity.this);
        sessionManager = new SessionManager(ExportDataTaawunActivity.this);
        GridLayoutManager layoutManager = new GridLayoutManager(ExportDataTaawunActivity.this, 1,
                GridLayoutManager.VERTICAL, false);
        recExport.setLayoutManager(layoutManager);

//        txIdKegiatan.setText("ID Belum Siap");
//        txIdKegiatan.setTextColor(Color.parseColor("#6d122d"));


        loading.show();
        Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                ListDataKegiatan();
            }
        });

        ListDataKegiatan();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
    }

    public void cekInternet() {

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
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
                        modelMenu.setIdKegiatan(json.getString("id_kegiatan"));
                        modelMenu.setNamaKegiatan(json.getString("nama_kegiatan"));
                        modelMenu.setJumlahUangKegiatan(json.getString("jml_target"));
//                        id_kegiatan = modelMenu.getIdKegiatan();

                        arrayList.add(modelMenu);
                    }
                    ExportAdapter adapter = new ExportAdapter(ExportDataTaawunActivity.this, arrayList);
                    recExport.setAdapter(adapter);

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

    private void FindKegiatan() {
        arrayList.clear();
        loading.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_CARI_KEGIATAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ds", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        ListKegiatanModel modelMenu = new ListKegiatanModel();
                        modelMenu.setIdKegiatan(json.getString("id_kegiatan"));
                        modelMenu.setNamaKegiatan(json.getString("nama_kegiatan"));
                        id_kegiatan = modelMenu.getIdKegiatan();

                        arrayList.add(modelMenu);
                    }
                    ExportAdapter adapter = new ExportAdapter(ExportDataTaawunActivity.this, arrayList);
                    recExport.setAdapter(adapter);

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
                    params.put("nama_kegiatan", etCariNamakegiatan.getText().toString());
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

    private void ListDataTaawun() {
        loading.show();
        txOutput.setText(null);
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_TAAWUN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("dscccc", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    String as = "";
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);

                        int i = a + 1;
                        if (json.getString("status").equals("1")) {

                            as += i + ". " + "Disetor via : " + json.getString("nama_penyetor") + "\n"
                                    + "Nominal : " + "Rp" + DecimalsFormat.priceWithoutDecimal(json.getString("jumlah_uang")) + "" + "\n";
                        } else {
                            as += i + ". " + "Disetor via : " + json.getString("nama_penyetor") + "\n"
                                    + "Nominal : " + "Rp" + DecimalsFormat.priceWithoutDecimal(json.getString("jumlah_uang")) + "(*)" + "\n";
                        }

                        txOutput.setText(as + "\n" + "\n" + "Jazakumullahu khairan kepada para dotatur, semoga Allah Azza Wa jalla memberi ganti yang lebih baik." + "\n" + "\n" + "NB : Tanda  (*) adalah tanda donasi yang belum disetor ke bendahara");
                    }


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

                    AndLog.ShowLog("params", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };

        requestQueue.add(stringRequest);
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
                        dibutuhkan = Integer.parseInt(getJumlahUang);
                        if (json.getString("total").equals("null")) {
                            terkumpul = 0;
                            txDanaTerkumpul.setText("Kegiatan : " + getNama + "\n"
                                    + "Jumlah dana yang dibutuhkan : " + "Rp." + DecimalsFormat.priceWithoutDecimal(getJumlahUang) + "\n"
                                    + "Dana Terkumpul " + "Rp. 0");
//                            txSisaKekurangan.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(uangkegd));
                        } else {
                            terkumpul = Integer.parseInt(json.getString("total"));
                            sisa = dibutuhkan - terkumpul;
                            String Sisas = String.valueOf(sisa);
//                            AndLog.ShowLog("totall", String.valueOf(sisa));
//                            txSisaKekurangan.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(String.valueOf(sisa)));

                            if (Integer.parseInt(json.getString("total")) > dibutuhkan) {

                                kelebihan = terkumpul - dibutuhkan;
                                String kelebihanS = String.valueOf(kelebihan);
                                txDanaTerkumpul.setText("Kegiatan : " + getNama + "\n"
                                        + "Jumlah dana yang dibutuhkan : " + "Rp. " + DecimalsFormat.priceWithoutDecimal(getJumlahUang) + "\n"
                                        + "Dana Terkumpul : " + "Rp. " + DecimalsFormat.priceWithoutDecimal(json.getString("total")) + "\n"
                                        + "Kelebihan Dana : " + "Rp. " + DecimalsFormat.priceWithoutDecimal(kelebihanS));

                            } else {
                                txDanaTerkumpul.setText("Kegiatan : " + getNama + "\n"
                                        + "Jumlah dana yang dibutuhkan : " + "Rp. " + DecimalsFormat.priceWithoutDecimal(getJumlahUang) + "\n"
                                        + "Dana Terkumpul : " + "Rp. " + DecimalsFormat.priceWithoutDecimal(json.getString("total")) + "\n"
                                        + "Kekurangan : " + "Rp. " + DecimalsFormat.priceWithoutDecimal(Sisas));
                            }
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                txCopy.setText(txOutput.getText().toString() + "\n" + "\n" + txDanaTerkumpul.getText().toString());

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

    @OnClick({R.id.imgBack, R.id.btPreview, R.id.btCari})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.btPreview:

                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(txCopy.getText());
//                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                GlobalToast.ShowToast(ExportDataTaawunActivity.this, "Copied to clipboard");
                break;
            case R.id.btCari:
                if (etCariNamakegiatan.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        FindKegiatan();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(linearlayout, "No Internet Connection, Try Again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        loading.dismiss();
                    }
                } else {
//                    Snackbar snackbar = Snackbar
//                            .make(linearlayout, "Kolom tidak boleh kosong, silahkan di isi", Snackbar.LENGTH_LONG);
//                    snackbar.show();
                    arrayList.clear();
                    ListDataKegiatan();
                }
                break;
        }
    }
}
