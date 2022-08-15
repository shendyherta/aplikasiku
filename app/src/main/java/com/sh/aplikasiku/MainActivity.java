package com.sh.aplikasiku;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private Button btnlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnlogout = findViewById(R.id.logout);
        btnlogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }

    public void pantau(View view) {
        Intent i = new Intent(MainActivity.this, PantauKehamilan.class);
        startActivity(i);
    }

    public void rekam(View view) {
        Intent i = new Intent(MainActivity.this, RekamMedis.class);
        startActivity(i);
    }

    public void artikel(View view) {
        Intent i = new Intent(MainActivity.this, Artikel.class);
        startActivity(i);
    }

    public void rujukan(View view) {
        Intent i = new Intent(MainActivity.this, Rujukan.class);
        startActivity(i);
    }

    public void home(View view) {
        Intent i = new Intent(MainActivity.this, Home.class);
        startActivity(i);
    }

    public void dashboard(View view) {
        Intent i = new Intent(MainActivity.this, Dashboard.class);
        startActivity(i);
    }

    public void notifikasi(View view) {
        Intent i = new Intent(MainActivity.this, Notifikasi.class);
        startActivity(i);
    }


}
