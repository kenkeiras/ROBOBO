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
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import es.udc.fic.android.robot_control.utils.C;
import org.ros.node.topic.Publisher;

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

}
