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

package es.udc.robotcontrol;

import org.ros.message.Time;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import udc_robot_control_java.ActionCommand;


/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 4/08/13
 * Time: 11:16
 * To change this template use File | Settings | File Templates.
 */
public class CommandsPublisher {
    private HeadlessRobotControl padre;

    private Publisher<ActionCommand> publisher;


    public CommandsPublisher(HeadlessRobotControl padre) {
        super();
        this.padre = padre;
    }

    public void conectar(ConnectedNode cn, String tn) {
        try {
            publisher = cn.newPublisher(tn, ActionCommand._TYPE);
        }
        catch (Exception ex) {
            // TODO: Manejar la excepcion
            ex.printStackTrace();
        }
    }

    public void desconectar() {
        publisher.shutdown();
    }

    public ActionCommand newMsg() {
        return publisher.newMessage();
    }

    public void publicar(ActionCommand comando) {
        comando.getHeader().setFrameId(padre.getRobotName());
        comando.getHeader().setStamp(Time.fromMillis(System.currentTimeMillis()));
        publisher.publish(comando);
    }
}
