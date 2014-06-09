package es.udc.robotcontrol;

import es.udc.robotcontrol.utils.Constantes;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import udc_robot_control_msgs.ActionCommand;
import udc_robot_control_msgs.Led;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 29/08/13
 * Time: 19:08
 * To change this template use File | Settings | File Templates.
 */
public abstract  class AbstractRobotControl implements NodeMain {
    private String robotName;
    private RosListener notificador;
    private ConnectedNode cn;

    protected AbstractRobotControl() {
        super();
    }

    public AbstractRobotControl(String theRobotName) {
        super();
        robotName = theRobotName;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("panelcontrol");
    }

    public void registerNotificador(RosListener n) {
        notificador = n;
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        cn = connectedNode;
    }


    @Override
    public void onShutdownComplete(Node node) {

    }

    @Override
    public void onError(Node node, Throwable throwable) {
        if (notificador != null) {
            notificador.onError(node, throwable);
        }
    }

    public void notifyMsg(org.ros.internal.message.Message msg) {
        if (notificador != null) {
            notificador.onMsgArrived(msg);
        }
    }

    public abstract ActionCommand newCommand() ;

    public Led newLed() {
        if (cn != null) {
            return cn.getTopicMessageFactory().newFromType(Led._TYPE);
        }
        else {
            return null;
        }
    }

    public abstract void sendCommand(ActionCommand msg);


    protected String nombreCola(String topicName) {
        return getRobotName() + "/" + topicName;
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }
}
