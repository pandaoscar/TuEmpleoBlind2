package com.example.tuempleoblind;

import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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

public class NewJob extends AppCompatActivity implements VoiceCommandController.ActivityCallback{
    private static final String NUMERO_DE_EMPLOS_PUBLICADOS = "numeroDeEmpleoPublicados";
    private static final String NUMERO_DE_EMPLEOS_PUBLICADOS_TOTALES = "numeroDeEmpleoPublicadosTotales";
    private static final String COLLECTION_REPORTE = "Reporte";
    private static final String DOCUMENT_TOTALES = "Totales";

    FirebaseFirestore mFirestore;
    Button btnback;
    Button btnPublish;
    FloatingActionButton microComand;
    private VoiceCommandController controller;
    private LottieAnimationView robotAnimation;
    private ImageView background;
    private static final int CODIGO_RECONOCIMIENTO_VOZ = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;
    Spinner spinnerCategory;
    Spinner spinnerTypeJob;
    EditText campTextTitle;
    EditText campTextMultiDescription;
    EditText campTextLevelEducation;
    EditText campTextExperienceLab;
    EditText campTextHabilities;
    EditText campTextSalary;
    EditText campTextBenefits;
    EditText campTextLocation;

    RadioButton radioButtonTrueElevator;
    RadioButton radioButtonTrueRamp;
    RadioButton radioButtonFalseElevator;
    RadioButton radioButtonFalseRamp;


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

        robotAnimation=findViewById(R.id.robot_animation);
        background=findViewById(R.id.backBlack);

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
        // Inflate the layout for this fragment
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
    public void onDestroy() {
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


    private void actionPublish() {
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = ProgressDialog.show(NewJob.this, "", "Registrando usuario...", true);
                progressDialog.setCancelable(false);
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                final String companyPublishId;
                final String title;
                final String description;
                final String levelEducation;
                final String experienceLab;
                final String habilities;
                final String salary;
                final String benefits;
                final String location;
                final String category;
                final String typeJob;
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

                boolean checkElevator = false;
                boolean checkRamp = false;
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
                        Intent intent = new Intent(getApplicationContext(), CompanyHome.class);
                        intent.putExtra("userID", userID);
                        intent.putExtra("jobID", jobId);
                        Utilidad.incrementarMensual(mFirestore,COLLECTION_REPORTE,NUMERO_DE_EMPLOS_PUBLICADOS);
                        Utilidad.incrementarTotal(mFirestore,COLLECTION_REPORTE,DOCUMENT_TOTALES,NUMERO_DE_EMPLEOS_PUBLICADOS_TOTALES);
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

    @Override
    public void onVoiceCommandReceived(String command, String predictedCategory) {
        if (AppState.getInstance().isModoEdicionActivo()) {
            //por hacer
        } else {
            if (predictedCategory.startsWith("navegacion_")) {
                String destino = extractAfterUnderscore(predictedCategory);
                String respuesta = "Cambiando a " + destino;
                controller.sendResponseToService(respuesta);
                NavigationManager.navigateToDestinationC(this, destino, getSupportFragmentManager(), null);
            } else if (predictedCategory.startsWith("accion_")) {
                String accion = extractAfterUnderscore(predictedCategory);
                AppState.getInstance().setModoEdicionActivo(true);
                NavigationManager.navigateToDestinationC(this, accion, getSupportFragmentManager(), null);
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
}