package es.udc.fic.android.robot_control.commands;

import android.util.Log;
import es.udc.fic.android.robot_control.UDCAndroidControl;
import es.udc.fic.android.robot_control.camara.RosCameraPreviewView;
import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constantes;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;
import org.ros.message.MessageListener;
import geometry_msgs.Twist;

/**
 * Este es el nodo encargado de recibir instrucciones desde el exterior y ejecutarlas.
 *
 *
 * Created by kerry on 1/08/13.
 */
public class EngineListener implements NodeMain {

    private EngineManager manager;
    private String robotName;
    private NodeMainExecutor nodeMainExecutor;

    private ConnectedNode cn;
    private Subscriber<Twist> subscriber;

    private CommandMessageListener cml;

    public EngineListener(EngineManager manager, String robotName, NodeMainExecutor nodeMainExecutor) {
        super();
        Log.d(C.TAG, "Creando Engine Listener");
        this.manager = manager;
        this.robotName = robotName;
        this.nodeMainExecutor = nodeMainExecutor;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(C.DefaultBaseNodeName + "/" + Constantes.TOPIC_ENGINES);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        cn = connectedNode;
        cml = new CommandMessageListener(manager, nodeMainExecutor);

        String topicName = robotName + "/" + Constantes.TOPIC_ENGINES;
        subscriber = connectedNode.newSubscriber(topicName, Twist._TYPE);
        subscriber.addMessageListener(cml);
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
        Log.w(C.CMD_TAG, "Error on Engine Listener [ " + node.getName() + " ] [ " + throwable.getMessage() + " ]", throwable);
    }

    private class CommandMessageListener implements MessageListener<Twist> {
        private EngineManager manager;
        private NodeMainExecutor nodeMainExecutor;


        public CommandMessageListener(EngineManager manager, NodeMainExecutor nodeMainExecutor) {
            this.manager = manager;
            this.nodeMainExecutor = nodeMainExecutor;
        }


        @Override
        public void onNewMessage(Twist twist) {
            manager.setTwist(twist);
        }
    }
}
