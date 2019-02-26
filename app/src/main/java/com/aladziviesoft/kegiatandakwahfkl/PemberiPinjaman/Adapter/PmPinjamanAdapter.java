package com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman.GPemberiPinjamanBaru;
import com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman.Model.PempinjModel;
import com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman.PengembalianSebagian;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_BAYAR_LUNAS;
import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DELETE_PEMBERI_PINJAMAN;

public class PmPinjamanAdapter extends RecyclerView.Adapter<PmPinjamanAdapter.PmHolder> {

    SessionManager sessionManager;
    String messages, level;
    OwnProgressDialog pDialog;
    String result = "result";
    private ArrayList<PempinjModel> arrayList;
    private Context context;
    private RequestQueue requestQueue;

    public PmPinjamanAdapter(ArrayList<PempinjModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PmHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_pemberi_pinjaman, viewGroup, false);
        return new PmHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PmHolder pmHolder, final int i) {
        pmHolder.txNamaPemberiPinjaman.setText("Nama Pemberi Pinjaman : " + "\n" + arrayList.get(i).getNamaPeminjam());
        pmHolder.txJatuhTempo.setText("Tempo : " + "\n" + arrayList.get(i).getJatuhTempo());
        pmHolder.txNoHP.setText("No. HP : " + "\n" + arrayList.get(i).getNoHP());
        pmHolder.txNoKtp.setText("No. KTP : " + "\n" + arrayList.get(i).getNoKtp());
        pmHolder.txNominal.setText("Nominal : " + "\n" + "Rp. " + DecimalsFormat.priceWithoutDecimal(arrayList.get(i).getNominal()));
        pmHolder.txSisa.setText("Sisa Hari :" + "\n" + arrayList.get(i).getSisa() + " Hari");
        if (arrayList.get(i).getStatus().equals("1")){
            pmHolder.txStatus.setText("Lunas");
            pmHolder.txStatus.setTextColor(Color.parseColor("#4caf50"));
        }else {
            pmHolder.txStatus.setTextColor(Color.parseColor("#6d122d"));
            pmHolder.txStatus.setText("Belum Lunas");
        }
        pmHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    level = AESCrypt.decrypt("lev", sessionManager.getLevel());
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                if (level.equals("Umum")) {

                } else if (level.equals("Admin")) {

                    final String[] item = {"Kembalikan Dana", "Update", "Delete"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Pilih Aksi");
                    builder.setItems(item, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // the user clicked on item[which]
                            final Intent[] intent = new Intent[1];
                            switch (which) {
                                case 0:
                                    item[0] = "Kembalikan Dana";
                                    String cek = arrayList.get(i).getStatus();
                                    if (cek.equals("1")) {
                                        Toast.makeText(context, "Pinjaman Lunas", Toast.LENGTH_SHORT).show();
                                    } else {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                context);

                                        // set title dialog
                                        alertDialogBuilder.setTitle("Metode Mengembalikan Dana");

                                        // set pesan dari dialog
                                        alertDialogBuilder
                                                .setCancelable(false)
                                                .setPositiveButton("Bayar Sebagian", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Intent A;
                                                        A = new Intent(context, PengembalianSebagian.class);
                                                        A.putExtra("idpp", arrayList.get(i).getIdPp());

                                                        context.startActivity(A);
                                                    }
                                                })
                                                .setNegativeButton("Bayar Kontan", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        bayar(arrayList.get(i).getIdPp(), arrayList.get(i).getNominal());
                                                    }
                                                });

                                        // membuat alert dialog dari builder
                                        AlertDialog alertDialog = alertDialogBuilder.create();

                                        // menampilkan alert dialog
                                        alertDialog.show();
                                    }
                                    break;
                                case 1:
                                    item[0] = "Update";
                                    intent[0] = new Intent(context, GPemberiPinjamanBaru.class);
                                    intent[0].putExtra("id_pp", arrayList.get(i).getIdPp());
                                    intent[0].putExtra("status_simpan", "1");
                                    context.startActivity(intent[0]);
                                    break;
                                case 2:

                                    item[2] = "Delete";
                                    AlertDialog.Builder alertDialogBuilderr = new AlertDialog.Builder(
                                            context);

                                    // set title dialog
                                    alertDialogBuilderr.setTitle("Yakin ingin menghapus data " + arrayList.get(i).getNamaPeminjam() + " ?");

                                    // set pesan dari dialog
                                    alertDialogBuilderr
                                            .setCancelable(false)
                                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Delete(arrayList.get(i).getIdPp(), arrayList.get(i).getNominalDua());
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
                                    AlertDialog alertDialogg = alertDialogBuilderr.create();

                                    // menampilkan alert dialog
                                    alertDialogg.show();
                                    break;
                            }
                        }
                    });


                    builder.show();
                }

            }
        });
    }


    private void bayar(final String idpinjam, final String nominalbayar) {
        final CheckBox checkBox = new CheckBox(context);
        checkBox.setText("Bayar Lunas Pinjaman");
        new AlertDialog.Builder(context)
                .setTitle("Bayar Cicilan")
                .setView(checkBox)
                .setPositiveButton("Bayar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkBox.isChecked()) {
                            bayarpinjaman(idpinjam, nominalbayar);
                        } else {

                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void bayarpinjaman(final String idpinjam, final String nominalbayar) {
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_BAYAR_LUNAS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                AndLog.ShowLog("ress", response);

                GlobalToast.ShowToast(context, "Data berhasil diedit");
                pDialog.dismiss();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,
                        "Ada Kesalahan", Toast.LENGTH_LONG).show();
                AndLog.ShowLog("errss", String.valueOf(error));
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
                    params.put("id_pp", idpinjam);
                    params.put("sisa_pengembalian", nominalbayar);
                    params.put("status", "1");

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                AndLog.ShowLog("params", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    private void Delete(final String id_data, final String nominal) {

        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_DELETE_PEMBERI_PINJAMAN, new Response.Listener<String>() {

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
                    params.put("id_pp", String.valueOf(id_data));
                    params.put("nominal", String.valueOf(nominal));
                    AndLog.ShowLog("paramsdeletetaawunsosial", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };
        requestQueue.add(strReq);


    }


    public class PmHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txNamaPemberiPinjaman)
        TextView txNamaPemberiPinjaman;
        @BindView(R.id.txNoKtp)
        TextView txNoKtp;
        @BindView(R.id.txNoHP)
        TextView txNoHP;
        @BindView(R.id.txNominal)
        TextView txNominal;
        @BindView(R.id.txJatuhTempo)
        TextView txJatuhTempo;
        @BindView(R.id.txSisa)
        TextView txSisa;
        @BindView(R.id.txStatus)
        TextView txStatus;
        @BindView(R.id.cardview)
        CardView cardview;

        public PmHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            pDialog = new OwnProgressDialog(context);
            requestQueue = Volley.newRequestQueue(context);
            sessionManager = new SessionManager(context);
        }
    }
}
