package com.aladziviesoft.kegiatandakwahfkl.PenerimaBantuan.Adapter;

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
import android.widget.TextView;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.PenerimaBantuan.Model.PenerimaBantuanModel;
import com.aladziviesoft.kegiatandakwahfkl.PenerimaBantuan.TambahPenerimaBantuan;
import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.Taawun.TambahPembayarActivity;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DELETE_PENERIMAB;

public class PenerimaBantuanAdapter extends RecyclerView.Adapter<PenerimaBantuanAdapter.Holder> {


    private Context context;
    private ArrayList<PenerimaBantuanModel> arrayList = new ArrayList<>();
    OwnProgressDialog pDialog;
    SessionManager sessionManager;
    String result = "result";
    String messages, level;
    private RequestQueue requestQueue;

    public PenerimaBantuanAdapter(Context context, ArrayList<PenerimaBantuanModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_penerima_bantuan, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        holder.etNamaPenerimaBantuan.setText(arrayList.get(i).getNama());
        holder.etNominal.setText( "Rp. " + DecimalsFormat.priceWithoutDecimal(arrayList.get(i).getNominal()));
        if(arrayList.get(i).getIdSumberDana().equals("2")) {
            holder.etSumberDana.setText("Dana Riba");
        }else {
            holder.etSumberDana.setText("Zakat");
        }
        holder.ettanggal.setText(arrayList.get(i).getTanggal());
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

                    final String[] item = {"Update", "Delete"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Pilih Aksi");
                    builder.setItems(item, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // the user clicked on item[which]
                            Intent intent;
                            switch (which) {
                                case 0:
                                    item[0] = "Update";
                                    intent = new Intent(context, TambahPenerimaBantuan.class);
                                    intent.putExtra("id_penerimab", arrayList.get(i).getId());
                                    intent.putExtra("nama", arrayList.get(i).getNama());
                                    intent.putExtra("tgl", arrayList.get(i).getTanggal());
                                    intent.putExtra("jenis", arrayList.get(i).getIdSumberDana());
                                    intent.putExtra("nomi", arrayList.get(i).getNominal());
                                    intent.putExtra("status_simpan", "1");
                                    context.startActivity(intent);
                                    break;
                                case 1:
                                    item[1] = "Delete";
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                            context);

                                    // set title dialog
                                    alertDialogBuilder.setTitle("Yakin ingin menghapus Bantuan ?");

                                    // set pesan dari dialog
                                    alertDialogBuilder
                                            .setCancelable(false)
                                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Delete(arrayList.get(i).getId(),arrayList.get(i).getIdSumberDana(),arrayList.get(i).getNominal());
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

    private void Delete(final String id_data,final String id_saldo,final String nominal) {

        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_DELETE_PENERIMAB, new Response.Listener<String>() {

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
                String token, apikey, refresh_code;
                token = sessionManager.getToken();
                apikey = sessionManager.getApikey();
                refresh_code = sessionManager.getRefreshcode();

                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("token", AESCrypt.decrypt("tok", token));
                    params.put("apikey", AESCrypt.decrypt("api", apikey));
                    params.put("refresh_code", AESCrypt.decrypt("ref", refresh_code));
                    params.put("id_penerimabantuan", id_data);
                    params.put("id_saldo", id_saldo);
                    params.put("nominal", nominal);

                    AndLog.ShowLog("params", String.valueOf(params));

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
        return arrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.etNamaPenerimaBantuan)
        TextView etNamaPenerimaBantuan;
        @BindView(R.id.etNominal)
        TextView etNominal;
        @BindView(R.id.etSumberDana)
        TextView etSumberDana;
        @BindView(R.id.ettanggal)
        TextView ettanggal;
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
