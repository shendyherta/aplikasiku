package com.sh.aplikasiku.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

        holder.itemView.setOnClickListener(v -> {
            Intent intentbaca = new Intent(context, TampilPantauKehamilan.class);
            intentbaca.putExtra("id", list.get(position).getId());
            intentbaca.putExtra("denyutjantung", list.get(position).getDenyut());
            intentbaca.putExtra("kondisibayi", list.get(position).getKondisi());
            context.startActivity(intentbaca);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView kondisibayi, denyutjantung;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            denyutjantung = itemView.findViewById(R.id.denyutjantung);
            kondisibayi = itemView.findViewById(R.id.kondisibayi);
        }
    }
}
