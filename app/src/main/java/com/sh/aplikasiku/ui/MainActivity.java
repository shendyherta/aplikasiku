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

    //create activity result
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

        tvUsername = findViewById(R.id.tv_username);
        ivProfile = findViewById(R.id.iv_profile);

        ivProfile.setOnClickListener(v -> {
            popupWindow();
        });

        getUserData();
    }

    public void pantau(View view) {
        Intent i = new Intent(MainActivity.this, PantauKehamilan.class);
        startActivity(i);
    }

    public void rekam(View view) {
        Intent i = new Intent(MainActivity.this, RekamMedis.class);
        startActivity(i);
    }

    public void artikel(View view) {
        Intent i = new Intent(MainActivity.this, Artikel.class);
        startActivity(i);
    }

    public void rujukan(View view) {
        Intent i = new Intent(MainActivity.this, Rujukan.class);
        startActivity(i);
    }

    private void getUserData() {
        //get username
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.user_name), "");
        tvUsername.setText(username);
    }

    private void popupWindow() {
        final PopupWindow popupWindow = new PopupWindow(this);

        // inflate your layout or dynamically add view
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.popup_main, null);

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_form));
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        popupWindow.showAsDropDown(ivProfile, -300, 50);

        TextView tvProfile = view.findViewById(R.id.tv_profile);
        TextView tvAbout = view.findViewById(R.id.tv_about);
        TextView tvLogout = view.findViewById(R.id.tv_logout);

        tvProfile.setOnClickListener(v -> {
            getCreateEditResult.launch(new Intent(this, ProfileActivity.class));
        });

        tvAbout.setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));

        tvLogout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Perhatian!");
            builder.setMessage("Apakah anda ingin keluar?");
            builder.setPositiveButton("Ya",
                    (dialog, which) -> {
                        //clear sharedpref
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();

                        //logout from user
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();

                        dialog.dismiss();
                    });
            builder.setNegativeButton("Batal", (dialog, which) -> {
                dialog.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

    }

}
