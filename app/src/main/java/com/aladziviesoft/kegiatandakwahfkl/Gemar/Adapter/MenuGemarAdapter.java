package com.aladziviesoft.kegiatandakwahfkl.Gemar.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aladziviesoft.kegiatandakwahfkl.Gemar.Model.menu_model;
import com.aladziviesoft.kegiatandakwahfkl.Infaq.GInfaq;
import com.aladziviesoft.kegiatandakwahfkl.MasukanDanaRiba.GMasukanDanaRiba;
import com.aladziviesoft.kegiatandakwahfkl.PemberiInfaqShodaqohZakat.GPemberiInfaqShodaqohZakatActivity;
import com.aladziviesoft.kegiatandakwahfkl.PemberiPinjaman.GPemberiPinjaman;
import com.aladziviesoft.kegiatandakwahfkl.PenerimaBantuan.GPenerimaBantuan;
import com.aladziviesoft.kegiatandakwahfkl.Pinjaman.GPinjaman;
import com.aladziviesoft.kegiatandakwahfkl.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuGemarAdapter extends RecyclerView.Adapter<MenuGemarAdapter.Holder> {


    private List<menu_model> arraylist;
    private Context context;

    public MenuGemarAdapter(List<menu_model> arraylist, Context context) {
        this.arraylist = arraylist;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_gemar, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        holder.namamenu.setText(arraylist.get(i).getNamajudul());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = arraylist.get(i).getId();
                Intent intent;
                if(id.equals("1")){
                    intent = new Intent(context, GPinjaman.class);
                    context.startActivity(intent);

                }
                if(id.equals("2")){
                    intent = new Intent(context, GPemberiPinjaman.class);
                    context.startActivity(intent);

                }
                if(id.equals("3")){
                    intent = new Intent(context, GPenerimaBantuan.class);
                    context.startActivity(intent);

                }
                if(id.equals("4")){
                    intent = new Intent(context, GPemberiInfaqShodaqohZakatActivity.class);
                    context.startActivity(intent);


                }
                if(id.equals("5")){
                    intent = new Intent(context, GMasukanDanaRiba.class);
                    context.startActivity(intent);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.namamenu)
        TextView namamenu;
        @BindView(R.id.cardview)
        CardView cardview;
        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
