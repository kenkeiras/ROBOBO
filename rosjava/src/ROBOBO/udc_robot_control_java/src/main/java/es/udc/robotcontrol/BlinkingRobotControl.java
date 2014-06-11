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

import org.ros.internal.message.Message;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import udc_robot_control_msgs.ActionCommand;
import udc_robot_control_msgs.Led;

/**
 *
 * Esta clase es un decorador que añade la funcionalidad necesaria para implementar un controlador de parpadeos de
 * los leds.
 *
 */

public class BlinkingRobotControl extends AbstractRobotControl implements RosListener {

    private AbstractRobotControl decorado;

    private long onTime;
    private long offTime;
    private boolean currentOn;
    private Vector<Led> stateStorage;

    private Timer timer;

    /**
     * Constructor.
     * @param decor Controler a decorar
     * @param onInterval intervalo en ms para tiempo encendido
     * @param offInterval intervalo en ms para tiempo apagado
     */
    public BlinkingRobotControl(AbstractRobotControl decor, long onInterval, long offInterval) {

        decorado = decor;
        onTime = onInterval;
        offTime = offInterval;
        currentOn = false;
        stateStorage = new Vector<Led>();
        timer = new Timer(true);
        // Somos el interfaz del decorado. Las notificaciones pasan por nosotros.
        decorado.registerNotificador(this);
        sendOn();
    }

    /**
     * Metodo decorado. Envia el comando al HeadlessRobotControl decorado y retiene el estado de los
     * leds si es preciso.
     * @param msg
     */
    @Override
    public void sendCommand(ActionCommand msg) {
        switch(msg.getCommand()) {
            case ActionCommand.CMD_SET_LEDS:
                processLeds(msg.getLeds());
        }
        decorado.sendCommand(msg);
    }



    @Override
    public GraphName getDefaultNodeName() {
        return decorado.getDefaultNodeName();
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        decorado.onStart(connectedNode);
    }

    @Override
    public void onShutdown(Node node) {
        decorado.onShutdown(node);
    }

    @Override
    public void onShutdownComplete(Node node) {
        decorado.onShutdownComplete(node);
    }


    /**
     * Somos el interfaz del decorado. Notificamos los mensajes a nuestro listener
     * @param message
     */
    @Override
    public void onMsgArrived(Message message) {
        super.notifyMsg(message);
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



    private void processLeds(List<Led> leds) {
        for (Led l:leds) {
            processOneLed(l);
        }
    }

    private void processOneLed(Led led) {
        // Si viene un ALL LEDS hay que eliminar el estado
        if (Led.ALL_LEDS == led.getLedNumber()) {
            stateStorage.removeAllElements();
        }
        else {
            // Si viene un led concrecto hay que quitarlo de la lista
            Led toRemove = null;
            for(Led mLed: stateStorage) {
                if (mLed.getLedNumber() == led.getLedNumber()) {
                    toRemove = mLed;
                    break;
                }
            }
            stateStorage.remove(toRemove);
        }

        if (led.getBlinking()) {
            System.out.println("Guardando led [ " + led.getLedNumber() + " ] as blinking ( " + led.getRed() + "," + led.getGreen() + "," + led.getBlue() + ")");
            // Si el led está parpadeando lo ponemos en la lista
            stateStorage.addElement(led);
        }
    }



    /**
     * Enviar un mensaje de on a todos los leds de la lista
     */
    void sendOn() {
        if (stateStorage.size() > 0) {
            ActionCommand ac = newCommand();
            ac.setCommand(ActionCommand.CMD_SET_LEDS);
            ac.getLeds().addAll(stateStorage);
            // Enviamos directamente a traves del decorado para no entrar en un bucle infinito.
            decorado.sendCommand(ac);
        }
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                sendOff();
            }
        };
        timer.schedule(tt, onTime);
    }

    /**
     * Enviar un mensaje de off a todos los leds de la lista
     */
    void sendOff() {
        if (stateStorage.size() > 0) {
            ActionCommand ac = newCommand();
            ac.setCommand(ActionCommand.CMD_SET_LEDS);
            for (Led l : stateStorage) {
                Led ll = newLed();
                ll.setLedNumber(l.getLedNumber());
                ll.setBlinking(l.getBlinking());
                ll.setRed(0);
                ll.setGreen(0);
                ll.setBlue(0);
                ac.getLeds().add(ll);
            }
            decorado.sendCommand(ac);
        }

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                sendOn();
            }
        };
        timer.schedule(tt, offTime);
    }




}
