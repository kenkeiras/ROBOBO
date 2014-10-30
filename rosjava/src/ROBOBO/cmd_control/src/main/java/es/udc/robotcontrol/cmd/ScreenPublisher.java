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


public class ScreenPublisher implements NodeMain {

    private String robotName;
    private NodeMainExecutor nodeMainExecutor;
    private static String[] args;

    private ConnectedNode cn;
    private Publisher<std_msgs.String> publisher;


    public static void main(String args[]) {
        if (args.length < 2){
            System.out.println("java ScreenPublisher <robotName> <message>");
            return;
        }
        ScreenPublisher.args = args;
        try {


            NodeMainExecutor ex = DefaultNodeMainExecutor.newDefault();
            new ScreenPublisher(args[0], ex);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public ScreenPublisher(String robotName, NodeMainExecutor nodeMainExecutor) {
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
        return GraphName.of("ScreenPublisher");
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        cn = connectedNode;
        publisher = connectedNode.newPublisher("/" + robotName + "/" + Constants.TOPIC_SCREEN,
                                               std_msgs.String._TYPE);

        try {
            Thread.sleep(1000);
            std_msgs.String msg = publisher.newMessage();

            msg.setData(ScreenPublisher.args[1]);

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
        System.out.println("Error on Screen Publisher [ " + node.getName() + " ] [ " + throwable.getMessage() + " ]");
    }
}
