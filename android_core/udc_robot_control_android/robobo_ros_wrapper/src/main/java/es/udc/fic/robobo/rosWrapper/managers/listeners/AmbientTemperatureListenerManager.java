package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import sensor_msgs.Temperature;

public class AmbientTemperatureListenerManager extends ListenerManager<Temperature> {

    public AmbientTemperatureListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_AMBIENT_TEMPERATURE, Temperature._TYPE);
    }
}
