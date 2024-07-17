package com.example.tuempleoblind;

import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LogIn extends AppCompatActivity implements VoiceCommandController.ActivityCallback, AppState.TTSObserver{

    FirebaseAuth mAuth;
    EditText campTextEmail;
    EditText campTextPassword;
    Button btnConfirm;
    Button btnBack;
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
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();

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

        campTextEmail = findViewById(R.id.editTextEmailLogIn);
        campTextPassword = findViewById(R.id.editTextPasswordLogIn);
        btnConfirm = findViewById(R.id.buttonConfirmLogIn);
        btnBack = findViewById(R.id.buttonBackLogIn);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish(); // Finalizar la actividad de inicio de sesión
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login(campTextEmail.getText().toString(), campTextPassword.getText().toString());
            }
        });

        controller.sendRoleUser("unLogin");

        AppState.getInstance().setModoEdicionActivo(true);
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());

        obtainEditText();
        result = UtilCommandModel.checkComponents(editTexts, null);

        if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("correo") || result.getEmptyEditText().getHint().toString().toLowerCase().contains("contraseña")){
            AppState.getInstance().setHelpGoogleActive(true);
            AppState.getInstance().setActiveAssistant(false);
            controller.sendResponseToService("Escribe tu " + result.getEmptyEditText().getHint().toString());
        }
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

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRefUsernameBlind = db.collection("UsernameBlind").document(user.getUid());
                        DocumentReference docRefUsernameC = db.collection("UsernameC").document(user.getUid());
                        if (AppState.getInstance().isActiveAssistant()){
                            controller.sendResponseToService("Iniciando sesión");
                        }
                        checkUserCollection(docRefUsernameBlind, docRefUsernameC);
                    } else {
                        Toast.makeText(getApplicationContext(), "Inicio de sesión fallido.", Toast.LENGTH_SHORT).show();
                        if (AppState.getInstance().isActiveAssistant()){
                            controller.sendResponseToService("Inicio de sesión fallido");
                        }
                    }
                });
    }

    private void checkUserCollection(DocumentReference docRefUsernameBlind, DocumentReference docRefUsernameC) {
        docRefUsernameBlind.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentBlind = task.getResult();
                if (documentBlind.exists()) {
                    navigateToHomePage(HomePageBlind.class);
                } else {
                    checkCompanyCollection(docRefUsernameC);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error: No se pudo obtener el documento del usuario de UsernameBlind.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkCompanyCollection(DocumentReference docRefUsernameC) {
        docRefUsernameC.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentC = task.getResult();
                if (documentC.exists()) {
                    navigateToHomePage(CompanyHome.class);
                } else {
                    Toast.makeText(getApplicationContext(), "Usuario no encontrado en ninguna colección.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error: No se pudo obtener el documento del usuario de UsernameC.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHomePage(Class<?> homePageClass) {
        startActivity(new Intent(getApplicationContext(), homePageClass));
        finish();
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
                    AppState.getInstance().setModoEdicionActivo(false);
                    btnConfirm.performClick();
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
                    }
                    else controller.sendResponseToService("Entonces, ¿Que valor quieres colocar?");
                }
            } else{
                controller.sendResponseToService("¿Quieres colocar " + newValue + "?" + ", dí, si, o no.");
            }
        }
        else {
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

        editTexts.add(campTextEmail);
        editTexts.add(campTextPassword);

    }
}