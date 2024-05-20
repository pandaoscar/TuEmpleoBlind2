package com.example.tuempleoblind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.tuempleoblind.databinding.ActivityHomePageBlindBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePageBlind extends AppCompatActivity {

    private ActivityHomePageBlindBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= ActivityHomePageBlindBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeBlindFragment());
        binding.navViewBlind.setBackground(null);

        String framentDeseado = getIntent().getStringExtra("keyword");
        if (framentDeseado != null){
            NavigationManager.navigateToDestinationBlind(HomePageBlind.this, framentDeseado, getSupportFragmentManager(), new HomeBlindFragment());
        }

        binding.navViewBlind.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.homeBlindFragment) {
                replaceFragment(new HomeBlindFragment());
            } else {
                if (item.getItemId() == R.id.profileBlindFragment){
                    replaceFragment(new ProfileBlindFragment());
                }else{
                    if (item.getItemId() == R.id.configBlindFragment){
                        replaceFragment(new ConfigBlindFragment());
                    }
                    else {
                        if(item.getItemId()==R.id.artificialIntelligence){
                            replaceFragment(new ArtificialIntelligence());
                        }else{ System.out.println("joa mani, no se pudo");}

                    }
                }
            }
            return true;

        });
    }
    private void replaceFragment (Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layoutConstraintBlind, fragment);
        fragmentTransaction.commit();
    }
}