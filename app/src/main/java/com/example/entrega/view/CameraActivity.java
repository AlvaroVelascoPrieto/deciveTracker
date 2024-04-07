package com.example.entrega.view;

// CameraActivity.java
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.entrega.R;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private String currentPhotoPath, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        id = getIntent().getStringExtra("id");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button captureButton = findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Image captured and saved to file, send it to server
            uploadFile();
        }
    }

    public void uploadFile() {
        String fileName = id + ".jpg";

        final HttpURLConnection[] conn = {null};
        final DataOutputStream[] dos = {null};
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        final int[] bytesRead = new int[1];
        final int[] bytesAvailable = new int[1];
        final int[] bufferSize = new int[1];
        final byte[][] buffer = new byte[1][1];
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(currentPhotoPath);

        if (!sourceFile.isFile()) {

            Log.e("uploadFile", "Source File not exist :"+ currentPhotoPath);

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(CameraActivity.this, "PSpurce file does not exist"+currentPhotoPath, Toast.LENGTH_SHORT).show();
                }
            });
            return;

        }
        else
        {
            final int[] serverResponseCode = new int[1];
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);
                        URL url = new URL("http://34.70.109.203/images/");

                        // Open a HTTP  connection to  the URL
                        conn[0] = (HttpURLConnection) url.openConnection();
                        conn[0].setDoInput(true); // Allow Inputs
                        conn[0].setDoOutput(true); // Allow Outputs
                        conn[0].setUseCaches(false); // Don't use a Cached Copy
                        conn[0].setRequestMethod("POST");
                        conn[0].setRequestProperty("Connection", "Keep-Alive");
                        conn[0].setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn[0].setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        conn[0].setRequestProperty("uploaded_file", fileName);

                        dos[0] = new DataOutputStream(conn[0].getOutputStream());

                        dos[0].writeBytes(twoHyphens + boundary + lineEnd);
                        dos[0].writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                                + fileName + "\"" + lineEnd);

                        dos[0].writeBytes(lineEnd);

                        // create a buffer of  maximum size
                        bytesAvailable[0] = fileInputStream.available();

                        bufferSize[0] = Math.min(bytesAvailable[0], maxBufferSize);
                        buffer[0] = new byte[bufferSize[0]];

                        // read file and write it into form...
                        bytesRead[0] = fileInputStream.read(buffer[0], 0, bufferSize[0]);

                        while (bytesRead[0] > 0) {

                            dos[0].write(buffer[0], 0, bufferSize[0]);
                            bytesAvailable[0] = fileInputStream.available();
                            bufferSize[0] = Math.min(bytesAvailable[0], maxBufferSize);
                            bytesRead[0] = fileInputStream.read(buffer[0], 0, bufferSize[0]);

                        }

                        // send multipart form data necesssary after file data...
                        dos[0].writeBytes(lineEnd);
                        dos[0].writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        // Responses from the server (code and message)
                        serverResponseCode[0] = conn[0].getResponseCode();
                        String serverResponseMessage = conn[0].getResponseMessage();

                        Log.i("uploadFile", "HTTP Response is : "
                                + serverResponseMessage + ": " + serverResponseCode[0]);

                        if (serverResponseCode[0] == 200) {

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    String msg = "File Upload Completed.\n\n See uploaded file your server. \n\n";
                                    Toast.makeText(CameraActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        //close the streams //
                        fileInputStream.close();
                        dos[0].flush();
                        dos[0].close();

                    } catch (
                            MalformedURLException ex) {

                        ex.printStackTrace();

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(CameraActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                    } catch (
                            Exception e) {

                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(CameraActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                            }
                        });
                        //Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);
                    }
                }});

            if (thread.isAlive()) {
                // Ending thread after there was a successful login
                thread.interrupt();
            }

            thread.start();

        } // End else block
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
