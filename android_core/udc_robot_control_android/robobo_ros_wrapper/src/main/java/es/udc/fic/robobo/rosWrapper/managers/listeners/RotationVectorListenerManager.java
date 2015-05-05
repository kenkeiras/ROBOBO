package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import udc_robot_control_msgs.AndroidSensor3;

public class RotationVectorListenerManager extends ListenerManager<AndroidSensor3> {

    public RotationVectorListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_ROTATION_VECTOR, AndroidSensor3._TYPE);
    }
}
