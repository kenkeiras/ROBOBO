/*
 * Copyright (C) 2013 Amancio Díaz Suárez
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package es.udc.fic.android.robot_control.sensors;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;
import es.udc.fic.android.robot_control.utils.C;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;
import udc_robot_control_msgs.AndroidSensor3;
import udc_robot_control_msgs.AndroidSensor4;

public abstract class AbstractSensorEventListener implements SensorEventListener {

    private Sensor relatedSensor;
    protected Publisher publisher;

    protected int currentAccuracy;

    public AbstractSensorEventListener(Sensor s, Publisher p) {
        relatedSensor = s;
        publisher = p;
    }

    public Sensor getRelatedSensor() {
        return relatedSensor;
    }

    public void setRelatedSensor(Sensor relatedSensor) {
        this.relatedSensor = relatedSensor;
    }

    public void registerSelf(SensorManager sm, int sensorDelay) {
        sm.registerListener(this, this.getRelatedSensor(), sensorDelay);
    }

    public void unregisterSelf(SensorManager sm) {
        sm.unregisterListener(this);
    }

    protected int getCurrentAccuracy() {
        return currentAccuracy;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.w(C.TAG, "Accuracy change in sensor [ " + sensor.getName() + " ] => [ " + accuracy + " ]");
        currentAccuracy = accuracy;
    }


    /**
     * Method to fill and send an AndroidSensor3 msg
     * @param event
     * @param robotName
     * @param sensorType
     */
    protected void sensorChangedSensor3(SensorEvent event, String robotName, int sensorType) {
        if (event.sensor.getType() == sensorType) {
            AndroidSensor3 imu = (AndroidSensor3) publisher.newMessage();

            long time_delta_millis = System.currentTimeMillis() - SystemClock.uptimeMillis();
            imu.getHeader().setStamp(Time.fromMillis(time_delta_millis + event.timestamp / 1000000));
            imu.getHeader().setFrameId(robotName);

            imu.getData().setX(event.values[0]);
            imu.getData().setY(event.values[1]);
            imu.getData().setZ(event.values[2]);

            double[] tmpCov = {};
            if (event.values.length > 3) {
                int size = event.values.length - 3;
                tmpCov = new double[size];
                for (int x = 3; x < event.values.length; x++) {
                    tmpCov[x - 3] = event.values[x];
                }
            }
            imu.setExtra(tmpCov);
            publisher.publish(imu);
        }
    }


    /**
     * Method to fill and send an AndroidSensor4 msg
     * @param event
     * @param robotName
     * @param sensorType
     */
    protected void sensorChangedSensor4(SensorEvent event, String robotName, int sensorType) {
        if (event.sensor.getType() == sensorType) {
            try {
                // Create a new message
                AndroidSensor4 imu = (AndroidSensor4) this.publisher.newMessage();

                // Convert event.timestamp (nanoseconds uptime) into system time, use that as the header stamp
                long time_delta_millis = System.currentTimeMillis() - SystemClock.uptimeMillis();
                imu.getHeader().setStamp(Time.fromMillis(time_delta_millis + event.timestamp / 1000000));
                imu.getHeader().setFrameId(robotName);

                imu.getData().setW(event.values[0]);
                imu.getData().setX(event.values[1]);
                imu.getData().setY(event.values[2]);
                imu.getData().setZ(event.values[3]);
                double[] tmpCov = {};
                if (event.values.length > 4) {
                    int size = event.values.length - 4;
                    tmpCov = new double[size];
                    for (int x = 4; x < event.values.length; x++) {
                        tmpCov[x - 4] = event.values[x];
                    }
                }
                else {
                    tmpCov = new double[0];
                }
                imu.setExtra(tmpCov);

                publisher.publish(imu);
            }
            catch (java.lang.ArrayIndexOutOfBoundsException obe) {
                // A veces salta esta excepción al conectar o desconectar un array de cuatro elementos.
                // Por alguna razón sólo vienen tres.
                Log.w(C.TAG, "Error reading sensorType [ " + sensorType + " ] [ " + event.sensor.getName() + " ]", obe);
                obe.printStackTrace();
                // TODO: Notificar la excepción de alguna forma.
                // Es posible que se deba a un bug en otra parte (mensaje mal clasificado)
            }
        }
    }
}
