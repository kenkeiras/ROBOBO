package es.udc.robotcontrol.testapp.comunication;

/**
 * Esta clase encapsula un comando a enviar a la placa.
 *
 * Created by kerry on 2/06/13.
 */
public class Comando {


    String origen;

    private byte modoMotor;
    private int motorIzquierdo;
    private int motorDerecho;

    private int[] leds;


    public Comando() {
        leds = new int[Constantes.NUMERO_LEDS];
        origen = null;
    }

    public Comando(String entrada) throws IllegalArgumentException {

        leds = new int[Constantes.NUMERO_LEDS];
        origen = entrada;

        // Sintaxis para el fichero: COMANDO MODO_MOTOR MOTOR_IZQUIERDO MOTOR_DERECHO [LED0...LEDN]

        // Descartamos comentarios
        String[] partes = entrada.split("#");

        entrada = partes[0];
        partes = entrada.split(" ");

        if (!Constantes.COMANDO.equalsIgnoreCase(partes[0])) throw new IllegalArgumentException("La entrada no es un COMANDO");

        try {
            modoMotor = Byte.parseByte(partes[1]);
            motorIzquierdo = Integer.parseInt(partes[2]);
            motorDerecho = Integer.parseInt(partes[3]);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("La entrada no es un comando valido [ " + entrada + " ] Fallo en la parte obligatoria", ex);
        }

        try {
            int tope = Math.min(partes.length, (Constantes.NUMERO_LEDS + 4));
            for (int x = 4; x < tope; x++) {
                int pos = x - 4;
                leds[pos] = Integer.parseInt(partes[x]);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("La entrada no es un comando valido [ " + entrada + " ] Fallo en la parte opcional", ex);
        }
    }

    @Override
    public String toString() {
        if (origen != null) {
            return origen;
        }
        else {
            return super.toString();
        }
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
        salida[pos] = modoMotor;
        checksum += salida[pos++];
        byte[] mi = motorIntToBytes(motorIzquierdo);
        salida[pos] = mi[0];
        checksum += salida[pos++];
        salida[pos] = mi[1];
        checksum += salida[pos++];
        byte[] md = motorIntToBytes(motorDerecho);
        salida[pos] = md[0];
        checksum += salida[pos++];
        salida[pos] = md[1];
        checksum += salida[pos++];

        for (int x = 0; x < Constantes.NUMERO_LEDS; x++) {
            byte[] led = intToRgb(leds[x]);

            for (int y = 0; y < led.length; y++) {
                salida[pos] = led[y];
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

    /**
     * Convierte un valor entero a tres bytes RGB.
     *
     * @param rgb
     * @return
     */
    private byte[] intToRgb(int rgb) {
        byte blue  = (byte) rgb;
        byte green = (byte) (rgb >> 8);
        byte red   = (byte) (rgb >> 16);
        byte[] salida = {red, green, blue};
        return salida;
    }

}
