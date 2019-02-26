package com.aladziviesoft.kegiatandakwahfkl.PemberiInfaqShodaqohZakat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.aladziviesoft.kegiatandakwahfkl.PemberiInfaqShodaqohZakat.Adapter.PemberiInfaq_Adapter;
import com.aladziviesoft.kegiatandakwahfkl.PemberiInfaqShodaqohZakat.Model.PemberiInfaq_Model;
import com.aladziviesoft.kegiatandakwahfkl.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GPemberiInfaqShodaqohZakatActivity extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.rec_pisz)
    RecyclerView recPisz;
    @BindView(R.id.Swipe)
    SwipeRefreshLayout Swipe;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    ArrayList<PemberiInfaq_Model> arrayList = new ArrayList<>();
    PemberiInfaq_Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemberi_infaq_shodaqoh_zakat);
        ButterKnife.bind(this);
        recPisz.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(GPemberiInfaqShodaqohZakatActivity.this, 1,
                GridLayoutManager.VERTICAL, false);
        recPisz.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>();
        model = new PemberiInfaq_Model("Fulan", "2000000", "14-02-2019");
        arrayList.add(model);
        model = new PemberiInfaq_Model("Fulan", "290000", "14-02-2019");
        arrayList.add(model);
        PemberiInfaq_Adapter adapter = new PemberiInfaq_Adapter(arrayList, GPemberiInfaqShodaqohZakatActivity.this);
        recPisz.setAdapter(adapter);

    }


    @OnClick({R.id.imgBack, R.id.floatingActionButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.floatingActionButton:
                Intent intent = new Intent(getApplicationContext(), TambahPemberiInfaq.class);
                startActivity(intent);
                break;
        }
    }
}
