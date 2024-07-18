package com.example.tuempleoblind;

import static android.app.Activity.RESULT_OK;

import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.util.Log;
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

public class ProfileCFragment extends Fragment implements VoiceCommandController.ActivityCallback, AppState.Observer {
    private static final int CODIGO_RECONOCIMIENTO_VOZ = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;

    Button btnExitC;
    Button btnDataPerfilC;
    Button btnReportC;

    Button btnAboutC;
    Button btnSecurityC;
    FloatingActionButton microComand;
    private LottieAnimationView robotAnimation;
    private ImageView background;
    private VoiceCommandController controller;

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
        btnExitC = view.findViewById(R.id.signOff);
        btnDataPerfilC = view.findViewById(R.id.buttonDataProfile);
        btnSecurityC = view.findViewById(R.id.buttonsecurity);
        btnAboutC = view.findViewById(R.id.buttonabout);
        btnReportC = view.findViewById(R.id.buttonReport);
        microComand = view.findViewById(R.id.floatingButtonComands);
        robotAnimation=view.findViewById(R.id.robot_animation);
        background=view.findViewById(R.id.backBlack);
        controller = VoiceCommandController.getInstance(getActivity());
        controller.registerActivityCallback(this);
        AppState.getInstance().addObserver(this);

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

        btnAboutC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });

        btnSecurityC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PoliticalSecurityActivity.class);
                startActivity(intent);
            }
        });

        btnDataPerfilC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataProfile();
            }
        });

        btnReportC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataReport();
            }
        });

        btnExitC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().stopService(new Intent(getActivity(), NewJobPublishedNotification.class));
                signOut();
            }
        });

        if (AppState.getInstance().isModoEdicionActivo()){
            String response = "Estas seguro que quieres cerrar sesión, dí, si, o no.";
            controller.sendResponseToService(response);
        }
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());

        return view;
    }

    private void updateRobotAnimationVisibility(boolean isActive){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        controller.unregisterActivityCallback(this);
        AppState.getInstance().removeObserver(this);
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
                NavigationManager.navigateToDestinationC(getContext(), destino, getActivity().getSupportFragmentManager(), this);
            } else if (predictedCategory.startsWith("accion_")) {
                String accion = extractAfterUnderscore(predictedCategory);
                AppState.getInstance().setModoEdicionActivo(true);
                NavigationManager.navigateToDestinationC(getContext(), accion, getActivity().getSupportFragmentManager(), this);
            } else {
                String respuesta = "No entiendo ese comando. Por favor, intenta de nuevo.";
                controller.sendResponseToService(respuesta);
            }
        }
    }
    @Override
    public void onActiveAssistantChanged(boolean isActive) {
        if (AppState.getInstance().isModoEdicionActivo()){
            updateRobotAnimationVisibility(true);
        } else updateRobotAnimationVisibility(isActive);
    }
}
