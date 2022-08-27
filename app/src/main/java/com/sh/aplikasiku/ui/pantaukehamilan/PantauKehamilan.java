package com.sh.aplikasiku.ui.pantaukehamilan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.adapter.AdminAdapterPantau;
import com.sh.aplikasiku.adapter.UserAdapterPantau;
import com.sh.aplikasiku.model.UserPantau;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

public class PantauKehamilan extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<UserPantau> list = new ArrayList<>();
    private UserAdapterPantau userAdapterPantau;
    private AdminAdapterPantau adminAdapterPantau;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPref;
    private int userrole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantau_kehamilan);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get userdata
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);
        userrole = sharedPref.getInt(getString(R.string.user_role), 0);
        String username = sharedPref.getString(getString(R.string.user_name), "");

        recyclerView = findViewById(R.id.recyclerview);
        btnAdd = findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditPantau.class);
            intent.putExtra("option", "add");
            startActivity(intent);
        });

        progressDialog = new ProgressDialog(PantauKehamilan.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("Mengambil data");

        if (userrole == 1) {
            btnAdd.setVisibility(View.VISIBLE);
            adminAdapterPantau = new AdminAdapterPantau(this, list);
            adminAdapterPantau.setDialog(pos -> {
                final CharSequence[] dialogItem = {"lihat", "edit", "hapus"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(PantauKehamilan.this);
                dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent intentbaca = new Intent(getApplicationContext(), TampilPantauKehamilan.class);
                                intentbaca.putExtra("id", list.get(pos).getId());
                                intentbaca.putExtra("denyutjantung", list.get(pos).getDenyut());
                                intentbaca.putExtra("kondisibayi", list.get(pos).getKondisi());
                                startActivity(intentbaca);
                                break;
                            case 1:
                                Intent intent = new Intent(getApplicationContext(), EditPantau.class);
                                intent.putExtra("id", list.get(pos).getId());
                                intent.putExtra("denyutjantung", list.get(pos).getDenyut());
                                intent.putExtra("kondisibayi", list.get(pos).getKondisi());
                                startActivity(intent);
                                break;
                            case 2:
                                deleteData(list.get(pos).getId());
                                break;
                        }
                    }
                });
                dialog.show();
            });
        } else {
            btnAdd.setVisibility(View.GONE);
            userAdapterPantau = new UserAdapterPantau(this, list);
        }
    }

    //untuk menampilkan getdata alias data yang telah diubah maupun ditambah
    @Override
    protected void onStart() {
        super.onStart();
        getData();
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

    private void getData() {
        progressDialog.show();
        db.collection("pantau")
                .get()
                .addOnCompleteListener(task -> {
                    list.clear();
                    if (task.isSuccessful()) {
                        try {
                            Log.d("QWE", "getData: " + task.getResult().getDocuments());
                            if (task.getResult().getDocuments().size() == 0) {
                                Toast.makeText(PantauKehamilan.this, "Belum ada data pantau kehamilan!", Toast.LENGTH_SHORT).show();
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String denyutjantung = document.get("denyutjantung").toString();
                                    String kondisibayi = document.get("kondisibayi").toString();
                                    UserPantau user = new UserPantau(denyutjantung, kondisibayi);
                                    user.setId(document.getId());
                                    list.add(user);
                                }
                                showPantauKehamilan();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Data Gagal", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                });
    }

    private void deleteData(String id) {
        progressDialog.show();
        db.collection("pantau").document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Data Gagal Dihapus", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        getData();
                    }
                });
    }

    private void showPantauKehamilan() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);

        if (userrole == 1) {
            recyclerView.setAdapter(adminAdapterPantau);
        } else {
            recyclerView.setAdapter(userAdapterPantau);
        }
    }

}