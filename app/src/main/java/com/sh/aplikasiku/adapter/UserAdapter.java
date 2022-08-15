package com.sh.aplikasiku.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh.aplikasiku.R;
import com.sh.aplikasiku.model.User;

import org.w3c.dom.Text;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder>{
    private Context context;
    private List<User> list;
    private Dialog dialog;

    public interface Dialog{
        void onClick(int pos);
    }

    public void setDialog(Dialog dialog){
        this.dialog = dialog;
    }

    //konstruktor
    public UserAdapter(Context context, List<User> list){
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView judul, penjelasan;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.judul);
            penjelasan = itemView.findViewById(R.id.penjelasan);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(dialog!=null){
                        dialog.onClick(getLayoutPosition());
                    }
                }
            });
        }
    }
}
