package com.sh.aplikasiku.ui.artikel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.adapter.UserAdapterArtikel;
import com.sh.aplikasiku.model.UserArtikel;

import java.util.ArrayList;
import java.util.List;

public class TampilArtikel extends AppCompatActivity {

    //inisiasi variabel baru dan komponen penampung
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<UserArtikel> list = new ArrayList<>();
    private AppCompatImageView gambar;
    private TextView tampiljudul, tampilpenjelasan, tvDate, tvUpdate;
    private SharedPreferences sharedPref;
    private int userrole;
    private LinearLayoutCompat llUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampil_artikel);

        //mengubah title di toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Artikel");

        //mendapatkan sharedpreferences berdasarkan key "data_user"
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);
        //get userrole
        userrole = sharedPref.getInt(getString(R.string.user_role), 0);

        //menyambungkan komponen dengan xml
        tampiljudul = findViewById(R.id.judul);
        tampilpenjelasan = findViewById(R.id.penjelasan);
        gambar = findViewById(R.id.gambar_artikel);
        tvDate = findViewById(R.id.tv_date);
        tvUpdate = findViewById(R.id.tv_update);
        llUpdate = findViewById(R.id.ll_update);

        //mendapatkan data dari intent dan memasukkan ke komponen
        Intent intent = getIntent();
        //cek userrole apakah 1(admin) atau 2(user biasa)
        if (userrole == 1) {
            //cek apakah intent kosong atau tidak
            if (intent != null) {
                //tampilkan semua data jika role 1
                tampiljudul.setText(intent.getStringExtra("judul"));
                tampilpenjelasan.setText(intent.getStringExtra("penjelasan"));
                tvDate.setText(intent.getStringExtra("dateCreated"));
                tvUpdate.setText(intent.getStringExtra("dateUpdated"));
                Glide.with(this).load(intent.getStringExtra("avatar")).into(gambar);
            }
        } else {
            //cek apakah intent kosong atau tidak
            if (intent != null) {
                //tampilkan sebagian data jika role 2 yang penting bagi user
                tampiljudul.setText(intent.getStringExtra("judul"));
                tampilpenjelasan.setText(intent.getStringExtra("penjelasan"));
                tvDate.setText(intent.getStringExtra("dateCreated"));
                tvDate.setText(intent.getStringExtra("dateUpdated"));
                Glide.with(this).load(intent.getStringExtra("avatar")).into(gambar);
                llUpdate.setVisibility(View.GONE);
            }
        }
    }

    //menutup aplikasi ketika tombol back ditekan
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    //menampilkan tombol back pada toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
