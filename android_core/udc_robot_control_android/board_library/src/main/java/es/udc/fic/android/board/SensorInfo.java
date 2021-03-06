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

/**
 * Esta clase contiene información sobre una lectura de sensores de la placa
 *
 * Created by kerry on 4/06/13.
 */
public class SensorInfo {

    public static int MSG_LENGTH = 24;

    private int sIr1;
    private int sIr2;
    private int sIr3;
    private int sIr4;
    private int sIr5;
    private int sIr6;
    private int sIr7;
    private int sIr8;
    private int sIr9;
    private int sIrS1;
    private int sIrS2;

    public SensorInfo() {

    }


    /// @TODO move to a utilities class
    private int toUnsignedInt(byte b){
        int result = 256 + b;
        return result & 0xFF;
    }


    private static int remap(int raw_position){

        int position = (raw_position - 1) / 2;
        switch(position){
        case 0: // Not used, keep it in a inocuous register
            return 12;

        case 1: case 2:
            return position + 7; // 1 -> 8, 2 -> 9

        case 10: case 11:
            return position;

        default:
            return position - 2; // 3 -> 1, 4 -> 2, ... 9 -> 7
        }
    }


    public SensorInfo(byte[] raw) throws IllegalArgumentException{
        if (raw.length < MSG_LENGTH) {
            throw new IllegalArgumentException("Data not valid");
        }
        else {
            if (raw[0] != (byte) 0x81) {
                throw new IllegalArgumentException("Incorrect lecture (byte 0 != 0x81)");
            }
            byte checksum = 0;
            for (int x = 3; x < 22; x += 2) {
                int value = (toUnsignedInt(raw[x]) << 8) + toUnsignedInt(raw[x+1]);
                // Raw data is in the following order:
                // 8, 9, 1, 2, 3, 4, 5, 6, 7
                setValue(remap(x), value);
                checksum += raw[x];
                checksum += raw[x+1];
            }

            // Checksum doesn't seem to be implemented as of now
            // if (lectura[23] != checksum) {
            //     throw new IllegalArgumentException("Lectura incorrecta. Checksum incorrecto");
            // }
        }
    }

    @Override
    public String toString() {
        String salida = "SensorInfo " +
                "[ " + getsIr1() + " ]" +
                "[ " + getsIr2() + " ]" +
                "[ " + getsIr3() + " ]" +
                "[ " + getsIr4() + " ]" +
                "[ " + getsIr5() + " ]" +
                "[ " + getsIr6() + " ]" +
                "[ " + getsIr7() + " ]" +
                "[ " + getsIr8() + " ]" +
                "[ " + getsIr9() + " ]" +
                "[ " + getsIrS1() + " ]" +
                "[ " + getsIrS2() + " ]";
        return salida;
    }


    private void setValue(int x, int value) {
        switch (x) {
            case 1:
                setsIr1(value);
                break;
            case 2:
                setsIr2(value);
                break;
            case 3:
                setsIr3(value);
                break;
            case 4:
                setsIr4(value);
                break;
            case 5:
                setsIr5(value);
                break;
            case 6:
                setsIr6(value);
                break;
            case 7:
                setsIr7(value);
                break;
            case 8:
                setsIr8(value);
                break;
            case 9:
                setsIr9(value);
                break;
            case 10:
                setsIrS1(value);
                break;
            case 11:
                setsIrS2(value);
                break;
        }
    }

    public int getsIr1() {
        return sIr1;
    }

    public void setsIr1(int sIr1) {
        this.sIr1 = sIr1;
    }

    public int getsIr2() {
        return sIr2;
    }

    public void setsIr2(int sIr2) {
        this.sIr2 = sIr2;
    }

    public int getsIr3() {
        return sIr3;
    }

    public void setsIr3(int sIr3) {
        this.sIr3 = sIr3;
    }

    public int getsIr4() {
        return sIr4;
    }

    public void setsIr4(int sIr4) {
        this.sIr4 = sIr4;
    }

    public int getsIr5() {
        return sIr5;
    }

    public void setsIr5(int sIr5) {
        this.sIr5 = sIr5;
    }

    public int getsIr6() {
        return sIr6;
    }

    public void setsIr6(int sIr6) {
        this.sIr6 = sIr6;
    }

    public int getsIr7() {
        return sIr7;
    }

    public void setsIr7(int sIr7) {
        this.sIr7 = sIr7;
    }

    public int getsIr8() {
        return sIr8;
    }

    public void setsIr8(int sIr8) {
        this.sIr8 = sIr8;
    }

    public int getsIr9() {
        return sIr9;
    }

    public void setsIr9(int sIr9) {
        this.sIr9 = sIr9;
    }

    public int getsIrS1() {
        return sIrS1;
    }

    public void setsIrS1(int sIrS1) {
        this.sIrS1 = sIrS1;
    }

    public int getsIrS2() {
        return sIrS2;
    }

    public void setsIrS2(int sIrS2) {
        this.sIrS2 = sIrS2;
    }

}
