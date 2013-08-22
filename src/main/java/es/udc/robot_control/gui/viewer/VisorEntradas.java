package es.udc.robot_control.gui.viewer;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 2/08/13
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */
public class VisorEntradas {
    private JTextArea datos;
    private JPanel VisorTxt;



    public void showSending(String msg) {
        datos.append(String.format("SENDING [ %s ]%n", msg));
    }

    public void showReceivedMsg(String msg) {
        datos.append(String.format("Received [ %s ]%n", msg));
    }
}
