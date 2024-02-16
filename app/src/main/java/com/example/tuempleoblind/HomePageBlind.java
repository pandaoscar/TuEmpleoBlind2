package com.example.tuempleoblind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.tuempleoblind.adapter.UserAdapter;
import com.example.tuempleoblind.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomePageBlind extends AppCompatActivity {
    RecyclerView mRecycle;
    UserAdapter mAdapter;
    FirebaseFirestore mFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_blind);

        mFirestore=FirebaseFirestore.getInstance();
        mRecycle=findViewById(R.id.listaP);
        mRecycle.setLayoutManager(new LinearLayoutManager(this));
        Query query=mFirestore.collection("UsernameBlind");
        FirestoreRecyclerOptions<User> firestoreRecyclerOptions=new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        mAdapter=new UserAdapter(firestoreRecyclerOptions);
        mAdapter.notifyDataSetChanged();
        mRecycle.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}