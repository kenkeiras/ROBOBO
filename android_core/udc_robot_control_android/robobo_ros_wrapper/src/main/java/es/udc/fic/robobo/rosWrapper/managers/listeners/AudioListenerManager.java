package es.udc.fic.robobo.rosWrapper.managers.listeners;

import audio_common_msgs.AudioData;
import es.udc.robotcontrol.utils.Constants;

public class AudioListenerManager extends ListenerManager<AudioData> {

    public AudioListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_AUDIO, AudioData._TYPE);
    }
}
