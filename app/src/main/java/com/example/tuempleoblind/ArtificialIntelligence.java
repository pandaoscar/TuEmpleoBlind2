package com.example.tuempleoblind;

import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArtificialIntelligence#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtificialIntelligence extends Fragment implements VoiceCommandController.ActivityCallback {
    Button iaBtn;
    FloatingActionButton microComand;

    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LottieAnimationView robotAnimation;
    private ImageView background;

    //Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private VoiceCommandController controller;

    public ArtificialIntelligence() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArtificialIntelligence.
     */
    //Rename and change types and number of parameters
    public static ArtificialIntelligence newInstance(String param1, String param2) {
        ArtificialIntelligence fragment = new ArtificialIntelligence();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artificial_intelligence, container, false);
        iaBtn =view.findViewById(R.id.buttonStartIa);
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
        iaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ComputerVision.class); // Reemplaza "NuevoActivity" con el nombre de tu Activity de destino
                startActivity(intent);
            }
        });
        if (AppState.getInstance().isModoEdicionActivo()){
            String response = "Estas seguro que quieres abrir el OCR, di, si, o no.";
            controller.sendResponseToService(response);
        }
        updateRobotAnimationVisibility(AppState.getInstance().isActiveAssistant());
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        controller.unregisterActivityCallback(this);
    }
    @Override
    public void onVoiceCommandReceived(String command, String predictedCategory) {
        if (AppState.getInstance().isModoEdicionActivo()) {
            if (command.equalsIgnoreCase("sí")) {
                String response = "Abriendo reconocimiento de caracteres";
                controller.sendResponseToService(response);
                AppState.getInstance().setModoEdicionActivo(false);
                iaBtn.performClick();
            } else if (command.equalsIgnoreCase("no")) {
                String response = "Cancelando";
                controller.sendResponseToService(response);
                AppState.getInstance().setModoEdicionActivo(false);
            } else {
                String response = "Estas seguro que quieres abrir el OCR, di, si, o no.";
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

    public void onSaveClicked(View view) {
        Intent intent = new Intent(getActivity(), ComputerVision.class); // Reemplaza "NuevoActivity" con el nombre de tu Activity de destino
        startActivity(intent);
    }
}