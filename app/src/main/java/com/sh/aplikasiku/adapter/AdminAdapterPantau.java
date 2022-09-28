package com.sh.aplikasiku.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.aplikasiku.R;
import com.sh.aplikasiku.model.UserPantau;

import java.util.List;

public class AdminAdapterPantau extends RecyclerView.Adapter<AdminAdapterPantau.MyViewHolder> {
    private Context context;
    private List<UserPantau> list;
    private Dialog dialog;

    public interface Dialog {
        void onClick(int pos);
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    //konstruktor
    public AdminAdapterPantau(Context context, List<UserPantau> list) {
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
        holder.denyutjantung.setText(String.format("%sx/menit", list.get(position).getDenyut()));
        holder.kondisibayi.setText(list.get(position).getKondisi());
        holder.pasien.setText(list.get(position).getPasien());
        holder.date.setText(list.get(position).getDateCreated());
        holder.rujukan.setText(list.get(position).getRujukan());

        String rujukan = list.get(position).getRujukan();

        if (rujukan.equalsIgnoreCase("butuh rujukan")) {
            holder.clHeader.setBackgroundColor(context.getResources().getColor(R.color.grey_blue));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView kondisibayi, denyutjantung, pasien, date, rujukan;
        ConstraintLayout clHeader;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            denyutjantung = itemView.findViewById(R.id.denyutjantung);
            kondisibayi = itemView.findViewById(R.id.kondisibayi);
            pasien = itemView.findViewById(R.id.tv_pasien);
            date = itemView.findViewById(R.id.tv_date);
            rujukan = itemView.findViewById(R.id.tv_rujukan);
            clHeader = itemView.findViewById(R.id.cl_header);

            itemView.setOnClickListener(view -> {
                    if(dialog!=null){
                        dialog.onClick(getLayoutPosition());
                    }
            });
        }
    }
}