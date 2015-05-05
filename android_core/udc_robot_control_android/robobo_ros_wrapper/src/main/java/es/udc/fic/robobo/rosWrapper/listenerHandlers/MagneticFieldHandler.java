package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

import sensor_msgs.MagneticField;

public interface MagneticFieldHandler extends MessageListener<MagneticField>{}
