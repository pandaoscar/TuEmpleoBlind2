package com.example.tuempleoblind;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.google.j2objc.annotations.Weak;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigBlindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigBlindFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
    // TODO: Rename and change types and number of parameters
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
        return view;
    }
}