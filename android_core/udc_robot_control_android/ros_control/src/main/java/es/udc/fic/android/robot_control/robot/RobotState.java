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
package es.udc.fic.android.robot_control.robot;

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


    private byte checksum(byte[] buff, int from, int limit){
        byte sum = 0;
        for (int i = from; i < limit; i++){
            sum += buff[i];
        }

        return sum;
    }


    /**
     * This method generates the message to send
     * @return
     */
    public byte[] message() {
        byte[] out = new byte[31];

        byte[] rightBytes = engineIntToBytes(rightEngine);
        byte[] leftBytes = engineIntToBytes(leftEngine);

        int pos = 0;
        out[0] = 0x37;
        out[1] = engineMode;
        out[2] = wheelState;
        out[3] = leftBytes[0];  // Left wheel, hight byte
        out[4] = leftBytes[1];  // Left wheel, low byte
        out[5] = rightBytes[0]; // Right wheel, hight byte
        out[6] = rightBytes[1]; // Right wheel, low byte

        try {
            Led led = leds[0];
            out[7] = (byte) led.getRed();
            out[8] = (byte) led.getGreen();
            out[9] = (byte) led.getBlue();
        }
        catch (NullPointerException e){
            out[7] = 0;
            out[8] = 0;
            out[9] = 0;
        }

        // Checksum
        out[10] = checksum(out, 1, 10);
        return out;
    }

    private byte[] engineIntToBytes(int motorValue) {
        byte low = (byte) motorValue;
        byte high = (byte) (motorValue >> 8);
        byte[] output = {high, low};
        return output;
    }


}
