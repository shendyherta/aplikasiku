package com.sh.aplikasiku.ui.rekammedis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.adapter.AdminAdapterRekam;
import com.sh.aplikasiku.adapter.ClaimsXAxisValueFormatter;
import com.sh.aplikasiku.adapter.UserAdapterRekam;
import com.sh.aplikasiku.model.UserRekam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RekamMedis extends AppCompatActivity {

    //inisiasi variabel baru dan komponen penampung
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private LineChart lineChart;
    private TextView tvTitle;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<UserRekam> list = new ArrayList<>();
    private UserAdapterRekam userAdapterRekam;
    private AdminAdapterRekam adminAdapterRekam;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPref;
    private int userrole;
    private List<Entry> denyutEntries = new ArrayList<Entry>();
    private List<Entry> beratEntries = new ArrayList<Entry>();
    private List<Entry> lingkarEntries = new ArrayList<Entry>();
    private List<Entry> kondisihbEntries = new ArrayList<Entry>();
    private List<Entry> lajuEntries = new ArrayList<Entry>();
    private List<Entry> suhuEntries = new ArrayList<Entry>();
    private List<Entry> tekananEntries = new ArrayList<Entry>();
    private List<String> listLabels = new ArrayList<>();

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
        setContentView(R.layout.activity_rekam_medis);

        //mengubah title di toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rekam Medis");

        //mendapatkan sharedpreferences berdasarkan key "data_user"
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);

        //mendapatkan userrole dari sharedpreferences
        userrole = sharedPref.getInt(getString(R.string.user_role), 0);

        //menyambungkan komponen dengan xml
        recyclerView = findViewById(R.id.recyclerview);
        btnAdd = findViewById(R.id.btn_add);
        lineChart = findViewById(R.id.line_chart);
        tvTitle = findViewById(R.id.tv_title);

        //membuat komponen progressdialog
        progressDialog = new ProgressDialog(RekamMedis.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("Mengambil data");

        //menangani ketika tombol floating tambah di tekan
        btnAdd.setOnClickListener(v -> {
            //intent ke halaman edit rekam medis dengan data option add
            Intent intent = new Intent(getApplicationContext(), EditRekam.class);
            intent.putExtra("option", "add");
            //menjalankan intent dengan activity result
            getCreateEditResult.launch(intent);
        });

        //cek userrole apakah 1(admin) atau 2(user biasa)
        if (userrole == 1) {
            //jika role 1 atau admin

            //menampilkan tombol floating tambah
            btnAdd.setVisibility(View.VISIBLE);

            //inisiasi adapter rekam admin
            adminAdapterRekam = new AdminAdapterRekam(this, list);

            //membuat fungsi ketika item pada adapter ditekan akan memunculkan dialog
            adminAdapterRekam.setDialog(pos -> {
                //membuat array untuk menjadi menu di dialog
                final CharSequence[] dialogItem = {"Detail", "Edit", "Hapus"};

                //membuat dialog kosong
                AlertDialog.Builder dialog = new AlertDialog.Builder(RekamMedis.this);

                //menambahkan array menu ke dialog kosong
                dialog.setItems(dialogItem, (dialogInterface, i) -> {
                    //menagani ketika menu pada dialog di klik
                    switch (i) {
                        //jika kasus 0 atau Detail
                        case 0:
                            //intent ke halaman tampil rekam dengan mengirim data rekam yang dipilih
                            Intent intentbaca = new Intent(getApplicationContext(), TampilanRekam.class);
                            intentbaca.putExtra("id", list.get(pos).getId());
                            intentbaca.putExtra("idUser", list.get(pos).getIdUser());
                            intentbaca.putExtra("pasien", list.get(pos).getPasien());
                            intentbaca.putExtra("berat", list.get(pos).getBeratBadan());
                            intentbaca.putExtra("lingkar", list.get(pos).getLingkarBadan());
                            intentbaca.putExtra("laju", list.get(pos).getLajuPernafasan());
                            intentbaca.putExtra("tekanan", list.get(pos).getTekananDarah());
                            intentbaca.putExtra("suhu", list.get(pos).getSuhu());
                            intentbaca.putExtra("denyut", list.get(pos).getDenyutJantung());
                            intentbaca.putExtra("kondisi", list.get(pos).getKondisiHB());
                            intentbaca.putExtra("rujukan", list.get(pos).getRujukan());
                            intentbaca.putExtra("dateCreated", list.get(pos).getDateCreated());
                            intentbaca.putExtra("dateUpdated", list.get(pos).getDateUpdated());
                            startActivity(intentbaca);
                            break;
                        //jika kasus 1 atau Edit
                        case 1:
                            //intent ke halaman edit rekam dengan data option edit
                            Intent intent = new Intent(getApplicationContext(), EditRekam.class);
                            intent.putExtra("id", list.get(pos).getId());
                            intent.putExtra("idUser", list.get(pos).getIdUser());
                            intent.putExtra("berat", list.get(pos).getBeratBadan());
                            intent.putExtra("lingkar", list.get(pos).getLingkarBadan());
                            intent.putExtra("laju", list.get(pos).getLajuPernafasan());
                            intent.putExtra("tekanan", list.get(pos).getTekananDarah());
                            intent.putExtra("suhu", list.get(pos).getSuhu());
                            intent.putExtra("denyut", list.get(pos).getDenyutJantung());
                            intent.putExtra("kondisi", list.get(pos).getKondisiHB());
                            intent.putExtra("rujukan", list.get(pos).getRujukan());
                            intent.putExtra("dateCreated", list.get(pos).getDateCreated());
                            intent.putExtra("dateUpdated", list.get(pos).getDateUpdated());
                            intent.putExtra("option", "edit");
                            getCreateEditResult.launch(intent);
                            break;
                        //jika kasus 2 atau Hapus
                        case 2:
                            //menghapus data rekam yang di pilih
                            deleteData(list.get(pos).getId());
                            break;
                    }
                });
                //menampilkan dialog
                dialog.show();
            });
        } else {
            //jika role 2 (user biasa), menyembunyikan tombol floating tambah dan menginisiasi adapter rekam user
            btnAdd.setVisibility(View.GONE);
            userAdapterRekam = new UserAdapterRekam(this, list);
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

    //fungsi untuk memanggil data rekam di firebase
    private void getData() {
        //tampilkan progressdialog
        progressDialog.show();

        //memanggil data rekam pada firebase
        //cek apakah role 1 (admin) atau 2 (user biasa)
        if (userrole == 1) {
            //jika role 1, ambil semua data
            db.collection("rekammedis")
                    .get()
                    .addOnCompleteListener(task -> {
                        //kosong kan list agar tidak menumpuk ketika diisi ulang
                        list.clear();
                        //cek apakah berhasil atau tidak
                        if (task.isSuccessful()) {
                            //coba mengecek dan menggunakan data
                            try {
                                //cek apakah data tidak kosong
                                if (task.getResult().getDocuments().size() == 0) {
                                    //jika iya, menampilkan toast peringatan
                                    Toast.makeText(RekamMedis.this, "Belum ada data rekam medis!", Toast.LENGTH_SHORT).show();
                                } else {
                                    //jika tidak, melakukan perulangan pada data result
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //memecah data ke masing-masing variabel yang sesuai
                                        String id = document.getId();
                                        String idUser = document.get("idPasien").toString();
                                        String pasien = document.get("pasien").toString();
                                        String berat = document.get("berat").toString();
                                        String denyut = document.get("denyut").toString();
                                        String laju = document.get("laju").toString();
                                        String suhu = document.get("suhu").toString();
                                        String tekanan = document.get("tekanan").toString();
                                        String kondisi = document.get("kondisi").toString();
                                        String lingkar = document.get("lingkar").toString();
                                        String rujukan = document.get("rujukan").toString();
                                        String dateCreated = document.get("dateCreated").toString();
                                        String dateUpdated = document.get("dateUpdated").toString();

                                        //memasukkan pecahal variabel ke variabel UserRekam
                                        UserRekam user = new UserRekam(id, idUser, pasien, berat, lingkar, kondisi, tekanan, laju, suhu, denyut, rujukan, dateCreated, dateUpdated);
                                        user.setId(document.getId());
                                        //menambahkan variabel user ke vairabel list
                                        list.add(user);
                                    }
                                    //memanggil fungsi sortData()
                                    sortData();
                                    //kemudian memanggil fungsi showRekamMedis()
                                    showRekamMedis();
                                    //karena admin tidak butuh chart, maka disembunyikan
                                    lineChart.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                //jika percobaan gagal, menampilkan toast peringatan
                                Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                                Log.d("REKAMMEDISGETDATA", "getData: " + e.getMessage());
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

            //mendapatkan data rekam dengan filter id user yang sedang login
            db.collection("rekammedis")
                    .whereEqualTo("idPasien", firebaseUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        list.clear();
                        //cek apakah berhasil atau tidak
                        if (task.isSuccessful()) {
                            //coba mengecek dan menggunakan data
                            try {
                                //cek apakah data tidak kosong
                                Log.d("QWE", "getData: " + task.getResult().getDocuments());
                                if (task.getResult().getDocuments().size() == 0) {
                                    //jika iya, menampilkan toast peringatan
                                    Toast.makeText(RekamMedis.this, "Belum ada data rekam medis!", Toast.LENGTH_SHORT).show();
                                } else {
                                    //jika tidak, melakukan perulangan pada data result
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //memecah data ke masing-masing variabel yang sesuai
                                        String id = document.getId();
                                        String idUser = document.get("idPasien").toString();
                                        String pasien = document.get("pasien").toString();
                                        String berat = document.get("berat").toString();
                                        String denyut = document.get("denyut").toString();
                                        String laju = document.get("laju").toString();
                                        String suhu = document.get("suhu").toString();
                                        String tekanan = document.get("tekanan").toString();
                                        String kondisi = document.get("kondisi").toString();
                                        String lingkar = document.get("lingkar").toString();
                                        String rujukan = document.get("rujukan").toString();
                                        String dateCreated = document.get("dateCreated").toString();
                                        String dateUpdated = document.get("dateUpdated").toString();
                                        //memasukkan pecahal variabel ke variabel UserRekam
                                        UserRekam user = new UserRekam(id, idUser, pasien, berat, lingkar, kondisi, tekanan, laju, suhu, denyut, rujukan, dateCreated, dateUpdated);
                                        user.setId(document.getId());
                                        //menambahkan variabel user ke vairabel list
                                        list.add(user);
                                    }
                                    //memanggil fungsi sortData()
                                    sortData();
                                    //memanggil fungsi showRekamMedis()
                                    showRekamMedis();
                                    //memanggil fungsi setRekamEntries() untuk chart
                                    setRekamEntries();
                                }
                            } catch (Exception e) {
                                //jika percobaan gagal, menampilkan toast peringatan
                                Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                                Log.d("REKAMMEDISGETDATA", "getData: " + e.getMessage());
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

    //fungsi untuk menghapus data rekam berdasarkan id
    private void deleteData(String id) {
        progressDialog.show();
        //menghapus data rekam di firebase berdasarkan id pada parameter
        db.collection("rekammedis").document(id)
                .delete()
                .addOnCompleteListener(task -> {
                    //cek apakah berhasil atau tidak
                    if (!task.isSuccessful()) {
                        //jika gagal tampilkan toast peringatan
                        Toast.makeText(getApplicationContext(), "Data gagal dihapus!", Toast.LENGTH_SHORT).show();
                    }
                    //tutup progressdialog
                    progressDialog.dismiss();

                    //panggil fungsi getData()
                    getData();
                });
    }

    //fungsi untuk menampilkan rekam berdasarkan role
    private void showRekamMedis() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        //cek role apakah 1 atau bukan
        if (userrole == 1) {
            //jika 1 pakai adapter rekam admin
            recyclerView.setAdapter(adminAdapterRekam);
        } else {
            //jika bukan pakai adapter rekam user
            recyclerView.setAdapter(userAdapterRekam);
        }
    }

    //fungsi untuk membuat label chart berdasarkan tanggal data rekam dibuat
    private void setRekamEntries() {
        for (int i = 0; i < list.size(); i++) {
            denyutEntries.add(new Entry(i + 1, Float.parseFloat(list.get(i).getDenyutJantung())));
            beratEntries.add(new Entry(i + 1, Float.parseFloat(list.get(i).getBeratBadan())));
            lingkarEntries.add(new Entry(i + 1, Float.parseFloat(list.get(i).getLingkarBadan())));
            kondisihbEntries.add(new Entry(i + 1, Float.parseFloat(list.get(i).getKondisiHB())));
            suhuEntries.add(new Entry(i + 1, Float.parseFloat(list.get(i).getSuhu())));
            lajuEntries.add(new Entry(i + 1, Float.parseFloat(list.get(i).getLajuPernafasan())));

            String tekananDarah = list.get(i).getTekananDarah();
            String sistolik = tekananDarah.substring(0, tekananDarah.indexOf("/"));
            tekananEntries.add(new Entry(i + 1, Float.parseFloat(sistolik)));

            listLabels.add(list.get(i).getDateCreated());
        }
        listLabels.add("");
        //memanggil fungsi setEntriesToChart() untuk membuat chart
        setEntriesToChart();
    }

    //fungsi untuk membuat chart berdasarkan data list rekam
    private void setEntriesToChart() {
        //set chart data
        LineDataSet dataSetDenyut = new LineDataSet(denyutEntries, "Denyut jantung(80x/menit)");
        dataSetDenyut.setColor(getResources().getColor(R.color.grey_blue));
        dataSetDenyut.setCircleColor(getResources().getColor(R.color.grey_blue));
        dataSetDenyut.setCircleRadius(4f);
        dataSetDenyut.setLineWidth(2f);
        dataSetDenyut.setValueTextSize(8f);

        LineDataSet dataSetBerat = new LineDataSet(beratEntries, "Berat badan(65-70 kg)");
        dataSetBerat.setColor(getResources().getColor(R.color.green));
        dataSetBerat.setCircleColor(getResources().getColor(R.color.green));
        dataSetBerat.setCircleRadius(4f);
        dataSetBerat.setLineWidth(2f);
        dataSetBerat.setValueTextSize(8f);

        LineDataSet dataSetLingkar = new LineDataSet(lingkarEntries, "Lingkar lengan(28-30 cm)");
        dataSetLingkar.setColor(getResources().getColor(R.color.purple));
        dataSetLingkar.setCircleColor(getResources().getColor(R.color.purple));
        dataSetLingkar.setCircleRadius(4f);
        dataSetLingkar.setLineWidth(2f);
        dataSetLingkar.setValueTextSize(8f);

        LineDataSet dataSetKondisi = new LineDataSet(kondisihbEntries, "Kondisi HB(12,5 gr%)");
        dataSetKondisi.setColor(getResources().getColor(R.color.red));
        dataSetKondisi.setCircleColor(getResources().getColor(R.color.red));
        dataSetKondisi.setCircleRadius(4f);
        dataSetKondisi.setLineWidth(2f);
        dataSetKondisi.setValueTextSize(8f);

        LineDataSet dataSetSuhu = new LineDataSet(suhuEntries, "Suhu tubuh(36.3 °C)");
        dataSetSuhu.setColor(getResources().getColor(R.color.pink));
        dataSetSuhu.setCircleColor(getResources().getColor(R.color.pink));
        dataSetSuhu.setCircleRadius(4f);
        dataSetSuhu.setLineWidth(2f);
        dataSetSuhu.setValueTextSize(8f);

        LineDataSet dataSetLaju = new LineDataSet(lajuEntries, "Laju pernafasan(20x)");
        dataSetLaju.setColor(getResources().getColor(R.color.yellow));
        dataSetLaju.setCircleColor(getResources().getColor(R.color.yellow));
        dataSetLaju.setCircleRadius(4f);
        dataSetLaju.setLineWidth(2f);
        dataSetLaju.setValueTextSize(8f);

        LineDataSet dataSetTekanan = new LineDataSet(tekananEntries, "Tekanan darah(110/72 mmHg)");
        dataSetTekanan.setColor(getResources().getColor(R.color.blue));
        dataSetTekanan.setCircleColor(getResources().getColor(R.color.blue));
        dataSetTekanan.setCircleRadius(4f);
        dataSetTekanan.setLineWidth(2f);
        dataSetTekanan.setValueTextSize(8f);

        //inisiasi dataset berdasarkan chart data diatas
        List<ILineDataSet> datasets = new ArrayList<>();
        datasets.add(dataSetDenyut);
        datasets.add(dataSetBerat);
        datasets.add(dataSetLingkar);
        datasets.add(dataSetKondisi);
        datasets.add(dataSetSuhu);
        datasets.add(dataSetLaju);
        datasets.add(dataSetTekanan);

        //set chart limit y axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(120f);

        //set chart description
        lineChart.getDescription().setEnabled(true);
        Description description = new Description();
        description.setText("Waktu Cek");
        description.setTextSize(10f);
        lineChart.setDescription(description);

        //set chart label x axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(list.size() + 1);
        xAxis.setLabelCount(list.size() + 1, true);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(7f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ClaimsXAxisValueFormatter(listLabels));

        //set legend
        Legend legend = lineChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);

        //set data to chart
        LineData lineData = new LineData(datasets);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setData(lineData);
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
                        UserRekam temp = list.get(j-1);
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