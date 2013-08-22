package es.udc.robot_control.gui;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 2/08/13
 * Time: 20:11
 * To change this template use File | Settings | File Templates.
 */
public class BaseComponent {

    private MainControlPanel padre;

    public void setPadre(MainControlPanel p) {
        padre = p;
    }

    public MainControlPanel getPadre() {
        return padre;
    }

}
