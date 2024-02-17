package com.example.tuempleoblind;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpBlind extends AppCompatActivity {

    Button btnSignUpSend;
    EditText campTextSignUpUsername, campTextPasswordSignUp1, campTextPasswordSignUp2;
    private FirebaseFirestore mfirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_blind);

        mfirestore = FirebaseFirestore.getInstance();

        btnSignUpSend = findViewById(R.id.buttonSignUpSendBlind);
        campTextSignUpUsername = findViewById(R.id.editTextSignUpUsernameBlind);
        campTextPasswordSignUp1 = findViewById(R.id.editTextPasswordSignUpBlind1);
        campTextPasswordSignUp2 = findViewById(R.id.editTextPasswordSignUpBlind2);

        btnSignUpSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = campTextSignUpUsername.getText().toString();
                String password1 = campTextPasswordSignUp1.getText().toString();
                String password2 = campTextPasswordSignUp2.getText().toString();

                if(username.isEmpty() || password1.isEmpty() || password2.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Completa todos los datos correspondientes", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!password1.equals(password2)){
                        Toast.makeText(getApplicationContext(), "Las contraseñas deben coincidir, porfavor verifica nuevamente", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        postUsernameBlind(username,password1);
                    }
                }
            }
        });


    }

    private void postUsernameBlind(String username, String password1) {

        Map<String, Object> map = new HashMap<>();
        map.put("Usuario", username);
        map.put("Contraeña", password1);
        mfirestore.collection("UsernameBlind").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al ingresar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}