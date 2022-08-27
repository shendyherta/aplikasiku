package com.sh.aplikasiku;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private ImageButton btnlogout;
    private TextView tvUsername;
    private SharedPreferences sharedPref;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get username
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.user_name), "");

        tvUsername = findViewById(R.id.tv_username);
        tvUsername.setText(username);

        btnlogout = findViewById(R.id.logout);
        btnlogout.setOnClickListener(v -> {
            //clear sharedpref
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();

            //logout from user
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
