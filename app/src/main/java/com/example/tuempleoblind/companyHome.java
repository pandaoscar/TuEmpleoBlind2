package com.example.tuempleoblind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.tuempleoblind.databinding.ActivityCompanyHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class companyHome extends AppCompatActivity {
    private ActivityCompanyHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityCompanyHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView= findViewById(R.id.nav_view_c);
        NavController navController= Navigation.findNavController(this,R.id.nav_host_fragment_c);
        NavigationUI.setupWithNavController(binding.navViewC,navController);

    }
}