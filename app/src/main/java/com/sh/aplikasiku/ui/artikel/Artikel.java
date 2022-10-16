package com.sh.aplikasiku.ui.artikel;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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
import com.sh.aplikasiku.model.UserPantau;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Artikel extends AppCompatActivity {

    //inisiasi variabel baru dan komponen penampung
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<UserArtikel> list = new ArrayList<>();
    private UserAdapterArtikel userAdapterArtikel;
    private AdminAdapterArtikel adminAdapterArtikel;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPref;
    private int userrole;

    //membuat fungsi activity result untuk mendapatkan feedback result dari intent
    ActivityResultLauncher<Intent> getCreateEditResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    getData();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel);

        //mengubah title di toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Artikel");

        //mendapatkan sharedpreferences berdasarkan key "data_user"
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);
        //mendapatkan userrole dari sharedpreferences
        userrole = sharedPref.getInt(getString(R.string.user_role), 0);

        //menyambungkan komponen dengan xml
        recyclerView = findViewById(R.id.recyclerview);
        btnAdd = findViewById(R.id.btn_add);

        //membuat komponen progressdialog
        progressDialog = new ProgressDialog(Artikel.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("Mengambil data");

        //menangani ketika tombol floating tambah di tekan
        btnAdd.setOnClickListener(v -> {
            //intent ke halaman edit artikel dengan data option add
            Intent intent = new Intent(getApplicationContext(), EditArtikel.class);
            intent.putExtra("option", "add");
            //menjalankan intent dengan activity result
            getCreateEditResult.launch(intent);
        });

        //cek userrole apakah 1(admin) atau 2(user biasa)
        if (userrole == 1) {
            //jika role 1 atau admin
            //inisiasi adapter artikel admin
            adminAdapterArtikel = new AdminAdapterArtikel(this, list);

            //membuat fungsi ketika item pada adapter ditekan akan memunculkan dialog
            adminAdapterArtikel.setDialog(pos -> {
                //membuat array untuk menjadi menu di dialog
                final CharSequence[] dialogItem = {"Detail","Edit", "Hapus"};

                //membuat dialog kosong
                AlertDialog.Builder dialog = new AlertDialog.Builder(Artikel.this);

                //menambahkan array menu ke dialog kosong
                dialog.setItems(dialogItem, (dialogInterface, i) -> {
                    //menagani ketika menu pada dialog di klik
                    switch(i){
                        //jika kasus 0 atau Detail
                        case 0:
                            //intent ke halaman tampil artikel dengan mengirim data artikel yang dipilih
                            Intent intentbaca = new Intent(getApplicationContext(), TampilArtikel.class);
                            intentbaca.putExtra("id", list.get(pos).getId());
                            intentbaca.putExtra("judul", list.get(pos).getJudul());
                            intentbaca.putExtra("penjelasan", list.get(pos).getPenjelasan());
                            intentbaca.putExtra("avatar", list.get(pos).getAvatar());
                            intentbaca.putExtra("dateCreated", list.get(pos).getDateCreated());
                            intentbaca.putExtra("dateUpdated", list.get(pos).getDateUpdated());
                            startActivity(intentbaca);
                            break;
                        //jika kasus 1 atau Edit
                        case 1:
                            //intent ke halaman edit artikel dengan data option edit
                            Intent intent = new Intent(getApplicationContext(), EditArtikel.class);
                            intent.putExtra("id", list.get(pos).getId());
                            intent.putExtra("judul", list.get(pos).getJudul());
                            intent.putExtra("penjelasan", list.get(pos).getPenjelasan());
                            intent.putExtra("avatar", list.get(pos).getAvatar());
                            intent.putExtra("dateCreated", list.get(pos).getDateCreated());
                            intent.putExtra("dateUpdated", list.get(pos).getDateUpdated());
                            intent.putExtra("option", "edit");
                            getCreateEditResult.launch(intent);
                            break;
                        //jika kasus 2 atau Hapus
                        case 2:
                            //menghapus data artikel yang di pilih
                            deleteData(list.get(pos).getId(), list.get(pos).getAvatar());
                            break;
                    }
                });
                //menampilkan dialog
                dialog.show();
            });
        } else {
            //jika role 2 (user biasa), menyembunyikan tombol floating tambah dan menginisiasi adapter artikel user
            btnAdd.setVisibility(View.GONE);
            userAdapterArtikel = new UserAdapterArtikel(this, list);
        }

        //menanggil fungsi getData()
        getData();
    }

    //untuk menampilkan getdata alias data yang telah diubah maupun ditambah
    @Override
    protected void onStart() {
        super.onStart();
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

    //fungsi untuk memanggil data artikel di firebase
    private void getData(){
        //tampilkan progressdialog
        progressDialog.show();

        //kosong kan list agar tidak menumpuk ketika diisi ulang
        list.clear();

        //memanggil data artikel pada firebase
        db.collection("artikel")
                .get()
                .addOnCompleteListener(task -> {
                    list.clear();
                    //cek apakah berhasil atau tidak
                    if (task.isSuccessful()) {
                        //coba mengecek dan menggunakan data
                        try {
                            //cek apakah data tidak kosong
                            if (task.getResult().getDocuments().size() == 0) {
                                //jika iya, menampilkan toast peringatan
                                Toast.makeText(Artikel.this, "Belum ada artikel!", Toast.LENGTH_SHORT).show();
                            } else {
                                //jika tidak, melakukan perulangan pada data result
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //memecah data ke masing-masing variabel yang sesuai
                                    String id = document.getId();
                                    String judul = document.get("judul").toString();
                                    String penjelasan = document.get("penjelasan").toString();
                                    String avatar = document.get("avatar").toString();
                                    String dateCreated = document.get("dateCreated").toString();
                                    String dateUpdated = document.get("dateUpdated").toString();

                                    //memasukkan pecahal variabel ke variabel UserArtikel
                                    UserArtikel userArtikel = new UserArtikel(
                                            id, judul, penjelasan, avatar, dateCreated, dateUpdated
                                    );
                                    userArtikel.setId(document.getId());
                                    //menambahkan variabel user ke vairabel list
                                    list.add(userArtikel);
                                }
                                //memanggil fungsi sortData()
                                sortData();
                                //kemudian memanggil fungsi showArtikel()
                                showArtikel();
                            }
                        } catch (Exception e) {
                            //jika percobaan gagal, menampilkan toast peringatan
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

    //fungsi untuk menghapus data artikel berdasarkan id
    private void deleteData(String id, String avatar){
        progressDialog.show();
        //menghapus data artikel di firebase berdasarkan id pada parameter
        db.collection("artikel").document(id)
                .delete()
                .addOnCompleteListener(task -> {
                    //cek apakah berhasil atau tidak
                    if(!task.isSuccessful()){
                        //jika gagal, tutup progressdialog dan tampilkan toast peringatan
                        progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Data Gagal Dihapus", Toast.LENGTH_SHORT).show();
                    }else{
                        //jika berhasil, hapus juga gambar artikel dan panggil ulang data pada firebase
                        FirebaseStorage.getInstance().getReferenceFromUrl(avatar).delete().addOnCompleteListener(task1 -> {
                            progressDialog.dismiss();
                            getData();
                        });
                    }

                });
    }

    //fungsi untuk menampilkan artikel berdasarkan role
    private void showArtikel() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        //cek role apakah 1 atau bukan
        if (userrole == 1) {
            //jika 1 pakai adapter artikel admin
            recyclerView.setAdapter(adminAdapterArtikel);
        } else {
            //jika bukan pakai adapter artikel user
            recyclerView.setAdapter(userAdapterArtikel);
        }
    }

    //fungsi untuk mengurutkan data berdasarkan tanggal yang baru terbuat di bagian paling atas
    private void sortData() {
        int n = list.size();

        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n-i); j++) {
                //create date format pattern
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date strDate1;
                Date strDate2;
                try {
                    //initialize date 1 and 2
                    strDate1 = sdf.parse(list.get(j-1).getDateCreated());
                    strDate2 = sdf.parse(list.get(j).getDateCreated());

                    //compare date 1 and 2
                    if (strDate2.after(strDate1)) {
                        UserArtikel temp = list.get(j-1);
                        list.set(j-1, list.get(j));
                        list.set(j, temp);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}