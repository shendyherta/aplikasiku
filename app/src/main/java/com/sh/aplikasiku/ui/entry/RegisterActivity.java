package com.sh.aplikasiku.ui.entry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.ui.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class  RegisterActivity extends AppCompatActivity {

    //inisiasi variabel baru dan komponen penampung
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

        //menyambungkan komponen dengan xml
        editname = findViewById(R.id.name);
        editemail = findViewById(R.id.emailregister);
        editpassword = findViewById(R.id.password);
        editkonfirmpwd = findViewById(R.id.pwd_konfirmasiregister);
        btnregister = findViewById(R.id.btn_register);
        txtLogin = findViewById(R.id.txt_login);

        //mendapatkan sharedpreferences berdasarkan key "data_user"
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);

        //mendapatkan instance firebaseauth
        mAuth = FirebaseAuth.getInstance();

        //membuat komponen progressdialog
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("silakan tunggu");
        progressDialog.setCancelable(false);

        //menangani ketika tombol login di klik
        txtLogin.setOnClickListener(v -> {
            //kembali ke halaman login
            finish();
        });

        //menangani ketika tombol register di klik
        btnregister.setOnClickListener(v -> {
            //cek apakah form ada yang kosong atau tidak
            if (editname.getText().length() > 0 && editemail.getText().length() > 0 && editpassword.getText().length() > 0 && editkonfirmpwd.getText().length() > 0) {
                //cek apakah password dan konfirmasi password sama
                if (editpassword.getText().toString().equals(editkonfirmpwd.getText().toString())) {
                    //jika iya, menjalankan fungsi register dengan mengirim data parameter dari form tadi
                    register(editname.getText().toString(), editemail.getText().toString(), editpassword.getText().toString());
                } else {
                    //jika tidak, menampilkan toast peringatan
                    Toast.makeText(getApplicationContext(), "Silakan masukkan password", Toast.LENGTH_SHORT).show();
                }
            } else {
                //jika ada yang kosong, menampilkan toast peringatan
                Toast.makeText(getApplicationContext(), "Silakan isi semua data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //fungsi untuk menangani register
    private void register(String name, String email, String password) {
        //menampung email yang diisi dan menghapus spasi yang berlebih dengan .trim()
        String emailToSend = email.trim();

        //menampilkan progress yang dibuat tadi
        progressDialog.show();

        //menjalankan fungsi register dengan email dan password
        mAuth.createUserWithEmailAndPassword(emailToSend, password).addOnCompleteListener(task -> {
            //cek apakah sukses dan result tidak kosong
            if (task.isSuccessful() && task.getResult() != null) {
                //jika berhasil, cek lagi apakah user ada atau tidak
                FirebaseUser firebaseUser = task.getResult().getUser();
                if (firebaseUser != null) {
                    //jika ada, get id user yang baru register dan memanggil fungsi saveDataUser()
                    // dan mengirim id dan username sebagai parameter
                    String idUser = firebaseUser.getUid();
                    saveDataUser(idUser, name);
                } else {
                    //jika tidak ada, menutup progressdialog dan menampilkan toast peringatan
                    Toast.makeText(getApplicationContext(), "Register gagal", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            } else {
                //jika gagal, menutup progressdialog dan menampilkan toast peringatan
                Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    //fungsi untuk menyimpan data user seperti username di firestore
    private void saveDataUser(String idUser, String username) {

        //membuat data map untuk dikirim ke firebase
        Map<String, Object> user = new HashMap<>();
        user.put("id", idUser);
        user.put("username", username);
        user.put("role", 2);

        //menampilkan progress dialog
        progressDialog.show();

        //mengirim data ke firebase di collection users
        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    //jika berhasil, menjalankan fungsi getUserData dengan mengirim parameter dari
                    // iduser yang baru register
                    getUserData(idUser);
                })
                .addOnFailureListener(e -> {
                    //jika gagal, menutup progressdialog dan menampilkan toast peringatan
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
    }

    //fungsi getUserData dengan parameter iduser untuk mendapatkan data user yang baru register
    private void getUserData(String idUser) {
        //memanggil data dari collection users dengan filter idUser di field id
        db.collection("users")
                .whereEqualTo("id", idUser)
                .get()
                .addOnCompleteListener(task -> {
                    //cek apakah pemanggilan berhasil
                    if (task.isSuccessful()) {
                        //coba mendapatkan data dari result
                        try {
                            //cek apakah result kosong atau tidak
                            if (task.getResult().getDocuments().size() == 0) {
                                //jika kosong, menampilkan toast peringatan
                                Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                            } else {
                                //jika tidak, melakukan perulangan pada data result
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //mendapatkan role user
                                    int role = Integer.parseInt(document.get("role").toString());

                                    //set user name dan role di sharedpreferences untuk data lokal dan pengecekan pada splashscreen
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putInt(getString(R.string.user_role), role);
                                    editor.putString(getString(R.string.user_name), document.get("username").toString());
                                    //menyimpan perubahan pada sharedpreferences
                                    editor.apply();

                                    //intent ke halaman main
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                    //menutup halaman login
                                    finish();
                                }
                            }
                        } catch (Exception e) {
                            //jika percobaan gagal, menampilkan toast peringatan
                            Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //jika gagal, menampilkan toast peringatan
                        Toast.makeText(getApplicationContext(), "Data Gagal", Toast.LENGTH_SHORT).show();
                    }
                    //menutup progressdialog
                    progressDialog.dismiss();
                });
    }
}