package com.aladziviesoft.kegiatandakwahfkl.PemberiInfaqShodaqohZakat.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aladziviesoft.kegiatandakwahfkl.PemberiInfaqShodaqohZakat.Model.PemberiInfaq_Model;
import com.aladziviesoft.kegiatandakwahfkl.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PemberiInfaq_Adapter extends RecyclerView.Adapter<PemberiInfaq_Adapter.Holder> {

    private List<PemberiInfaq_Model> arrayList;
    private Context context;

    public PemberiInfaq_Adapter(List<PemberiInfaq_Model> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PemberiInfaq_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_pemberiinfaqshodaqoh, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PemberiInfaq_Adapter.Holder holder, int i) {
        holder.etnama.setText(arrayList.get(i).getNama());
        holder.etNominal.setText(arrayList.get(i).getNominal());
        holder.ettanggal.setText(arrayList.get(i).getTanggal());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.etnama)
        TextView etnama;
        @BindView(R.id.etNominal)
        TextView etNominal;
        @BindView(R.id.ettanggal)
        TextView ettanggal;
        @BindView(R.id.cardview)
        CardView cardview;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
