package com.example.tuempleoblind;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import androidx.annotation.Nullable;
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

    @Override
    public void onCreate() {
        super.onCreate();

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
        // Configurar UtteranceProgressListener una sola vez
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // No hacer nada cuando empieza
            }

            @Override
            public void onDone(String utteranceId) {
                if (speechService != null) {
                    speechService.setPause(false); // Reanudar el servicio de reconocimiento de voz
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
            } catch (IOException e) {
                Log.e(TAG, "Failed to start listening", e);
            }
        }
    }

    private void handleVoiceCommand(String jsonCommand) {
        String command = extractTextFromJson(jsonCommand); // Extraer el texto del JSON
        Intent intent = new Intent("VOICE_COMMAND");
        intent.putExtra("command", command);
        sendBroadcast(intent);
        respondToVoiceCommand(command);
    }

    private void respondToVoiceCommand(String command) {
        switch (command.toLowerCase()) {
            case "hola":
                speak("Hola, ¿cómo puedo ayudarte?");
                break;
            case "editar primer texto":
                speak("Editando el primer texto");
                break;
            case "presionar botón uno":
                speak("Presionando el botón uno");
                break;

        }
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
        if (speechService != null) {
            speechService.setPause(true); // Pausar el servicio de reconocimiento de voz
        }

        // Llamar a tts.speak con el identificador de utteranceId
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
        // Puedes manejar resultados parciales si es necesario
    }

    @Override
    public void onError(Exception e) {
        Log.e(TAG, "Error during recognition", e);
    }

    @Override
    public void onTimeout() {
        // Manejar el tiempo de espera si es necesario
    }
}