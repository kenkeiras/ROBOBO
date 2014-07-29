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
 * This class is a decorator which adds the needed functionality to implement
 * the leds blinking control.
 *
 */

public class BlinkingRobotControl extends AbstractRobotControl implements RosListener {

    private AbstractRobotControl decorated;

    private long onTime;
    private long offTime;
    private boolean currentOn;
    private Vector<Led> stateStorage;

    private Timer timer;

    /**
     * Constructor.
     * @param decor Controller to decorate
     * @param onInterval time in ms to be on
     * @param offInterval time in ms to be off
     */
    public BlinkingRobotControl(AbstractRobotControl decorated, long onInterval, long offInterval) {

        this.decorated = decorated;
        onTime = onInterval;
        offTime = offInterval;
        currentOn = false;
        stateStorage = new Vector<Led>();
        timer = new Timer(true);
        // BlinkingRobotControl acts as the notifier
        decorated.registerNotifier(this);
        sendOn();
    }

    /**
     * Decorated method. Sends the command to the decorated HeadlessRobotControl
     * and retains the led state if needed.
     * @param msg
     */
    @Override
    public void sendCommand(ActionCommand msg) {
        switch(msg.getCommand()) {
            case ActionCommand.CMD_SET_LEDS:
                processLeds(msg.getLeds());
        }
        decorated.sendCommand(msg);
    }



    @Override
    public GraphName getDefaultNodeName() {
        return decorated.getDefaultNodeName();
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        decorated.onStart(connectedNode);
    }

    @Override
    public void onShutdown(Node node) {
        decorated.onShutdown(node);
    }

    @Override
    public void onShutdownComplete(Node node) {
        decorated.onShutdownComplete(node);
    }


    /**
     * As a decorator interface. Pass the messages to the listener
     * @param message
     */
    @Override
    public void onMsgArrived(Message message) {
        super.notifyMsg(message);
    }

    @Override
    public void onError(Node node, Throwable throwable) {
        decorated.onError(node, throwable);
    }

    @Override
    public void notifyMsg(Message msg) {
        decorated.notifyMsg(msg);
    }

    @Override
    public ActionCommand newCommand() {
        return decorated.newCommand();
    }

    @Override
    public Led newLed() {
        return decorated.newLed();
    }

    @Override
    public String getRobotName() {
        return decorated.getRobotName();
    }

    @Override
    public void setRobotName(String robotName) {
        decorated.setRobotName(robotName);
    }



    private void processLeds(List<Led> leds) {
        for (Led l:leds) {
            processOneLed(l);
        }
    }

    private void processOneLed(Led led) {
        // Restart the state on ALL LEDS
        if (Led.ALL_LEDS == led.getLedNumber()) {
            stateStorage.removeAllElements();
        }
        else {
            // If it's a specific led, remove it from the list
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
            System.out.println("Saving led [ " + led.getLedNumber() + " ] as blinking ( " + led.getRed() + "," + led.getGreen() + "," + led.getBlue() + ")");
            // If the led is blinking, add it to the list
            stateStorage.addElement(led);
        }
    }



    /**
     * Send a 'turn on' message to all the leds on the list
     */
    void sendOn() {
        if (stateStorage.size() > 0) {
            ActionCommand ac = newCommand();
            ac.setCommand(ActionCommand.CMD_SET_LEDS);
            ac.getLeds().addAll(stateStorage);
            // Send it directly through the decorated class to avoid
            // entering an infinite loop
            decorated.sendCommand(ac);
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
     * Send a 'turn off' message to all the leds on the list
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
            decorated.sendCommand(ac);
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
