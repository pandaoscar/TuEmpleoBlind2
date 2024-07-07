package com.example.tuempleoblind;



import static com.example.tuempleoblind.NavigationManager.extractAfterUnderscore;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tuempleoblind.adapter.JobsAvailableAdapter;
import com.example.tuempleoblind.model.JobsAvailable;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeBlindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeBlindFragment extends Fragment implements JobsAvailableAdapter.OnViewMoreClickListener, VoiceCommandController.ActivityCallback{

    RecyclerView mRecicle;
    JobsAvailableAdapter mAdapter;
    FirebaseFirestore mFirestore;
    FloatingActionButton microComand;
    private VoiceCommandController controller;

    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeBlindFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeBlindFragment.
     */
    // Rename and change types and number of parameters
    public static HomeBlindFragment newInstance(String param1, String param2) {
        HomeBlindFragment fragment = new HomeBlindFragment();
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
        mFirestore=FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.fragment_home_blind, container, false);
        mRecicle=view.findViewById(R.id.AllJobs);
        microComand = view.findViewById(R.id.floatingButtonComands);
        mRecicle.setLayoutManager(new LinearLayoutManager(getActivity()));
        Query query=mFirestore.collection("TrabajosPublicados");
        FirestoreRecyclerOptions<JobsAvailable>  firestoreRecyclerOptions= new FirestoreRecyclerOptions.Builder<JobsAvailable>().setQuery(query,JobsAvailable.class).build();
        mAdapter=new JobsAvailableAdapter(firestoreRecyclerOptions);
        mAdapter.notifyDataSetChanged();
        mRecicle.setAdapter(mAdapter);
        mAdapter.setOnViewMoreClickListener(this);

        controller = VoiceCommandController.getInstance(getActivity());
        controller.registerActivityCallback(this);
        controller.sendRoleUser("userBlind");
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

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull String[] perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull String[] perms) {

    }

    public void onViewMoreClick(int position){
        DocumentSnapshot snapshot= mAdapter.getSnapshots().getSnapshot(position);
        String jobId = snapshot.getId();
        JobsAvailable job=snapshot.toObject(JobsAvailable.class);
        Intent intent= new Intent(getActivity(),JobDetails.class);
        intent.putExtra("jobID", jobId);
        intent.putExtra("title",job.getTitle());
        intent.putExtra("category",job.getCategory());
        intent.putExtra("salary",job.getSalary());
        intent.putExtra("typeJob",job.getTypeJob());
        intent.putExtra("description",job.getDescription());
        intent.putExtra("levelEducation",job.getLevelEducation());
        intent.putExtra("experienceLab",job.getExperienceLab());
        intent.putExtra("location",job.getLocation());
        intent.putExtra("habilities",job.getHabilities());
        intent.putExtra("checkRamp",job.getCheckRamp());
        startActivity(intent);
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
    }

    @Override
    public void onStop() {
        super.onStop();
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
                NavigationManager.navigateToDestinationBlind(getContext(), destino, getActivity().getSupportFragmentManager(), this);
            } else if (predictedCategory.startsWith("accion_")) {
                String accion = extractAfterUnderscore(predictedCategory);
                AppState.getInstance().setModoEdicionActivo(true);
                NavigationManager.navigateToDestinationBlind(getContext(), accion, getActivity().getSupportFragmentManager(), this);
            } else {
                String respuesta = "No entiendo ese comando. Por favor, intenta de nuevo.";
                controller.sendResponseToService(respuesta);
            }
        }
    }
}