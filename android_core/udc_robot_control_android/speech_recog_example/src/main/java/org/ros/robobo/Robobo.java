package org.ros.robobo;

import java.net.URI;

import es.udc.fic.robobo.rosWrapper.ControllerNotFound;
import es.udc.fic.robobo.rosWrapper.RoboboController;
import es.udc.fic.robobo.rosWrapper.listenerHandlers.SpeechRecognitionHandler;

public class Robobo {

    public static final String taskName = "Speech Recognition example";
    public static final String taskDescription = "Enable speech recognition "
        + "and pipe it's output to the screen";


    private String robotName;
    private static RoboboController roboboController;

    public static void main(String args[]) throws ControllerNotFound {
        final String master = args[1];
        final String robotName = args[2];
        System.out.println("Connecting to master [URI: " + master
                           + " Robot name: " + robotName + " ]");


        roboboController = new RoboboController(URI.create(master), robotName);
        roboboController.addSpeechRecognitionHandler(new SpeechRecognitionHandler() {
            @Override
            public void onNewMessage(std_msgs.String message) {

                System.out.println("Speech: " + message.getData() + "\n\n");

                String html = "<html><head></head><body><center><font size=\"12\">"
                               + message.getData()
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
