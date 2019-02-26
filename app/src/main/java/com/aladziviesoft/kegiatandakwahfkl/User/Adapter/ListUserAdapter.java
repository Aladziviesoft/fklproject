package com.aladziviesoft.kegiatandakwahfkl.User.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.User.NewUserActivity;
import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.User.Model.ListUserModel;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aladziviesoft.kegiatandakwahfkl.utils.AppConf.URL_DELETE_USER;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.Holder> {

    OwnProgressDialog pDialog;
    SessionManager sessionManager;
    String result = "result";
    String messages;
    private Context context;
    private ArrayList<ListUserModel> arrayList;
    private RequestQueue requestQueue;

    public ListUserAdapter(Context context, ArrayList<ListUserModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_user, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        int no = i + 1;
        holder.txNo.setText(String.valueOf(no + ". "));
        holder.txNamaUser.setText(arrayList.get(i).getNama_user());
        holder.txLevel.setText("Level User : " + arrayList.get(i).getLevel());

        holder.linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewUserActivity.class);
                intent.putExtra("id_user", arrayList.get(i).getId_user());
                intent.putExtra("status_simpan", "1");
                context.startActivity(intent);
            }
        });

        holder.linearlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String[] item = {"Hapus User"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Pilih Aksi");
                builder.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on item[which]
                        Intent intent;
                        switch (which) {

                            case 0:
                                item[0] = "Hapus User";
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        context);

                                // set title dialog
                                alertDialogBuilder.setTitle("Yakin ingin menghapus user " + arrayList.get(i).getNama_user() + " ?");

                                // set pesan dari dialog
                                alertDialogBuilder
                                        .setCancelable(false)
                                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Delete(arrayList.get(i).getId_user());
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
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void Delete(final String id_data) {
        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_DELETE_USER, new Response.Listener<String>() {

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

                Map<String, String> params = new HashMap<String, String>();

                params.put("id", String.valueOf(id_data));

                AndLog.ShowLog("paramsdeleteuser", String.valueOf(params));


                return params;
            }

        };
        requestQueue.add(strReq);


    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.txNo)
        TextView txNo;
        @BindView(R.id.txNamaUser)
        TextView txNamaUser;
        @BindView(R.id.linearlayout)
        LinearLayout linearlayout;
        @BindView(R.id.txLevel)
        TextView txLevel;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            pDialog = new OwnProgressDialog(context);
            requestQueue = Volley.newRequestQueue(context);
            sessionManager = new SessionManager(context);
        }
    }
}
