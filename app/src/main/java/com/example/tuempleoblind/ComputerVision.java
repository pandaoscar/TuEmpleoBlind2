package com.example.tuempleoblind;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class ComputerVision extends AppCompatActivity {

    private SurfaceView surfaceView;
    private TextView textView;
    private CameraSource cameraSource;
    private static final int PERMISSION =100;
    private Handler handler;
    private boolean isProcessing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer_vision);

        surfaceView=findViewById(R.id.camera);
        textView=findViewById(R.id.textC);
        handler = new Handler();
        startCameraSource();

    }

    private void startCameraSource(){
        final TextRecognizer textRecognizer= new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()){
            Log.w("Tag", "Dependecies not loaded yet");

        }else{
            cameraSource= new CameraSource.Builder(getApplicationContext(),textRecognizer).setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedPreviewSize(1280, 1024).setAutoFocusEnabled(true).setRequestedFps(2.0f).build();
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                    try{
                        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(ComputerVision.this, new String[]{Manifest.permission.CAMERA},
                                    PERMISSION);
                            return;

                        }
                        cameraSource.start(surfaceView.getHolder());
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                    //Release sourse for camerasouerce

                }

                @Override
                public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                    cameraSource.stop();

                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                    // Detect all text from camera
                }

                @Override
                public void receiveDetections(@NonNull Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items=detections.getDetectedItems();
                    if(items.size()!=0  && !isProcessing){
                        isProcessing = true;
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder= new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    TextBlock item= items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                final String detectedText = stringBuilder.toString().trim();
                                textView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(detectedText);
                                    }
                                });
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        isProcessing = false;
                                    }
                                }, 5000);
                            }
                        });
                    }

                }
            });
        }
    }
}