package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import udc_robot_control_msgs.AndroidSensor3;

public class GyroscopeListenerManager extends ListenerManager<AndroidSensor3> {

    public GyroscopeListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_GYROSCOPE, AndroidSensor3._TYPE);
    }
}
