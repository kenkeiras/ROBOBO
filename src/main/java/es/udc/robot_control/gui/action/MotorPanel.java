package es.udc.robot_control.gui.action;

import es.udc.robot_control.gui.BaseComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 2/08/13
 * Time: 19:49
 * To change this template use File | Settings | File Templates.
 */
public class MotorPanel extends BaseComponent {
    private JCheckBox cbMotorIzquierdo;
    private JCheckBox cbMotorDerecho;
    private JSlider sliderSpeed;
    private JButton btnEnviar;
    private JButton btnDetener;
    private JPanel panelMotores;

    public MotorPanel() {
        btnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPadre().enviarMotores(cbMotorIzquierdo.isSelected(), cbMotorDerecho.isSelected(), sliderSpeed.getValue());
            }
        });
        btnDetener.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPadre().detenerMotores();
            }
        });
    }
}
