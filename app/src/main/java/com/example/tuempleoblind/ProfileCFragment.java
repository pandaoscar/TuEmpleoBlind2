package com.example.tuempleoblind;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileCFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ProfileCFragment extends Fragment {
    Button btn_exit;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileCFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileCFragment newInstance(String param1, String param2) {
        ProfileCFragment fragment = new ProfileCFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileCFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_profile_c, container, false);
        btn_exit = view.findViewById(R.id.signOff);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        // Inflate the layout for this fragment
        return view;
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