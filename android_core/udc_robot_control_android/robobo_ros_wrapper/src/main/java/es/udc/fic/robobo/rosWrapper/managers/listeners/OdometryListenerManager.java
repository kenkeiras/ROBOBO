package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import nav_msgs.Odometry;

public class OdometryListenerManager extends ListenerManager<Odometry> {

    public OdometryListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_ODOMETRY, Odometry._TYPE);
    }
}
