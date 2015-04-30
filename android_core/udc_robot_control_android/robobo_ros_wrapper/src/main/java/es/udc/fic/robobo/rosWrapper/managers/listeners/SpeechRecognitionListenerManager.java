package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;

public class SpeechRecognitionListenerManager extends ListenerManager<std_msgs.String> {

    public SpeechRecognitionListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_SPEECH_RECOGNITION, std_msgs.String._TYPE);
    }
}
