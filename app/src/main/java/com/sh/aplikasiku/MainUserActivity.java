package com.sh.aplikasiku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainUserActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private Button btnlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        btnlogout = findViewById(R.id.logout);
        btnlogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }

    public void pantau(View view) {
        Intent i = new Intent(this, PantauKehamilan.class);
        startActivity(i);
    }

    public void rekam(View view) {
        Intent i = new Intent(this, RekamMedis.class);
        startActivity(i);
    }

    public void artikel(View view) {
        Intent i = new Intent(this, Artikel.class);
        startActivity(i);
    }

    public void rujukan(View view) {
        Intent i = new Intent(this, Rujukan.class);
        startActivity(i);
    }

    public void home(View view) {
        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }

    public void dashboard(View view) {
        Intent i = new Intent(this, Dashboard.class);
        startActivity(i);
    }

    public void notifikasi(View view) {
        Intent i = new Intent(this, Notifikasi.class);
        startActivity(i);
    }
}