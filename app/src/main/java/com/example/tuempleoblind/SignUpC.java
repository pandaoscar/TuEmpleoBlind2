package com.example.tuempleoblind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignUpC extends AppCompatActivity {
    private static final String numeroDeInvidentesRegistrados = "numeroDeEmpleadoresRegistrados";
    private static final String numeroDeInvidentesRegistradosTotales = "numeroDeEmpleadoresRegistradosTotales";
    private static final String collecctionReporte = "Reporte";
    private static final String documentTotales = "Totales";

    EditText campTextName;
    EditText campTextUserName;
    EditText campTextEmail;
    EditText campTextPassword1;
    EditText campTextPassword2;
    Button btnContinue;
    Button btnBack;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_c);
        mFirestore = FirebaseFirestore.getInstance();

        campTextName = findViewById(R.id.editTextNameCompanyEditDataC);
        campTextUserName = findViewById(R.id.editTextCompanyTypeEditDataC);
        campTextEmail = findViewById(R.id.editTextUserEmailSignUpC);
        campTextPassword1 = findViewById(R.id.editTextLocationEditDataC);
        campTextPassword2 = findViewById(R.id.editTextPasswordSignUpC2);

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

                final String name = campTextName.getText().toString();
                final String username = campTextUserName.getText().toString();
                final String email = campTextEmail.getText().toString();
                final String password1 = campTextPassword1.getText().toString();
                String password2 = campTextPassword2.getText().toString();

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
        Utilidad.incrementarMensual(mFirestore,collecctionReporte,numeroDeInvidentesRegistrados);
        Utilidad.incrementarTotal(mFirestore,collecctionReporte,documentTotales,numeroDeInvidentesRegistradosTotales);
    }



}