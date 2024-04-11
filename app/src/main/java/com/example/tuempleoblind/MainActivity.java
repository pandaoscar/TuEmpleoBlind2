package com.example.tuempleoblind;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    Button btnFindJob, btnFindHire, btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFindJob = findViewById(R.id.buttonFindJob);
        btnFindHire = findViewById(R.id.buttonFindHire);
        btnLogIn = findViewById(R.id.buttonLogIn);
        login();
        hire();
        job();
    }

    private void login() {
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LogIn.class));
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Verificar si el usuario ya está autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Obtener una instancia de Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Obtener referencias a los documentos en ambas colecciones
            DocumentReference docRefUsernameBlind = db.collection("UsernameBlind").document(currentUser.getUid());
            DocumentReference docRefUsernameC = db.collection("UsernameC").document(currentUser.getUid());

            // Verificar si existe el documento en UsernameBlind
            docRefUsernameBlind.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentBlind = task.getResult();
                        if (documentBlind.exists()) {
                            // El usuario pertenece a la colección UsernameBlind
                            startActivity(new Intent(getApplicationContext(), HomePageBlind.class));
                            finish(); // Finalizar la actividad de inicio de sesión
                        } else {
                            // Verificar si existe el documento en UsernameC
                            docRefUsernameC.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentC = task.getResult();
                                        if (documentC.exists()) {
                                            // El usuario pertenece a la colección UsernameC
                                            startActivity(new Intent(getApplicationContext(), companyHome.class));
                                            finish(); // Finalizar la actividad de inicio de sesión
                                        } else {
                                            // No se encontró el usuario en ninguna colección
                                            Toast.makeText(getApplicationContext(), "Usuario no encontrado en ninguna colección.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Error al obtener el documento de UsernameC
                                        Toast.makeText(getApplicationContext(), "Error: No se pudo obtener el documento del usuario de UsernameC.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        // Error al obtener el documento de UsernameBlind
                        Toast.makeText(getApplicationContext(), "Error: No se pudo obtener el documento del usuario de UsernameBlind.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


    private void hire() {
        btnFindHire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpC.class));
                finish();
            }
        });
    }

    private void job() {
        btnFindJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpBlind.class));
                finish();
            }
        });
    }
}