package es.udc.fic.android.robot_control.commands;

import android.util.Log;
import es.udc.fic.android.robot_control.robot.RobotState;

import geometry_msgs.Twist;
import geometry_msgs.Vector3;


public class EngineManager {

    private double speed_x, speed_y, speed_z;
    private double turn_x, turn_y, turn_z;

    private final static double TOLERANCE = 0.0000001f;

    public EngineManager(){
        reset();
        speed_x = 0.0f;
        turn_x = 0.0f;
    }


    public void reset(){
        speed_x = speed_y = speed_z = 0;
        turn_x = turn_y = turn_z = 0;
    }


    double getLeftRotationSpeed(){
        if (turn_y > -TOLERANCE){
            return speed_x;
        }
        else {
            return Math.max(-1, speed_x + 2 * turn_y);
        }
    }


    double getRightRotationSpeed(){
        if (turn_y < TOLERANCE){
            return speed_x;
        }
        else {
            return Math.max(-1, speed_x - 2 * turn_y);
        }
    }


    byte getWheelState(double left, double right){
        // Left stopped
        if (Math.abs(left) < TOLERANCE){
            if (Math.abs(right) < TOLERANCE){ // Right stopped
                return (byte) 0x0; // 0000
            }
            if (right > 0){ // Right forward
                return (byte) 0x1; // 0001
            }
            if (right < 0){ // Right reverse
                return (byte) 0x3; // 0011
            }
        }
        // Left forward
        if (left > 0){
            if (Math.abs(right) < TOLERANCE){ // Right stopped
                return (byte) 0x4; // 0100
            }
            if (right > 0){ // Right forward
                return (byte) 0x5; // 0101
            }
            if (right < 0){ // Right reverse
                return (byte) 0xB; // 1011
            }
        }
        // Left reverse
        if (left < 0){
            if (Math.abs(right) < TOLERANCE){ // Right stopped
                return (byte) 0x6; // 0110
            }
            if (right > 0){ // Right forward
                return (byte) 0xA; // 1010
            }
            if (right < 0){ // Right reverse
                return (byte) 0x7; // 0111
            }
        }

        throw new RuntimeException("This shouldn't be reached");
    }


    int getPower(double engineValue){

        engineValue = Math.abs(engineValue);
        if (engineValue < TOLERANCE){
            return 0;
        }

         // max: 0.020
         // min: 0.764

        double power = ((1 - engineValue) * 0.762) + 0.02;

        power = Math.min(0.764, Math.max(0.02, power));

        return (int) Math.round(power * 65535);
    }


    public void refresh(RobotState robotState){

        double left = getLeftRotationSpeed();
        double right = getRightRotationSpeed();

        Log.d("UDC_EngineManager", "Left : " + left + " -> " + getPower(left));
        Log.d("UDC_EngineManager", "Right: " + right + " -> " + getPower(right));

        robotState.setEngines(getWheelState(left, right),
                              getPower(left),
                              getPower(right));
    }


    public void setTwist(Twist twist){
        System.out.print("(" + speed_x + ", " + turn_y + ") -> ");

        Vector3 linear = twist.getLinear();
        speed_x = linear.getX();
        speed_y = linear.getY();
        speed_z = linear.getZ();

        Vector3 angular = twist.getAngular();
        turn_x = angular.getX();
        turn_y = angular.getY();
        turn_z = angular.getZ();

        System.out.println("(" + speed_x + ", " + turn_y + ")");
    }
}
