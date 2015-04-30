package es.udc.fic.robobo.rosWrapper.managers.producers;

import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import java.util.LinkedList;


public abstract class ProducerManager<T, J> {

    private final String robotName;
    private final String topicName;
    private final String topicType;

    private ConnectedNode connectedNode;
    private Publisher<T> publisher;
    private final LinkedList<J> queue = new LinkedList<>();

    public ProducerManager(String robotName, String topicName, String topicType) {
        this.robotName = robotName;
        this.topicName = topicName;
        this.topicType = topicType;
    }


    public synchronized void setConnectedNode(ConnectedNode connectedNode){
        this.connectedNode = connectedNode;

        sendOrQueue(null);
    }


    /**
     * Send the data if and only if the connected node
     *
     * @param data Data to be sent (or null, to flush the queue).
     */
    private synchronized void sendOrQueue(J data){
        if (data != null) {
            queue.add(data);
        }

        if (connectedNode == null){
            return;
        }

        if (publisher == null){
            publisher = connectedNode.newPublisher("/" + robotName + "/" + topicName, topicType);
        }

        // Flush the queue
        while(queue.size() > 0){
            T msg = publisher.newMessage();
            populate(msg, queue.pop());
            publisher.publish(msg);
        }
    }


    /**
     * Send data to the ROS channel.
     *
     * @param data Data to be sent.
     */
    public void publish(J data){
        if (data == null){
            throw new IllegalArgumentException("Expected non-null data");
        }

        sendOrQueue(data);
    }

    /**
     * Fit the input data in the message.
     *
     * @param message Message to be sent.
     * @param data Data sent to the controller
     */
    protected abstract void populate(T message, J data);
}
