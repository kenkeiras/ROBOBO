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
