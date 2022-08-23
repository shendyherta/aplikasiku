package com.sh.aplikasiku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sh.aplikasiku.adapter.UserAdapterRekam;
import com.sh.aplikasiku.model.UserRekam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;

public class RekamMedis extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<UserRekam> list = new ArrayList<>();
    private UserAdapterRekam userAdapterRekam;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekam_medis);
        recyclerView = findViewById(R.id.recyclerview);
        btnAdd = findViewById(R.id.btn_add);

        progressDialog = new ProgressDialog(RekamMedis.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("Mengambil data");

        userAdapterRekam = new UserAdapterRekam(getApplicationContext(), list);
        userAdapterRekam.setDialog(new UserAdapterRekam.Dialog() {
            @Override
            public void onClick(int pos) {
                final CharSequence[] dialogItem = {"tampil", "edit", "hapus"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(RekamMedis.this);
                dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent intentbaca = new Intent(getApplicationContext(), TampilanRekam.class);
                                intentbaca.putExtra("id", list.get(pos).getId());
                                intentbaca.putExtra("berat", list.get(pos).getBerat());
                                intentbaca.putExtra("lingkar", list.get(pos).getLingkar());
                                intentbaca.putExtra("laju", list.get(pos).getLaju());
                                intentbaca.putExtra("tekanan", list.get(pos).getTekanan());
                                intentbaca.putExtra("suhu", list.get(pos).getSuhu());
                                intentbaca.putExtra("denyut", list.get(pos).getDenyut());
                                intentbaca.putExtra("kondisi", list.get(pos).getKondisi());
                                startActivity(intentbaca);
                                break;
                            case 1:
                                Intent intent = new Intent(getApplicationContext(), EditRekam.class);
                                intent.putExtra("id", list.get(pos).getId());
                                intent.putExtra("berat", list.get(pos).getBerat());
                                intent.putExtra("lingkar", list.get(pos).getLingkar());
                                intent.putExtra("laju", list.get(pos).getLaju());
                                intent.putExtra("tekanan", list.get(pos).getTekanan());
                                intent.putExtra("suhu", list.get(pos).getSuhu());
                                intent.putExtra("denyut", list.get(pos).getDenyut());
                                intent.putExtra("kondisi", list.get(pos).getKondisi());
                                startActivity(intent);
                                break;
                            case 2:
                                deleteData(list.get(pos).getId());
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });


        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), EditRekam.class));
        });

    }

    //untuk menampilkan getdata alias data yang telah diubah maupun ditambah
    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    private void getData() {
        progressDialog.show();
        db.collection("rekammedis")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        if (task.isSuccessful()) {
                            try {
                                if (task.getResult().getDocuments().size() == 0) {
                                    Toast.makeText(RekamMedis.this, "Belum ada data rekam medis!", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String berat = document.get("berat").toString();
                                        String denyut = document.get("denyut").toString();
                                        String laju = document.get("laju").toString();
                                        String suhu = document.get("suhu").toString();
                                        String tekanan = document.get("tekanan").toString();
                                        String kondisi = document.get("kondisi").toString();
                                        String lingkar = document.get("lingkar").toString();
                                        UserRekam user = new UserRekam(berat, denyut, laju, suhu, tekanan, kondisi, lingkar);
                                        user.setId(document.getId());
                                        list.add(user);
                                    }
                                    showRekamMedis();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Data Gagal", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void deleteData(String id) {
        progressDialog.show();
        db.collection("rekammedis").document(id)
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

    private void showRekamMedis() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(userAdapterRekam);
    }

}