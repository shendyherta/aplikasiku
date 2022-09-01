package com.sh.aplikasiku.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.aplikasiku.R;
import com.sh.aplikasiku.ui.pantaukehamilan.TampilPantauKehamilan;
import com.sh.aplikasiku.model.UserPantau;

import java.util.List;

public class UserAdapterPantau extends RecyclerView.Adapter<UserAdapterPantau.MyViewHolder> {
    private Context context;
    private List<UserPantau> list;

    //konstruktor
    public UserAdapterPantau(Context context, List<UserPantau> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pantau_kehamilan, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.denyutjantung.setText(list.get(position).getDenyut());
        holder.kondisibayi.setText(list.get(position).getKondisi());
        holder.date.setText(list.get(position).getDateCreated());

        holder.itemView.setOnClickListener(v -> {
            Intent intentbaca = new Intent(context, TampilPantauKehamilan.class);
            intentbaca.putExtra("id", list.get(position).getId());
            intentbaca.putExtra("idUser", list.get(position).getIdUser());
            intentbaca.putExtra("pasien", list.get(position).getPasien());
            intentbaca.putExtra("denyutjantung", list.get(position).getDenyut());
            intentbaca.putExtra("kondisibayi", list.get(position).getKondisi());
            intentbaca.putExtra("dateCreated", list.get(position).getDateCreated());
            intentbaca.putExtra("dateUpdated", list.get(position).getDateUpdated());
            context.startActivity(intentbaca);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView kondisibayi, denyutjantung, date;
        LinearLayoutCompat llPasien;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            denyutjantung = itemView.findViewById(R.id.denyutjantung);
            kondisibayi = itemView.findViewById(R.id.kondisibayi);
            date = itemView.findViewById(R.id.tv_date);
            llPasien = itemView.findViewById(R.id.ll_pasien);

            llPasien.setVisibility(View.GONE);
        }
    }
}
