package com.sh.aplikasiku.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.model.User;
import com.sh.aplikasiku.model.UserPantau;
import com.sh.aplikasiku.ui.pantaukehamilan.PantauKehamilan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etUsername;
    private Button btnsave;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPref;
    private String username;
    private List<User> list = new ArrayList<>();
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //set toolbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profil");

        etUsername = findViewById(R.id.et_username);
        btnsave = findViewById(R.id.btn_save);

        //get intent data
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            etUsername.setText(username);
        }

        //create progress bar
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("Mengambil data");

        //init firebaseuser
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //init sharedpref
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);

        getUserData();

        btnsave.setOnClickListener(v -> {
            if (etUsername.getText().toString().trim().length() > 0) {
                saveData(etUsername.getText().toString());
            } else {
                Toast.makeText(getApplicationContext(), "Silakan isi nama dahulu!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getUserData() {
        progressDialog.show();

        db.collection("users")
                .whereEqualTo("id", firebaseUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    list.clear();
                    if (task.isSuccessful()) {
                        try {
                            if (task.getResult().getDocuments().size() == 0) {
                                Toast.makeText(EditProfileActivity.this, "Data user tidak ditemukan, coba lagi nanti!", Toast.LENGTH_SHORT).show();
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String id = document.getId();
                                    String role = document.get("role").toString();
                                    String username = document.get("username").toString();
                                    User dataUser = new User(id, username, Integer.parseInt(role));
                                    list.add(dataUser);
                                }
                            }
                        } catch (Exception e) {
                            Log.d("GETDATAUSER", "getData: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Data Gagal", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                });
    }

    private void saveData(String newUsername) {
        progressDialog.show();

        //set data pantau to create
        Map<String, Object> dataUser = new HashMap<>();
        dataUser.put("id", firebaseUser.getUid());
        dataUser.put("role", 2);
        dataUser.put("username", newUsername);

        db.collection("users").document(list.get(0).getId())
                .set(dataUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();

                        //set user role and name
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.user_name), newUsername);
                        editor.apply();

                        progressDialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}