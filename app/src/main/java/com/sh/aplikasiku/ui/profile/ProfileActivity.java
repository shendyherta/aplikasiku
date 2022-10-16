package com.sh.aplikasiku.ui.profile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.ui.pantaukehamilan.EditPantau;

public class ProfileActivity extends AppCompatActivity {

    //inisiasi variabel baru dan komponen penampung
    FirebaseUser firebaseUser;
    private SharedPreferences sharedPref;
    private String username;
    private TextView tvUsername;
    private TextView tvEmail;
    private Button btnEdit;
    private int userrole;

    //membuat fungsi activity result untuk mendapatkan feedback result dari intent
    ActivityResultLauncher<Intent> getCreateEditResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    getUserData();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //mengubah title di toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profil Pengguna");

        //menyambungkan komponen dengan xml
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        btnEdit = findViewById(R.id.btn_edit);

        //menagani ketika tombol edit di tekan
        btnEdit.setOnClickListener(v -> {
            //intent ke halaman edit profile
            Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
            intent.putExtra("username", username);
            //menggunakan intent dengan activity result untuk mendapatkan feedback dari halaman edit profile
            getCreateEditResult.launch(intent);
        });

        //memanggil fungsi getUserData() untuk mendapatkan data user yang sedang login
        getUserData();

        //mengembalikan result ok untuk main activity agar menjalankan ulang pemanggilan
        // sharedpreferences jika ada perubahan data user di sharedpreferences
        setResult(RESULT_OK);
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

    //fungsi untuk mendapatkan data user
    private void getUserData() {
        //mendapatkan sharedpreferences berdasarkan key "data_user"
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);

        //get username dan role dari sharedpreferences
        username = sharedPref.getString(getString(R.string.user_name), "");
        userrole = sharedPref.getInt(getString(R.string.user_role), 0);

        //mendapatkan instance firebaseauth yang sedang login
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //set username dan role di textview
        tvUsername.setText(username);
        tvEmail.setText(firebaseUser.getEmail());

        //cek apakah role admin atau user biasa
        if (userrole == 1) {
            //jika admin, menghilangkan tombol edit
            btnEdit.setVisibility(View.GONE);
        }
    }
}