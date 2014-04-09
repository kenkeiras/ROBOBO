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
