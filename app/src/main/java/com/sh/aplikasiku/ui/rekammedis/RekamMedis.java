package com.sh.aplikasiku.ui.rekammedis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

public class RekamMedis extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private LineChart lineChart;
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

    //create activity result
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rekam Medis");

        //get userrole
        sharedPref = getSharedPreferences(getString(R.string.data_user), MODE_PRIVATE);
        userrole = sharedPref.getInt(getString(R.string.user_role), 0);

        recyclerView = findViewById(R.id.recyclerview);
        btnAdd = findViewById(R.id.btn_add);
        lineChart = findViewById(R.id.line_chart);

        //create progress bar
        progressDialog = new ProgressDialog(RekamMedis.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("Mengambil data");

        userAdapterRekam = new UserAdapterRekam(getApplicationContext(), list);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditRekam.class);
            intent.putExtra("option", "add");
            getCreateEditResult.launch(intent);
        });

        if (userrole == 1) {
            btnAdd.setVisibility(View.VISIBLE);
            adminAdapterRekam = new AdminAdapterRekam(this, list);
            adminAdapterRekam.setDialog(pos -> {
                final CharSequence[] dialogItem = {"Detail", "Edit", "Hapus"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(RekamMedis.this);
                dialog.setItems(dialogItem, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
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
                        case 1:
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
                        case 2:
                            deleteData(list.get(pos).getId());
                            break;
                    }
                });
                dialog.show();
            });
        } else {
            btnAdd.setVisibility(View.GONE);
            userAdapterRekam = new UserAdapterRekam(this, list);
        }

        getData();
    }

    //untuk menampilkan getdata alias data yang telah diubah maupun ditambah
    @Override
    protected void onStart() {
        super.onStart();
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

        if (userrole == 1) {
            db.collection("rekammedis")
                    .orderBy("dateCreated")
                    .get()
                    .addOnCompleteListener(task -> {
                        list.clear();
                        if (task.isSuccessful()) {
                            try {
                                if (task.getResult().getDocuments().size() == 0) {
                                    Toast.makeText(RekamMedis.this, "Belum ada data rekam medis!", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
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
                                        UserRekam user = new UserRekam(id, idUser, pasien, berat, lingkar, kondisi, tekanan, laju, suhu, denyut, rujukan, dateCreated, dateUpdated);
                                        user.setId(document.getId());
                                        list.add(user);
                                    }
                                    showRekamMedis();
                                    lineChart.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                                Log.d("REKAMMEDISGETDATA", "getData: " + e.getMessage());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Data Gagal", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    });
        } else {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            db.collection("rekammedis")
                    .whereEqualTo("idPasien", firebaseUser.getUid())
                    .orderBy("dateCreated")
                    .get()
                    .addOnCompleteListener(task -> {
                        list.clear();
                        if (task.isSuccessful()) {
                            try {
                                Log.d("QWE", "getData: " + task.getResult().getDocuments());
                                if (task.getResult().getDocuments().size() == 0) {
                                    Toast.makeText(RekamMedis.this, "Belum ada data rekam medis!", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
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
                                        UserRekam user = new UserRekam(id, idUser, pasien, berat, lingkar, kondisi, tekanan, laju, suhu, denyut, rujukan, dateCreated, dateUpdated);
                                        user.setId(document.getId());
                                        list.add(user);
                                    }
                                    showRekamMedis();
                                    setRekamEntries();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Coba lagi nanti!", Toast.LENGTH_SHORT).show();
                                Log.d("REKAMMEDISGETDATA", "getData: " + e.getMessage());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Data Gagal", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    });
        }

    }

    private void deleteData(String id) {
        progressDialog.show();
        db.collection("rekammedis").document(id)
                .delete()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Data gagal dihapus!", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                    getData();
                });
    }

    private void showRekamMedis() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        if (userrole == 1) {
            recyclerView.setAdapter(adminAdapterRekam);
        } else {
            recyclerView.setAdapter(userAdapterRekam);
        }
    }

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
        setEntriesToChart();
    }

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

        List<ILineDataSet> datasets = new ArrayList<>();
        datasets.add(dataSetDenyut);
        datasets.add(dataSetBerat);
        datasets.add(dataSetLingkar);
        datasets.add(dataSetKondisi);
        datasets.add(dataSetSuhu);
        datasets.add(dataSetLaju);
        datasets.add(dataSetTekanan);

        //set chart range
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(10f);
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

}