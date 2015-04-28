package es.udc.fic.android.board.utils;

import es.udc.fic.android.board.RobotState;
import udc_robot_control_msgs.Led;

public class BoardMessageBuilder {

    /**
     * This method generates the message to send
     * @return
     */
    public static byte[] message(RobotState robotState) {
        byte[] out = new byte[31];

        byte[] rightBytes = engineIntToBytes(robotState.rightEngine);
        byte[] leftBytes = engineIntToBytes(robotState.leftEngine);

        int pos = 0;
        out[0] = 0x37;
        out[1] = robotState.engineMode;
        out[2] = robotState.wheelState;
        out[3] = leftBytes[0];  // Left wheel, hight byte
        out[4] = leftBytes[1];  // Left wheel, low byte
        out[5] = rightBytes[0]; // Right wheel, hight byte
        out[6] = rightBytes[1]; // Right wheel, low byte

        try {
            Led led = robotState.leds[0];
            out[7] = (byte) led.getRed();
            out[8] = (byte) led.getGreen();
            out[9] = (byte) led.getBlue();
        }
        catch (NullPointerException e){
            out[7] = 0;
            out[8] = 0;
            out[9] = 0;
        }

        // Checksum
        out[10] = checksum(out, 1, 10);
        return out;
    }

    private static byte checksum(byte[] buff, int from, int limit){
        byte sum = 0;
        for (int i = from; i < limit; i++){
            sum += buff[i];
        }

        return sum;
    }


    private static byte[] engineIntToBytes(int motorValue) {
        byte low = (byte) motorValue;
        byte high = (byte) (motorValue >> 8);
        byte[] output = {high, low};
        return output;
    }
}
