package com.sh.aplikasiku.ui.pantaukehamilan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditPantau extends AppCompatActivity {
    private EditText editkondisibayi, editdenyutjantung;
    private Button btnsave;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    private String id, id_user, pasien, dateCreated, dateUpdated, option;
    private ArrayList<User> listUser = new ArrayList<>();
    private AppCompatSpinner spinner_pasien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pantau);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        editdenyutjantung = findViewById(R.id.denyutjantung);
        editkondisibayi = findViewById(R.id.kondisibayi);
        btnsave = findViewById(R.id.btn_save);
        spinner_pasien = findViewById(R.id.spinner_pasien);

        progressDialog = new ProgressDialog(EditPantau.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("menyimpan...");

        btnsave.setOnClickListener(v -> {
            if (editdenyutjantung.getText().length() > 0 && editkondisibayi.getText().length() > 0) {
                saveData(editdenyutjantung.getText().toString(), editkondisibayi.getText().toString());
            } else {
                Toast.makeText(getApplicationContext(), "Silakan isi dulu artikel", Toast.LENGTH_SHORT).show();
            }
        });

        getAllUserData();

        Intent intent = getIntent();
        if (intent != null) {
            option = intent.getStringExtra("option");
            if (option.equalsIgnoreCase("edit")) {
                id = intent.getStringExtra("id");
                id_user = intent.getStringExtra("idUser");
                pasien = intent.getStringExtra("pasien");
                editdenyutjantung.setText(intent.getStringExtra("denyutjantung"));
                editkondisibayi.setText(intent.getStringExtra("kondisibayi"));
                dateCreated = intent.getStringExtra("dateCreated");
                dateUpdated = intent.getStringExtra("dateUpdated");
                spinner_pasien.setEnabled(false);
            } else {
                id = null;
            }
        }
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

    private void saveData(String denyut, String kondisi) {
        progressDialog.show();

        //untuk mendapatkan tanggal saat data dibuat
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());

        //untuk menambahkan data, jika id length not null berati edit kalau di else nya berarti fitur tambah
        if (id != null) {
            //set data pantau to create
            Map<String, Object> dataPantau = new HashMap<>();
            dataPantau.put("idPasien", id_user);
            dataPantau.put("pasien", pasien);
            dataPantau.put("denyutjantung", denyut);
            dataPantau.put("kondisibayi", kondisi);
            dataPantau.put("rujukan", checkPantauForRujukan(denyut));
            dataPantau.put("dateCreated", dateCreated);
            dataPantau.put("dateUpdated", date);

            db.collection("pantau").document(id)
                    .set(dataPantau)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            //set data pantau to create
            Map<String, Object> dataPantau = new HashMap<>();
            dataPantau.put("idPasien", id_user);
            dataPantau.put("pasien", pasien);
            dataPantau.put("denyutjantung", denyut);
            dataPantau.put("kondisibayi", kondisi);
            dataPantau.put("rujukan", checkPantauForRujukan(denyut));
            dataPantau.put("dateCreated", date);
            dataPantau.put("dateUpdated", date);

            db.collection("pantau")
                    .add(dataPantau)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    });
        }
    }

    private void getAllUserData() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            if (task.getResult().getDocuments().size() == 0) {
                                Toast.makeText(getApplicationContext(), "Data tidak ditemukan!!", Toast.LENGTH_SHORT).show();
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String id = document.get("id").toString();
                                    int role = Integer.parseInt(document.get("role").toString());
                                    String username = document.get("username").toString();
                                    User user = new User(id, username, role);
                                    listUser.add(user);
                                }
                                setSpinnerPasien();
                            }
                        } catch (Exception e) {
                            Log.d("EDITREKAMGETUSER", "getData: " + e.getMessage() + e.getLocalizedMessage());
                            Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Data Gagal", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                });
    }

    private void setSpinnerPasien() {
        ArrayAdapter<User> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listUser);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_pasien.setAdapter(adapter);

        if (id != null) {
            spinner_pasien.setSelection(getUserPosition());
        }

        spinner_pasien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                User user = adapter.getItem(position);
                id_user = user.getId();
                pasien = user.getUsername();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private int getUserPosition() {
        int pos = 0;
        for (int i = 0; i < listUser.size(); i++) {
            String id = listUser.get(i).getId();
            if (id.equals(id_user)) {
                pos = i;
                return pos;
            }
        }
        return pos;
    }

    private String checkPantauForRujukan(String denyutBayi) {
        String rujukan;

        if (Integer.parseInt(denyutBayi) <= 85 && Integer.parseInt(denyutBayi) >= 130) {
            rujukan = "Butuh rujukan";
        } else {
            rujukan = "Baik";
        }

        return rujukan;
    }
}