package es.udc.fic.android.robot_control.audio;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constants;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;
import org.ros.message.MessageListener;

public class TextToSpeechListener implements NodeMain {

    private Context ctx;
    private String robotName;
    private NodeMainExecutor nodeMainExecutor;

    private ConnectedNode cn;
    private Subscriber<std_msgs.String> subscriber;

    private TTSMessageListener ml;

    public TextToSpeechListener(Context ctx, String robotName, NodeMainExecutor nodeMainExecutor) {
        super();
        Log.d(C.TAG, "Creating TTS Listener");
        this.ctx = ctx;
        this.robotName = robotName;
        this.nodeMainExecutor = nodeMainExecutor;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(C.DefaultBaseNodeName + "/" + Constants.TOPIC_TEXT_TO_SPEECH);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        cn = connectedNode;
        ml = new TTSMessageListener(ctx, nodeMainExecutor);

        String topicName = robotName + "/" + Constants.TOPIC_TEXT_TO_SPEECH;
        subscriber = connectedNode.newSubscriber(topicName, std_msgs.String._TYPE);
        subscriber.addMessageListener(ml);
    }

    @Override
    public void onShutdown(Node node) {
        if (subscriber != null) {
            subscriber.shutdown();
            subscriber = null;
        }
    }

    @Override
    public void onShutdownComplete(Node node) {
    }


    @Override
    public void onError(Node node, Throwable throwable) {
        Log.w(C.CMD_TAG, "Error on TextToSpeech Listener [ " + node.getName() + " ] [ " + throwable.getMessage() + " ]", throwable);
    }

    private class TTSMessageListener implements
                                     MessageListener<std_msgs.String>,
                                     TextToSpeech.OnInitListener {

        private TextToSpeech tts;
        private boolean ttsReady = false;
        private NodeMainExecutor nodeMainExecutor;


        public TTSMessageListener(Context ctx, NodeMainExecutor nodeMainExecutor) {
            this.nodeMainExecutor = nodeMainExecutor;
            tts = new TextToSpeech(ctx, this);
        }

        public void onInit(int status){
            if (status == TextToSpeech.SUCCESS){
                ttsReady = true;
            }
            else {
                Log.e("UDC", "Couldn't setup TTS");
            }
        }


        @Override
        public void onNewMessage(std_msgs.String msg) {
            if (ttsReady){
                tts.speak(msg.getData(), TextToSpeech.QUEUE_FLUSH, null);
            }
            else {
                Log.w("UDC", "TTS wasn't ready, skipped message “"
                      + msg.getData() + "”");
            }
        }
    }
}
