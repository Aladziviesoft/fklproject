package com.aladziviesoft.kegiatandakwahfkl.DakwahSosial;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.aladziviesoft.kegiatandakwahfkl.DakwahSosial.Adapter.DaksosAdapter;
import com.aladziviesoft.kegiatandakwahfkl.DakwahSosial.Model.DaksosModel;
import com.aladziviesoft.kegiatandakwahfkl.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DaksosActivity extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.rec_daksos)
    RecyclerView recDaksos;
    ArrayList<DaksosModel> arrayList = new ArrayList<>();
    DaksosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daksos);
        ButterKnife.bind(this);

        recDaksos.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(DaksosActivity.this, 2, LinearLayoutManager.VERTICAL, false);
        recDaksos.setLayoutManager(manager);

        adapter = new DaksosAdapter(DaksosActivity.this, arrayList);
        recDaksos.setAdapter(adapter);

        menuslist();
    }

    private void menuslist() {
        arrayList.add(new DaksosModel("Kegiatan" + "\n" + "Dakwah", R.drawable.kegiatan));
        arrayList.add(new DaksosModel("Kegiatan" + "\n" + "Sosial", R.drawable.kegiatan));
    }

    @OnClick(R.id.imgBack)
    public void onViewClicked() {
        finish();
    }
}
