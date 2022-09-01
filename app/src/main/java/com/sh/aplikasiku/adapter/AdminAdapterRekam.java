package com.sh.aplikasiku.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.aplikasiku.R;
import com.sh.aplikasiku.model.UserRekam;

import java.util.List;

public class AdminAdapterRekam extends RecyclerView.Adapter<AdminAdapterRekam.MyViewHolder>{
    private Context context;
    private List<UserRekam> list;
    private Dialog dialog;

    public interface Dialog{
        void onClick(int pos);
    }

    public void setDialog(Dialog dialog){
        this.dialog = dialog;
    }

    //konstruktor
    public AdminAdapterRekam(Context context, List<UserRekam> list){
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
        holder.pasien.setText(list.get(position).getPasien());
        holder.date.setText(list.get(position).getDateCreated());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView berat, lingkar, suhu, laju, kondisi, tekanan, denyut, pasien, date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            berat = itemView.findViewById(R.id.berat);
            lingkar = itemView.findViewById(R.id.lingkar);
            denyut = itemView.findViewById(R.id.denyut);
            kondisi = itemView.findViewById(R.id.kondisi);
            laju = itemView.findViewById(R.id.laju);
            suhu = itemView.findViewById(R.id.suhu);
            tekanan = itemView.findViewById(R.id.tekanan);
            pasien = itemView.findViewById(R.id.tv_pasien);
            date = itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(view -> {
                if(dialog!=null){
                    dialog.onClick(getLayoutPosition());
                }
            });
        }
    }
}
