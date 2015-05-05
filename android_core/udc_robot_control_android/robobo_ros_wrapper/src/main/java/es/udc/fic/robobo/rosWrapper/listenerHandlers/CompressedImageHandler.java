package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

import sensor_msgs.CompressedImage;

public interface CompressedImageHandler extends MessageListener<CompressedImage>{}
