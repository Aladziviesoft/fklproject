package com.aladziviesoft.kegiatandakwahfkl.Gemar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.aladziviesoft.kegiatandakwahfkl.Gemar.Adapter.MenuGemarAdapter;
import com.aladziviesoft.kegiatandakwahfkl.Gemar.Model.menu_model;
import com.aladziviesoft.kegiatandakwahfkl.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Gemar extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.recyview)
    RecyclerView recyview;
    ArrayList<menu_model> arrayList = new ArrayList<>();
    menu_model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gemar);
        ButterKnife.bind(this);
        recyview.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(Gemar.this, 2,
                GridLayoutManager.VERTICAL, false);
        recyview.setLayoutManager(layoutManager);


        arrayList = new ArrayList<>();
        model = new menu_model("1", "Pinjaman");
        arrayList.add(model);
        model = new menu_model("2", "Pemberi Pinjaman");
        arrayList.add(model);
        model = new menu_model("3", "Penerima Bantuan");
        arrayList.add(model);
        model = new menu_model("4", "Pemberi ISZ");
        arrayList.add(model);
        model = new menu_model("5", "Masukkan Dana Riba");
        arrayList.add(model);
        MenuGemarAdapter adapter = new MenuGemarAdapter(arrayList, Gemar.this);
        recyview.setAdapter(adapter);

    }

    @OnClick(R.id.imgBack)
    public void onViewClicked() {
        finish();
    }
}
