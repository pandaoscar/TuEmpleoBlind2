package com.example.tuempleoblind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class NewJob extends AppCompatActivity {
    Spinner spinner1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job);

        spinner1=findViewById(R.id.a_c_type_compani);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.lista,R.layout.style_spinner);
        adapter.setDropDownViewResource(R.layout.style_spinner);
        spinner1.setAdapter(adapter);


    }
}