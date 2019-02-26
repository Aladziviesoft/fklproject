package com.aladziviesoft.kegiatandakwahfkl.Dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.Dashboard.Adapter.DashAdapter;
import com.aladziviesoft.kegiatandakwahfkl.Dashboard.Model.DashModel;
import com.aladziviesoft.kegiatandakwahfkl.LoginActivity;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import de.hdodenhof.circleimageview.CircleImageView;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.BASE_API;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.BASE_URL;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DATA_KAS_BENDAHARA;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LOGOUT;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_PROFILE;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.rec_menu)
    RecyclerView recMenu;
    @BindView(R.id.txNamaUser)
    TextView txNamaUser;
    @BindView(R.id.txSaldo)
    TextView txSaldo;
    SessionManager sessionManager;
    DashAdapter adapter;
    String result = "result";
    String data = "result";
    String messages;
    OwnProgressDialog pDialog;
    ConnectivityManager conMgr;
    StringRequest stringRequest;
    @BindView(R.id.imageView)
    CircleImageView imageView;
    @BindView(R.id.txHakAkses)
    TextView txHakAkses;
    String level;
    @BindView(R.id.txNamaPenyelenggara)
    TextView txNamaPenyelenggara;
    private ArrayList<DashModel> arrayList = new ArrayList<>();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(MainActivity.this);
        pDialog = new OwnProgressDialog(MainActivity.this);
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        recMenu.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3, LinearLayoutManager.VERTICAL, false);
        recMenu.setLayoutManager(layoutManager);

        DashAdapter adapter = new DashAdapter(MainActivity.this, arrayList);
        recMenu.setAdapter(adapter);

        try {
            level = AESCrypt.decrypt("lev", sessionManager.getLevel());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        if (level.equals("Umum")) {
            txHakAkses.setText("Akses Login :  Umum");
        } else if (level.equals("Admin")) {
            txHakAkses.setText("Akses Login :  Admin");
        }

        LihatSaldo();
        LihatImage();

        setMenu();

    }

    private void LihatImage() {
        stringRequest = new StringRequest(Request.Method.POST, URL_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("mainactivityresponse", response);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        String image = json.getString("image");
                        String nama = json.getString("full_name");
//                        txNamaPenyelenggara.setText(json.getString("nama_majlis"));
                        txNamaUser.setText(nama);
                        Glide.with(MainActivity.this).load(BASE_URL + BASE_API + "assets/images/" + image)
                                .fitCenter() // menyesuaikan ukuran imageview
                                .crossFade() // animasi
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject object = new JSONObject(response);
                    result = object.getString("result");
                    if (result.equals("expired")) {
                        GlobalToast.ShowToast(MainActivity.this, "Session Expired, Silahkan Login Ulang");
                        Logout();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AndLog.ShowLog("asdasd : ", String.valueOf(error));
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                String token, apikey, refresh_code, id_user, id_majlis;
                token = sessionManager.getToken();
                apikey = sessionManager.getApikey();
                refresh_code = sessionManager.getRefreshcode();
                id_user = sessionManager.getIduser();


                Map<String, String> params = new HashMap<String, String>();
                try {

                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                    params.put("id", AESCrypt.decrypt("id_user", id_user));

                    AndLog.ShowLog("params", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };

        requestQueue.add(stringRequest);

    }

    private void LihatSaldo() {
//        pDialog.show();
//        stringRequest = new StringRequest(Request.Method.POST, URL_DATA_KAS_BENDAHARA, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                AndLog.ShowLog("saldo", response);
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//
//                    JSONArray jsonArray = jsonObject.getJSONArray("data");
//                    for (int a = 0; a < jsonArray.length(); a++) {
//                        JSONObject json = jsonArray.getJSONObject(a);
//                        if (json.getString("nominal").equals("null")) {
//                            txSaldo.setText("Rp. 0");
//                        } else {
//                            txSaldo.setText("Kas Bendahara :" + "\n" + "Rp. " + DecimalsFormat.priceWithoutDecimal(json.getString("nominal")));
//                        }
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                LihatImage();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                String token, id_majlis, apikey, refresh_code;
//                token = sessionManager.getToken();
//                apikey = sessionManager.getApikey();
//                refresh_code = sessionManager.getRefreshcode();
//
//                Map<String, String> params = new HashMap<String, String>();
//                try {
//                    params.put("token", AESCrypt.decrypt("tok", token));
//                    params.put("apikey", AESCrypt.decrypt("api", apikey));
//                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
//
//                    AndLog.ShowLog("params", String.valueOf(params));
//
//                } catch (GeneralSecurityException e) {
//                    e.printStackTrace();
//                }
//
//                return params;
//            }
//
//        };
//
//        requestQueue.add(stringRequest);
    }

    private void setMenu() {
        arrayList.add(new DashModel(R.drawable.kegiatan, "Dakwah" + "\n" + "Sosial"));
        arrayList.add(new DashModel(R.drawable.saldo, "Kas" + "\n" + "Bendahara"));
        arrayList.add(new DashModel(R.drawable.inventory, "Inventory" + "\n"));
        arrayList.add(new DashModel(R.drawable.edituser, "Edit Profile" + "\n"));
        arrayList.add(new DashModel(R.drawable.add_user, "User Baru" + "\n"));
        arrayList.add(new DashModel(R.drawable.add_user, "Program Tetap" + "\n"));
        arrayList.add(new DashModel(R.drawable.kegiatan, "Daftar Saldo" + "\n" + "Sosial"));
        arrayList.add(new DashModel(R.drawable.export, "Export" + "\n" + "Data Ta'awun"));
        arrayList.add(new DashModel(R.drawable.about2, "Tentang" + "\n" + "Aplikasi"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_logout) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set title dialog
            alertDialogBuilder.setTitle("Ingin Keluar dari aplikasi?");

            // set pesan dari dialog
            alertDialogBuilder
                    .setMessage("Klik Ya untuk keluar!")
                    .setIcon(R.drawable.ic_logout)
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Logout();

                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // jika tombol ini diklik, akan menutup dialog
                            // dan tidak terjadi apa2
                            dialog.cancel();
                        }
                    });

            // membuat alert dialog dari builder
            AlertDialog alertDialog = alertDialogBuilder.create();

            // menampilkan alert dialog
            alertDialog.show();
        }

        if (id == R.id.nav_refresh) {
            LihatSaldo();
            LihatImage();
        }

        return super.onOptionsItemSelected(item);
    }


    private void Logout() {
        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_LOGOUT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("lgout", response);

                sessionManager.logoutUser();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Ada Kesalahan", Toast.LENGTH_LONG).show();
                AndLog.ShowLog("errss", String.valueOf(error));
                pDialog.dismiss();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                String id_user;
                id_user = sessionManager.getIduser();


                Map<String, String> params = new HashMap<String, String>();
                try {

                    params.put("id", AESCrypt.decrypt("id_user", id_user));

                    AndLog.ShowLog("params", String.valueOf(params));
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("params", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);


    }

}
