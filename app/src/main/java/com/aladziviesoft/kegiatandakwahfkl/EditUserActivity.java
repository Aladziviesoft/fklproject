package com.aladziviesoft.kegiatandakwahfkl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.BASE_API;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.BASE_URL;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_EDIT_FOTO;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_EDIT_USER;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_PROFILE_BY_ID;

public class EditUserActivity extends AppCompatActivity {

    @BindView(R.id.btEditFoto)
    Button btEditFoto;
    @BindView(R.id.btSimpan)
    AppCompatButton btSimpan;
    SessionManager sessionManager;
    String result = "result";
    String messages;
    OwnProgressDialog pDialog;
    ConnectivityManager conMgr;
    StringRequest stringRequest;
    @BindView(R.id.imageView)
    CircleImageView imageView;
    @BindView(R.id.txNamaUser)
    EditText txNamaUser;
    @BindView(R.id.txAlamat)
    EditText txAlamat;
    @BindView(R.id.txUsername)
    EditText txUsername;
    @BindView(R.id.txPassword)
    EditText txPassword;
    @BindView(R.id.txNoTelp)
    EditText txNoTelp;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.btUpdate)
    Button btUpdate;
    @BindView(R.id.linearlayout)
    LinearLayout linearlayout;
    int PICK_IMAGE_REQUEST = 1, PICK_IMAGE_REQUEST2 = 2;
    Bitmap bitmap, decoded;
    File outFileKtp, sdCard, dir;
    String image, fileName;
    int bitmap_size = 50;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.btCancel)
    Button btCancel;
    @BindView(R.id.linear1)
    LinearLayout linear1;
    @BindView(R.id.linear2)
    LinearLayout linear2;
    @BindView(R.id.btSimpanFoto)
    Button btSimpanFoto;
    @BindView(R.id.txLevel)
    EditText txLevel;
    String level;
    @BindView(R.id.txWarning2)
    TextView txWarning2;
    @BindView(R.id.txWarning)
    TextView txWarning;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edituser);
        ButterKnife.bind(this);
        cekInternet();
        sessionManager = new SessionManager(EditUserActivity.this);
        pDialog = new OwnProgressDialog(EditUserActivity.this);
        requestQueue = Volley.newRequestQueue(EditUserActivity.this);

        linear1.setVisibility(View.GONE);
        for (int i = 0; i < linear2.getChildCount(); i++) {
            View view = linear2.getChildAt(i);
            view.setEnabled(false); // Or whatever you want to do with the view.
        }
        btSimpan.setVisibility(View.GONE);
        btSimpanFoto.setVisibility(View.GONE);
        txWarning.setVisibility(View.GONE);
        txWarning2.setVisibility(View.GONE);

        pDialog.show();
        LihatData();
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

    private void LihatData() {
        stringRequest = new StringRequest(Request.Method.POST, URL_PROFILE_BY_ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("detail", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    result = jsonObject.getString("data");
                    if (result.equals("expired")) {

                        sessionManager.logoutUser();

                        Intent intent = new Intent(EditUserActivity.this, LoginActivity.class);
                        finish();
                        startActivity(intent);

                    } else {

                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int a = 0; a < jsonArray.length(); a++) {
                            JSONObject json = jsonArray.getJSONObject(a);
                            String image = json.getString("image");
                            txNamaUser.setText(json.getString("full_name"));
                            txAlamat.setText(json.getString("alamat"));
                            txNoTelp.setText(json.getString("phone"));
                            txUsername.setText(json.getString("username"));
                            txPassword.requestFocus();
                            Glide.with(EditUserActivity.this).load(BASE_URL + BASE_API + "assets/images/" + image)
                                    .fitCenter() // menyesuaikan ukuran imageview
                                    .crossFade() // animasi
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageView);
                            try {
                                level = AESCrypt.decrypt("lev", sessionManager.getLevel());
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                            }
                            if (level.equals("Umum")) {
                                txLevel.setText("Umum");
                            } else if (level.equals("Admin")) {
                                txLevel.setText("Admin");
                            }
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
                id_majlis = sessionManager.getIdMajlis();

                Map<String, String> params = new HashMap<String, String>();
                try {
//                    params.put("id_majlis", AESCrypt.decrypt("id_majlis", id_majlis));
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                    params.put("id", AESCrypt.decrypt("id_user", sessionManager.getIduser()));
                    AndLog.ShowLog("params", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };

        requestQueue.add(stringRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        sdCard = Environment.getExternalStorageDirectory();
        dir = new File(sdCard.getAbsolutePath() + "/ImageRS");
        dir.mkdirs();
        fileName = String.format("%d.jpg", System.currentTimeMillis());
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        imageView.setImageBitmap(decoded);
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void SimpanData() {
        pDialog.show();
        if (txNamaUser.getText().length() > 1) {
            StringRequest strReq = new StringRequest(Request.Method.POST, URL_EDIT_USER, new Response.Listener<String>() {

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
                        GlobalToast.ShowToast(EditUserActivity.this, "Berhasil di ubah");
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
                    String token, id_majlis, apikey, refresh_code;
                    token = sessionManager.getToken();
                    apikey = sessionManager.getApikey();
                    refresh_code = sessionManager.getRefreshcode();


                    Map<String, String> params = new HashMap<String, String>();
                    try {

                        params.put("token", AESCrypt.decrypt("tok", token));
                        params.put("apikey", AESCrypt.decrypt("api", apikey));
                        params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                        params.put("id", AESCrypt.decrypt("id_user", sessionManager.getIduser()));
                        params.put("full_name", txNamaUser.getText().toString());
                        params.put("username", txUsername.getText().toString());
                        params.put("password", txPassword.getText().toString());
                        params.put("level", txLevel.getText().toString());
                        params.put("alamat", txAlamat.getText().toString());
                        params.put("phone", txNoTelp.getText().toString());
//                        if (decoded != null) {
//                            params.put("image", getStringImage(decoded));
//                        }


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

    private void UbahGambar() {
        pDialog.show();
        if (txNamaUser.getText().length() > 1) {
            StringRequest strReq = new StringRequest(Request.Method.POST, URL_EDIT_FOTO, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    String values = "log";
                    AndLog.ShowLog(values, response);
                    if (response.equals("1")) {
                        Snackbar snackbar = Snackbar
                                .make(linearlayout, "Gagal, coba ulangi", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        pDialog.dismiss();
                    } else {
//                        Snackbar snackbar = Snackbar
//                                .make(linearlayout, "Success :" + response, Snackbar.LENGTH_LONG);
//                        snackbar.show();
                        GlobalToast.ShowToast(EditUserActivity.this, "Berhasil Di Input Ke database");
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
                    String token, id_majlis, apikey, refresh_code;
                    token = sessionManager.getToken();
                    apikey = sessionManager.getApikey();
                    refresh_code = sessionManager.getRefreshcode();


                    Map<String, String> params = new HashMap<String, String>();
                    try {

                        params.put("token", AESCrypt.decrypt("tok", token));
                        params.put("apikey", AESCrypt.decrypt("api", apikey));
                        params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                        params.put("id", AESCrypt.decrypt("id_user", sessionManager.getIduser()));
                        if (decoded != null) {
                            params.put("image", getStringImage(decoded));
                        }


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

    @OnClick({R.id.btEditFoto, R.id.btSimpan, R.id.imgBack, R.id.btUpdate, R.id.floatingActionButton, R.id.btCancel, R.id.btSimpanFoto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btSimpanFoto:
                UbahGambar();
                break;
            case R.id.btEditFoto:
                showFileChooser();
                break;
            case R.id.btSimpan:
                if (txPassword.getText().toString().trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        SimpanData();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(linearlayout, "No Internet Connection, Try Again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        pDialog.dismiss();
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(linearlayout, "Kolom tidak boleh kosong, silahkan di isi", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
            case R.id.imgBack:
                finish();
                break;
            case R.id.btUpdate:
                if (btUpdate.getText().equals("Update")) {
                    for (int i = 0; i < linear2.getChildCount(); i++) {
                        view = linear2.getChildAt(i);
                        view.setEnabled(true); // Or whatever you want to do with the view.
                    }
                    btSimpan.setVisibility(View.VISIBLE);
                    btUpdate.setText("Cancel");
                    txLevel.setEnabled(false);
                    txWarning.setVisibility(View.VISIBLE);
                } else if (btUpdate.getText().equals("Cancel")) {
                    for (int i = 0; i < linear2.getChildCount(); i++) {
                        view = linear2.getChildAt(i);
                        view.setEnabled(false); // Or whatever you want to do with the view.
                    }
                    btSimpan.setVisibility(View.GONE);
                    btUpdate.setText("Update");
                    txWarning.setVisibility(View.GONE);
                }

                break;

            case R.id.floatingActionButton:
                linear1.setVisibility(View.VISIBLE);
                btSimpanFoto.setVisibility(View.VISIBLE);
                txWarning2.setVisibility(View.VISIBLE);
                break;

            case R.id.btCancel:
                linear1.setVisibility(View.GONE);
                btSimpanFoto.setVisibility(View.GONE);
                txWarning2.setVisibility(View.GONE);
                break;
        }
    }
}
