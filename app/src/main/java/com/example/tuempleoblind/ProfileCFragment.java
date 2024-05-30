package com.example.tuempleoblind;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class ProfileCFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    private static final int CODIGO_RECONOCIMIENTO_VOZ = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;

    Button btn_exit;
    Button btn_dataPerfil, btnReport;
    Button btn_about, btn_security;
    FloatingActionButton microComand;

    public ProfileCFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_c, container, false);
        btn_exit = view.findViewById(R.id.signOff);
        btn_dataPerfil = view.findViewById(R.id.buttonDataProfile);
        btn_security = view.findViewById(R.id.buttonsecurity);
        btn_about = view.findViewById(R.id.buttonabout);
        btnReport = view.findViewById(R.id.buttonReport);
        microComand = view.findViewById(R.id.floatingButtonComands);

        microComand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
            }
        });

        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });

        btn_security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PoliticalSecurityActivity.class);
                startActivity(intent);
            }
        });

        btn_dataPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataProfile();
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataReport();
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().stopService(new Intent(getActivity(), NewJobPublishedNotification.class));
                signOut();
            }
        });

        return view;
    }

    private void checkAndRequestPermissions() {
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.RECORD_AUDIO)) {
            iniciarReconocimientoVoz();
        } else {
            EasyPermissions.requestPermissions(this, "Por favor acepta los permisos del micrófono", PERMISSION_REQUEST_CODE, Manifest.permission.RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
        iniciarReconocimientoVoz();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
    }

    private void iniciarReconocimientoVoz() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di algo...");
        startActivityForResult(intent, CODIGO_RECONOCIMIENTO_VOZ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_RECONOCIMIENTO_VOZ) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> palabrasReconocidas = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (palabrasReconocidas != null && !palabrasReconocidas.isEmpty()) {
                    String palabra = palabrasReconocidas.get(0);
                    NavigationManager.navigateToDestinationC(getActivity(), palabra, getActivity().getSupportFragmentManager(), this);
                }
            } else {
                Toast.makeText(requireActivity(), "Error en el reconocimiento de voz", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dataProfile() {
        try {
            Intent intent = new Intent(getActivity(), EditDataProfileC.class);
            startActivity(intent);
            getActivity().finish();
        } catch (Exception e) {
            Log.e("DataProfile", "Error al iniciar EditDataProfileC", e);
        }
    }

    private void dataReport() {
        try {
            Intent intent = new Intent(getActivity(), DowloadReportActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("DataReport", "Error al iniciar DownloadReportActivity", e);
        }
    }

    private void signOut() {
        try {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        } catch (Exception e) {
            Log.e("SignOut", "Error al cerrar sesión", e);
        }
    }
}
