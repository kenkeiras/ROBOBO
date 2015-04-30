package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import sensor_msgs.Illuminance;

public class LightListenerManager extends ListenerManager<Illuminance> {

    public LightListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_LIGHT, Illuminance._TYPE);
    }
}
