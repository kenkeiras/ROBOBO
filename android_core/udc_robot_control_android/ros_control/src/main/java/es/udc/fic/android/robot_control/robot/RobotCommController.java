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
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import es.udc.fic.android.board.BoardService;
import es.udc.fic.android.board.SensorInfo;
import es.udc.fic.android.board.SensorInfoHandler;
import es.udc.fic.android.robot_control.UDCAndroidControl;
import es.udc.fic.android.robot_control.PublisherFactory;
import es.udc.fic.android.robot_control.camera.RosCameraPreviewView;
import es.udc.fic.android.robot_control.screen.InfoActivity;
import es.udc.fic.android.robot_control.sensors.RobotSensorPublisher;
import es.udc.fic.android.robot_control.utils.C;
import udc_robot_control_msgs.ActionCommand;

import org.ros.RosCore;
import org.ros.node.NodeMainExecutor;

import java.net.URI;

/**
 * Service to manage the connection with the robot
 * It opens and handles the connection
 *
 * Created by kerry on 2/06/13.
 */
public class RobotCommController extends Service implements SensorInfoHandler {

    public UDCAndroidControl androidControl;

    private String robotName;
    private URI masterURI;

    private PublisherFactory pf;
    private RobotSensorPublisher rsp;
        private NodeMainExecutor nodeMainExecutor;
    private RosCameraPreviewView rosCameraPreviewView;

    private static final int DEFAULT_MASTER_PORT = 11311;
    private RosCore core = null;
    private boolean createdInitialNodes = false;
    private String lastInfo = null;

    private BoardService boardService = null;

    private ServiceConnection boardConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            boardService = ((BoardService.SimpleBinder) service).getService();
            boardService.addCallbackTo(RobotCommController.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            boardService = null;
        }
    };

    private final IBinder sBinder = (IBinder) new SimpleBinder();

    public class SimpleBinder extends Binder {
        public RobotCommController getService(){
            return RobotCommController.this;
        }
    }

    public String getLastInfo(){
        return lastInfo;
    }

    public void setLastInfo(String info){
        lastInfo = info;
        sendBroadcast(new Intent(InfoActivity.NEW_INFO_TAG));
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d("UDC", "Controller bound: " + this.hashCode());
        return sBinder;
    }

    @Override
    public void onCreate(){
        pf = new PublisherFactory();
        pf.setRobotName(robotName);

        // Connect to the board service
        Intent boardIntent = new Intent(this, BoardService.class);
        startService(boardIntent);
        bindService(boardIntent, boardConn, 0);
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
        pf.configureEngineListener(boardService, nodeMainExecutor);
        rsp = pf.configureIRSensorPublisher(androidControl, nodeMainExecutor);
        Log.d("UDC", "Let's check...");
        pf.configureOdometry(androidControl, nodeMainExecutor);
        pf.configureTTS(androidControl, nodeMainExecutor);
        pf.configureScreenListener(this, nodeMainExecutor);
        pf.configureSpeechRecognition(this, nodeMainExecutor);

        if (rosCameraPreviewView != null){
            pf.configureCamera(androidControl, nodeMainExecutor,
                               rosCameraPreviewView, 0, 90);
        }
    }


    public synchronized void setCameraPreview(RosCameraPreviewView newCameraPreview){
        Log.d("UDC", "Set Camera Preview " + rosCameraPreviewView + " -> " + newCameraPreview);

        if (rosCameraPreviewView != null){
            rosCameraPreviewView.releaseCamera();
            if (nodeMainExecutor != null) {
                nodeMainExecutor.shutdownNodeMain(rosCameraPreviewView);
            }
        }

        if (createdInitialNodes && (newCameraPreview != null)){
            pf.configureCamera(androidControl, nodeMainExecutor,
                    newCameraPreview, 0, 90);
        }

        rosCameraPreviewView = newCameraPreview;
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

            if (boardService == null){
                throw new IllegalStateException("start() called before BoardService binded");
            }

            if (boardService.isConnected()) { // Already connected
                Log.w(C.ROBOT_TAG, "Called START with a robot already connected");
                return;
            }

            boardService.connect(intent);
        }
        catch (Exception ex) {
            Log.w(C.ROBOT_TAG, "Error starting connection [ " + ex.getMessage() + " ] ", ex);
        }
    }

    public void manualStart(UDCAndroidControl androidControl) {
        setAndroidControl(androidControl);
        try {
            if (boardService == null){
                throw new IllegalStateException("manualStart() called before BoardService binded");
            }

            if (boardService.isConnected()) { // Already connected
                Log.w(C.ROBOT_TAG, "Called START with a robot already connected");
                return;
            }

            boardService.connect();
        }
        catch (Exception ex) {
            Log.w(C.ROBOT_TAG, "Error staring connection [ " + ex.getMessage() + " ] ", ex);
        }
    }


    public void stop() {
        boardService.disconnect();
    }

    public void newSensorInfo(SensorInfo sensorInfo) {
        rsp.sendInfo(sensorInfo);
    }


    public void write(ActionCommand command) {
        try {
            switch (command.getCommand()) {
                case ActionCommand.CMD_HARD_RESET:
                    stop();
                    manualStart(androidControl);
                    break;
                case ActionCommand.CMD_RESET:
                    boardService.setEngines(0, 0, 0);
                    break;
                case ActionCommand.CMD_SET_LEDS:
                    /** @TODO Port led management to BoardService */
                    //robotState.setLeds(command.getLeds());
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

    public void stop(ActionCommand actionCommand){
        pf.stopPublisher(nodeMainExecutor, actionCommand.getPublisher());
    }

}