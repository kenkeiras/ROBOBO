package es.udc.fic.robobo.rosWrapper.managers.listeners;

import org.ros.message.MessageListener;
import java.util.Set;

/**
 * Replicates the input unchanged for all the output defuned in the output set.
 *
 * @param <T> Type of ROS message to be multiplexed.
 */
class ListenerMultiplexer<T> implements MessageListener<T> {

    private final Set<MessageListener<T>> outputSet;

    public ListenerMultiplexer(final Set<MessageListener<T>> outputSet){
        this.outputSet = outputSet;
    }

    @Override
    public void onNewMessage(T t) {
        for (MessageListener<T> listener : outputSet){
            listener.onNewMessage(t);
        }
    }
}
