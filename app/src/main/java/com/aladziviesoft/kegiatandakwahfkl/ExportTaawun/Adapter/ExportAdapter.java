package com.aladziviesoft.kegiatandakwahfkl.ExportTaawun.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aladziviesoft.kegiatandakwahfkl.R;
import com.aladziviesoft.kegiatandakwahfkl.KegiatanDakwah.Model.ListKegiatanModel;
import com.aladziviesoft.kegiatandakwahfkl.utils.SessionManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExportAdapter extends RecyclerView.Adapter<ExportAdapter.ExportHolder> {

    private Context context;
    private ArrayList<ListKegiatanModel> arrayList;

    public ExportAdapter(Context context, ArrayList<ListKegiatanModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ExportHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_list_export_kegiatan, viewGroup, false);
        return new ExportHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ExportHolder exportHolder, final int i) {
        int no = i + 1;
        exportHolder.txNo.setText(String.valueOf(no));
        exportHolder.txNamaKegiatan.setText(arrayList.get(i).getNamaKegiatan());
        exportHolder.linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("custom-message");
                intent.putExtra("id_kegiatan", arrayList.get(i).getIdKegiatan());
                intent.putExtra("nama_kegiatan", arrayList.get(i).getNamaKegiatan());
                intent.putExtra("jml_target", arrayList.get(i).getJumlahUangKegiatan());

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ExportHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txNo)
        TextView txNo;
        @BindView(R.id.txNamaKegiatan)
        TextView txNamaKegiatan;
        @BindView(R.id.linearlayout)
        LinearLayout linearlayout;
        SessionManager sessionManager;

        public ExportHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            sessionManager = new SessionManager(context);


        }
    }
}
