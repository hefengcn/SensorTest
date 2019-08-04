package com.tablet.sensor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MSG_SHAKE = 1;
    private static final int SPEED_THRESHOLD = 1000;
    private static final int UPDATE_INTERVAL_TIME = 50;
    private float lastX;
    private float lastY;
    private float lastZ;
    private long lastUpdateTime;
    private SensorManager sensorManager;
    private Vibrator vibrator;


    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_SHAKE) {
                Toast.makeText(MainActivity.this, "手机摇一摇了", Toast.LENGTH_SHORT).show();
                vibrator.vibrate(500);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if(!vibrator.hasVibrator()){
            Toast.makeText(MainActivity.this, "this device has no vibrator", Toast.LENGTH_SHORT).show();
        }
        //testSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(
                    sensorEventListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            long currentUpdateTime = System.currentTimeMillis();
            long timeInterval = currentUpdateTime - lastUpdateTime;

            if (timeInterval < UPDATE_INTERVAL_TIME)
                return;
            lastUpdateTime = currentUpdateTime;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float deltaX = x - lastX;
            float deltaY = y - lastY;
            float deltaZ = z - lastZ;

            lastX = x;
            lastY = y;
            lastZ = z;

            double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval * 10000;
            if (speed >= SPEED_THRESHOLD) {
                Message message = new Message();
                message.what = MSG_SHAKE;
                handler.sendMessage(message);
            }
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void testSensor() {

        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (int i = 0; i < sensorList.size(); i++) {
            Sensor sensor = sensorList.get(i);
            String name = sensor.getName();
            int type = sensor.getType();
            String vendor = sensor.getVendor();

            Log.e(TAG, "name:" + name + "---type:" + type + "---vendor:" + vendor);
        }

    }
}
