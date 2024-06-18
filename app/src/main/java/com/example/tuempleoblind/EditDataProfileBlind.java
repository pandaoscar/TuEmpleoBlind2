package com.example.tuempleoblind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class EditDataProfileBlind extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private static final String FIELD_COLLECTION="UsernameBlind";
    private static final String FIELD_NAME = "Nombre";
    private static final String FIELD_USERNAME = "Usuario";
    private static final String FIELD_EMAIL = "Correo";
    private static final String FIELD_PROFESSION = "Profesion";
    private static final String FIELD_ADDRESS = "Dirección";
    private static final String FIELD_PHONE = "Numero de Teléfono";
    private static final String FIELD_ABILITIES = "abilities";
    private static final String FIELD_LEVEL = "Nivel de ceguera";

    EditText campTextName;
    EditText campTextUserName;
    EditText campTextEmail;
    EditText campTextProfession;
    EditText campTextAddress;
    EditText campTextPhone;
    EditText campTextAbilities;
    Spinner spinnerLevelBlind;
    Button btnSave;
    Button btnCancel;
    FirebaseFirestore mFirestore;
    private FirebaseAuth mUser;
    private static final int CODIGO_RECONOCIMIENTO_VOZ = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;
    FloatingActionButton microComand;
    private BroadcastReceiver voiceCommandReceiver;

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.list_level_blind, R.layout.style_spinner);
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
        microComand = findViewById(R.id.floatingButtonComands);

        btnSave.setEnabled(false);
        btnSave.setBackgroundResource(R.drawable.btn_disabled);
        btnSave.setTextColor(getResources().getColor(R.color.litle_color));
        obtenerValoresFirestore();

        microComand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkAndRequestPermissions();

            }
        });

        spinnerLevelBlind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                verificarCambios();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // default implementation ignored
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
        voiceCommandReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getStringExtra("command");
                handleVoiceCommand(command);
            }
        };
        IntentFilter filter = new IntentFilter("VOICE_COMMAND");
        registerReceiver(voiceCommandReceiver, filter);

    }
    private void handleVoiceCommand(String command) {
        System.out.println("epaaa "+command);
        switch (command.toLowerCase()) {

            case "editar primer texto":
                campTextName.setText("Texto editado por voz");
                break;
            case "editar segundo texto":
                campTextUserName.setText("Texto editado por voz");
                break;
            case "guardar":
                btnSave.performClick();
                break;
            case "cancelar":
                btnCancel.performClick();
                break;
            // Agrega más casos según tus necesidades
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(voiceCommandReceiver);
    }

    private void checkAndRequestPermissions() {
        if (EasyPermissions.hasPermissions(getApplicationContext(), android.Manifest.permission.RECORD_AUDIO)) {
            // Permission already granted, perform operation
            Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
            iniciarReconocimientoVoz();
        } else {
            // Request permissions
            EasyPermissions.requestPermissions(this, "Porfavor acepta los permisos del microfono", PERMISSION_REQUEST_CODE, Manifest.permission.RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission is denied
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // Permission granted, handle accordingly
        Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // Permission denied, handle accordingly
        Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
    }



    private void iniciarReconocimientoVoz() {
        // Crea un Intent para el reconocimiento de voz
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Configura el idioma para el reconocimiento (puedes cambiarlo según tus necesidades)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Configura un mensaje para el usuario
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di algo...");
        // Inicia la actividad de reconocimiento de voz y espera los resultados
        startActivityForResult(intent, CODIGO_RECONOCIMIENTO_VOZ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_RECONOCIMIENTO_VOZ) {
            if (resultCode == RESULT_OK && data != null) {
                // Obtiene la lista de palabras reconocidas
                ArrayList<String> palabrasReconocidas = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (palabrasReconocidas != null && palabrasReconocidas.isEmpty()) {
                    // Guarda la primera palabra reconocida en un String
                    String palabra = palabrasReconocidas.toString().replace("[", "").replace("]", "");
                    NavigationManager.navigateToDestinationBlind(getApplicationContext(), palabra, getSupportFragmentManager(), null);
                }
            } else {
                // Mensaje de error si el reconocimiento de voz no fue exitoso
                Toast.makeText(getApplicationContext(), "Error en el reconocimiento de comandos", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void postUserBlind(String name, String username, String email, String profession, String address, String phone, String abilities, String level, String userID) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_NAME, name);
        map.put(FIELD_USERNAME, username);
        map.put(FIELD_EMAIL, email);
        map.put(FIELD_PROFESSION, profession);
        map.put(FIELD_ADDRESS, address);
        map.put(FIELD_PHONE, phone);
        map.put(FIELD_ABILITIES, abilities);
        map.put(FIELD_LEVEL, level);

        mFirestore.collection(FIELD_COLLECTION).document(userID)
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
        DocumentReference docRef = mFirestore.collection(FIELD_COLLECTION).document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString(FIELD_NAME);
                    String username = documentSnapshot.getString(FIELD_USERNAME);
                    String email = documentSnapshot.getString(FIELD_EMAIL);
                    String profession = documentSnapshot.getString(FIELD_PROFESSION);
                    String address = documentSnapshot.getString(FIELD_ADDRESS);
                    String phone = documentSnapshot.getString(FIELD_PHONE);
                    String abilities = documentSnapshot.getString(FIELD_ABILITIES);
                    String level = documentSnapshot.getString(FIELD_LEVEL);

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
            // default implementation ignored
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // default implementation ignored
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
            DocumentReference docRef = mFirestore.collection(FIELD_COLLECTION).document(user.getUid());
            String finalLevelSpinner = levelSpinner;
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Obtener los valores del documento
                        String emailFire = documentSnapshot.getString(FIELD_EMAIL);
                        String nameFire = documentSnapshot.getString(FIELD_NAME);
                        String usernameFire = documentSnapshot.getString(FIELD_USERNAME);
                        String professionFire = documentSnapshot.getString(FIELD_PROFESSION);
                        String addressFire = documentSnapshot.getString(FIELD_ADDRESS);
                        String phoneFire = documentSnapshot.getString(FIELD_PHONE);
                        String abilitiesFire = documentSnapshot.getString(FIELD_ABILITIES);
                        String levelFire = documentSnapshot.getString(FIELD_LEVEL);

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
