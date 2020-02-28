package com.example.qrscanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    SurfaceView camSurface;
    CameraSource camera;
    TextView qrResult;
    BarcodeDetector barcodeDetector;
    MutableLiveData<String> result;
    final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        resultObserver();
        barCodeInit();
        surfaceInit();
        camInit();
        barCodeInit();

    }

    void init() {

        result = new MutableLiveData<>();
        camSurface = findViewById(R.id.sv_camera);
        qrResult = findViewById(R.id.tv_result);
    }

    void barCodeInit() {
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qr = detections.getDetectedItems();
                if (qr.size() != 0) {
                    result.postValue(qr.valueAt(0).displayValue);
                }
            }
        });

    }

    void surfaceInit() {
        camSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.CAMERA)) {


                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                        try {
                            camera.start(holder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                        // MY_PERMISSIONS_REQUEST_CAMeRA is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }

                } else {

                    try {
                        camera.start(holder);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                camera.stop();
            }
        });

    }

    void camInit() {
        camera = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).build();


    }

    void resultObserver() {
        result.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                qrResult.setText(s);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    try {
                        camera.start(camSurface.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}
