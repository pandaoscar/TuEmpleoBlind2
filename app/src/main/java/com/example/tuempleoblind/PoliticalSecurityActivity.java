package com.example.tuempleoblind;

import static android.content.ContentValues.TAG;

import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class PoliticalSecurityActivity extends AppCompatActivity implements VoiceCommandController.ActivityCallback, AppState.Observer{
    Button btnclose;
    FloatingActionButton microComand;
    private static final int CODIGO_RECONOCIMIENTO_VOZ = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private VoiceCommandController controller;
    private ImageView background;
    private LottieAnimationView robotAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_political_security);
        btnclose=findViewById(R.id.buttonCloseSecurity);
        microComand = findViewById(R.id.floatingButtonComands);
        background=findViewById(R.id.backBlack);
        robotAnimation=findViewById(R.id.robot_animation);

        controller = VoiceCommandController.getInstance(this);
        controller.registerActivityCallback(this);
        AppState.getInstance().addObserver(this);

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
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        AppState.getInstance().setModoEdicionActivo(false);
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());
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
    protected void onDestroy() {
        super.onDestroy();
        controller.unregisterActivityCallback(this);
        AppState.getInstance().removeObserver(this);
    }
    public void isCompanyOrBlind(String palabra){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Verificar si el usuario pertenece a la colección "UsernameC"
        FirebaseFirestore.getInstance().collection("UsernameC")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (!document.exists()) {
                                FirebaseFirestore.getInstance().collection("UsernameBlind")
                                        .document(userId)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        NavigationManager.navigateToDestinationBlind(getApplicationContext(), palabra, getSupportFragmentManager(), null);
                                                    }
                                                } else {
                                                    Log.w(TAG, "Error getting document.", task.getException());
                                                }
                                            }
                                        });
                            }
                            else {
                                NavigationManager.navigateToDestinationC(getApplicationContext(), palabra, getSupportFragmentManager(), null);
                            }
                        } else {
                            Log.w(TAG, "Error getting document.", task.getException());
                        }
                    }
                });
    }
    @Override
    public void onVoiceCommandReceived(String command, String predictedCategory) {
        if (AppState.getInstance().isModoEdicionActivo()) {
            //por hacer
            if (command.equalsIgnoreCase("sí")) {
                String response = "Leyendo";
                controller.sendResponseToService(response);
                AppState.getInstance().setModoEdicionActivo(false);
            } else if (command.equalsIgnoreCase("no")) {
                String response = "Cancelando";
                controller.sendResponseToService(response);
                AppState.getInstance().setModoEdicionActivo(false);
            } else {
                String response = "¿Quieres que te lea?, dí, si, o no.";
                controller.sendResponseToService(response);
            }
        } else if (predictedCategory.startsWith("navegacion_")) {
            String destino = extractAfterUnderscore(predictedCategory);
            String respuesta = "Cambiando a " + destino;
            controller.sendResponseToService(respuesta);
            isCompanyOrBlind(destino);
        } else if (predictedCategory.startsWith("accion_")) {
            String accion = extractAfterUnderscore(predictedCategory);
            // Primero navegar a la actividad correcta si es necesario
            AppState.getInstance().setModoEdicionActivo(true);
            isCompanyOrBlind(accion);
        } else {
            String respuesta = "No entiendo ese comando. Por favor, intenta de nuevo.";
            controller.sendResponseToService(respuesta);
        }
    }
    @Override
    public void onActiveAssistantChanged(boolean isActive) {
        if (AppState.getInstance().isModoEdicionActivo()){
            updateRobotAnimationVisibility(true);
        } else updateRobotAnimationVisibility(isActive);
    }
}