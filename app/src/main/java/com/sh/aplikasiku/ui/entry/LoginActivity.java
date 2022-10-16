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

public class LoginActivity extends AppCompatActivity {

    //inisiasi variabel baru dan komponen penampung
    private EditText editemail, editpasssword;
    private Button btnlogin;
    private AppCompatTextView txtRegister;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        //menyambungkan komponen dengan xml
        editemail = findViewById(R.id.email);
        editpasssword = findViewById(R.id.password);
        btnlogin = findViewById(R.id.btn_login);
        txtRegister = findViewById(R.id.txt_register);

        //mendapatkan sharedpreferences berdasarkan key "data_user"
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);

        //mendapatkan instance firebaseauth
        mAuth = FirebaseAuth.getInstance();

        //membuat komponen progressdialog
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("silakan tunggu");
        progressDialog.setCancelable(false);

        txtRegister.setOnClickListener(v -> {
            //intent ke halaman register
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        });


        btnlogin.setOnClickListener(v -> {
            //cek apakah email dan password tidak kosong
            if (editemail.getText().length() > 0 && editpasssword.getText().length() > 0) {
                //jika tidak langsung menjalankan fungsi login
                login(editemail.getText().toString(), editpasssword.getText().toString());
            } else {
                //jika iya menampilkan peringatan
                Toast.makeText(getApplicationContext(), "Silakan isi semua data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //fungsi untuk menangani login
    private void login(String email, String password) {
        //menampung email yang diisi dan menghapus spasi yang berlebih dengan .trim()
        String emailToSend = email.trim();

        //menampilkan progress yang dibuat tadi
        progressDialog.show();

        //menjalankan fungsi login dengan email dan password
        mAuth.signInWithEmailAndPassword(emailToSend, password).addOnCompleteListener(task -> {
            //cek apakah sukses dan result tidak kosong
            if (task.isSuccessful() && task.getResult() != null) {
                //jika berhasil, cek lagi apakah user ada atau tidak
                if (task.getResult().getUser() != null) {
                    //jika ada, get id user yang baru login dan memanggil fungsi getUserdata() dan mengirim id sebagai parameter
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    if (firebaseUser != null) {
                        String idUser = firebaseUser.getUid();
                        getUserData(idUser);
                    }
                } else {
                    //jika user tidak ada, menutup progressdialog dan menampilkan toast peringatan
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login gagal", Toast.LENGTH_SHORT).show();
                }
            } else {
                //jika gagal, menutup progressdialog dan menampilkan toast peringatan
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Login gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //fungsi getUserData untuk mendapatkan dari diri/profile dari user yang login berdasarkan id
    private void getUserData(String idUser) {
        //memanggil data dari firebase dengan filter field "id" berdasarkan idUser dari parameter
        db.collection("users")
                .whereEqualTo("id", idUser)
                .get()
                .addOnCompleteListener(task -> {
                    //cek apakah fungsi berhasil
                    if (task.isSuccessful()) {
                        //coba mengecek dan menggunakan data
                        try {
                            //cek apakah data tidak kosong
                            if (task.getResult().getDocuments().size() == 0) {
                                //jika iya, menampilkan toast peringatan
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