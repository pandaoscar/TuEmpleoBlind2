package com.example.tuempleoblind;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
    private FirebaseFirestore mFirestore;
    private PdfDocument pdfDocument;
    private Document document;
    private String fileName;
    private Uri pdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dowload_report);

        mText = findViewById(R.id.textViewLarge);
        mButtonEmailUser = findViewById(R.id.buttonDownloadPdf);

        mFirestore = FirebaseFirestore.getInstance();

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

        // Create notification channel for Android O and above
        createNotificationChannel();
    }

    private void createPdf() throws FileNotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            savePdfToDownloads();
        } else {
            savePdfToDownloadsLegacy();
        }
    }

    private void savePdfToDownloads() throws FileNotFoundException {
        fileName = "Reporte.pdf";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        ContentResolver contentResolver = getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            pdfUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        }

        if (pdfUri != null) {
            OutputStream outputStream = contentResolver.openOutputStream(pdfUri);
            if (outputStream != null) {
                writePdf(outputStream);
            }
        }
    }

    private void savePdfToDownloadsLegacy() throws FileNotFoundException {
        File pdfDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (pdfDir != null && !pdfDir.exists()) {
            pdfDir.mkdirs();
        }
        fileName = "Reporte.pdf";
        File file = new File(pdfDir, fileName);

        int con = 0;
        while (file.exists()) {
            con += 1;
            fileName = "Reporte(" + con + ").pdf";
            file = new File(pdfDir, fileName);
        }

        pdfUri = Uri.fromFile(file);
        OutputStream outputStream = new FileOutputStream(file);
        writePdf(outputStream);
    }

    private void writePdf(OutputStream outputStream) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(outputStream);
        pdfDocument = new PdfDocument(writer);
        document = new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.LETTER);
        document.setMargins(0, 0, 0, 0);

        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getDrawable(R.drawable.background3);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
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

    private void writeUser(String aspirantesPost, String empleadosReg, String empleosPub, String invidentesReg) {
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

        BarcodeQRCode qrCode = new BarcodeQRCode("https://www.ucundinamarca.edu.co/");
        PdfFormXObject qrCodeObject = qrCode.createFormXObject(ColorConstants.BLACK, pdfDocument);
        Image qrCodeImage = new Image(qrCodeObject).setWidth(80).setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(reportUser);
        document.add(description);
        document.add(user);
        document.add(table);
        document.add(qrCodeImage);
        document.close();

        showNotification();
    }

    private void getInformation() {
        mFirestore.collection("Reporte").document("Totales").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
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
                    } else {
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

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "tuempleoblind")
                .setSmallIcon(R.drawable.ic_profile)
                .setContentTitle("PDF creado, toca aquí")
                .setContentText("El PDF se ha guardado en la carpeta de descargas")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(getOpenPdfPendingIntent());

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            //  Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private PendingIntent getOpenPdfPendingIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TuEmpleoBlindChannel";
            String description = "Channel for Tu Empleo Blind notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("tuempleoblind", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
