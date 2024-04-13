package com.example.tuempleoblind.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuempleoblind.R;
import com.example.tuempleoblind.model.JobsAvailable;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class JobsAvailableAdapter extends FirestoreRecyclerAdapter<JobsAvailable, JobsAvailableAdapter.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public JobsAvailableAdapter(@NonNull FirestoreRecyclerOptions<JobsAvailable> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull JobsAvailable model) {
        holder.title.setText(model.getTitle());
        holder.category.setText(model.getCategory());
        holder.salary.setText(model.getSalary());
        holder.checkElevator.setText(model.getCheckElevator() ? "Sí" : "No");
        holder.checkRamp.setText(model.getCheckRamp() ? "Sí" : "No");
        holder.btn_view_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_jobs_available,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, category, salary, checkElevator, checkRamp;
        Button btn_view_more;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.JobsAvailableTitle);
            category=itemView.findViewById(R.id.JobsAvailableCategory);
            salary=itemView.findViewById(R.id.JobsAvailableSalary);
            checkElevator=itemView.findViewById(R.id.JobsAvailableElevator);
            checkRamp=itemView.findViewById(R.id.JobsAvailableRamp);
            btn_view_more=itemView.findViewById(R.id.buttonViewMore);
        }
    }
}
