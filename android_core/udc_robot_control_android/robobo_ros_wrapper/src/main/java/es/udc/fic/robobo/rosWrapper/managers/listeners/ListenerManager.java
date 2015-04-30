package es.udc.fic.robobo.rosWrapper.managers.listeners;

import org.ros.master.client.TopicType;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.udc.robotcontrol.utils.Constants;

public class ListenerManager<T> {

    private final Set<MessageListener<T>> handlers = new HashSet<MessageListener<T>>();
    private final ListenerMultiplexer<T> multiplexer = new ListenerMultiplexer<T>(handlers);

    private final String robotName;
    private final String topicName;
    private final String topicType;

    private Subscriber<T> subscriber = null;
    private ConnectedNode connectedNode = null;

    public ListenerManager(String robotName, String topicName, String topicType){
        this.robotName = robotName;
        this.topicName = topicName;
        this.topicType = topicType;
    }

    private synchronized void initSubscriberIfNeeded(){
        if ((subscriber == null) && (connectedNode != null) && (handlers.size() > 0)) {

            String fullTopicName = "/" + robotName + "/" + topicName;

            subscriber = connectedNode.newSubscriber(fullTopicName, topicType);

            subscriber.addMessageListener(multiplexer);
        }
    }


    public void setConnectedNode(ConnectedNode connectedNode){
        this.connectedNode = connectedNode;

        initSubscriberIfNeeded();
    }


    /**
     * Add a handler to manage the incoming changes.
     *
     * Spawn the AprilTagListenerNode node if it wasn't before.
     *
     */
    public void addHandler(MessageListener<T> handler){
        handlers.add(handler);

        initSubscriberIfNeeded();
    }

    /**
     * Remove a single handler.
     *
     * If the handler set gets empty, stop the listener node.
     *
     */
    public synchronized void removeHandler(MessageListener<T> handler) {
        handlers.remove(handler);
        if ((handlers.size() == 0) && (subscriber != null)) {
            subscriber.shutdown();
            subscriber = null;
        }
    }
}
