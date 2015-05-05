package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import udc_robot_control_msgs.SensorStatus;

public class RobotSensorListenerManager extends ListenerManager<SensorStatus> {

    public RobotSensorListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_IR_SENSORS, SensorStatus._TYPE);
    }
}
