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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SignUpCForm extends AppCompatActivity implements VoiceCommandController.ActivityCallback, AppState.TTSObserver {
    EditText campTextNameCompany;
    EditText campTextTypeCompany;
    EditText campTextLocation;
    EditText campTextWebPag;
    Button btnContinue;
    Button btnBack;
    private VoiceCommandController controller;
    private LottieAnimationView robotAnimation;
    private ImageView background;
    FloatingActionButton microComand;
    private String newValue = null;
    List<EditText> editTexts = new ArrayList<>();
    UtilCommandModel.ComponentResult result;
    private FirebaseFirestore mFirestore;
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
        setContentView(R.layout.activity_sign_up_cform);

        controller = VoiceCommandController.getInstance(this);
        controller.registerActivityCallback(this);
        AppState.getInstance().addTTSObserver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(speechRecognitionResultsReceiver, new IntentFilter("SpeechRecognitionResults"));
        microComand = findViewById(R.id.floatingButtonComands);

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

        robotAnimation=findViewById(R.id.robot_animation);
        background=findViewById(R.id.backBlack);

        mFirestore = FirebaseFirestore.getInstance();
        campTextNameCompany = findViewById(R.id.editTextNameCompanyEditDataC);
        campTextTypeCompany = findViewById(R.id.editTextCompanyTypeEditDataC);
        campTextLocation = findViewById(R.id.editTextLocationEditDataC);
        campTextWebPag = findViewById(R.id.editTextWebPagEditDataC);
        btnContinue = findViewById(R.id.buttonSaveEditDataC);

        controller.sendRoleUser("unLogin");

        actionContinue();
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());

        obtainEditTextAndSpinner();
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
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameCompany = campTextNameCompany.getText().toString();
                String typeCompany = campTextTypeCompany.getText().toString();
                String locationCompany = campTextLocation.getText().toString();
                String webPagCompany = campTextWebPag.getText().toString();

                if(nameCompany.isEmpty() || typeCompany.isEmpty() || locationCompany.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Completa todos los datos correspondientes", Toast.LENGTH_SHORT).show();
                }
                else{
                    postUsernameBlind(nameCompany, typeCompany, locationCompany, webPagCompany);
                }
            }

            private void postUsernameBlind(String nameCompany, String typeCompany, String location, String webPag) {
                Map<String, Object> map = new HashMap<>();
                map.put("Nombre de la compañia", nameCompany);
                map.put("Tipo de compañia", typeCompany);
                map.put("Ubicación", location);
                map.put("Pagina web", webPag);

                String userID = getIntent().getStringExtra("userID");

                mFirestore.collection("UsernameC").document(userID).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (AppState.getInstance().isActiveAssistant()){
                            controller.sendResponseToService("Bienvenido");
                        }
                        Toast.makeText(getApplicationContext(), "Datos del usuario guardados correctamente", Toast.LENGTH_SHORT).show();
                        // Continuar con la lógica de tu aplicación
                        Intent intent = new Intent(getApplicationContext(), CompanyHome.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (AppState.getInstance().isActiveAssistant()){
                            controller.sendResponseToService("No se pudo continuar");
                        }
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });
    }

    @Override
    public void onVoiceCommandReceived(String command, String predictedCategory) {
        if (AppState.getInstance().isModoEdicionActivo()) {
            command = NavigationManager.eliminarTildes(command);
            //por hacer
            if (result == null){
                obtainEditTextAndSpinner();
                result = UtilCommandModel.checkComponents(editTexts, null);
                if (result.getEmptyEditText() != null){
                    if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("pagina") || result.getEmptyEditText().getHint().toString().toLowerCase().contains("web")){
                        AppState.getInstance().setHelpGoogleActive(true);
                        AppState.getInstance().setActiveAssistant(false);
                        controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint());
                    } else controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint());
                } else {
                    AppState.getInstance().setModoEdicionActivo(false);
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
                    if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("pagina") || result.getEmptyEditText().getHint().toString().toLowerCase().contains("web")){
                        AppState.getInstance().setHelpGoogleActive(true);
                        AppState.getInstance().setActiveAssistant(false);
                        controller.sendResponseToService("Entonces, ¿Que valor quieres colocar?");
                    } else controller.sendResponseToService("Entonces, ¿Que valor quieres colocar?");
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

    public void obtainEditTextAndSpinner(){
        editTexts.clear();

        editTexts.add(campTextNameCompany);
        editTexts.add(campTextTypeCompany);
        editTexts.add(campTextLocation);
        editTexts.add(campTextWebPag);
    }
}