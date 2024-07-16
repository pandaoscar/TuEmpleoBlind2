package com.example.tuempleoblind;

import static android.app.Activity.RESULT_OK;

import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
public class ProfileBlindFragment extends Fragment implements VoiceCommandController.ActivityCallback{
    Button btnExit;
    Button btnDataPerfil;
    Button btnabout;
    Button btnSecurity;
    FloatingActionButton microComand;
    private LottieAnimationView robotAnimation;
    private ImageView background;
    private VoiceCommandController controller;
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
        btnSecurity =view.findViewById(R.id.buttonsecurityblind);
        microComand = view.findViewById(R.id.floatingButtonComands);
        robotAnimation=view.findViewById(R.id.robot_animation);
        background=view.findViewById(R.id.backBlack);
        controller = VoiceCommandController.getInstance(getActivity());
        controller.registerActivityCallback(this);

        microComand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppState.getInstance().isActiveAssistant()){
                    AppState.getInstance().setActiveAssistant(false);
                    controller.sendResponseToService("Desactivado");
                }
                else{
                    AppState.getInstance().setActiveAssistant(true);
                    controller.sendResponseToService("Activado");
                }
                updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());
            }
        });
        btnabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutActivity.class); // Reemplaza "NuevoActivity" con el nombre de tu Activity de destino
                startActivity(intent);
            }
        });
        btnSecurity.setOnClickListener(new View.OnClickListener() {
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
        if (AppState.getInstance().isModoEdicionActivo()){
            String response = "Estas seguro que quieres cerrar sesión, dí, si, o no.";
            controller.sendResponseToService(response);
        }
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());
        // Inflate the layout for this fragment
        return view;
    }
    private void updateRobotAnimationVisibility(boolean isActive){
        if (isActive) {
            background.setVisibility(View.VISIBLE);
            robotAnimation.setVisibility(View.VISIBLE);
            robotAnimation.playAnimation(); // Para iniciar la animación si es necesario
        } else {
            background.setVisibility(View.INVISIBLE);
            robotAnimation.setVisibility(View.INVISIBLE);
            robotAnimation.cancelAnimation(); // Para detener la animación si es necesario
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        controller.unregisterActivityCallback(this);
    }
    @Override
    public void onVoiceCommandReceived(String command, String predictedCategory) {
        if (AppState.getInstance().isModoEdicionActivo()) {
            if (NavigationManager.eliminarTildes(command).contains("si") || command.contains("se")) {
                String response = "Cerrando sesión";
                controller.sendResponseToService(response);
                AppState.getInstance().setModoEdicionActivo(false);
                signOut();
            } else if (command.equalsIgnoreCase("no")) {
                String response = "Cancelando";
                controller.sendResponseToService(response);
                AppState.getInstance().setModoEdicionActivo(false);
            } else {
                String response = "Estás seguro que quieres cerrar sesión, dí, si, o no.";
                controller.sendResponseToService(response);
            }
        } else {
            if (predictedCategory.startsWith("navegacion_")) {
                String destino = extractAfterUnderscore(predictedCategory);
                String respuesta = "Cambiando a " + destino;
                controller.sendResponseToService(respuesta);
                NavigationManager.navigateToDestinationBlind(getContext(), destino, getActivity().getSupportFragmentManager(), this);
            } else if (predictedCategory.startsWith("accion_")) {
                String accion = extractAfterUnderscore(predictedCategory);
                AppState.getInstance().setModoEdicionActivo(true);
                NavigationManager.navigateToDestinationBlind(getContext(), accion, getActivity().getSupportFragmentManager(), this);
            } else {
                if(command.equals("4p4g4d0_4ut0m4t1c0")&& predictedCategory.equals("4p4g4d0_10s3gund0s")){
                updateRobotAnimationVisibility(false);
                }else{
                    String respuesta = "No entiendo ese comando. Por favor, intenta de nuevo.";
                    controller.sendResponseToService(respuesta);
                    updateRobotAnimationVisibility(false);}
                }
        }
    }
}