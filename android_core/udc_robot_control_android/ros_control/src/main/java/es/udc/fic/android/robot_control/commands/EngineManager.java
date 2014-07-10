package es.udc.fic.android.robot_control.commands;

import es.udc.fic.android.robot_control.robot.EstadoRobot;

import geometry_msgs.Twist;
import geometry_msgs.Vector3;


public class EngineManager {

    private double speed_x, speed_y, speed_z;
    private double turn_x, turn_y, turn_z;


    public EngineManager(){
        reset();
        speed_x = 0.5f;
        turn_x = 0.0f;
    }


    public void reset(){
        speed_x = speed_y = speed_z = 0;
        turn_x = turn_y = turn_z = 0;
    }


    public void refresh(EstadoRobot robotState){
        byte runningLeft = (byte) (speed_x >= 0.5f? 1 : 0);
        byte runningRight = runningLeft;

        if (turn_y >= 0.5f){
            runningRight = 0;
        }
        else if (turn_y <= -0.5f){
            runningLeft = 0;
        }

        robotState.setEngines(runningLeft, runningRight);
    }


    public void setTwist(Twist twist){
        Vector3 linear = twist.getLinear();
        speed_x = linear.getX();
        speed_y = linear.getY();
        speed_z = linear.getZ();

        Vector3 angular = twist.getAngular();
        turn_x = angular.getX();
        turn_y = angular.getY();
        turn_z = angular.getZ();

    }
}
