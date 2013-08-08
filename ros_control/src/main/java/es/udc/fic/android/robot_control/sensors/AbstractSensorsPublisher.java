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

package es.udc.fic.android.robot_control.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Looper;
import android.util.Log;
import es.udc.fic.android.robot_control.utils.C;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import java.util.List;

/**
 * Abstract sensors publisher.
 *
 * Configure common environment for all sensors publisher.
 *
 */
public abstract class AbstractSensorsPublisher implements NodeMain {

    // Robot name used in compose topic names
    protected String robotName;
    protected Context context;

    // Sensor manager
    protected SensorManager sensorManager;
    // Sensor delay. it seems to be hardware dependant
    protected int sensorDelay;

    // Thread that will listen for events
    protected ListenerThread listenerThread;
    //
    protected AbstractSensorEventListener sensorEventListener;
    protected Publisher publisher;

    public AbstractSensorsPublisher(Context ctx, String robotName) {
        super();

        this.robotName = robotName;
        this.context = ctx;
        this.sensorManager = (SensorManager)ctx.getSystemService(Context.SENSOR_SERVICE);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        sensorDelay = 20000; // 20,000 us == 50 Hz for Android 3.1 and above
        if (currentapiVersion <= android.os.Build.VERSION_CODES.HONEYCOMB) {
            sensorDelay = SensorManager.SENSOR_DELAY_UI; // 16.7Hz for older devices.  They only support enum values, not the microsecond version.
        }
    }

    /**
     * Returns the type of sensor that we use
     * @return
     */
    protected abstract int getSensorType();

    /**
     * Indicates if there is hardware available for this sensor
     * @return
     */
    protected boolean isHardwarePresent() {
        List<Sensor> mfList = this.sensorManager.getSensorList(getSensorType());
        return (mfList.size() > 0);
    }

    /**
     * Create ONE publisher for the concrete sensor
     * @param n
     * @return
     */
    protected abstract Publisher createPublisher(ConnectedNode n);

    /**
     * Create a collection of SensorEventListners to publish data on the input publisher
     * @param P
     * @return
     */
    protected abstract AbstractSensorEventListener createListener(Publisher P);

    /**
     * Topic name used for the sensor
     * @return
     */
    protected abstract String getTopicName();

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(C.DefaultBaseNodeName + "/" + getTopicName());
    }

    /**
     * Template method for onStart. Subclass must implement the helpers with concrete contents.
     * @param connectedNode
     */
    @Override
    public void onStart(ConnectedNode connectedNode) {
        try {
            if (isHardwarePresent()) {
                Log.w(C.TAG, "Hardware present for [ " + connectedNode.getName() + " ] topic [ " + getTopicName() + " ] will be created");
                this.publisher = createPublisher(connectedNode);
                this.sensorEventListener = createListener(this.publisher);
                this.listenerThread = new ListenerThread(this.sensorManager, this.sensorEventListener);
                Log.w(C.TAG, "Starting listener thread for [ " + connectedNode.getName() + " ] topic [ " + getTopicName() + " ]");
                this.listenerThread.start();
            }
            else {
                Log.w(C.TAG, "No hardware present for [ " + connectedNode.getName() + " ] No topic [ " + getTopicName() + " ] will be created");
            }
        }
        catch (Exception e) {
            Log.w(C.TAG, "Exception onStart [ " + e.getMessage() + " ] Node [ " + connectedNode + " ]", e);
            if (connectedNode != null) {
                Log.w(C.TAG, "Exception onStart [ " + e.getMessage() + " ] Node [ " + connectedNode.getName() + " ]", e);
                connectedNode .getLog().fatal(e);
            }
            else {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onShutdown(Node node) {
        if(this.listenerThread != null) {
            this.listenerThread.shutdown();

            try {
                this.listenerThread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onShutdownComplete(Node node) {
        Log.i(C.TAG, "SensorsPublisher Shutdown Complete [ " + node.getName() + " ]");
    }

    @Override
    public void onError(Node node, Throwable throwable) {
        Log.w(C.TAG, "Unhandled error [ " + throwable.getMessage() + " ] on [ " + node.getName() + " ]", throwable);
    }


    /**
     * This class manages the listeners life-cicly
     */
    private class ListenerThread extends Thread {
        private final SensorManager sensorManager;
        private AbstractSensorEventListener listener;
        private Looper threadLooper;

        /**
         * Constructor. Receives a sensorManager and a collection o AbstractSensorEventListeners
         *
         * @param sensorManager
         * @param listener
         */
        private ListenerThread(SensorManager sensorManager, AbstractSensorEventListener listener) {
            this.sensorManager = sensorManager;
            this.listener = listener;
        }



        /**
         * Prepare a Looper and register all listeners in the sensor manager
         */
        public void run()
        {
            Looper.prepare();
            this.threadLooper = Looper.myLooper();
            listener.registerSelf(this.sensorManager, sensorDelay);
            Looper.loop();
        }


        /**
         * Unregister listeners and terminate looper.
         */
        public void shutdown() {

            this.listener.unregisterSelf(this.sensorManager);
            if(this.threadLooper != null) {
                this.threadLooper.quit();
            }
        }
    }


}
