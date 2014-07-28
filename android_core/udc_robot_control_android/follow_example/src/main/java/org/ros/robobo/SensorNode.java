package org.ros.robobo;

import es.udc.robotcontrol.RosListener;
import es.udc.robotcontrol.utils.Constantes;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;
import org.ros.address.InetAddressFactory;

import sensor_msgs.*;
import udc_robot_control_msgs.ActionCommand;
import udc_robot_control_msgs.SensorStatus;



public class SensorNode implements NodeMain {

    private String robotName;
    private NodeMainExecutor nodeMainExecutor;

    private ConnectedNode cn;
    private Subscriber<SensorStatus> subscriber;

    private MessageListener cml;

    public static void main(String args[]) {
        try {
            NodeMainExecutor ex = DefaultNodeMainExecutor.newDefault();
            new SensorNode("robot1", args[1], ex, new SensorListener(ex));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public SensorNode(String robotName, String masterPath,
                      NodeMainExecutor nodeMainExecutor,
                      MessageListener listener) {
        super();
        System.out.println("Creando Status Listener");
        this.robotName = robotName;
        this.nodeMainExecutor = nodeMainExecutor;
        cml = listener;

        String host = InetAddressFactory.newNonLoopback().getHostAddress();
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host);

        URI masterUri = null;
        try {
            masterUri = new URI(masterPath);
            nodeConfiguration.setMasterUri(masterUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        nodeMainExecutor.execute(this, nodeConfiguration);
    }


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("SensorNode");
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        cn = connectedNode;

        String topicName = robotName + "/" + Constantes.TOPIC_IR_SENSORS;
        subscriber = connectedNode.newSubscriber(topicName, SensorStatus._TYPE);
        subscriber.addMessageListener(cml);
    }


    public void onShutdown(Node node) {
    }


    public void onShutdownComplete(Node node) {
    }


    public void shutdown(){
        if (subscriber != null) {
            subscriber.shutdown();
            subscriber = null;
        }
    }


    @Override
    public void onError(Node node, Throwable throwable) {
        System.err.println("Error on Sensor Listener [ " + node.getName() + " ] [ " + throwable.getMessage() + " ]");
    }
}
