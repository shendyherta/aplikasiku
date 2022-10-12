package com.sh.aplikasiku.ui.artikel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sh.aplikasiku.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditArtikel extends AppCompatActivity {
    private EditText editjudul, editpenjelasan;
    private ImageView avatar;
    private AppCompatImageButton btnAdd;
    private Button btnsave;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    private String id = "", dateCreated, dateUpdated;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_artikel);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        editjudul = findViewById(R.id.judul);
        editpenjelasan = findViewById(R.id.penjelasan);
        btnsave = findViewById(R.id.btn_save);
        avatar = findViewById(R.id.avatar);
        btnAdd = findViewById(R.id.btn_add);


        progressDialog = new ProgressDialog(EditArtikel.this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("menyimpan...");

        btnAdd.setOnClickListener(v -> selectImage());
        avatar.setOnClickListener(v -> selectImage());

        btnsave.setOnClickListener(v -> {
            if (editjudul.getText().length() > 0 && editpenjelasan.getText().length() > 0) {
                upload(editjudul.getText().toString(), editpenjelasan.getText().toString());
            } else {
                Toast.makeText(getApplicationContext(), "Silakan isi dulu artikel", Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            String option = intent.getStringExtra("option");
            if (option.equalsIgnoreCase("edit")) {
                id = intent.getStringExtra("id");
                editjudul.setText(intent.getStringExtra("judul"));
                editpenjelasan.setText(intent.getStringExtra("penjelasan"));
                dateCreated = intent.getStringExtra("dateCreated");
                dateUpdated = intent.getStringExtra("dateUpdated");
                Glide.with(getApplicationContext()).load(intent.getStringExtra("avatar")).into(avatar);
                btnAdd.setVisibility(View.GONE);
                avatar.setVisibility(View.VISIBLE);

                //set toolbar title
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Edit Artikel");
            } else {
                id = null;

                //set toolbar title
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Tambah Artikel");
            }

        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditArtikel.this);
        builder.setTitle(getString(R.string.app_name));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 10);
            } else if (items[item].equals("Choose from Library")) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 20);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
                ;
            }
        });
        builder.show();
    }

    //codingan pengambilan foto galeri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == RESULT_OK && data != null) {
            final Uri path = data.getData();
            Thread thread = new Thread(() -> {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(path);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    avatar.post(() -> avatar.setImageBitmap(bitmap));


                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            btnAdd.setVisibility(View.GONE);
            avatar.setVisibility(View.VISIBLE);
        }

        //codingan take photo dari camera
        if (requestCode == 10 && resultCode == RESULT_OK) {
            final Bundle extras = data.getExtras();
            Thread thread = new Thread(() -> {
                Bitmap bitmap = (Bitmap) extras.get("data");
                avatar.post(() -> avatar.setImageBitmap(bitmap));
            });
            thread.start();
            btnAdd.setVisibility(View.GONE);
            avatar.setVisibility(View.VISIBLE);
        }

    }

    private void upload(String judul, String penjelasan) {
        progressDialog.show();
        avatar.setDrawingCacheEnabled(true);
        avatar.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) avatar.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        //upload
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("images").child("IMG" + new Date().getTime() + ".jpeg");
        UploadTask uploadTask = reference.putBytes(data);
        uploadTask.addOnFailureListener(e -> {

            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }).addOnSuccessListener(taskSnapshot -> {
            if (taskSnapshot.getMetadata() != null) {
                if (taskSnapshot.getMetadata().getReference() != null) {
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.getResult() != null) {
                                saveData(judul, penjelasan, task.getResult().toString());
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                }
            } else {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void saveData(String judul, String penjelasan, String avatar) {
        //untuk mendapatkan tanggal saat data dibuat
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());


        progressDialog.show();
        //untuk menambahkan data, jika id length not null berati edit kalau di else nya berarti fitur tambah

        if (id != null) {
            Map<String, Object> artikel = new HashMap<>();
            artikel.put("judul", judul);
            artikel.put("penjelasan", penjelasan);
            artikel.put("avatar", avatar);
            artikel.put("dateCreated", date);
            artikel.put("dateUpdated", date);

            db.collection("artikel").document(id)
                    .set(artikel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });

        } else {
            Map<String, Object> artikel = new HashMap<>();
            artikel.put("judul", judul);
            artikel.put("penjelasan", penjelasan);
            artikel.put("avatar", avatar);
            artikel.put("dateCreated", date);
            artikel.put("dateUpdated", date);

            db.collection("artikel")
                    .add(artikel)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    });
        }
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
}