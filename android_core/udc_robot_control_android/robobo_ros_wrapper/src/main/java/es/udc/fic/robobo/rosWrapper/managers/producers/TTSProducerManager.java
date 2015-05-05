package es.udc.fic.robobo.rosWrapper.managers.producers;

import es.udc.robotcontrol.utils.Constants;

public class TTSProducerManager extends ProducerManager<std_msgs.String, String> {

    public TTSProducerManager(String robotName){
        super(robotName, Constants.TOPIC_SCREEN, std_msgs.String._TYPE);
    }

    @Override
    protected void populate(std_msgs.String message, String data) {
        message.setData(data);
    }
}
