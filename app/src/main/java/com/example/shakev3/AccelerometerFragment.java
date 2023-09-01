package com.example.shakev3;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


public class AccelerometerFragment extends Fragment implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private float xValue, yValue, zValue;

    private TextView xValueText, yValueText, zValueText;
    private ImageView accelerometerImage;


    private static final int SHAKE_THRESHOLD = 20;
    private long lastShakeTime = 0;

    public AccelerometerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_accelerometer, container, false);

        xValueText = rootView.findViewById(R.id.xValueTextView);
        yValueText= rootView.findViewById(R.id.yValueTextView);
        zValueText = rootView.findViewById(R.id.zValueTextView);

        accelerometerImage = rootView.findViewById(R.id.accelerometerImage);

        Button logButton = rootView.findViewById(R.id.logButton);
        logButton.setOnClickListener(view -> logAccelerometerData());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xValue = event.values[0];
            yValue = event.values[1];
            zValue = event.values[2];

            xValueText.setText("X: " + xValue);
            yValueText.setText("Y: " + yValue);
            zValueText.setText("Z: " + zValue);

            float rotationAngle = -xValue * 10.0f;
            accelerometerImage.setRotation(rotationAngle);

            if (isShakeDetected(event.values)) {
                showToast("Shake Detected!\nX: " + xValue + "\nY: " + yValue + "\nZ: " + zValue);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    private void logAccelerometerData() {
        Log.d("Accelerometer", "X: " + xValue + ", Y: " + yValue + ", Z: " + zValue);
    }
    private boolean isShakeDetected(float[] values) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShakeTime > 1000) {
            float acceleration = Math.abs(values[0] + values[1] + values[2] - xValue - yValue - zValue) / SensorManager.GRAVITY_EARTH;
            if (acceleration > SHAKE_THRESHOLD) {
                lastShakeTime = currentTime;
                return true;
            }
        }
        return false;
    }
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}