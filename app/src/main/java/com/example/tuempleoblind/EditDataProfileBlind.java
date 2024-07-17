package com.example.tuempleoblind;

import static com.example.tuempleoblind.NavigationManager.eliminarTildes;
import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
import java.util.Locale;
import java.util.Map;

public class EditDataProfileBlind extends AppCompatActivity implements VoiceCommandController.ActivityCallback, AppState.TTSObserver{
    private static final String FIELD_COLLECTION="UsernameBlind";
    private static final String FIELD_NAME = "Nombre";
    private static final String FIELD_USERNAME = "Usuario";
    private static final String FIELD_EMAIL = "Correo";
    private static final String FIELD_PROFESSION = "Profesion";
    private static final String FIELD_ADDRESS = "Dirección";
    private static final String FIELD_PHONE = "Numero de Teléfono";
    private static final String FIELD_ABILITIES = "abilities";
    private static final String FIELD_LEVEL = "Nivel de ceguera";
    private ImageView background;
    private LottieAnimationView robotAnimation;

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
    FloatingActionButton microComand;

    private VoiceCommandController controller;
    private EditText campTextToEdit;
    private Spinner spinToEdit;
    private String campToEdit = null;
    private String newValue = null;
    private String oldValue = null;
    private SpeechRecognizer speechRecognizer;
    private int opcionSpinner;

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

        background=findViewById(R.id.backBlack);
        robotAnimation=findViewById(R.id.robot_animation);

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

        controller = VoiceCommandController.getInstance(this);
        controller.registerActivityCallback(this);
        AppState.getInstance().addTTSObserver(this);
        obtenerValoresFirestore();

