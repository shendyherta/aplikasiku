package com.sh.aplikasiku.ui.rekammedis;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditRekam extends AppCompatActivity {
    private EditText editberat, editlingkar, editkondisi, edittekanan, editlaju, editsuhu, editdenyut;
    private Button btnsave;
    private AppCompatSpinner spinner_pasien;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    private String id = "", id_user, option;
    private ArrayList<User> listUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rekam);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editberat = findViewById(R.id.berat);
        editlingkar = findViewById(R.id.lingkar);
        editkondisi = findViewById(R.id.kondisi);
        edittekanan = findViewById(R.id.tekanan);
        editlaju = findViewById(R.id.laju);
        editsuhu = findViewById(R.id.suhu);
        editdenyut = findViewById(R.id.denyut);
        btnsave = findViewById(R.id.btn_save);
        spinner_pasien = findViewById(R.id.spinner_pasien);

        progressDialog = new ProgressDialog(EditRekam.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("menyimpan...");

        btnsave.setOnClickListener(v -> {
            if (editberat.getText().length() > 0 && editlingkar.getText().length() > 0 && editkondisi.getText().length() > 0 && edittekanan.getText().length() > 0 && editlaju.getText().length() > 0 && editsuhu.getText().length() > 0 && editdenyut.getText().length() > 0) {
                saveData(editberat.getText().toString(), editlingkar.getText().toString(), editkondisi.getText().toString(), edittekanan.getText().toString(), editlaju.getText().toString(), editsuhu.getText().toString(), editdenyut.getText().toString());
            } else {
                Toast.makeText(getApplicationContext(), "Silakan isi dulu artikel", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            option = intent.getStringExtra("option");
            if (option.equalsIgnoreCase("edit")) {
                id = intent.getStringExtra("id");
                editberat.setText(intent.getStringExtra("berat"));
                editlingkar.setText(intent.getStringExtra("lingkar"));
                editkondisi.setText(intent.getStringExtra("laju"));
                edittekanan.setText(intent.getStringExtra("tekanan"));
                editlaju.setText(intent.getStringExtra("suhu"));
                editsuhu.setText(intent.getStringExtra("denyut"));
                editdenyut.setText(intent.getStringExtra("kondisi"));
            }
        }

        getAllUserData();
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

    private void saveData(String denyut, String suhu, String laju, String tekanan, String kondisi, String berat, String lingkar) {
        progressDialog.show();
        //untuk menambahkan data, jika id length not null berati edit kalau di else nya berarti fitur tambah

        if (id != null) {
            Map<String, Object> rekam = new HashMap<>();
            rekam.put("berat", berat);
            rekam.put("lingkar", lingkar);
            rekam.put("kondisi", kondisi);
            rekam.put("tekanan", tekanan);
            rekam.put("laju", laju);
            rekam.put("suhu", suhu);
            rekam.put("denyut", denyut);
            db.collection("rekammedis").document(id)
                    .set(rekam)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Map<String, Object> rekam = new HashMap<>();
            rekam.put("idUser", id_user);
            rekam.put("berat", berat);
            rekam.put("lingkar", lingkar);
            rekam.put("kondisi", kondisi);
            rekam.put("tekanan", tekanan);
            rekam.put("laju", laju);
            rekam.put("suhu", suhu);
            rekam.put("denyut", denyut);

            db.collection("rekammedis")
                    .add(rekam)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
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
                            Log.d("QWE", "getData: " + e.getMessage());
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
        spinner_pasien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                User user = adapter.getItem(position);
                id_user = user.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
}