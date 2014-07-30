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

package es.udc.fic.android.robot_control.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constants;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;


public  class BatteryStatus implements NodeMain {

    private static String QUEUE_NAME = Constants.TOPIC_BATTERY;
    private static int BATERY_PUBLISH_SLEEP = 30000;

    private Context context;
    private String robotName;

    private Publisher<udc_robot_control_msgs.BatteryStatus> publisher;

    public BatteryStatus(Context context, String robotName) {
        super();
        Log.d(C.TAG, "Creating Batery Status publisher");
        this.context = context;
        this.robotName = robotName;

    }


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(C.DefaultBaseNodeName + "/" + QUEUE_NAME);
    }

    /**
     * Called when the {@link Node} has started and successfully connected to the
     * master.
     *
     * @param connectedNode
     *          the {@link ConnectedNode} that has been started
     */
    @Override
    public void onStart(final ConnectedNode connectedNode) {
        Log.i(C.TAG, "Starting Batery Status Monitoring");
        String queueName = robotName + "/" + QUEUE_NAME;
        publisher = connectedNode.newPublisher(queueName, udc_robot_control_msgs.BatteryStatus._TYPE);

        Log.d(C.TAG, "Publisher for [ " + connectedNode.getName() + " ][ " + QUEUE_NAME + " ] created");

        // This CancellableLoop will be canceled automatically when the node shuts
        // down.
        connectedNode.executeCancellableLoop(new CancellableLoop() {


            @Override
            protected void setup() {

            }

            @Override
            protected void loop() throws InterruptedException {
                Log.d(C.TAG, "[ "  + connectedNode.getName() + " ] Entering loop for [ " + QUEUE_NAME + " ]");
                udc_robot_control_msgs.BatteryStatus msg = publisher.newMessage();

                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = context.registerReceiver(null, ifilter);
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                double currentBateryLevel = level / (double)scale;

                Log.d(C.TAG, "[ "  + connectedNode.getName() + " ] Entering loop for [ " + QUEUE_NAME + " ] [ " + currentBateryLevel + " ]");

                if (currentBateryLevel > 0.2) {
                    msg.setDescription("OK");
                    msg.setStatus(udc_robot_control_msgs.BatteryStatus.STATUS_OK);
                } else {
                    if (currentBateryLevel > 0.1) {
                        msg.setDescription("WARNING");
                        msg.setStatus(udc_robot_control_msgs.BatteryStatus.STATUS_WARNING);
                    } else {
                        msg.setDescription("CRITICAL");
                        msg.setStatus(udc_robot_control_msgs.BatteryStatus.STATUS_CRITICAL);
                    }
                }
                msg.setLevel(currentBateryLevel);
                msg.getHeader().setFrameId(robotName);
                msg.getHeader().setStamp(Time.fromMillis(System.currentTimeMillis()));
                publisher.publish(msg);
                Log.d(C.TAG, "[ "  + connectedNode.getName() + " ] Message published [ " + QUEUE_NAME + " ] [ " + currentBateryLevel + " ]");
                Thread.sleep(BATERY_PUBLISH_SLEEP);
            }

            @Override
            protected void handleInterruptedException(InterruptedException e) {
                super.handleInterruptedException(e);
                Log.d(C.TAG, "Stopping Batery Status");
            }
        });
    }




    /**
     * Called when the {@link ConnectedNode} has started shutting down. Shutdown
     * will be delayed, although not indefinitely, until all {@link NodeListener}s
     * have returned from this method.
     * <p>
     * Since this method can potentially delay {@link ConnectedNode} shutdown, it
     * is preferred to use {@link #onShutdownComplete(Node)} when
     * {@link ConnectedNode} resources are not required during the method call.
     *
     * @param node
     *          the {@link Node} that has started shutting down
     */
    @Override
    public void onShutdown(Node node) {
        Log.i(C.TAG, "[ "  + node.getName() + " ] onShutdown [ " + QUEUE_NAME + " ]");
        publisher.shutdown();
    }

    /**
     * Called when the {@link Node} has shut down.
     *
     * @param node
     *          the {@link Node} that has shut down
     */
    @Override
    public void onShutdownComplete(Node node) {
        Log.i(C.TAG, "[ "  + node.getName() + " ] onShutdownComplete [ " + QUEUE_NAME + " ]");
    }

    /**
     * Called when the {@link Node} experiences an unrecoverable error.
     *
     * @param node
     *          the {@link Node} that experienced the error
     * @param throwable
     *          the {@link Throwable} describing the error condition
     */
    @Override
    public void onError(Node node, Throwable throwable) {
        Log.w(C.TAG, "[ "  + node.getName() + " ] onError [ " + QUEUE_NAME + " ]", throwable);
    }

}
