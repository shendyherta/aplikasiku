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
                Intent home = new Intent(Splashscreen.this, LoginActivity.class);
                startActivity(home);
                finish();
            }
        } catch (Exception e) {
            Log.d("HALOO", "run: " + e.getMessage());
            Intent home = new Intent(Splashscreen.this, LoginActivity.class);
            startActivity(home);
            finish();
        }
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
                                    Toast.makeText(getApplicationContext(), "User tidak ditemukan!", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        int role = Integer.parseInt(document.get("role").toString());
                                        if (role == 1) {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                        } else if (role == 2) {
                                            startActivity(new Intent(getApplicationContext(), MainUserActivity.class));
                                            finish();
                                        } else {
                                            startActivity(new Intent(getApplicationContext(), MainUserActivity.class));
                                            finish();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                                Log.d("SPLASH", "onComplete: " + e.getMessage());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Data Gagal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}