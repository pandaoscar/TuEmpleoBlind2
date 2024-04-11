package com.example.tuempleoblind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogIn extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText campTextEmail, campTextPassword;
    Button btnConfirm, btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();

        campTextEmail = findViewById(R.id.editTextEmailLogIn);
        campTextPassword = findViewById(R.id.editTextPasswordLogIn);
        btnConfirm = findViewById(R.id.buttonConfirmLogIn);
        btnBack = findViewById(R.id.buttonBackLogIn);

        Toast.makeText(getApplicationContext(), "oñis", Toast.LENGTH_SHORT).show();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish(); // Finalizar la actividad de inicio de sesión
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login(campTextEmail.getText().toString(), campTextPassword.getText().toString());
            }
        });


    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // El inicio de sesión fue exitoso
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Obtener una instancia de Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Obtener referencias a los documentos en ambas colecciones
                            DocumentReference docRefUsernameBlind = db.collection("UsernameBlind").document(user.getUid());
                            DocumentReference docRefUsernameC = db.collection("UsernameC").document(user.getUid());

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
                        } else {
                            // El inicio de sesión falló
                            Toast.makeText(getApplicationContext(), "Inicio de sesión fallido.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}