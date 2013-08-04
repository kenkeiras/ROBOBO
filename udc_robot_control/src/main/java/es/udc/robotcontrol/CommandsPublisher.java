package es.udc.robotcontrol;

import org.ros.internal.message.Message;
import org.ros.message.MessageListener;
import org.ros.message.Time;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import udc_robot_control_java.ActionCommand;


/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 4/08/13
 * Time: 11:16
 * To change this template use File | Settings | File Templates.
 */
public class CommandsPublisher {
    private HeadlessRobotControl padre;

    private Publisher<ActionCommand> publisher;


    public CommandsPublisher(HeadlessRobotControl padre) {
        super();
        this.padre = padre;
    }

    public void conectar(ConnectedNode cn, String tn) {
        try {
            publisher = cn.newPublisher(tn, ActionCommand._TYPE);
        }
        catch (Exception ex) {
            // TODO: Manejar la excepcion
            ex.printStackTrace();
        }
    }

    public void desconectar() {
        publisher.shutdown();
    }

    public ActionCommand newMsg() {
        return publisher.newMessage();
    }

    public void publicar(ActionCommand comando) {
        comando.getHeader().setFrameId(padre.getRobotName());
        comando.getHeader().setStamp(Time.fromMillis(System.currentTimeMillis()));
        publisher.publish(comando);
    }
}
