package com.example.tuempleoblind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    Context context;

    ArrayList<OportunutiesJobs> list;

    public Adapter(Context context, ArrayList<OportunutiesJobs> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.job_offer,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OportunutiesJobs user = list.get(position);
        holder.Usuario.setText(user.getUsuario());
        holder.Contraeña.setText(user.getContraeña());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Usuario, Contraeña;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Usuario= itemView.findViewById(R.id.first_name);
            Contraeña=itemView.findViewById(R.id.pasword_text);
        }
    }
}
