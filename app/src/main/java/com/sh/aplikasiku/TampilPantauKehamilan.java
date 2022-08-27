package com.sh.aplikasiku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sh.aplikasiku.adapter.UserAdapterPantau;
import com.sh.aplikasiku.model.UserPantau;

import java.util.ArrayList;
import java.util.List;

public class TampilPantauKehamilan extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<UserPantau> list = new ArrayList<>();
    private UserAdapterPantau userAdapterPantau;
    private ProgressDialog progressDialog;
    private TextView tampildenyutjantung , tampilkondisibayi;
    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampil_pantau_kehamilan);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerview);
        tampildenyutjantung = findViewById(R.id.denyutjantung);
        tampilkondisibayi = findViewById(R.id.kondisibayi);

        Intent intent = getIntent();
        if(intent!=null){
            id= intent.getStringExtra("id");
            tampildenyutjantung.setText(intent.getStringExtra("denyutjantung"));
            tampilkondisibayi.setText(intent.getStringExtra("kondisibayi"));

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
