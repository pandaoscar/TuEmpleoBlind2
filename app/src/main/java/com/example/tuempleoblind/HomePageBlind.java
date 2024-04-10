package com.example.tuempleoblind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.tuempleoblind.databinding.ActivityHomePageBlindBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePageBlind extends AppCompatActivity {

    private @NonNull ActivityHomePageBlindBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= ActivityHomePageBlindBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView= findViewById(R.id.nav_view_blind);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_blind);
        NavigationUI.setupWithNavController(binding.navViewBlind, navController);
    }
}