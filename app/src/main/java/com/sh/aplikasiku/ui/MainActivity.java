package com.sh.aplikasiku.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.ui.artikel.Artikel;
import com.sh.aplikasiku.ui.entry.LoginActivity;
import com.sh.aplikasiku.ui.pantaukehamilan.PantauKehamilan;
import com.sh.aplikasiku.ui.rekammedis.RekamMedis;


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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Perhatian!");
            builder.setMessage("Apakah anda ingin keluar?");
            builder.setPositiveButton("Ya",
                    (dialog, which) -> {
                        //clear sharedpref
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();

                        //logout from user
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();

                        dialog.dismiss();
                    });
            builder.setNegativeButton("Batal", (dialog, which) -> {
                dialog.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
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

}
