package com.sh.aplikasiku.ui.entry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sh.aplikasiku.ui.MainActivity;
import com.sh.aplikasiku.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText editname, editemail, editpassword, editkonfirmpwd;
    private Button btnregister;

    private AppCompatTextView txtLogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editname = findViewById(R.id.name);
        editemail = findViewById(R.id.emailregister);
        editpassword = findViewById(R.id.password);
        editkonfirmpwd = findViewById(R.id.pwd_konfirmasiregister);
        btnregister = findViewById(R.id.btn_register);
        txtLogin = findViewById(R.id.txt_login);

        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("silakan tunggu");
        progressDialog.setCancelable(false);

        txtLogin.setOnClickListener(v -> {
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
        String emailToSend = email.trim();
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(emailToSend, password).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                FirebaseUser firebaseUser = task.getResult().getUser();
                if (firebaseUser != null) {
                    String idUser = firebaseUser.getUid();
                    saveDataUser(idUser, name);
                } else {
                    Toast.makeText(getApplicationContext(), "Register gagal", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            } else {
                Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void saveDataUser(String idUser, String username) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", idUser);
        user.put("username", username);
        user.put("role", 2);

        progressDialog.show();
        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    getUserData(idUser);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
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