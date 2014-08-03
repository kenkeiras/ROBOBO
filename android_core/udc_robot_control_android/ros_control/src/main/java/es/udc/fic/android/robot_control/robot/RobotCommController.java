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
package es.udc.fic.android.robot_control.robot;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import es.udc.fic.android.robot_control.R;
import es.udc.fic.android.robot_control.UDCAndroidControl;
import es.udc.fic.android.robot_control.PublisherFactory;
import es.udc.fic.android.robot_control.camera.RosCameraPreviewView;
import es.udc.fic.android.robot_control.commands.EngineManager;
import es.udc.fic.android.robot_control.utils.C;
import udc_robot_control_msgs.ActionCommand;

import org.ros.RosCore;
import org.ros.node.NodeMainExecutor;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Service to manage the connection with the robot
 * It opens and handles the connection
 *
 * Created by kerry on 2/06/13.
 */
public class RobotCommController extends Service {

    public UDCAndroidControl androidControl;

    private RobotState robotState;
    private long readSleepTime;
    private ControlThread control;
    private BoardConnector connector;
    private String robotName;
    private URI masterURI;

    private PublisherFactory pf;
    private RobotSensorPublisher rsp;
    private NodeMainExecutor nodeMainExecutor;
    private RosCameraPreviewView rosCameraPreviewView;

    private static final int DEFAULT_MASTER_PORT = 11311;
    private RosCore core = null;
    private boolean createdInitialNodes = false;
    private EngineManager engineManager;

    private final IBinder sBinder = (IBinder) new SimpleBinder();

    public class SimpleBinder extends Binder {
        public RobotCommController getService(){
            return RobotCommController.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d("UDC", "Controller bound: " + this.hashCode());
        return sBinder;
    }

    @Override
    public void onCreate(){
        robotState = new RobotState();
        engineManager = new EngineManager();

        pf = new PublisherFactory();
        pf.setRobotName(robotName);
    }


    private RosCore spawnCoreFromUri(URI masterUri){
        int port = masterUri.getPort();
        if (port < 0){
            port = DEFAULT_MASTER_PORT;
        }
        return RosCore.newPublic(masterUri.getHost(), port);
    }


    private void createInitialNodes(){
        Log.d("UDC", "Creating initial nodes...");

        if ((masterURI == null)
            || (nodeMainExecutor == null)
            || (androidControl == null)){

            Log.d("UDC", "NOT! NodeMainExec=" + nodeMainExecutor + " masterUri=" + masterURI + " androidControl=" + androidControl);
            return;
        }
        Log.d("UDC", "OK");

        createdInitialNodes = true;
        // Configure the initial node. A listener.
        // It's the one which receives instructions from outside
        pf.configureCommandListener(androidControl, nodeMainExecutor);
        pf.configureEngineListener(engineManager, nodeMainExecutor);
        rsp = pf.configureIRSensorPublisher(androidControl, nodeMainExecutor);
        Log.d("UDC", "Let's check...");
        pf.configureCamera(androidControl, nodeMainExecutor,
                           rosCameraPreviewView, 0, 90);
        pf.configureTTS(androidControl, nodeMainExecutor);
    }


    public synchronized void setCameraPreview(RosCameraPreviewView cameraPreview){
        if (rosCameraPreviewView != null){
            rosCameraPreviewView.releaseCamera();
            nodeMainExecutor.shutdownNodeMain(rosCameraPreviewView);

            if (createdInitialNodes){
                pf.configureCamera(androidControl, nodeMainExecutor,
                                   cameraPreview, 0, 90);
            }
        }

        rosCameraPreviewView = cameraPreview;
    }


    /// @TODO Couple this methods?
    public void setNodeMainExecutor(NodeMainExecutor node){
        nodeMainExecutor = node;
        if (!createdInitialNodes){
            createInitialNodes();
        }
    }

    public void setAndroidControl(UDCAndroidControl androidControl){
        this.androidControl = androidControl;
        if (!createdInitialNodes){
            createInitialNodes();
        }
    }

    public void setRobotName(String robotName){
        this.robotName = robotName;
        pf.setRobotName(robotName);
    }

    public synchronized void setMasterUri(URI mu){
        if (mu.equals(masterURI)){
            return;
        }

        if (core != null){
            core.shutdown();
            core = null;
        }

        String host = mu.getHost();
        if ((host != null) && (host.equals("localhost")
                               || host.equals("[::1]")
                               || host.startsWith("127."))){
            core = spawnCoreFromUri(mu);
            core.start();
        }

        masterURI = mu;
        pf.setMasterUri(masterURI);

        if (!createdInitialNodes){
            createInitialNodes();
        }
    }


