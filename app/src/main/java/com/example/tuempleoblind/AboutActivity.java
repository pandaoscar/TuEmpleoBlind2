package com.example.tuempleoblind;

import static android.content.ContentValues.TAG;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class AboutActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    Button btnclose;
    FloatingActionButton microComand;
    private static final int CODIGO_RECONOCIMIENTO_VOZ = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        btnclose=findViewById(R.id.buttonCloseAbout);
        microComand = findViewById(R.id.floatingButtonComands);

        microComand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
            }
        });
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            finish();
            }
        });
    }
    private void checkAndRequestPermissions() {
        if (EasyPermissions.hasPermissions(getApplicationContext(), android.Manifest.permission.RECORD_AUDIO)) {
            // Permission already granted, perform operation
            Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
            iniciarReconocimientoVoz();
        } else {
            // Request permissions
            EasyPermissions.requestPermissions(this, "Porfavor acepta los permisos del microfono", PERMISSION_REQUEST_CODE, Manifest.permission.RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission is denied
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // Permission granted, handle accordingly
        Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // Permission denied, handle accordingly
        Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
    }



    private void iniciarReconocimientoVoz() {
        // Crea un Intent para el reconocimiento de voz
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Configura el idioma para el reconocimiento (puedes cambiarlo según tus necesidades)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Configura un mensaje para el usuario
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di algo...");
        // Inicia la actividad de reconocimiento de voz y espera los resultados
        startActivityForResult(intent, CODIGO_RECONOCIMIENTO_VOZ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_RECONOCIMIENTO_VOZ) {
            if (resultCode == RESULT_OK && data != null) {
                // Obtiene la lista de palabras reconocidas
                ArrayList<String> palabrasReconocidas = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (palabrasReconocidas != null && palabrasReconocidas.isEmpty()) {
                    // Guarda la primera palabra reconocida en un String
                    String palabra = palabrasReconocidas.toString().replace("[", "").replace("]", "");
                    isCompanyOrBlind(palabra);
                }
            } else {
                // Mensaje de error si el reconocimiento de voz no fue exitoso
                Toast.makeText(getApplicationContext(), "Error en el reconocimiento de comandos", Toast.LENGTH_SHORT).show();
            }
        }
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
}