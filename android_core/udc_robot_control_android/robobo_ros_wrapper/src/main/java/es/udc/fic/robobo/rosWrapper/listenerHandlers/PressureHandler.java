package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

import sensor_msgs.FluidPressure;

public interface PressureHandler extends MessageListener<FluidPressure>{}
