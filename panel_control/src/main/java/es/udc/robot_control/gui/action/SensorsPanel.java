/*
 * Copyright (C) 2013 Amancio Díaz Suárez
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package es.udc.robot_control.gui.action;

import es.udc.robot_control.gui.BaseComponent;
import udc_robot_control_java.ActionCommand;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 2/08/13
 * Time: 20:29
 * To change this template use File | Settings | File Templates.
 */
public class SensorsPanel extends BaseComponent {
    private JComboBox cbSensores;
    private JButton btnActivar;
    private JButton btnDesactivar;
    private JPanel panelSensores;

    public SensorsPanel() {



        btnActivar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                getPadre().activarSensor((SensorModel) cbSensores.getSelectedItem());
            }
        });
        btnDesactivar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                getPadre().desactivarSensor((SensorModel) cbSensores.getSelectedItem());
            }
        });
    }

    private void createUIComponents() {


        cbSensores = new JComboBox();

        DefaultComboBoxModel<SensorModel> model = new DefaultComboBoxModel<SensorModel>();


        model.addElement(new SensorModel(ActionCommand.PUBLISHER_BATERY, "Batería"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_GPS, "GPS"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_IMU, "IMU"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_ACCELEROMTER, "Accelerometer"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_MAGNETIC_FIELD, "Magnetic Field"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_GYROSCOPE, "Gyroscope"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_LIGHT, "Light"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_PRESSURE, "Pressure"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_PROXIMITY, "Proximity"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_GRAVITY, "Gravity"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_LINEAL_ACCELERATION, "Lineal Acceleration"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_ROTATION_VECTOR, "Rotation Vector"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_ORIENTATION, "Orientation"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_RELATIVE_HUMIDITY, "Relative Humidity"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_AMBIENT_TEMPERATURE, "Ambient Temperature"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_MAGNETIC_FIELD_UNCALIBRATED, "Magnetic Field (Uncalibrated)"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_GAME_ROTATION_VECTOR, "Game Rotation Vector"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_GYROSCOPE_UNCALIBRATED, "Gyroscore (Uncalibrated)"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_AUDIO, "Audio"));
        model.addElement(new SensorModel(ActionCommand.PUBLISHER_VIDEO, "Vídeo"));

        cbSensores.setModel(model);


    }
}
