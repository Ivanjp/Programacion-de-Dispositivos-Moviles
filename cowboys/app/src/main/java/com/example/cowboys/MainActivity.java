package com.example.cowboys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView gunView;
    private Button start_btn;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private byte step;
    private ExecutorService singleThreadProducer;
    private DrawTimer asyncCounter;
    public static final byte SECONDS_TO_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gunView = findViewById(R.id.gun_iv);
        start_btn = findViewById(R.id.start_btn)
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null){
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }
        if (stepDetectorSensor == null){
            sensorManager = null;
        }
    }

    @Override
    protected void onPause() {
        if (sensorManager != null){
            sensorManager.unregisterListener(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        init();
        super.onResume();
    }

    //Método para ocupar la pantalla completa
    //Este método se ejecutara mientras la ventana esté en primer plano
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //Aquí se declara que se quiere la pantalla en modo inmersivo cuando la pantalla esta en primer plano
        if(hasFocus){
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    public void init(){
        gunView.setVisibility(View.INVISIBLE);
        start_btn.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if (!checkActivityRecognitionPermission()){
                ActivityCompat.requestPermissions(activity: this,
                    new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, requestCode: 0);
            }
        }
    }

    @TargetApi(29)
    private boolean checkActivityRecognitionPermission(){
        return ContextCompat.checkSelfPermission( context: this, Manifest.permission.ACTIVITY_RECOGNITION) ==
        PackageManager.PERMISSION_GRANTED;
    }
    public void finalCountdown(View startBtn){
        startBtn.setVisibility(View.INVISIBLE);
        checkStepSensor();
    }

    private void checkStepSensor(){
        if (sensorManager == null){
            startTimer();
            return;
        }
        sensorManager.registerListener(listener: this, stepDetectorSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        step++;
        if (step >= 3){
            sensorManager.unregisterListener(this);
            gunView.setVisibility(View.VISIBLE);
            step = 0;
        }
    }

    private void startTimer(){

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}