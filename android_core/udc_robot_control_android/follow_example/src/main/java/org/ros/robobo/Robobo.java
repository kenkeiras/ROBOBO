package org.ros.robobo;

import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeMainExecutor;

import geometry_msgs.Twist;


import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;
import org.ros.node.DefaultNodeMainExecutor;

import org.ros.RosCore;
import org.ros.address.InetAddressFactory;
import org.ros.internal.message.Message;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;
import es.udc.robotcontrol.utils.Constantes;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.ros.message.Time;

public class Robobo implements NodeMain {
    private Publisher<Twist> publisher;
    private String robotName;
    private NodeMainExecutor nodeMainExecutor;
    private static float speed = 0;
    private static float turn = 0;


    public static void main(String args[]){
        NodeMainExecutor ex = DefaultNodeMainExecutor.newDefault();

        final String master = args[1];
        new Thread() {
            public void run(){
                NodeMainExecutor ex = DefaultNodeMainExecutor.newDefault();
                new Robobo("robot1", master, ex);
            }
        }.start();

        System.out.println("Connecting to master [URI: " + master + " ]");

        new SensorNode("robot1", master, ex, new SensorListener(ex));

       System.out.println("Exiting [ " + args[0] + " ]");
    }


    public Robobo(String robotName, String master, NodeMainExecutor nodeMainExecutor) {
        super();
        System.out.println("Creando Command Publisher");
        this.robotName = robotName;
        this.nodeMainExecutor = nodeMainExecutor;


        String host = InetAddressFactory.newNonLoopback().getHostAddress();
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host);

        URI masterUri = null;
        try {
            masterUri = new URI(master);
            nodeConfiguration.setMasterUri(masterUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        nodeMainExecutor.execute(this, nodeConfiguration);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("RED");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        System.out.println("Starting...");
        try {

            while (true){
                String topicName = robotName + "/" + Constantes.TOPIC_ENGINES;
                publisher = connectedNode.newPublisher(topicName, Twist._TYPE);

                Twist msg = publisher.newMessage();
                msg.getLinear().setX(speed);
                msg.getAngular().setY(turn);

                publisher.publish(msg);
                System.out.println("Published! " + speed + " * " + turn);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void setEngines(boolean leftEngine, boolean rightEngine){
        if (leftEngine || rightEngine) {
            speed = 1.0f;
        }
        else {
            speed = 0.0f;
        }

        if (leftEngine && (!rightEngine)) {
            turn = 1.0f;
        }
        else if (rightEngine && (!leftEngine)) {
            turn = -1.0f;
        }
        else {
            turn = 0.0f;
        }
    }


    @Override
    public void onShutdown(Node node) {
        if (publisher != null) {
            publisher.shutdown();
            publisher = null;
        }
    }

    @Override
    public void onShutdownComplete(Node node) {

    }

    @Override
    public void onError(Node node, Throwable throwable) {
        System.out.println("Error on Command Listener [ " + node.getName() + " ] [ " + throwable.getMessage() + " ]");
    }
}
