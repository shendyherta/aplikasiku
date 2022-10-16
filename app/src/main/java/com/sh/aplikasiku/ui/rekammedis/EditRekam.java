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

    //inisiasi variabel baru dan komponen penampung
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

        //mengubah title di toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        //menyambungkan komponen dengan xml
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

        //membuat komponen progressdialog
        progressDialog = new ProgressDialog(EditRekam.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("menyimpan...");

        //menangani ketika tombol save ditekan
        btnsave.setOnClickListener(v -> {
            //cek apakah data rekam tidak kosong
            if (editberat.getText().length() > 0 &&
                    editlingkar.getText().length() > 0 &&
                    editkondisi.getText().length() > 0 &&
                    edittekananSistolik.getText().length() > 0 &&
                    edittekananDiastolik.getText().length() > 0 &&
                    editlaju.getText().length() > 0 &&
                    editsuhu.getText().length() > 0 &&
                    editdenyut.getText().length() > 0) {
                //jika tidak langsung menjalankan fungsi saveData dengan mengirim
                // parameter data rekam
                saveData(editberat.getText().toString(),
                        editlingkar.getText().toString(),
                        editkondisi.getText().toString(),
                        edittekananSistolik.getText().toString(),
                        edittekananDiastolik.getText().toString(),
                        editlaju.getText().toString(),
                        editsuhu.getText().toString(),
                        editdenyut.getText().toString());
            } else {
                //jika iya menampilkan peringatan
                Toast.makeText(getApplicationContext(), "Mohon lengkapi form!", Toast.LENGTH_SHORT).show();
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
                editberat.setText(intent.getStringExtra("berat"));
                editlingkar.setText(intent.getStringExtra("lingkar"));
                editkondisi.setText(intent.getStringExtra("kondisi"));
                editlaju.setText(intent.getStringExtra("laju"));
                editsuhu.setText(intent.getStringExtra("suhu"));
                editdenyut.setText(intent.getStringExtra("denyut"));
                rujukan = intent.getStringExtra("rujukan");
                dateCreated = intent.getStringExtra("dateCreated");
                dateUpdated = intent.getStringExtra("dateUpdated");
                //disable spinner agar user tidak berubah
                spinner_pasien.setEnabled(false);

                String tekananDarah = intent.getStringExtra("tekanan");
                String sistolik = tekananDarah.substring(0, tekananDarah.indexOf("/"));
                String diastolik = tekananDarah.substring(tekananDarah.indexOf("/") + 1, tekananDarah.length());

                edittekananSistolik.setText(sistolik);
                edittekananDiastolik.setText(diastolik);

                //mengubah title di toolbar
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Edit Rekam Medis");
            } else {
                //jika selain edit, kosongi variabel id
                id = null;

                //mengubah title di toolbar
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Tambah Rekam Medis");
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

    //fungsi untuk menyimpan data rekam dengan parameter data rekam
    private void saveData(String berat, String lingkar, String kondisi, String tekananSistolik,
                          String tekananDiastolik, String laju, String suhu, String denyut) {

        //menampilkan progress yang dibuat tadi
        progressDialog.show();

        //untuk mendapatkan tanggal saat data dibuat
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());
        String tekanan = tekananSistolik + '/' + tekananDiastolik;

        //untuk menambahkan data, jika id length not null berati edit kalau di else nya berarti fitur tambah
        if (id != null) {
            //membuat data map untuk dikirim ke firebase
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

            //mengirim data rekam medis ke firebase dengan parameter id document data rekam medis
            db.collection("rekammedis").document(id)
                    .set(rekam)
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
                            Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        } else {
            //membuat data map untuk dikirim ke firebase
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

            //mengirim data pantau ke firebase
            db.collection("rekammedis")
                    .add(rekam)
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

        //cek apakah statusRujukan lebih dari sama dengan 4
        if (statusRujukan >= 4) {
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