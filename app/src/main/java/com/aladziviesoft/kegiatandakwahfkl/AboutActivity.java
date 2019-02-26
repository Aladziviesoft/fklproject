package com.aladziviesoft.kegiatandakwahfkl;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.btWhatsapp)
    Button btWhatsapp;
    String phone, message;
    @BindView(R.id.txNoAxis)
    TextView txNoAxis;
    @BindView(R.id.txNoTsel)
    TextView txNoTsel;
    @BindView(R.id.txEmail)
    TextView txEmail;
    String em = "infoaladzivie@gmail.com";
    @BindView(R.id.txAlamat)
    TextView txAlamat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.imgBack, R.id.txNoAxis, R.id.txNoTsel, R.id.txEmail, R.id.btWhatsapp, R.id.txAlamat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txAlamat:
                // Buat Uri dari intent string. Gunakan hasilnya untuk membuat Intent.
                Uri gmmIntentUri = Uri.parse("google.navigation:q=-8.1216438,113.2358493");

// Buat Uri dari intent gmmIntentUri. Set action => ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

// Set package Google Maps untuk tujuan aplikasi yang di Intent
                mapIntent.setPackage("com.google.android.apps.maps");

// Untuk menjalankan aplikasi yang di hendel dari intent
                startActivity(mapIntent);
                break;
            case R.id.imgBack:
                finish();
                break;
            case R.id.txNoAxis:
                String toDial = "tel:" + txNoAxis.getText().toString();

                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(toDial)));
                break;
            case R.id.txNoTsel:
                String toDial2 = "tel:" + txNoTsel.getText().toString();

                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(toDial2)));
                break;
            case R.id.txEmail:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"infoaladzivie@gmail.com"});
                //email.putExtra(Intent.EXTRA_CC, new String[]{ to});
                //email.putExtra(Intent.EXTRA_BCC, new String[]{to});
                email.putExtra(Intent.EXTRA_SUBJECT, "Judul");
                email.putExtra(Intent.EXTRA_TEXT, "Isi Email");

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));

                break;
            case R.id.btWhatsapp:
                PackageManager packageManager = getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);

                try {
                    String url = "https://api.whatsapp.com/send?phone=" + "6282232803556" + "&text=" + URLEncoder.encode("Assalamualaikum akhi.. ", "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
