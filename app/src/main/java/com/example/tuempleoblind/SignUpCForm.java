package com.example.tuempleoblind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpCForm extends AppCompatActivity {
    EditText campTextNameCompany, campTextTypeCompany, campTextLocation, campTextWebPag;
    Button btnContinue, btnBack;
    private FirebaseFirestore mFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_cform);

        mFirestore = FirebaseFirestore.getInstance();
        campTextNameCompany = findViewById(R.id.editTextNameCompanyFormC);
        campTextTypeCompany = findViewById(R.id.editTextCompanyTypeFormC);
        campTextLocation = findViewById(R.id.editTextLocationFormC);
        campTextWebPag = findViewById(R.id.editTextWebPagFormC);
        btnContinue = findViewById(R.id.buttonContinueFormC);

        actionContinue();
    }

    private void actionContinue() {
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String NameCompany = campTextNameCompany.getText().toString();
                String TypeCompany = campTextTypeCompany.getText().toString();
                String Location = campTextLocation.getText().toString();
                String WebPag = campTextWebPag.getText().toString();

                if(NameCompany.isEmpty() || TypeCompany.isEmpty() || Location.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Completa todos los datos correspondientes", Toast.LENGTH_SHORT).show();
                }
                else{
                    postUsernameBlind(NameCompany, TypeCompany, Location, WebPag);
                }
            }

            private void postUsernameBlind(String nameCompany, String typeCompany, String location, String webPag) {
                Map<String, Object> map = new HashMap<>();
                map.put("Nombre de la compañia", nameCompany);
                map.put("Tipo de compañia", typeCompany);
                map.put("Ubicación", location);
                map.put("Pagina web", webPag);

                String userID = getIntent().getStringExtra("userID");

                mFirestore.collection("UsernameC").document(userID).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Datos del usuario guardados correctamente", Toast.LENGTH_SHORT).show();
                        // Continuar con la lógica de tu aplicación
                        Intent intent = new Intent(getApplicationContext(), companyHome.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });
    }
}