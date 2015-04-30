package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import sensor_msgs.MagneticField;

public class MagneticFieldListenerManager extends ListenerManager<MagneticField> {

    public MagneticFieldListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_MAGNETIC_FIELD, MagneticField._TYPE);
    }
}
