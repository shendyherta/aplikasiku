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
    private Context context;
    private List<UserArtikel> list;

    //konstruktor
    public UserAdapterArtikel(Context context, List<UserArtikel> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.judul.setText(list.get(position).getJudul());
        holder.penjelasan.setText(list.get(position).getPenjelasan());
        Glide.with(context).load(list.get(position).getAvatar()).into(holder.avatar);

        holder.itemView.setOnClickListener(v -> {
            Intent intentbaca = new Intent(context, TampilArtikel.class);
            intentbaca.putExtra("id", list.get(position).getId());
            intentbaca.putExtra("judul", list.get(position).getJudul());
            intentbaca.putExtra("penjelasan", list.get(position).getPenjelasan());
            intentbaca.putExtra("avatar", list.get(position).getAvatar());
            context.startActivity(intentbaca);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView judul, penjelasan;
        ImageView avatar;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.judul);
            penjelasan = itemView.findViewById(R.id.penjelasan);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }
}
