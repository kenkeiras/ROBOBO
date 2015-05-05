package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import sensor_msgs.FluidPressure;

public class PressureListenerManager extends ListenerManager<FluidPressure> {

    public PressureListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_PRESSURE, FluidPressure._TYPE);
    }
}
