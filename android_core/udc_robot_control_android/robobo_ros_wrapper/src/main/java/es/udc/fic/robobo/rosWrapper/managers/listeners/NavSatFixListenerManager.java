package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import sensor_msgs.NavSatFix;

public class NavSatFixListenerManager extends ListenerManager<NavSatFix> {

    public NavSatFixListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_NAV_SAT_FIX, NavSatFix._TYPE);
    }
}
