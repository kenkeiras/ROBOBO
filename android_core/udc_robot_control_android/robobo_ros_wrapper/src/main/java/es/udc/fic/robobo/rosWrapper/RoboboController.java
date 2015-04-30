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
import org.ros.node.topic.Subscriber;

import java.lang.String;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import es.udc.robotcontrol.utils.Constants;
import udc_robot_control_msgs.AprilTag;

public class RoboboController implements NodeMain {

    private final String robotName;
    private final URI masterURI;
    private final NodeMainExecutor executor;

    private ConnectedNode connectedNode = null;


    public RoboboController(URI masterURI, String robotName) throws ControllerNotFound {
        executor = DefaultNodeMainExecutor.newDefault();
        this.masterURI = masterURI;
        this.robotName = robotName;

        // Retrieve own address
        String host = InetAddressFactory.newNonLoopback().getHostAddress();
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host);

        nodeConfiguration.setMasterUri(masterURI);
        executor.execute(this, nodeConfiguration);
    }


    // Listeners
    // AprilTags
    private final Set<MessageListener<AprilTag>> aprilTagHandlers =
            new HashSet<MessageListener<AprilTag>>();

    private final MessageListener<AprilTag> messageListener =
            new ListenerMultiplexer<AprilTag>(aprilTagHandlers);

    private Subscriber<AprilTag> aprilTagSubscriber = null;

    /**
     * Initialize the april tag subscriber if it's possible, it isn't already working and there is
     * some handler in the set.
     *
     */
    private synchronized void initAprilTagSubscriberIfNeeded(){
        if ((aprilTagSubscriber == null)
                && (connectedNode != null)
                && (aprilTagHandlers.size() > 0)){

            String topicName = "/" + robotName + "/" + Constants.TOPIC_APRIL_TAGS;
            aprilTagSubscriber = connectedNode.newSubscriber(topicName,
                    udc_robot_control_msgs.AprilTag._TYPE);

            aprilTagSubscriber.addMessageListener(messageListener);
        }
    }

    /**
     * Add an AprilTag handler to manage the incoming changes.
     *
     * Spawn the AprilTagListenerNode node if it wasn't before.
     *
     */
    public synchronized void addAprilTagHandler(MessageListener<AprilTag> aprilTagHandler) {
        initAprilTagSubscriberIfNeeded();

        aprilTagHandlers.add(aprilTagHandler);
    }


    /**
     * Remove a single AprilTag handler.
     *
     * If the handler set gets empty, stop the listener node.
     *
     */
    public synchronized void removeAprilTagHandler(MessageListener<AprilTag> aprilTagHandler){
        aprilTagHandlers.remove(aprilTagHandler);
        if ((aprilTagHandlers.size() == 0) && (aprilTagSubscriber != null)){
            aprilTagSubscriber.shutdown();
            aprilTagSubscriber = null;
        }
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
        initAprilTagSubscriberIfNeeded();

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
