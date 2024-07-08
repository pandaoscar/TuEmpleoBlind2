package com.example.tuempleoblind;


import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.tuempleoblind.tensorflowlite.TextClassifier;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.StorageService;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Locale;



public class VoiceService extends Service implements RecognitionListener {
    private static final String TAG = "VoiceCommandService";
    private Model model;
    private SpeechService speechService;
    private TextToSpeech tts;
    private TextClassifier classifier;
    private String commandNotRecognizer = null;
    private Handler timeoutHandler;
    private Runnable timeoutRunnable;
    private String roleUser = null;

    private final Messenger messenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            String role = data.getString("role");
            String response = data.getString("response");

            System.out.println("holaaa " + role);


            if (response != null) {
                handleResponseFromActivity(response);
            }

            if (role != null) {
                try {
                    getRole(role);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getRole(String rol) throws Exception {
        // Cargar el modelo en función del rol del usuario
        if(rol.equals("userBlind")){
            classifier = new TextClassifier(this, "model.tflite", "maxlen.txt", "category_mapping.json", "word_index.json");
        }
        else{
            if (rol.equals("userCompany")){
                classifier = new TextClassifier(this, "model_company.tflite", "maxlen.txt", "category_mapping.json", "word_index.json");
            }
            else{
                Log.e("Rol validacion", "No se supo cual era el rol");
            }

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppState.getInstance().setActiveAssistant(true);
        timeoutHandler = new Handler();
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                onTimeout();
                //validamos que comando desactivado no se haya mandado por el boton
                if (!"Desactivado".equals(commandNotRecognizer)){
                    speak("Desactivado");
                    commandNotRecognizer = "Desactivado";
                }
                if (!AppState.getInstance().isModoEdicionActivo()){
                    Intent intent = new Intent("VOICE_COMMAND");
                    intent.putExtra("command", "4p4g4d0_4ut0m4t1c0");
                    intent.putExtra("predictedCategory", "4p4g4d0_10s3gund0s");
                    sendBroadcast(intent);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }

            }
        };
        LibVosk.setLogLevel(LogLevel.INFO);
        StorageService.unpack(this, "vosk-model-small-es-0.42", "model",
                (model) -> {
                    this.model = model;

                    startListening();

                },
                (exception) -> Log.e(TAG, "Failed to unpack the model", exception)
        );

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(new Locale("es", "ES"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Language not supported");
                }
            } else {
                Log.e(TAG, "Initialization failed");
            }
        });

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // No hacer nada cuando empieza
            }

            @Override
            public void onDone(String utteranceId) {
                if ("comando no reconocido".equals(commandNotRecognizer)){
                    resetTimeout(0);
                    commandNotRecognizer = null;
                }
                else{
                    if ("Activado".equals(commandNotRecognizer)){
                        if (speechService != null) {
                            startListening(); // Reanudar el servicio de reconocimiento de voz
                            commandNotRecognizer = null;
                        }
                    }
                    else{
                        if ("Desactivado".equals(commandNotRecognizer)){
                            commandNotRecognizer = null;
                        }
                        else startListening();
                    }
                }
                Log.d(TAG, "Texto TTS reproducido completamente");
            }

            @Override
            public void onError(String utteranceId) {
                // Manejar errores si es necesario
            }
        });
    }

    private void startListening() {
        if (model != null) {
            try {
                Recognizer recognizer = new Recognizer(model, 16000.0f);
                speechService = new SpeechService(recognizer, 16000.0f);
                speechService.startListening(this);
                resetTimeout(20000);
                AppState.getInstance().setActiveAssistant(true);
                System.out.println("ya actualicé el estado del asistente");
            } catch (IOException e) {
                Log.e(TAG, "Failed to start listening", e);
            }
        }
    }

    private void resetTimeout(int time) {
        timeoutHandler.removeCallbacks(timeoutRunnable);
        timeoutHandler.postDelayed(timeoutRunnable, time);
    }

    private void handleVoiceCommand(String jsonCommand) {
        String command = extractTextFromJson(jsonCommand);
        if (!command.isEmpty()){
            try {
                String predictedCategory = classifier.classifyText(command);
                Log.d("TextClassification", "Categoría predicha: " + predictedCategory + "  Comando: " + command);

                if (predictedCategory.startsWith("accion_")){
                    resetTimeout(100000);
                }



                if (predictedCategory.equals("comando no reconocido") && !AppState.getInstance().isModoEdicionActivo()) {
                    commandNotRecognizer = predictedCategory;

                }


                Intent intent = new Intent("VOICE_COMMAND");
                intent.putExtra("command", command);
                intent.putExtra("predictedCategory", predictedCategory);
                sendBroadcast(intent);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleResponseFromActivity(String response) {
        System.out.println("Devuelto por el activity: " + response);
        if (response.equals("Activado") || response.equals("Desactivado")){
            if (AppState.getInstance().isActiveAssistant()){
                startListening();
            }
            else{
                resetTimeout(1);
            }
            commandNotRecognizer = response;
        }
        speak(response);
    }

    private String extractTextFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error al procesar el JSON";
        }
    }

    private void speak(String text) {
        if (speechService != null && text != null && AppState.getInstance().isActiveAssistant()) {
            speechService.stop(); // Pausar el servicio de reconocimiento de voz
        }

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "uniqueId");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechService != null) {
            speechService.stop();
            speechService.shutdown();
        }
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public void onResult(String hypothesis) {
        handleVoiceCommand(hypothesis);
    }

    @Override
    public void onFinalResult(String hypothesis) {
        handleVoiceCommand(hypothesis);
    }

    @Override
    public void onPartialResult(String hypothesis) {

    }

    @Override
    public void onError(Exception e) {
        Log.e(TAG, "Error during recognition", e);
    }

    @Override
    public void onTimeout() {
        Log.i(TAG, "No se detectó entrada de voz o comando no reconocido en 10 segundos, pausando reconocimiento.");
        if (speechService != null) {
            speechService.stop();
        }
        AppState.getInstance().setActiveAssistant(false);
    }
}
