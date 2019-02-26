package com.aladziviesoft.kegiatandakwahfkl.KegiatanSosial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.aladziviesoft.kegiatandakwahfkl.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateKegiatanSosial extends AppCompatActivity {

    String id, nama, uang;
    @BindView(R.id.txNamaKegiatan)
    EditText txNamaKegiatan;
    @BindView(R.id.txJumlahUang)
    EditText txJumlahUang;
    @BindView(R.id.btUpdate)
    Button btUpdate;
    @BindView(R.id.imgBack)
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_kegiatan_sosial);
        ButterKnife.bind(this);


        id = getIntent().getStringExtra("id_kegiatan");
        nama = getIntent().getStringExtra("nama_kegiatan");
        uang = getIntent().getStringExtra("jumlah_uang_kegiatan");
        txNamaKegiatan.setText(nama);
        txJumlahUang.setText(uang);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_exit) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
