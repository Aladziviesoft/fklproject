package com.aladziviesoft.kegiatandakwahfkl.PemberiInfaqShodaqohZakat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aladziviesoft.kegiatandakwahfkl.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TambahPemberiInfaq extends AppCompatActivity {

    @BindView(R.id.etnama)
    EditText etnama;
    @BindView(R.id.etNominal)
    EditText etNominal;
    @BindView(R.id.parent_linear_layout)
    LinearLayout parentLinearLayout;
    @BindView(R.id.btSimpan)
    Button btSimpan;
    @BindView(R.id.imgBack)
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pemberi_infaq);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.imgBack, R.id.btSimpan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.btSimpan:
                finish();
                break;
        }
    }
}
