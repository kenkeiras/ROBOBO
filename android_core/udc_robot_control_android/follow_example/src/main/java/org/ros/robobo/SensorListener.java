package org.ros.robobo;

import org.ros.message.MessageListener;
import org.ros.node.NodeMainExecutor;

import udc_robot_control_msgs.SensorStatus;

class SensorListener implements MessageListener<SensorStatus> {

    private NodeMainExecutor nodeMainExecutor;
    private static final int IR_THRESHOLD = 0x0FFF;

    public SensorListener(NodeMainExecutor nodeMainExecutor) {
        System.out.println("Listening...");
        this.nodeMainExecutor = nodeMainExecutor;
    }


    @Override
    public void onNewMessage(SensorStatus actionCommand) {
        System.out.println("IR0 = " + actionCommand.getSIr0());
        System.out.println("IR1 = " + actionCommand.getSIr1());
        System.out.println("IR2 = " + actionCommand.getSIr2());
        System.out.println("IR3 = " + actionCommand.getSIr3());
        System.out.println("IR4 = " + actionCommand.getSIr4());
        System.out.println("IR5 = " + actionCommand.getSIr5());
        System.out.println("IR6 = " + actionCommand.getSIr6());
        System.out.println("IR7 = " + actionCommand.getSIr7());
        System.out.println("IR8 = " + actionCommand.getSIr8());
        System.out.println("");

        boolean goLeft = actionCommand.getSIr1() < IR_THRESHOLD;
        boolean goRight = actionCommand.getSIr2() < IR_THRESHOLD;
        System.out.println("<" + goLeft + " - " + goRight + ">");

        Robobo.setEngines(goLeft, goRight);
    }
}
