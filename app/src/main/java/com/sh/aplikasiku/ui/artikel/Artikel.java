package com.sh.aplikasiku.ui.artikel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.adapter.AdminAdapterArtikel;
import com.sh.aplikasiku.adapter.UserAdapterArtikel;
import com.sh.aplikasiku.model.UserArtikel;

import java.util.ArrayList;
import java.util.List;

public class Artikel extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<UserArtikel> list = new ArrayList<>();
    private UserAdapterArtikel userAdapterArtikel;
    private AdminAdapterArtikel adminAdapterArtikel;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPref;
    private int userrole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Artikel");

        //get userrole
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);
        userrole = sharedPref.getInt(getString(R.string.user_role), 0);

        recyclerView = findViewById(R.id.recyclerview);
        btnAdd = findViewById(R.id.btn_add);

        progressDialog = new ProgressDialog(Artikel.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("Mengambil data");

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditArtikel.class);
            intent.putExtra("option", "add");
            startActivity(intent);
        });

        if (userrole == 1) {
            adminAdapterArtikel = new AdminAdapterArtikel(this, list);
            adminAdapterArtikel.setDialog(pos -> {
                final CharSequence[] dialogItem = {"Detail","Edit", "Hapus"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(Artikel.this);
                dialog.setItems(dialogItem, (dialogInterface, i) -> {
                    switch(i){
                        case 0:
                            Intent intentbaca = new Intent(getApplicationContext(), TampilArtikel.class);
                            intentbaca.putExtra("id", list.get(pos).getId());
                            intentbaca.putExtra("judul", list.get(pos).getJudul());
                            intentbaca.putExtra("penjelasan", list.get(pos).getPenjelasan());
                            intentbaca.putExtra("avatar", list.get(pos).getAvatar());
                            intentbaca.putExtra("dateCreated", list.get(pos).getDateCreated());
                            intentbaca.putExtra("dateUpdated", list.get(pos).getDateUpdated());
                            startActivity(intentbaca);
                            break;
                        case 1:
                            Intent intent = new Intent(getApplicationContext(), EditArtikel.class);
                            intent.putExtra("id", list.get(pos).getId());
                            intent.putExtra("judul", list.get(pos).getJudul());
                            intent.putExtra("penjelasan", list.get(pos).getPenjelasan());
                            intent.putExtra("avatar", list.get(pos).getAvatar());
                            intent.putExtra("dateCreated", list.get(pos).getDateCreated());
                            intent.putExtra("dateUpdated", list.get(pos).getDateUpdated());
                            intent.putExtra("option", "edit");
                            startActivity(intent);
                            break;
                        case 2:
                            deleteData(list.get(pos).getId(), list.get(pos).getAvatar());
                            break;
                    }
                });
                dialog.show();
            });
        } else {
            btnAdd.setVisibility(View.GONE);
            userAdapterArtikel = new UserAdapterArtikel(this, list);
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

    private void getData(){
        progressDialog.show();

        db.collection("artikel")
                .get()
                .addOnCompleteListener(task -> {
                    list.clear();
                    if (task.isSuccessful()) {
                        try {
                            if (task.getResult().getDocuments().size() == 0) {
                                Toast.makeText(Artikel.this, "Belum ada artikel!", Toast.LENGTH_SHORT).show();
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String id = document.getId();
                                    String judul = document.get("judul").toString();
                                    String penjelasan = document.get("penjelasan").toString();
                                    String avatar = document.get("avatar").toString();
                                    String dateCreated = document.get("dateCreated").toString();
                                    String dateUpdated = document.get("dateUpdated").toString();
                                    UserArtikel userArtikel = new UserArtikel(
                                            id, judul, penjelasan, avatar, dateCreated, dateUpdated
                                    );
                                    userArtikel.setId(document.getId());
                                    list.add(userArtikel);
                                }
                                showArtikel();
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

    private void deleteData(String id, String avatar){
        progressDialog.show();
        db.collection("artikel").document(id)
                .delete()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Data Gagal Dihapus", Toast.LENGTH_SHORT).show();
                    }else{
                        FirebaseStorage.getInstance().getReferenceFromUrl(avatar).delete().addOnCompleteListener(task1 -> {
                            progressDialog.dismiss();
                            getData();
                        });
                    }

                });
    }

    private void showArtikel() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        if (userrole == 1) {
            recyclerView.setAdapter(adminAdapterArtikel);
        } else {
            recyclerView.setAdapter(userAdapterArtikel);
        }
    }
}