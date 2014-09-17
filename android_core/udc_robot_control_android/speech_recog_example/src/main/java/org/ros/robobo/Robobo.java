package org.ros.robobo;

import java.net.URISyntaxException;

import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeMainExecutor;


public class Robobo {

    public static final String taskName = "Speech Recognition example";
    public static final String taskDescription = "Enable speech recognition "
        + "and pipe it's output to the screen";


    private String robotName;
    private static NodeMainExecutor executor;


    public static void main(String args[]) throws URISyntaxException {
        executor = DefaultNodeMainExecutor.newDefault();

        final String master = args[1];
        final String robotName = args[2];
        System.out.println("Connecting to master [URI: " + master
                           + " Robot name: " + robotName + " ]");

        SpeechRecognitionListener listener = new SpeechRecognitionListener(
                robotName, master, executor
                );

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
