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

import udc_robot_control_msgs.ActionCommand;


public class SpeechRecognitionListener implements NodeMain {

    private String robotName;
    private NodeMainExecutor nodeMainExecutor;

    private ConnectedNode cn;
    private Subscriber<std_msgs.String> subscriber;

    private SpeechRecognitionMessageListener ml;

    public SpeechRecognitionListener(String robotName, String master,
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
        return GraphName.of("SpeechRecognitionExample");
    }

    public void onStart(ConnectedNode connectedNode) {
        cn = connectedNode;

        String commandTopicName = robotName + "/" + Constants.TOPIC_COMMANDS;
        Publisher<ActionCommand> commander = connectedNode.newPublisher(
            commandTopicName, ActionCommand._TYPE);


        Publisher<std_msgs.String> publisher = connectedNode.newPublisher(
            robotName + "/" + Constants.TOPIC_SCREEN, std_msgs.String._TYPE);

        ml = new SpeechRecognitionMessageListener(nodeMainExecutor,
                                                  publisher);

        String topicName = robotName + "/" + Constants.TOPIC_SPEECH_RECOGNITION;
        subscriber = connectedNode.newSubscriber(topicName, std_msgs.String._TYPE);
        subscriber.addMessageListener(ml);
    }


    public void onShutdown(Node node) {}

    public void onShutdownComplete(Node node) {}

    public void onError(Node node, Throwable throwable) {
        System.out.println("Error on SpeechRecognitionExample [ "
                           + node.getName() + " ] [ "
                           + throwable.getMessage() + " ]");
    }


    private class SpeechRecognitionMessageListener implements MessageListener<std_msgs.String> {
        private NodeMainExecutor nodeMainExecutor;
        private Publisher<std_msgs.String> publisher;

        public SpeechRecognitionMessageListener(NodeMainExecutor nodeMainExecutor, Publisher<std_msgs.String> publisher) {
            System.out.println("Listening...");
            this.nodeMainExecutor = nodeMainExecutor;
            this.publisher = publisher;
        }


        public void onNewMessage(std_msgs.String message) {

            System.out.println("Speech: " + message.getData() + "\n\n");


            std_msgs.String msg = publisher.newMessage();
            msg.setData("<html><head></head><body><center><font size=\"12\">"
                        + message.getData()
                        + "</font><center></body></html>");

            publisher.publish(msg);
            System.out.println("Published!");
        }
    }
}
