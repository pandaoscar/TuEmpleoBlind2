package com.example.tuempleoblind;

import static android.app.PendingIntent.getActivity;

import static com.example.tuempleoblind.NavigationManager.eliminarTildes;
import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tuempleoblind.tensorflowlite.TextClassifier;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;

public class EditDataProfileC extends AppCompatActivity implements VoiceCommandController.ActivityCallback, AppState.TTSObserver {
    private static final String FIELD_COLLECTION_C="UsernameC";
    private static final String FIELD_NAME_C = "Nombre";
    private static final String FIELD_USERNAME_C = "Usuario";
    private static final String FIELD_EMAIL_C = "Correo Electronico";
    private static final String FIELD_COMPANY_NAME_C = "Nombre de la compañia";
    private static final String FIELD_COMPANY_TYPE_C = "Tipo de compañia";
    private static final String FIELD_LOCATION_C = "Ubicación";
    private static final String FIELD_WEB_PAGE_C = "Pagina web";

    EditText campTextName;
    EditText campTextUserName;
    EditText campTextEmail;
    EditText campTextNameCompany;
    EditText campTextCompanyType;
    EditText campTextLocation;
    EditText campTextWebPag;
    Button btnSave;
    Button btnCancel;
    FloatingActionButton microComand;
    private VoiceCommandController controller;
    private LottieAnimationView robotAnimation;
    private ImageView background;
    FirebaseFirestore mFirestore;
    private FirebaseAuth mUser;
    private String campToEdit = null;
    private EditText campTextToEdit;
    private String newValue = null;
    private String oldValue = null;
    private BroadcastReceiver speechRecognitionResultsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Estoy en broadcast");
            if (intent != null && "SpeechRecognitionResults".equals(intent.getAction())) {
                String recognizedText = intent.getStringExtra("recognizedText");
                onVoiceCommandReceived(recognizedText, "accion");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data_profile_c);

        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance();


        campTextName = findViewById(R.id.editTextNameEditDataC);
        campTextUserName = findViewById(R.id.editTextUserNameEditDataC);
        campTextEmail = findViewById(R.id.editTextEmailEditDataC);
        campTextNameCompany = findViewById(R.id.editTextNameCompanyEditDataC);
        campTextCompanyType = findViewById(R.id.editTextCompanyTypeEditDataC);
        campTextLocation = findViewById(R.id.editTextLocationEditDataC);
        campTextWebPag = findViewById(R.id.editTextWebPagEditDataC);

        campTextNameCompany.addTextChangedListener(textWatcher);

        btnSave = findViewById(R.id.buttonSaveEditDataC);
        btnCancel = findViewById(R.id.buttonCancelEditDataC);
        microComand = findViewById(R.id.floatingButtonComands);

        btnSave.setEnabled(false);
        btnSave.setBackgroundResource(R.drawable.btn_disabled);
        btnSave.setTextColor(getResources().getColor(R.color.litle_color));
        obtenerValoresFirestore();

        background=findViewById(R.id.backBlack);
        robotAnimation=findViewById(R.id.robot_animation);
        controller = VoiceCommandController.getInstance(this);
        controller.registerActivityCallback(this);
        AppState.getInstance().addTTSObserver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(speechRecognitionResultsReceiver, new IntentFilter("SpeechRecognitionResults"));

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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = campTextName.getText().toString();
                final String username = campTextUserName.getText().toString();
                final String email = campTextEmail.getText().toString();
                final String nameCompany = campTextNameCompany.getText().toString();
                final String typeCompany = campTextCompanyType.getText().toString();
                final String location = campTextLocation.getText().toString();
                final String webPag = campTextWebPag.getText().toString();

