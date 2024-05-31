package com.example.tuempleoblind.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuempleoblind.R;
import com.example.tuempleoblind.model.PostulatesToJob;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

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
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull PostulatesToJob postulatesToJob) {
        holder.userBlindName.setText(postulatesToJob.getUserBlindName());
        holder.userBlindPhone.setText(postulatesToJob.getUserBlindPhone());
        holder.userEmail.setText(postulatesToJob.getUserEmail());
        holder.userBlindProfesión.setText(postulatesToJob.getProfesión());
        holder.userBlindAbilities.setText(postulatesToJob.getAbilities());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_postulates,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userBlindName;
        TextView userBlindPhone;
        TextView userEmail;
        TextView userBlindProfesión;
        TextView userBlindAbilities;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userBlindName=itemView.findViewById(R.id.postulateName);
            userBlindPhone=itemView.findViewById(R.id.postulatePhone);
            userEmail=itemView.findViewById(R.id.postulateEmail);
            userBlindProfesión=itemView.findViewById(R.id.postulateProfetion);
            userBlindAbilities=itemView.findViewById(R.id.postulateAbilities);


            Button buttonEliminatePostulate= itemView.findViewById(R.id.buttonPostulateDiscard);
            buttonEliminatePostulate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Obtener la referencia al documento del postulado que se va a eliminar
                        DocumentReference postulateRef = getSnapshots().getSnapshot(position).getReference();
                        // Eliminar el postulado de la base de datos
                        postulateRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(itemView.getContext(), "Postulado eliminado.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(itemView.getContext(), "Error al eliminar el postulado: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                }
            });
        }
    }
}
