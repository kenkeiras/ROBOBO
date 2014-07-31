package es.udc.fic.android.robot_control.commands;

import android.util.Log;
import es.udc.fic.android.robot_control.robot.RobotState;

import geometry_msgs.Twist;
import geometry_msgs.Vector3;


public class EngineManager {

    private double speed_x, speed_y, speed_z;
    private double turn_x, turn_y, turn_z;


    public EngineManager(){
        reset();
        speed_x = 0.0f;
        turn_x = 0.0f;
    }


    public void reset(){
        speed_x = speed_y = speed_z = 0;
        turn_x = turn_y = turn_z = 0;
    }


    public void refresh(RobotState robotState){
        byte runningLeft = 0;
        byte runningRight = 0;

        if (speed_x >= 0.5f){
            runningLeft = 1;
            runningRight = 1;
        }
        else if (speed_x <= -0.5){
            runningLeft = 2;
            runningRight = 2;
        }

        if (Math.abs(speed_x) >= 0.5f){
            if (turn_y >= 0.5f){
                runningRight = 2;
                runningLeft = 1;
            }
            else if (turn_y <= -0.5f){
                runningRight = 1;
                runningLeft = 2;
            }
        }

        robotState.setEngines(runningLeft, runningRight);
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
