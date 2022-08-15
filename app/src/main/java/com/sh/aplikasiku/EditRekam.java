package com.sh.aplikasiku;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditRekam extends AppCompatActivity {
private EditText editberat, editlingkar, editkondisi, edittekanan, editlaju, editsuhu, editdenyut;
private Button btnsave;
private FirebaseFirestore db = FirebaseFirestore.getInstance();
private ProgressDialog progressDialog;
private String id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rekam);
        editberat = findViewById(R.id.berat);
        editlingkar = findViewById(R.id.lingkar);
        editkondisi = findViewById(R.id.kondisi);
        edittekanan = findViewById(R.id.tekanan);
        editlaju = findViewById(R.id.laju);
        editsuhu = findViewById(R.id.suhu);
        editdenyut = findViewById(R.id.denyut);
        btnsave = findViewById(R.id.btn_save);


        progressDialog = new ProgressDialog(EditRekam.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("menyimpan...");
        
        btnsave.setOnClickListener(v -> {
            if(editberat.getText().length()>0 && editlingkar.getText().length()>0 && editkondisi.getText().length()>0 && edittekanan.getText().length()>0 && editlaju.getText().length()>0 && editsuhu.getText().length()>0 && editdenyut.getText().length()>0){
                saveData(editberat.getText().toString(), editlingkar.getText().toString(), editkondisi.getText().toString(), edittekanan.getText().toString(), editlaju.getText().toString(), editsuhu.getText().toString(), editdenyut.getText().toString());
            }else{
                Toast.makeText(getApplicationContext(), "Silakan isi dulu artikel", Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = getIntent();
        if(intent!=null){
            id= intent.getStringExtra("id");
            editberat.setText(intent.getStringExtra("Berat Badan"));
            editlingkar.setText(intent.getStringExtra("Lingkar Lengan"));
            editkondisi.setText(intent.getStringExtra("Kondisi"));
            edittekanan.setText(intent.getStringExtra("Tekanan Darah"));
            editlaju.setText(intent.getStringExtra("Laju Pernafasan"));
            editsuhu.setText(intent.getStringExtra("Suhu Badan"));
            editdenyut.setText(intent.getStringExtra("Denyut Jantung"));

        }
    }
    private void saveData(String denyut, String suhu, String laju, String tekanan, String kondisi, String berat, String lingkar){
        Map<String, Object> user = new HashMap<>();
        user.put("berat", berat);
        user.put("lingkar", lingkar);
        user.put("kondisi", kondisi);
        user.put("tekanan", tekanan);
        user.put("laju", laju);
        user.put("suhu", suhu);
        user.put("denyut", denyut);


        progressDialog.show();
        //untuk menambahkan data, jika id length not null berati edit kalau di else nya berarti fitur tambah

        if(id!=null){
            db.collection("rekammedis").document(id)
                    .set(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }else {
            db.collection("rekammedis")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        }

    }
}