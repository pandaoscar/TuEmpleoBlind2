package com.example.tuempleoblind;

import static android.app.Activity.RESULT_OK;

import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.j2objc.annotations.Weak;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigBlindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigBlindFragment extends Fragment implements VoiceCommandController.ActivityCallback{

    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final int CODIGO_RECONOCIMIENTO_VOZ = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;
    FloatingActionButton microComand;
    private VoiceCommandController controller;

    public ConfigBlindFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfigBlindFragment.
     */
    // Rename and change types and number of parameters
    public static ConfigBlindFragment newInstance(String param1, String param2) {
        ConfigBlindFragment fragment = new ConfigBlindFragment();
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
        View view = inflater.inflate(R.layout.fragment_config_blind, container, false);

        // Encuentra el WebView en el layout
        WebView webViewInterview = view.findViewById(R.id.entrevistaVideo);
        String videoInterview="<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/eRdqCjdUp-M?si=oCTw1iigMO-SSO-8\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
        webViewInterview.loadData(videoInterview,"text/html","utf-8");
        webViewInterview.getSettings().setJavaScriptEnabled(true);
        webViewInterview.setWebChromeClient(new WebChromeClient());

        WebView webViewFirstTime = view.findViewById(R.id.sinExperienciaVideo);
        String videoFirstTime="<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/0MtYRN5eA5c?si=XRQ94l024wR6eo_G\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
        webViewFirstTime.loadData(videoFirstTime,"text/html","utf-8");
        webViewFirstTime.getSettings().setJavaScriptEnabled(true);
        webViewFirstTime.setWebChromeClient(new WebChromeClient());

        microComand = view.findViewById(R.id.floatingButtonComands);

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

            }
        });

        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        controller.unregisterActivityCallback(this);
    }
    @Override
    public void onVoiceCommandReceived(String command, String predictedCategory) {
        if (predictedCategory.startsWith("navegacion_")) {
            String destino = extractAfterUnderscore(predictedCategory);
            String respuesta = "Cambiando a " + destino;
            controller.sendResponseToService(respuesta);
            NavigationManager.navigateToDestinationBlind(getContext(), destino, getActivity().getSupportFragmentManager(), this);
        } else if (predictedCategory.startsWith("accion_")) {
            String accion = extractAfterUnderscore(predictedCategory);
            // Primero navegar a la actividad correcta si es necesario
            AppState.getInstance().setModoEdicionActivo(true);
            NavigationManager.navigateToDestinationBlind(getContext(), accion, getActivity().getSupportFragmentManager(), this);
        } else {
            String respuesta = "No entiendo ese comando. Por favor, intenta de nuevo.";
            controller.sendResponseToService(respuesta);
        }
    }
}