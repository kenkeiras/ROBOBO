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
import sensor_msgs.FluidPressure;


public class PressurePublisher extends AbstractSensorsPublisher {

    private String QUEUE_NAME = Constantes.TOPIC_PRESSURE;

    public PressurePublisher(Context ctx, String robotName) {
        super(ctx, robotName);
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_PRESSURE;
    }

    @Override
    protected Publisher createPublisher(ConnectedNode n) {
        String queueName = robotName + "/" + QUEUE_NAME;
        return n.newPublisher(queueName, FluidPressure._TYPE);
    }

    @Override
    protected String getTopicName() {
        return QUEUE_NAME;
    }

    @Override
    protected AbstractSensorEventListener createListener(Publisher p) {
        Sensor sensor = sensorManager.getDefaultSensor(getSensorType());
        FluidPressureListener pl = new FluidPressureListener(sensor, p);
        return pl;
    }


    protected class FluidPressureListener extends AbstractSensorEventListener {

        private FluidPressureListener(Sensor s, Publisher p) {
            super(s, p);
        }


        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_PRESSURE) {
                FluidPressure msg = (FluidPressure) this.publisher.newMessage();
                long time_delta_millis = System.currentTimeMillis() - SystemClock.uptimeMillis();
                msg.getHeader().setStamp(Time.fromMillis(time_delta_millis + event.timestamp / 1000000));
                msg.getHeader().setFrameId(robotName);
                msg.setFluidPressure(event.values[0]);
                msg.setVariance(0.0);

                publisher.publish(msg);
            }
        }

    }


}
