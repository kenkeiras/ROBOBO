package es.udc.fic.android.robot_control.robot;

import udc_robot_control_java.Engines;
import udc_robot_control_java.Led;

import java.util.List;

/**
 * Created by kerry on 4/08/13.
 * Esta clase representa el estado interno del robot. Se utiliza para implementar el protocolo de envio al robot
 *
 */
public class EstadoRobot {

    public static int NUM_LEDS = 8;

    public Engines motores;
    public Led[] leds;

    public EstadoRobot() {
        leds = new Led[NUM_LEDS];
    }

    public void setMotores(Engines m) {
        motores = m;
    }

    public void setLeds(List<Led> ledList) {
        for (Led l:ledList) {
            if (l.getLedNumber() == Led.ALL_LEDS) {
                // Todos los leds iguales
                for (int x = 0; x < leds.length; x++) {
                    leds[x] = l;
                }
            }
            else {
                // Un unico led
                leds[l.getLedNumber()] = l;
            }
        }
    }

    public void reset() {
        motores = null;
        leds = new Led[NUM_LEDS];
    }

    /**
     * Este método se encarga de generar un mensaje para enviar
     * @return
     */
    public byte[] mensaje() {
        byte[] salida = new byte[31];

        int pos = 0;
        byte checksum = 0;
        salida[pos++] = 0x37;
        if (motores != null) {
            salida[pos] = (byte) motores.getMotorMode();
            checksum += salida[pos++];
            byte[] mi = motorIntToBytes(motores.getLeftEngine());
            salida[pos] = mi[0];
            checksum += salida[pos++];
            salida[pos] = mi[1];
            checksum += salida[pos++];
            byte[] md = motorIntToBytes(motores.getRightEngine());
            salida[pos] = md[0];
            checksum += salida[pos++];
            salida[pos] = md[1];
            checksum += salida[pos++];
        }
        else {
            // No hay valor para motores. Estamos parados
            for (int x = 0; x < 5; x++) {
                salida[pos] = 0;
                checksum += salida[pos++];
            }
        }

        for (int x = 0; x < leds.length; x++) {
            if (leds[x] != null) {
                byte r = (byte) leds[x].getRed();
                byte g = (byte) leds[x].getGreen();
                byte b = (byte) leds[x].getBlue();
                salida[pos] = r;
                checksum += salida[pos++];
                salida[pos] = g;
                checksum += salida[pos++];
                salida[pos] = b;
                checksum += salida[pos++];
            }
            else {
                salida[pos] = 0;
                checksum += salida[pos++];
                salida[pos] = 0;
                checksum += salida[pos++];
                salida[pos] = 0;
                checksum += salida[pos++];
            }
        }
        // Checksum
        salida[pos] = checksum;
        return salida;
    }

    private byte[] motorIntToBytes(int motorValue) {
        byte bajo = (byte) motorValue;
        byte alto = (byte) (motorValue >> 8);
        byte[] salida = {alto, bajo};
        return salida;
    }


}
