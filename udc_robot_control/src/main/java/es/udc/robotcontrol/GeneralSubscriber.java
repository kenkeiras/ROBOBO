package es.udc.robotcontrol;

import org.ros.internal.message.Message;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import udc_robot_control_java.BateryStatus;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 1/08/13
 * Time: 19:56
 * Esta clase implementa un subscriptor general, que se configura desde el HeadlessRobotControl
 *
 */
public class GeneralSubscriber  {

    private HeadlessRobotControl padre;

    private String messageTypeName;
    private Subscriber subscriber;


    public GeneralSubscriber(HeadlessRobotControl papa, String messageTypeName) {
        super();
        this.padre = papa;
        this.messageTypeName = messageTypeName;
    }


    public void conectar(ConnectedNode cn, String tn) {
        try {
            subscriber = cn.newSubscriber(tn, messageTypeName);
            subscriber.addMessageListener(new MessageListener() {
                @Override
                public void onNewMessage(Object o) {
                    Message m = (Message) o;
                    padre.notifyMsg(m);
                }
            });

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void desconectar() {
        subscriber.shutdown();
    }

}
