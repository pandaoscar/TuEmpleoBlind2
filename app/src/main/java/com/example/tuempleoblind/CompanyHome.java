package com.example.tuempleoblind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.tuempleoblind.databinding.ActivityCompanyHomeBinding;

public class CompanyHome extends AppCompatActivity {
    private ActivityCompanyHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCompanyHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeCFragment());
        binding.navViewC.setBackground(null);

        String framentDeseado = getIntent().getStringExtra("keyword");
        if (framentDeseado != null){
            NavigationManager.navigateToDestinationC(CompanyHome.this, framentDeseado, getSupportFragmentManager(), new HomeCFragment());
        }

        binding.navViewC.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.homeCFragment) {
                replaceFragment(new HomeCFragment());
            } else {
                if (item.getItemId() == R.id.profileCFragment){
                    replaceFragment(new ProfileCFragment());
                }else{
                    if (item.getItemId() == R.id.configCFragment){
                        replaceFragment(new ConfigCFragment());
                    }
                    else {
                        Log.d("TAG", "No se pudo");
                    }
                }
            }
            return true;
        });
    }
    private void replaceFragment (Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layoutConstraint, fragment);
        fragmentTransaction.commit();
    }

}
