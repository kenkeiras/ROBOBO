package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

import udc_robot_control_msgs.SensorStatus;

public interface RobotSensorHandler extends MessageListener<SensorStatus>{}
