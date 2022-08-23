package com.sh.aplikasiku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sh.aplikasiku.adapter.UserAdapter;
import com.sh.aplikasiku.model.User;

import java.util.ArrayList;
import java.util.List;

public class TampilArtikel extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<User> list = new ArrayList<>();
    private UserAdapter userAdapter;
    private ProgressDialog progressDialog;
    private AppCompatImageView gambar;
    private TextView tampiljudul, tampilpenjelasan;
    private String avatar;
    private String id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampil_artikel);
        recyclerView = findViewById(R.id.recyclerview);
        tampiljudul = findViewById(R.id.judul);
        tampilpenjelasan = findViewById(R.id.penjelasan);
        gambar = findViewById(R.id.gambar_artikel);


        Intent intent = getIntent();
        if(intent!=null){
            id= intent.getStringExtra("id");
            tampiljudul.setText(intent.getStringExtra("judul"));
            tampilpenjelasan.setText(intent.getStringExtra("penjelasan"));
            Glide.with(this).load(intent.getStringExtra("avatar")).into(gambar);

        }

    }

}
