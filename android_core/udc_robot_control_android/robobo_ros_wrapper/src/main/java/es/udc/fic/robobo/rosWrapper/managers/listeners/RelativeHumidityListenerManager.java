package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import sensor_msgs.RelativeHumidity;

public class RelativeHumidityListenerManager extends ListenerManager<RelativeHumidity>{

    public RelativeHumidityListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_RELATIVE_HUMIDITY, RelativeHumidity._TYPE);
    }
}
