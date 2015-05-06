package es.udc.fic.android.board;
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

import es.udc.fic.android.board.utils.BoardMessageBuilder;

/**
 * This class simulates the read/write from the board connector.
 *
 * Created by kerry on 4/06/13.
 */
public class BoardConnector  {
    private static final String ACTION_USB_PERMISSION = "es.udc.fic.android.board.USB_PERMISSION";

    private UsbManager manager;
    private UsbDevice device;
    private UsbDeviceConnection connection;
    private UsbInterface intf;
    private UsbEndpoint endpointOUT;
    private UsbEndpoint endpointIN;

    public boolean write(RobotState robotState) {
        boolean output = false;
        if (robotState != null) {
            Log.i(BoardConstants.TAG, "Sending Command [ " + robotState.toString() + " ]");
            try {
                byte[] m = BoardMessageBuilder.message(robotState);
                StringBuffer sb = new StringBuffer();
                for (int x = 0; x < m.length; x++) {
                    sb.append(String.valueOf(m[x]));
                    sb.append(" ");
                }
                Log.i(BoardConstants.TAG, "In the wire [ " + sb.toString() + " ]");
                int result = connection.bulkTransfer(endpointOUT, m, m.length, 1000);
                Log.i(BoardConstants.TAG, "Result of the write [ " + result + " ]");
                output = (result >= 0);
            }
            catch (Exception ex) {
                Log.w(BoardConstants.TAG, "Sending data to board", ex);
                output = false;
            }
        }
        else {
            Log.w(BoardConstants.TAG, "Noting to send. Attempted to send a null state");
        }
        return output;
    }

    public byte[] read() {
        int BUFFER_LENGHT = 64;
        byte[] output = new byte[BUFFER_LENGHT];
        int result = -1;

        result = connection.bulkTransfer(endpointIN, output, output.length, 1000);
        Log.d(BoardConstants.TAG, "Read result [ " + result + " ]");

        StringBuffer sb = new StringBuffer();
        for (int x = 0; x < BUFFER_LENGHT; x++) {
            sb.append(String.valueOf(output[x]));
            sb.append(" ");
        }
        Log.i(BoardConstants.TAG, "Read from the wire [ " + sb.toString() + " ]");
        if (result > 0) {
            ByteArrayBuffer bas = new ByteArrayBuffer(result);
            bas.append(output, 0, result);
            bas.setLength(result);
            return bas.toByteArray();
        }
        else {
            Log.i(BoardConstants.TAG, "Result of the read [ " + result + " ]");
            return null;
        }
    }

    public void connect(Context ctx, Intent intent) throws TransmisionErrorException {

        Log.i(BoardConstants.TAG, "Connected to [ " + intent.getAction() + " ]. mode - Host");
        device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        Log.i(BoardConstants.TAG, "Connected to [ " + device.getDeviceName() + " ]. mode - Host");
		/* Get the USB manager from the requesting context */
        this.manager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        connect();
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void manualConnect(Context ctx) throws TransmisionErrorException {
        Log.i(BoardConstants.TAG, "Manually connected. mode - Host");
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
            Log.i(BoardConstants.TAG, "No devices found");
            throw new TransmisionErrorException("No devices found");
        }
    }

    public void disconnect() {
        Log.i(BoardConstants.TAG, "Disconnected from [ " + device.getDeviceName() + " ] mode - Host");
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
                            Log.i(BoardConstants.TAG, "No accesory to connect");
                        }
                    }
                    else {
                        Log.w(BoardConstants.TAG, "Permission denied for device " + mDevice);
                    }
                }
            }
            }
            catch (Exception ex) {
                Log.w(BoardConstants.TAG, "Exception on BroadcastReceiver [ " + ex + " ]");
            }
        }
    };

    void connect() throws TransmisionErrorException {
        Log.i(BoardConstants.TAG, "Connected to [ " + device.getDeviceName() + " ]. mode - Host");
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

        Log.i(BoardConstants.TAG, "Device openned");

            /* Claim the required interface to gain access to it */
        if(connection.claimInterface(intf, true) == true) {
            /* Get the OUT endpoint.  It is the second endpoint in the interface */
            endpointOUT = intf.getEndpoint(1);
            /* Get the IN endpoint.  It is the first endpoint in the interface */
            endpointIN = intf.getEndpoint(0);
            Log.i(BoardConstants.TAG, "Connection established");
        } else {
                /* if the interface claim failed, we should close the
                 * connection and exit.
                 */
            connection.close();
            Log.i(BoardConstants.TAG, "Interface couldn't be claimed");
            throw new TransmisionErrorException("Interface couldn't be claimed");
        }
    }

}
