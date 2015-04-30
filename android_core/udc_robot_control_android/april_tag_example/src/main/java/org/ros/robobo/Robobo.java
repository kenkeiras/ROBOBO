package org.ros.robobo;

import java.net.URI;
import java.net.URISyntaxException;

import es.udc.fic.robobo.rosWrapper.ControllerNotFound;
import es.udc.fic.robobo.rosWrapper.RoboboController;
import es.udc.fic.robobo.rosWrapper.listenerHandlers.AprilTagHandler;
import udc_robot_control_msgs.AprilTag;

public class Robobo {

    public static final String taskName = "April Tag example";
    public static final String taskDescription = "Reads April tags and "
        + "shows it's id on information screen";

    private static RoboboController roboboController;

    public static void main(String args[]) throws URISyntaxException, ControllerNotFound {
        final String master = args[1];
        final String robotName = args[2];
        System.out.println("Connecting to master [URI: " + master
                + " Robot name: " + robotName + " ]");


        roboboController = new RoboboController(URI.create(master), robotName);

        // Define the action to do when a new AprilTag is found
        roboboController.addAprilTagHandler(new AprilTagHandler() {
            @Override
            public void onNewMessage(AprilTag tag) {

                System.out.println("April tag detection" + "\n"
                        + "Code: " + tag.getCode() + "\n"
                        + "ID: " + tag.getId() + "\n"
                        + "Hamming: " + tag.getHammingDistance() + "\n"
                        + "Rotation: " + tag.getRotation() + "\n"
                        + "Perimeter: " + tag.getObservedPerimeter() + "\n\n");


                String html = "<html><head></head><body><center><font size=\"12\">"
                              + tag.getId()
                              + "</font><center></body></html>";

                roboboController.publishInfoMessage(html);
            }
        });
        waitForShutdown();
    }


    private static void waitForShutdown(){
        try {
            while (true){
                Thread.sleep(Integer.MAX_VALUE);
            }
        }
        catch(InterruptedException e){
            roboboController.stop();
        }
    }
}
