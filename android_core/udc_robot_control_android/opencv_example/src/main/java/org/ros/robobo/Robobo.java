package org.ros.robobo;

import java.net.URISyntaxException;

import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeMainExecutor;
import org.opencv.core.Mat;

public class Robobo {

    public static final String taskName = "OpenCV example";
    public static final String taskDescription = "Looks for orange "
        + "spheres from the camera feed";


    private String robotName;
    private static NodeMainExecutor executor;


    public static void main(String args[]) throws URISyntaxException {
        executor = DefaultNodeMainExecutor.newDefault();

        final String master = args[1];
        final String robotName = args[2];
        System.out.println("Connecting to master [URI: " + master
                           + " Robot name: " + robotName + " ]");

        Mat tmpMat = new Mat();

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
