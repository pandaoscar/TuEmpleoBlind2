package com.example.tuempleoblind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditDataProfileBlind extends AppCompatActivity {

    EditText campTextName, campTextUserName, campTextEmail, campTextProfession, campTextAddress, campTextPhone, campTextAbilities;
    Spinner spinnerLevelBlind;
    Button btnSave, btnCancel;
    FirebaseFirestore mFirestore;
    private FirebaseAuth mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data_profile_blind);

        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance();

        campTextName = findViewById(R.id.editTextNameEditDataBlind);
        campTextUserName = findViewById(R.id.editTextUserNameEditDataBlind);
        campTextEmail = findViewById(R.id.editTextEmailEditDataBlind);

        campTextProfession = findViewById(R.id.editTextProfessionEditDataBlind);
        campTextAddress = findViewById(R.id.editTextAddressEditDataBlind);
        campTextPhone = findViewById(R.id.editTextPhoneNumberEditDataBlind);
        campTextAbilities = findViewById(R.id.editTextAbilitiesEditDataBlind);
        spinnerLevelBlind = findViewById(R.id.spinnerCategoryLevelEditDataBlind);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.list_level_blind,R.layout.style_spinner);
        adapter.setDropDownViewResource(R.layout.style_spinner);
        spinnerLevelBlind.setAdapter(adapter);

        campTextName.addTextChangedListener(textWatcher);
        campTextUserName.addTextChangedListener(textWatcher);
        campTextEmail.addTextChangedListener(textWatcher);
        campTextProfession.addTextChangedListener(textWatcher);
        campTextAddress.addTextChangedListener(textWatcher);
        campTextPhone.addTextChangedListener(textWatcher);
        campTextAbilities.addTextChangedListener(textWatcher);

        btnSave = findViewById(R.id.buttonSaveEditDataBlind);
        btnCancel = findViewById(R.id.buttonCancelEditDataBlind);

        btnSave.setEnabled(false);
        btnSave.setBackgroundResource(R.drawable.btn_disabled);
        btnSave.setTextColor(getResources().getColor(R.color.litle_color));
        obtenerValoresFirestore();


        spinnerLevelBlind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                verificarCambios();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = campTextName.getText().toString();
                final String username = campTextUserName.getText().toString();
                final String email = campTextEmail.getText().toString();
                final String profession = campTextProfession.getText().toString();
                final String address = campTextAddress.getText().toString();
                final String phone = campTextPhone.getText().toString();
                final String abilities = campTextAbilities.getText().toString();
                final String level;
                if (spinnerLevelBlind.getSelectedItem() != null) {
                    level = spinnerLevelBlind.getSelectedItem().toString();
                } else {
                    // Manejar el caso en el que no hay ningún elemento seleccionado en el Spinner
                    level = "";
                }


                if(name.isEmpty() || username.isEmpty() || email.isEmpty() || profession.isEmpty() || address.isEmpty() || phone.isEmpty() || abilities.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Completa todos los datos correspondientes", Toast.LENGTH_SHORT).show();
                }
                else{
                    postUserBlind(name, username, email, profession, address, phone, abilities, level, mUser.getUid());
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void postUserBlind(String name, String username, String email, String profession, String address, String phone, String abilities, String level, String userID) {
        Map<String, Object> map = new HashMap<>();
        map.put("Nombre", name);
        map.put("Usuario", username);
        map.put("Correo", email);
        map.put("Profesión", profession);
        map.put("Dirección", address);
        map.put("Numero de Teléfono", phone);
        map.put("abilities", abilities);
        map.put("Nivel de ceguera", level);

        mFirestore.collection("UsernameBlind").document(userID)
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Datos del usuario guardados correctamente", Toast.LENGTH_SHORT).show();
                        // Continuar con la lógica de tu aplicación
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class); // Cambia "MainActivity" por la actividad a la que quieras regresar
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

    private void obtenerValoresFirestore() {
        FirebaseUser user = mUser.getCurrentUser();
        DocumentReference docRef = mFirestore.collection("UsernameBlind").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("Nombre");
                    String username = documentSnapshot.getString("Usuario");
                    String email = documentSnapshot.getString("Correo");
                    String profession = documentSnapshot.getString("Profesión");
                    String address = documentSnapshot.getString("Dirección");
                    String phone = documentSnapshot.getString("Numero de Teléfono");
                    String abilities = documentSnapshot.getString("abilities");
                    String level = documentSnapshot.getString("Nivel de ceguera");

                    campTextName.setText(name);
                    campTextUserName.setText(username);
                    campTextEmail.setText(email);
                    campTextProfession.setText(profession);
                    campTextAddress.setText(address);
                    campTextPhone.setText(phone);
                    campTextAbilities.setText(abilities);

                    Toast.makeText(EditDataProfileBlind.this, level, Toast.LENGTH_SHORT).show();
                    if (level != null){
                        if(level.equals("Baja Visión")){
                            spinnerLevelBlind.setSelection(0);
                        }else{
                            if (level.equals("Cegera Parcial")){
                                spinnerLevelBlind.setSelection(1);
                            }else{
                                if (level.equals("Cegera Legal")){
                                    spinnerLevelBlind.setSelection(2);
                                }else{
                                    spinnerLevelBlind.setSelection(3);
                                }
                            }
                        }
                    }


                    // Agregar un TextWatcher a cada EditText
                    campTextName.addTextChangedListener(textWatcher);
                    campTextUserName.addTextChangedListener(textWatcher);
                    campTextEmail.addTextChangedListener(textWatcher);
                    campTextProfession.addTextChangedListener(textWatcher);
                    campTextAddress.addTextChangedListener(textWatcher);
                    campTextPhone.addTextChangedListener(textWatcher);
                    campTextAbilities.addTextChangedListener(textWatcher);

                    verificarCambios();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            verificarCambios();
        }
    };

    // Método para verificar si ha habido cambios en los EditText
    private void verificarCambios() {
        // Obtener los valores actuales de los EditText y el Spinner
        String emailEdit = campTextEmail.getText().toString();
        String nameEdit = campTextName.getText().toString();
        String usernameEdit = campTextUserName.getText().toString();
        String professionEdit = campTextProfession.getText().toString();
        String addressEdit = campTextAddress.getText().toString();
        String phoneEdit = campTextPhone.getText().toString();
        String abilitiesEdit = campTextAbilities.getText().toString();
        String levelSpinner = "";
        if (spinnerLevelBlind.getSelectedItem() != null) {
            levelSpinner = spinnerLevelBlind.getSelectedItem().toString();
        }


        // Obtener los valores almacenados en Firestore
        FirebaseUser user = mUser.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = mFirestore.collection("UsernameBlind").document(user.getUid());
            String finalLevelSpinner = levelSpinner;
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Obtener los valores del documento
                        String emailFire = documentSnapshot.getString("Correo");
                        String nameFire = documentSnapshot.getString("Nombre");
                        String usernameFire = documentSnapshot.getString("Usuario");
                        String professionFire = documentSnapshot.getString("Profesión");
                        String addressFire = documentSnapshot.getString("Dirección");
                        String phoneFire = documentSnapshot.getString("Numero de Teléfono");
                        String abilitiesFire = documentSnapshot.getString("abilities");
                        String levelFire = documentSnapshot.getString("Nivel de ceguera");

                        // Verificar si hay cambios en los EditText y el Spinner
                        if (!emailEdit.equals(emailFire) || !nameEdit.equals(nameFire) ||
                                !usernameEdit.equals(usernameFire) || !professionEdit.equals(professionFire) ||
                                !addressEdit.equals(addressFire) || !phoneEdit.equals(phoneFire) ||
                                !abilitiesEdit.equals(abilitiesFire) || !finalLevelSpinner.equals(levelFire)) {
                            // Si hay cambios, habilitar el botón
                            btnSave.setEnabled(true);
                            btnSave.setBackgroundResource(R.drawable.btn_orange);
                            btnSave.setTextColor(getResources().getColor(R.color.ghost_white));
                        } else {
                            // Si no hay cambios, deshabilitar el botón
                            btnSave.setEnabled(false);
                            btnSave.setTextColor(getResources().getColor(R.color.litle_color));
                            btnSave.setBackgroundResource(R.drawable.btn_disabled);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}