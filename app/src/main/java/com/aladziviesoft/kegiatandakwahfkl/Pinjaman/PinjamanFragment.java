package com.aladziviesoft.kegiatandakwahfkl.Pinjaman;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aladziviesoft.kegiatandakwahfkl.Pinjaman.Adapter.PinjamanAdapter;
import com.aladziviesoft.kegiatandakwahfkl.Pinjaman.Model.PinjamanModel;
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
import butterknife.Unbinder;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LIST_PINJAMAN;

/**
 * A simple {@link Fragment} subclass.
 */
public class PinjamanFragment extends Fragment {


    @BindView(R.id.recyview)
    RecyclerView recyview;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    Unbinder unbinder;
    FragmentActivity mActivity;
    ArrayList<PinjamanModel> arrayList = new ArrayList<>();
    PinjamanAdapter adapter;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    SessionManager sessionManager;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    OwnProgressDialog loading;

    public PinjamanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pinjaman, container, false);
        unbinder = ButterKnife.bind(this, view);
        mActivity = getActivity();
        recyview.setHasFixedSize(true);
        requestQueue = Volley.newRequestQueue(mActivity);
        loading = new OwnProgressDialog(mActivity);
        sessionManager = new SessionManager(mActivity);


        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 1,
                LinearLayoutManager.VERTICAL, false);
        recyview.setLayoutManager(layoutManager);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                getdata();
            }
        });

        getdata();

        loading.show();

        return view;
    }

    private void getdata() {
        stringRequest = new StringRequest(Request.Method.POST, URL_LIST_PINJAMAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ds", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        PinjamanModel modelMenu = new PinjamanModel();
                        modelMenu.setId_pinjaman(json.getString("id_pinjaman"));
                        modelMenu.setNamapeminjam(json.getString("nama"));
                        modelMenu.setNominal(json.getString("nominal"));
                        modelMenu.setTempo(json.getString("jatuh_tempo"));
                        modelMenu.setSisapinjaman(json.getString("sisa_pinjaman"));

                        arrayList.add(modelMenu);
                    }
                    PinjamanAdapter adapter = new PinjamanAdapter(arrayList, mActivity);
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
        Intent intent = new Intent(mActivity, TambahPinjamanActivity.class);
        intent.putExtra("e", "e");
        startActivity(intent);
    }
}
