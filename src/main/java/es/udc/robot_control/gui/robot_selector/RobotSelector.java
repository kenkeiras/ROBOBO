package es.udc.robot_control.gui.robot_selector;

import es.udc.robot_control.gui.BaseComponent;
import es.udc.robot_control.gui.MainControlPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 2/08/13
 * Time: 18:18
 * To change this template use File | Settings | File Templates.
 */
public class RobotSelector extends BaseComponent {
    private JComboBox comboRobotSelector;
    private JButton seleccionarButton;
    private JPanel panelSelector;
    private JTextField tfUrl;
    private JButton startMasterButton;

    private MainControlPanel padre;

    public RobotSelector() {
        seleccionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Robot seleccionado");
                getPadre().connectRobot((String) comboRobotSelector.getSelectedItem(), tfUrl.getText());
            }
        });
        startMasterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Arrancando/Parando roscore");
                String uri = getPadre().startStopMaster();
                if (uri != null) {
                    setUrl(uri);
                    tfUrl.setEnabled(false);
                    startMasterButton.setText("Stop Master");
                }
                else {
                    setUrl("");
                    tfUrl.setEnabled(true);
                    startMasterButton.setText("Start Master");
                }
            }
        });


    }

    public void setUrl(String u) {
        tfUrl.setText(u);
    }

}
