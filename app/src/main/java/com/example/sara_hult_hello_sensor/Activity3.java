package com.example.sara_hult_hello_sensor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.TimerTask;


public class Activity3 extends AppCompatActivity implements SensorEventListener {
    ImageView compassImage;
    TextView textView;
    ImageButton backButton;
    public static Activity activity3;

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

    private MediaPlayer cowPlayer, seaPlayer, birdPlayer;
    static final float ALPHA = 0.25f;
    /*
    private float RTmp[] = new float[9];
    private float Rot[] = new float[9];
    private float I[] = new float[9];
    private float grav[] = new float[3];
    private float mag[] = new float[3];
    private float results[] = new float[3];

    protected float[] gravSensorVals;
    protected float[] magSensorVals;

    private SoundPool soundPool;
    private int cowSound, sheepSound, birdSound;
    private boolean loaded; */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);
        activity3 = this;

        getSupportActionBar().setTitle("CompassYou");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cowPlayer = MediaPlayer.create(this, R.raw.cow);
        seaPlayer = MediaPlayer.create(this, R.raw.seatrimmed);
        birdPlayer = MediaPlayer.create(this, R.raw.birdstrimmed);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        compassImage = (ImageView) findViewById(R.id.compass_3);
        textView = (TextView) findViewById(R.id.text3);
        backButton = (ImageButton) findViewById(R.id.backButton3);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        start();


        /* F??rs??k med SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(3)
                    .setAudioAttributes(audioAttributes)
                    .build();
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId,
                                           int status) {
                    loaded = true;
                }
            });
        } else {
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    loaded = true;
                }
            });
        }
        cowSound = soundPool.load(this, R.raw.cow, 1);
        sheepSound = soundPool.load(this, R.raw.sheep, 1);
        birdSound = soundPool.load(this, R.raw.birds, 1);
        */
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
            startSound(birdPlayer);
        }if(max <= 260 && max > 190) {
            where = "Sw";
        }if(max <= 190 && max > 170) {
            where = "S";
            seaPlayer.start();
        }if(max <= 170 && max > 100) {
            where = "SE";
        }if(max <= 100 && max > 80) {
            where = "E";
            //soundPool.play(birdSound, 1, 1, 0, 0, 1);
        }if(max <= 80 && max > 10) {
            where = "NE";
        }

        textView.setText(max + " " + where);

    }

    private void startSound(MediaPlayer player) {
        if (!player.isPlaying()) {
            player.start();
        } else {
            player.pause();
        }
    }
    // Anv??nds ej nu
    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
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

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}