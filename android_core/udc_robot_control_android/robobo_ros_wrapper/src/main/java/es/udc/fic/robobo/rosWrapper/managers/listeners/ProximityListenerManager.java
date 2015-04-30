package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import sensor_msgs.Range;

public class ProximityListenerManager extends ListenerManager<Range> {

    public ProximityListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_PROXIMITY, Range._TYPE);
    }
}
