package com.example.tuempleoblind;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tuempleoblind.adapter.TrabajosPublicadosAdapter;
import com.example.tuempleoblind.model.TrabajosPublicados;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeCFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeCFragment extends Fragment implements TrabajosPublicadosAdapter.OnViewPostulatesClickListener {
    Button newJob;
    RecyclerView cRecycleView;
    TrabajosPublicadosAdapter mAdapter;
    FirebaseFirestore cFirestore;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
    // TODO: Rename and change types and number of parameters
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
        cRecycleView= view.findViewById(R.id.recycleViewJobsOfCompany);
        cRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();
        Query query = cFirestore.collection("TrabajosPublicados").whereEqualTo("companyPublishId", currentUserId);
        FirestoreRecyclerOptions<TrabajosPublicados> firestoreRecyclerOptions= new FirestoreRecyclerOptions.Builder<TrabajosPublicados>().setQuery(query,TrabajosPublicados.class).build();
        mAdapter= new TrabajosPublicadosAdapter(firestoreRecyclerOptions, this);
        mAdapter.notifyDataSetChanged();
        cRecycleView.setAdapter(mAdapter);
        mAdapter.setOnViewPostulatesClick(this);

        newJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewJob.class);
                startActivity(intent);
            }
        });
        return view;
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
    public void onResume() {
        super.onResume();
        mAdapter.startListening();
    }

    @Override
    public void onViewPostulatesClick(int position) {
        DocumentSnapshot snapshot= mAdapter.getSnapshots().getSnapshot(position);
        String jobId = snapshot.getId();
        Intent intent= new Intent(getActivity(),ViewPostulates.class);
        intent.putExtra("jobID", jobId);
        startActivity(intent);

    }
}