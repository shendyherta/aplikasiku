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

    FirebaseUser firebaseUser;
    private SharedPreferences sharedPref;
    private String username;
    private TextView tvUsername;
    private TextView tvEmail;
    private Button btnEdit;
    private int userrole;

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
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profil Pengguna");

        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        btnEdit = findViewById(R.id.btn_edit);

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
            intent.putExtra("username", username);
            getCreateEditResult.launch(intent);
        });

        getUserData();
        setResult(RESULT_OK);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getUserData() {
        //get username
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);
        username = sharedPref.getString(getString(R.string.user_name), "");

        //get logged in user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userrole = sharedPref.getInt(getString(R.string.user_role), 0);

        //set user data
        tvUsername.setText(username);
        tvEmail.setText(firebaseUser.getEmail());

        if (userrole == 1) {
            btnEdit.setVisibility(View.GONE);
        }
    }
}