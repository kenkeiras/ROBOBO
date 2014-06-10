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

package es.udc.robot_control.gui.action;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 9/08/13
 * Time: 19:22
 *
 * This class is a data model for the combo box.
 * Represents sensors in the robot
 *
 */
public class SensorModel {

    private int sensorValue;
    private String sensorName;

    public SensorModel(int value, String name) {
        setSensorValue(value);
        setSensorName(name);
    }

    @Override
    public String toString() {
        return getSensorName();
    }

    public int getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(int sensorValue) {
        this.sensorValue = sensorValue;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }
}
