package com.sh.aplikasiku.ui.rekammedis;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
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

import android.os.Bundle;

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
    private LinearLayoutCompat llPasien;
    private int userrole;
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
                                        String dateCreated = document.get("dateCreated").toString();
                                        String dateUpdated = document.get("dateUpdated").toString();
                                        UserRekam user = new UserRekam(id, idUser, pasien, berat, lingkar, kondisi, tekanan, laju, suhu, denyut, dateCreated, dateUpdated);
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
                                        String dateCreated = document.get("dateCreated").toString();
                                        String dateUpdated = document.get("dateUpdated").toString();
                                        UserRekam user = new UserRekam(id, idUser, pasien, berat, lingkar, kondisi, tekanan, laju, suhu, denyut, dateCreated, dateUpdated);
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
                        Toast.makeText(getApplicationContext(), "Data Gagal Dihapus", Toast.LENGTH_SHORT).show();
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
        for(int i = 0; i < list.size(); i++) {
            suhuEntries.add(new Entry(i+1, Integer.parseInt(list.get(i).getSuhu())));
            lajuEntries.add(new Entry(i+1, Integer.parseInt(list.get(i).getLajuPernafasan())));
            tekananEntries.add(new Entry(i+1, Integer.parseInt(list.get(i).getTekananDarah())));
            listLabels.add(list.get(i).getDateCreated());
        }
        listLabels.add("");
        setEntriesToChart();
    }

    private void setEntriesToChart() {
        //set chart data
        LineDataSet dataSetSuhu = new LineDataSet(suhuEntries, "Suhu tubuh");
        dataSetSuhu.setColor(getResources().getColor(R.color.pink));
        dataSetSuhu.setCircleColor(getResources().getColor(R.color.pink));
        dataSetSuhu.setLineWidth(2f);

        LineDataSet dataSetLaju = new LineDataSet(lajuEntries, "Laju pernafasan");
        dataSetLaju.setColor(getResources().getColor(R.color.yellow));
        dataSetLaju.setCircleColor(getResources().getColor(R.color.yellow));
        dataSetLaju.setLineWidth(2f);

        LineDataSet dataSetTekanan = new LineDataSet(tekananEntries, "Tekanan darah");
        dataSetTekanan.setColor(getResources().getColor(R.color.blue));
        dataSetTekanan.setCircleColor(getResources().getColor(R.color.blue));
        dataSetTekanan.setLineWidth(2f);

        List<ILineDataSet> datasets = new ArrayList<>();
        datasets.add(dataSetSuhu);
        datasets.add(dataSetLaju);
        datasets.add(dataSetTekanan);

        //set chart range
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(110f);

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

}