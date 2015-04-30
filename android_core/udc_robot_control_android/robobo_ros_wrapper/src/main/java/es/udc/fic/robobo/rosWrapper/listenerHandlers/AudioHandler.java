package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

import audio_common_msgs.AudioData;

public interface AudioHandler extends MessageListener<AudioData>{}
