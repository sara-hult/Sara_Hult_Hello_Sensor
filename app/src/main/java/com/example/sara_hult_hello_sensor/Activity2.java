package com.example.sara_hult_hello_sensor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.math.MathUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Activity2 extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView xValue, yValue, zValue;
    private Vibrator vibrator;
    public static Activity activity2;
    private CameraManager camManager;
    private boolean hasFlash = false;
    boolean keep;
    private Intent intent;
    private Object mCameraCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        intent = this.getIntent();
        activity2 = this;

        getSupportActionBar().setTitle("BalanceYou");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        xValue = findViewById(R.id.xValue);
        yValue = findViewById(R.id.yValue);
        zValue = findViewById(R.id.zValue);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); //Låter oss komma åt mobilens olika sensorer
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            camManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        }
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                hasFlash = true;
                setTorch(true);
            } else {
                Toast.makeText(Activity2.this, "No flash", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Activity2.this, "No camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        xValue.setText("xValue: " + Math.round((double) event.values[0]));
        yValue.setText("yValue: " + Math.round( (double)event.values[1]));
        zValue.setText("zValue: " + Math.round( (double)event.values[2]));

        if (Math.round((double) event.values[0]) > 5 || Math.round((double) event.values[0]) < -4) {
            tipped(xValue);
        } else if(Math.round((double) event.values[1]) > 5 || Math.round((double) event.values[1]) < -3) {
            tipped(yValue);
        } else if (Math.round((double) event.values[2]) > 13 || Math.round((double) event.values[2]) < -7 ) {
            tipped(zValue);
        } else {
            clear(xValue);
            clear(yValue);
            clear(zValue);
            setTorch(true);
        }
    }

    private void tipped(TextView view) {
        vibrator.vibrate(1000);
        view.setTextColor(Color.RED);
        setTorch(false);
    }

    private void setTorch(boolean b) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                camManager.setTorchMode("0", b);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void clear(TextView view) {
        view.setTextColor(Color.BLACK);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        setTorch(false);
    }
}