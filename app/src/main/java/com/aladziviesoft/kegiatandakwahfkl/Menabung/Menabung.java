package com.aladziviesoft.kegiatandakwahfkl.Menabung;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.aladziviesoft.kegiatandakwahfkl.Pinjaman.Adapter.ViewPager_Adapter;
import com.aladziviesoft.kegiatandakwahfkl.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Menabung extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menabung);
        ButterKnife.bind(this);

        setuplayout();

    }

    private void setuplayout() {
        ViewPager_Adapter adapter = new ViewPager_Adapter(getSupportFragmentManager());
        adapter.add(new UnTerlaksanaFragment(), "Rencana");
        adapter.add(new TerlaksanaFragment(), "Sukses");
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(2);
        tablayout.setupWithViewPager(viewpager);
    }


    @OnClick({R.id.imgBack})
    public void onViewClicked(View view) {

        finish();

    }
}
