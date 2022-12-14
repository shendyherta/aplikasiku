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
import com.sh.aplikasiku.ui.rekammedis.TampilanRekam;
import com.sh.aplikasiku.model.UserRekam;

import java.util.List;

public class UserAdapterRekam extends RecyclerView.Adapter<UserAdapterRekam.MyViewHolder> {

    //inisiasi variabel baru
    private Context context;
    private List<UserRekam> list;

    //konstruktor untuk menerima data dan memasukkannya ke variabel global disini
    public UserAdapterRekam(Context context, List<UserRekam> list) {
        this.context = context;
        this.list = list;
    }

    //fungsi untuk membuat viewholder dan menggunakan xml pilihan dengan layout inflater
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rekam_medis, parent, false);
        return new MyViewHolder(itemView);
    }

    //fungsi untuk melakukan bind data dengan view
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.berat.setText(String.format("%s kg", list.get(position).getBeratBadan()));
        holder.lingkar.setText(String.format("%s cm", list.get(position).getLingkarBadan()));
        holder.denyut.setText(String.format("%sx/menit", list.get(position).getDenyutJantung()));
        holder.kondisi.setText(String.format("%s gr%%", list.get(position).getKondisiHB()));
        holder.laju.setText(String.format("%sx/menit", list.get(position).getLajuPernafasan()));
        holder.suhu.setText(String.format("%s °C", list.get(position).getSuhu()));
        holder.tekanan.setText(String.format("%s mmHg", list.get(position).getTekananDarah()));
        holder.date.setText(list.get(position).getDateCreated());

        holder.rujukan.setText(list.get(position).getRujukan());

        String rujukan = list.get(position).getRujukan();

        //merubah warna background rujukan ke abu-abu jika rujukan berisi butuh rujukan
        if (rujukan.equalsIgnoreCase("butuh rujukan")) {
            holder.clHeader.setBackgroundColor(context.getResources().getColor(R.color.grey_blue));
        }

        //menangani ketika item pada recyclerview di klik
        holder.itemView.setOnClickListener(v -> {
            //mengirim data artikel ketika di klik ke halaman tampil rekam medis
            Intent intent = new Intent(context, TampilanRekam.class);
            intent.putExtra("id", list.get(position).getId());
            intent.putExtra("berat", list.get(position).getBeratBadan());
            intent.putExtra("lingkar", list.get(position).getLingkarBadan());
            intent.putExtra("laju", list.get(position).getLajuPernafasan());
            intent.putExtra("tekanan", list.get(position).getTekananDarah());
            intent.putExtra("suhu", list.get(position).getSuhu());
            intent.putExtra("denyut", list.get(position).getDenyutJantung());
            intent.putExtra("kondisi", list.get(position).getKondisiHB());
            intent.putExtra("rujukan", list.get(position).getRujukan());
            intent.putExtra("dateCreated", list.get(position).getDateCreated());
            context.startActivity(intent);
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
        TextView berat, lingkar, suhu, laju, kondisi, tekanan, denyut, date, rujukan;
        LinearLayoutCompat llPasien;
        ConstraintLayout clHeader;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //menyambungkan komponen dengan xml
            berat = itemView.findViewById(R.id.berat);
            lingkar = itemView.findViewById(R.id.lingkar);
            denyut = itemView.findViewById(R.id.denyut);
            kondisi = itemView.findViewById(R.id.kondisi);
            laju = itemView.findViewById(R.id.laju);
            suhu = itemView.findViewById(R.id.suhu);
            tekanan = itemView.findViewById(R.id.tekanan);
            date = itemView.findViewById(R.id.tv_date);
            llPasien = itemView.findViewById(R.id.ll_pasien);
            rujukan = itemView.findViewById(R.id.tv_rujukan);
            clHeader = itemView.findViewById(R.id.cl_header);

            llPasien.setVisibility(View.GONE);
        }
    }
}
