package com.example.tuempleoblind.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuempleoblind.R;
import com.example.tuempleoblind.model.PostulatesToJob;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class PostulatesToJobAdapter extends FirestoreRecyclerAdapter<PostulatesToJob,PostulatesToJobAdapter.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PostulatesToJobAdapter(@NonNull FirestoreRecyclerOptions<PostulatesToJob> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull PostulatesToJob PostulatesToJob) {
        holder.userBlindName.setText(PostulatesToJob.getUserBlindName());
        holder.userBlindPhone.setText(PostulatesToJob.getUserBlindPhone());
        holder.userEmail.setText(PostulatesToJob.getUserEmail());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_postulates,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userBlindName,userBlindPhone,userEmail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userBlindName=itemView.findViewById(R.id.postulateName);
            userBlindPhone=itemView.findViewById(R.id.postulatePhone);
            userEmail=itemView.findViewById(R.id.postulateEmail);
        }
    }
}
