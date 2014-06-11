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
