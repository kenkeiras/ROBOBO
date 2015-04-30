package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import udc_robot_control_msgs.AndroidSensor3;

public class OrientationListenerManager extends ListenerManager<AndroidSensor3> {

    public OrientationListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_ORIENTATION, AndroidSensor3._TYPE);
    }
}
