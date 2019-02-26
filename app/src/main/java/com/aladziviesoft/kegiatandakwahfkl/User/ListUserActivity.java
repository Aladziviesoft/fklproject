package com.aladziviesoft.kegiatandakwahfkl.User;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.User.Adapter.ListUserAdapter;
import com.aladziviesoft.kegiatandakwahfkl.User.Model.ListUserModel;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_CARI_USER;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_USER;

public class ListUserActivity extends AppCompatActivity {


    String id, nama, status;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;
    ConnectivityManager conMgr;
    OwnProgressDialog pDialog;
    @BindView(R.id.rec_list_user)
    RecyclerView recListUser;
    @BindView(R.id.Swipe)
    SwipeRefreshLayout Swipe;
    ArrayList<ListUserModel> arrayList = new ArrayList<>();
    ListUserAdapter adapter;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.etCar)
    EditText etCar;
    @BindView(R.id.btCari)
    Button btCari;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.linearlayout)
    RelativeLayout linearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        ButterKnife.bind(this);
        cekInternet();

        requestQueue = Volley.newRequestQueue(ListUserActivity.this);
        pDialog = new OwnProgressDialog(ListUserActivity.this);
        sessionManager = new SessionManager(ListUserActivity.this);

        recListUser.setHasFixedSize(true);

        GridLayoutManager manager = new GridLayoutManager(ListUserActivity.this, 1, LinearLayoutManager.VERTICAL, false);
        recListUser.setLayoutManager(manager);

        Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                ListUser();
            }
        });
        ListUser();
    }


    private void ListUser() {
        pDialog.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("listUser", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        ListUserModel modelMenu = new ListUserModel();
                        modelMenu.setId_user(json.getString("id"));
                        modelMenu.setNama_user(json.getString("full_name"));
                        modelMenu.setLevel(json.getString("level"));

                        arrayList.add(modelMenu);
                    }
                    ListUserAdapter adapter = new ListUserAdapter(ListUserActivity.this, arrayList);
                    recListUser.setAdapter(adapter);

                    pDialog.dismiss();
                    if (Swipe != null) {
                        Swipe.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AndLog.ShowLog("error", "" + error);
                pDialog.dismiss();
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
//                    params.put("id_majlis", AESCrypt.decrypt("id_majlis", id_majlis));
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));

                    AndLog.ShowLog("paramslist_user", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };

        requestQueue.add(stringRequest);
    }

    private void CariUser() {
        pDialog.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_CARI_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("HasilPencarianUser", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        ListUserModel modelMenu = new ListUserModel();
                        modelMenu.setId_user(json.getString("id"));
                        modelMenu.setNama_user(json.getString("full_name"));
                        modelMenu.setLevel(json.getString("level"));

                        arrayList.add(modelMenu);
                    }
                    ListUserAdapter adapter = new ListUserAdapter(ListUserActivity.this, arrayList);
                    recListUser.setAdapter(adapter);

                    pDialog.dismiss();
                    if (Swipe != null) {
                        Swipe.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AndLog.ShowLog("error", "" + error);
                pDialog.dismiss();
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
//                    params.put("id_majlis", AESCrypt.decrypt("id_majlis", id_majlis));
                    params.put("full_name", etCar.getText().toString());
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));

                    AndLog.ShowLog("paramslist_user", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };

        requestQueue.add(stringRequest);
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


    @OnClick({R.id.imgBack, R.id.btCari, R.id.floatingActionButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.btCari:
                if (etCar.getText().toString().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        arrayList.clear();
                        CariUser();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(linearlayout, "No Internet Connection, Try Again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        loading.dismiss();
                    }
                } else {
                    arrayList.clear();
                    ListUser();
                }
                break;
            case R.id.floatingActionButton:
                Intent intent = new Intent(ListUserActivity.this, NewUserActivity.class);
                intent.putExtra("status_simpan", "0");
                startActivity(intent);
                break;
        }
    }
}


