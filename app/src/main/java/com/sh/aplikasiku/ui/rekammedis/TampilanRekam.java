package com.sh.aplikasiku.ui.rekammedis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
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
import com.sh.aplikasiku.model.UserRekam;

import java.util.ArrayList;
import java.util.List;

public class TampilanRekam extends AppCompatActivity {
    private TextView tampilberat, tampillingkar, tampilkondisi, tampiltekanan, tampillaju,
            tampilsuhu, tampildenyut, tampilpasien, tampilUpdated, tampilCreated;
    private LinearLayoutCompat llPasien, llUpdate;
    private SharedPreferences sharedPref;
    private int userrole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_rekam);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        //get userrole
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);
        userrole = sharedPref.getInt(getString(R.string.user_role), 0);

        tampilberat = findViewById(R.id.berat);
        tampillingkar = findViewById(R.id.lingkar);
        tampilkondisi = findViewById(R.id.kondisi);
        tampiltekanan = findViewById(R.id.tekanan);
        tampillaju = findViewById(R.id.laju);
        tampilsuhu = findViewById(R.id.suhu);
        tampildenyut = findViewById(R.id.denyut);
        tampilpasien = findViewById(R.id.tv_pasien);
        tampilCreated = findViewById(R.id.tv_date);
        tampilUpdated = findViewById(R.id.tv_update);
        llPasien = findViewById(R.id.ll_pasien);
        llUpdate = findViewById(R.id.ll_update);

        Intent intent = getIntent();

        if (userrole == 1) {
            if (intent != null) {
                tampilberat.setText(intent.getStringExtra("berat"));
                tampillingkar.setText(intent.getStringExtra("lingkar"));
                tampillaju.setText(intent.getStringExtra("laju"));
                tampiltekanan.setText(intent.getStringExtra("tekanan"));
                tampilsuhu.setText(intent.getStringExtra("suhu"));
                tampildenyut.setText(intent.getStringExtra("denyut"));
                tampilkondisi.setText(intent.getStringExtra("kondisi"));
                tampilpasien.setText(intent.getStringExtra("pasien"));
                tampilUpdated.setText(intent.getStringExtra("dateUpdated"));
                tampilCreated.setText(intent.getStringExtra("dateCreated"));
            }
        } else {
            if (intent != null) {
                tampilberat.setText(intent.getStringExtra("berat"));
                tampillingkar.setText(intent.getStringExtra("lingkar"));
                tampillaju.setText(intent.getStringExtra("laju"));
                tampiltekanan.setText(intent.getStringExtra("tekanan"));
                tampilsuhu.setText(intent.getStringExtra("suhu"));
                tampildenyut.setText(intent.getStringExtra("denyut"));
                tampilkondisi.setText(intent.getStringExtra("kondisi"));
                tampilCreated.setText(intent.getStringExtra("dateCreated"));
                llPasien.setVisibility(View.GONE);
                llUpdate.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}