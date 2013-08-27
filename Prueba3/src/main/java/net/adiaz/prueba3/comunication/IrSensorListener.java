package net.adiaz.prueba3.comunication;

/**
 * Interfaz que implementarán las clases que quieran suscribirse a las notificaciones de eventos de la placa.
 *
 * Recibirán el array con los 22 bytes del micro.
 *
 * Respuesta del micro:
 * Byte 0 - 0x81 (Cabecera)
 * Byte 1 - sensorIR0ByteAlto
 * Byte 2 - sensorIR0ByteBajo
 * ...
 * Byte 15  - sensorIR8ByteAlto
 * Byte 16  - sensorIR8ByteBajo
 * Byte 17  - sensorIRSuelo0ByteAlto
 * Byte 18  - sensorIRSuelo0ByteBajo
 * Byte 19  - sensorIRSuelo1ByteAlto
 * Byte 20  - sensorIRSuelo1ByteBajo
 * Byte 21 - checksum mensaje sin incluir cabecera
 *
 * Created by kerry on 1/06/13.
 */
public interface IrSensorListener {

    public void receiveData(byte[] data);

}
