package com.example.tuempleoblind.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuempleoblind.R;
import com.example.tuempleoblind.model.TrabajosPublicados;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TrabajosPublicadosAdapter extends FirestoreRecyclerAdapter<TrabajosPublicados, TrabajosPublicadosAdapter.viewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    String companyPublishId;
    public TrabajosPublicadosAdapter(@NonNull FirestoreRecyclerOptions<TrabajosPublicados> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull TrabajosPublicados TrabajosPublicados) { // Verificar si el ID del usuario asociado al trabajo coincide con el ID del usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        companyPublishId=currentUser.getUid().toString();
        if (TrabajosPublicados.getCompanyPublishId().equals(companyPublishId)) {
            holder.title.setText(TrabajosPublicados.getTitle());
            holder.category.setText(TrabajosPublicados.getCategory());
            holder.salary.setText(TrabajosPublicados.getSalary());
            holder.checkElevator.setText(TrabajosPublicados.getCheckElevator() ? "Sí" : "No");
            holder.checkRamp.setText(TrabajosPublicados.getCheckRamp() ? "Sí" : "No");
        } else {
            // Si no coincide, ocultar la vista del trabajo
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0)); // Opcionalmente, puedes establecer el ancho y la altura de la vista a cero para evitar problemas de diseño
        }

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_trabajos_publicados_single, parent, false);
        return new viewHolder(v);
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView title, category, salary, checkElevator, checkRamp;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.RecycleViewJobsTitle);
            category=itemView.findViewById(R.id.RecycleViewJobsCategory);
            salary=itemView.findViewById(R.id.RecycleViewJobsSalary);
            checkElevator=itemView.findViewById(R.id.RecycleViewJobsElevator);
            checkRamp=itemView.findViewById(R.id.RecycleViewJobsRamp);
        }
    }
}
