package es.udc.fic.android.robot_control.webserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import es.udc.fic.android.robot_control.camera.CompressedImagePublisher;
import es.udc.fic.android.robot_control.commands.EngineManager;
import es.udc.fic.android.robot_control.robot.RobotSensorPublisher;
import es.udc.fic.android.robot_control.robot.RobotState;
import es.udc.fic.android.robot_control.sensors.OdometryPublisher;


/**
 * Keeps an updated state of all the robot and makes it available to the RequestHandler
 * to access it from the HTTP server.
 */
public class RobotStateWrapper extends BroadcastReceiver implements SensorEventListener {

    // Update rate low because it's only for visualization, and most of the time will go unused
    private static final int ACCELEROMETER_RATE = SensorManager.SENSOR_DELAY_NORMAL;
    private static final int AMBIENT_TEMPERATURE_RATE = SensorManager.SENSOR_DELAY_NORMAL;
    private static final int GRAVITY_RATE = SensorManager.SENSOR_DELAY_NORMAL;
    private static final int GYROSCOPE_RATE = SensorManager.SENSOR_DELAY_NORMAL;
    private static final int LIGHT_RATE = SensorManager.SENSOR_DELAY_NORMAL;
    private static final int MAGNETIC_FIELD_RATE = SensorManager.SENSOR_DELAY_NORMAL;
    private static final int PRESSURE_RATE = SensorManager.SENSOR_DELAY_NORMAL;
    private static final int PROXIMITY_RATE = SensorManager.SENSOR_DELAY_NORMAL;

    private static final String TAG = "UDC_ROBOT_RobotStateWra";


    private final Context ctx;
    private final IntentFilter boardIntentFilter;
    private final IntentFilter imageIntentFilter;
    private final IntentFilter odometryIntentFilter;

    private double wheelLeft = 0.0f;
    private double wheelRight = 0.0f;
    private int[] irSensors = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
    private byte[] lastCompressedImage = new byte[]{};

    private final Sensor accelerometer;
    private float acceleration_x = 0.0f,
            acceleration_y = 0.0f,
            acceleration_z = 0.0f;

    private double position_x = 0.0f,
            position_y = 0.0f;

    private final Sensor ambTemperature;
    private float temperature = 0.0f;

    private final Sensor gravity;
    private float gravity_x = 0.0f,
            gravity_y = 0.0f,
            gravity_z = 0.0f;

    private final Sensor gyroscope;
    private float gyroscope_x = 0.0f,
            gyroscope_y = 0.0f,
            gyroscope_z = 0.0f;

    private final Sensor lightSensor;
    private float lightLevel = 0.0f;

    private final Sensor magneticField;
    private float magneticField_x = 0.0f,
            magneticField_y = 0.0f,
            magneticField_z = 0.0f;

    private final Sensor pressureSensor;
    private float pressure = 0.0f;

    private final Sensor proximitySensor;
    private float proximity = 0.0f;


    public RobotStateWrapper(Context ctx){
        this.ctx = ctx;
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        // Board sensors
        boardIntentFilter = new IntentFilter(RobotState.UPDATE_BOARD_STATE);
        ctx.registerReceiver(this, boardIntentFilter);

        imageIntentFilter = new IntentFilter(CompressedImagePublisher.COMPRESSED_CAMERA_IMAGE_ACTION);
        ctx.registerReceiver(this, imageIntentFilter);

        // Odometry
        odometryIntentFilter = new IntentFilter(OdometryPublisher.UPDATE_ODOMETRY);
        ctx.registerReceiver(this, odometryIntentFilter);

        // Android sensors
        SensorManager sensorManager = (SensorManager)ctx.getSystemService(Context.SENSOR_SERVICE);

        // Accelerometer
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, ACCELEROMETER_RATE);

        // Ambient temperature
        if (currentApiVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            ambTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }
        else {
            // Only use this when the not deprecated type is not available
            // noinspection deprecation
            ambTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        }
        sensorManager.registerListener(this, ambTemperature, AMBIENT_TEMPERATURE_RATE);

        // Gravity sensor
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(this, gravity, GRAVITY_RATE);

