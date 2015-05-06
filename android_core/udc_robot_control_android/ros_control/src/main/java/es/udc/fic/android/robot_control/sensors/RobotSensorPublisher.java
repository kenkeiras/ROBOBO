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
import android.content.Intent;
import android.util.Log;

import es.udc.fic.android.board.BoardConstants;
import es.udc.fic.android.board.RobotState;
import es.udc.fic.android.board.SensorInfo;
import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constants;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import udc_robot_control_msgs.SensorStatus;

/**
 * Created by kerry on 4/08/13.
 */
public class RobotSensorPublisher implements NodeMain {
    public static String TOPIC_NAME = Constants.TOPIC_IR_SENSORS;
    public static final String IR_SENSORS_UPDATE_KEY = "IR_SENSORS_UPDATE";


    private Context context;
    private String robotName;

    private Publisher<SensorStatus> publisher;


    public RobotSensorPublisher(Context context, String robotName) {
        super();
        Log.d(C.TAG, "Creating IrSensorPublisher");
        this.context = context;
        this.robotName = robotName;
    }


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(C.DefaultBaseNodeName + "/" + TOPIC_NAME);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        String tn = robotName + "/" + TOPIC_NAME;
        publisher = connectedNode.newPublisher(tn, SensorStatus._TYPE);
    }

    @Override
    public void onShutdown(Node node) {
        Log.i(C.TAG, "[ "  + node.getName() + " ] onShutdown [ " + TOPIC_NAME + " ]");
        publisher.shutdown();
    }

    @Override
    public void onShutdownComplete(Node node) {
        Log.i(C.TAG, "[ "  + node.getName() + " ] onShutdownComplete [ " + TOPIC_NAME + " ]");
    }

    @Override
    public void onError(Node node, Throwable throwable) {
        Log.w(C.TAG, "[ "  + node.getName() + " ] onError [ " + TOPIC_NAME + " ]", throwable);
    }

    public void sendInfo(SensorInfo inf) {
        broadcastInfo(inf);
        SensorStatus ss = publisher.newMessage();
        ss.getHeader().setFrameId(robotName);
        ss.getHeader().setStamp(Time.fromMillis(System.currentTimeMillis()));
        ss.setSIr1(inf.getsIr1());
        ss.setSIr2(inf.getsIr2());
        ss.setSIr3(inf.getsIr3());
        ss.setSIr4(inf.getsIr4());
        ss.setSIr5(inf.getsIr5());
        ss.setSIr6(inf.getsIr6());
        ss.setSIr7(inf.getsIr7());
        ss.setSIr8(inf.getsIr8());
        ss.setSIr9(inf.getsIr9());
        ss.setSIrS1(inf.getsIrS1());
        ss.setSIrS2(inf.getsIrS2());
        publisher.publish(ss);
    }

    private void broadcastInfo(SensorInfo inf) {
        int[] sensors = new int[]{inf.getsIr1(), inf.getsIr2(), inf.getsIr3(),
                                  inf.getsIr4(), inf.getsIr5(), inf.getsIr6(),
                                  inf.getsIr7(), inf.getsIr8(), inf.getsIr9()
        };
        Intent i = new Intent(BoardConstants.UPDATE_BOARD_STATE);
        i.putExtra(IR_SENSORS_UPDATE_KEY, sensors);
        context.sendBroadcast(i);
    }
}
