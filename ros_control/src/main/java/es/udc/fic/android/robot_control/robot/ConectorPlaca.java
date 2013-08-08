package es.udc.fic.android.robot_control.robot;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.*;
import android.util.Log;
import es.udc.fic.android.robot_control.utils.C;
import org.apache.http.util.ByteArrayBuffer;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Esta clase simula la lectura y escritura del conector de la placa.
 *
 * Created by kerry on 4/06/13.
 */
public class ConectorPlaca  {
    private static final String ACTION_USB_PERMISSION = "es.udc.fic.android.robot_control.USB_PERMISSION";


    private UsbManager manager;
    private UsbDevice device;
    private UsbDeviceConnection connection;
    private UsbInterface intf;
    private UsbEndpoint endpointOUT;
    private UsbEndpoint endpointIN;

    public boolean escribir(EstadoRobot c) {
        boolean salida = false;
        if (c != null) {
            Log.i(C.ROBOT_TAG, "Enviando Comando [ " + c.toString() + " ]");
            try {
                byte[] m = c.mensaje();
                StringBuffer sb = new StringBuffer();
                for (int x = 0; x < m.length; x++) {
                    sb.append(String.valueOf(m[x]));
                    sb.append(" ");
                }
                Log.i(C.ROBOT_TAG, "En el cable [ " + sb.toString() + " ]");
                int result = connection.bulkTransfer(endpointOUT, m, m.length, 1000);
                Log.i(C.ROBOT_TAG, "Resultado de escritura [ " + result + " ]");
                salida = (result >= 0);
            }
            catch (Exception ex) {
                Log.w("Error enviando datos al robot ", ex);
                salida = false;
            }
        }
        else {
            Log.w(C.ROBOT_TAG, "Nada que enviar. Se ha intentado enviar un estado nulo");
        }
        return salida;
    }

    public byte[] leer() {
        int BUFFER_LENGHT = 64;
        byte[] salida = new byte[BUFFER_LENGHT];
        int result = -1;

        result = connection.bulkTransfer(endpointIN, salida, salida.length, 1000);
        Log.d(C.ROBOT_TAG, "Resultado de lectura [ " + result + " ]");

        StringBuffer sb = new StringBuffer();
        for (int x = 0; x < BUFFER_LENGHT; x++) {
            sb.append(String.valueOf(salida[x]));
            sb.append(" ");
        }
        Log.i(C.ROBOT_TAG, "Leido en el cable [ " + sb.toString() + " ]");
        if (result > 0) {
            ByteArrayBuffer bas = new ByteArrayBuffer(result);
            bas.append(salida, 0, result);
            bas.setLength(result);
            return bas.toByteArray();
        }
        else {
            Log.i(C.ROBOT_TAG, "Resultado de lectura [ " + result + " ]");
            return null;
        }
    }

    public void conectar(Context ctx, Intent intent) throws TransmisionErrorException {

        Log.i(C.ROBOT_TAG, "Conectando a [ " + intent.getAction() + " ]. modo - Host");
        device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        Log.i(C.ROBOT_TAG, "Conectando a [ " + device.getDeviceName() + " ]. modo - Host");
		/* Get the USB manager from the requesting context */
        this.manager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        conectar();
    }

    public void conectarManual(Context ctx) throws TransmisionErrorException {
        Log.i(C.ROBOT_TAG, "Conectando manualmente. modo - Host");
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
            //conectar();
        }
        else {
            Log.i(C.ROBOT_TAG, "No se han encontrado dispositivos");
            throw new TransmisionErrorException("No se han encontrado dispositivos");
        }
    }

    public void desconectar() {
        Log.i(C.ROBOT_TAG, "Desconectando a [ " + device.getDeviceName() + " ] modo - Host");
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
                    UsbDevice mDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(mDevice != null){
                            //call method to set up device communication
                            device = mDevice;
                            conectar();
                        }
                        else {
                            Log.i(C.ROBOT_TAG, "No hay accesorio para conectar");
                        }
                    }
                    else {
                        Log.w(C.ROBOT_TAG, "Permiso denegado para el accesorio " + mDevice);
                    }
                }
            }
            }
            catch (Exception ex) {
                Log.w(C.ROBOT_TAG, "Exception on BroadcastReceiver [ " + ex + " ]");
            }
        }
    };

    private void conectar() throws TransmisionErrorException {
        Log.i(C.ROBOT_TAG, "Conectando a [ " + device.getDeviceName() + " ]. modo - Host");
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

        Log.i(C.ROBOT_TAG, "Dispositivo abierto");

            /* Claim the required interface to gain access to it */
        if(connection.claimInterface(intf, true) == true) {
            /* Get the OUT endpoint.  It is the second endpoint in the interface */
            endpointOUT = intf.getEndpoint(1);
            /* Get the IN endpoint.  It is the first endpoint in the interface */
            endpointIN = intf.getEndpoint(0);
            Log.i(C.ROBOT_TAG, "Conexion realizada");
        } else {
                /* if the interface claim failed, we should close the
                 * connection and exit.
                 */
            connection.close();
            Log.i(C.ROBOT_TAG, "No se puede reclamar el interfaz");
            throw new TransmisionErrorException("No se puede reclamar el interfaz");
        }
    }

}
