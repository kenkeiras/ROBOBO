package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

public interface SpeechRecognitionHandler extends MessageListener<std_msgs.String>{}
