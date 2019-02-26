package com.aladziviesoft.kegiatandakwahfkl;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.Dashboard.MainActivity;
import com.aladziviesoft.kegiatandakwahfkl.utils.AndLog;
import com.aladziviesoft.kegiatandakwahfkl.utils.AppConf;
import com.aladziviesoft.kegiatandakwahfkl.utils.GlobalToast;
import com.aladziviesoft.kegiatandakwahfkl.utils.OwnProgressDialog;
import com.aladziviesoft.kegiatandakwahfkl.utils.SessionManager;
import com.aladziviesoft.kegiatandakwahfkl.utils.VolleyHttp;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_LOGIN;
import static com.aladziviesoft.kegiatandakwahfkl.utils.SessionManager.APIKEY;
import static com.aladziviesoft.kegiatandakwahfkl.utils.SessionManager.IMAGE;
import static com.aladziviesoft.kegiatandakwahfkl.utils.SessionManager.KEY_IDUSER;
import static com.aladziviesoft.kegiatandakwahfkl.utils.SessionManager.KEY_NAMA;
import static com.aladziviesoft.kegiatandakwahfkl.utils.SessionManager.LEVEL;
import static com.aladziviesoft.kegiatandakwahfkl.utils.SessionManager.REFRESHCODE;
import static com.aladziviesoft.kegiatandakwahfkl.utils.SessionManager.TOKEN;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "result";
    private static final String TAG_MESSAGE = "message";
    @BindView(R.id.input_username)
    EditText inputUsername;
    @BindView(R.id.input_password)
    EditText inputPassword;
    @BindView(R.id.btn_login)
    AppCompatButton btnLogin;
    @BindView(R.id.btn_sign)
    Button btnSign;
    ConnectivityManager conMgr;
    OwnProgressDialog loading;
    String result;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        cekInternet();
        sessionManager = new SessionManager(LoginActivity.this);
        loading = new OwnProgressDialog(LoginActivity.this);

        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        }

    }

    private void LoginProses() {
        loading.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog(TAG, "response_login" + response);
                loading.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    result = jObj.getString(TAG_SUCCESS);

                    // Check for error node in json
                    if (result.equals("true")) {

                        String token = jObj.getString(TOKEN);
                        String apikey = jObj.getString(APIKEY);
                        String refreshcode = jObj.getString(REFRESHCODE);
                        String level = jObj.getString(LEVEL);
                        String id_user = jObj.getString(KEY_IDUSER);
                        String name = jObj.getString(KEY_NAMA);
                        String image = jObj.getString(IMAGE);


                        AndLog.ShowLog("Login berhasil", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        try {
                            sessionManager.setToken(AESCrypt.encrypt("tok", token));
                            sessionManager.setApiKey(AESCrypt.encrypt("api", apikey));
                            sessionManager.setRefreshcode(AESCrypt.encrypt("ref", refreshcode));
                            sessionManager.setLevel(AESCrypt.encrypt("lev", level));
                            sessionManager.setIduser(AESCrypt.encrypt("id_user", id_user));

                            sessionManager.setNama(name);
                            sessionManager.setImage(image);
                            sessionManager.setLogin();

                        } catch (GeneralSecurityException e) {

                        }


                        // Memanggil main activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

                if (response.equals("false")) {
                    GlobalToast.ShowToast(LoginActivity.this, "Maaf akun ini sudah masuk pada hp yang lain");
//                        AndLog.ShowLog("EWEK", "EWEK EWEK");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndLog.ShowLog(TAG, "Login Error: " + error.getMessage());
                if (error instanceof TimeoutError) {
                    Toast.makeText(LoginActivity.this, "Timeout", Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "Please check your server network", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(LoginActivity.this, "no connection", Toast.LENGTH_SHORT).show();
                }

                loading.dismiss();

            }


        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", inputUsername.getText().toString());
                params.put("password", inputPassword.getText().toString());

                return params;
            }

        };

        strReq.setTag(AppConf.httpTag);
        VolleyHttp.getInstance(LoginActivity.this).addToRequestQueue(strReq);
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

    @OnClick({R.id.btn_login, R.id.btn_sign})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (inputUsername.getText().toString().trim().length() > 0 && inputPassword.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        LoginProses();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection, Try Again", Toast.LENGTH_LONG).show();
                    }
                } else {
                    GlobalToast.ShowToast(LoginActivity.this, "Maaf, kolom tidak boleh kosong!");
                }
                break;
            case R.id.btn_sign:

                break;
        }
    }
}
