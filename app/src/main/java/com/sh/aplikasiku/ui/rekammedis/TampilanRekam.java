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
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<UserRekam> list = new ArrayList<>();
    private TextView tampilberat, tampillingkar, tampilkondisi, tampiltekanan, tampillaju,
            tampilsuhu, tampildenyut, tampilpasien;
    private LinearLayoutCompat llPasien;
    private String id = "";
    private SharedPreferences sharedPref;
    private int userrole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_rekam);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get userrole
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);
        userrole = sharedPref.getInt(getString(R.string.user_role), 0);

        recyclerView = findViewById(R.id.recyclerview);
        tampilberat = findViewById(R.id.berat);
        tampillingkar = findViewById(R.id.lingkar);
        tampilkondisi = findViewById(R.id.kondisi);
        tampiltekanan = findViewById(R.id.tekanan);
        tampillaju = findViewById(R.id.laju);
        tampilsuhu = findViewById(R.id.suhu);
        tampildenyut = findViewById(R.id.denyut);
        tampilpasien = findViewById(R.id.tv_pasien);
        llPasien = findViewById(R.id.ll_pasien);

        Intent intent = getIntent();

        if (userrole == 1) {
            if (intent != null) {
                id = intent.getStringExtra("id");
                tampilberat.setText(intent.getStringExtra("berat"));
                tampillingkar.setText(intent.getStringExtra("lingkar"));
                tampillaju.setText(intent.getStringExtra("laju"));
                tampiltekanan.setText(intent.getStringExtra("tekanan"));
                tampilsuhu.setText(intent.getStringExtra("suhu"));
                tampildenyut.setText(intent.getStringExtra("denyut"));
                tampilkondisi.setText(intent.getStringExtra("kondisi"));
                tampilpasien.setText(intent.getStringExtra("pasien"));
            }
        } else {
            if (intent != null) {
                id = intent.getStringExtra("id");
                tampilberat.setText(intent.getStringExtra("berat"));
                tampillingkar.setText(intent.getStringExtra("lingkar"));
                tampillaju.setText(intent.getStringExtra("laju"));
                tampiltekanan.setText(intent.getStringExtra("tekanan"));
                tampilsuhu.setText(intent.getStringExtra("suhu"));
                tampildenyut.setText(intent.getStringExtra("denyut"));
                tampilkondisi.setText(intent.getStringExtra("kondisi"));
                llPasien.setVisibility(View.GONE);
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