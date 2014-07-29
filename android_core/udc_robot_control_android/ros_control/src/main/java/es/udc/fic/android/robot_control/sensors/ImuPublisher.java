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
import android.util.Log;
import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constants;
import org.ros.message.Time;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import sensor_msgs.Imu;

import java.util.List;


public class ImuPublisher extends AbstractSensorsPublisher {
    // TODO: Check names
    private static String QUEUE_NAME = Constants.TOPIC_IMU;

    public ImuPublisher(Context ctx, String robotName) {
        super(ctx, robotName);
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_ACCELEROMETER;
    }

    @Override
    protected Publisher createPublisher(ConnectedNode n) {
        String queueName = robotName + "/" + QUEUE_NAME;
        return n.newPublisher(queueName, Imu._TYPE);
    }

    @Override
    protected AbstractSensorEventListener createListener(Publisher p) {
        Sensor sensor = sensorManager.getDefaultSensor(getSensorType());

        List<Sensor> accelList = this.sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        boolean hasAccel = (accelList.size() > 0);
        List<Sensor> gyroList = this.sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);
        boolean hasGyro = (gyroList.size() > 0);
        List<Sensor> quatList = this.sensorManager.getSensorList(Sensor.TYPE_ROTATION_VECTOR);
        boolean hasQuat = (quatList.size() > 0);

        Log.i(C.TAG, "Acelerometer [ " + hasAccel + " ] Gyroscope [ " + hasGyro + " ] Quat [ " + hasQuat + " ]");
        ImuSensorListener pl = new ImuSensorListener(p, sensor, hasAccel, hasGyro, hasQuat);
        return pl;
    }

    @Override
    protected String getTopicName() {
        return QUEUE_NAME;
    }


    /**
     * This class has been taken from android_sensors_driver as_is.
     * Maybe we will have to split it in three different classes based on the target.
     *
     */
    private class ImuSensorListener extends AbstractSensorEventListener {

        private Sensor s2;
        private Sensor s3;

        private boolean hasAccel;
        private boolean hasGyro;
        private boolean hasQuat;

        private long accelTime;
        private long gyroTime;
        private long quatTime;

        private Imu imu;

        protected ImuSensorListener(Publisher<Imu> publisher, Sensor s, boolean hasAccel, boolean hasGyro, boolean hasQuat) {
            super(s, publisher);
            this.hasAccel = hasAccel;
            this.hasGyro = hasGyro;
            this.hasQuat = hasQuat;

            this.accelTime = 0;
            this.gyroTime = 0;
            this.quatTime = 0;
            this.imu = (Imu) super.publisher.newMessage();
        }

        //	@Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                this.imu.getLinearAcceleration().setX(event.values[0]);
                this.imu.getLinearAcceleration().setY(event.values[1]);
                this.imu.getLinearAcceleration().setZ(event.values[2]);
                double[] tmpCov = {0, 0, 0, 0, 0, 0, 0, 0, 0};// TODO Make Parameter
                this.imu.setLinearAccelerationCovariance(tmpCov);
                this.accelTime = event.timestamp;
            } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                this.imu.getAngularVelocity().setX(event.values[0]);
                this.imu.getAngularVelocity().setY(event.values[1]);
                this.imu.getAngularVelocity().setZ(event.values[2]);
                double[] tmpCov = {0, 0, 0, 0, 0, 0, 0, 0, 0};// TODO Make Parameter
                this.imu.setAngularVelocityCovariance(tmpCov);
                this.gyroTime = event.timestamp;
            } else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                float[] quaternion = new float[4];
                SensorManager.getQuaternionFromVector(quaternion, event.values);
                this.imu.getOrientation().setW(quaternion[0]);
                this.imu.getOrientation().setX(quaternion[1]);
                this.imu.getOrientation().setY(quaternion[2]);
                this.imu.getOrientation().setZ(quaternion[3]);
                double[] tmpCov = {0, 0, 0, 0, 0, 0, 0, 0, 0};// TODO Make Parameter
                this.imu.setOrientationCovariance(tmpCov);
                this.quatTime = event.timestamp;
            }

            // Currently storing event times in case I filter them in the future.  Otherwise they are used to determine if all sensors have reported.
            if ((this.accelTime != 0 || !this.hasAccel) &&
                    (this.gyroTime != 0 || !this.hasGyro) &&
                    (this.quatTime != 0 || !this.hasQuat)) {
                // Convert event.timestamp (nanoseconds uptime) into system time, use that as the header stamp
                long time_delta_millis = System.currentTimeMillis() - SystemClock.uptimeMillis();
                this.imu.getHeader().setStamp(Time.fromMillis(time_delta_millis + event.timestamp / 1000000));
                this.imu.getHeader().setFrameId(robotName);

                publisher.publish(this.imu);

                // Create a new message
                this.imu = (Imu) this.publisher.newMessage();

                // Reset times
                this.accelTime = 0;
                this.gyroTime = 0;
                this.quatTime = 0;
            }
        }

        @Override
        public void registerSelf(SensorManager sm, int sensorDelay) {

            s2 = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            s3 = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

            sm.registerListener(this, getRelatedSensor(), sensorDelay);
            sm.registerListener(this, s2, sensorDelay);
            sm.registerListener(this, s3, sensorDelay);
        }
    }
}