        microComand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppState.getInstance().isActiveAssistant()){
                    AppState.getInstance().setActiveAssistant(false);
                    controller.sendResponseToService("Desactivado");
                }
                else{
                    AppState.getInstance().setActiveAssistant(true);
                    controller.sendResponseToService("Activado");
                }
                updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());

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

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                helpGoogle();
            }

            @Override
            public void onResults(Bundle results) {
                System.out.println("holaaaaaaaaa aaa");
                ArrayList<String> palabrasReconocidas = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (palabrasReconocidas != null && !palabrasReconocidas.isEmpty()) {
                    String palabra = palabrasReconocidas.toString().replace("[", "").replace("]", "");
                    String[] pal = palabra.split(" ");
                    palabra = String.join("", pal);
                    palabra = palabra.toLowerCase();
                    System.out.println("holaaaaaaaaa aaa" + "valor recibido "+palabra);
                    AppState.getInstance().setHelpGoogleActive(false);
                    onVoiceCommandReceived(palabra, "accion");
                    AppState.getInstance().setModoEdicionActivo(true);
                    AppState.getInstance().setActiveAssistant(true);
                    updateRobotAnimationVisibility(true);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        if (AppState.getInstance().isModoEdicionActivo()){
            String response = "¿Cúal de tus datos quieres cambiar?";
            controller.sendResponseToService(response);
        }
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());
    }
    private void updateRobotAnimationVisibility(boolean isActive){
        if (isActive) {
            background.setVisibility(View.VISIBLE);
            robotAnimation.setVisibility(View.VISIBLE);
            robotAnimation.playAnimation(); // Para iniciar la animación si es necesario
        } else {
            background.setVisibility(View.INVISIBLE);
            robotAnimation.setVisibility(View.INVISIBLE);
            robotAnimation.cancelAnimation(); // Para detener la animación si es necesario
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.unregisterActivityCallback(this);
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        AppState.getInstance().removeTTSObserver(this);
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

    @Override
    public void onVoiceCommandReceived(String command, String predictedCategory) {
        if (AppState.getInstance().isModoEdicionActivo()) {
            command = command.toLowerCase();
            command = eliminarTildes(command);
            if ((command.contains("nombre") || command.contains("telefono") || command.contains("correo") || command.contains("usuario") || (command.contains("ceguera") && command.contains("nivel")) || command.contains("profesion") || command.contains("habilidades") || command.contains("ubicacion")) && campToEdit == null){

                campToEdit = command;

                String respuesta = "¿Cual es el nuevo valor?";


                if (command.contains("correo") || campToEdit.contains("telefono")){
                    AppState.getInstance().setHelpGoogleActive(true);
                    AppState.getInstance().setActiveAssistant(false);
                    controller.sendResponseToService(respuesta);
                } else controller.sendResponseToService(respuesta);
            }
            else{
                if (campToEdit != null){
                    if (newValue != null && (command.contains("si") || command.contains("se"))){
                        String response = "Editando";
                        controller.sendResponseToService(response);
                        if (campTextToEdit != null){
                            campTextToEdit.setText(newValue);
                        }
                        if (spinToEdit != null){
                            spinToEdit.setSelection(opcionSpinner);
                        }
                        AppState.getInstance().setModoEdicionActivo(false);
                        btnSave.performClick();
                    } else if (newValue != null && command.contains("no")) {
                        String respuesta = "Entonces, ¿Cual es el nuevo valor?";
                        newValue = null;
                        if (campToEdit.contains("correo") || campToEdit.contains("telefono")){
                            AppState.getInstance().setHelpGoogleActive(true);
                            AppState.getInstance().setActiveAssistant(false);
                            controller.sendResponseToService(respuesta);
                        } else controller.sendResponseToService(respuesta);
                    }
                    else{
                        if (newValue == null){
                            editValue(command);
                        } else controller.sendResponseToService("¿Estas de acuerdo con el nuevo valor?, dí, si, o no." + newValue);
                    }
                }
                else{
                    String response = "¿Cúal de tus datos quieres cambiar?";
                    controller.sendResponseToService(response);
                }
            }

        } else {
            if (predictedCategory.startsWith("navegacion_")) {
                String destino = extractAfterUnderscore(predictedCategory);
                String respuesta = "Cambiando a " + destino;
                controller.sendResponseToService(respuesta);
                NavigationManager.navigateToDestinationBlind(this, destino, getSupportFragmentManager(), null);
            } else if (predictedCategory.startsWith("accion_")) {
                String accion = extractAfterUnderscore(predictedCategory);
                // Primero navegar a la actividad correcta si es necesario
                AppState.getInstance().setModoEdicionActivo(true);
                NavigationManager.navigateToDestinationBlind(this, accion, getSupportFragmentManager(), null);
            } else {
                if(command.equals("4p4g4d0_4ut0m4t1c0")&& predictedCategory.equals("4p4g4d0_10s3gund0s")){
                    updateRobotAnimationVisibility(false);
                }else{
                    String respuesta = "No entiendo ese comando. Por favor, intenta de nuevo.";
                    controller.sendResponseToService(respuesta);
                    updateRobotAnimationVisibility(false);}
            }
        }
    }
    private void editValue(String newVal) {
        String respuesta = null;
        // Variable de control para el switch
        String control = "";

        if (campToEdit.contains("nombre")) {
            control = "NOMBRE";
        } else if (campToEdit.contains("correo")) {
            control = "CORREO";
        } else if (campToEdit.contains("telefono")) {
            control = "TELEFONO";
        } else if ((campToEdit.contains("ceguera") && campToEdit.contains("nivel"))) {
            control = "CEGUERA";
        }else if (campToEdit.contains("usuario")) {
            control = "USUARIO";
        }else if (campToEdit.contains("profesion")) {
            control = "PROFESION";
        }else if (campToEdit.contains("habilidades")) {
            control = "HABILIDADES";
        }else if (campToEdit.contains("ubicacion")) {
            control = "UBICACION";
        }

        switch (control) {
            case "NOMBRE":
                newValue = newVal;
                oldValue = campTextName.getText().toString();
                campTextToEdit = campTextName;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue;
                controller.sendResponseToService(respuesta);
                break;
            case "CORREO":
                newValue = newVal;
                oldValue = campTextEmail.getText().toString();
                campTextToEdit = campTextEmail;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue;
                controller.sendResponseToService(respuesta);
                break;

            case "TELEFONO":
                newValue = newVal;
                oldValue = campTextPhone.getText().toString();
                campTextToEdit = campTextPhone;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue;
                controller.sendResponseToService(respuesta);
                break;
            case "CEGUERA":
                respuesta = verifyTypeSpinner(newVal);
                oldValue = spinnerLevelBlind.getSelectedItem().toString();
                spinToEdit = spinnerLevelBlind;
                controller.sendResponseToService(respuesta);
                break;
            case "USUARIO":
                newValue = newVal;
                oldValue = campTextUserName.getText().toString();
                campTextToEdit = campTextUserName;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue;
                controller.sendResponseToService(respuesta);
                break;
            case "PROFESION":
                newValue = newVal;
                oldValue = campTextProfession.getText().toString();
                campTextToEdit = campTextProfession;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue;
                controller.sendResponseToService(respuesta);
                break;
            case "HABILIDADES":
                newValue = newVal;
                oldValue = campTextAbilities.getText().toString();
                campTextToEdit = campTextAbilities;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue;
                controller.sendResponseToService(respuesta);
                break;
            case "UBICACION":
                newValue = newVal;
                oldValue = campTextAddress.getText().toString();
                campTextToEdit = campTextAddress;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue;
                controller.sendResponseToService(respuesta);
                break;
        }
    }
    private String verifyTypeSpinner(String command) {
        if (command.contains("uno") || command.contains("baja") || command.contains("vision")){
            newValue = "Baja Visión";
            opcionSpinner = 1;
            return "¿Estas seguro? Colocaras Baja Visión, dí, si, o no.";
        } else if (command.contains("dos") || command.contains("parcial")) {
            newValue = "Ceguera Parcial";
            opcionSpinner = 2;
            return "¿Estas seguro? Colocaras Ceguera Parcial, dí, si, o no.";
        } else if (command.contains("tres") || command.contains("legal")) {
            newValue = "Ceguera Legal";
            opcionSpinner = 3;
            return "¿Estas seguro? Colocaras Ceguera Legal, dí, si, o no.";
        } else if (command.contains("cuatro") || command.contains("total")) {
            newValue = "Ceguera Total";
            opcionSpinner = 4;
            return "¿Estas seguro? Colocaras Ceguera Total, dí, si, o no.";
        } else {
            newValue = null;
            return "No entendí, prueba de nuevo ";
        }
    }
    private void helpGoogle() {
        AppState.getInstance().setHelpGoogleActive(true);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla algo...");

        try {
            speechRecognizer.startListening(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Tu dispositivo no soporta el reconocimiento de voz", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTTSCompleted() {

    }
}
