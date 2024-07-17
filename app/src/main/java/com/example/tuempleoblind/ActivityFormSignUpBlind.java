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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityFormSignUpBlind extends AppCompatActivity implements VoiceCommandController.ActivityCallback, AppState.TTSObserver {

    EditText campTextProfession;
        EditText campTextAddress;
    EditText campTextPhone;
        EditText campTextAbilities;
    Spinner spinnerLevelBlind;
    Button btnContinue;
    Button btnBack;
    private FirebaseFirestore mFirestore;
    private VoiceCommandController controller;
    private LottieAnimationView robotAnimation;
    private ImageView background;
    FloatingActionButton microComand;

    List<EditText> editTexts = new ArrayList<>();
    List<Spinner> spinners = new ArrayList<>();
    UtilCommandModel.ComponentResult result;
    private String newValue = null;
    private int opcionSpinner = -1;
    private SpeechRecognizer speechRecognizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_sign_up_blind);

        mFirestore = FirebaseFirestore.getInstance();
        spinnerLevelBlind = findViewById(R.id.spinnerCategoryLevelBlind);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.list_level_blind,R.layout.style_spinner);
        adapter.setDropDownViewResource(R.layout.style_spinner);
        spinnerLevelBlind.setAdapter(adapter);

        robotAnimation=findViewById(R.id.robot_animation);
        background=findViewById(R.id.backBlack);

        controller = VoiceCommandController.getInstance(this);
        controller.registerActivityCallback(this);
        AppState.getInstance().addTTSObserver(this);
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

        campTextAbilities=findViewById(R.id.editTextAbilitiesBlind);
        campTextProfession = findViewById(R.id.editTextProfessionFormBlind);
        campTextAddress = findViewById(R.id.editTextAddressFormBlind);
        campTextPhone = findViewById(R.id.editTextPhoneNumberFormBlind);
        btnContinue = findViewById(R.id.buttonContinueFormBlind);

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

        obtainEditTextAndSpinner();
        result = UtilCommandModel.checkComponents(editTexts, spinners);
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
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        AppState.getInstance().removeTTSObserver(this);
    }

    private void actionContinue() {
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String levelBlind = spinnerLevelBlind.getSelectedItem().toString();
                String profession = campTextProfession.getText().toString();
                String address = campTextAddress.getText().toString();
                String phone = campTextPhone.getText().toString();
                String abilities=campTextAbilities.getText().toString();

                if(levelBlind.isEmpty() || profession.isEmpty() ||phone.isEmpty()||abilities.isEmpty()|| address.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Completa todos los datos para continuar", Toast.LENGTH_SHORT).show();
                }
                else{
                    postUsernameBlind(levelBlind, profession, address, phone,abilities);
                }
            }

            private void postUsernameBlind(String levelBlind, String profesion, String location,
                                           String phonenumbre,String abilities) {
                Map<String, Object> map = new HashMap<>();
                map.put("Nivel de ceguera", levelBlind);
                map.put("Profesion", profesion);
                map.put("Dirección", location);
                map.put("Numero de Teléfono", phonenumbre);
                map.put("abilities",abilities);

                String userID = getIntent().getStringExtra("userID");

                mFirestore.collection("UsernameBlind").document(userID).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Datos del usuario guardados correctamente", Toast.LENGTH_SHORT).show();
                        // Continuar con la lógica de tu aplicación
                        Intent intent = new Intent(getApplicationContext(), HomePageBlind.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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
                result = UtilCommandModel.checkComponents(editTexts, spinners);
                if (result.getEmptyEditText() != null){


                    if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("telefono")){
                        AppState.getInstance().setHelpGoogleActive(true);
                        AppState.getInstance().setActiveAssistant(false);
                        controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint());
                    } else controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint());
                } else if (result.getEmptySpinner() != null) {
                    controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptySpinner().getContentDescription());
                }
                else {
                    controller.sendResponseToService("Bienvenido");
                    AppState.getInstance().setModoEdicionActivo(false);
                    btnContinue.performClick();
                }

            } else if (newValue == null) {
                if (result.getEmptyEditText() != null){
                    controller.sendResponseToService("¿Estas seguro? Colocaras " + command + ", dí, si, o no.");
                    newValue = command;
                } else if (result.getEmptySpinner() != null) {
                    String response = verifyTypeSpinner(command);
                    controller.sendResponseToService(response);
                }

            } else if (command.contains("si") || command.contains("se")) {
                if (result.getEmptyEditText() != null){
                    result.getEmptyEditText().setText(newValue);
                } else if (result.getEmptySpinner() != null) {
                    result.getEmptySpinner().setSelection(opcionSpinner);
                }
                result = null;
                newValue = null;
                onVoiceCommandReceived("siguiente", "comando no reconocido");
            } else if (command.contains("no")) {
                newValue = null;
                if (result.getEmptyEditText() != null){
                    if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("telefono")){
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
    @Override
    public void onTTSCompleted() {
        if (AppState.getInstance().isHelpGoogleActive()){
            helpGoogle();
        }
    }

    public void obtainEditTextAndSpinner(){
        spinners.clear();
        editTexts.clear();

        spinners.add(spinnerLevelBlind);
        editTexts.add(campTextAbilities);
        editTexts.add(campTextProfession);
        editTexts.add(campTextAddress);
        editTexts.add(campTextPhone);
    }
}