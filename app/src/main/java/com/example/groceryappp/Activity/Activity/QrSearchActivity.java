package com.example.groceryappp.Activity.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.groceryappp.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONObject;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public class QrSearchActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private static final int SELECT_PICTURE = 20;
    private CameraSource cameraSource;
    private CountDownTimer scanTimer;
    private boolean isScanning = false;
    private boolean isCameraCreated = true;
    private boolean isFlashOn = false;
    private ImageButton flashOn, flashOff;
    private AppCompatButton uploadQrImage;
    private String cameraId;
    private CameraManager cameraManager;
    BarcodeDetector barcodeDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_search);
        surfaceView = findViewById(R.id.surfaceView);
        uploadQrImage = findViewById(R.id.uploadQrImage);
        getSupportActionBar().hide();
        startCameraSource();
        // Get the CameraManager instance
      /*  cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            // Get the ID of the back camera
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }*/

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // stopScanning();
                onBackPressed();

            }
        });
        uploadQrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QrImageScan();
            }
        });
/*
                flashOff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (isFlashOn) {
                        // Turn off the flashlight
                        cameraManager.setTorchMode(cameraId, false);
                        flashOff.setBackground(getDrawable(R.drawable.flashoff));

                        isFlashOn = false;
                    } else {
                        // Turn on the flashlight
                        cameraManager.setTorchMode(cameraId, true);
                        flashOff.setBackground(getDrawable(R.drawable.flashon));

                        isFlashOn = true;
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
*/
    }
    private void startCameraSource() {

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .build();
        scanTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Countdown is in progress
            }

            @Override
            public void onFinish() {
                // Timer has finished, stop scanning
                Toast.makeText(QrSearchActivity.this, "Scanning Timeout", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        };


        if (!barcodeDetector.isOperational()) {
            Toast.makeText(this, "Could not set up the barcode detector", Toast.LENGTH_SHORT).show();
            return;
        }
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Do nothing
            }

            @Override
            public void receiveDetections(com.google.android.gms.vision.Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if (qrCodes.size() != 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String qrCode = qrCodes.valueAt(0).displayValue;
                            cameraSource.stop();
                            String rawQRCodeValue = "{ \"name\": \"Product Name\", \"price\": 2.99, \"barcode\": \"123456789\" }";
                            try {

                                Toast.makeText(QrSearchActivity.this, ""+qrCode, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(QrSearchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                //Toast.makeText(QrSearchActivity.this, "Error processing QR code: ", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(QrSearchActivity.this, qrCode, Toast.LENGTH_SHORT).show();
                           /* Intent intent=new Intent();
                            intent.putExtra("QR_CODE",qrCode);
                            setResult(RESULT_OK,intent);
                            finish();*/
                            // Log.d("qr", "run: "+qrCode);
                            // Toast.makeText(ViewAllActivity.this, "QR Code: " + qrCode, Toast.LENGTH_SHORT).show();
                            // Do something with the QR code data
                        }
                    });
                }
            }
        });
        ;

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(QrSearchActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    } else {
                        cameraSource.start(surfaceView.getHolder());
                        isScanning = true;
                        scanTimer.start();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // Do nothing
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Dexter.withContext(QrSearchActivity.this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                // startCameraSource();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                Toast.makeText(QrSearchActivity.this, "Please allow camera permission", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();


    }

    private void stopScanning() {
        if (isScanning) {
            isScanning = false;
            if (cameraSource != null) {
                cameraSource.stop();
                cameraSource.release();
            }
            if (scanTimer != null) {
                scanTimer.cancel();
            }
            onBackPressed();
        }else {
            return;
        }

    }



    private void QrImageScan() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getContents() != null) {
            String qrcodeData = intentResult.getContents();
            Log.e("QR", qrcodeData.toString());
            parse(qrcodeData);
        } else {
            DialogUtils.showErrorDialog(QrStep2.this, "Timeout,Please try again");
            return;*/
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            Uri uri = data.getData();
            readQRCodeFromImage(uri);
        }
    }
    private void readQRCodeFromImage(Uri imageUri) {
        // Open an image picker or use any other method to obtain an image bitmap
        InputStream inputStream;
        try {
            inputStream = getContentResolver().openInputStream(imageUri); // Replace imageUri with your image URI
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Create a BarcodeDetector instance
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                    .setBarcodeFormats(Barcode.ALL_FORMATS)
                    .build();

            // Create a Frame using the image bitmap
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            // Detect and process the QR codes in the image
            SparseArray<Barcode> qrCodes = barcodeDetector.detect(frame);

            if (qrCodes.size() > 0) {
                // QR code(s) detected
                Barcode qrCode = qrCodes.valueAt(0);
                String qrCodeValue = qrCode.displayValue;
                Toast.makeText(this, "QR Code: " + qrCodeValue, Toast.LENGTH_SHORT).show();
                // Process the QR code value as needed
            } else {
                // No QR codes detected

                 Toast.makeText(this, "No QR Code found in the image", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        stopScanning();
        super.onBackPressed();
    }
}