package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

import sensor_msgs.Range;

public interface ProximityHandler extends MessageListener<Range>{}
