package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import udc_robot_control_msgs.AprilTag;

public class AprilTagListenerManager extends ListenerManager<AprilTag> {

    public AprilTagListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_APRIL_TAGS, AprilTag._TYPE);
    }

}
