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

package net.adiaz.prueba3.comunication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.*;
import android.util.Log;
import org.apache.http.util.ByteArrayBuffer;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Esta clase simula la lectura y escritura del conector de la placa.
 *
 * Created by kerry on 4/06/13.
 */
public class ConectorPlaca implements ConectorPlacaInterface {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";


    private UsbManager manager;
    private UsbDevice device;
    private UsbDeviceConnection connection;
    private UsbInterface intf;
    private UsbEndpoint endpointOUT;
    private UsbEndpoint endpointIN;

    public boolean escribir(Comando c) {
        boolean salida = false;
        if (c != null) {
            Log.i(Constantes.TAG_CONECTOR, "Enviando Comando [ " + c.toString() + " ]");
            byte[] m = c.mensaje();
            StringBuffer sb = new StringBuffer();
            for (int x = 0; x < m.length; x++) {
                sb.append(String.valueOf(m[x]));
                sb.append(" ");
            }
            Log.i(Constantes.TAG_CONECTOR, "En el cable [ " + sb.toString() + " ]");
            int result = connection.bulkTransfer(endpointOUT, m, m.length, 1000);
            Log.i(Constantes.TAG_CONECTOR, "Resultado de escritura [ " + result + " ]");
            salida = (result >= 0);
        }
        else {
            Log.i(Constantes.TAG_CONECTOR, "Nada que enviar");
        }
        return salida;
    }

    public byte[] leer() {
        int BUFFER_LENGHT = 64;
        byte[] salida = new byte[BUFFER_LENGHT];
			/* Read the push button status */
        int result = -1;

        result = connection.bulkTransfer(endpointIN, salida, salida.length, 1000);
        Log.d(Constantes.TAG_CONECTOR, "Resultado de lectura [ " + result + " ]");

        StringBuffer sb = new StringBuffer();
        for (int x = 0; x < BUFFER_LENGHT; x++) {
            sb.append(String.valueOf(salida[x]));
            sb.append(" ");
        }
        Log.i(Constantes.TAG_CONECTOR, "Leido en el cable [ " + sb.toString() + " ]");
        if (result > 0) {
            ByteArrayBuffer bas = new ByteArrayBuffer(result);
            bas.append(salida, 0, result);
            bas.setLength(result);
            return bas.toByteArray();
        }
        else {
            Log.i(Constantes.TAG_CONECTOR, "Resultado de lectura [ " + result + " ]");
            return null;
        }
    }

    @Override
    public void conectar(Context ctx, Intent intent) throws TransmisionErrorException {

        Log.i(Constantes.TAG_CONECTOR, "Conectando a [ " + intent.getAction() + " ]. modo - Host");
        device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        Log.i(Constantes.TAG_CONECTOR, "Conectando a [ " + device.getDeviceName() + " ]. modo - Host");
		/* Get the USB manager from the requesting context */
        this.manager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        conectar();
    }

    public void conectarManual(Context ctx) throws TransmisionErrorException {
        Log.i(Constantes.TAG_CONECTOR, "Conectando manualmente. modo - Host");
		/* Get the USB manager from the requesting context */
        this.manager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> it = deviceList.values().iterator();

        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(ctx, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        ctx.registerReceiver(mUsbReceiver, filter);

        if (it.hasNext()) {
            device = it.next();
            manager.requestPermission(device, mPermissionIntent);
            conectar();
        }
        else {
            Log.i(Constantes.TAG_CONECTOR, "No se han encontrado dispositivos");
            throw new TransmisionErrorException("No se han encontrado dispositivos");
        }
    }

    @Override
    public void desconectar() {
        Log.i(Constantes.TAG_CONECTOR, "Desconectando a [ " + device.getDeviceName() + " ] modo - Host");
        if (connection != null) {
            connection.close();
        }
    }


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            try {
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice mDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(mDevice != null){
                            //call method to set up device communication
                            device = mDevice;
                            conectar();
                        }
                        else {
                            Log.i(Constantes.TAG_CONECTOR, "No hay accesorio para conectar");
                        }
                    }
                    else {
                        Log.w(Constantes.TAG_CONECTOR, "Permiso denegado para el accesorio " + mDevice);
                    }
                }
            }
            }
            catch (Exception ex) {
                Log.w(Constantes.TAG_CONECTOR, "Exception on BroadcastReceiver [ " + ex + " ]");
            }
        }
    };

    private void conectar() throws TransmisionErrorException {
        Log.i(Constantes.TAG_CONECTOR, "Conectando a [ " + device.getDeviceName() + " ]. modo - Host");
            /*
             * Get the required interface from the USB device.  In this case
             * we are hard coding the interface number to 0.  In a dynamic example
             * the code could scan through the interfaces to find the right
             * interface.  In this case since we know the exact device we are connecting
             * to, we can hard code it.
             */
        intf = device.getInterface(0);

            /* Open a connection to the USB device */
        connection = manager.openDevice(device);

        Log.i(Constantes.TAG_CONECTOR, "Dispositivo abierto");

            /* Claim the required interface to gain access to it */
        if(connection.claimInterface(intf, true) == true) {
            /* Get the OUT endpoint.  It is the second endpoint in the interface */
            endpointOUT = intf.getEndpoint(1);
            /* Get the IN endpoint.  It is the first endpoint in the interface */
            endpointIN = intf.getEndpoint(0);
            Log.i(Constantes.TAG_CONECTOR, "Conexion finalizada");
        } else {
                /* if the interface claim failed, we should close the
                 * connection and exit.
                 */
            connection.close();
            throw new TransmisionErrorException("No se puede reclamar el interfaz");
        }
    }

}
