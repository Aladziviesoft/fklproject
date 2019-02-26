package com.aladziviesoft.kegiatandakwahfkl.Menabung.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aladziviesoft.kegiatandakwahfkl.Menabung.Model.Menabung_model;
import com.aladziviesoft.kegiatandakwahfkl.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterTerlaksana extends RecyclerView.Adapter<AdapterTerlaksana.Holder> {

    private List<Menabung_model> arrayList;
    private Context context;

    public AdapterTerlaksana(List<Menabung_model> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_menabung, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.namamasjid.setText(arrayList.get(i).getNamamasjid());
        holder.tanggal.setText(arrayList.get(i).getTanggal());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        @BindView(R.id.namamasjid)
        TextView namamasjid;
        @BindView(R.id.tanggal)
        TextView tanggal;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
