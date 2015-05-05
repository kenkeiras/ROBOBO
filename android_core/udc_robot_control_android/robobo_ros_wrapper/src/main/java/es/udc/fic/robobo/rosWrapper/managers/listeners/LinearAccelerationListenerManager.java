package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import udc_robot_control_msgs.AndroidSensor3;

public class LinearAccelerationListenerManager extends ListenerManager<AndroidSensor3> {

    public LinearAccelerationListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_LINEAL_ACCELERATION, AndroidSensor3._TYPE);
    }
}
