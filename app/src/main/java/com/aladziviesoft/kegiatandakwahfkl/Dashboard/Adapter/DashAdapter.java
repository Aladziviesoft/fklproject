package com.aladziviesoft.kegiatandakwahfkl.Dashboard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aladziviesoft.kegiatandakwahfkl.AboutActivity;
import com.aladziviesoft.kegiatandakwahfkl.DaftarSaldo.DaftarSaldo;
import com.aladziviesoft.kegiatandakwahfkl.DakwahSosial.DaksosActivity;
import com.aladziviesoft.kegiatandakwahfkl.Dashboard.Model.DashModel;
import com.aladziviesoft.kegiatandakwahfkl.EditUserActivity;
import com.aladziviesoft.kegiatandakwahfkl.ExportTaawun.ExportDataTaawunActivity;
import com.aladziviesoft.kegiatandakwahfkl.Inventory.InventoryActivity;
import com.aladziviesoft.kegiatandakwahfkl.ProgramTetap.ProgramTetap;
import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.SaldoActivity;
import com.aladziviesoft.kegiatandakwahfkl.User.ListUserActivity;
import com.aladziviesoft.kegiatandakwahfkl.utils.GlobalToast;
import com.aladziviesoft.kegiatandakwahfkl.utils.SessionManager;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashAdapter extends RecyclerView.Adapter<DashAdapter.DashHolder> {


    String level;
    SessionManager sessionManager;
    private Context context;
    private ArrayList<DashModel> models;


    public DashAdapter(Context context, ArrayList<DashModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public DashHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_menu, viewGroup, false);
        return new DashHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashHolder dashHolder, final int i) {

        dashHolder.img.setImageResource(models.get(i).getGambar());
        dashHolder.nama.setText(models.get(i).getNama());
        dashHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                try {
                    level = AESCrypt.decrypt("lev", sessionManager.getLevel());
                    if (level.equals("Umum")) {

                        if (i == 0) {
                            intent = new Intent(context, DaksosActivity.class);
                            context.startActivity(intent);
                        }

                        if (i == 1) {
                            GlobalToast.ShowToast(context, "Akses hanya untuk admin");
                        }

                        if (i == 2) {
                            intent = new Intent(context, InventoryActivity.class);
                            context.startActivity(intent);
                        }

                        if (i == 3) {
                            intent = new Intent(context, EditUserActivity.class);
                            context.startActivity(intent);
                        }

                        if (i == 4) {
                            GlobalToast.ShowToast(context, "Akses hanya untuk admin");
                        }

                        if (i == 5) {
                            intent = new Intent(context, ExportDataTaawunActivity.class);
                            context.startActivity(intent);
                        }

                        if (i == 6) {
                            intent = new Intent(context, AboutActivity.class);
                            context.startActivity(intent);
                        }
                    } else if (level.equals("Admin")) {
                        if (i == 0) {
                            intent = new Intent(context, DaksosActivity.class);
                            context.startActivity(intent);
                        }

                        if (i == 1) {
                            intent = new Intent(context, SaldoActivity.class);
                            context.startActivity(intent);
                        }

                        if (i == 2) {
                            intent = new Intent(context, InventoryActivity.class);
                            context.startActivity(intent);
                        }

                        if (i == 3) {
                            intent = new Intent(context, EditUserActivity.class);
                            context.startActivity(intent);
                        }

                        if (i == 4) {
                            intent = new Intent(context, ListUserActivity.class);
                            context.startActivity(intent);
                        }

                        if (i == 5) {
                            intent = new Intent(context, ProgramTetap.class);
                            context.startActivity(intent);
                        }

                        if (i == 6) {
                            intent = new Intent(context, DaftarSaldo.class);
                            context.startActivity(intent);
                        }

                        if (i == 7) {
                            intent = new Intent(context, ExportDataTaawunActivity.class);
                            context.startActivity(intent);
                        }
                        if (i == 8) {
                            intent = new Intent(context, AboutActivity.class);
                            context.startActivity(intent);
                        }
                    }
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }


    public class DashHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.nama)
        TextView nama;
        @BindView(R.id.cardview)
        CardView cardview;

        public DashHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            sessionManager = new SessionManager(context);
        }
    }
}
