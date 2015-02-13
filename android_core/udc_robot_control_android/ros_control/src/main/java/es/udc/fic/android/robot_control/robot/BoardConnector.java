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
 * This class simulates the read/write from the board connector.
 *
 * Created by kerry on 4/06/13.
 */
public class BoardConnector  {
    private static final String ACTION_USB_PERMISSION = "es.udc.fic.android.robot_control.USB_PERMISSION";


    private UsbManager manager;
    private UsbDevice device;
    private UsbDeviceConnection connection;
    private UsbInterface intf;
    private UsbEndpoint endpointOUT;
    private UsbEndpoint endpointIN;

    public boolean write(RobotState c) {
        boolean output = false;
        if (c != null) {
            Log.i(C.ROBOT_TAG, "Sending Command [ " + c.toString() + " ]");
            try {
                byte[] m = c.message();
                StringBuffer sb = new StringBuffer();
                for (int x = 0; x < m.length; x++) {
                    sb.append(String.valueOf(m[x]));
                    sb.append(" ");
                }
                Log.i(C.ROBOT_TAG, "In the wire [ " + sb.toString() + " ]");
                int result = connection.bulkTransfer(endpointOUT, m, m.length, 1000);
                Log.i(C.ROBOT_TAG, "Result of the write [ " + result + " ]");
                output = (result >= 0);
            }
            catch (Exception ex) {
                Log.w("Error sending data to the robot ", ex);
                output = false;
            }
        }
        else {
            Log.w(C.ROBOT_TAG, "Noting to send. Attempted to send a null state");
        }
        return output;
    }

    public byte[] read() {
        int BUFFER_LENGHT = 64;
        byte[] output = new byte[BUFFER_LENGHT];
        int result = -1;

        result = connection.bulkTransfer(endpointIN, output, output.length, 1000);
        Log.d(C.ROBOT_TAG, "Read result [ " + result + " ]");

        StringBuffer sb = new StringBuffer();
        for (int x = 0; x < BUFFER_LENGHT; x++) {
            sb.append(String.valueOf(output[x]));
            sb.append(" ");
        }
        Log.i(C.ROBOT_TAG, "Read from the wire [ " + sb.toString() + " ]");
        if (result > 0) {
            ByteArrayBuffer bas = new ByteArrayBuffer(result);
            bas.append(output, 0, result);
            bas.setLength(result);
            return bas.toByteArray();
        }
        else {
            Log.i(C.ROBOT_TAG, "Result of the read [ " + result + " ]");
            return null;
        }
    }

    public void connect(Context ctx, Intent intent) throws TransmisionErrorException {

        Log.i(C.ROBOT_TAG, "Connected to [ " + intent.getAction() + " ]. mode - Host");
        device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        Log.i(C.ROBOT_TAG, "Connected to [ " + device.getDeviceName() + " ]. mode - Host");
		/* Get the USB manager from the requesting context */
        this.manager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        connect();
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void manualConnect(Context ctx) throws TransmisionErrorException {
        Log.i(C.ROBOT_TAG, "Manually connected. mode - Host");
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
            //connect();
        }
        else {
            Log.i(C.ROBOT_TAG, "No devices found");
            throw new TransmisionErrorException("No devices found");
        }
    }

    public void disconnect() {
        Log.i(C.ROBOT_TAG, "Disconnected from [ " + device.getDeviceName() + " ] mode - Host");
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
                            connect();
                        }
                        else {
                            Log.i(C.ROBOT_TAG, "No accesory to connect");
                        }
                    }
                    else {
                        Log.w(C.ROBOT_TAG, "Permission denied for device " + mDevice);
                    }
                }
            }
            }
            catch (Exception ex) {
                Log.w(C.ROBOT_TAG, "Exception on BroadcastReceiver [ " + ex + " ]");
            }
        }
    };

    private void connect() throws TransmisionErrorException {
        Log.i(C.ROBOT_TAG, "Connected to [ " + device.getDeviceName() + " ]. mode - Host");
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

        Log.i(C.ROBOT_TAG, "Device openned");

            /* Claim the required interface to gain access to it */
        if(connection.claimInterface(intf, true) == true) {
            /* Get the OUT endpoint.  It is the second endpoint in the interface */
            endpointOUT = intf.getEndpoint(1);
            /* Get the IN endpoint.  It is the first endpoint in the interface */
            endpointIN = intf.getEndpoint(0);
            Log.i(C.ROBOT_TAG, "Connection established");
        } else {
                /* if the interface claim failed, we should close the
                 * connection and exit.
                 */
            connection.close();
            Log.i(C.ROBOT_TAG, "Interface couldn't be claimed");
            throw new TransmisionErrorException("Interface couldn't be claimed");
        }
    }

}
