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

    //inisiasi variabel baru dan komponen penampung
    private Context context;
    private List<UserPantau> list;
    private Dialog dialog;

    //membuat interface dialog ketika di klik
    public interface Dialog {
        void onClick(int pos);
    }

    //fungsi untuk menerima dialog buatan dan memasukkannya di variabel global disini
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    //konstruktor untuk menerima data dan memasukkannya ke variabel global disini
    public AdminAdapterPantau(Context context, List<UserPantau> list) {
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
        holder.pasien.setText(list.get(position).getPasien());
        holder.date.setText(list.get(position).getDateCreated());
        holder.rujukan.setText(list.get(position).getRujukan());

        String rujukan = list.get(position).getRujukan();

        //merubah warna background rujukan ke abu-abu jika rujukan berisi butuh rujukan
        if (rujukan.equalsIgnoreCase("butuh rujukan")) {
            holder.clHeader.setBackgroundColor(context.getResources().getColor(R.color.grey_blue));
        }
    }

    //fungsi untuk mengembalikan ukuran dari data list
    @Override
    public int getItemCount() {
        return list.size();
    }

    //class untuk membuat komponen penampung dan menyambungkannya
    class MyViewHolder extends RecyclerView.ViewHolder {

        //inisiasi komponen penampung
        TextView kondisibayi, denyutjantung, pasien, date, rujukan;
        ConstraintLayout clHeader;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //menyambungkan komponen dengan xml
            denyutjantung = itemView.findViewById(R.id.denyutjantung);
            kondisibayi = itemView.findViewById(R.id.kondisibayi);
            pasien = itemView.findViewById(R.id.tv_pasien);
            date = itemView.findViewById(R.id.tv_date);
            rujukan = itemView.findViewById(R.id.tv_rujukan);
            clHeader = itemView.findViewById(R.id.cl_header);

            //menangani ketika dialog di klik
            itemView.setOnClickListener(view -> {
                    if(dialog!=null){
                        dialog.onClick(getLayoutPosition());
                    }
            });
        }
    }
}