package com.example.tuempleoblind;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigCFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigCFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConfigCFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfigCFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfigCFragment newInstance(String param1, String param2) {
        ConfigCFragment fragment = new ConfigCFragment();
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
        View view =inflater.inflate(R.layout.fragment_config_c, container, false);
        TextView textViewLaw1=view.findViewById(R.id.law1link);
        textViewLaw1.setMovementMethod(LinkMovementMethod.getInstance());

        TextView textViewLaw2=view.findViewById(R.id.law2link);
        textViewLaw2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView textViewLaw3=view.findViewById(R.id.law3link);
        textViewLaw3.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }
}