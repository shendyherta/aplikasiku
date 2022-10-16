package com.sh.aplikasiku.ui;

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
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.ui.entry.LoginActivity;

public class Splashscreen extends AppCompatActivity {
    private int waktu_loading = 2000;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        //cek apakah user sudah login atau belum
        try {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                //jika sudah, langsung menjalankan fungsi getUserData
                //mendapatkan id dengan fungsi firebaseauth
                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                //memanggil fungsi getUserData
                getUserData(id);
            } else {
                //memanggil fungsi navigateToLogin()
                navigateToLogin();
            }
        } catch (Exception e) {
            //saat terjadi error, langsung memanggil fungsi navigateToLogin()
            Log.d("HALOO", "run: " + e.getMessage());
            navigateToLogin();
        }
    }

    //fungsi untuk intent ke activity login
    private void navigateToLogin() {
        Intent home = new Intent(Splashscreen.this, LoginActivity.class);
        startActivity(home);
        finish();
    }

    //fungsi untuk mengecek apakah user yang login sudah terdaftar di database
    private void getUserData(String idUser) {
        db.collection("users")
                .whereEqualTo("id", idUser)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            if (task.getResult().getDocuments().size() == 0) {
                                //jika tidak ada, langsung memanggil fungsi navigateToLogin()
                                Log.d("SplashScreen", "onComplete: User tidak ditemukan!");
                                navigateToLogin();
                            } else {
                                //jika ada, langsung diarahkan ke activity main
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }
                            }
                        } catch (Exception e) {
                            //saat gagal mendapatkan data dari result, langsung diarahkan ke halaman login
                            Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                            Log.d("SPLASH", "onComplete: " + e.getMessage());
                            navigateToLogin();
                        }
                    } else {
                        //saat gagal mendapatkan data, langsung diarahkan ke halaman login
                        Toast.makeText(getApplicationContext(), "Gagal mendapatkan data!", Toast.LENGTH_SHORT).show();
                        navigateToLogin();
                    }
                });
    }
}