package com.aladziviesoft.kegiatandakwahfkl.RadKegiatanSosial.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.RadKegiatanSosial.Model.ModelRabKegiatanSosial;
import com.aladziviesoft.kegiatandakwahfkl.RadKegiatanSosial.UpdateRadKegiatanSosial;
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
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DELETE_RAD;

public class AdapterRabKegiatanSosial extends RecyclerView.Adapter<AdapterRabKegiatanSosial.Holder> {

    String level;
    SessionManager sessionManager;
    OwnProgressDialog pDialog;
    private RequestQueue requestQueue;
    String result = "result";
    String messages;
    private Context context;
    private ArrayList<ModelRabKegiatanSosial> arrayList;

    public AdapterRabKegiatanSosial(Context context, ArrayList<ModelRabKegiatanSosial> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_rab, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        holder.namarab.setText(arrayList.get(i).getNamaRabSosial());
        holder.jumlahuang.setText(arrayList.get(i).getJumlahUang());
        holder.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateRadKegiatanSosial.class);
                intent.putExtra("status", "1");
                intent.putExtra("id_rad", arrayList.get(i).getId_radSosial());
                intent.putExtra("nama_rad", arrayList.get(i).getNamaRabSosial());
                intent.putExtra("biaya", arrayList.get(i).getJumlahUang());
                context.startActivity(intent);
            }
        });
        holder.btHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title dialog
                alertDialogBuilder.setTitle("Yakin ingin menghapus RAD " + arrayList.get(i).getNamaRabSosial() + " ?");

                // set pesan dari dialog
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Delete(arrayList.get(i).getId_radSosial());
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
        });

        try {
            level = AESCrypt.decrypt("lev", sessionManager.getLevel());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        if (level.equals("Umum")) {
            holder.btEdit.setVisibility(View.GONE);
            holder.btHapus.setVisibility(View.GONE);
        } else if (level.equals("Admin")) {
            holder.btEdit.setVisibility(View.VISIBLE);
            holder.btHapus.setVisibility(View.VISIBLE);
        }
    }


    private void Delete(final String id_data) {

        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_DELETE_RAD, new Response.Listener<String>() {

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
                    params.put("id_rad", String.valueOf(id_data));

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
        @BindView(R.id.namarab)
        TextView namarab;
        @BindView(R.id.jumlahuang)
        TextView jumlahuang;
        @BindView(R.id.btEdit)
        Button btEdit;
        @BindView(R.id.btHapus)
        Button btHapus;
        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            sessionManager = new SessionManager(context);
            requestQueue = Volley.newRequestQueue(context);
            pDialog = new OwnProgressDialog(context);
        }
    }
}
