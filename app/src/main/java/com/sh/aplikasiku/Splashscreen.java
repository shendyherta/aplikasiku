package com.sh.aplikasiku;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Splashscreen extends AppCompatActivity {
    private int waktu_loading = 2000;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                getUserData(id);
            } else {
                navigateToLogin();
            }
        } catch (Exception e) {
            Log.d("HALOO", "run: " + e.getMessage());
            navigateToLogin();
        }
    }

    private void navigateToLogin() {
        Intent home = new Intent(Splashscreen.this, LoginActivity.class);
        startActivity(home);
        finish();
    }

    private void getUserData(String idUser) {
        db.collection("users")
                .whereEqualTo("id", idUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                if (task.getResult().getDocuments().size() == 0) {
                                    Log.d("SplashScreen", "onComplete: User tidak ditemukan!");
                                    navigateToLogin();
                                } else {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                                Log.d("SPLASH", "onComplete: " + e.getMessage());
                                navigateToLogin();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Gagal mendapatkan data!", Toast.LENGTH_SHORT).show();
                            navigateToLogin();
                        }
                    }
                });
    }
}