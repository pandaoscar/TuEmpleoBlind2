package com.example.tuempleoblind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import pub.devrel.easypermissions.EasyPermissions;

public class NewJob extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    FirebaseFirestore mFirestore;
    Button btnback, btnPublish;
    FloatingActionButton microComand;
    private static final int CODIGO_RECONOCIMIENTO_VOZ = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;
    Spinner spinnerCategory, spinnerTypeJob;
    EditText campTextTitle, campTextMultiDescription, campTextLevelEducation, campTextExperienceLab, campTextHabilities, campTextSalary,
            campTextBenefits, campTextLocation;

    RadioButton radioButtonTrueElevator, radioButtonTrueRamp, radioButtonFalseElevator, radioButtonFalseRamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job);

        mFirestore = FirebaseFirestore.getInstance();

        btnPublish = findViewById(R.id.buttonPublishNewJobC);
        btnback = findViewById(R.id.buttonBackNewJobC);
        microComand = findViewById(R.id.floatingButtonComands);

        campTextTitle = findViewById(R.id.editTextTittleNewJobC);
        campTextMultiDescription = findViewById(R.id.editTextMultiDescripNewJobC);
        campTextLevelEducation = findViewById(R.id.editTextLevelEduNewJobC);
        campTextExperienceLab = findViewById(R.id.editTextExperienceNewJobC);
        campTextHabilities = findViewById(R.id.editTextHabilitiesNewJobC);
        campTextSalary = findViewById(R.id.editTextSalaryNewJobC);
        campTextBenefits = findViewById(R.id.editTextBenefitsNewJobC);
        campTextLocation = findViewById(R.id.editTextLocationNewJobC);

        radioButtonTrueElevator = findViewById(R.id.trueElevatorNewJobC);
        radioButtonTrueRamp = findViewById(R.id.trueRampNewJobC);
        radioButtonFalseElevator = findViewById(R.id.falseElevatorNewJobC);
        radioButtonFalseRamp = findViewById(R.id.falseRampNewJobC);

        microComand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spinnerCategory =findViewById(R.id.spinnerCategoryNewJobC);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.lista_categoria_empleo,R.layout.style_spinner);
        adapter.setDropDownViewResource(R.layout.style_spinner);
        spinnerCategory.setAdapter(adapter);

        spinnerTypeJob =findViewById(R.id.spinnerTypeJobNewJobC);
        ArrayAdapter<CharSequence> adapter2=ArrayAdapter.createFromResource(this,R.array.lista_typo_empleo,R.layout.style_spinner);
        adapter2.setDropDownViewResource(R.layout.style_spinner);
        spinnerTypeJob.setAdapter(adapter2);

        actionPublish();

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
                if (palabrasReconocidas != null && palabrasReconocidas.size() > 0) {
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

    private void actionPublish() {
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = ProgressDialog.show(NewJob.this, "", "Registrando usuario...", true);
                progressDialog.setCancelable(false);
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                final String companyPublishId,title, description, levelEducation, experienceLab, habilities, salary, benefits, location, category, typeJob;
                companyPublishId=currentUser.getUid().toString();
                title = campTextTitle.getText().toString();
                description = campTextMultiDescription.getText().toString();
                levelEducation = campTextLevelEducation.getText().toString();
                experienceLab = campTextExperienceLab.getText().toString();
                habilities = campTextHabilities.getText().toString();
                salary = campTextSalary.getText().toString();
                benefits = campTextBenefits.getText().toString();
                location = campTextLocation.getText().toString();
                category = spinnerCategory.getSelectedItem().toString();
                typeJob = spinnerTypeJob.getSelectedItem().toString();

                boolean checkElevator = false, checkRamp = false;
                if(radioButtonTrueElevator.isChecked()){
                    checkElevator = true;
                    progressDialog.dismiss();
                }

                if(radioButtonTrueRamp.isChecked()){
                    checkRamp = true;
                    progressDialog.dismiss();
                }

                if(title.isEmpty() || description.isEmpty() || levelEducation.isEmpty() || experienceLab.isEmpty() || habilities.isEmpty() || salary.isEmpty() || location.isEmpty() || category.isEmpty() || typeJob.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Llena todos los campos necesarios", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else{
                    postNewJob(companyPublishId,title, description, levelEducation, experienceLab, habilities, salary, benefits, location, category,
                            typeJob, checkElevator, checkRamp);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    },2000);
                }

            }

            private void postNewJob(String companyPublishId, String title, String description, String levelEducation, String experienceLab, String habilities, String salary, String benefits, String location, String category, String typeJob, boolean checkElevator, boolean checkRamp) {
                Map<String, Object> jobPublish = new HashMap<>();
                jobPublish.put("companyPublishId", companyPublishId);
                jobPublish.put("title", title);
                jobPublish.put("description", description);
                jobPublish.put("levelEducation", levelEducation);
                jobPublish.put("experienceLab", experienceLab);
                jobPublish.put("habilities", habilities);
                jobPublish.put("salary", salary);
                jobPublish.put("benefits", benefits);
                jobPublish.put("location", location);
                jobPublish.put("category", category);
                jobPublish.put("typeJob", typeJob);
                jobPublish.put("checkElevator", checkElevator);
                jobPublish.put("checkRamp", checkRamp);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userID = user.getUid();
                mFirestore.collection("TrabajosPublicados").add(jobPublish).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String jobId = documentReference.getId();
                        Toast.makeText(getApplicationContext(), "Trabajo publicado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), companyHome.class);
                        intent.putExtra("userID", userID);
                        intent.putExtra("jobID", jobId);
                        startActivity(intent);
                        finish();


                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}