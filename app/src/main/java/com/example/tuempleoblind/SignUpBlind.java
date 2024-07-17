package com.example.tuempleoblind;



import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class SignUpBlind extends AppCompatActivity implements VoiceCommandController.ActivityCallback, AppState.TTSObserver{
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
    private String newValue = null;
    List<EditText> editTexts = new ArrayList<>();
    UtilCommandModel.ComponentResult result;
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
        setContentView(R.layout.activity_sign_up_blind);

        mFirestore = FirebaseFirestore.getInstance();

        controller = VoiceCommandController.getInstance(this);
        controller.registerActivityCallback(this);

        AppState.getInstance().addTTSObserver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(speechRecognitionResultsReceiver, new IntentFilter("SpeechRecognitionResults"));
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

        obtainEditText();
        result = UtilCommandModel.checkComponents(editTexts, null);
        controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint().toString());
        Utilidad.incrementarMensual(mFirestore, COLLECTION_REPORTE, NUMERO_DE_INVIDENTES_REGISTRADOS);
        Utilidad.incrementarTotal(mFirestore,COLLECTION_REPORTE,DOCUMENT_TOTALES,NUMERO_DE_INVIDENTES_REGISTRADOS_TOTALES);
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
    public void onDestroy() {
        super.onDestroy();
        controller.unregisterActivityCallback(this);
        AppState.getInstance().removeTTSObserver(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(speechRecognitionResultsReceiver);
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

                    if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("correo") || result.getEmptyEditText().getHint().toString().toLowerCase().contains("contraseña")){
                        AppState.getInstance().setHelpGoogleActive(true);
                        AppState.getInstance().setActiveAssistant(false);
                        controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint().toString());
                    } else controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint().toString());
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
                newValue = null;
                if (result.getEmptyEditText() != null){
                    if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("correo") || result.getEmptyEditText().getHint().toString().toLowerCase().contains("contraseña")){
                        AppState.getInstance().setHelpGoogleActive(true);
                        AppState.getInstance().setActiveAssistant(false);
                        controller.sendResponseToService("Entonces, ¿Que valor quieres colocar?");
                    } else controller.sendResponseToService("Entonces, ¿Que valor quieres colocar?");
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
                if(command.equals("4p4g4d0_4ut0m4t1c0")&& predictedCategory.equals("4p4g4d0_10s3gund0s")){
                    updateRobotAnimationVisibility(false);
                }else{
                    String respuesta = "No entiendo ese comando. Por favor, intenta de nuevo.";
                    controller.sendResponseToService(respuesta);
                    updateRobotAnimationVisibility(false);}
            }
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

    public void obtainEditText(){
        editTexts.clear();

        editTexts.add(campTextName);
        editTexts.add(campTextUserName);
        editTexts.add(campTextEmail);
        editTexts.add(campTextPassword1);
        editTexts.add(campTextPassword2);
    }
}