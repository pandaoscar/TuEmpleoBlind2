package com.example.tuempleoblind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActivityFormSignUpBlind extends AppCompatActivity {

    EditText campTextProfession;
    EditText campTextAddress;
    EditText campTextPhone;
    EditText campTextAbilities;
    Spinner spinnerLevelBlind;
    Button btnContinue;
    Button btnBack;
    private FirebaseFirestore mFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_sign_up_blind);

        mFirestore = FirebaseFirestore.getInstance();
        spinnerLevelBlind = findViewById(R.id.spinnerCategoryLevelBlind);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.list_level_blind,R.layout.style_spinner);
        adapter.setDropDownViewResource(R.layout.style_spinner);
        spinnerLevelBlind.setAdapter(adapter);


        campTextAbilities=findViewById(R.id.editTextAbilitiesBlind);
        campTextProfession = findViewById(R.id.editTextProfessionFormBlind);
        campTextAddress = findViewById(R.id.editTextAddressFormBlind);
        campTextPhone = findViewById(R.id.editTextPhoneNumberFormBlind);
        btnContinue = findViewById(R.id.buttonContinueFormBlind);

        actionContinue();
    }

    private void actionContinue() {
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String levelBlind = spinnerLevelBlind.getSelectedItem().toString();
                String profession = campTextProfession.getText().toString();
                String address = campTextAddress.getText().toString();
                String phone = campTextPhone.getText().toString();
                String abilities=campTextAbilities.getText().toString();

                if(levelBlind.isEmpty() || profession.isEmpty() ||phone.isEmpty()||abilities.isEmpty()|| address.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Completa todos los datos para continuar", Toast.LENGTH_SHORT).show();
                }
                else{
                    postUsernameBlind(levelBlind, profession, address, phone,abilities);
                }
            }

            private void postUsernameBlind(String levelBlind, String profesión, String location,
                                           String phonenumbre,String abilities) {
                Map<String, Object> map = new HashMap<>();
                map.put("Nivel de ceguera", levelBlind);
                map.put("Profesión", profesión);
                map.put("Dirección", location);
                map.put("Numero de Teléfono", phonenumbre);
                map.put("abilities",abilities);

                String userID = getIntent().getStringExtra("userID");

                mFirestore.collection("UsernameBlind").document(userID).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Datos del usuario guardados correctamente", Toast.LENGTH_SHORT).show();
                        // Continuar con la lógica de tu aplicación
                        Intent intent = new Intent(getApplicationContext(), HomePageBlind.class);
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