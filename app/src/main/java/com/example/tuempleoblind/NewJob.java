package com.example.tuempleoblind;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.tuempleoblind.databinding.ActivityCompanyHomeBinding;

public class NewJob extends AppCompatActivity {
    Button btn_back;
    Spinner spinner1,spinner2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job);

        btn_back=findViewById(R.id.tbn_back_home_c);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spinner1=findViewById(R.id.a_c_category);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.lista_categoria_empleo,R.layout.style_spinner);
        adapter.setDropDownViewResource(R.layout.style_spinner);
        spinner1.setAdapter(adapter);

        spinner2=findViewById(R.id.a_c_type_compani);
        ArrayAdapter<CharSequence> adapter2=ArrayAdapter.createFromResource(this,R.array.lista_typo_empleo,R.layout.style_spinner);
        adapter2.setDropDownViewResource(R.layout.style_spinner);
        spinner2.setAdapter(adapter2);

    }
}