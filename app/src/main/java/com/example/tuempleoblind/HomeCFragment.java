package com.example.tuempleoblind;

import static android.app.Activity.RESULT_OK;

import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.Manifest;
import android.widget.ImageView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.example.tuempleoblind.adapter.TrabajosPublicadosAdapter;
import com.example.tuempleoblind.model.TrabajosPublicados;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeCFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeCFragment extends Fragment implements TrabajosPublicadosAdapter.OnViewPostulatesClickListener, VoiceCommandController.ActivityCallback {
    Button newJob;
    FloatingActionButton microComand;
    RecyclerView cRecycleView;
    TrabajosPublicadosAdapter mAdapter;
    FirebaseFirestore cFirestore;
    private static final int CODIGO_RECONOCIMIENTO_VOZ = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;

    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private VoiceCommandController controller;
    private LottieAnimationView robotAnimation;
    private ImageView background;

    public HomeCFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeCFragment.
     */
    // Rename and change types and number of parameters
    public static HomeCFragment newInstance(String param1, String param2) {
        HomeCFragment fragment = new HomeCFragment();
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

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        cFirestore= FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_home_c, container, false);
        newJob = view.findViewById(R.id.btn_new_job);
        microComand = view.findViewById(R.id.floatingButtonComands);
        cRecycleView= view.findViewById(R.id.recycleViewJobsOfCompany);
        cRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        robotAnimation=view.findViewById(R.id.robot_animation);
        background=view.findViewById(R.id.backBlack);
        String currentUserId = currentUser.getUid();
        Query query = cFirestore.collection("TrabajosPublicados").whereEqualTo("companyPublishId", currentUserId);
        FirestoreRecyclerOptions<TrabajosPublicados> firestoreRecyclerOptions= new FirestoreRecyclerOptions.Builder<TrabajosPublicados>().setQuery(query,TrabajosPublicados.class).build();
        mAdapter= new TrabajosPublicadosAdapter(firestoreRecyclerOptions, this);
        mAdapter.notifyDataSetChanged();
        cRecycleView.setAdapter(mAdapter);
        mAdapter.setOnViewPostulatesClick(this);

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



        newJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewJob.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
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
    public void onStart() {
        super.onStart();
        mAdapter.startListening();

        controller.sendRoleUser("userCompany");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.startListening();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull String[] perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull String[] perms) {

    }

    @Override
    public void onViewPostulatesClick(int position) {
        DocumentSnapshot snapshot= mAdapter.getSnapshots().getSnapshot(position);
        String jobId = snapshot.getId();
        Intent intent= new Intent(getActivity(),ViewPostulates.class);
        intent.putExtra("jobID", jobId);
        startActivity(intent);

    }


    @Override
    public void onVoiceCommandReceived(String command, String predictedCategory) {
        if (AppState.getInstance().isModoEdicionActivo()) {
            //por hacer
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