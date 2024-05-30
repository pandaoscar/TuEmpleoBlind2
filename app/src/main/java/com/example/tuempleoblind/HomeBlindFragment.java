package com.example.tuempleoblind;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
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
import android.widget.Toast;

import com.example.tuempleoblind.adapter.JobsAvailableAdapter;
import com.example.tuempleoblind.model.JobsAvailable;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeBlindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeBlindFragment extends Fragment implements JobsAvailableAdapter.OnViewMoreClickListener{

    RecyclerView mRecicle;
    JobsAvailableAdapter mAdapter;
    FirebaseFirestore mFirestore;
    private static final int CODIGO_RECONOCIMIENTO_VOZ = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;
    FloatingActionButton microComand;

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
        microComand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAndRequestPermissions();
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
    public void onPermissionsGranted(int requestCode, @NonNull String[] perms) {
        // Permission granted, handle accordingly
        Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull String[] perms) {
        // Permission denied, handle accordingly
        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
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
                if (palabrasReconocidas != null && palabrasReconocidas.size() > 0) {
                    // Guarda la primera palabra reconocida en un String
                    String palabra = palabrasReconocidas.toString().replace("[", "").replace("]", "");

                    NavigationManager.navigateToDestinationBlind(getContext(), palabra, getActivity().getSupportFragmentManager(), this);
                }
            } else {
                // Mensaje de error si el reconocimiento de voz no fue exitoso
                Toast.makeText(requireActivity(), "Error en el reconocimiento de voz", Toast.LENGTH_SHORT).show();
            }
        }
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
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}