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
import com.sh.aplikasiku.ui.rekammedis.TampilanRekam;
import com.sh.aplikasiku.model.UserRekam;

import java.util.List;

public class UserAdapterRekam extends RecyclerView.Adapter<UserAdapterRekam.MyViewHolder> {
    private Context context;
    private List<UserRekam> list;

    //konstruktor
    public UserAdapterRekam(Context context, List<UserRekam> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rekam_medis, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.berat.setText(list.get(position).getBeratBadan());
        holder.lingkar.setText(list.get(position).getLingkarBadan());
        holder.denyut.setText(list.get(position).getDenyutJantung());
        holder.kondisi.setText(list.get(position).getKondisiHB());
        holder.laju.setText(list.get(position).getLajuPernafasan());
        holder.suhu.setText(list.get(position).getSuhu());
        holder.tekanan.setText(list.get(position).getTekananDarah());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TampilanRekam.class);
            intent.putExtra("id", list.get(position).getId());
            intent.putExtra("berat", list.get(position).getBeratBadan());
            intent.putExtra("lingkar", list.get(position).getLingkarBadan());
            intent.putExtra("laju", list.get(position).getLajuPernafasan());
            intent.putExtra("tekanan", list.get(position).getTekananDarah());
            intent.putExtra("suhu", list.get(position).getSuhu());
            intent.putExtra("denyut", list.get(position).getDenyutJantung());
            intent.putExtra("kondisi", list.get(position).getKondisiHB());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView berat, lingkar, suhu, laju, kondisi, tekanan, denyut;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            berat = itemView.findViewById(R.id.berat);
            lingkar = itemView.findViewById(R.id.lingkar);
            denyut = itemView.findViewById(R.id.denyut);
            kondisi = itemView.findViewById(R.id.kondisi);
            laju = itemView.findViewById(R.id.laju);
            suhu = itemView.findViewById(R.id.suhu);
            tekanan = itemView.findViewById(R.id.tekanan);
        }
    }
}
