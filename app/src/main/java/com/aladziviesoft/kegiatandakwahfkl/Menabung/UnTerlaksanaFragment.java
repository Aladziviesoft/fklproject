package com.aladziviesoft.kegiatandakwahfkl.Menabung;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aladziviesoft.kegiatandakwahfkl.Menabung.Adapter.Adapter_Menabung;
import com.aladziviesoft.kegiatandakwahfkl.Menabung.Model.Menabung_model;
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
import butterknife.Unbinder;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_MENABUNG;

public class UnTerlaksanaFragment extends Fragment {

    @BindView(R.id.rec_menabung)
    RecyclerView recMenabung;
    @BindView(R.id.Swipe)
    SwipeRefreshLayout Swipe;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    Unbinder unbinder;
    ArrayList<Menabung_model> arrayList = new ArrayList<>();
    Adapter_Menabung adapter;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;
    FragmentActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_un_terlaksana_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        recMenabung.setHasFixedSize(true);

        mActivity = getActivity();

        requestQueue = Volley.newRequestQueue(mActivity);
        loading = new OwnProgressDialog(mActivity);
        sessionManager = new SessionManager(mActivity);


        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 1,
                GridLayoutManager.VERTICAL, false);
        recMenabung.setLayoutManager(layoutManager);
        loading.show();
        Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                getdata();
            }
        });

        getdata();
        return view;
    }

    private void getdata() {
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_MENABUNG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ds", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        Menabung_model modelMenu = new Menabung_model();
                        modelMenu.setId_masjid(json.getString("id_menabung"));
                        modelMenu.setNamamasjid(json.getString("nama_masjid"));
                        modelMenu.setTanggal(json.getString("tgl"));
                        modelMenu.setStatus(json.getString("status"));
                        arrayList.add(modelMenu);
                    }
                    adapter = new Adapter_Menabung(arrayList, mActivity);
                    recMenabung.setAdapter(adapter);
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
                    params.put("status", "0");
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.floatingActionButton)
    public void onViewClicked() {
        Intent intent = new Intent(mActivity, TambahMenabung.class);
        intent.putExtra("status_simpan","0");
        startActivity(intent);
    }
}
