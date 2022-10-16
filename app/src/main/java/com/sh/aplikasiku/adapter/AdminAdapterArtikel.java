package com.sh.aplikasiku.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sh.aplikasiku.R;
import com.sh.aplikasiku.model.UserArtikel;

import java.util.List;

public class AdminAdapterArtikel extends RecyclerView.Adapter<AdminAdapterArtikel.MyViewHolder>{

    //inisiasi variabel baru dan komponen penampung
    private Context context;
    private List<UserArtikel> list;
    private Dialog dialog;

    //membuat interface dialog ketika di klik
    public interface Dialog{
        void onClick(int pos);
    }

    //fungsi untuk menerima dialog buatan dan memasukkannya di variabel global disini
    public void setDialog(Dialog dialog){
        this.dialog = dialog;
    }

    //konstruktor untuk menerima data dan memasukkannya ke variabel global disini
    public AdminAdapterArtikel(Context context, List<UserArtikel> list){
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

            //menangani ketika dialog di klik
            itemView.setOnClickListener(view -> {
                if(dialog!=null){
                    dialog.onClick(getLayoutPosition());
                }
            });
        }
    }
}