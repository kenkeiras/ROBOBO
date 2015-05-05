package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

import sensor_msgs.RelativeHumidity;

public interface RelativeHumidityHandler extends MessageListener<RelativeHumidity>{}
