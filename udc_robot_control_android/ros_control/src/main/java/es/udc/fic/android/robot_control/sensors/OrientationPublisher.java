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
import udc_robot_control_java.AndroidSensor3;


public class OrientationPublisher extends AbstractSensorsPublisher {

    private static String QUEUE_NAME = Constantes.TOPIC_ORIENTATION;
            ;

    public OrientationPublisher(Context ctx, String robotName) {
        super(ctx, robotName);
    }


    @Override
    protected int getSensorType() {
        return Sensor.TYPE_ORIENTATION;
    }


    @Override
    protected Publisher createPublisher(ConnectedNode n) {
        String queueName = robotName + "/" + QUEUE_NAME;
        return n.newPublisher(queueName, AndroidSensor3._TYPE);
    }

    @Override
    protected AbstractSensorEventListener createListener(Publisher p) {
        Sensor sensor = sensorManager.getDefaultSensor(getSensorType());
        AcelSensorListener pl = new AcelSensorListener(p, sensor);
        return pl;
    }

    @Override
    protected String getTopicName() {
        return QUEUE_NAME;
    }

    private class AcelSensorListener extends AbstractSensorEventListener {

        protected AcelSensorListener(Publisher publisher, Sensor s) {
            super(s, publisher);
        }

        //	@Override
        public void onSensorChanged(SensorEvent event) {
            sensorChangedSensor3(event, robotName, getSensorType());
        }
    }

}
