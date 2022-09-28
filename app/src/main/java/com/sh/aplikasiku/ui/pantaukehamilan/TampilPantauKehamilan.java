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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        //get userrole
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);
        userrole = sharedPref.getInt(getString(R.string.user_role), 0);

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

        Intent intent = getIntent();
        if (userrole == 1) {
            if (intent != null) {
                tampildenyutjantung.setText(String.format("%sx/menit", intent.getStringExtra("denyutjantung")));
                tampilkondisibayi.setText(intent.getStringExtra("kondisibayi"));
                tampilpasien.setText(intent.getStringExtra("pasien"));
                tampilRujukan.setText(intent.getStringExtra("rujukan"));
                tampilUpdated.setText(intent.getStringExtra("dateUpdated"));
                tampilCreated.setText(intent.getStringExtra("dateCreated"));
                setRujukanBackground();
            }
        } else {
            if (intent != null) {
                tampildenyutjantung.setText(String.format("%sx/menit", intent.getStringExtra("denyutjantung")));
                tampilkondisibayi.setText(intent.getStringExtra("kondisibayi"));
                tampilRujukan.setText(intent.getStringExtra("rujukan"));
                tampilCreated.setText(intent.getStringExtra("dateCreated"));
                llPasien.setVisibility(View.GONE);
                llUpdate.setVisibility(View.GONE);
                setRujukanBackground();
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

    private void setRujukanBackground() {
        if (tampilRujukan.getText().equals("Butuh rujukan")) {
            clRujukan.setBackgroundColor(getResources().getColor(R.color.grey_blue));
        }
    }
}
