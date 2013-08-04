package es.udc.robotcontrol;

import org.ros.internal.message.Message;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

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

    private Subscriber<Message> subscriber;


    public GeneralSubscriber(HeadlessRobotControl papa, String messageTypeName) {
        super();
        this.padre = papa;
        this.messageTypeName = messageTypeName;
    }


    public void conectar(ConnectedNode cn, String tn) {
        try {
            subscriber = cn.newSubscriber(tn, messageTypeName);
            subscriber.addMessageListener(new MessageListener<Message>() {
                @Override
                public void onNewMessage(Message m) {
                    padre.notifyMsg(m);
                }
            });

        }
        catch (Exception ex) {
            // TODO: Manejar la excepción
            ex.printStackTrace();
        }
    }

    public void desconectar() {
        subscriber.shutdown();
    }

}
