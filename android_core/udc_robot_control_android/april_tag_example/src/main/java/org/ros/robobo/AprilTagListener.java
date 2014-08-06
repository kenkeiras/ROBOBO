package org.ros.robobo;

import es.udc.robotcontrol.utils.Constants;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.ros.address.InetAddressFactory;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;
import org.ros.node.topic.Publisher;


public class AprilTagListener implements NodeMain {

    private String robotName;
    private NodeMainExecutor nodeMainExecutor;

    private ConnectedNode cn;
    private Subscriber<udc_robot_control_msgs.AprilTag> subscriber;

    private AprilTagMessageListener ml;

    public AprilTagListener(String robotName, String master,
                            NodeMainExecutor nodeMainExecutor)
        throws URISyntaxException{

        super();
        this.robotName = robotName;
        this.nodeMainExecutor = nodeMainExecutor;

        String host = InetAddressFactory.newNonLoopback().getHostAddress();
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host);

        URI masterUri = new URI(master);
        nodeConfiguration.setMasterUri(masterUri);
        nodeMainExecutor.execute(this, nodeConfiguration);
    }

    public GraphName getDefaultNodeName() {
        return GraphName.of("AprilTagExample");
    }

    public void onStart(ConnectedNode connectedNode) {
        cn = connectedNode;

        Publisher<std_msgs.String> publisher = connectedNode.newPublisher(
            robotName + "/" + Constants.TOPIC_SCREEN, std_msgs.String._TYPE);

        ml = new AprilTagMessageListener(nodeMainExecutor,
                                         publisher);

        String topicName = robotName + "/" + Constants.TOPIC_APRIL_TAGS;
        subscriber = connectedNode.newSubscriber(topicName, udc_robot_control_msgs.AprilTag._TYPE);
        subscriber.addMessageListener(ml);
    }


    public void onShutdown(Node node) {}

    public void onShutdownComplete(Node node) {}

    public void onError(Node node, Throwable throwable) {
        System.out.println("Error on AprilTagExample [ "
                           + node.getName() + " ] [ "
                           + throwable.getMessage() + " ]");
    }


    private class AprilTagMessageListener implements MessageListener<udc_robot_control_msgs.AprilTag> {
        private NodeMainExecutor nodeMainExecutor;
        private Publisher<std_msgs.String> publisher;

        public AprilTagMessageListener(NodeMainExecutor nodeMainExecutor, Publisher<std_msgs.String> publisher) {
            System.out.println("Listening...");
            this.nodeMainExecutor = nodeMainExecutor;
            this.publisher = publisher;
        }


        public void onNewMessage(udc_robot_control_msgs.AprilTag tag) {

            System.out.println("Code: " + tag.getCode() + "\n"
                               + "ID: " + tag.getId() + "\n"
                               + "Hamming: " + tag.getHammingDistance() + "\n"
                               + "Rotation: " + tag.getRotation() + "\n"
                               + "Perimeter: " + tag.getObservedPerimeter() + "\n\n");


            std_msgs.String msg = publisher.newMessage();
            msg.setData("<html><head></head><body><center><font size=\"12\">"
                        + tag.getId()
                        + "</font><center></body></html>");

            publisher.publish(msg);
            System.out.println("Published!");
        }
    }
}
