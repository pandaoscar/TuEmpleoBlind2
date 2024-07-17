package com.example.tuempleoblind;

import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

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

public class SignUpC extends AppCompatActivity implements VoiceCommandController.ActivityCallback, AppState.TTSObserver{
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
        setContentView(R.layout.activity_sign_up_c);
        mFirestore = FirebaseFirestore.getInstance();

        controller = VoiceCommandController.getInstance(this);
        controller.registerActivityCallback(this);
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

        campTextNameC = findViewById(R.id.editTextNameCompanyEditDataC);
        campTextUserNameC = findViewById(R.id.editTextCompanyTypeEditDataC);
        campTextEmailC = findViewById(R.id.editTextUserEmailSignUpC);
        campTextPassword1C = findViewById(R.id.editTextLocationEditDataC);
        campTextPassword2C = findViewById(R.id.editTextPasswordSignUpC2);

        btnContinue = findViewById(R.id.buttonSaveEditDataC);
        btnBack = findViewById(R.id.buttonCancelEditDataC);

        controller.sendRoleUser("unLogin");


        actionContinue();
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());

        obtainEditText();
        result = UtilCommandModel.checkComponents(editTexts, null);
        controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint().toString());
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
                    if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("correo") || result.getEmptyEditText().getHint().toString().toLowerCase().contains("contraseña")){
                        AppState.getInstance().setHelpGoogleActive(true);
                        AppState.getInstance().setActiveAssistant(false);
                        controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint().toString());
                    }
                    else controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint().toString());
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
                controller.sendResponseToService("Entonces, ¿Que valor quieres colocar?");
                newValue = null;
                if (result.getEmptyEditText() != null){
                    if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("correo") || result.getEmptyEditText().getHint().toString().toLowerCase().contains("contraseña")){
                        AppState.getInstance().setHelpGoogleActive(true);
                        AppState.getInstance().setActiveAssistant(false);
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

        editTexts.add(campTextNameC);
        editTexts.add(campTextUserNameC);
        editTexts.add(campTextEmailC);
        editTexts.add(campTextPassword1C);
        editTexts.add(campTextPassword2C);
    }
}