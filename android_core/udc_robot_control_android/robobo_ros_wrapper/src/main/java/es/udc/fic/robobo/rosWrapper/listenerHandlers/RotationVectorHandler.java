package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

import udc_robot_control_msgs.AndroidSensor3;

public interface RotationVectorHandler extends MessageListener<AndroidSensor3>{}
