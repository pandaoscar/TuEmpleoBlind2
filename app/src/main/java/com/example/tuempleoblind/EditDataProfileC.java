package com.example.tuempleoblind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditDataProfileC extends AppCompatActivity {

    EditText campTextName, campTextUserName, campTextEmail, campTextNameCompany, campTextCompanyType, campTextLocation, campTextWebPag;
    Button btnSave, btnCancel;
    FirebaseFirestore mFirestore;
    private FirebaseAuth mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data_profile_c);

        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance();


        campTextName = findViewById(R.id.editTextNameEditDataC);
        campTextUserName = findViewById(R.id.editTextUserNameEditDataC);
        campTextEmail = findViewById(R.id.editTextEmailEditDataC);
        campTextNameCompany = findViewById(R.id.editTextNameCompanyEditDataC);
        campTextCompanyType = findViewById(R.id.editTextCompanyTypeEditDataC);
        campTextLocation = findViewById(R.id.editTextLocationEditDataC);
        campTextWebPag = findViewById(R.id.editTextWebPagEditDataC);

        campTextNameCompany.addTextChangedListener(textWatcher);

        btnSave = findViewById(R.id.buttonSaveEditDataC);
        btnCancel = findViewById(R.id.buttonCancelEditDataC);

        btnSave.setEnabled(false);

        obtenerValoresFirestore();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = campTextName.getText().toString();
                final String username = campTextUserName.getText().toString();
                final String email = campTextEmail.getText().toString();
                final String nameCompany = campTextNameCompany.getText().toString();
                final String typeCompany = campTextCompanyType.getText().toString();
                final String location = campTextLocation.getText().toString();
                final String webPag = campTextWebPag.getText().toString();

                if(name.isEmpty() || username.isEmpty() || email.isEmpty() || nameCompany.isEmpty() || typeCompany.isEmpty() || location.isEmpty() || webPag.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Completa todos los datos correspondientes", Toast.LENGTH_SHORT).show();

                }
                else{
                    postUsernameC(name, username, email, nameCompany, typeCompany, location, webPag, mUser.getUid());
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), companyHome.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void postUsernameC(String name, String username, String email, String nameCompany, String typeCompany, String location, String webPag, String userID) {
        Map<String, Object> map = new HashMap<>();
        map.put("Nombre", name);
        map.put("Usuario", username);
        map.put("Correo Electronico", email);
        map.put("Nombre de la compañia", nameCompany);
        map.put("Pagina web", webPag);
        map.put("Tipo de compañia", typeCompany);
        map.put("Ubicación", location);

        mFirestore.collection("UsernameC").document(userID)
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Datos del usuario guardados correctamente", Toast.LENGTH_SHORT).show();
                        // Continuar con la lógica de tu aplicación
                        Intent intent = new Intent(getApplicationContext(), companyHome.class);
                        intent.putExtra("userID", userID);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void obtenerValoresFirestore() {
        // Obtener el documento deseado de Firestore
        FirebaseUser user = mUser.getCurrentUser();
        DocumentReference docRef = mFirestore.collection("UsernameC").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener los valores del documento
                    String email = documentSnapshot.getString("Correo Electronico");
                    String name = documentSnapshot.getString("Nombre");
                    String nameCompany = documentSnapshot.getString("Nombre de la compañia");
                    String webPag = documentSnapshot.getString("Pagina web");
                    String typeCompany = documentSnapshot.getString("Tipo de compañia");
                    String location = documentSnapshot.getString("Ubicación");
                    String username = documentSnapshot.getString("Usuario");

                    // Establecer los valores en los EditText
                    campTextEmail.setText(email);
                    campTextName.setText(name);
                    campTextNameCompany.setText(nameCompany);
                    campTextWebPag.setText(webPag);
                    campTextCompanyType.setText(typeCompany);
                    campTextLocation.setText(location);
                    campTextUserName.setText(username);

                    // Agregar un TextWatcher a cada EditText
                    campTextEmail.addTextChangedListener(textWatcher);
                    campTextName.addTextChangedListener(textWatcher);
                    campTextNameCompany.addTextChangedListener(textWatcher);
                    campTextWebPag.addTextChangedListener(textWatcher);
                    campTextCompanyType.addTextChangedListener(textWatcher);
                    campTextLocation.addTextChangedListener(textWatcher);
                    campTextUserName.addTextChangedListener(textWatcher);

                    verificarCambios();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Toast.makeText(getApplicationContext(), "oñis", Toast.LENGTH_SHORT).show();
            verificarCambios();
        }
    };

    // Método para verificar si ha habido cambios en los EditText
    private void verificarCambios() {
        // Obtener los valores actuales de los EditText
        String emailEdit = campTextEmail.getText().toString();
        String nameEdit = campTextName.getText().toString();
        String nameCompanyEdit = campTextNameCompany.getText().toString();
        String webPagEdit = campTextWebPag.getText().toString();
        String typeCompanyEdit = campTextCompanyType.getText().toString();
        String locationEdit = campTextLocation.getText().toString();
        String usernameEdit = campTextUserName.getText().toString();


        // Obtener los valores almacenados en Firestore
        DocumentReference docRef = mFirestore.collection("UsernameC").document(mUser.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener los valores del documento
                    String emailFire = documentSnapshot.getString("Correo Electronico");
                    String nameFire = documentSnapshot.getString("Nombre");
                    String nameCompanyFire = documentSnapshot.getString("Nombre de la compañia");
                    String webPagFire = documentSnapshot.getString("Pagina web");
                    String typeCompanyFire = documentSnapshot.getString("Tipo de compañia");
                    String locationFire = documentSnapshot.getString("Ubicación");
                    String usernameFire = documentSnapshot.getString("Usuario");

                    // Verificar si hay cambios en los EditText
                    if (!emailEdit.equals(emailFire) || !nameEdit.equals(nameFire) ||
                            !nameCompanyEdit.equals(nameCompanyFire) || !webPagEdit.equals(webPagFire) ||
                            !typeCompanyEdit.equals(typeCompanyFire) || !locationEdit.equals(locationFire) ||
                            !usernameEdit.equals(usernameFire)) {
                        // Si hay cambios, habilitar el botón
                        btnSave.setEnabled(true);
                    } else {
                        // Si no hay cambios, deshabilitar el botón
                        btnSave.setEnabled(false);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}