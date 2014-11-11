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

package es.udc.fic.android.robot_control.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constants;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;


public class AudioPublisher implements NodeMain {

    private static String QUEUE_NAME = Constants.TOPIC_AUDIO;
    private static int PUBLISH_SLEEP = 1000;

    private int bsib;

    private Context context;
    private String robotName;

    private Publisher<audio_common_msgs.AudioData> publisher;
    private AudioRecord ar;

    public AudioPublisher(Context context, String robotName) {
        super();
        Log.d(C.TAG, "Creating Audio Publisher");
        this.context = context;
        this.robotName = robotName;
        this.bsib = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        Log.i(C.TAG, "Initializing with a [ " + bsib + " ] bytes buffer");
        this.ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bsib);
    }


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(C.DefaultBaseNodeName + "/" + QUEUE_NAME);
    }

    /**
     * Called when the {@link org.ros.node.Node} has started and successfully connected to the
     * master.
     *
     * @param connectedNode
     *          the {@link org.ros.node.ConnectedNode} that has been started
     */
    @Override
    public void onStart(final ConnectedNode connectedNode) {
        Log.i(C.TAG, "Starting Audio Publisher");
        String queueName = robotName + "/" + QUEUE_NAME;
        publisher = connectedNode.newPublisher(queueName, audio_common_msgs.AudioData._TYPE);

        Log.d(C.TAG, "Publisher for [ " + connectedNode.getName() + " ][ " + QUEUE_NAME + " ] created");
        ar.startRecording();

        // This CancellableLoop will be canceled automatically when the node shuts
        // down.
        connectedNode.executeCancellableLoop(new CancellableLoop() {


            @Override
            protected void setup() {

            }

            @Override
            protected void loop() throws InterruptedException {
                Log.d(C.TAG, "[ "  + connectedNode.getName() + " ] Entering loop for [ " + QUEUE_NAME + " ]");
                // Read Data
                byte[] data = new byte[bsib];
                int read = ar.read(data, 0, bsib);
                Log.d(C.TAG, "Read [ " + read + " ] bytes from the micro");
                if (read > 0) {
                    if (read < bsib) {
                        byte[] newData = new byte[read];
                        System.arraycopy(data, 0, newData, 0, read);
                        data = newData;
                    }
                    Log.d(C.TAG, "Received [ " + data.length + " ] bytes");
                    int pos = 0;
                    while (pos < read) {
                        audio_common_msgs.AudioData msg = publisher.newMessage();
                        int max = msg.getData().writableBytes();
                        int ini = pos;
                        int end = pos + max;
                        end = read <  end ? read:end;
                        max = end - ini;
                        byte[] dataMax = new byte[max];
                        System.arraycopy(data, ini, dataMax, 0, max);

                        msg.getData().writeBytes(dataMax);

                        publisher.publish(msg);
                        Log.d(C.TAG, "[ " + connectedNode.getName() + " ] Message published [ " + QUEUE_NAME + " ] [ " + pos + " / " + read + " ]");
                        pos = end;
                    }
                }
                else {
                    Log.d(C.TAG, "No se ha leido nada del audio");
                }
                Thread.sleep(PUBLISH_SLEEP);
            }
        });
    }




    /**
     * Called when the {@link org.ros.node.ConnectedNode} has started shutting down. Shutdown
     * will be delayed, although not indefinitely, until all {@link NodeListener}s
     * have returned from this method.
     * <p>
     * Since this method can potentially delay {@link org.ros.node.ConnectedNode} shutdown, it
     * is preferred to use {@link #onShutdownComplete(org.ros.node.Node)} when
     * {@link org.ros.node.ConnectedNode} resources are not required during the method call.
     *
     * @param node
     *          the {@link org.ros.node.Node} that has started shutting down
     */
    @Override
    public void onShutdown(Node node) {

        Log.i(C.TAG, "[ "  + node.getName() + " ] onShutdown [ " + QUEUE_NAME + " ]");
        ar.stop();
    }

    /**
     * Called when the {@link org.ros.node.Node} has shut down.
     *
     * @param node
     *          the {@link org.ros.node.Node} that has shut down
     */
    @Override
    public void onShutdownComplete(Node node) {
        Log.i(C.TAG, "[ "  + node.getName() + " ] onShutdownComplete [ " + QUEUE_NAME + " ]");
    }

    /**
     * Called when the {@link org.ros.node.Node} experiences an unrecoverable error.
     *
     * @param node
     *          the {@link org.ros.node.Node} that experienced the error
     * @param throwable
     *          the {@link Throwable} describing the error condition
     */
    @Override
    public void onError(Node node, Throwable throwable) {
        Log.w(C.TAG, "[ "  + node.getName() + " ] onError [ " + QUEUE_NAME + " ]", throwable);
    }



}
