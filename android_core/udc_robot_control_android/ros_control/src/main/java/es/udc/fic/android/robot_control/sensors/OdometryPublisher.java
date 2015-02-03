package es.udc.fic.android.robot_control.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.rosjava_geometry.Vector3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import es.udc.fic.android.robot_control.commands.EngineManager;
import es.udc.fic.android.robot_control.robot.RobotState;
import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constants;
import geometry_msgs.Twist;
import nav_msgs.Odometry;

public class OdometryPublisher extends BroadcastReceiver implements NodeMain {

    private static final String QUEUE_NAME = Constants.TOPIC_ODOMETRY;
    private static final long SLEEP_TIME = 100; // Miliseconds between each publication
    private final Context ctx;
    private final IntentFilter boardIntentFilter;
    private final String robotName;

    private Long lastUpdateTime = null; // Last time speeds had been set, in miliseconds
    private double leftSpeed = 0.0f,
                   rightSpeed = 0.0f;

    Lock lock = new ReentrantLock();

    private double angle = 0.0f;
    private double pos_x = 0.0f,
                   pos_y = 0.0f;
    private Publisher<Object> publisher = null;
    private double speed;
    private double turn;
    private Thread publisherThread = null;

    public OdometryPublisher(Context ctx, String robotName){
        this.ctx = ctx;
        this.robotName = robotName;

        // Board sensors
        boardIntentFilter = new IntentFilter(RobotState.UPDATE_BOARD_STATE);
        ctx.registerReceiver(this, boardIntentFilter);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();

        if (boardIntentFilter.hasAction(intent.getAction())){
            lock.lock();
            if (data.containsKey(EngineManager.LEFT_WHEEL_UPDATE_KEY)){
                leftSpeed = data.getDouble(EngineManager.LEFT_WHEEL_UPDATE_KEY);
            }
            if (data.containsKey(EngineManager.RIGHT_WHEEL_UPDATE_KEY)){
                rightSpeed = data.getDouble(EngineManager.RIGHT_WHEEL_UPDATE_KEY);
            }
            lock.unlock();
        }
    }


    private void publishOdom() {
        if (publisher == null) return;

        // Speed
        Odometry odom = (Odometry) publisher.newMessage();
        odom.getTwist().getTwist().getLinear().setX(speed);
        odom.getTwist().getTwist().getAngular().setY(turn);

        // Position
        odom.getPose().getPose().getPosition().setX(pos_x);
        odom.getPose().getPose().getPosition().setY(pos_y);

        // Angle
        odom.getPose().getPose().getOrientation().setY(angle);
        publisher.publish(odom);
    }


    private synchronized void computeOdom() {
        long currentTime = System.currentTimeMillis();

        if (lastUpdateTime != null){
            double timeInc = ((double)(currentTime - lastUpdateTime)) / 1000.0f;

            // Inverse of the EngineManager.setTwist() operations
            speed = (rightSpeed + leftSpeed) / 2;
            turn = (rightSpeed - speed) / EngineManager.DISTANCE_TO_AXIS;

            // Update the angle according to the turn speed
            angle += turn * timeInc;

            // Update the position according to the speed and angle
            pos_x += speed * Math.cos(angle) * timeInc;
            pos_y += speed * Math.sin(angle) * timeInc;
        }

        lastUpdateTime = currentTime;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(C.DefaultBaseNodeName + "/" + QUEUE_NAME);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(robotName + "/" + QUEUE_NAME, Odometry._TYPE);

        // Compute and publish odometry on a regular basis
        publisherThread = new Thread(){
            public void run() {
                try {
                    while (!interrupted()){
                        lock.lock();
                        computeOdom();
                        publishOdom();
                        lock.unlock();
                        Thread.sleep(SLEEP_TIME);
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        publisherThread.start();
    }

    @Override
    public void onShutdown(Node node) {
        if (publisherThread != null) {
            publisherThread.interrupt();
        }
        ctx.unregisterReceiver(this);
        publisher.shutdown();
    }

    @Override
    public void onShutdownComplete(Node node) {

    }

    @Override
    public void onError(Node node, Throwable throwable) {

    }
}
