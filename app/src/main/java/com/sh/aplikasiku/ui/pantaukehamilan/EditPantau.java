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

    //inisiasi variabel baru dan komponen penampung
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

        //mengubah title di toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        //menyambungkan komponen dengan xml
        editdenyutjantung = findViewById(R.id.denyutjantung);
        editkondisibayi = findViewById(R.id.kondisibayi);
        btnsave = findViewById(R.id.btn_save);
        spinner_pasien = findViewById(R.id.spinner_pasien);

        //membuat komponen progressdialog
        progressDialog = new ProgressDialog(EditPantau.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("menyimpan...");

        //menangani ketika tombol save ditekan
        btnsave.setOnClickListener(v -> {
            //cek apakah detak jantung dan kondisi bayi tidak kosong
            if (editdenyutjantung.getText().length() > 0 && editkondisibayi.getText().length() > 0) {
                //jika tidak langsung menjalankan fungsi saveData dengan mengirim
                // parameter detak jantung dan kondisi bayu
                saveData(editdenyutjantung.getText().toString(), editkondisibayi.getText().toString());
            } else {
                //jika iya menampilkan peringatan
                Toast.makeText(getApplicationContext(), "Silakan isi dulu artikel", Toast.LENGTH_SHORT).show();
            }
        });

        //memanggil fungsi getAllUserData()
        getAllUserData();

        //mendapatkan data dari intent
        Intent intent = getIntent();
        //cek apakah intent kosong atau tidak
        if (intent != null) {
            option = intent.getStringExtra("option");
            //cek apakah option berisi edit atau tambah/kosong
            if (option.equalsIgnoreCase("edit")) {
                //jika edit masukkan data intent ke variabel penampung dan komponen
                id = intent.getStringExtra("id");
                id_user = intent.getStringExtra("idUser");
                pasien = intent.getStringExtra("pasien");
                editdenyutjantung.setText(intent.getStringExtra("denyutjantung"));
                editkondisibayi.setText(intent.getStringExtra("kondisibayi"));
                dateCreated = intent.getStringExtra("dateCreated");
                dateUpdated = intent.getStringExtra("dateUpdated");
                //disable spinner agar user tidak berubah
                spinner_pasien.setEnabled(false);

                //mengubah title di toolbar
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Edit Pantau Kehamilan");
            } else {
                //jika selain edit, kosongi variabel id
                id = null;

                //mengubah title di toolbar
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Tambah Pantau Kehamilan");
            }
        }
    }

    //menutup aplikasi ketika tombol back ditekan
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    //menampilkan tombol back pada toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //fungsi untuk menyimpan data pantau dengan parameter denyut jantung dan kondisi bayi
    private void saveData(String denyut, String kondisi) {
        //menampilkan progress yang dibuat tadi
        progressDialog.show();

        //untuk mendapatkan tanggal saat data dibuat
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());

        //untuk menambahkan data, jika id length not null berati edit kalau di else nya berarti fitur tambah
        if (id != null) {
            //membuat data map untuk dikirim ke firebase
            Map<String, Object> dataPantau = new HashMap<>();
            dataPantau.put("idPasien", id_user);
            dataPantau.put("pasien", pasien);
            dataPantau.put("denyutjantung", denyut);
            dataPantau.put("kondisibayi", kondisi);
            dataPantau.put("rujukan", checkPantauForRujukan(denyut));
            dataPantau.put("dateCreated", dateCreated);
            dataPantau.put("dateUpdated", date);

            //mengirim data pantau ke firebase dengan parameter id document data pantau
            db.collection("pantau").document(id)
                    .set(dataPantau)
                    .addOnCompleteListener(task -> {
                        //cek apakah berhasil atau tidak
                        if (task.isSuccessful()) {
                            //jika berhasil, tampilkan toast peringatan, tutup progress dialog,
                            //kembalikan nilai result ok, dan hapus activity
                            Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            //jika gagal, tampilkan toast peringatan, tutup progress dialog
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            //membuat data map untuk dikirim ke firebase
            Map<String, Object> dataPantau = new HashMap<>();
            dataPantau.put("idPasien", id_user);
            dataPantau.put("pasien", pasien);
            dataPantau.put("denyutjantung", denyut);
            dataPantau.put("kondisibayi", kondisi);
            dataPantau.put("rujukan", checkPantauForRujukan(denyut));
            dataPantau.put("dateCreated", date);
            dataPantau.put("dateUpdated", date);

            //mengirim data pantau ke firebase
            db.collection("pantau")
                    .add(dataPantau)
                    .addOnSuccessListener(documentReference -> {
                        //jika berhasil, tampilkan toast peringatan, tutup progress dialog,
                        //kembalikan nilai result ok, dan hapus activity
                        Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        //jika berhasil, tampilkan toast peringatan, tutup progress dialog
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    });
        }
    }

    //fungsi untuk mendapatkan data semua user untuk ditampilkan di spinner
    private void getAllUserData() {
        //mendapatkan semua data user di firebase
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    //cek apakah berhasil atau tidak
                    if (task.isSuccessful()) {
                        //coba mengecek dan menggunakan data
                        try {
                            //cek apakah data tidak kosong
                            if (task.getResult().getDocuments().size() == 0) {
                                //jika iya, menampilkan toast peringatan
                                Toast.makeText(getApplicationContext(), "Data tidak ditemukan!!", Toast.LENGTH_SHORT).show();
                            } else {
                                //jika tidak, melakukan perulangan pada data result
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //memecah data ke masing-masing variabel yang sesuai
                                    String id = document.get("id").toString();
                                    int role = Integer.parseInt(document.get("role").toString());
                                    String username = document.get("username").toString();
                                    //memasukkan pecahal variabel ke variabel user
                                    User user = new User(id, username, role);
                                    //menambahkan variabel user ke vairabel list
                                    listUser.add(user);
                                }
                                //memanggil fungsi setSpinnerPasien()
                                setSpinnerPasien();
                            }
                        } catch (Exception e) {
                            //jika percobaan gagal, menampilkan toast peringatan
                            Log.d("EDITREKAMGETUSER", "getData: " + e.getMessage() + e.getLocalizedMessage());
                            Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //jika percobaan gagal, menampilkan toast peringatan
                        Toast.makeText(getApplicationContext(), "Data Gagal", Toast.LENGTH_SHORT).show();
                    }
                    //menutup progressdialog
                    progressDialog.dismiss();
                });
    }

    //fungsi untuk memasukkan data user ke spinner
    private void setSpinnerPasien() {
        //membuat adapter array menggunakan layout simple_spinner_item dan data listUser
        ArrayAdapter<User> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listUser);

        //set layout drop down dengan layout simple_spinner_dropdown_item
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //set adapter spinner dengan adapter yang baru dibuat
        spinner_pasien.setAdapter(adapter);

        //cek apakah id kosong
        if (id != null) {
            //jika tidak kosong, set item spinner yang pertama berdasarkan idUser yang dipilih
            //untuk diedit
            spinner_pasien.setSelection(getUserPosition());
        }

        //menangani ketika item pada spinner di pilih
        spinner_pasien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //Menampung data user berdasarkan position yang di pilih pada spinner
                User user = adapter.getItem(position);
                //Memasukkan data user ke variabel global id_user dan pasien
                id_user = user.getId();
                pasien = user.getUsername();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //fungsi untuk mendapatkan posisi yang terpilih dalam array dan me-return posisinya sebagai int
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

    //fungsi untuk menentukan apakah pasien baik atau butuh rujukan dan mengembalikan nilainya
    private String checkPantauForRujukan(String denyutBayi) {
        //buat variabel rujukan
        String rujukan;

        //cek apakah denyut jantung kurang dari sama dengan 85 atau lebih dari sama dengan 130
        if (Integer.parseInt(denyutBayi) <= 85 || Integer.parseInt(denyutBayi) >= 130) {
            //jika iya, isi variabel rujukan dengan "Butuh rujukan"
            rujukan = "Butuh rujukan";
        } else {
            //jika tidak, isi variabel rujukan dengan "Baik"
            rujukan = "Baik";
        }

        //kembalikan nilai rujukan
        return rujukan;
    }
}