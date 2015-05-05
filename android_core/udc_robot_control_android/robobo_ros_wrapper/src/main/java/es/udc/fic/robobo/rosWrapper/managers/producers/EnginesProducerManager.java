package es.udc.fic.robobo.rosWrapper.managers.producers;

import es.udc.fic.robobo.rosWrapper.utils.Tuple;
import es.udc.robotcontrol.utils.Constants;
import geometry_msgs.Twist;

public class EnginesProducerManager extends ProducerManager<Twist, Tuple<Double, Double>> {

    public EnginesProducerManager(String robotName){
        super(robotName, Constants.TOPIC_SCREEN, std_msgs.String._TYPE);
    }

    @Override
    protected void populate(Twist message, Tuple<Double, Double> data) {
        message.getLinear().setX(data._1);
        message.getAngular().setY(data._2);
    }
}
