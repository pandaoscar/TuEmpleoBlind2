package com.example.tuempleoblind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tuempleoblind.databinding.ActivityCompanyHomeBinding;

import pub.devrel.easypermissions.EasyPermissions;

public class CompanyHome extends AppCompatActivity {
    private ActivityCompanyHomeBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 123;

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
        checkAndRequestPermissions();
    }
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Verificar si hay fragmentos en el BackStack
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            // Asegurarse de que se muestre HomeCFragment después de limpiar el BackStack
            replaceFragment(new HomeCFragment());
        } else {
            super.onBackPressed(); // Comportamiento predeterminado si no hay fragmentos en el BackStack
        }
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layoutConstraint, fragment);
        fragmentTransaction.addToBackStack(null); // Agregar transacción al BackStack
        fragmentTransaction.commit();
    }

    private void checkAndRequestPermissions() {
        if (EasyPermissions.hasPermissions(getApplicationContext(), android.Manifest.permission.RECORD_AUDIO)) {
            // Permission already granted, perform operation
            Toast.makeText(getApplicationContext(), "Escuchando...", Toast.LENGTH_SHORT).show();
            startService(new Intent(this, VoiceService.class));
        } else {
            // Request permissions
            EasyPermissions.requestPermissions(this, "Porfavor acepta los permisos del microfono", PERMISSION_REQUEST_CODE, Manifest.permission.RECORD_AUDIO);
        }
    }

}
