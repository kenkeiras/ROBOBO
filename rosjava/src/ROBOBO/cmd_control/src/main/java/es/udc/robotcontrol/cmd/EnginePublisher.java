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
import org.ros.node.topic.Publisher;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.address.InetAddressFactory;

import sensor_msgs.*;

import udc_robot_control_msgs.*;


public class EnginePublisher implements NodeMain {

    private String robotName;
    private NodeMainExecutor nodeMainExecutor;
    private static String[] args;

    private ConnectedNode cn;
    private Publisher<geometry_msgs.Twist> publisher;


    public static void main(String args[]) {
        if (args.length < 3){
            System.out.println("java EnginePublisher <robotName> <speed> <turn>");
            return;
        }
        EnginePublisher.args = args;
        try {


            NodeMainExecutor ex = DefaultNodeMainExecutor.newDefault();
            new EnginePublisher(args[0], ex);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public EnginePublisher(String robotName, NodeMainExecutor nodeMainExecutor) {
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
        return GraphName.of("EnginePublisher");
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        cn = connectedNode;
        publisher = connectedNode.newPublisher("/" + robotName + "/" + Constants.TOPIC_ENGINES,
                                               geometry_msgs.Twist._TYPE);

        try {
            Thread.sleep(1000);
            geometry_msgs.Twist msg = publisher.newMessage();

            float speed = Float.parseFloat(args[1]);
            System.out.println("Speed: " + speed);
            msg.getLinear().setX(speed);

            float angle = Float.parseFloat(args[2]);
            System.out.println("Angle: " + angle);
            msg.getAngular().setY(angle);

            publisher.publish(msg);

            System.exit(0);

        }
        catch (Exception e){
            e.printStackTrace();
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
    public void onShutdownComplete(Node node) {}


    @Override
    public void onError(Node node, Throwable throwable) {
        System.out.println("Error on EnginePublisher [ " + node.getName() + " ] [ " + throwable.getMessage() + " ]");
    }
}
