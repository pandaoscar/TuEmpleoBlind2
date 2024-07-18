package com.example.tuempleoblind;

import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class NewJob extends AppCompatActivity implements VoiceCommandController.ActivityCallback, AppState.Observer{
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
    List<EditText> editTexts = new ArrayList<>();
    List<Spinner> spinners = new ArrayList<>();
    List<RadioGroup> radioGroups = new ArrayList<>();
    UtilCommandModel.ComponentResult result;
    private String newValue = null;
    private int opcionSpinner = -1;
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
    RadioGroup radioGroupElevator;
    RadioGroup radioGroupRamp;


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

        radioGroupElevator = findViewById(R.id.groupElevatorNewJobC);
        radioGroupRamp = findViewById(R.id.groupRampNewJobC);

        actionPublish();
        // Inflate the layout for this fragment
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());
        if (AppState.getInstance().isActiveAssistant()){
            AppState.getInstance().setModoEdicionActivo(true);
        }
        obtainEditTextAndSpinner();
        radioGroups.add(radioGroupElevator);
        radioGroups.add(radioGroupRamp);
        result = UtilCommandModel.checkComponents(editTexts, spinners);
        controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptyEditText().getHint().toString());

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
    public void onDestroy() {
        super.onDestroy();
        controller.unregisterActivityCallback(this);
        AppState.getInstance().removeObserver(this);
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

                final String companyPublishId = currentUser.getUid();
                final String title = campTextTitle.getText().toString();
                final String description = campTextMultiDescription.getText().toString();
                final String levelEducation = campTextLevelEducation.getText().toString();
                final String experienceLab = campTextExperienceLab.getText().toString();
                final String habilities = campTextHabilities.getText().toString();
                final String salary = campTextSalary.getText().toString();
                final String benefits = campTextBenefits.getText().toString();
                final String location = campTextLocation.getText().toString();
                final String category = spinnerCategory.getSelectedItem().toString();
                final String typeJob = spinnerTypeJob.getSelectedItem().toString();

                boolean checkElevator = radioButtonTrueElevator.isChecked();
                boolean checkRamp = radioButtonTrueRamp.isChecked();

                if (title.isEmpty() || description.isEmpty() || levelEducation.isEmpty() || experienceLab.isEmpty() || habilities.isEmpty() || salary.isEmpty() || location.isEmpty() || category.isEmpty() || typeJob.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Llena todos los campos necesarios", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                postNewJob(companyPublishId, title, description, levelEducation, experienceLab, habilities, salary, benefits, location, category, typeJob, checkElevator, checkRamp);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }, 2000);

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
            command = NavigationManager.eliminarTildes(command);
            if (result == null){
                obtainEditTextAndSpinner();
                result = UtilCommandModel.checkComponents(editTexts, spinners);
                if (result.getEmptyEditText() != null){
                    String response = "Que valor quieres colocarle a " + result.getEmptyEditText().getHint();
                    controller.sendResponseToService(response);
                } else if (result.getEmptySpinner() != null) {
                    controller.sendResponseToService("Que valor quieres colocarle a " + result.getEmptySpinner().getContentDescription());
                }
                else if (!radioGroups.isEmpty()){
                    controller.sendResponseToService("Que valor quieres colocarle a " + radioGroups.get(0).getContentDescription() + ". Dí, si, o no");
                } else {
                    controller.sendResponseToService("Bienvenido");
                    AppState.getInstance().setModoEdicionActivo(false);
                    btnPublish.performClick();
                }

            } else if (newValue == null) {
                if (result.getEmptyEditText() != null){
                    controller.sendResponseToService("¿Estas seguro? Colocaras " + command + ", dí, si, o no.");
                    newValue = command;
                } else if (result.getEmptySpinner() != null) {
                    String response = verifyTypeSpinner(command);
                    controller.sendResponseToService(response);
                } else if (!radioGroups.isEmpty()){
                    if (command.contains("si") || command.contains("se") || command.contains("no")){
                        controller.sendResponseToService("¿Estas seguro? Colocaras " + command + " en " + radioGroups.get(0).getContentDescription() + ", dí, si, o no.");
                        newValue = command;
                    }
                }

            } else if (command.contains("si") || command.contains("se")) {
                if (result.getEmptyEditText() != null){
                    result.getEmptyEditText().setText(newValue);
                } else if (result.getEmptySpinner() != null) {
                    result.getEmptySpinner().setSelection(opcionSpinner);
                } else if (!radioGroups.isEmpty()) {
                    if (newValue.contains("si") || newValue.contains("se")){
                        if (radioGroups.get(0).getContentDescription().equals("Cuenta con Rampas")){
                            radioGroups.get(0).check(R.id.trueRampNewJobC);
                        } else if (radioGroups.get(0).getContentDescription().equals("Cuenta con Elevador")) {
                            radioGroups.get(0).check(R.id.trueElevatorNewJobC);
                        }
                    } else if (newValue.contains("no")) {
                        if (radioGroups.get(0).getContentDescription().equals("Cuenta con Rampas")){
                            radioGroups.get(0).check(R.id.falseRampNewJobC);
                        } else if (radioGroups.get(0).getContentDescription().equals("Cuenta con Elevador")) {
                            radioGroups.get(0).check(R.id.falseElevatorNewJobC);
                        }
                    }
                    radioGroups.remove(0);
                }
                result = null;
                newValue = null;
                onVoiceCommandReceived("siguiente", "comando no reconocido");
            } else if (command.contains("no")) {
                String response = "Entonces, ¿Que valor quieres colocar?";
                newValue = null;
                if (result.getEmptyEditText() != null){
                    if (result.getEmptyEditText().getHint().toString().toLowerCase().contains("telefono")){
                        AppState.getInstance().setActiveAssistant(false);
                        controller.sendGoogleAlert(response);
                    } else controller.sendResponseToService(response);
                }
            } else{
                controller.sendResponseToService("¿Quieres colocar " + newValue + "?" + ", dí, si, o no.");
            }
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
    private String verifyTypeSpinner(String command) {
        if (command.contains("tecnologia")){
            newValue = "Tecnología";
            opcionSpinner = 1;
            return "¿Estas seguro? Colocaras " + newValue + ", dí, si, o no.";
        } else if (command.contains("finanzas")) {
            newValue = "Finanzas";
            opcionSpinner = 2;
            return "¿Estas seguro? Colocaras " + newValue + ", dí, si, o no.";
        } else if (command.contains("salud") || command.contains("bienestar")) {
            newValue = "Salud y bienestar";
            opcionSpinner = 3;
            return "¿Estas seguro? Colocaras " + newValue + ", dí, si, o no.";
        } else if (command.contains("marketing") || command.contains("publicidad")) {
            newValue = "Marketing y publicidad";
            opcionSpinner = 4;
            return "¿Estas seguro? Colocaras " + newValue + ", dí, si, o no.";
        } else if (command.contains("ventas") || command.contains("atencion")  || command.contains("cliente")) {
            newValue = "Ventas y atención al cliente";
            opcionSpinner = 5;
            return "¿Estas seguro? Colocaras " + newValue + ", dí, si, o no.";
        } if (command.contains("arte") || command.contains("entretenimiento")) {
            newValue = "Arte y entretenimiento";
            opcionSpinner = 6;
            return "¿Estas seguro? Colocaras " + newValue + ", dí, si, o no.";
        } if (command.contains("ingeniería") || command.contains("manufactura")) {
            newValue = "Ingeniería y manufactura";
            opcionSpinner = 7;
            return "¿Estas seguro? Colocaras " + newValue + ", dí, si, o no.";
        } if (command.contains("recursos") || command.contains("humanos")  || command.contains("reclutamiento")) {
            newValue = "Recursos humanos y reclutamiento";
            opcionSpinner = 8;
            return "¿Estas seguro? Colocaras " + newValue + ", dí, si, o no.";
        } if (command.contains("ciencia") || command.contains("tecnología")) {
            newValue = "Ciencia y tecnología";
            opcionSpinner = 9;
            return "¿Estas seguro? Colocaras " + newValue + ", dí, si, o no.";
        } if (command.contains("otro")) {
            newValue = "Otro";
            opcionSpinner = 10;
            return "¿Estas seguro? Colocaras " + newValue + ", dí, si, o no.";
        } else {
            return verifyTypeSpinnerType(command);
        }
    }

    public String verifyTypeSpinnerType(String command){
        if (command.contains("medio tiempo")) {
            newValue = "Medio Tiempo";
            opcionSpinner = 1;
            return "¿Estas seguro? Colocaras " + newValue + ", dí, si, o no.";
        } if (command.contains("tiempo completo")) {
            newValue = "Tiempo Completo";
            opcionSpinner = 2;
            return "¿Estas seguro? Colocaras " + newValue + ", dí, si, o no.";
        } if (command.contains("tiempo parcial")) {
            newValue = "Tiempo Parcial";
            opcionSpinner = 3;
            return "¿Estas seguro? Colocaras " + newValue + ", dí, si, o no.";
        } else {
            newValue = null;
            return "No entendí, prueba de nuevo ";
        }
    }
    private void obtainEditTextAndSpinner() {
        spinners.clear();
        editTexts.clear();

        spinners.add(spinnerCategory);
        spinners.add(spinnerTypeJob);
        editTexts.add(campTextTitle);
        editTexts.add(campTextMultiDescription);
        editTexts.add(campTextLevelEducation);
        editTexts.add(campTextExperienceLab);
        editTexts.add(campTextHabilities);
        editTexts.add(campTextSalary);
        editTexts.add(campTextBenefits);
        editTexts.add(campTextLocation);
    }

    @Override
    public void onActiveAssistantChanged(boolean isActive) {

    }
}