package com.example.tuempleoblind;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DowloadReportActivity extends AppCompatActivity {

    private TextView mText;
    private Button mButtonEmailUser;
    static DocumentSnapshot documentSnapshot;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private FirebaseFirestore mFirestore;
    private PdfDocument pdfDocument;
    private Document document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dowload_report);

        mText = findViewById(R.id.textViewLarge);
        mButtonEmailUser = findViewById(R.id.buttonDownloadPdf);


        mFirestore = FirebaseFirestore.getInstance();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);
            }
        }


        mButtonEmailUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    createPdf();
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void createPdf () throws FileNotFoundException{
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = null;
        File prueba;
        int con = 0;
        String path = "Reporte.pdf";
        while(file == null){
            prueba = new File(pdfPath, path);
            if(!prueba.exists()){
                file = new File(pdfPath, path);
            }
            con+=1;
            path = "Reporte(" + con + ").pdf";

        }


        PdfWriter writer = new PdfWriter(file);
        pdfDocument = new PdfDocument(writer);
        document = new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.LETTER);
        document.setMargins(0,0,0,0);

        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getDrawable(R.drawable.background3);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] bitmapData = stream.toByteArray();

        ImageData imageData = ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData);
        document.add(new Paragraph("\n\n"));
        image.setFixedPosition(0, 0);
        image.setHeight(pdfDocument.getDefaultPageSize().getHeight());
        image.setWidth(pdfDocument.getDefaultPageSize().getWidth());


        document.add(image);

        getInformation();

    }
    private void writeUser(String aspirantesPost, String empleadosReg, String empleosPub, String invidentesReg){
        document.add(new Paragraph("\n\n\n\n\n."));
        Paragraph reportUser = new Paragraph("Reporte de Actividad").setBold().setFontSize(24).setTextAlignment(TextAlignment.CENTER);
        Paragraph description = new Paragraph("Este informe proporciona un resumen detallado \n" +
                "de la actividad en la plataforma, incluyendo los siguientes datos:").setTextAlignment(TextAlignment.CENTER).setFontSize(10);
        Paragraph user = new Paragraph("Usuario").setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(20);

        float[] widht = {100, 100};
        Table table = new Table(widht);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        table.addCell(new Cell().add(new Paragraph("Fecha")));
        table.addCell(new Cell().add(new Paragraph(LocalDate.now().format(dateFormatter))));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss a");
        table.addCell(new Cell().add(new Paragraph("Hora")));
        table.addCell(new Cell().add(new Paragraph(LocalTime.now().format(timeFormatter))));

        table.addCell(new Cell().add(new Paragraph("Numero de aspirantes postulados totales:")));
        table.addCell(new Cell().add(new Paragraph(aspirantesPost)));

        table.addCell(new Cell().add(new Paragraph("Numero de empleadores registrados totales:")));
        table.addCell(new Cell().add(new Paragraph(empleadosReg)));

        table.addCell(new Cell().add(new Paragraph("Numero de empleos publicados totales:")));
        table.addCell(new Cell().add(new Paragraph(empleosPub)));

        table.addCell(new Cell().add(new Paragraph("Numero de invidentes registrados totales:")));
        table.addCell(new Cell().add(new Paragraph(invidentesReg)));

        BarcodeQRCode qrCode = new BarcodeQRCode(aspirantesPost + "\n" + empleadosReg + "\n" + empleosPub + "\n" + invidentesReg + "\n" + LocalDate.now().format(dateFormatter) +
                "\n" + LocalTime.now().format(timeFormatter));
        PdfFormXObject qrCodeObject = qrCode.createFormXObject(ColorConstants.BLACK, pdfDocument);
        Image qrCodeImage = new Image(qrCodeObject).setWidth(80).setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(reportUser);
        document.add(description);
        document.add(user);
        document.add(table);
        document.add(qrCodeImage);
        document.close();

        Toast.makeText(getApplicationContext(), "Documento creado exitosamente en descargas", Toast.LENGTH_SHORT).show();

    }

    private void getInformation(){
        mFirestore.collection("Reporte").document("Totales").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        Toast.makeText(getApplicationContext(), "Procesando...", Toast.LENGTH_SHORT).show();
                        // Obtener los valores como números y luego convertirlos a cadenas
                        Long numAspirantesPostTot = documentSnapshot.getLong("numeroDeAspirantesPostuladosTotales");
                        Long numEmpleadosRegTot = documentSnapshot.getLong("numeroDeEmpleadoresRegistradosTotales");
                        Long numDeEmpleadosPublTot = documentSnapshot.getLong("numeroDeEmpleoPublicadosTotales");
                        Long numeDeInvidentesRegTot = documentSnapshot.getLong("numeroDeInvidentesRegistradosTotales");

                        // Convertir los valores a cadenas
                        String aspirantesPost = (numAspirantesPostTot != null) ? numAspirantesPostTot.toString() : "0";
                        String empleadosReg = (numEmpleadosRegTot != null) ? numEmpleadosRegTot.toString() : "0";
                        String empleosPub = (numDeEmpleadosPublTot != null) ? numDeEmpleadosPublTot.toString() : "0";
                        String invidentesReg = (numeDeInvidentesRegTot != null) ? numeDeInvidentesRegTot.toString() : "0";
                        Toast.makeText(DowloadReportActivity.this, numAspirantesPostTot + " " + numEmpleadosRegTot, Toast.LENGTH_SHORT).show();
                        writeUser(aspirantesPost, empleadosReg, empleosPub, invidentesReg);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Correo electronico no registrado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}