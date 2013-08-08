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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.SystemClock;
import android.util.Log;
import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constantes;
import org.ros.message.Time;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import sensor_msgs.Temperature;


public class TemperaturePublisher extends AbstractSensorsPublisher {
    private int sensorType;

    private String QUEUE_NAME = Constantes.TOPIC_TEMPERATURE;

    public TemperaturePublisher(Context ctx, String robotName) {
        super(ctx, robotName);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        sensorType = Sensor.TYPE_TEMPERATURE; // Older temperature
        if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Log.i(C.TAG, "Using Sensor.TYPE_AMBIENT_TEMPERATURE for sensor type");
            sensorType = Sensor.TYPE_AMBIENT_TEMPERATURE; // Use newer temperature if possible
        }
        else {
            Log.i(C.TAG, "Using Sensor.TYPE_TEMPERATURE for sensor type");
        }
    }

    /**
     * Returns the type of sensor that we use
     *
     * @return
     */
    @Override
    protected int getSensorType() {
        return sensorType;
    }

    /**
     * Create ONE publisher for the concrete sensor
     *
     * @param n
     * @return
     */
    @Override
    protected Publisher createPublisher(ConnectedNode n) {
        String queueName = robotName + "/" + QUEUE_NAME;
        return n.newPublisher(queueName, Temperature._TYPE);
    }

    /**
     * Create a collection of SensorEventListners to publish data on the input publisher
     *
     * @param p
     * @return
     */
    @Override
    protected AbstractSensorEventListener createListener(Publisher p) {
        Sensor sensor = sensorManager.getDefaultSensor(getSensorType());
        TemperatureListener pl = new TemperatureListener(sensor, p);
        return pl;
    }

    /**
     * Topic name used for the sensor
     *
     * @return
     */
    @Override
    protected String getTopicName() {
        return QUEUE_NAME;
    }

    protected class TemperatureListener extends AbstractSensorEventListener {

        public TemperatureListener(Sensor s, Publisher p) {
            super(s, p);
        }

        /**
         * Called when sensor values have changed.
         * <p>See {@link android.hardware.SensorManager SensorManager}
         * for details on possible sensor types.
         * <p>See also {@link android.hardware.SensorEvent SensorEvent}.
         * <p/>
         * <p><b>NOTE:</b> The application doesn't own the
         * {@link android.hardware.SensorEvent event}
         * object passed as a parameter and therefore cannot hold on o it.
         * The object may be part of an internal pool and may be reused by
         * the framework.
         *
         * @param event the {@link android.hardware.SensorEvent SensorEvent}.
         */
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == sensorType) {
                Temperature msg = (Temperature) this.publisher.newMessage();
                long time_delta_millis = System.currentTimeMillis() - SystemClock.uptimeMillis();
                msg.getHeader().setStamp(Time.fromMillis(time_delta_millis + event.timestamp / 1000000));
                msg.getHeader().setFrameId(robotName);// TODO Make parameter
                msg.setTemperature(event.values[0]);
                msg.setVariance(0.0);
                this.publisher.publish(msg);
            }
        }
    }
}