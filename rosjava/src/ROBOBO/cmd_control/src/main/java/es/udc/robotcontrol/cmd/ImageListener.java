package es.udc.robotcontrol.cmd;

import es.udc.robotcontrol.utils.Constants;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
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


public class ImageListener implements NodeMain {

    private String robotName;
    private NodeMainExecutor nodeMainExecutor;

    private ConnectedNode cn;
    private Subscriber<sensor_msgs.CompressedImage> subscriber;

    private CommandMessageListener cml;
    static String fileDest;

    public static void main(String args[]) {
        if (args.length < 2){
            System.out.println("java ImageListener <robotName> <output>");
            System.exit(1);
        }
        fileDest = args[1];

        try {

            NodeMainExecutor ex = DefaultNodeMainExecutor.newDefault();
            new ImageListener(args[0], ex);
            //Thread.sleep(9999999);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public ImageListener(String robotName, NodeMainExecutor nodeMainExecutor) {
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
        return GraphName.of("ImageListener");
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        cn = connectedNode;
        cml = new CommandMessageListener(nodeMainExecutor);

        String topicName = "/" + robotName + "/" + Constants.TOPIC_IMAGE + "/compressed";
        subscriber = connectedNode.newSubscriber(topicName, sensor_msgs.CompressedImage._TYPE);
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
        System.out.println("Error on Image Listener [ " + node.getName() + " ] [ " + throwable.getMessage() + " ]");
    }


    private class CommandMessageListener implements MessageListener<sensor_msgs.CompressedImage> {

        private NodeMainExecutor nodeMainExecutor;

        public CommandMessageListener(NodeMainExecutor nodeMainExecutor) {
            this.nodeMainExecutor = nodeMainExecutor;
        }


        @Override
        public void onNewMessage(sensor_msgs.CompressedImage actionCommand) {

            byte[] data = actionCommand.getData().array();

            System.out.println("Reading...");

            BufferedOutputStream bos = null;
            try {
                FileOutputStream fos = new FileOutputStream(new File(fileDest));

                bos = new BufferedOutputStream(fos);
                bos.write(data);
                bos.close();
                fos.close();

                System.out.println("Done");
                System.exit(0);
            }
            catch(Exception e){
                e.printStackTrace();
            }

            System.exit(1);
            nodeMainExecutor.shutdown();
        }
    }
}
