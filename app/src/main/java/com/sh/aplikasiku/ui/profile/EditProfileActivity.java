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

    //inisiasi variabel baru dan komponen penampung
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

        //mengubah title di toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profil");

        //menyambungkan komponen dengan xml
        etUsername = findViewById(R.id.et_username);
        btnsave = findViewById(R.id.btn_save);

        //mendapatkan data dari intent
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            etUsername.setText(username);
        }

        //membuat komponen progressdialog
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("Mengambil data");

        //mendapatkan instance firebaseauth yang sedang login
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //mendapatkan sharedpreferences berdasarkan key "data_user"
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);

        //memanggil fungsi getUserData()
        getUserData();

        //menangani ketika tombol save di klik
        btnsave.setOnClickListener(v -> {
            //cek apakah form kosong atau tidak
            if (etUsername.getText().toString().trim().length() > 0) {
                //jika tidak, memanggil fungsi saveData dan mengirim data form sebagai parameter
                saveData(etUsername.getText().toString());
            } else {
                //jika iya, menampilkan toast peringatan
                Toast.makeText(getApplicationContext(), "Silakan isi nama dahulu!", Toast.LENGTH_SHORT).show();
            }
        });

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

    //fungsi getUserData untuk mendapatkan dari diri/profile dari user yang login berdasarkan id
    private void getUserData() {

        //menampilkan progress yang dibuat tadi
        progressDialog.show();

        //memanggil data dari firebase dengan filter field "id" berdasarkan idUser dari parameter
        db.collection("users")
                .whereEqualTo("id", firebaseUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    //cek apakah fungsi berhasil
                    if (task.isSuccessful()) {
                        //coba mengecek dan menggunakan data
                        try {
                            //cek apakah data tidak kosong
                            if (task.getResult().getDocuments().size() == 0) {
                                //jika iya, menampilkan toast peringatan
                                Toast.makeText(EditProfileActivity.this, "Data user tidak ditemukan, coba lagi nanti!", Toast.LENGTH_SHORT).show();
                            } else {
                                //jika tidak, melakukan perulangan pada data result
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //memecah data ke masing-masing variabel yang sesuai
                                    String id = document.getId();
                                    String role = document.get("role").toString();
                                    String username = document.get("username").toString();
                                    //memasukkan pecahal variabel ke variabel user
                                    User dataUser = new User(id, username, Integer.parseInt(role));
                                    //menambahkan variabel user ke vairabel list
                                    list.add(dataUser);
                                }
                            }
                        } catch (Exception e) {
                            //jika percobaan gagal, menampilkan toast peringatan
                            Log.d("GETDATAUSER", "getData: " + e.getMessage());
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

    //fungsi saveData untuk menyimpan perubahan data user
    private void saveData(String newUsername) {
        //menampilkan progress yang dibuat tadi
        progressDialog.show();

        //membuat data map untuk dikirimkan ke firebase
        Map<String, Object> dataUser = new HashMap<>();
        dataUser.put("id", firebaseUser.getUid());
        dataUser.put("role", 2);
        dataUser.put("username", newUsername);

        //mengirim data map ke firebase berdasarkan id user yang login
        db.collection("users").document(list.get(0).getId())
                .set(dataUser)
                .addOnCompleteListener(task -> {
                    //cek apakah berhasil atau tidak
                    if (task.isSuccessful()) {
                        //jika berhasil, tampilkan toast peringatan dan ganti sharedpreferences
                        Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();

                        //ganti username di sharedpreferences
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.user_name), newUsername);
                        //simpan perubahan sharedpreferences
                        editor.apply();

                        //menutup progressdialog
                        progressDialog.dismiss();
                        //mengembalikan result ok
                        setResult(RESULT_OK);
                        finish();
                        //menutup activty
                    } else {
                        //jika gagal
                        //menutup progressdialog
                        progressDialog.dismiss();
                        //jika berhasil, tampilkan toast peringatan
                        Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}