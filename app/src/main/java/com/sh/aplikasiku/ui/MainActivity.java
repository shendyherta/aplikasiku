package com.sh.aplikasiku.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.ui.artikel.Artikel;
import com.sh.aplikasiku.ui.entry.LoginActivity;
import com.sh.aplikasiku.ui.pantaukehamilan.PantauKehamilan;
import com.sh.aplikasiku.ui.profile.ProfileActivity;
import com.sh.aplikasiku.ui.rekammedis.RekamMedis;


public class MainActivity extends AppCompatActivity {

    private ImageView ivProfile;
    private TextView tvUsername;
    private SharedPreferences sharedPref;
    private String username;

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
        setContentView(R.layout.activity_main);

        //menyambungkan deklarasi komponen dengan xml
        tvUsername = findViewById(R.id.tv_username);
        ivProfile = findViewById(R.id.iv_profile);

        //menangani ketika tombol profile di tekan untuk membuka popup
        ivProfile.setOnClickListener(v -> {
            popupWindow();
        });

        //memanggil fungsi getUserData() untuk mendapatkan data user yang sedang login
        getUserData();
    }

    //untuk menangani intent ke pantau kehamilan
    public void pantau(View view) {
        Intent i = new Intent(MainActivity.this, PantauKehamilan.class);
        startActivity(i);
    }

    //untuk menangani intent ke rekam medis
    public void rekam(View view) {
        Intent i = new Intent(MainActivity.this, RekamMedis.class);
        startActivity(i);
    }

    //untuk menangani intent ke artikel
    public void artikel(View view) {
        Intent i = new Intent(MainActivity.this, Artikel.class);
        startActivity(i);
    }

    //untuk menangani intent ke rujukan
    public void rujukan(View view) {
        Intent i = new Intent(MainActivity.this, Rujukan.class);
        startActivity(i);
    }

    //untuk mendapatkan data user yang sedang login dan menambahkan ke textview username
    private void getUserData() {

        //mendapatkan sharedpreferences berdasarkan key "data_user"
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);

        // get username dan set ke textview
        username = sharedPref.getString(getString(R.string.user_name), "");
        tvUsername.setText(username);
    }

    //fungsi untuk menampilkan popup custom
    private void popupWindow() {
        //inisiasi popupWindow
        final PopupWindow popupWindow = new PopupWindow(this);

        //membuat layout inflater
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //membuat view dari layout inflater yang menargetkan custom xml popup_main
        View view = layoutInflater.inflate(R.layout.popup_main, null);

        //melakukan setting pada popupWindow
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_form));
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        popupWindow.showAsDropDown(ivProfile, -300, 50);

        //menyambungkan deklarasi komponen dengan xml
        TextView tvProfile = view.findViewById(R.id.tv_profile);
        TextView tvAbout = view.findViewById(R.id.tv_about);
        TextView tvLogout = view.findViewById(R.id.tv_logout);

        //intent ke activity profile
        tvProfile.setOnClickListener(v -> {
            //menggunakan intent dengan activity result untuk mendapatkan feedback dari ProfileActivity
            getCreateEditResult.launch(new Intent(this, ProfileActivity.class));
        });

        //intent ke activity about
        tvAbout.setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));

        //fungsi untuk keluar dari aplikasi
        tvLogout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Perhatian!");
            builder.setMessage("Apakah anda ingin keluar?");
            //fungsi ketika tombol ya ditekan
            builder.setPositiveButton("Ya",
                    (dialog, which) -> {
                        //menghapus sharedpreferences
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();

                        //logout dari firebase auth dan kembali ke aktivity login
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();

                        dialog.dismiss();
                    });
            //fungsi ketika tombol batal dihapus
            builder.setNegativeButton("Batal", (dialog, which) -> {
                dialog.dismiss();
            });

            //membangun dan menampilkan dialog popup
            AlertDialog dialog = builder.create();
            dialog.show();
        });

    }

}
