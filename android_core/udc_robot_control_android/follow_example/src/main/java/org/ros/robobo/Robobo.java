package org.ros.robobo;

import es.udc.fic.robobo.rosWrapper.ControllerNotFound;
import es.udc.fic.robobo.rosWrapper.RoboboController;
import es.udc.fic.robobo.rosWrapper.listenerHandlers.RobotSensorHandler;

import java.net.URI;
import udc_robot_control_msgs.SensorStatus;

public class Robobo {

    public static final String taskName = "Follower example";
    public static final String taskDescription = "Follows the object in front "
        + "by turning left or right based on the IR sensors";


    private static final int IR_THRESHOLD = 0x0FFF;
    private static RoboboController roboboController;

    public static void main(String args[]) throws ControllerNotFound {
        final String master = args[1];
        final String robotName = args[2];
        System.out.println("Connecting to master [URI: " + master
                           + "  Robot name: " + robotName + " ]");

        roboboController = new RoboboController(URI.create(master), robotName);
        roboboController.setEnginesTwist(1, 0);
        roboboController.addRobotSensorHandler(new RobotSensorHandler() {

            public void updateEngines(boolean leftEngine, boolean rightEngine){

                double speed, turn;

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

                roboboController.setEnginesTwist(speed, turn);
            }


            @Override
            public void onNewMessage(SensorStatus sensorStatus) {
                boolean goLeft = sensorStatus.getSIr9() < IR_THRESHOLD;
                boolean goRight = sensorStatus.getSIr2() < IR_THRESHOLD;

                updateEngines(goLeft, goRight);
            }
        });

        waitForShutdown();
        System.out.println("Done stopping");
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
