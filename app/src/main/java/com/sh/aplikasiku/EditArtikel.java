package com.sh.aplikasiku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditArtikel extends AppCompatActivity {
private EditText editjudul, editpenjelasan;
private Button btnsave;
private FirebaseFirestore db = FirebaseFirestore.getInstance();
private ProgressDialog progressDialog;
private String id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_artikel);
        editjudul = findViewById(R.id.judul);
        editpenjelasan = findViewById(R.id.penjelasan);
        btnsave = findViewById(R.id.btn_save);


        progressDialog = new ProgressDialog(EditArtikel.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("menyimpan...");
        
        btnsave.setOnClickListener(v -> {
            if(editjudul.getText().length()>0 && editpenjelasan.getText().length()>0){
                saveData(editjudul.getText().toString(), editpenjelasan.getText().toString());
            }else{
                Toast.makeText(getApplicationContext(), "Silakan isi dulu artikel", Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = getIntent();
        if(intent!=null){
            id= intent.getStringExtra("id");
            editjudul.setText(intent.getStringExtra("judul"));
            editpenjelasan.setText(intent.getStringExtra("penjelasan"));

        }
    }
    private void saveData(String judul, String penjelasan){
        Map<String, Object> user = new HashMap<>();
        user.put("judul", judul);
        user.put("penjelasan", penjelasan);


        progressDialog.show();
        //untuk menambahkan data, jika id length not null berati edit kalau di else nya berarti fitur tambah

        if(id!=null){
            db.collection("users").document(id)
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
            db.collection("users")
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