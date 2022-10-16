package com.sh.aplikasiku.ui.pantaukehamilan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.adapter.UserAdapterPantau;
import com.sh.aplikasiku.model.UserPantau;

import java.util.ArrayList;
import java.util.List;

public class TampilPantauKehamilan extends AppCompatActivity {

    //inisiasi variabel baru dan komponen penampung
    private RecyclerView recyclerView;
    private List<UserPantau> list = new ArrayList<>();
    private TextView tampildenyutjantung, tampilkondisibayi, tampilUpdated, tampilCreated,
            tampilpasien, tampilRujukan;
    private ConstraintLayout clRujukan;
    private LinearLayoutCompat llPasien, llUpdate;
    private SharedPreferences sharedPref;
    private int userrole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampil_pantau_kehamilan);

        //mengubah title di toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pantau Kehamilan");

        //mendapatkan sharedpreferences berdasarkan key "data_user"
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);
        //get userrole
        userrole = sharedPref.getInt(getString(R.string.user_role), 0);

        //menyambungkan komponen dengan xml
        recyclerView = findViewById(R.id.recyclerview);
        tampildenyutjantung = findViewById(R.id.denyutjantung);
        tampilkondisibayi = findViewById(R.id.kondisibayi);
        tampilpasien = findViewById(R.id.tv_pasien);
        tampilUpdated = findViewById(R.id.tv_update);
        tampilCreated = findViewById(R.id.tv_date);
        llPasien = findViewById(R.id.ll_pasien);
        llUpdate = findViewById(R.id.ll_update);
        clRujukan = findViewById(R.id.cl_rujukan);
        tampilRujukan = findViewById(R.id.tv_rujukan);

        //mendapatkan data dari intent dan memasukkan ke komponen
        Intent intent = getIntent();
        //cek userrole apakah 1(admin) atau 2(user biasa)
        if (userrole == 1) {
            //cek apakah intent kosong atau tidak
            if (intent != null) {
                //tampilkan semua data jika role 1
                tampildenyutjantung.setText(String.format("%sx/menit", intent.getStringExtra("denyutjantung")));
                tampilkondisibayi.setText(intent.getStringExtra("kondisibayi"));
                tampilpasien.setText(intent.getStringExtra("pasien"));
                tampilRujukan.setText(intent.getStringExtra("rujukan"));
                tampilUpdated.setText(intent.getStringExtra("dateUpdated"));
                tampilCreated.setText(intent.getStringExtra("dateCreated"));
                //memanggil fungsi setRujukanBackground()
                setRujukanBackground();
            }
        } else {
            //cek apakah intent kosong atau tidak
            if (intent != null) {
                //tampilkan sebagian data jika role 2 yang penting bagi user
                tampildenyutjantung.setText(String.format("%sx/menit", intent.getStringExtra("denyutjantung")));
                tampilkondisibayi.setText(intent.getStringExtra("kondisibayi"));
                tampilRujukan.setText(intent.getStringExtra("rujukan"));
                tampilCreated.setText(intent.getStringExtra("dateCreated"));
                llPasien.setVisibility(View.GONE);
                llUpdate.setVisibility(View.GONE);
                //memanggil fungsi setRujukanBackground()
                setRujukanBackground();
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

    //fungsi untuk mengubah warna background rujukan ke abu-abu jika butuh rujukan
    private void setRujukanBackground() {
        if (tampilRujukan.getText().equals("Butuh rujukan")) {
            clRujukan.setBackgroundColor(getResources().getColor(R.color.grey_blue));
        }
    }
}
