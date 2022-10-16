package com.sh.aplikasiku.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.ui.artikel.TampilArtikel;
import com.sh.aplikasiku.model.UserArtikel;

import java.util.List;

public class UserAdapterArtikel extends RecyclerView.Adapter<UserAdapterArtikel.MyViewHolder>{

    //inisiasi variabel baru
    private Context context;
    private List<UserArtikel> list;

    //konstruktor untuk menerima data dan memasukkannya ke variabel global disini
    public UserAdapterArtikel(Context context, List<UserArtikel> list){
        this.context = context;
        this.list = list;
    }

    //fungsi untuk membuat viewholder dan menggunakan xml pilihan dengan layout inflater
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_artikel, parent, false);
        return new MyViewHolder(itemView);
    }

    //fungsi untuk melakukan bind data dengan view
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.judul.setText(list.get(position).getJudul());
        holder.date.setText(list.get(position).getDateCreated());
        Glide.with(context).load(list.get(position).getAvatar()).into(holder.avatar);

        //menangani ketika item pada recyclerview di klik
        holder.itemView.setOnClickListener(v -> {
            //mengirim data artikel ketika di klik ke halaman tampil artikel
            Intent intentbaca = new Intent(context, TampilArtikel.class);
            intentbaca.putExtra("id", list.get(position).getId());
            intentbaca.putExtra("judul", list.get(position).getJudul());
            intentbaca.putExtra("penjelasan", list.get(position).getPenjelasan());
            intentbaca.putExtra("avatar", list.get(position).getAvatar());
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
    class MyViewHolder extends RecyclerView.ViewHolder{

        //inisiasi komponen penampung
        TextView judul, date;
        ImageView avatar;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //menyambungkan komponen dengan xml
            judul = itemView.findViewById(R.id.judul);
            avatar = itemView.findViewById(R.id.avatar);
            date = itemView.findViewById(R.id.tv_date);
        }
    }
}
