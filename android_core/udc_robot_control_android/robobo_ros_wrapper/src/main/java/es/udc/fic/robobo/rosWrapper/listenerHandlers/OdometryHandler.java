package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

import nav_msgs.Odometry;

public interface OdometryHandler extends MessageListener<Odometry>{}
