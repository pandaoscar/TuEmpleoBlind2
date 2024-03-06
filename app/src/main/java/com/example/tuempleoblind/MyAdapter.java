package com.example.tuempleoblind;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Job> mData;

    public MyAdapter(List<Job> data) {
        mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Job job = mData.get(position);
        holder.titleTextView.setText(job.getTitle());
        holder.categoryTextView.setText(job.getCategory());
        holder.locationTextView.setText(job.getLocation());
        holder.salaryTextView.setText(job.getSalary());
        holder.elevatorTextView.setText(job.hasElevator() ? "Sí" : "No");
        holder.rampTextView.setText(job.hasRamp() ? "Sí" : "No");
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView categoryTextView;
        public TextView locationTextView;
        public TextView salaryTextView;
        public TextView elevatorTextView;
        public TextView rampTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            salaryTextView = itemView.findViewById(R.id.salaryTextView);
            elevatorTextView = itemView.findViewById(R.id.elevatorTextView);
            rampTextView = itemView.findViewById(R.id.rampTextView);
        }
    }
}
