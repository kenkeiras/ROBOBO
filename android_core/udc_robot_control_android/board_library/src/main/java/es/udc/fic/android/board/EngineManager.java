package es.udc.fic.android.board;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import geometry_msgs.Twist;
import geometry_msgs.Vector3;


public class EngineManager extends BroadcastReceiver {

    private static final double TOLERANCE = 0.0000001f;

    private double leftSpeed, rightSpeed;

    private Double endTime = null;
    private final Context ctx;

    public EngineManager(Context ctx){
        this.ctx = ctx;
        IntentFilter f = new IntentFilter(BoardConstants.SET_WHEELS_ACTION);
        ctx.registerReceiver(this, f);
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

        // Done with scheduled movement
        if ((endTime != null) && (System.currentTimeMillis() > endTime)){
            endTime = null;
            left = leftSpeed = right = rightSpeed = 0;
            publishSpeeds();
        }

        robotState.setEngines(getWheelState(left, right),
                              getPower(left),
                              getPower(right));
    }


    public void setEngines(double leftEngine, double rightEngine, double distance){
        leftSpeed = leftEngine;
        rightSpeed = rightEngine;
        endTime = (double) System.currentTimeMillis()
                + (distance * 1000) / BoardConstants.SPEED_CONVERSION;
        Log.e("EngineManager", "t=" + (endTime - System.currentTimeMillis()));
    }


    public void setTwist(Twist twist){
        Vector3 linear = twist.getLinear();
        double speed = linear.getX();

        Vector3 angular = twist.getAngular();
        double turn = angular.getY();

        double turnRadius = speed / turn;

        double vLeft = speed - turn * BoardConstants.DISTANCE_TO_AXIS;
        double vRight = speed + turn * BoardConstants.DISTANCE_TO_AXIS;

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

        endTime = null;

        publishSpeeds();
        Log.d(BoardConstants.TAG, "(" + speed + ", " + turn + ") "
              + "-> L: " + leftSpeed + " R: " + rightSpeed);
    }

    private void publishSpeeds() {
        Intent i = new Intent(BoardConstants.UPDATE_BOARD_STATE);
        i.putExtra(BoardConstants.LEFT_WHEEL_UPDATE_KEY, leftSpeed);
        i.putExtra(BoardConstants.RIGHT_WHEEL_UPDATE_KEY, rightSpeed);
        ctx.sendBroadcast(i);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        boolean updates = false;

        if (data.containsKey(BoardConstants.LEFT_WHEEL_UPDATE_KEY)){
            leftSpeed = data.getDouble(BoardConstants.LEFT_WHEEL_UPDATE_KEY);
            updates = true;
        }
        if (data.containsKey(BoardConstants.RIGHT_WHEEL_UPDATE_KEY)){
            rightSpeed = data.getDouble(BoardConstants.RIGHT_WHEEL_UPDATE_KEY);
            updates = true;
        }
        if (data.containsKey(BoardConstants.DISTANCE_UPDATE_KEY)){
            endTime = (double) System.currentTimeMillis()
                    + (data.getDouble(BoardConstants.DISTANCE_UPDATE_KEY) * 1000)
                      / BoardConstants.SPEED_CONVERSION;
            Log.e("EngineManager", "t=" + (endTime - System.currentTimeMillis()));
        }
        else {
            endTime = null;
        }

        if (updates){
            publishSpeeds();
        }
    }
}
