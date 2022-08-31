package com.sh.aplikasiku.ui.entry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sh.aplikasiku.ui.MainActivity;
import com.sh.aplikasiku.R;

public class LoginActivity extends AppCompatActivity {
    private EditText editemail, editpasssword;
    private Button btnlogin;
    private AppCompatTextView txtRegister;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        editemail = findViewById(R.id.email);
        editpasssword = findViewById(R.id.password);
        btnlogin = findViewById(R.id.btn_login);
        txtRegister = findViewById(R.id.txt_register);
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("silakan tunggu");
        progressDialog.setCancelable(false);


        txtRegister.setOnClickListener(v -> {
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
        String emailToSend = email.trim();
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(emailToSend, password).addOnCompleteListener(task -> {
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
        });
    }

    private void getUserData(String idUser) {
        db.collection("users")
                .whereEqualTo("id", idUser)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            if (task.getResult().getDocuments().size() == 0) {
                                Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    int role = Integer.parseInt(document.get("role").toString());

                                    //set user role and name
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putInt(getString(R.string.user_role), role);
                                    editor.putString(getString(R.string.user_name), document.get("username").toString());
                                    editor.apply();

                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Data Gagal", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                });
    }
}