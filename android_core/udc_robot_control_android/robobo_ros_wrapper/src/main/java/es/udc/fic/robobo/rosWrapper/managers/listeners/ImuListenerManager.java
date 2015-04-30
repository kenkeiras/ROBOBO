package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import sensor_msgs.Imu;

public class ImuListenerManager extends ListenerManager<Imu> {

    public ImuListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_IMU, Imu._TYPE);
    }
}
