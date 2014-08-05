package org.ros.robobo;

import java.net.URISyntaxException;

import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeMainExecutor;


public class Robobo {

    public static final String taskName = "April Tag example";
    public static final String taskDescription = "Reads April tags and "
        + "shows it's id on information screen";


    private String robotName;
    private static NodeMainExecutor executor;
    private static float speed = 0;
    private static float turn = 0;
    private static final int REFRESH_TIME = 100; // In millis


    public static void main(String args[]) throws URISyntaxException {
        executor = DefaultNodeMainExecutor.newDefault();

        final String master = args[1];
        System.out.println("Connecting to master [URI: " + master + " ]");

        AprilTagListener listener = new AprilTagListener("robot1", master, executor);

        waitForShutdown();
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
}
