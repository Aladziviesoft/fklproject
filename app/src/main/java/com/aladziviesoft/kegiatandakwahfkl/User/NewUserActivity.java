package com.aladziviesoft.kegiatandakwahfkl.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.LoginActivity;
import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.utils.AndLog;
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

import java.io.File;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.BASE_API;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.BASE_URL;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_PROFILE_BY_ID;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_REGISTER;

public class NewUserActivity extends AppCompatActivity {

    @BindView(R.id.txNama)
    EditText txNama;
    @BindView(R.id.txAlamat)
    EditText txAlamat;
    @BindView(R.id.txUsername)
    EditText txUsername;
    @BindView(R.id.txPassword)
    EditText txPassword;
    @BindView(R.id.txNoTelp)
    EditText txNoTelp;
    @BindView(R.id.btRegister)
    AppCompatButton btRegister;
    OwnProgressDialog pDialog;
    int PICK_IMAGE_REQUEST = 1, PICK_IMAGE_REQUEST2 = 2;
    Bitmap bitmap, decoded;
    File outFileKtp, sdCard, dir;
    String image, fileName;
    int bitmap_size = 50;
    @BindView(R.id.linearlayout)
    LinearLayout linearlayout;
    @BindView(R.id.txLevel)
    EditText txLevel;
    String id_user, level, status_simpan;
    SessionManager sessionManager;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.imageView)
    CircleImageView imageView;
    StringRequest stringRequest;
    String result = "result";
    @BindView(R.id.linearlayoutText)
    LinearLayout linearlayoutText;
    private RequestQueue requestQueue;
    private Uri mHighQualityImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        ButterKnife.bind(this);
        pDialog = new OwnProgressDialog(NewUserActivity.this);
        requestQueue = Volley.newRequestQueue(NewUserActivity.this);
        sessionManager = new SessionManager(NewUserActivity.this);

        id_user = getIntent().getStringExtra("id_user");
        status_simpan = getIntent().getStringExtra("status_simpan");
        if (status_simpan.equals("1")) {
            for (int i = 0; i < linearlayoutText.getChildCount(); i++) {
                View view = linearlayoutText.getChildAt(i);
                view.setEnabled(false); // Or whatever you want to do with the view.
            }
            LihatData();
            btRegister.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < linearlayout.getChildCount(); i++) {
                View view = linearlayout.getChildAt(i);
                view.setEnabled(true); // Or whatever you want to do with the view.
            }
            btRegister.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }


    }

    private void LihatData() {
        pDialog.show();
        stringRequest = new StringRequest(Request.Method.POST, URL_PROFILE_BY_ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("detail", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    result = jsonObject.getString("data");
                    if (result.equals("expired")) {

                        sessionManager.logoutUser();

                        Intent intent = new Intent(NewUserActivity.this, LoginActivity.class);
                        finish();
                        startActivity(intent);

                    } else {

                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int a = 0; a < jsonArray.length(); a++) {
                            JSONObject json = jsonArray.getJSONObject(a);
                            String image = json.getString("image");
                            txNama.setText(json.getString("full_name"));
                            txAlamat.setText(json.getString("alamat"));
                            txNoTelp.setText(json.getString("phone"));
                            txUsername.setText(json.getString("username"));
                            txPassword.requestFocus();
                            Glide.with(NewUserActivity.this).load(BASE_URL + BASE_API + "assets/images/" + image)
                                    .fitCenter() // menyesuaikan ukuran imageview
                                    .crossFade() // animasi
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageView);

                            txLevel.setText(json.getString("level"));

                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
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
                    params.put("id", id_user);
                    AndLog.ShowLog("params", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };

        requestQueue.add(stringRequest);

    }

    private void SimpanData() {
        pDialog.show();
        if (txNama.getText().length() > 1) {
            StringRequest strReq = new StringRequest(Request.Method.POST, URL_REGISTER, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    String values = "log";
                    AndLog.ShowLog(values, response);
                    if (response.equals("1")) {
                        Snackbar snackbar = Snackbar
                                .make(linearlayout, "Username tersebut telah terdaftar, mohon gunakan username yang lain", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        pDialog.dismiss();
                    } else {
//                        Snackbar snackbar = Snackbar
//                                .make(linearlayout, "Success :" + response, Snackbar.LENGTH_LONG);
//                        snackbar.show();
                        GlobalToast.ShowToast(NewUserActivity.this, "Berhasil Di Input Ke database");
                        pDialog.dismiss();
                        finish();
                    }


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

                    String id_majlis;
                    id_majlis = sessionManager.getIdMajlis();


                    Map<String, String> params = new HashMap<String, String>();

                    try {
//                        params.put("id_majlis", AESCrypt.decrypt("id_majlis", id_majlis));
                        params.put("full_name", txNama.getText().toString());
                        params.put("username", txUsername.getText().toString());
                        params.put("password", txPassword.getText().toString());
                        params.put("level", "Umum");
                        params.put("alamat", txAlamat.getText().toString());
                        params.put("phone", txNoTelp.getText().toString());
                        params.put("image", getString(R.string.base64));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    AndLog.ShowLog("params", String.valueOf(params));
                    return params;
                }

            };
            requestQueue.add(strReq);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_exit) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick({R.id.btRegister, R.id.imgBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btRegister:
                SimpanData();
                break;
            case R.id.imgBack:
                finish();
                break;
        }
    }

}
