package com.example.tuempleoblind.adapter;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuempleoblind.HomeCFragment;
import com.example.tuempleoblind.NewJob;
import com.example.tuempleoblind.R;
import com.example.tuempleoblind.ViewPostulates;
import com.example.tuempleoblind.model.TrabajosPublicados;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull TrabajosPublicados TrabajosPublicados) { // Verificar si el ID del usuario asociado al trabajo coincide con el ID del usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        companyPublishId=currentUser.getUid().toString();
        if (TrabajosPublicados.getCompanyPublishId().equals(companyPublishId)) {
            DocumentSnapshot documentSnapshot= getSnapshots().getSnapshot(holder.getAdapterPosition());
            final String id=documentSnapshot.getId();
            holder.title.setText(TrabajosPublicados.getTitle());
            holder.category.setText(TrabajosPublicados.getCategory());
            holder.salary.setText(TrabajosPublicados.getSalary());
            holder.checkElevator.setText(TrabajosPublicados.getCheckElevator() ? "Sí" : "No");
            holder.checkRamp.setText(TrabajosPublicados.getCheckRamp() ? "Sí" : "No");
            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteJob(id);

                }
            });
            holder.btn_viewPostulates.setOnClickListener(new View.OnClickListener() {
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
        } else {
            // Si no coincide, ocultar la vista del trabajo
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0)); // Opcionalmente, puedes establecer el ancho y la altura de la vista a cero para evitar problemas de diseño
        }

    }

    private void deleteJob(String id) {
        mFiestore.collection("TrabajosPublicados").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
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
        void onViewPostulatesClick(int position);
    }
    private OnViewPostulatesClickListener listener;

    public void setOnViewPostulatesClick(OnViewPostulatesClickListener listener) {
        this.listener = listener;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView title, category, salary, checkElevator, checkRamp;
        Button btn_delete,btn_viewPostulates;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.RecycleViewJobsTitle);
            category=itemView.findViewById(R.id.RecycleViewJobsCategory);
            salary=itemView.findViewById(R.id.RecycleViewJobsSalary);
            checkElevator=itemView.findViewById(R.id.RecycleViewJobsElevator);
            checkRamp=itemView.findViewById(R.id.RecycleViewJobsRamp);
            btn_delete=itemView.findViewById(R.id.buttonDeleteJob);
            btn_viewPostulates=itemView.findViewById(R.id.buttonViewPostulatesToJob);

        }
    }
}
