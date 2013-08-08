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
import es.udc.robotcontrol.utils.Constantes;
import org.ros.message.Time;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import sensor_msgs.Imu;


public class GyroPublisher extends AbstractSensorsPublisher {
    // TODO: Check names
    private static String QUEUE_NAME = Constantes.TOPIC_GYRO;

    public GyroPublisher(Context ctx, String robotName) {
        super(ctx, robotName);
    }


    @Override
    protected int getSensorType() {
        return Sensor.TYPE_GYROSCOPE;
    }

    @Override
    protected Publisher createPublisher(ConnectedNode n) {
        String queueName = robotName + "/" + QUEUE_NAME;
        return n.newPublisher(queueName, Imu._TYPE);
    }

    @Override
    protected AbstractSensorEventListener createListener(Publisher p) {
        Sensor sensor = sensorManager.getDefaultSensor(getSensorType());
        GyroSensorListener pl = new GyroSensorListener(p, sensor);
        return pl;
    }

    @Override
    protected String getTopicName() {
        return QUEUE_NAME;
    }

    protected class GyroSensorListener extends AbstractSensorEventListener {

        protected GyroSensorListener(Publisher publisher, Sensor s) {
            super(s, publisher);
        }

        //	@Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                // Create a new message
                Imu imu = (Imu) this.publisher.newMessage();
                imu.getAngularVelocity().setX(event.values[0]);
                imu.getAngularVelocity().setY(event.values[1]);
                imu.getAngularVelocity().setZ(event.values[2]);
                double[] tmpCov = {0, 0, 0, 0, 0, 0, 0, 0, 0};// TODO Make Parameter
                imu.setAngularVelocityCovariance(tmpCov);

                // Convert event.timestamp (nanoseconds uptime) into system time, use that as the header stamp
                long time_delta_millis = System.currentTimeMillis() - SystemClock.uptimeMillis();
                imu.getHeader().setStamp(Time.fromMillis(time_delta_millis + event.timestamp / 1000000));
                imu.getHeader().setFrameId(robotName);

                publisher.publish(imu);
            }
        }
    }
}
