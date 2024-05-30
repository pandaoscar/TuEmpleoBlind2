package com.example.tuempleoblind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JobDetails extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private void incrementarMensual() {
        CollectionReference reporteRef = db.collection("Reporte");
        Calendar calendar = Calendar.getInstance();
        String monthYear = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault()).format(calendar.getTime());
        reporteRef.document(monthYear).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Verifica si el campo "numeroDeAspirantesPostulados" existe
                        if (document.contains("numeroDeAspirantesPostulados")) {
                            // Si el campo existe, obtén el número actual de empleos publicados y añade uno
                            long currentJobs = document.getLong("numeroDeAspirantesPostulados");
                            reporteRef.document(monthYear).update("numeroDeAspirantesPostulados", currentJobs + 1);
                        } else {
                            // Si el campo no existe, crea uno con numeroDeAspirantesPostulados = 1
                            reporteRef.document(monthYear).update("numeroDeAspirantesPostulados", 1);
                        }
                    } else {
                        // Si el documento no existe, crea uno con numeroDeAspirantesPostulados = 1
                        Map<String, Object> data = new HashMap<>();
                        data.put("numeroDeAspirantesPostulados", 1);
                        reporteRef.document(monthYear).set(data);
                    }
                } else {
                    Log.d("Error obteniendo documentos:", String.valueOf(task.getException()));
                }
            }
        });
    }
    private void incrementarTotal() {
        CollectionReference reporteRef = db.collection("Reporte");
        reporteRef.document("Totales").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Verifica si el campo "numeroDeAspirantesPostulados" existe
                        if (document.contains("numeroDeAspirantesPostuladosTotales")) {
                            // Si el campo existe, obtén el número actual de empleos publicados y añade uno
                            long currentJobs = document.getLong("numeroDeAspirantesPostuladosTotales");
                            reporteRef.document("Totales").update("numeroDeAspirantesPostuladosTotales", currentJobs + 1);
                        } else {
                            // Si el campo no existe, crea uno con numeroDeAspirantesPostulados = 1
                            reporteRef.document("Totales").update("numeroDeAspirantesPostuladosTotales", 1);
                        }
                    } else {
                        // Si el documento no existe, crea uno con numeroDeAspirantesPostulados = 1
                        Map<String, Object> data = new HashMap<>();
                        data.put("numeroDeAspirantesPostuladosTotales", 1);
                        reporteRef.document("Totales").set(data);
                    }
                } else {
                    Log.d("Error obteniendo documentos:", String.valueOf(task.getException()));
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
        if (getIntent().hasExtra("jobID")) {
            // Si se ha pasado el extra "jobID", obtener su valor
            String jobID = getIntent().getStringExtra("jobID");

            // Aquí puedes hacer lo que necesites con el ID del trabajo (jobID)
            // Por ejemplo, puedes utilizarlo para obtener los detalles del trabajo desde Firestore
        } else {
            // Si no se ha pasado el extra "jobID", mostrar un mensaje de error o tomar alguna acción
            Toast.makeText(this, "No se ha proporcionado el ID del trabajo.", Toast.LENGTH_SHORT).show();
            finish(); // Finalizar la actividad si no se proporciona el ID del trabajo
        }

        db=FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        // Obtener los datos de la intención
        String title=getIntent().getStringExtra("title");
        TextView jobDetailTitle=findViewById(R.id.jobDetailsTitle);

        String category=getIntent().getStringExtra("category");
        TextView jobDetailCategory=findViewById(R.id.jobDetailsCategory);

        String salary=getIntent().getStringExtra("salary");
        TextView jobDetailSalary=findViewById(R.id.jobDetailsSalary);

        String typeJob=getIntent().getStringExtra("typeJob");
        TextView jobDetailTypeJob=findViewById(R.id.jobDetailsTypeJob);

        String description=getIntent().getStringExtra("description");
        TextView jobDetailDescription=findViewById(R.id.jobDetailsDescription);

        String levelEducation=getIntent().getStringExtra("levelEducation");
        TextView jobDetailLevelEducation=findViewById(R.id.jobDetailsLevelEducation);

        String experienceLab=getIntent().getStringExtra("experienceLab");
        TextView jobDetailExperienceLab=findViewById(R.id.jobDetailsExperience);

        String location=getIntent().getStringExtra("location");
        TextView jobDetailLocation=findViewById(R.id.jobDetailsLocation);

        String abilities=getIntent().getStringExtra("habilities");
        TextView jobDetailAbilities=findViewById(R.id.jobDetailsHabilities);

        boolean checkRamp = getIntent().getBooleanExtra("checkRamp", false);
        TextView jobDetailRamp = findViewById(R.id.jobDetailsRamp);
        String booleanRamp;
        if (checkRamp) {
            booleanRamp="Si";
        } else booleanRamp="No";


        boolean checkElevator = getIntent().getBooleanExtra("checkElevator", false);
        TextView jobDetailElevator = findViewById(R.id.jobDetailsElevator);
        String booleanElevator;
        if (checkElevator) {
            booleanElevator="Si";
        } else booleanElevator = "No";
        // Asignar los valores a las vistas
        jobDetailTitle.setText(title);
        jobDetailCategory.setText(category);
        jobDetailSalary.setText(salary);
        jobDetailTypeJob.setText(typeJob);
        jobDetailDescription.setText(description);
        jobDetailLevelEducation.setText(levelEducation);
        jobDetailExperienceLab.setText(experienceLab);
        jobDetailLocation.setText(location);
        jobDetailAbilities.setText(abilities);
        jobDetailRamp.setText(booleanRamp);
        jobDetailElevator.setText(booleanElevator);

        Button buttonBackToAllJobs=findViewById(R.id.buttonBackToAllJobs);
        buttonBackToAllJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button buttonPostulate=findViewById(R.id.buttonPostulate);
        buttonPostulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userID = user.getUid();

                String userBlindEmail = user.getEmail();

                String jobID = getIntent().getStringExtra("jobID");

                // Realizar consulta para obtener los datos del usuario actual
                FirebaseFirestore.getInstance().collection("UsernameBlind").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String userBlindPhone=documentSnapshot.getString("Numero de Teléfono");
                            String userBlindName=documentSnapshot.getString("Nombre");
                            String userBlindProfesión=documentSnapshot.getString("Profesión");
                            String userBlindAbilities=documentSnapshot.getString("abilities");

                            Map<String, Object> userBlindData = new HashMap<>();
                            userBlindData.put("userBlindName",userBlindName);
                            userBlindData.put("userEmail", userBlindEmail);
                            userBlindData.put("userBlindPhone", userBlindPhone);
                            userBlindData.put("Profesión",userBlindProfesión);
                            userBlindData.put("abilities",userBlindAbilities);

                            // Añadir el ID del usuario a la subcolección "postulantes" del documento del empleo
                            DocumentReference jobRef = db.collection("TrabajosPublicados").document(jobID);
                            jobRef.collection("Postulates").document(userID).set(userBlindData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(JobDetails.this, "Te has postulado al empleo.", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(JobDetails.this, "Error al postularse al empleo.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else {
                            Toast.makeText(JobDetails.this, "No se encontraron datos de usuario.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JobDetails.this, "Error al obtener datos de usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }
        });
        incrementarMensual();
        incrementarTotal();
    }
}