                if(name.isEmpty() || username.isEmpty() || email.isEmpty() || nameCompany.isEmpty() || typeCompany.isEmpty() || location.isEmpty() || webPag.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Completa todos los datos correspondientes", Toast.LENGTH_SHORT).show();

                }
                else{
                    postUsernameC(name, username, email, nameCompany, typeCompany, location, webPag, mUser.getUid());
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(speechRecognitionResultsReceiver);
    }
    private void postUsernameC(String name, String username, String email, String nameCompany, String typeCompany, String location, String webPag, String userID) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_NAME_C, name);
        map.put(FIELD_USERNAME_C, username);
        map.put(FIELD_EMAIL_C, email);
        map.put(FIELD_COMPANY_NAME_C, nameCompany);
        map.put(FIELD_WEB_PAGE_C, webPag);
        map.put(FIELD_COMPANY_TYPE_C, typeCompany);
        map.put(FIELD_LOCATION_C, location);

        mFirestore.collection(FIELD_COLLECTION_C).document(userID)
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (AppState.getInstance().isActiveAssistant()){
                            controller.sendResponseToService("Editando");
                        }
                        Toast.makeText(getApplicationContext(), "Datos del usuario guardados correctamente", Toast.LENGTH_SHORT).show();
                        // Continuar con la lógica de tu aplicación
                        Intent intent = new Intent(getApplicationContext(), CompanyHome.class);
                        intent.putExtra("userID", userID);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (AppState.getInstance().isActiveAssistant()){
                            controller.sendResponseToService("No se completo correctamente, intenta de nuevo");
                        }
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void obtenerValoresFirestore() {
        // Obtener el documento deseado de Firestore
        FirebaseUser user = mUser.getCurrentUser();
        DocumentReference docRef = mFirestore.collection(FIELD_COLLECTION_C).document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener los valores del documento
                    String email = documentSnapshot.getString(FIELD_EMAIL_C);
                    String name = documentSnapshot.getString(FIELD_NAME_C);
                    String nameCompany = documentSnapshot.getString(FIELD_COMPANY_NAME_C);
                    String webPag = documentSnapshot.getString(FIELD_WEB_PAGE_C);
                    String typeCompany = documentSnapshot.getString(FIELD_COMPANY_TYPE_C);
                    String location = documentSnapshot.getString(FIELD_LOCATION_C);
                    String username = documentSnapshot.getString(FIELD_USERNAME_C);

                    // Establecer los valores en los EditText
                    campTextEmail.setText(email);
                    campTextName.setText(name);
                    campTextNameCompany.setText(nameCompany);
                    campTextWebPag.setText(webPag);
                    campTextCompanyType.setText(typeCompany);
                    campTextLocation.setText(location);
                    campTextUserName.setText(username);

                    // Agregar un TextWatcher a cada EditText
                    campTextEmail.addTextChangedListener(textWatcher);
                    campTextName.addTextChangedListener(textWatcher);
                    campTextNameCompany.addTextChangedListener(textWatcher);
                    campTextWebPag.addTextChangedListener(textWatcher);
                    campTextCompanyType.addTextChangedListener(textWatcher);
                    campTextLocation.addTextChangedListener(textWatcher);
                    campTextUserName.addTextChangedListener(textWatcher);

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
        // Obtener los valores actuales de los EditText
        String emailEdit = campTextEmail.getText().toString();
        String nameEdit = campTextName.getText().toString();
        String nameCompanyEdit = campTextNameCompany.getText().toString();
        String webPagEdit = campTextWebPag.getText().toString();
        String typeCompanyEdit = campTextCompanyType.getText().toString();
        String locationEdit = campTextLocation.getText().toString();
        String usernameEdit = campTextUserName.getText().toString();


        // Obtener los valores almacenados en Firestore
        DocumentReference docRef = mFirestore.collection(FIELD_COLLECTION_C).document(mUser.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener los valores del documento
                    String emailFire = documentSnapshot.getString(FIELD_EMAIL_C);
                    String nameFire = documentSnapshot.getString(FIELD_NAME_C);
                    String nameCompanyFire = documentSnapshot.getString(FIELD_COMPANY_NAME_C);
                    String webPagFire = documentSnapshot.getString(FIELD_WEB_PAGE_C);
                    String typeCompanyFire = documentSnapshot.getString(FIELD_COMPANY_TYPE_C);
                    String locationFire = documentSnapshot.getString(FIELD_LOCATION_C);
                    String usernameFire = documentSnapshot.getString(FIELD_USERNAME_C);

                    // Verificar si hay cambios en los EditText
                    if (!emailEdit.equals(emailFire) || !nameEdit.equals(nameFire) ||
                            !nameCompanyEdit.equals(nameCompanyFire) || !webPagEdit.equals(webPagFire) ||
                            !typeCompanyEdit.equals(typeCompanyFire) || !locationEdit.equals(locationFire) ||
                            !usernameEdit.equals(usernameFire)) {
                        // Si hay cambios, habilitar el botón
                        btnSave.setEnabled(true);
                        btnSave.setBackgroundResource(R.drawable.btn_orange);
                        btnSave.setTextColor(getResources().getColor(R.color.ghost_white));
                    } else {
                        // Si no hay cambios, deshabilitar el botón
                        btnSave.setEnabled(false);
                        btnSave.setBackgroundResource(R.drawable.btn_disabled);
                        btnSave.setTextColor(getResources().getColor(R.color.litle_color));
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

    @Override
    public void onVoiceCommandReceived(String command, String predictedCategory) {
        if (AppState.getInstance().isModoEdicionActivo()) {
            command = command.toLowerCase();
            command = eliminarTildes(command);
            //TextClassifier precide = new TextClassifier("", );
            //String aux = precide.prediccion("nombre de la compañia");
            if ((command.contains("nombre") || command.contains("usuario") || command.contains("correo") || (command.contains("nombre") && command.contains("empresa")) || (command.contains("tipo") && command.contains("empresa")) || (command.contains("ubicacion") && command.contains("empresa")) || command.contains("pagina")) && campToEdit == null){
                campToEdit = command;

                String respuesta = "¿Cual es el nuevo valor?";


                if (command.contains("correo") || command.contains("pagina")){
                    AppState.getInstance().setHelpGoogleActive(true);
                    AppState.getInstance().setActiveAssistant(false);
                    controller.sendResponseToService(respuesta);
                } else controller.sendResponseToService(respuesta);
            }
            else{
                if (campToEdit != null){
                    if (newValue != null && (command.contains("si") || command.contains("se"))){
                        campTextToEdit.setText(newValue);
                        AppState.getInstance().setModoEdicionActivo(false);
                        btnSave.performClick();
                    } else if (newValue != null && command.contains("no")) {
                        String respuesta = "Entonces, ¿Cual es el nuevo valor?";
                        controller.sendResponseToService(respuesta);
                        newValue = null;
                        if (campToEdit.contains("correo") || campToEdit.contains("pagina")){
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
                NavigationManager.navigateToDestinationC(this, destino, getSupportFragmentManager(), null);
            } else if (predictedCategory.startsWith("accion_")) {
                String accion = extractAfterUnderscore(predictedCategory);
                AppState.getInstance().setModoEdicionActivo(true);
                NavigationManager.navigateToDestinationC(this, accion, getSupportFragmentManager(), null);
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

        if (campToEdit.contains("nombre") && campToEdit.contains("empresa")) {
            control = "NOMBRE EMPRESA";
        } else if (campToEdit.contains("ubicacion") && campToEdit.contains("empresa")) {
            control = "UBICACION EMPRESA";
        } else if (campToEdit.contains("tipo") && campToEdit.contains("empresa")) {
            control = "TIPO EMPRESA";
        } else if (campToEdit.contains("pagina")) {
            control = "PAGINA";
        } else if (campToEdit.contains("nombre")) {
            control = "NOMBRE";
        } else if (campToEdit.contains("correo")) {
            control = "CORREO";
        } else if (campToEdit.contains("usuario")) {
            control = "USUARIO";
        }

            switch (control) {
            case "NOMBRE":
                newValue = newVal;
                oldValue = campTextName.getText().toString();
                campTextToEdit = campTextName;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue + ", dí, si, o no.";
                controller.sendResponseToService(respuesta);

                break;
            case "CORREO":
                newValue = newVal;
                oldValue = campTextEmail.getText().toString();
                campTextToEdit = campTextEmail;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue + ", dí, si, o no.";
                controller.sendResponseToService(respuesta);
                break;
            case "USUARIO":
                newValue = newVal;
                oldValue = campTextUserName.getText().toString();
                campTextToEdit = campTextUserName;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue + ", dí, si, o no.";
                controller.sendResponseToService(respuesta);
                break;
            case "NOMBRE EMPRESA":
                newValue = newVal;
                oldValue = campTextNameCompany.getText().toString();
                campTextToEdit = campTextNameCompany;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue + ", dí, si, o no.";
                controller.sendResponseToService(respuesta);
                break;
            case "UBICACION EMPRESA":
                newValue = newVal;
                oldValue = campTextLocation.getText().toString();
                campTextToEdit = campTextLocation;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue + ", dí, si, o no.";
                controller.sendResponseToService(respuesta);
                break;
            case "TIPO EMPRESA":
                newValue = newVal;
                oldValue = campTextCompanyType.getText().toString();
                campTextToEdit = campTextCompanyType;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue + ", dí, si, o no.";
                controller.sendResponseToService(respuesta);
                break;
            case "PAGINA":
                newValue = newVal;
                oldValue = campTextWebPag.getText().toString();
                campTextToEdit = campTextWebPag;
                respuesta = "¿Estas seguro del nuevo valor? cambiaras "+ oldValue + " por " + newValue + ", dí, si, o no.";
                controller.sendResponseToService(respuesta);
                break;
        }
    }
    private void helpGoogle() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(HelpGoogleWorker.class).build();
        WorkManager.getInstance(this).enqueue(workRequest);
    }
    @Override
    public void onTTSCompleted() {
        //TTS terminó de hablar
        if (AppState.getInstance().isHelpGoogleActive()){
            helpGoogle();
        }
    }
}