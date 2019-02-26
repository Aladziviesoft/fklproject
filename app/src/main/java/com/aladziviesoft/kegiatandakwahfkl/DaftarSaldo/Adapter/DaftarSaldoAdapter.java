package com.aladziviesoft.kegiatandakwahfkl.DaftarSaldo.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aladziviesoft.kegiatandakwahfkl.DaftarSaldo.Model.DaftarSaldoModel;
import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.utils.DecimalsFormat;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DaftarSaldoAdapter extends RecyclerView.Adapter<DaftarSaldoAdapter.Holder> {


    private ArrayList<DaftarSaldoModel> arrayList;
    private Context context;

    public DaftarSaldoAdapter(ArrayList<DaftarSaldoModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_menudaftarsaldo, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.nama.setText(arrayList.get(i).getJenis());
        holder.totalsaldo.setText("Rp. " + DecimalsFormat.priceWithoutDecimal(arrayList.get(i).getTotalsaldo()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.nama)
        TextView nama;
        @BindView(R.id.totalsaldo)
        TextView totalsaldo;
        @BindView(R.id.cardview)
        CardView cardview;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
