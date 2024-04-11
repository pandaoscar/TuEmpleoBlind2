package com.example.tuempleoblind;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button btnFindJob, btnFindHire, btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFindJob = findViewById(R.id.buttonFindJob);
        btnFindHire = findViewById(R.id.buttonFindHire);
        btnLogIn = findViewById(R.id.buttonLogIn);
        login();
        hire();
        job();
    }

    private void login() {
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignUpBlindForm.class));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Verificar si el usuario ya está autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // El usuario ya está autenticado, redirigir al menú principal
            startActivity(new Intent(this, companyHome.class));
            finish(); // Finalizar la actividad actual para que no pueda volver atrás

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
}