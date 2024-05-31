package com.example.tuempleoblind;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Utilidad {

    // Constructor privado para evitar la instanciación de la clase
    private Utilidad() {
        throw new IllegalStateException("Clase utilitaria");
    }

    public static void incrementarMensual(FirebaseFirestore firestore, String collectionName, String fieldName) {
        CollectionReference reporteRef = firestore.collection(collectionName);
        Calendar calendar = Calendar.getInstance();
        String monthYear = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault()).format(calendar.getTime());
        reporteRef.document(monthYear).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.contains(fieldName)) {
                            long currentJobs = document.getLong(fieldName);
                            reporteRef.document(monthYear).update(fieldName, currentJobs + 1);
                        } else {
                            reporteRef.document(monthYear).update(fieldName, 1);
                        }
                    } else {
                        Map<String, Object> data = new HashMap<>();
                        data.put(fieldName, 1);
                        reporteRef.document(monthYear).set(data);
                    }
                } else {
                    Log.d("Error obteniendo documentos:", String.valueOf(task.getException()));
                }
            }
        });
    }

    public static void incrementarTotal(FirebaseFirestore firestore, String collectionName, String documentName, String fieldName) {
        CollectionReference reporteRef = firestore.collection(collectionName);
        reporteRef.document(documentName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.contains(fieldName)) {
                            long currentJobs = document.getLong(fieldName);
                            reporteRef.document(documentName).update(fieldName, currentJobs + 1);
                        } else {
                            reporteRef.document(documentName).update(fieldName, 1);
                        }
                    } else {
                        Map<String, Object> data = new HashMap<>();
                        data.put(fieldName, 1);
                        reporteRef.document(documentName).set(data);
                    }
                } else {
                    Log.d("Error obteniendo documentos:", String.valueOf(task.getException()));
                }
            }
        });
    }
}
