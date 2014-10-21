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


public class SensorListener implements NodeMain {

    private String robotName;
    private NodeMainExecutor nodeMainExecutor;

    private ConnectedNode cn;
    private Subscriber<udc_robot_control_msgs.ActionCommand> subscriber;

    private MessageListener cml;

    public static void main(String args[]) {
        if (args.length < 1){
            System.err.println("java SensorListener <robotName>");
            System.exit(1);
        }


        try {
            NodeMainExecutor ex = DefaultNodeMainExecutor.newDefault();
            new SensorListener(args[0], ex);
            //Thread.sleep(9999999);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public SensorListener(String robotName, NodeMainExecutor nodeMainExecutor) {
        super();
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
        return GraphName.of("SensorListener");
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        cn = connectedNode;
        cml = new CommandMessageListener(nodeMainExecutor);

        String topicName = "/" + robotName + "/" + Constants.TOPIC_IR_SENSORS;
        subscriber = connectedNode.newSubscriber(topicName, udc_robot_control_msgs.SensorStatus._TYPE);
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
        System.out.println("Error on Sensor Listener [ " + node.getName() + " ] [ " + throwable.getMessage() + " ]");
    }


    private class CommandMessageListener implements MessageListener<udc_robot_control_msgs.SensorStatus> {

        private NodeMainExecutor nodeMainExecutor;

        public CommandMessageListener(NodeMainExecutor nodeMainExecutor) {
            this.nodeMainExecutor = nodeMainExecutor;
        }


        @Override
        public void onNewMessage(udc_robot_control_msgs.SensorStatus actionCommand) {

            System.out.println("IR0 = " + actionCommand.getSIr0());
            System.out.println("IR1 = " + actionCommand.getSIr1());
            System.out.println("IR2 = " + actionCommand.getSIr2());
            System.out.println("IR3 = " + actionCommand.getSIr3());
            System.out.println("IR4 = " + actionCommand.getSIr4());
            System.out.println("IR5 = " + actionCommand.getSIr5());
            System.out.println("IR6 = " + actionCommand.getSIr6());
            System.out.println("IR7 = " + actionCommand.getSIr7());
            System.out.println("IR8 = " + actionCommand.getSIr8());
            // System.out.println("IR9 = " + actionCommand.getSIr9());
            System.out.println("");
        }
    }
}
