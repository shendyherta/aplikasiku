package com.sh.aplikasiku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private EditText editname, editemail, editpassword, editkonfirmpwd;
    private Button btnregister, btnlogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editname = findViewById(R.id.name);
        editemail = findViewById(R.id.emailregister);
        editpassword = findViewById(R.id.password);
        editkonfirmpwd = findViewById(R.id.pwd_konfirmasiregister);
        btnregister = findViewById(R.id.btn_register);
        btnlogin = findViewById(R.id.btn_login);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("silakan tunggu");
        progressDialog.setCancelable(false);

        btnlogin.setOnClickListener(v -> {
            finish();
        });
        btnregister.setOnClickListener(v -> {
            if (editname.getText().length() > 0 && editemail.getText().length() > 0 && editpassword.getText().length() > 0 && editkonfirmpwd.getText().length() > 0) {
                if (editpassword.getText().toString().equals(editkonfirmpwd.getText().toString())) {
                    register(editname.getText().toString(), editemail.getText().toString(), editpassword.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Silakan masukkan password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Silakan isi semua data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void register(String name, String email, String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    if (firebaseUser != null) {
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                reload();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Register gagal", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void reload() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }
}