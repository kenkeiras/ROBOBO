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
import udc_robot_control_java.ActionCommand;

/**
 * Este es el nodo encargado de recibir instrucciones desde el exterior y ejecutarlas.
 *
 *
 * Created by kerry on 1/08/13.
 */
public class CommandListener implements NodeMain {

    private UDCAndroidControl context;
    private String robotName;
    private NodeMainExecutor nodeMainExecutor;

    private ConnectedNode cn;
    private Subscriber<ActionCommand> subscriber;

    private CommandMessageListener cml;

    public CommandListener(UDCAndroidControl ctx, String robotName, NodeMainExecutor nodeMainExecutor) {
        super();
        Log.d(C.TAG, "Creando Command Listener");
        this.context = ctx;
        this.robotName = robotName;
        this.nodeMainExecutor = nodeMainExecutor;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(C.DefaultBaseNodeName + "/" + Constantes.TOPIC_COMMANDS);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        cn = connectedNode;
        cml = new CommandMessageListener(this, context, nodeMainExecutor);

        String topicName = robotName + "/" + Constantes.TOPIC_COMMANDS;
        subscriber = connectedNode.newSubscriber(topicName, ActionCommand._TYPE);
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
        Log.w(C.CMD_TAG, "Error on Command Listener [ " + node.getName() + " ] [ " + throwable.getMessage() + " ]", throwable);
    }

    private class CommandMessageListener implements MessageListener<ActionCommand> {

        private CommandListener padre;
        private UDCAndroidControl ctx;
        private NodeMainExecutor nodeMainExecutor;
        private RosCameraPreviewView rosCameraPreviewView;

        public CommandMessageListener(CommandListener papa, UDCAndroidControl context, NodeMainExecutor nodeMainExecutor) {
            this.padre = papa;
            this.ctx = context;
            this.nodeMainExecutor = nodeMainExecutor;
        }


        @Override
        public void onNewMessage(ActionCommand actionCommand) {

            Log.d(C.CMD_TAG, "Recibido comando [ " + actionCommand.getCommand() + " ]");
            switch (actionCommand.getCommand()) {
                case ActionCommand.CMD_HARD_RESET:
                case ActionCommand.CMD_RESET:
                case ActionCommand.CMD_SET_ENGINES:
                case ActionCommand.CMD_SET_LEDS:
                    context.enviarRobot(actionCommand);
                    break;
                case ActionCommand.CMD_START_PUBLISHER:
                    ctx.arrancarListener(actionCommand);
                    break;
                case ActionCommand.CMD_STOP_PUBLISHER:
                    ctx.detenerListener(actionCommand);
                    break;
                default:
                    Log.w(C.CMD_TAG, "Commando no reconocido [ " + actionCommand.getCommand() + " ]");
            }
        }
    }
}
