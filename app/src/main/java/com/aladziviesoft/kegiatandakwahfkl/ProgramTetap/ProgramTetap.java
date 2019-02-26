package com.aladziviesoft.kegiatandakwahfkl.ProgramTetap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aladziviesoft.kegiatandakwahfkl.Gemar.Gemar;
import com.aladziviesoft.kegiatandakwahfkl.Menabung.Menabung;
import com.aladziviesoft.kegiatandakwahfkl.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProgramTetap extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.nama)
    TextView nama;
    @BindView(R.id.crd_gemar)
    CardView crdGemar;
    @BindView(R.id.crd_menabung)
    CardView crdMenabung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_tetap);
        ButterKnife.bind(this);
    }



    @OnClick({R.id.crd_gemar, R.id.crd_menabung,R.id.imgBack})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.crd_gemar:
                intent = new Intent(ProgramTetap.this, Gemar.class);
                startActivity(intent);
                break;
            case R.id.crd_menabung:

                intent = new Intent(ProgramTetap.this, Menabung.class);
                startActivity(intent);
                break;
            case R.id.imgBack:
                finish();
                break;
        }
    }
}
