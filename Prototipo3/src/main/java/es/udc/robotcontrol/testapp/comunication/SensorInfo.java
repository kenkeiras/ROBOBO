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
package es.udc.robotcontrol.testapp.comunication;

/**
 * Esta clase contiene información sobre una lectura de sensores de la placa
 *
 * Created by kerry on 4/06/13.
 */
public class SensorInfo {

    private int sIr0;
    private int sIr1;
    private int sIr2;
    private int sIr3;
    private int sIr4;
    private int sIr5;
    private int sIr6;
    private int sIr7;
    private int sIr8;
    private int sIrS1;
    private int sIrS2;

    public SensorInfo() {

    }

    public SensorInfo(byte[] lectura) throws IllegalArgumentException{
        if (lectura.length != 24) {
            throw new IllegalArgumentException("Lectura incorrecta");
        }
        else {
            if (lectura[0] != (byte) 0x81) {
                throw new IllegalArgumentException("Lectura incorrecta (byte 0 != 0x81)");
            }
            byte checksum = 0;
            for (int x = 1; x < 22; x += 2) {
                int valor = lectura[x] << 8 + lectura[x+1];
                setValor(x, valor);
                checksum += lectura[x];
                checksum += lectura[x+1];
            }
            if (lectura[21] != checksum) {
                throw new IllegalArgumentException("Lectura incorrecta. Checksum incorrecto");
            }
        }
    }

    @Override
    public String toString() {
        String salida = "SensorInfo " +
                "[ " + getsIr0() + " ]" +
                "[ " + getsIr1() + " ]" +
                "[ " + getsIr2() + " ]" +
                "[ " + getsIr3() + " ]" +
                "[ " + getsIr4() + " ]" +
                "[ " + getsIr5() + " ]" +
                "[ " + getsIr6() + " ]" +
                "[ " + getsIr7() + " ]" +
                "[ " + getsIr8() + " ]" +
                "[ " + getsIrS1() + " ]" +
                "[ " + getsIrS2() + " ]";
        return salida;
    }

    /**
     * Utilidad para devolver los parametros de forma que se puedan pegar a una URL para una llamada rest.
     * @return
     */
    public String toUrlFormat() {
        String salida = getsIr0() + "/" + getsIr1() + "/" + getsIr2() + "/" + getsIr3() + "/" + getsIr4() + "/" +
                        getsIr5() + "/" + getsIr6() + "/" + getsIr7() + "/" + getsIr8() + "/" +
                        getsIrS1() + "/" + getsIrS2();
        return salida;
    }

    private void setValor(int x, int valor) {
        switch (x) {
            case 1:
                setsIr1(valor);
                break;
            case 3:
                setsIr2(valor);
                break;
            case 5:
                setsIr3(valor);
                break;
            case 7:
                setsIr4(valor);
                break;
            case 9:
                setsIr5(valor);
                break;
            case 11:
                setsIr6(valor);
                break;
            case 13:
                setsIr7(valor);
                break;
            case 15:
                setsIr8(valor);
                break;
            case 17:
                setsIrS1(valor);
                break;
            case 19:
                setsIrS2(valor);
                break;
        }
    }

    public int getsIr0() {
        return sIr0;
    }

    public void setsIr0(int sIr0) {
        this.sIr0 = sIr0;
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
