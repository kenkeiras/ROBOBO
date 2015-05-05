package es.udc.fic.robobo.rosWrapper.managers.producers;

import es.udc.robotcontrol.utils.Constants;

public class InfoProducerManager extends ProducerManager<std_msgs.String, String> {

    public InfoProducerManager(String robotName){
        super(robotName, Constants.TOPIC_SCREEN, std_msgs.String._TYPE);
    }

    @Override
    protected void populate(std_msgs.String message, String data) {
        message.setData(data);
    }
}
