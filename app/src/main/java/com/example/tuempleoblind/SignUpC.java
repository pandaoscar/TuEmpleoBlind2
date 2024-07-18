package com.example.tuempleoblind;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SignUpC extends AppCompatActivity implements VoiceCommandController.ActivityCallback, VoiceCommandController.BooleanCallback, AppState.Observer{
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
        setContentView(R.layout.activity_sign_up_c);
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

        campTextNameC = findViewById(R.id.editTextNameCompanyEditDataC);
        campTextUserNameC = findViewById(R.id.editTextCompanyTypeEditDataC);
        campTextEmailC = findViewById(R.id.editTextUserEmailSignUpC);
        campTextPassword1C = findViewById(R.id.editTextLocationEditDataC);
        campTextPassword2C = findViewById(R.id.editTextPasswordSignUpC2);

        btnContinue = findViewById(R.id.buttonSaveEditDataC);
        btnBack = findViewById(R.id.buttonCancelEditDataC);

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


        actionContinue();
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());
        if (AppState.getInstance().isActiveAssistant()){
            AppState.getInstance().setModoEdicionActivo(true);
        } else AppState.getInstance().setModoEdicionActivo(false);

        obtainEditText();
        result = UtilCommandModel.checkComponents(editTexts, null);
        controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint().toString());
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
                                            if (AppState.getInstance().isActiveAssistant()){
                                                controller.sendResponseToService("Porfavor, llena los siguientes datos");
                                            }
                                            // Registro exitoso, obtener el ID único del usuario
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            String userID = user.getUid();
                                            // Guardar datos adicionales del usuario en Firestore
                                            postUserNameC(name, username, email, userID);
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

        editTexts.add(campTextNameC);
        editTexts.add(campTextUserNameC);
        editTexts.add(campTextEmailC);
        editTexts.add(campTextPassword1C);
        editTexts.add(campTextPassword2C);
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