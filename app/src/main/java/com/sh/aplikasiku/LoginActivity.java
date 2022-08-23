package com.sh.aplikasiku;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private EditText editemail, editpasssword;
    private Button btnlogin, btnregister;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //private SignInButton btnGoogle;
    //private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        editemail = findViewById(R.id.email);
        editpasssword = findViewById(R.id.password);
        btnlogin = findViewById(R.id.btn_login);
        btnregister = findViewById(R.id.btn_register);
        //btnGoogle = findViewById(R.id.btn_google);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("silakan tunggu");
        progressDialog.setCancelable(false);


        btnregister.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });
        btnlogin.setOnClickListener(v -> {
            if (editemail.getText().length() > 0 && editpasssword.getText().length() > 0) {
                login(editemail.getText().toString(), editpasssword.getText().toString());
            } else {
                Toast.makeText(getApplicationContext(), "Silakan isi semua data", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void login(String email, String password) {
        // coding login
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (task.getResult().getUser() != null) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            String idUser = firebaseUser.getUid();
                            getUserData(idUser);
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Login gagal", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login gagal", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                                    Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        int role = Integer.parseInt(document.get("role").toString());
                                        if (role == 1) {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        } else if (role == 2) {
                                            startActivity(new Intent(getApplicationContext(), MainUserActivity.class));
                                        } else {
                                            startActivity(new Intent(getApplicationContext(), MainUserActivity.class));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Data Gagal", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            getUserData(currentUser.getUid());
//        }
//    }
}