    public void startListener(ActionCommand actionCommand){
        switch (actionCommand.getPublisher()) {
            case ActionCommand.PUBLISHER_ACCELEROMTER:
                pf.configureAccelerometer(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_AMBIENT_TEMPERATURE:
                pf.configureTemperature(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_GAME_ROTATION_VECTOR:
                pf.configureGameRotationVector(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_GRAVITY:
                pf.configureGravity(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_GYROSCOPE:
                pf.configureGyroscope(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_GYROSCOPE_UNCALIBRATED:
                pf.configureGyroscopeUncalibrated(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_LIGHT:
                pf.configureLight(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_LINEAL_ACCELERATION:
                pf.configureLinearAcceleration(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_MAGNETIC_FIELD:
                pf.configureMagneticField(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_MAGNETIC_FIELD_UNCALIBRATED:
                pf.configureMagneticUncalibrated(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_ORIENTATION:
                pf.configureOrientation(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_PRESSURE:
                pf.configurePressure(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_PROXIMITY:
                pf.configureProximity(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_RELATIVE_HUMIDITY:
                pf.configureRelativeHumidity(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_ROTATION_VECTOR:
                pf.configureRotationVector(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_AUDIO:
                pf.configureAudio(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_BATTERY:
                pf.configureBattery(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_GPS:
                pf.configureNavSatFix(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_IMU:
                pf.configureImu(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_VIDEO:
                int cameraId = actionCommand.getParam0();
                int orientation = actionCommand.getParam1();
                pf.configureCamera(androidControl, nodeMainExecutor, rosCameraPreviewView, cameraId, orientation);
                break;
            default:
                Log.w(C.CMD_TAG, "Publisher desconocido [ " + actionCommand.getPublisher() + " ]");
        }
    }


    public void start(UDCAndroidControl androidControl, Intent intent) {
        setAndroidControl(androidControl);
        Log.i(C.ROBOT_TAG, "Starting the robot controller");
        try {

            if (connector != null) { // Already connected
                Log.w(C.ROBOT_TAG, "Called START with a robot already connected");
                return;
            }
            Log.i(C.ROBOT_TAG, "Creating board connector");
            connector = new BoardConnector();
            Log.i(C.ROBOT_TAG, "Board connector created. Calling connect");
            connector.connect(this, intent);
            Log.i(C.ROBOT_TAG, "The connect call has been successfull");

            Toast.makeText(this, R.string.robot_service_started, Toast.LENGTH_SHORT).show();

            Log.i(C.ROBOT_TAG, "Creating control thread");
            control = new ControlThread(this);

            Log.i(C.ROBOT_TAG, "Launching control thread");
            control.start();
            Log.i(C.ROBOT_TAG, "All threads had been launched. Start completed.");
        }
        catch (Exception ex) {
            Log.w(C.ROBOT_TAG, "Error starting connection [ " + ex.getMessage() + " ] ", ex);
        }
    }

    public void manualStart(UDCAndroidControl androidControl) {
        setAndroidControl(androidControl);
        try {
            Log.v("UDC", "Manually starting, continued? " + (connector != null));
            if (connector != null) { // Already connected
                Log.w(C.ROBOT_TAG, "Called START with a robot already connected");
                return;
            }
            Log.i(C.ROBOT_TAG, "Manually starting controller.");
            connector = new BoardConnector();
            connector.manualConnect(androidControl);
            control = new ControlThread(this);
            control.start();
        }
        catch (Exception ex) {
            Log.w(C.ROBOT_TAG, "Error staring connection [ " + ex.getMessage() + " ] ", ex);
        }
    }


    public void stop() {
        if (connector != null) {
            connector.disconnect();
        }
        connector = null;
    }


    public synchronized boolean continueControl(ControlThread hl) {
        return ((connector != null) && (control == hl));
    }


    public synchronized  byte[] read() {
        return connector.read();
    }

    public long getReadSleepTime() {
        return readSleepTime;
    }

    public void setReadSleepTime(long readSleepTime) {
        this.readSleepTime = readSleepTime;
    }

    public void write(ActionCommand command) {
        try {
            switch (command.getCommand()) {
                case ActionCommand.CMD_HARD_RESET:
                    stop();
                    manualStart(androidControl);
                    break;
                case ActionCommand.CMD_RESET:
                    robotState.reset();
                    connector.write(robotState);
                    break;
                case ActionCommand.CMD_SET_LEDS:
                    robotState.setLeds(command.getLeds());
                    connector.write(robotState);
                    break;
            }
        }
        catch (Exception ex) {
            if (command != null) {
                Log.e(C.ROBOT_TAG, "Error running command [ " + command.getCommand() + " ]", ex);
            }
            else {
                Log.e(C.ROBOT_TAG, "Error running command. The command is null", ex);
            }
        }
    }

    public void sendToRos(SensorInfo ss) {
        rsp.sendInfo(ss);
    }

    public void refreshRobot(){
        engineManager.refresh(robotState);
        connector.write(robotState);
    }

    public void stop(ActionCommand actionCommand){
        pf.stopPublisher(nodeMainExecutor, actionCommand.getPublisher());
    }

}


class ControlThread extends Thread {

    RobotCommController parent;

    public ControlThread (RobotCommController parent) {
        this.parent = parent;
    }


    @Override
    public void run() {
        while (parent.continueControl(this)) {
            // Commands have to be written for sensor data to be sent back
            writeCommands();
            readData();
        }
    }


    private void  writeCommands(){
        parent.refreshRobot();
    }

    private void readData(){
        try {
            Log.d(C.ROBOT_TAG, "Reading sensors");
            byte[] read = parent.read();
            if (read != null) {
                Log.i(C.ROBOT_TAG, "Read [ " + read.length + " ] bytes");
                try {
                    // Parse the read data
                    SensorInfo info = new SensorInfo(read);
                    parent.sendToRos(info);

                } catch (Exception e) {
                    Log.w("Error retrieving data", e);
                    StringBuilder sb = new StringBuilder();
                    for (int x = 0; x < read.length; x++) {
                        sb.append("byte [ " + x + " ] = (" + read[x] + ")");
                    }
                    Log.w(C.ROBOT_TAG, "Read => " + sb.toString());
                }
            }
            else {
                Log.i(C.ROBOT_TAG, "Nothing to read");
            }
            try {
                sleep(parent.getReadSleepTime());
            }
            catch (InterruptedException ie) {
                Log.w(C.ROBOT_TAG, "InterruptedException in the reader");
            }
        }
        catch (Exception ex) {
            Log.w(C.ROBOT_TAG, "Exception in the control thread", ex);
        }
    }
}
