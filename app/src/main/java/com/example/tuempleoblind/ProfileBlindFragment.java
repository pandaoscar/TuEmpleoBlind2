package com.example.tuempleoblind;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileBlindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileBlindFragment extends Fragment implements EasyPermissions.PermissionCallbacks{
    Button btnExit;
    Button btnDataPerfil;
    Button btnabout, btnsecurity;
    FloatingActionButton microComand;
    private static final int CODIGO_RECONOCIMIENTO_VOZ = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;
    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileBlindFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileBlindFragment.
     */
    // Rename and change types and number of parameters
    public static ProfileBlindFragment newInstance(String param1, String param2) {
        ProfileBlindFragment fragment = new ProfileBlindFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_blind, container, false);
        btnExit = view.findViewById(R.id.signOffBlind);
        btnDataPerfil = view.findViewById(R.id.buttonDataProfileBlind);
        btnabout=view.findViewById(R.id.buttonaboutblind);
        btnsecurity=view.findViewById(R.id.buttonsecurityblind);
        microComand = view.findViewById(R.id.floatingButtonComands);

        microComand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAndRequestPermissions();
            }
        });
        btnabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutActivity.class); // Reemplaza "NuevoActivity" con el nombre de tu Activity de destino
                startActivity(intent);
            }
        });
        btnsecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PoliticalSecurityActivity.class); // Reemplaza "NuevoActivity" con el nombre de tu Activity de destino
                startActivity(intent);
            }
        });
        btnDataPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataProfile();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().stopService(new Intent(getActivity(),NewJobPublishedNotification.class));
                signOut();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void checkAndRequestPermissions() {
        if (EasyPermissions.hasPermissions(requireContext(), android.Manifest.permission.RECORD_AUDIO)) {
            // Permission already granted, perform operation
            Toast.makeText(requireContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
            iniciarReconocimientoVoz();
        } else {
            // Request permissions
            EasyPermissions.requestPermissions(this, "Porfavor acepta los permisos del microfono", PERMISSION_REQUEST_CODE, Manifest.permission.RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission is denied
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // Permission granted, handle accordingly
        Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // Permission denied, handle accordingly
        Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
    }


    private void iniciarReconocimientoVoz() {
        // Crea un Intent para el reconocimiento de voz
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Configura el idioma para el reconocimiento (puedes cambiarlo según tus necesidades)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Configura un mensaje para el usuario
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di algo...");
        // Inicia la actividad de reconocimiento de voz y espera los resultados
        startActivityForResult(intent, CODIGO_RECONOCIMIENTO_VOZ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_RECONOCIMIENTO_VOZ) {
            if (resultCode == RESULT_OK && data != null) {
                // Obtiene la lista de palabras reconocidas
                ArrayList<String> palabrasReconocidas = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (palabrasReconocidas != null && palabrasReconocidas.isEmpty()) {
                    // Guarda la primera palabra reconocida en un String
                    String palabra = palabrasReconocidas.toString().replace("[", "").replace("]", "");
                    NavigationManager.navigateToDestinationBlind(getActivity(), palabra, getActivity().getSupportFragmentManager(), this);
                }
            } else {
                // Mensaje de error si el reconocimiento de voz no fue exitoso
                Toast.makeText(requireActivity(), "Error en el reconocimiento de voz", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dataProfile() {
        try {
            Intent intent = new Intent(getActivity(), EditDataProfileBlind.class); // Reemplaza "NuevoActivity" con el nombre de tu Activity de destino
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // Manejar la excepción aquí, como mostrar un mensaje de error al usuario
        }
    }

    private void signOut() {
        try {
            FirebaseAuth.getInstance().signOut();
            // Cerrar sesión correctamente, luego iniciar el nuevo Activity
            Intent intent = new Intent(getActivity(), MainActivity.class); // Reemplaza "NuevoActivity" con el nombre de tu Activity de destino
            startActivity(intent);
            getActivity().finish(); // Opcionalmente, puedes finalizar el Activity actual
        } catch (Exception e) {
            e.printStackTrace();
            // Manejar la excepción aquí, como mostrar un mensaje de error al usuario
        }
    }
}