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
import android.hardware.SensorManager;
import android.os.SystemClock;
import es.udc.robotcontrol.utils.Constantes;
import org.ros.message.Time;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import sensor_msgs.Imu;


public class QuatPublisher extends AbstractSensorsPublisher {
    // TODO: Check names
    private static String QUEUE_NAME = Constantes.TOPIC_ROTATION;

    public QuatPublisher(Context ctx, String robotName) {
        super(ctx, robotName);
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_ROTATION_VECTOR;
    }

    @Override
    protected Publisher createPublisher(ConnectedNode n) {
        String queueName = robotName + "/" + QUEUE_NAME;
        return n.newPublisher(queueName, Imu._TYPE);
    }

    @Override
    protected AbstractSensorEventListener createListener(Publisher p) {
        Sensor sensor = sensorManager.getDefaultSensor(getSensorType());
        QuatSensorListener pl = new QuatSensorListener(p, sensor);
        return pl;
    }

    @Override
    protected String getTopicName() {
        return QUEUE_NAME;
    }

    private class QuatSensorListener extends AbstractSensorEventListener {

        protected QuatSensorListener(Publisher publisher, Sensor s) {
            super(s, publisher);

        }

        //	@Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                // Create a new message
                Imu imu = (Imu) this.publisher.newMessage();

                float[] quaternion = new float[4];
                SensorManager.getQuaternionFromVector(quaternion, event.values);
                imu.getOrientation().setW(quaternion[0]);
                imu.getOrientation().setX(quaternion[1]);
                imu.getOrientation().setY(quaternion[2]);
                imu.getOrientation().setZ(quaternion[3]);
                double[] tmpCov = {0, 0, 0, 0, 0, 0, 0, 0, 0};// TODO Make Parameter
                imu.setOrientationCovariance(tmpCov);

                // Convert event.timestamp (nanoseconds uptime) into system time, use that as the header stamp
                long time_delta_millis = System.currentTimeMillis() - SystemClock.uptimeMillis();
                imu.getHeader().setStamp(Time.fromMillis(time_delta_millis + event.timestamp / 1000000));
                imu.getHeader().setFrameId(robotName);

                publisher.publish(imu);
            }
        }

    }
}
