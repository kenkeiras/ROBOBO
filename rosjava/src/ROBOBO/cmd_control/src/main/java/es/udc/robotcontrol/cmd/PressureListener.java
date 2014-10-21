package es.udc.robotcontrol.cmd;

import es.udc.robotcontrol.utils.Constants;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.address.InetAddressFactory;

import sensor_msgs.*;

import udc_robot_control_msgs.*;


public class PressureListener implements NodeMain {

    private String robotName;
    private NodeMainExecutor nodeMainExecutor;

    private ConnectedNode cn;
    private Subscriber<sensor_msgs.FluidPressure> subscriber;

    private CommandMessageListener cml;

    public static void main(String args[]) {
        if (args.length < 1){
            System.err.println("java PressureListener <robotName>");
            System.exit(1);
        }


        try {
            NodeMainExecutor ex = DefaultNodeMainExecutor.newDefault();
            new PressureListener(args[0], ex);
            //Thread.sleep(9999999);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public PressureListener(String robotName, NodeMainExecutor nodeMainExecutor) {
        super();
        System.out.println("Creando Pressure Listener");
        this.robotName = robotName;
        this.nodeMainExecutor = nodeMainExecutor;


        String host = InetAddressFactory.newNonLoopback().getHostAddress();
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host);

        URI masterUri = null;
        try {
            String master = System.getenv("ROS_MASTER_URI");
            System.out.println("ROS_MASTER_URI: " + master);
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
        return GraphName.of("PressureListener");
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        cn = connectedNode;
        cml = new CommandMessageListener(nodeMainExecutor);

        String topicName = "/" + robotName + "/" + Constants.TOPIC_PRESSURE;
        subscriber = connectedNode.newSubscriber(topicName, sensor_msgs.FluidPressure._TYPE);
        subscriber.addMessageListener(cml);
    }


    @Override
    public void onShutdown(Node node) {
        if (subscriber != null) {
            subscriber.shutdown();
            subscriber = null;
        }
    }


    @Override
    public void onShutdownComplete(Node node) {}


    @Override
    public void onError(Node node, Throwable throwable) {
        System.out.println("Error on Pressure Listener [ " + node.getName() + " ] [ " + throwable.getMessage() + " ]");
    }


    private class CommandMessageListener implements MessageListener<sensor_msgs.FluidPressure> {

        private NodeMainExecutor nodeMainExecutor;

        public CommandMessageListener(NodeMainExecutor nodeMainExecutor) {
            this.nodeMainExecutor = nodeMainExecutor;
        }


        @Override
        public void onNewMessage(sensor_msgs.FluidPressure actionCommand) {
            System.out.println("Pressure: " + actionCommand.getFluidPressure());
        }
    }
}
