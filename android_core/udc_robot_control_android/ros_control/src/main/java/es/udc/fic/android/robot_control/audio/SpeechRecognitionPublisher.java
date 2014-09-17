package es.udc.fic.android.robot_control.audio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constants;
import es.udc.fic.android.robot_control.audio.SpeechRecognitionService;
import es.udc.fic.android.robot_control.audio.SpeechRecognitionService.SimpleBinder;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;


public class SpeechRecognitionPublisher implements NodeMain {

    private static String QUEUE_NAME = Constants.TOPIC_SPEECH_RECOGNITION;
    private Publisher<std_msgs.String> publisher;
    private Context context;
    private String robotName;
    private String topicName;

    private Intent intent = null;

    public SpeechRecognitionPublisher(Context context, String robotName) {
        super();
        this.context = context;
        this.robotName = robotName;
        topicName = robotName + "/" + Constants.TOPIC_SPEECH_RECOGNITION;
    }


    public void publish(String speech){
        std_msgs.String msg = publisher.newMessage();
        msg.setData(speech);
        publisher.publish(msg);
    }


    ServiceConnection speechRecognizerConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder bind) {
                SimpleBinder sBinder = (SimpleBinder) bind;
                sBinder.getService().setPublisher(SpeechRecognitionPublisher.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };


    // Node interface
    @Override
    public void onStart(final ConnectedNode connectedNode) {
        Log.i(C.TAG, "Starting Speech Recognition Publisher");
        publisher = connectedNode.newPublisher(topicName, std_msgs.String._TYPE);

        intent = new Intent(context, SpeechRecognitionService.class);
        context.startService(intent);

        context.bindService(intent, speechRecognizerConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(C.DefaultBaseNodeName + "/" + QUEUE_NAME);
    }


    /**
     * Called when the {@link org.ros.node.ConnectedNode} has started shutting down. Shutdown
     * will be delayed, although not indefinitely, until all {@link NodeListener}s
     * have returned from this method.
     * <p>
     * Since this method can potentially delay {@link org.ros.node.ConnectedNode} shutdown, it
     * is preferred to use {@link #onShutdownComplete(org.ros.node.Node)} when
     * {@link org.ros.node.ConnectedNode} resources are not required during the method call.
     *
     * @param node
     *          the {@link org.ros.node.Node} that has started shutting down
     */
    @Override
    public void onShutdown(Node node) {
        Log.i(C.TAG, "[ "  + node.getName() + " ] onShutdown [ " + QUEUE_NAME + " ]");
    }


    /**
     * Called when the {@link org.ros.node.Node} has shut down.
     *
     * @param node
     *          the {@link org.ros.node.Node} that has shut down
     */
    @Override
    public void onShutdownComplete(Node node) {
        Log.i(C.TAG, "[ "  + node.getName() + " ] onShutdownComplete [ " + QUEUE_NAME + " ]");
    }


    /**
     * Called when the {@link org.ros.node.Node} experiences an unrecoverable error.
     *
     * @param node
     *          the {@link org.ros.node.Node} that experienced the error
     * @param throwable
     *          the {@link Throwable} describing the error condition
     */
    @Override
    public void onError(Node node, Throwable throwable) {
        Log.w(C.TAG, "[ "  + node.getName() + " ] onError [ " + QUEUE_NAME + " ]", throwable);
    }
}
