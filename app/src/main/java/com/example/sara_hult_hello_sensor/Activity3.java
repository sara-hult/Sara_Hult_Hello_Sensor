package com.example.sara_hult_hello_sensor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


public class Activity3 extends AppCompatActivity implements SensorEventListener {
    ImageView compassImage;
    TextView textView;
    int max;
    private SensorManager sensorManager;
    private Sensor rotationSensor, accelerometer, magnetometer;
    float[] rMat = new float[9];
    float[] orientation = new float[9];
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean haveSensor = false, haveSensor2 = false;
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private MediaPlayer cowSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        cowSound = MediaPlayer.create(this, R.raw.cow);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        compassImage = (ImageView) findViewById(R.id.compass_3);
        textView = (TextView) findViewById(R.id.text3);
        start();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, sensorEvent.values);
            max = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0])+360)%360;
        }
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(sensorEvent.values, 0, lastAccelerometer, 0, sensorEvent.values.length);
            lastAccelerometerSet = true;
        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(sensorEvent.values, 0, lastMagnetometer, 0, sensorEvent.values.length);
            lastMagnetometerSet = true;
        }

        if (lastMagnetometerSet && lastAccelerometerSet) {
            SensorManager.getRotationMatrix(rMat, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            max = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0])+360)%360;
        }

        max = Math.round(max);
        compassImage.setRotation(-max);

        String where = "NO";

        if(max >= 350 || max <= 10) {
            where = "N";
        }if(max < 350 && max > 280) {
            where = "Nw";
        }if(max <= 280 && max > 260) {
            where = "W";
            cowSound.start();
        }if(max <= 260 && max > 190) {
            where = "Sw";
        }if(max <= 190 && max > 170) {
            where = "S";
        }if(max <= 170 && max > 100) {
            where = "SE";
        }if(max <= 100 && max > 80) {
            where = "E";
        }if(max <= 80 && max > 10) {
            where = "NE";
        }

        textView.setText(max + " " + where);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void start() {
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null || sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) {
                noSensorAlert();
            } else {
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                haveSensor = sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);


            }
        } else {
            rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void noSensorAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your phone does not support this compass")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void stop() {
        if(haveSensor && haveSensor2) {
            sensorManager.unregisterListener(this, accelerometer);
            sensorManager.unregisterListener(this, magnetometer);
        } else {
            if (haveSensor) {
                sensorManager.unregisterListener(this, rotationSensor);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }
}