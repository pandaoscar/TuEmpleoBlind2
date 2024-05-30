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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpC extends AppCompatActivity {
    private static final String NUMERO_DE_EMPLEADORES_REGISTRADOS = "numeroDeEmpleadoresRegistrados";
    private static final String NUMERO_DE_EMPLEADORES_REGISTRADOS_TOTALES = "numeroDeEmpleadoresRegistradosTotales";
    private static final String COLLECTION_REPORTE = "Reporte";
    private static final String DOCUMENT_TOTALES = "Totales";

    EditText campTextNameC;
    EditText campTextUserNameC;
    EditText campTextEmailC;
    EditText campTextPassword1C;
    EditText campTextPassword2C;
    Button btnContinue;
    Button btnBack;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_c);
        mFirestore = FirebaseFirestore.getInstance();

        campTextNameC = findViewById(R.id.editTextNameCompanyEditDataC);
        campTextUserNameC = findViewById(R.id.editTextCompanyTypeEditDataC);
        campTextEmailC = findViewById(R.id.editTextUserEmailSignUpC);
        campTextPassword1C = findViewById(R.id.editTextLocationEditDataC);
        campTextPassword2C = findViewById(R.id.editTextPasswordSignUpC2);

        btnContinue = findViewById(R.id.buttonSaveEditDataC);
        btnBack = findViewById(R.id.buttonCancelEditDataC);



        actionContinue();

    }

    private void actionContinue() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpC.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = campTextNameC.getText().toString();
                final String username = campTextUserNameC.getText().toString();
                final String email = campTextEmailC.getText().toString();
                final String password1 = campTextPassword1C.getText().toString();
                String password2 = campTextPassword2C.getText().toString();

                if(username.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Completa todos los datos correspondientes", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!password1.equals(password2)){
                        Toast.makeText(getApplicationContext(), "Las contraseñas deben coincidir, por favor verifica nuevamente", Toast.LENGTH_SHORT).show();
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
                                            postUserNameC(name, username, email, userID);
                                        } else {
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                    }
                }
            }

            private void postUserNameC(String name, String username, String email, String userID) {
                // Guardar datos adicionales del usuario en Firestore
                Map<String, Object> userData = new HashMap<>();
                userData.put("Nombre", name);
                userData.put("Usuario", username);
                userData.put("Correo Electronico", email);
                // Agregar más campos de datos según sea necesario

                mFirestore.collection("UsernameC").document(userID)
                        .set(userData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Datos del usuario guardados correctamente", Toast.LENGTH_SHORT).show();
                                // Continuar con la lógica de tu aplicación
                                Intent intent = new Intent(getApplicationContext(), SignUpCForm.class);
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
        });
        Utilidad.incrementarMensual(mFirestore,COLLECTION_REPORTE,NUMERO_DE_EMPLEADORES_REGISTRADOS);
        Utilidad.incrementarTotal(mFirestore,COLLECTION_REPORTE,DOCUMENT_TOTALES,NUMERO_DE_EMPLEADORES_REGISTRADOS_TOTALES);
    }



}