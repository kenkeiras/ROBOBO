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

import es.udc.robotcontrol.utils.Constants;

import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import udc_robot_control_msgs.AndroidSensor3;


public class GyroscopeUncalibratedPublisher extends AbstractSensorsPublisher {
    // TODO: Check names
    private static String QUEUE_NAME = Constants.TOPIC_GYROSCOPE_UNCALIBRATED;

    public GyroscopeUncalibratedPublisher(Context ctx, String robotName) {
        super(ctx, robotName);
    }


    @Override
    protected int getSensorType() {
        return Sensor.TYPE_GYROSCOPE_UNCALIBRATED;
    }

    @Override
    protected Publisher createPublisher(ConnectedNode n) {
        String queueName = robotName + "/" + QUEUE_NAME;
        return n.newPublisher(queueName, AndroidSensor3._TYPE);
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

        @Override
        public void onSensorChanged(SensorEvent event) {
            sensorChangedSensor3(event, robotName, getSensorType());
        }
    }
}
