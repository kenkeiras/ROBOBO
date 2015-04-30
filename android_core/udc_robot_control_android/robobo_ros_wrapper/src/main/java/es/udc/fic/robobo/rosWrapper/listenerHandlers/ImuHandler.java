package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

import sensor_msgs.Imu;

public interface ImuHandler extends MessageListener<Imu>{}
