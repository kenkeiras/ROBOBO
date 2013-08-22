package es.udc.robot_control.gui.action;

import es.udc.robot_control.gui.BaseComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 2/08/13
 * Time: 18:32
 * To change this template use File | Settings | File Templates.
 */
public class LedPanel extends BaseComponent {
    private JPanel ledPanel;
    private JCheckBox cbRed;
    private JCheckBox cbGreen;
    private JCheckBox cbBlue;
    private JCheckBox cbBlinking;
    private JComboBox cbLeds;
    private JButton enviarButton;

    public LedPanel() {
        enviarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getPadre().enviarLeds(cbLeds.getSelectedIndex(), cbRed.isSelected(), cbGreen.isSelected(), cbBlue.isSelected(), cbBlinking.isSelected());
            }
        });
    }
}
