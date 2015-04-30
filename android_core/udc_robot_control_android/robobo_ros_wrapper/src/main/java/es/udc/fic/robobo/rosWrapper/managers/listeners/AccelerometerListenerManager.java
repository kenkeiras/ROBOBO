package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import udc_robot_control_msgs.AndroidSensor3;

public class AccelerometerListenerManager extends  ListenerManager<AndroidSensor3> {
    public AccelerometerListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_ACCELEROMETER, AndroidSensor3._TYPE);
    }
}
