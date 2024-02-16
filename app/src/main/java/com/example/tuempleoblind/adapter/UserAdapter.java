package com.example.tuempleoblind.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuempleoblind.R;
import com.example.tuempleoblind.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class UserAdapter extends FirestoreRecyclerAdapter <User, UserAdapter.ViewHolder>  {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User User) {
        holder.Usuario.setText(User.getUsuario());
        holder.Contraeña.setText(User.getContraeña());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user_single, parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Usuario, Contraeña;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Usuario= itemView.findViewById(R.id.user);
            Contraeña=itemView.findViewById(R.id.passw);
        }
    }
}
