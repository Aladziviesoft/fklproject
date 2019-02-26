package com.aladziviesoft.kegiatandakwahfkl.Pinjaman.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DELETE_CICILAN;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DELETE_PINJAMAN;

public class DetailCicilanAdapter extends RecyclerView.Adapter<DetailCicilanAdapter.Holder> {



    private List<DetailCicilanModel> arraylist;
    private Context context;
    OwnProgressDialog pDialog;
    SessionManager sessionManager;
    String result = "result";
    String messages;
    private RequestQueue requestQueue;
    String level;

    public DetailCicilanAdapter(List<DetailCicilanModel> arraylist, Context context) {
        this.arraylist = arraylist;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_detailcicilan, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        holder.jumlahpinjam.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(arraylist.get(i).getNominalpinjam()));
        holder.tglbayar.setText(arraylist.get(i).getTanggalbayar());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    level = AESCrypt.decrypt("lev", sessionManager.getLevel());
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                if (level.equals("Umum")) {

                } else if (level.equals("Admin")) {
                    final String[] item = {"Hapus"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Pilih Aksi");
                    builder.setItems(item, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // the user clicked on item[which]
                            Intent intent;
                            switch (which) {
                                case 0:
                                    item[0] = "Hapus";
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                            context);

                                    // set title dialog
                                    alertDialogBuilder.setTitle("Yakin ingin menghapus cicilan ? ");

                                    // set pesan dari dialog
                                    alertDialogBuilder
                                            .setCancelable(false)
                                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Delete(arraylist.get(i).getId(), arraylist.get(i).getIdcicilan(), arraylist.get(i).getNominalpinjam());
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
                                    break;

                            }
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    private void Delete(final String id_pinjaman,final String id_cicilan,final String nominal) {

        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_DELETE_CICILAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("notes: ", response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    result = jObj.getString("result");
                    if (result.equals("true")) {
                        messages = jObj.getString("message");
                        GlobalToast.ShowToast(context, messages);
                        pDialog.dismiss();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, messages, Toast.LENGTH_LONG).show();

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
                    params.put("id_pinjaman", id_pinjaman);
                    params.put("id_cicilan", id_cicilan);
                    params.put("sisa_pinjaman", nominal);

                    AndLog.ShowLog("paramsdelete", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };
        requestQueue.add(strReq);


    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.jumlahpinjam)
        TextView jumlahpinjam;
        @BindView(R.id.tglbayar)
        TextView tglbayar;
        @BindView(R.id.cardview)
        CardView cardview;
        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            pDialog = new OwnProgressDialog(context);
            requestQueue = Volley.newRequestQueue(context);
            sessionManager = new SessionManager(context);
        }
    }
}

