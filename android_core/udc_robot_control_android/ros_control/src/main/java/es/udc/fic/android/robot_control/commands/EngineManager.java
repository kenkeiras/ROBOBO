package es.udc.fic.android.robot_control.commands;

import android.util.Log;
import es.udc.fic.android.robot_control.robot.RobotState;
import es.udc.fic.android.robot_control.utils.C;

import geometry_msgs.Twist;
import geometry_msgs.Vector3;


public class EngineManager {

    private double leftSpeed, rightSpeed;

    private final static double DISTANCE_TO_AXIS = 0.045f; // 4,5cm
    private final static double TOLERANCE = 0.0000001f;

    public EngineManager(){
        reset();
    }


    public void reset(){
        leftSpeed = rightSpeed = 0.0f;
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

        double left = leftSpeed;
        double right = rightSpeed;

        Log.d("UDC_EngineManager", "Left : " + left + " -> " + getPower(left));
        Log.d("UDC_EngineManager", "Right: " + right + " -> " + getPower(right));

        robotState.setEngines(getWheelState(left, right),
                              getPower(left),
                              getPower(right));
    }


    public void setTwist(Twist twist){
        Vector3 linear = twist.getLinear();
        double speed = linear.getX();

        Vector3 angular = twist.getAngular();
        double turn = angular.getY();

        double turnRadius = speed / turn;

        double vLeft = speed - turn * DISTANCE_TO_AXIS;
        double vRight = speed + turn * DISTANCE_TO_AXIS;

        leftSpeed = vLeft / 1;
        rightSpeed = vRight / 1;

        // Keep it in range, while mantaining the course
        if (Math.abs(leftSpeed) > 1){
            rightSpeed /= Math.abs(leftSpeed);
            leftSpeed = 1;
        }
        if (Math.abs(rightSpeed) > 1){
            leftSpeed /= Math.abs(rightSpeed);
            rightSpeed = 1;
        }

        Log.d(C.TAG, "(" + speed + ", " + turn + ") "
              + "-> L: " + leftSpeed + " R: " + rightSpeed);
    }
}
