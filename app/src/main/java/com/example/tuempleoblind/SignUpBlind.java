package com.example.tuempleoblind;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpBlind extends AppCompatActivity {

    EditText campTextName, campTextUserName, campTextEmail, campTextPassword1, campTextPassword2;
    Button btnContinue, btnBack;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_blind);

        mFirestore = FirebaseFirestore.getInstance();

        btnContinue = findViewById(R.id.buttonSignUpSendBlind);
        btnBack = findViewById(R.id.buttonBackSignUpBlind);
        campTextName = findViewById(R.id.editTextNameSignUpBlind);
        campTextUserName = findViewById(R.id.editTextSignUpUsernameBlind);
        campTextEmail = findViewById(R.id.editTextUserEmailSignUpBlind);
        campTextPassword1 = findViewById(R.id.editTextPasswordSignUpBlind1);
        campTextPassword2 = findViewById(R.id.editTextPasswordSignUpBlind2);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = ProgressDialog.show(getApplicationContext(), "", "Registrando usuario...", true);
                progressDialog.setCancelable(false);

                final String name = campTextName.getText().toString();
                final String username = campTextUserName.getText().toString();
                final String email = campTextEmail.getText().toString();
                final String password1 = campTextPassword1.getText().toString();
                final String password2 = campTextPassword2.getText().toString();

                if(name.isEmpty() || username.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Completa todos los datos correspondientes", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else{
                    if(!password1.equals(password2)){
                        Toast.makeText(getApplicationContext(), "Las contraseñas deben coincidir, porfavor verifica nuevamente", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    else{
                        // Registrar usuario en Firebase Authentication
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password1)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Registro exitoso, obtener el ID único del usuario
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            String userID = user.getUid();
                                            // Guardar datos adicionales del usuario en Firestore
                                            postUsernameBlind(name, username, email, userID);
                                        } else {
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                            }
                                        },2000);
                                    }
                                });
                    }
                }
            }
        });


    }

    private void postUsernameBlind(String name, String username, String email, String userID) {

        Map<String, Object> map = new HashMap<>();
        map.put("Nombre", name);
        map.put("Usuario", username);
        map.put("Correo", email);

        mFirestore.collection("UsernameBlind").document(userID)
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Datos del usuario guardados correctamente", Toast.LENGTH_SHORT).show();
                        // Continuar con la lógica de tu aplicación
                        Intent intent = new Intent(getApplicationContext(), SignUpBlindForm.class);
                        intent.putExtra("userID", userID);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}