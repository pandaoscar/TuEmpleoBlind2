package com.example.tuempleoblind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class JobDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
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
    }
}