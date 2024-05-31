package com.example.tuempleoblind;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;

public class EditDataProfileC extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String FIELD_COLLECTION_C="UsernameC";
    private static final String FIELD_NAME_C = "Nombre";
    private static final String FIELD_USERNAME_C = "Usuario";
    private static final String FIELD_EMAIL_C = "Correo Electronico";
    private static final String FIELD_COMPANY_NAME_C = "Nombre de la compañia";
    private static final String FIELD_COMPANY_TYPE_C = "Tipo de compañia";
    private static final String FIELD_LOCATION_C = "Ubicación";
    private static final String FIELD_WEB_PAGE_C = "Pagina web";

    EditText campTextName;
    EditText campTextUserName;
    EditText campTextEmail;
    EditText campTextNameCompany;
    EditText campTextCompanyType;
    EditText campTextLocation;
    EditText campTextWebPag;
    Button btnSave;
    Button btnCancel;
    FloatingActionButton microComand;
    FirebaseFirestore mFirestore;
    private FirebaseAuth mUser;
    private static final int CODIGO_RECONOCIMIENTO_VOZ = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;

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
        microComand = findViewById(R.id.floatingButtonComands);

        btnSave.setEnabled(false);
        btnSave.setBackgroundResource(R.drawable.btn_disabled);
        btnSave.setTextColor(getResources().getColor(R.color.litle_color));
        obtenerValoresFirestore();

        microComand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
            }
        });

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
                finish();
            }
        });

    }

    private void checkAndRequestPermissions() {
        if (EasyPermissions.hasPermissions(getApplicationContext(), Manifest.permission.RECORD_AUDIO)) {
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
                    NavigationManager.navigateToDestinationC(getApplicationContext(), palabra, getSupportFragmentManager(), null);
                }
            } else {
                // Mensaje de error si el reconocimiento de voz no fue exitoso
                Toast.makeText(getApplicationContext(), "Error en el reconocimiento de comandos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void postUsernameC(String name, String username, String email, String nameCompany, String typeCompany, String location, String webPag, String userID) {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_NAME_C, name);
        map.put(FIELD_USERNAME_C, username);
        map.put(FIELD_EMAIL_C, email);
        map.put(FIELD_COMPANY_NAME_C, nameCompany);
        map.put(FIELD_WEB_PAGE_C, webPag);
        map.put(FIELD_COMPANY_TYPE_C, typeCompany);
        map.put(FIELD_LOCATION_C, location);

        mFirestore.collection(FIELD_COLLECTION_C).document(userID)
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
        DocumentReference docRef = mFirestore.collection(FIELD_COLLECTION_C).document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener los valores del documento
                    String email = documentSnapshot.getString(FIELD_EMAIL_C);
                    String name = documentSnapshot.getString(FIELD_NAME_C);
                    String nameCompany = documentSnapshot.getString(FIELD_COMPANY_NAME_C);
                    String webPag = documentSnapshot.getString(FIELD_WEB_PAGE_C);
                    String typeCompany = documentSnapshot.getString(FIELD_COMPANY_TYPE_C);
                    String location = documentSnapshot.getString(FIELD_LOCATION_C);
                    String username = documentSnapshot.getString(FIELD_USERNAME_C);

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
        DocumentReference docRef = mFirestore.collection(FIELD_COLLECTION_C).document(mUser.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener los valores del documento
                    String emailFire = documentSnapshot.getString(FIELD_EMAIL_C);
                    String nameFire = documentSnapshot.getString(FIELD_NAME_C);
                    String nameCompanyFire = documentSnapshot.getString(FIELD_COMPANY_NAME_C);
                    String webPagFire = documentSnapshot.getString(FIELD_WEB_PAGE_C);
                    String typeCompanyFire = documentSnapshot.getString(FIELD_COMPANY_TYPE_C);
                    String locationFire = documentSnapshot.getString(FIELD_LOCATION_C);
                    String usernameFire = documentSnapshot.getString(FIELD_USERNAME_C);

                    // Verificar si hay cambios en los EditText
                    if (!emailEdit.equals(emailFire) || !nameEdit.equals(nameFire) ||
                            !nameCompanyEdit.equals(nameCompanyFire) || !webPagEdit.equals(webPagFire) ||
                            !typeCompanyEdit.equals(typeCompanyFire) || !locationEdit.equals(locationFire) ||
                            !usernameEdit.equals(usernameFire)) {
                        // Si hay cambios, habilitar el botón
                        btnSave.setEnabled(true);
                        btnSave.setBackgroundResource(R.drawable.btn_orange);
                        btnSave.setTextColor(getResources().getColor(R.color.ghost_white));
                    } else {
                        // Si no hay cambios, deshabilitar el botón
                        btnSave.setEnabled(false);
                        btnSave.setBackgroundResource(R.drawable.btn_disabled);
                        btnSave.setTextColor(getResources().getColor(R.color.litle_color));
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