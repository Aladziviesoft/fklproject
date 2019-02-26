package com.aladziviesoft.kegiatandakwahfkl.Inventory.Adapter;

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

import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.Inventory.UpdateInventoryActivity;
import com.aladziviesoft.kegiatandakwahfkl.Inventory.Model.ListInventoryModel;
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

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DELETE_INVENTORY;

public class ListInventoryAdapter extends RecyclerView.Adapter<ListInventoryAdapter.Holder> {

    private List<ListInventoryModel> arraylist;
    private Context context;
    OwnProgressDialog pDialog;
    SessionManager sessionManager;
    String result = "result";
    String messages;
    private RequestQueue requestQueue;


    public ListInventoryAdapter(List<ListInventoryModel> arraylist, Context context) {
        this.arraylist = arraylist;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_inventory, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        holder.Namabarang.setText("Nama Barang : "+arraylist.get(i).getNamaBarang());
        holder.Hargabarang.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(arraylist.get(i).getHargaBarang()));
        holder.Banyakbarang.setText("Jumlah Item : "+arraylist.get(i).getBanyakBarang());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                intent = new Intent(context, UpdateInventoryActivity.class);
                                intent.putExtra("id_inventory", arraylist.get(i).getIdBarang());
                                intent.putExtra("nama_barang", arraylist.get(i).getNamaBarang());
                                intent.putExtra("qty", arraylist.get(i).getBanyakBarang());
                                intent.putExtra("harga", arraylist.get(i).getHargaBarang());
                                context.startActivity(intent);
                                break;

                            case 1:
                                item[1] = "Delete";
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        context);

                                // set title dialog
                                alertDialogBuilder.setTitle("Yakin ingin menghapus inventory "+ arraylist.get(i).getNamaBarang()+" ?");

                                // set pesan dari dialog
                                alertDialogBuilder
                                        .setCancelable(false)
                                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Delete(arraylist.get(i).getIdBarang());
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
        });

    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    private void Delete(final String id_data) {

        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_DELETE_INVENTORY, new Response.Listener<String>() {

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
                    params.put("id_inventory", String.valueOf(id_data));

                    AndLog.ShowLog("params", String.valueOf(params));

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                return params;
            }

        };
        requestQueue.add(strReq);


    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.Namabarang)
        TextView Namabarang;
        @BindView(R.id.Hargabarang)
        TextView Hargabarang;
        @BindView(R.id.Banyakbarang)
        TextView Banyakbarang;
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
