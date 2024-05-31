package com.example.tuempleoblind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuempleoblind.adapter.PostulatesToJobAdapter;
import com.example.tuempleoblind.model.PostulatesToJob;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.recaptcha.RecaptchaClient;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ViewPostulates extends AppCompatActivity {
    private static final String FIELD_ID = "jobID";

    RecyclerView mRecycler;
    PostulatesToJobAdapter mAdapter;
    FirebaseFirestore mFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_postulates);
        if (getIntent().hasExtra(FIELD_ID)) {
            // Si se ha pasado el extra FIELD_ID, obtener su valor

            // Aquí puedes hacer lo que necesites con el ID del trabajo (jobID)
            // Por ejemplo, puedes utilizarlo para obtener los detalles del trabajo desde Firestore
        } else {
            // Si no se ha pasado el extra FIELD_ID, mostrar un mensaje de error o tomar alguna acción
            Toast.makeText(this, "No se ha proporcionado el ID del trabajo.", Toast.LENGTH_SHORT).show();
            finish(); // Finalizar la actividad si no se proporciona el ID del trabajo
        }

        String jobID = getIntent().getStringExtra(FIELD_ID);
        FirebaseFirestore.getInstance().collection("TrabajosPublicados").document(jobID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                String title = documentSnapshot.getString("title");
                TextView jobDetailTitle = findViewById(R.id.titleOfJob);
                jobDetailTitle.setText(title);
            }else {
                Toast.makeText(this, "No se encontró el trabajo.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al obtener el trabajo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
        mFirestore=FirebaseFirestore.getInstance();
        mRecycler=findViewById(R.id.listPostulatesForJob);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        Query query= mFirestore.collection("TrabajosPublicados").document(jobID).collection("Postulates");
        FirestoreRecyclerOptions<PostulatesToJob>firestoreRecyclerOptions=new FirestoreRecyclerOptions.Builder<PostulatesToJob>().setQuery(query,PostulatesToJob.class).build();
        mAdapter=new PostulatesToJobAdapter(firestoreRecyclerOptions);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}