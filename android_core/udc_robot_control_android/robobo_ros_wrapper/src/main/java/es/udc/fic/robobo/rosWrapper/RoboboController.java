package es.udc.fic.robobo.rosWrapper;

import org.ros.address.InetAddressFactory;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;

import java.lang.String;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import es.udc.fic.robobo.rosWrapper.managers.listeners.AprilTagListenerManager;
import es.udc.robotcontrol.utils.Constants;
import udc_robot_control_msgs.AprilTag;

public class RoboboController implements NodeMain {

    private final String robotName;
    private final URI masterURI;
    private final NodeMainExecutor executor;

    private ConnectedNode connectedNode = null;

    // Listeners
    final AprilTagListenerManager aprilTagListenerManager;


    public RoboboController(URI masterURI, String robotName) throws ControllerNotFound {
        executor = DefaultNodeMainExecutor.newDefault();
        this.masterURI = masterURI;
        this.robotName = robotName;

        // Initialize listener managers
        aprilTagListenerManager = new AprilTagListenerManager(robotName);


        // Start this node
        String host = InetAddressFactory.newNonLoopback().getHostAddress();
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host);

        nodeConfiguration.setMasterUri(masterURI);
        executor.execute(this, nodeConfiguration);
    }


    // Listeners
    /**
     * Add an AprilTag handler to manage the incoming changes.
     *
     */
    public synchronized void addAprilTagHandler(MessageListener<AprilTag> aprilTagHandler) {
        aprilTagListenerManager.addHandler(aprilTagHandler);
    }


    /**
     * Remove a single AprilTag handler.
     *
     */
    public synchronized void removeAprilTagHandler(MessageListener<AprilTag> aprilTagHandler){
        aprilTagListenerManager.removeHandler(aprilTagHandler);
    }


    // Publishers
    // HTML screen
    private final Set<String> queuedMessages = new HashSet<String>();

    /**
     *
     *
     */
    public void publishInfoMessage(String htmlData){
        queueAndPublish(htmlData);
    }


    private synchronized void queueAndPublish(String htmlData){

        if (htmlData != null) {
            queuedMessages.add(htmlData);
        }
        if (connectedNode == null){
            return;
        }

        Publisher<std_msgs.String> publisher = connectedNode.newPublisher(
                "/" + robotName + "/" + Constants.TOPIC_SCREEN, std_msgs.String._TYPE);

        for (String message : queuedMessages){
            std_msgs.String msg = publisher.newMessage();
            System.out.println("Sent! " + message);
            msg.setData(message);
            publisher.publish(msg);
        }

        queuedMessages.clear();
    }


    public void stop(){
        executor.shutdownNodeMain(this);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("ROBOBO_controller_" + UUID.randomUUID().toString().replace("-", "_"));
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        this.connectedNode = connectedNode;
        aprilTagListenerManager.setConnectedNode(connectedNode);

        // Clear the message queue
        queueAndPublish(null);
    }

    @Override
    public void onShutdown(Node node) {}

    @Override
    public void onShutdownComplete(Node node) {}

    @Override
    public void onError(Node node, Throwable throwable) {}
}
