package com.example.tuempleoblind;

import static android.content.ContentValues.TAG;

import static com.example.tuempleoblind.NavigationManager.eliminarTildes;
import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

public class AboutActivity extends AppCompatActivity implements VoiceCommandController.ActivityCallback{
    Button btnclose;
    FloatingActionButton microComand;
    private VoiceCommandController controller;
    private ImageView background;
    private LottieAnimationView robotAnimation;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        btnclose=findViewById(R.id.buttonCloseAbout);
        microComand = findViewById(R.id.floatingButtonComands);
        background=findViewById(R.id.backBlack);
        robotAnimation=findViewById(R.id.robot_animation);

        controller = VoiceCommandController.getInstance(this);
        controller.registerActivityCallback(this);

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
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());
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
    protected void onDestroy() {
        super.onDestroy();
        controller.unregisterActivityCallback(this);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, CompanyHome.class);
        startActivity(intent);
        finish();
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
        if (predictedCategory.startsWith("navegacion_")) {
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
            if(command.equals("4p4g4d0_4ut0m4t1c0")&& predictedCategory.equals("4p4g4d0_10s3gund0s")){
                updateRobotAnimationVisibility(false);
            }else{
                String respuesta = "No entiendo ese comando. Por favor, intenta de nuevo.";
                controller.sendResponseToService(respuesta);
                updateRobotAnimationVisibility(false);}
        }
    }
}