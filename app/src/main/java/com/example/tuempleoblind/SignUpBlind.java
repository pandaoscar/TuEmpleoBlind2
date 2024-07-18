package com.example.tuempleoblind;



import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.grpc.okhttp.internal.Util;

public class SignUpBlind extends AppCompatActivity implements VoiceCommandController.ActivityCallback, VoiceCommandController.BooleanCallback, AppState.Observer{
    private static final String NUMERO_DE_INVIDENTES_REGISTRADOS = "numeroDeInvidentesRegistrados";
    private static final String NUMERO_DE_INVIDENTES_REGISTRADOS_TOTALES = "numeroDeInvidentesRegistradosTotales";
    private static final String COLLECTION_REPORTE = "Reporte";
    private static final String DOCUMENT_TOTALES = "Totales";

    EditText campTextName;
    EditText campTextUserName;
    EditText campTextEmail;
    EditText campTextPassword1;
    EditText campTextPassword2;
    Button btnContinue;
    Button btnBack;
    private FirebaseFirestore mFirestore;
    private VoiceCommandController controller;
    private LottieAnimationView robotAnimation;
    private ImageView background;
    FloatingActionButton microComand;
    private SpeechRecognizer speechRecognizer;
    private String newValue = null;
    List<EditText> editTexts = new ArrayList<>();
    UtilCommandModel.ComponentResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_blind);

        mFirestore = FirebaseFirestore.getInstance();

        controller = VoiceCommandController.getInstance(this);
        controller.registerActivityCallback(this);
        controller.registerBooleanCallback(this);
        AppState.getInstance().addObserver(this);
        microComand = findViewById(R.id.floatingButtonComands);

        robotAnimation=findViewById(R.id.robot_animation);
        background=findViewById(R.id.backBlack);

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

        btnContinue = findViewById(R.id.buttonSignUpSendBlind);
        btnBack = findViewById(R.id.buttonBackSignUpBlind);
        campTextName = findViewById(R.id.editTextNameSignUpBlind);
        campTextUserName = findViewById(R.id.editTextSignUpUsernameBlind);
        campTextEmail = findViewById(R.id.editTextUserEmailSignUpBlind);
        campTextPassword1 = findViewById(R.id.editTextPasswordSignUpBlind1);
        campTextPassword2 = findViewById(R.id.editTextPasswordSignUpBlind2);

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
                ArrayList<String> palabrasReconocidas = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (palabrasReconocidas != null && !palabrasReconocidas.isEmpty()) {
                    String palabra = palabrasReconocidas.toString().replace("[", "").replace("]", "");
                    String[] pal = palabra.split(" ");
                    palabra = String.join("", pal);
                    palabra = palabra.toLowerCase();
                    palabra = NavigationManager.eliminarTildes(palabra);
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

        controller.sendRoleUser("unLogin");


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = campTextName.getText().toString();
                final String username = campTextUserName.getText().toString();
                final String email = campTextEmail.getText().toString();
                final String password1 = campTextPassword1.getText().toString();
                final String password2 = campTextPassword2.getText().toString();

                if(name.isEmpty() || username.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Completa todos los datos correspondientes", Toast.LENGTH_SHORT).show();

                }
                else{
                    if(!password1.equals(password2)){
                        Toast.makeText(getApplicationContext(), "Las contraseñas deben coincidir, porfavor verifica nuevamente", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        // Registrar usuario en Firebase Authentication
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password1)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            if (AppState.getInstance().isActiveAssistant()){
                                                controller.sendResponseToService("Porfavor, llena los siguientes datos");
                                            }
                                            // Registro exitoso, obtener el ID único del usuario
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            String userID = user.getUid();
                                            // Guardar datos adicionales del usuario en Firestore
                                            postUsernameBlind(name, username, email, userID);
                                        } else {
                                            if (AppState.getInstance().isActiveAssistant()){
                                                controller.sendResponseToService("No se completo correctamente, intenta de nuevo");
                                            }
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());
        if (AppState.getInstance().isActiveAssistant()){
            AppState.getInstance().setModoEdicionActivo(true);
        } else AppState.getInstance().setModoEdicionActivo(false);
        obtainEditText();
        result = UtilCommandModel.checkComponents(editTexts, null);
        controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint().toString());
        Utilidad.incrementarMensual(mFirestore, COLLECTION_REPORTE, NUMERO_DE_INVIDENTES_REGISTRADOS);
        Utilidad.incrementarTotal(mFirestore,COLLECTION_REPORTE,DOCUMENT_TOTALES,NUMERO_DE_INVIDENTES_REGISTRADOS_TOTALES);
    }

    private void updateRobotAnimationVisibility(boolean isActive){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
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
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        controller.unregisterActivityCallback(this);
        controller.unregisterBooleanCallback(this);
        AppState.getInstance().removeObserver(this);
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
                        Toast.makeText(getApplicationContext(), "Usuario creado correctamente, llena la siguiente información", Toast.LENGTH_SHORT).show();
                        // Continuar con la lógica de tu aplicación
                        Intent intent = new Intent(getApplicationContext(), ActivityFormSignUpBlind.class);
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

    @Override
    public void onVoiceCommandReceived(String command, String predictedCategory) {
        if (AppState.getInstance().isModoEdicionActivo()) {
            command = NavigationManager.eliminarTildes(command);
            //por hacer
            if (result == null){
                obtainEditText();
                result = UtilCommandModel.checkComponents(editTexts, null);
                if (result.getEmptyEditText() != null){
                    String response = "Que valor quieres colocarle a " + result.getEmptyEditText().getHint().toString();
                    if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("correo") || result.getEmptyEditText().getHint().toString().toLowerCase().contains("contraseña")){
                        AppState.getInstance().setActiveAssistant(false);
                        controller.sendGoogleAlert(response);
                    } else controller.sendResponseToService(response);
                } else {
                    btnContinue.performClick();
                }

            } else if (newValue == null) {
                if (result.getEmptyEditText() != null){
                    controller.sendResponseToService("¿Estas seguro? Colocaras " + command + ", dí, si, o no.");
                    newValue = command;
                }

            } else if (command.contains("si") || command.contains("se")) {
                result.getEmptyEditText().setText(newValue);
                result = null;
                newValue = null;
                onVoiceCommandReceived("siguiente", "comando no reconocido");
            } else if (command.contains("no")) {
                String response = "Entonces, ¿Que valor quieres colocar?";
                newValue = null;
                if (result.getEmptyEditText() != null){
                    if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("correo") || result.getEmptyEditText().getHint().toString().toLowerCase().contains("contraseña")){
                        AppState.getInstance().setActiveAssistant(false);
                        controller.sendGoogleAlert(response);
                    } else controller.sendResponseToService(response);
                }
            } else{
                controller.sendResponseToService("¿Quieres colocar " + newValue + "?" + ", dí, si, o no.");
            }
        }
        else{
            if (predictedCategory.startsWith("accion_")) {
                String accion = extractAfterUnderscore(predictedCategory);
                AppState.getInstance().setModoEdicionActivo(true);
                NavigationManager.navigateToDestinationUnLogin(this, accion, getSupportFragmentManager(), null);
            } else {
                String respuesta = "No entiendo ese comando. Por favor, intenta de nuevo.";
                controller.sendResponseToService(respuesta);
            }
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

    public void obtainEditText(){
        editTexts.clear();

        editTexts.add(campTextName);
        editTexts.add(campTextUserName);
        editTexts.add(campTextEmail);
        editTexts.add(campTextPassword1);
        editTexts.add(campTextPassword2);
    }

    @Override
    public void onBooleanCommandReceived(boolean booleanValue) {
        helpGoogle();
    }
    @Override
    public void onActiveAssistantChanged(boolean isActive) {
        if (AppState.getInstance().isModoEdicionActivo()){
            updateRobotAnimationVisibility(true);
        } else updateRobotAnimationVisibility(isActive);
    }
}