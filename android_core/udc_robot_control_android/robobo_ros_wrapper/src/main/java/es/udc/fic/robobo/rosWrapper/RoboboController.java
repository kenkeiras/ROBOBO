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

import java.lang.String;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import es.udc.fic.robobo.rosWrapper.managers.listeners.AprilTagListenerManager;
import es.udc.fic.robobo.rosWrapper.managers.producers.InfoProducerManager;
import udc_robot_control_msgs.AprilTag;

public class RoboboController implements NodeMain {

    private final NodeMainExecutor executor;

    // Listeners
    final AprilTagListenerManager aprilTagListenerManager;

    // Publishers
    final InfoProducerManager infoProducerManager;

    public RoboboController(URI masterURI, String robotName) throws ControllerNotFound {
        // Initialize listener managers
        aprilTagListenerManager = new AprilTagListenerManager(robotName);

        // Initialize producer managers
        infoProducerManager = new InfoProducerManager(robotName);

        // Start this node
        executor = DefaultNodeMainExecutor.newDefault();
        String host = InetAddressFactory.newNonLoopback().getHostAddress();
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host);

        nodeConfiguration.setMasterUri(masterURI);
        executor.execute(this, nodeConfiguration);
    }


    // Listeners
    /**
     * Add an AprilTag handler to manage the incoming changes.
     */
    public synchronized void addAprilTagHandler(MessageListener<AprilTag> aprilTagHandler) {
        aprilTagListenerManager.addHandler(aprilTagHandler);
    }

    /**
     * Remove a single AprilTag handler.
     */
    public synchronized void removeAprilTagHandler(MessageListener<AprilTag> aprilTagHandler){
        aprilTagListenerManager.removeHandler(aprilTagHandler);
    }


    // Publishers
    /**
     * Publish a message to the Info screen.
     */
    public void publishInfoMessage(String htmlData){
        infoProducerManager.publish(htmlData);
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
        aprilTagListenerManager.setConnectedNode(connectedNode);

        infoProducerManager.setConnectedNode(connectedNode);
    }

    @Override
    public void onShutdown(Node node) {}

    @Override
    public void onShutdownComplete(Node node) {}

    @Override
    public void onError(Node node, Throwable throwable) {}
}
