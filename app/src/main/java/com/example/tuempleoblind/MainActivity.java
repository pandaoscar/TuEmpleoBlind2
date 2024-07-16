package com.example.tuempleoblind;



import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import pub.devrel.easypermissions.EasyPermissions;
import android.Manifest;

public class MainActivity extends AppCompatActivity implements VoiceCommandController.ActivityCallback {

    Button btnFindJob;
    Button btnFindHire;
    Button btnLogIn;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private VoiceCommandController controller;
    private LottieAnimationView robotAnimation;
    private ImageView background;
    FloatingActionButton microComand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = VoiceCommandController.getInstance(this);
        controller.registerActivityCallback(this);
        microComand = findViewById(R.id.floatingButtonComands);

        robotAnimation=findViewById(R.id.robot_animation);
        background=findViewById(R.id.backBlack);
        controller.sendRoleUser(null);

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
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());

        btnFindJob = findViewById(R.id.buttonFindJob);
        btnFindHire = findViewById(R.id.buttonFindHire);
        btnLogIn = findViewById(R.id.buttonLogIn);



        login();
        hire();
        job();

        controller.sendRoleUser("unLogin");
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
    }
    public void activarTalkback(View view){
        try {
            // Verifica si el TalkBack ya está activado
            if (!esTalkBackActivado()) {
                // Activa el TalkBack
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                // Muestra un mensaje de éxito
                Toast.makeText(this, "Por favor activa el TalkBakc", Toast.LENGTH_SHORT).show();
            } else {
                // Muestra un mensaje indicando que el TalkBack ya está activado
                Toast.makeText(this, "TalkBack ya está activado", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Muestra un mensaje de error si hay algún problema
            Toast.makeText(this, "Error al activar TalkBack: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private boolean esTalkBackActivado() {
        String enabledListeners = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );
        return enabledListeners != null && enabledListeners.contains("com.google.android.marvin.talkback/");
    }
    private void login() {
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LogIn.class));
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, NewJobPublishedNotification.class));
        startService(new Intent(this, VoiceService.class));
        controller.sendRoleUser(null);
        // Verificar si el usuario ya está autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Obtener una instancia de Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Obtener referencias a los documentos en ambas colecciones
            DocumentReference docRefUsernameBlind = db.collection("UsernameBlind").document(currentUser.getUid());
            DocumentReference docRefUsernameC = db.collection("UsernameC").document(currentUser.getUid());

            // Verificar si existe el documento en UsernameBlind
            docRefUsernameBlind.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentBlind = task.getResult();
                        if (documentBlind.exists()) {

                            // El usuario pertenece a la colección UsernameBlind
                            startActivity(new Intent(getApplicationContext(), HomePageBlind.class));
                            finish(); // Finalizar la actividad de inicio de sesión
                        } else {
                            // Verificar si existe el documento en UsernameC
                            docRefUsernameC.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentC = task.getResult();
                                        if (documentC.exists()) {
                                            // El usuario pertenece a la colección UsernameC

                                            startActivity(new Intent(getApplicationContext(), CompanyHome.class));
                                            finish(); // Finalizar la actividad de inicio de sesión
                                        } else {
                                            // No se encontró el usuario en ninguna colección
                                            Toast.makeText(getApplicationContext(), "Usuario no encontrado en ninguna colección.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Error al obtener el documento de UsernameC
                                        Toast.makeText(getApplicationContext(), "Error: No se pudo obtener el documento del usuario de UsernameC.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        // Error al obtener el documento de UsernameBlind
                        Toast.makeText(getApplicationContext(), "Error: No se pudo obtener el documento del usuario de UsernameBlind.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else{
            checkAndRequestPermissions();
        }
    }


    private void hire() {
        btnFindHire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpC.class));
                finish();
            }
        });
    }

    private void job() {
        btnFindJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpBlind.class));
                finish();
            }
        });
    }
    private void checkAndRequestPermissions() {
        if (EasyPermissions.hasPermissions(getApplicationContext(), android.Manifest.permission.RECORD_AUDIO)) {
            // Permission already granted, perform operation
            Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_SHORT).show();

        } else {
            // Request permissions
            EasyPermissions.requestPermissions(this, "Porfavor acepta los permisos del microfono", PERMISSION_REQUEST_CODE, Manifest.permission.RECORD_AUDIO);
        }
    }

    @Override
    public void onVoiceCommandReceived(String command, String predictedCategory) {
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
