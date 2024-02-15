package com.example.tuempleoblind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;


import android.os.Bundle;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.core.DatabaseInfo;

import java.util.ArrayList;
import java.util.Date;

public class HomePageBlind extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    Adapter adapter;
    ArrayList<OportunutiesJobs> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_blind);

        recyclerView=findViewById(R.id.userlist);
        databaseReference=FirebaseDatabase.getInstance().getReference("UsernameBlind");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list= new ArrayList<>();
        adapter=new Adapter(this,list);
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    OportunutiesJobs oportunutiesJobs= dataSnapshot.getValue(OportunutiesJobs.class);
                    list.add(oportunutiesJobs);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}