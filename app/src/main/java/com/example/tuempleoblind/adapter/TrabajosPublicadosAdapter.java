package com.example.tuempleoblind.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuempleoblind.R;
import com.example.tuempleoblind.model.TrabajosPublicados;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TrabajosPublicadosAdapter extends FirestoreRecyclerAdapter<TrabajosPublicados, TrabajosPublicadosAdapter.viewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    String companyPublishId;
    private FirebaseFirestore mFiestore=FirebaseFirestore.getInstance();

    Fragment fragment;

    public TrabajosPublicadosAdapter(@NonNull FirestoreRecyclerOptions<TrabajosPublicados> options, Fragment fragment) {
        super(options);
        this.fragment=fragment;
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull TrabajosPublicados trabajosPublicados) { // Verificar si el ID del usuario asociado al trabajo coincide con el ID del usuario actual
            DocumentSnapshot documentSnapshot= getSnapshots().getSnapshot(holder.getAdapterPosition());
            final String id=documentSnapshot.getId();
            holder.title.setText(trabajosPublicados.getTitle());
            holder.category.setText(trabajosPublicados.getCategory());
            holder.salary.setText(trabajosPublicados.getSalary());
            // Si getCheckElevator devuelve Boolean
            boolean checkElevator = trabajosPublicados.getCheckElevator() != null && trabajosPublicados.getCheckElevator();
            holder.checkElevator.setText(checkElevator ? "Sí" : "No");
            // Si getCheckRamp devuelve Boolean
            boolean checkRamp = trabajosPublicados.getCheckRamp() != null && trabajosPublicados.getCheckRamp();
            holder.checkRamp.setText(checkRamp ? "Sí" : "No");
            holder.btnDeleteJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteJob(id);

                }
            });
            holder.btnViewPostulates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position= holder.getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onViewPostulatesClick(position);
                        }
                    }
                }
            });

    }

    private void deleteJob(String id) {
        mFiestore.collection("TrabajosPublicados").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // default implementation ignored
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // default implementation ignored
            }
        });
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_trabajos_publicados_single, parent, false);
        return new viewHolder(v);
    }
    public interface OnViewPostulatesClickListener {
        void onPermissionsGranted(int requestCode, @NonNull String[] perms);

        void onPermissionsDenied(int requestCode, @NonNull String[] perms);

        void onViewPostulatesClick(int position);
    }
    private OnViewPostulatesClickListener listener;

    public void setOnViewPostulatesClick(OnViewPostulatesClickListener listener) {
        this.listener = listener;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView category;
        TextView salary;
        TextView checkElevator;
        TextView checkRamp;
        Button btnDeleteJob;
        Button btnViewPostulates;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.RecycleViewJobsTitle);
            category=itemView.findViewById(R.id.RecycleViewJobsCategory);
            salary=itemView.findViewById(R.id.RecycleViewJobsSalary);
            checkElevator=itemView.findViewById(R.id.RecycleViewJobsElevator);
            checkRamp=itemView.findViewById(R.id.RecycleViewJobsRamp);
            btnDeleteJob =itemView.findViewById(R.id.buttonDeleteJob);
            btnViewPostulates =itemView.findViewById(R.id.buttonViewPostulatesToJob);

        }
    }
}
