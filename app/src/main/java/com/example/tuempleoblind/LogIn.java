package com.example.tuempleoblind;

import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private SpeechRecognizer speechRecognizer;
    private String newValue = null;
    List<EditText> editTexts = new ArrayList<>();
    UtilCommandModel.ComponentResult result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();

        controller = VoiceCommandController.getInstance(this);
        controller.registerActivityCallback(this);
        AppState.getInstance().addTTSObserver(this);
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

        AppState.getInstance().setModoEdicionActivo(true);
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());

        obtainEditText();
        result = UtilCommandModel.checkComponents(editTexts, null);
        controller.sendResponseToService("Escribe tu " + result.getEmptyEditText().getHint().toString());

        if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("correo") || result.getEmptyEditText().getHint().toString().toLowerCase().contains("contraseña")){
            AppState.getInstance().setActiveAssistant(false);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    helpGoogle();
                }
            }, 3000);
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
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRefUsernameBlind = db.collection("UsernameBlind").document(user.getUid());
                        DocumentReference docRefUsernameC = db.collection("UsernameC").document(user.getUid());
                        checkUserCollection(docRefUsernameBlind, docRefUsernameC);
                    } else {
                        Toast.makeText(getApplicationContext(), "Inicio de sesión fallido.", Toast.LENGTH_SHORT).show();
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
                    controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint().toString());
                    if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("correo") || result.getEmptyEditText().getHint().toString().toLowerCase().contains("contraseña")){
                        AppState.getInstance().setHelpGoogleActive(true);
                        AppState.getInstance().setActiveAssistant(false);
                    }
                } else {
                    controller.sendResponseToService("Iniciando sesión");
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
                controller.sendResponseToService("Entonces, ¿Que valor quieres colocar?");
                newValue = null;
                if (result.getEmptyEditText() != null){
                    if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("correo") || result.getEmptyEditText().getHint().toString().toLowerCase().contains("contraseña")){
                        AppState.getInstance().setHelpGoogleActive(true);
                        AppState.getInstance().setActiveAssistant(false);
                    }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla algo...");

                try {
                    speechRecognizer.startListening(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void obtainEditText(){
        editTexts.clear();

        editTexts.add(campTextEmail);
        editTexts.add(campTextPassword);

    }

    @Override
    public void onTTSCompleted() {
        if (AppState.getInstance().isHelpGoogleActive()){
            helpGoogle();
        }
    }
}