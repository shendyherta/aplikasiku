package com.sh.aplikasiku.ui.pantaukehamilan;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.adapter.AdminAdapterPantau;
import com.sh.aplikasiku.adapter.ClaimsXAxisValueFormatter;
import com.sh.aplikasiku.adapter.UserAdapterPantau;
import com.sh.aplikasiku.model.UserPantau;
import com.sh.aplikasiku.model.UserRekam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;

public class PantauKehamilan extends AppCompatActivity {

    //inisiasi variabel baru dan komponen penampung
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private LineChart lineChart;
    private TextView tvTitle;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<UserPantau> list = new ArrayList<>();
    private List<Entry> pantauEntries = new ArrayList<>();
    private List<String> listLabels = new ArrayList<>();
    private UserAdapterPantau userAdapterPantau;
    private AdminAdapterPantau adminAdapterPantau;
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
        setContentView(R.layout.activity_pantau_kehamilan);

        //mengubah title di toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pantau Kehamilan");

        //mendapatkan sharedpreferences berdasarkan key "data_user"
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);
        //mendapatkan userrole dan username dari sharedpreferences
        userrole = sharedPref.getInt(getString(R.string.user_role), 0);
        String username = sharedPref.getString(getString(R.string.user_name), "");

        //menyambungkan komponen dengan xml
        recyclerView = findViewById(R.id.recyclerview);
        btnAdd = findViewById(R.id.btn_add);
        lineChart = findViewById(R.id.line_chart);
        tvTitle = findViewById(R.id.tv_title);

        //membuat komponen progressdialog
        progressDialog = new ProgressDialog(PantauKehamilan.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("Mengambil data");

        //menangani ketika tombol floating tambah di tekan
        btnAdd.setOnClickListener(v -> {
            //intent ke halaman edit pantau kehamilan dengan data option add
            Intent intent = new Intent(getApplicationContext(), EditPantau.class);
            intent.putExtra("option", "add");
            //menjalankan intent dengan activity result
            getCreateEditResult.launch(intent);
        });

        //cek userrole apakah 1(admin) atau 2(user biasa)
        if (userrole == 1) {
            //jika role 1 atau admin

            //menampilkan tombol floating tambah
            btnAdd.setVisibility(View.VISIBLE);

            //inisiasi adapter artikel admin
            adminAdapterPantau = new AdminAdapterPantau(this, list);

            //membuat fungsi ketika item pada adapter ditekan akan memunculkan dialog
            adminAdapterPantau.setDialog(pos -> {
                //membuat array untuk menjadi menu di dialog
                final CharSequence[] dialogItem = {"Detail", "Edit", "Hapus"};

                //membuat dialog kosong
                AlertDialog.Builder dialog = new AlertDialog.Builder(PantauKehamilan.this);

                //menambahkan array menu ke dialog kosong
                dialog.setItems(dialogItem, (dialogInterface, i) -> {
                    //menagani ketika menu pada dialog di klik
                    switch (i) {
                        //jika kasus 0 atau Detail
                        case 0:
                            //intent ke halaman tampil pantau dengan mengirim data pantau yang dipilih
                            Intent intentbaca = new Intent(getApplicationContext(), TampilPantauKehamilan.class);
                            intentbaca.putExtra("id", list.get(pos).getId());
                            intentbaca.putExtra("idUser", list.get(pos).getIdUser());
                            intentbaca.putExtra("pasien", list.get(pos).getPasien());
                            intentbaca.putExtra("denyutjantung", list.get(pos).getDenyut());
                            intentbaca.putExtra("kondisibayi", list.get(pos).getKondisi());
                            intentbaca.putExtra("rujukan", list.get(pos).getRujukan());
                            intentbaca.putExtra("dateCreated", list.get(pos).getDateCreated());
                            intentbaca.putExtra("dateUpdated", list.get(pos).getDateUpdated());
                            startActivity(intentbaca);
                            break;
                        //jika kasus 1 atau Edit
                        case 1:
                            //intent ke halaman edit pantau dengan data option edit
                            Intent intent = new Intent(getApplicationContext(), EditPantau.class);
                            intent.putExtra("id", list.get(pos).getId());
                            intent.putExtra("idUser", list.get(pos).getIdUser());
                            intent.putExtra("pasien", list.get(pos).getPasien());
                            intent.putExtra("denyutjantung", list.get(pos).getDenyut());
                            intent.putExtra("kondisibayi", list.get(pos).getKondisi());
                            intent.putExtra("rujukan", list.get(pos).getRujukan());
                            intent.putExtra("dateCreated", list.get(pos).getDateCreated());
                            intent.putExtra("dateUpdated", list.get(pos).getDateUpdated());
                            intent.putExtra("option", "edit");
                            getCreateEditResult.launch(intent);
                            break;
                        //jika kasus 2 atau Hapus
                        case 2:
                            //menghapus data pantau yang di pilih
                            deleteData(list.get(pos).getId());
                            break;
                    }
                });
                //menampilkan dialog
                dialog.show();
            });
        } else {
            //jika role 2 (user biasa), menyembunyikan tombol floating tambah dan menginisiasi adapter artikel user
            btnAdd.setVisibility(View.GONE);
            userAdapterPantau = new UserAdapterPantau(this, list);
            tvTitle.setVisibility(View.GONE);
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

    //fungsi untuk memanggil data pantau di firebase
    private void getData() {
        //tampilkan progressdialog
        progressDialog.show();

        //kosong kan list agar tidak menumpuk ketika diisi ulang
        list.clear();

        //memanggil data pantau pada firebase
        //cek apakah role 1 (admin) atau 2 (user biasa)
        if (userrole == 1) {
            //jika role 1, ambil semua data
            db.collection("pantau")
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
                                    Toast.makeText(PantauKehamilan.this, "Belum ada data pantau kehamilan!", Toast.LENGTH_SHORT).show();
                                } else {
                                    //jika tidak, melakukan perulangan pada data result
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //memecah data ke masing-masing variabel yang sesuai
                                        String id = document.getId();
                                        String idUser = document.get("idPasien").toString();
                                        String pasien = document.get("pasien").toString();
                                        String denyutjantung = document.get("denyutjantung").toString();
                                        String kondisibayi = document.get("kondisibayi").toString();
                                        String rujukan = document.get("rujukan").toString();
                                        String dateCreated = document.get("dateCreated").toString();
                                        String dateUpdated = document.get("dateUpdated").toString();

                                        //memasukkan pecahal variabel ke variabel UserPantau
                                        UserPantau user = new UserPantau(id, idUser, pasien,
                                                denyutjantung, kondisibayi, rujukan, dateCreated, dateUpdated);
                                        user.setId(document.getId());
                                        //menambahkan variabel user ke vairabel list
                                        list.add(user);
                                    }
                                    //memanggil fungsi sortData()
                                    sortData();
                                    //kemudian memanggil fungsi showPantauKehamilan()
                                    showPantauKehamilan();
                                    //karena admin tidak butuh chart, maka disembunyikan
                                    lineChart.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                //jika percobaan gagal, menampilkan toast peringatan
                                Log.d("GETDATAPANTAU", "getData: " + e.getMessage());
                                Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //jika percobaan gagal, menampilkan toast peringatan
                            Toast.makeText(getApplicationContext(), "Data Gagal", Toast.LENGTH_SHORT).show();
                        }
                        //menutup progressdialog
                        progressDialog.dismiss();
                    });
        } else {
            //jika role 2, ambil data yang memiliki id user yang sama dengan user yang login

            //inisiasi firebaseuser untuk mendapatkan data user yang login
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            //mendapatkan data pantau dengan filter id user yang sedang login
            db.collection("pantau")
                    .whereEqualTo("idPasien", firebaseUser.getUid())
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
                                    Toast.makeText(PantauKehamilan.this, "Belum ada data pantau kehamilan!", Toast.LENGTH_SHORT).show();
                                } else {
                                    //jika tidak, melakukan perulangan pada data result
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //memecah data ke masing-masing variabel yang sesuai
                                        String id = document.getId();
                                        String idUser = document.get("idPasien").toString();
                                        String pasien = document.get("pasien").toString();
                                        String denyutjantung = document.get("denyutjantung").toString();
                                        String kondisibayi = document.get("kondisibayi").toString();
                                        String rujukan = document.get("rujukan").toString();
                                        String dateCreated = document.get("dateCreated").toString();
                                        String dateUpdated = document.get("dateUpdated").toString();
                                        //memasukkan pecahal variabel ke variabel UserPantau
                                        UserPantau user = new UserPantau(id, idUser, pasien,
                                                denyutjantung, kondisibayi, rujukan, dateCreated, dateUpdated);
                                        user.setId(document.getId());
                                        //menambahkan variabel user ke vairabel list
                                        list.add(user);
                                    }
                                    //memanggil fungsi sortData()
                                    sortData();
                                    //memanggil fungsi showPantauKehamilan()
                                    showPantauKehamilan();
                                    //memanggil fungsi setPantauEntries() untuk chart
                                    setPantauEntries();
                                }
                            } catch (Exception e) {
                                //jika percobaan gagal, menampilkan toast peringatan
                                Log.d("GETDATAPANTAU", "getData: " + e.getMessage());
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
    }

    //fungsi untuk menghapus data pantau berdasarkan id
    private void deleteData(String id) {
        progressDialog.show();
        //menghapus data pantau di firebase berdasarkan id pada parameter
        db.collection("pantau").document(id)
                .delete()
                .addOnCompleteListener(task -> {
                    //cek apakah berhasil atau tidak
                    if (!task.isSuccessful()) {
                        //jika gagal tampilkan toast peringatan
                        Toast.makeText(getApplicationContext(), "Data Gagal Dihapus", Toast.LENGTH_SHORT).show();
                    }
                    //tutup progressdialog
                    progressDialog.dismiss();

                    //panggil fungsi getData()
                    getData();
                });
    }

    //fungsi untuk menampilkan pantau berdasarkan role
    private void showPantauKehamilan() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        //cek role apakah 1 atau bukan
        if (userrole == 1) {
            //jika 1 pakai adapter pantau admin
            recyclerView.setAdapter(adminAdapterPantau);
        } else {
            //jika bukan pakai adapter pantau user
            recyclerView.setAdapter(userAdapterPantau);
        }
    }

    //fungsi untuk membuat label chart berdasarkan tanggal data pantau dibuat
    private void setPantauEntries() {
        for(int i = 0; i < list.size(); i++) {
            pantauEntries.add(new Entry(i+1, Integer.parseInt(list.get(i).getDenyut())));
            listLabels.add(list.get(i).getDateCreated());
        }
        listLabels.add("");
        //memanggil fungsi setEntriesToChart() untuk membuat chart
        setEntriesToChart();
    }

    //fungsi untuk membuat chart berdasarkan data list pantau
    private void setEntriesToChart() {
        //set chart data
        LineDataSet dataSet = new LineDataSet(pantauEntries, "Denyut jantung");
        dataSet.setColor(getResources().getColor(R.color.pink));
        dataSet.setLineWidth(2f);

        //inisiasi dataset berdasarkan chart data diatas
        List<ILineDataSet> datasets = new ArrayList<>();
        datasets.add(dataSet);

        //set limit bawah chart
        LimitLine minLine = new LimitLine(100f, "Batas normal bawah");
        minLine.setLineColor(Color.GREEN);
        minLine.setLineWidth(2f);

        //set limit atas chart
        LimitLine maxLine = new LimitLine(120f, "Batas normal atas");
        maxLine.setLineColor(Color.GREEN);
        maxLine.setLineWidth(2f);

        //set chart limit y axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(minLine);
        leftAxis.addLimitLine(maxLine);
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.setAxisMinimum(70f);
        leftAxis.setAxisMaximum(130f);

        //set chart description
        lineChart.getDescription().setEnabled(true);
        Description description = new Description();
        description.setText("Waktu Cek");
        description.setTextSize(10f);
        lineChart.setDescription(description);

        //set chart label x axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(list.size()+1);
        xAxis.setLabelCount(list.size()+1, true);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(7f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ClaimsXAxisValueFormatter(listLabels));

        //set data to chart
        LineData lineData = new LineData(datasets);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setData(lineData);
        lineChart.setPinchZoom(false);
        lineChart.invalidate();
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
                        UserPantau temp = list.get(j-1);
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