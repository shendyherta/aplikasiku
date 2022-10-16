package com.sh.aplikasiku.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.aplikasiku.R;
import com.sh.aplikasiku.ui.pantaukehamilan.TampilPantauKehamilan;
import com.sh.aplikasiku.model.UserPantau;

import java.util.List;

public class UserAdapterPantau extends RecyclerView.Adapter<UserAdapterPantau.MyViewHolder> {

    //inisiasi variabel baru
    private Context context;
    private List<UserPantau> list;

    //konstruktor untuk menerima data dan memasukkannya ke variabel global disini
    public UserAdapterPantau(Context context, List<UserPantau> list) {
        this.context = context;
        this.list = list;
    }

    //fungsi untuk membuat viewholder dan menggunakan xml pilihan dengan layout inflater
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pantau_kehamilan, parent, false);
        return new MyViewHolder(itemView);
    }

    //fungsi untuk melakukan bind data dengan view
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.denyutjantung.setText(String.format("%sx/menit", list.get(position).getDenyut()));
        holder.kondisibayi.setText(list.get(position).getKondisi());
        holder.date.setText(list.get(position).getDateCreated());
        holder.rujukan.setText(list.get(position).getRujukan());

        String rujukan = list.get(position).getRujukan();

        //merubah warna background rujukan ke abu-abu jika rujukan berisi butuh rujukan
        if (rujukan.equalsIgnoreCase("butuh rujukan")) {
            holder.clHeader.setBackgroundColor(context.getResources().getColor(R.color.grey_blue));
        }

        //menangani ketika item pada recyclerview di klik
        holder.itemView.setOnClickListener(v -> {
            //mengirim data artikel ketika di klik ke halaman tampil pantau kehamilan
            Intent intentbaca = new Intent(context, TampilPantauKehamilan.class);
            intentbaca.putExtra("id", list.get(position).getId());
            intentbaca.putExtra("idUser", list.get(position).getIdUser());
            intentbaca.putExtra("pasien", list.get(position).getPasien());
            intentbaca.putExtra("denyutjantung", list.get(position).getDenyut());
            intentbaca.putExtra("kondisibayi", list.get(position).getKondisi());
            intentbaca.putExtra("rujukan", list.get(position).getRujukan());
            intentbaca.putExtra("dateCreated", list.get(position).getDateCreated());
            intentbaca.putExtra("dateUpdated", list.get(position).getDateUpdated());
            context.startActivity(intentbaca);
        });
    }

    //fungsi untuk mengembalikan ukuran dari data list
    @Override
    public int getItemCount() {
        return list.size();
    }

    //class untuk membuat komponen penampung dan menyambungkannya
    class MyViewHolder extends RecyclerView.ViewHolder {

        //inisiasi komponen penampung
        TextView kondisibayi, denyutjantung, date, rujukan;
        ConstraintLayout clHeader;
        LinearLayoutCompat llPasien;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //menyambungkan komponen dengan xml
            denyutjantung = itemView.findViewById(R.id.denyutjantung);
            kondisibayi = itemView.findViewById(R.id.kondisibayi);
            date = itemView.findViewById(R.id.tv_date);
            rujukan = itemView.findViewById(R.id.tv_rujukan);
            clHeader = itemView.findViewById(R.id.cl_header);
            llPasien = itemView.findViewById(R.id.ll_pasien);

            llPasien.setVisibility(View.GONE);
        }
    }
}
