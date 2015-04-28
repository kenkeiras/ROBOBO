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
package es.udc.fic.android.board;

import android.content.IntentFilter;
import android.util.Log;

import udc_robot_control_msgs.Led;

import java.util.List;

/**
 * Created by kerry on 4/08/13.
 * Esta clase representa el estado interno del robot. Se utiliza para implementar el protocolo de envio al robot
 *
 */
public class RobotState {

    public static int NUM_LEDS = 8;

    public byte engineMode;
    public byte wheelState;
    public int leftEngine, rightEngine;
    public Led[] leds;

    public RobotState() {
        leds = new Led[NUM_LEDS];
        leftEngine = rightEngine = 0;
    }

    public void setEngines(byte wheelState, int leftEngine, int rightEngine) {
        this.wheelState = wheelState;
        this.leftEngine = leftEngine;
        this.rightEngine = rightEngine;
    }

    public void setLeds(List<Led> ledList) {
        for (Led l:ledList) {
            if (l.getLedNumber() == Led.ALL_LEDS) {
                // Set all the leds equal
                for (int x = 0; x < leds.length; x++) {
                    leds[x] = l;
                }
            }
            else {
                // Just one led
                leds[l.getLedNumber()] = l;
            }
        }
    }

    public void reset() {
        leftEngine = rightEngine = 0;
        leds = new Led[NUM_LEDS];
    }
}
