package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

import sensor_msgs.Illuminance;

public interface LightHandler extends MessageListener<Illuminance>{}
