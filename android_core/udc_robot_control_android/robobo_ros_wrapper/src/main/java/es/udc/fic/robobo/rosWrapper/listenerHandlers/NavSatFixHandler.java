package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

import sensor_msgs.NavSatFix;

public interface NavSatFixHandler extends MessageListener<NavSatFix>{}
