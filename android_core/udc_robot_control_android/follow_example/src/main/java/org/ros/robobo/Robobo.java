package org.ros.robobo;

import geometry_msgs.Twist;

import java.net.URI;
import java.net.URISyntaxException;
import es.udc.robotcontrol.utils.Constants;

import org.ros.address.InetAddressFactory;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;


public class Robobo implements NodeMain {

    public static final String taskName = "Follower example";
    public static final String taskDescription = "Follows the object in front "
        + "by turning left or right based on the IR sensors";


    private Publisher<Twist> publisher;
    private String robotName;
    private static NodeMainExecutor executor;
    private static float speed = 0;
    private static float turn = 0;
    private static final int REFRESH_TIME = 100; // In millis


    public static void main(String args[]){
        executor = DefaultNodeMainExecutor.newDefault();

        final String master = args[1];
        final String robotName = args[2];
        System.out.println("Connecting to master [URI: " + master
                           + "  Robot name: " + robotName + " ]");

        Robobo robot = new Robobo(robotName, master);
        SensorNode sensor = new SensorNode(robotName, master, executor,
                                           new SensorListener(executor));

        waitForShutdown();
        sensor.shutdown();
        robot.shutdown();
        System.out.println("Done stopping");
    }


    private static void waitForShutdown(){
        try {
            while (true){
                Thread.sleep(Integer.MAX_VALUE);
            }
        }
        catch(InterruptedException e){}

        executor.shutdown();
    }


    public Robobo(String robotName, String master) {
        super();
        System.out.println("Creando Command Publisher");
        this.robotName = robotName;

        String host = InetAddressFactory.newNonLoopback().getHostAddress();
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host);

        URI masterUri = null;
        try {
            masterUri = new URI(master);
            nodeConfiguration.setMasterUri(masterUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        executor.execute(this, nodeConfiguration);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("RED");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        System.out.println("Starting...");
        String topicName = robotName + "/" + Constants.TOPIC_ENGINES;
        publisher = connectedNode.newPublisher(topicName, Twist._TYPE);

        try {

            while (publisher != null){

                Twist msg = publisher.newMessage();
                msg.getLinear().setX(speed);
                msg.getAngular().setY(turn);


                // If publisher may be set to null from asynchronously
                // keep it's value on a local references across the check
                Publisher<Twist> oncePublisher = publisher;
                if (oncePublisher != null){
                    oncePublisher.publish(msg);

                    System.out.println("Published! " + speed + " * " + turn);

                    Thread.sleep(REFRESH_TIME);
                }

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void setEngines(boolean leftEngine, boolean rightEngine){
        if (leftEngine || rightEngine) {
            speed = 1.0f;
        }
        else {
            speed = 0.0f;
        }

        if (leftEngine && (!rightEngine)) {
            turn = 1.0f;
        }
        else if (rightEngine && (!leftEngine)) {
            turn = -1.0f;
        }
        else {
            turn = 0.0f;
        }
    }


    public void onShutdown(Node node) {
    }

    public void onShutdownComplete(Node node) {
    }


    public void shutdown(){
        if (publisher != null) {
            publisher.shutdown();
            publisher = null;
        }
    }


    @Override
    public void onError(Node node, Throwable throwable) {
        System.out.println("Error on Command Listener [ " + node.getName() + " ] [ " + throwable.getMessage() + " ]");
    }
}
