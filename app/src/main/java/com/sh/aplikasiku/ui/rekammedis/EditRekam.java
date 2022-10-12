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
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditRekam extends AppCompatActivity {
    private EditText editberat, editlingkar, editkondisi, edittekananSistolik, edittekananDiastolik,
            editlaju, editsuhu, editdenyut;
    private Button btnsave;
    private AppCompatSpinner spinner_pasien;
    private RadioGroup rgRujukan;
    private MaterialRadioButton rbRujukanBaik, rbRujukanButuh;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    private String id = "", id_user, pasien, option, dateCreated, dateUpdated, rujukan = "";
    private ArrayList<User> listUser = new ArrayList<>();
    private int statusRujukan = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rekam);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        editberat = findViewById(R.id.berat);
        editlingkar = findViewById(R.id.lingkar);
        editkondisi = findViewById(R.id.kondisi);
        edittekananSistolik = findViewById(R.id.tekanan_sistolik);
        edittekananDiastolik = findViewById(R.id.tekanan_diastolik);
        editlaju = findViewById(R.id.laju);
        editsuhu = findViewById(R.id.suhu);
        editdenyut = findViewById(R.id.denyut);
        btnsave = findViewById(R.id.btn_save);
        spinner_pasien = findViewById(R.id.spinner_pasien);

        progressDialog = new ProgressDialog(EditRekam.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("menyimpan...");

        btnsave.setOnClickListener(v -> {
            if (editberat.getText().length() > 0 &&
                    editlingkar.getText().length() > 0 &&
                    editkondisi.getText().length() > 0 &&
                    edittekananSistolik.getText().length() > 0 &&
                    edittekananDiastolik.getText().length() > 0 &&
                    editlaju.getText().length() > 0 &&
                    editsuhu.getText().length() > 0 &&
                    editdenyut.getText().length() > 0 ) {
                saveData(editberat.getText().toString(),
                        editlingkar.getText().toString(),
                        editkondisi.getText().toString(),
                        edittekananSistolik.getText().toString(),
                        edittekananDiastolik.getText().toString(),
                        editlaju.getText().toString(),
                        editsuhu.getText().toString(),
                        editdenyut.getText().toString());
            } else {
                Toast.makeText(getApplicationContext(), "Mohon lengkapi form!", Toast.LENGTH_SHORT).show();
            }
        });

        getAllUserData();

        Intent intent = getIntent();
        if (intent != null) {
            option = intent.getStringExtra("option");
            if (option.equalsIgnoreCase("edit")) {
                id = intent.getStringExtra("id");
                id_user = intent.getStringExtra("idUser");
                editberat.setText(intent.getStringExtra("berat"));
                editlingkar.setText(intent.getStringExtra("lingkar"));
                editkondisi.setText(intent.getStringExtra("kondisi"));
                editlaju.setText(intent.getStringExtra("laju"));
                editsuhu.setText(intent.getStringExtra("suhu"));
                editdenyut.setText(intent.getStringExtra("denyut"));
                rujukan = intent.getStringExtra("rujukan");
                dateCreated = intent.getStringExtra("dateCreated");
                dateUpdated = intent.getStringExtra("dateUpdated");
                spinner_pasien.setEnabled(false);

                String tekananDarah = intent.getStringExtra("tekanan");
                String sistolik = tekananDarah.substring(0, tekananDarah.indexOf("/"));
                String diastolik = tekananDarah.substring(tekananDarah.indexOf("/")+1, tekananDarah.length());

                edittekananSistolik.setText(sistolik);
                edittekananDiastolik.setText(diastolik);

                //set toolbar title
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Edit Rekam Medis");
            } else {
                id = null;

                //set toolbar title
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Tambah Rekam Medis");
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

    private void saveData(String berat, String lingkar, String kondisi, String tekananSistolik,
                          String tekananDiastolik, String laju, String suhu, String denyut) {
//        progressDialog.show();

        //untuk mendapatkan tanggal saat data dibuat
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());
        String tekanan = tekananSistolik+'/'+tekananDiastolik;

        //untuk menambahkan data, jika id length not null berati edit kalau di else nya berarti fitur tambah
        if (id != null) {
            Map<String, Object> rekam = new HashMap<>();
            rekam.put("idPasien", id_user);
            rekam.put("pasien", pasien);
            rekam.put("berat", berat);
            rekam.put("lingkar", lingkar);
            rekam.put("kondisi", kondisi);
            rekam.put("tekanan", tekanan);
            rekam.put("laju", laju);
            rekam.put("suhu", suhu);
            rekam.put("denyut", denyut);
            rekam.put("rujukan", checkRekamMedisForRujukan(berat, lingkar, kondisi, tekanan, laju, suhu, denyut));
            rekam.put("dateCreated", dateCreated);
            rekam.put("dateUpdated", date);
            db.collection("rekammedis").document(id)
                    .set(rekam)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });

        } else {
            Map<String, Object> rekam = new HashMap<>();
            rekam.put("idPasien", id_user);
            rekam.put("pasien", pasien);
            rekam.put("berat", berat);
            rekam.put("lingkar", lingkar);
            rekam.put("kondisi", kondisi);
            rekam.put("tekanan", tekanan);
            rekam.put("laju", laju);
            rekam.put("suhu", suhu);
            rekam.put("denyut", denyut);
            rekam.put("rujukan", checkRekamMedisForRujukan(berat, lingkar, kondisi, tekanan, laju, suhu, denyut));
            rekam.put("dateCreated", date);
            rekam.put("dateUpdated", date);
            db.collection("rekammedis")
                    .add(rekam)
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

    private String checkRekamMedisForRujukan(String berat, String lingkar, String kondisi, String tekanan, String laju, String suhu, String denyut) {
        String rujukan;

        if (Integer.parseInt(berat) < 65 || Integer.parseInt(berat) > 70) {
            statusRujukan = statusRujukan + 1;
        }
        if (Integer.parseInt(lingkar) < 28 || Integer.parseInt(lingkar) > 30) {
            statusRujukan = statusRujukan + 1;
        }
        if (Float.parseFloat(kondisi) < 12.0 || Float.parseFloat(kondisi) > 14.0) {
            statusRujukan = statusRujukan + 1;
        }
        String sistolik = tekanan.substring(0, tekanan.indexOf("/"));
        if (Integer.parseInt(sistolik) < 108 || Integer.parseInt(sistolik) > 120) {
            statusRujukan = statusRujukan + 1;
        }
        if (Integer.parseInt(laju) < 18 || Integer.parseInt(laju) > 20) {
            statusRujukan = statusRujukan + 1;
        }
        if (Float.parseFloat(suhu) < 36.6 || Float.parseFloat(suhu) > 37) {
            statusRujukan = statusRujukan + 1;
        }
        if (Integer.parseInt(denyut) < 75 || Integer.parseInt(denyut) > 90) {
            statusRujukan = statusRujukan + 1;
        }
        if (statusRujukan >= 4) {
            rujukan = "Butuh rujukan";
        } else {
            rujukan = "Baik";
        }
        return rujukan;
    }
}