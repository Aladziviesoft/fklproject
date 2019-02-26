package com.aladziviesoft.kegiatandakwahfkl.DakwahSosial.Adapter;

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

import com.aladziviesoft.kegiatandakwahfkl.DakwahSosial.Model.DaksosModel;
import com.aladziviesoft.kegiatandakwahfkl.KegiatanDakwah.KegiatanActivity;
import com.aladziviesoft.kegiatandakwahfkl.KegiatanSosial.SosialActivity;
import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.utils.SessionManager;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DaksosAdapter extends RecyclerView.Adapter<DaksosAdapter.DashHolder> {


    String level;
    SessionManager sessionManager;
    private Context context;
    private ArrayList<DaksosModel> models;


    public DaksosAdapter(Context context, ArrayList<DaksosModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public DashHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_daksos, viewGroup, false);
        return new DashHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashHolder dashHolder, final int i) {

        dashHolder.img.setImageResource(models.get(i).getGambar());
        dashHolder.nama.setText(models.get(i).getNamaDaksos());
        dashHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                try {
                    level = AESCrypt.decrypt("lev", sessionManager.getLevel());
                    if (level.equals("Umum")) {

                        if (i == 0) {
                            intent = new Intent(context, KegiatanActivity.class);
                            context.startActivity(intent);
                        }

                        if (i == 1) {
                            intent = new Intent(context, SosialActivity.class);
                            context.startActivity(intent);
                        }

                    } else if (level.equals("Admin")) {
                        if (i == 0) {
                            intent = new Intent(context, KegiatanActivity.class);
                            context.startActivity(intent);
                        }

                        if (i == 1) {
                            intent = new Intent(context, SosialActivity.class);
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
