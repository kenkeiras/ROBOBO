package es.udc.robotcontrol;

import org.ros.internal.message.Message;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import sensor_msgs.Joy;
import udc_robot_control_java.ActionCommand;
import udc_robot_control_java.Led;

/**
 * Este es otro decorador para utilizar un joystick con el robot
 *
 */
public class JoystickRobotControl extends AbstractRobotControl implements RosListener {

    private AbstractRobotControl decorado;

    private GeneralSubscriber joystickSubscriber;

    private Joy lastMessage;

    /**
     * Constructor.
     * @param decor Controler a decorar
     */
    public JoystickRobotControl(AbstractRobotControl decor) {
        decorado = decor;
        // Somos el interfaz del decorado. Las notificaciones pasan por nosotros.
        decorado.registerNotificador(this);
    }

    @Override
    public void sendCommand(ActionCommand msg) {
        decorado.sendCommand(msg);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return decorado.getDefaultNodeName();
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        decorado.onStart(connectedNode);
        // Subscribirnos a la cola de joy
        joystickSubscriber = new GeneralSubscriber(this, sensor_msgs.Joy._TYPE);
        joystickSubscriber.conectar(connectedNode, "joy");
    }

    @Override
    public void onShutdown(Node node) {
        decorado.onShutdown(node);
    }

    @Override
    public void onShutdownComplete(Node node) {
        decorado.onShutdownComplete(node);
    }


    @Override
    public void onMsgArrived(Message message) {
        if (message instanceof Joy) {
            Joy j = (Joy) message;
            processJoy(j);
        }
        else super.notifyMsg(message);
    }

    @Override
    public void onError(Node node, Throwable throwable) {
        decorado.onError(node, throwable);
    }

    @Override
    public void notifyMsg(Message msg) {
        decorado.notifyMsg(msg);
    }

    @Override
    public ActionCommand newCommand() {
        return decorado.newCommand();
    }

    @Override
    public Led newLed() {
        return decorado.newLed();
    }

    @Override
    public String getRobotName() {
        return decorado.getRobotName();
    }

    @Override
    public void setRobotName(String robotName) {
        decorado.setRobotName(robotName);
    }


    /**
     * Comparamos el mensaje actual con el anterior.
     * Guardamos el mensaje actual.
     * @param j
     */
    private void processJoy(Joy j) {

        // Comprobar avance
        boolean izquierdo = false;
        boolean derecho = false;

        float[] ax = j.getAxes();

        float[] old = new float[ax.length];
        if (lastMessage != null) {
            old = lastMessage.getAxes();
        }

        if ((old[4] == ax[4]) && (old[5] ==  ax[5])) {
            // Ejes igual. no hacemos nada
        }
        else {
            if (((ax[4] < 0.5) && (ax[4] > -0.5)) &&
                ((ax[5] < 0.5) && (ax[5] > -0.5))) {
                // Estamos en situacion de parada
                ActionCommand ac = newCommand();
                ac.setCommand(ActionCommand.CMD_SET_ENGINES);
                ac.getEngines().setMotorMode(0);
                ac.getEngines().setLeftEngine(0);
                ac.getEngines().setRightEngine(0);
                sendCommand(ac);
            } else if (ax[5] > 0.5) {
                // Avanzar ambas
                ActionCommand ac = newCommand();
                ac.setCommand(ActionCommand.CMD_SET_ENGINES);
                ac.getEngines().setMotorMode(0);
                ac.getEngines().setLeftEngine(1);
                ac.getEngines().setRightEngine(1);
                sendCommand(ac);
            } else if (ax[4] > 0.5) {
                // Queremos girar a la izquierda
                ActionCommand ac = newCommand();
                ac.setCommand(ActionCommand.CMD_SET_ENGINES);
                ac.getEngines().setMotorMode(0);
                ac.getEngines().setLeftEngine(0);
                ac.getEngines().setRightEngine(1);
                sendCommand(ac);
            } else if (ax[4] < -0.5) {
                // Queremos girar a la derecha
                ActionCommand ac = newCommand();
                ac.setCommand(ActionCommand.CMD_SET_ENGINES);
                ac.getEngines().setMotorMode(0);
                ac.getEngines().setLeftEngine(1);
                ac.getEngines().setRightEngine(0);
                sendCommand(ac);
            } else if (ax[5] < -0.5) {
                // Queremos retroceder
                ActionCommand ac = newCommand();
                ac.setCommand(ActionCommand.CMD_SET_ENGINES);
                ac.getEngines().setMotorMode(0);
                ac.getEngines().setLeftEngine(-1);
                ac.getEngines().setRightEngine(-1);
                sendCommand(ac);
            }


        }

        // Comprobar giro


        // Comprobar leds
        int[] jb = j.getButtons();
        int[] b = new int[jb.length];

        if (lastMessage != null) {
            b = lastMessage.getButtons();
        }


        if (jb[0] > b[0]) {
            // Se ha pulsado el boton disparo
            // Enviamos leds en rojo
            ActionCommand ac = newCommand();
            ac.setCommand(ActionCommand.CMD_SET_LEDS);
            Led led0 = newLed();
            Led led1 = newLed();
            Led led2 = newLed();
            Led led3 = newLed();
            led0.setLedNumber(0);
            led1.setLedNumber(1);
            led2.setLedNumber(2);
            led3.setLedNumber(3);
            led0.setRed(255);
            led1.setRed(255);
            led2.setRed(255);
            led3.setRed(255);
            ac.getLeds().add(led0);
            ac.getLeds().add(led1);
            ac.getLeds().add(led2);
            ac.getLeds().add(led3);
            sendCommand(ac);
        }
        if (jb[0] < b[0]) {
            // Se ha soltado el boton disparo
            // Apagamos
            ActionCommand ac = newCommand();
            ac.setCommand(ActionCommand.CMD_SET_LEDS);
            Led l = newLed();
            l.setRed(0);
            l.setGreen(0);
            l.setBlue(0);
            l.setBlinking(false);
            l.setLedNumber(Led.ALL_LEDS);
            ac.getLeds().add(l);
            sendCommand(ac);
        }

        for (int x = 1; x < 4; x++) {
            if (jb[x] > b[x]) {
                // Se ha pulsado el boton disparo (1-3)
                // Enviamos Todos los leds de ese color
                ActionCommand ac = newCommand();
                ac.setCommand(ActionCommand.CMD_SET_LEDS);
                Led led = newLed();
                led.setLedNumber(Led.ALL_LEDS);
                int r = 0;
                int g = 0;
                int b0 = 0;

                if(x == 1) {
                    r = 255;
                }
                if(x == 2) {
                    g = 255;
                }
                if (x == 3) {
                    b0 = 255;
                }

                led.setRed(r);
                led.setGreen(g);
                led.setBlue(b0);
                ac.getLeds().add(led);
                sendCommand(ac);
            }
            if (jb[x] < b[x]) {
                // Se ha soltado el boton disparo
                // Apagamos
                ActionCommand ac = newCommand();
                ac.setCommand(ActionCommand.CMD_SET_LEDS);
                Led l = newLed();
                l.setRed(0);
                l.setGreen(0);
                l.setBlue(0);
                l.setBlinking(false);
                l.setLedNumber(Led.ALL_LEDS);
                ac.getLeds().add(l);
                sendCommand(ac);
            }
        }

        lastMessage = j;
    }

}
