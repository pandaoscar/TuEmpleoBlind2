package com.example.tuempleoblind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogIn extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText campTextEmail;
    EditText campTextPassword;
    Button btnConfirm;
    Button btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();

        campTextEmail = findViewById(R.id.editTextEmailLogIn);
        campTextPassword = findViewById(R.id.editTextPasswordLogIn);
        btnConfirm = findViewById(R.id.buttonConfirmLogIn);
        btnBack = findViewById(R.id.buttonBackLogIn);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish(); // Finalizar la actividad de inicio de sesión
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login(campTextEmail.getText().toString(), campTextPassword.getText().toString());
            }
        });


    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRefUsernameBlind = db.collection("UsernameBlind").document(user.getUid());
                        DocumentReference docRefUsernameC = db.collection("UsernameC").document(user.getUid());
                        checkUserCollection(docRefUsernameBlind, docRefUsernameC);
                    } else {
                        Toast.makeText(getApplicationContext(), "Inicio de sesión fallido.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserCollection(DocumentReference docRefUsernameBlind, DocumentReference docRefUsernameC) {
        docRefUsernameBlind.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentBlind = task.getResult();
                if (documentBlind.exists()) {
                    navigateToHomePage(HomePageBlind.class);
                } else {
                    checkCompanyCollection(docRefUsernameC);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error: No se pudo obtener el documento del usuario de UsernameBlind.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkCompanyCollection(DocumentReference docRefUsernameC) {
        docRefUsernameC.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentC = task.getResult();
                if (documentC.exists()) {
                    navigateToHomePage(CompanyHome.class);
                } else {
                    Toast.makeText(getApplicationContext(), "Usuario no encontrado en ninguna colección.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error: No se pudo obtener el documento del usuario de UsernameC.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHomePage(Class<?> homePageClass) {
        startActivity(new Intent(getApplicationContext(), homePageClass));
        finish();
    }



}