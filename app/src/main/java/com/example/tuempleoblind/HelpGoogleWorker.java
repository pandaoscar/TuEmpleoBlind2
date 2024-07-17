package com.example.tuempleoblind;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.Locale;

public class HelpGoogleWorker extends Worker {

    private Handler mainHandler;

    public HelpGoogleWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                setupSpeechRecognizer(context);
            }
        });

        return Result.success();
    }

    private void setupSpeechRecognizer(Context context) {
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
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
                Log.e("HelpGoogleWorker", "Speech Recognition Error: " + error);
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
                    Intent intent = new Intent("SpeechRecognitionResults");
                    intent.putExtra("recognizedText", palabra);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    AppState.getInstance().setModoEdicionActivo(true);
                    AppState.getInstance().setActiveAssistant(true);
                    speechRecognizer.destroy();

                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla algo...");

        try {
            speechRecognizer.startListening(intent);
        } catch (Exception e) {
            Log.e("HelpGoogleWorker", "Error starting speech recognition", e);
        }
    }
}