        // Gyroscope
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, gyroscope, GYROSCOPE_RATE);

        // Light sensor
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, lightSensor, LIGHT_RATE);

        // Magnetic field
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magneticField, MAGNETIC_FIELD_RATE);

        // Atmosferic pressure
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorManager.registerListener(this, pressureSensor, PRESSURE_RATE);

        // Proximity
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(this, proximitySensor, PROXIMITY_RATE);
    }



    public float[] getAcceleration(){
        return new float[]{acceleration_x, acceleration_y, acceleration_z};
    }

    public double getBatteryLevel(){
        IntentFilter batFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = ctx.registerReceiver(null, batFilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return  level / (double)scale;
    }

    public float[] getGyroscope(){
        return new float[]{gyroscope_x, gyroscope_y, gyroscope_z};
    }

    public float[] getGravity(){
        return new float[]{gravity_x, gravity_y, gravity_z};
    }

    public float getLight(){
        return lightLevel;
    }

    public double[] getOdometry(){
        return new double[]{position_x, position_y};
    }

    public float[] getMagneticField(){
        return new float[]{magneticField_x, magneticField_y, magneticField_z};
    }

    public float getPressure(){
        return pressure;
    }

    public float getProximity(){
        return proximity;
    }

    public float getTemperature(){
        return temperature;
    }

    public int[] getIrSensors(){
        return irSensors;
    }

    public double[] getWheels(){
        return new double[]{wheelLeft, wheelRight};
    }

    public byte[] getLastCompressedImage(){
        return lastCompressedImage;
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;

        if (sensor.equals(accelerometer)){
            acceleration_x = sensorEvent.values[0];
            acceleration_y = sensorEvent.values[1];
            acceleration_z = sensorEvent.values[2];
        }
        else if (sensor.equals(ambTemperature)){
            temperature = sensorEvent.values[0];
        }
        else if (sensor.equals(gravity)){
            gravity_x = sensorEvent.values[0];
            gravity_y = sensorEvent.values[1];
            gravity_z = sensorEvent.values[2];
        }
        else if (sensor.equals(gyroscope)){
            gyroscope_x = sensorEvent.values[0];
            gyroscope_y = sensorEvent.values[1];
            gyroscope_z = sensorEvent.values[2];
        }
        else if (sensor.equals(lightSensor)){
            lightLevel = sensorEvent.values[0];
        }
        else if (sensor.equals(magneticField)){
            magneticField_x = sensorEvent.values[0];
            magneticField_y = sensorEvent.values[1];
            magneticField_z = sensorEvent.values[2];
        }
        else if (sensor.equals(pressureSensor)){
            pressure = sensorEvent.values[0];
        }
        else if (sensor.equals(proximitySensor)){
            proximity = sensorEvent.values[0];
        }
        else {
            Log.w(TAG, "Unknown sensor change received");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        if (boardIntentFilter.hasAction(intent.getAction())) {
            if (data.containsKey(EngineManager.LEFT_WHEEL_UPDATE_KEY)) {
                wheelLeft = data.getDouble(EngineManager.LEFT_WHEEL_UPDATE_KEY);
            }
            if (data.containsKey(EngineManager.RIGHT_WHEEL_UPDATE_KEY)) {
                wheelRight = data.getDouble(EngineManager.RIGHT_WHEEL_UPDATE_KEY);
            }
            if (data.containsKey(RobotSensorPublisher.IR_SENSORS_UPDATE_KEY)) {
                irSensors = data.getIntArray(EngineManager.RIGHT_WHEEL_UPDATE_KEY);
            }
        }
        if (imageIntentFilter.hasAction(intent.getAction())){
            if (data.containsKey(CompressedImagePublisher.COMPRESSED_CAMERA_IMAGE_KEY)){
                lastCompressedImage = data.getByteArray(CompressedImagePublisher.COMPRESSED_CAMERA_IMAGE_KEY);
            }
        }
        if (odometryIntentFilter.hasAction(intent.getAction())){
            if (data.containsKey(OdometryPublisher.POSITION_X)){
                position_x = data.getDouble(OdometryPublisher.POSITION_X);
            }
            if (data.containsKey(OdometryPublisher.POSITION_Y)){
                position_y = data.getDouble(OdometryPublisher.POSITION_Y);
            }
        }
    }
}
