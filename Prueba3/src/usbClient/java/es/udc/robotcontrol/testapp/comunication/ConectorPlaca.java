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

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;
import es.udc.robotcontrol.testapp.comunication.Comando;
import es.udc.robotcontrol.testapp.comunication.ConectorPlacaInterface;
import es.udc.robotcontrol.testapp.comunication.Constantes;
import es.udc.robotcontrol.testapp.comunication.TransmisionErrorException;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * Esta clase simula la lectura y escritura del conector de la placa.
 *
 * Created by kerry on 4/06/13.
 */
public class ConectorPlaca implements ConectorPlacaInterface {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private UsbManager manager;
    private UsbAccessory accessory;
    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;
    private ParcelFileDescriptor mFileDescriptor;

    public boolean escribir(Comando c) {
        if (c != null) {
            Log.i(Constantes.TAG_CONECTOR, "Enviando Comando [ " + c.toString() + " ]");
            // TODO: Enviar esto por el cable
            byte[] m = c.mensaje();
            StringBuffer sb = new StringBuffer();
            for (int x = 0; x < m.length; x++) {
                sb.append(String.valueOf(m[x]));
                sb.append(" ");
            }
            Log.i(Constantes.TAG_CONECTOR, "En el cable [ " + sb.toString() + " ]");
        }
        else {
            Log.i(Constantes.TAG_CONECTOR, "Nada que enviar");
        }
        return true;
    }

    public byte[] leer() {
        byte[] salida = new byte[22];
        salida[0] = (byte) 0x81;
        byte checksum = 0;
        for (int x = 1; x < salida.length - 1; x += 2) {
            salida[x] = 0;
            salida[x+1] = 1;
            checksum += 1;
        }
        salida[salida.length -1] = checksum;
        return salida;
    }

    @Override
    public void conectar(Context ctx, Intent intent) {
        Log.i(Constantes.TAG_CONECTOR, "Conectando a [ " + intent.getAction() + " ]. Modo - Cliente");
        manager = UsbManager.getInstance(ctx);
        accessory = UsbManager.getAccessory(intent);
        conectar();
    }

    @Override
    public void conectarManual(Context ctx) throws TransmisionErrorException {
        Log.i(Constantes.TAG_CONECTOR, "Conectando Manualmente. Modo - Cliente");
        manager = UsbManager.getInstance(ctx);
        UsbAccessory[] accesoryList =  manager.getAccessoryList();
        if (accesoryList.length > 0) {
            this.accessory = accesoryList[0];
        }
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(ctx, 0, new Intent(ACTION_USB_PERMISSION), 0);
        manager.requestPermission(accessory, mPermissionIntent);
    }

    @Override
    public void desconectar() {
        Log.i(Constantes.TAG_CONECTOR, "Desconectando. Modo - Cliente");
        try {
            mInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mFileDescriptor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = UsbManager.getAccessory(intent);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

                        if(accessory != null){
                            conectar();
                        }
                        else {
                            Log.i(Constantes.TAG_CONECTOR, "No hay accesorio para conectar");
                        }
                    }
                    else {
                        Log.w(Constantes.TAG_CONECTOR, "Permiso denegado para el accesorio " + accessory);
                    }
                }
            }
        }
    };
    
    private void conectar() {
        Log.i(Constantes.TAG_CONECTOR, "Conectando al accesorio " + accessory);
        mFileDescriptor = manager.openAccessory(accessory);
        if (mFileDescriptor != null) {
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);
        }
    }
    
}
