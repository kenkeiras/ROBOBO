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

package es.udc.fic.android.robot_control;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import es.udc.fic.android.robot_control.audio.AudioPublisher;
import es.udc.fic.android.robot_control.batery.BateryStatus;
import es.udc.fic.android.robot_control.camara.RosCameraPreviewView;
import es.udc.fic.android.robot_control.commands.CommandListener;
import es.udc.fic.android.robot_control.commands.EngineListener;
import es.udc.fic.android.robot_control.commands.EngineManager;
import es.udc.fic.android.robot_control.gps.NavSatFixPublisher;
import es.udc.fic.android.robot_control.robot.RobotSensorPublisher;
import es.udc.fic.android.robot_control.sensors.*;
import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constantes;
import org.ros.address.InetAddressFactory;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import udc_robot_control_msgs.ActionCommand;

import java.net.URI;

/**
 *
 * Created by kerry on 22/07/13.
 *
 * This is a factory for publishers. Makes easy the configuration work
 *
 */
public class PublisherFactory {

    private String robotName;
    private URI masterUri;
    private NodeConfiguration nodeConfiguration;

    // Drivers
    private ImuPublisher imuPub;
    private GyroscopeUncalibratedPublisher gyroscopeUncalibratedPub;
    private GyroscopePublisher gyroscopePub;
    private RotationVectorPublisher rotationVectorPub;
    private GameRotationVectorPublisher gameRotationVectorPub;
    private AccelerometerPublisher accelerometerPub;
    private GravityPublisher gravityPub;
    private LinearAccelerationPublisher linearAccelerationPub;
    private MagneticFieldPublisher magneticFieldPub;
    private MagneticFieldUncalibratedPublisher magneticFieldUncalibratedPub;
    private PressurePublisher fluidPressurePub;
    private LightPublisher lightPub;
    private RelativeHumidityPublisher relativeHumidityPub;
    private AmbientTemperaturePublisher temperaturePub;
    private ProximityPublisher proximityPub;
    private OrientationPublisher orientationPub;


    // Camara
    private RosCameraPreviewView rosCameraPreviewView;
    // Audio
    private AudioPublisher audioPub;
    // GPS
    private NavSatFixPublisher navSatFixPub;

    // Bateria
    private BateryStatus bateryStatusPublisher;

    // ROBOT
    private RobotSensorPublisher rsp;
    private CommandListener cmdl;
    private EngineListener engineListener;


    public void setRobotName(String r) {
        robotName = r;
        checkConfig();
    }
    public void setMasterUri(URI u) {
        masterUri = u;
        checkConfig();
    }

    private void checkConfig() {
            Log.v("UDC_FACTORY", "ROBOT NAME: " + robotName + "  URI: " + masterUri);
        if ((robotName != null) && (masterUri != null)) {
            nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
            nodeConfiguration.setMasterUri(masterUri);
            nodeConfiguration.setNodeName("/" + robotName);
            Log.v("UDC_FACTORY", "NODE: " + nodeConfiguration);
        }
    }

    public void configureCommandListener(UDCAndroidControl ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating Commands Listener");
        NodeConfiguration nc0 = NodeConfiguration.copyOf(nodeConfiguration);
        nc0.setNodeName("/" + robotName + "/" + Constantes.NODE_COMMANDS);
        cmdl = new CommandListener(ctx, robotName, nodeMainExecutor);
        nodeMainExecutor.execute(cmdl, nc0);
    }

    public void configureEngineListener(EngineManager manager, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating Engine Listener");
        NodeConfiguration nc0 = NodeConfiguration.copyOf(nodeConfiguration);
        nc0.setNodeName("/" + robotName + "/" + Constantes.NODE_ENGINES);
        engineListener = new EngineListener(manager, robotName, nodeMainExecutor);
        nodeMainExecutor.execute(engineListener, nc0);
    }

    public RobotSensorPublisher configureIRSensorPublisher(UDCAndroidControl ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating IR Sensor publisher");
        NodeConfiguration nc0 = NodeConfiguration.copyOf(nodeConfiguration);
        nc0.setNodeName("/" + robotName + "/" + Constantes.NODE_IR_SENSORS);
        rsp = new RobotSensorPublisher(ctx, robotName);
        nodeMainExecutor.execute(rsp, nc0);
        return rsp;
    }

    public void configureBatery(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating BateryStatus Publisher");
        NodeConfiguration nc0 = NodeConfiguration.copyOf(nodeConfiguration);
        nc0.setNodeName("/" + robotName + "/" + Constantes.NODE_BATERY);
        bateryStatusPublisher = new BateryStatus(ctx, robotName);
        nodeMainExecutor.execute(bateryStatusPublisher, nc0);
    }

    public void configureProximity(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating Proximity Publisher");
        NodeConfiguration ncProximity = NodeConfiguration.copyOf(nodeConfiguration);
        ncProximity.setNodeName("/" + robotName + "/" + Constantes.NODE_PROXIMITY);
        proximityPub = new ProximityPublisher(ctx, robotName);
        nodeMainExecutor.execute(proximityPub, ncProximity);
    }

    public void configurePressure(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating PressurePublisher");
        NodeConfiguration ncProximity = NodeConfiguration.copyOf(nodeConfiguration);
        ncProximity.setNodeName("/" + robotName + "/" + Constantes.NODE_PRESSURE);
        fluidPressurePub = new PressurePublisher(ctx, robotName);
        nodeMainExecutor.execute(fluidPressurePub, ncProximity);
    }

    public void configureLight(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating LightPublisher");
        NodeConfiguration nc= NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_LIGHT);
        lightPub = new LightPublisher(ctx, robotName);
        nodeMainExecutor.execute(lightPub, nc);
    }

    public void configureImu(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating ImuPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_IMU);
        imuPub = new ImuPublisher(ctx, robotName);
        nodeMainExecutor.execute(imuPub, nc);
    }

    public void configureGyroscope(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating GyroscopePublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_GYROSCOPE);
        gyroscopePub = new GyroscopePublisher(ctx, robotName);
        nodeMainExecutor.execute(gyroscopePub, nc);
    }

    public void configureGyroscopeUncalibrated(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating GyroscopeUncalibratedPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_GYROSCOPE_UNCALIBRATED);
        gyroscopeUncalibratedPub = new GyroscopeUncalibratedPublisher(ctx, robotName);
        nodeMainExecutor.execute(gyroscopeUncalibratedPub, nc);
    }

    public void configureAccelerometer(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating AccelerometerPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_ACCELEROMETER);
        accelerometerPub = new AccelerometerPublisher(ctx, robotName);
        nodeMainExecutor.execute(accelerometerPub, nc);
    }

    public void configureGravity(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating GraviyPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_GRAVITY);
        gravityPub = new GravityPublisher(ctx, robotName);
        nodeMainExecutor.execute(gravityPub, nc);
    }

    public void configureLinearAcceleration(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating LinearAcceleration");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_LINEAL_ACCELERATION);
        linearAccelerationPub = new LinearAccelerationPublisher(ctx, robotName);
        nodeMainExecutor.execute(accelerometerPub, nc);
    }


    public void configureRotationVector(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating RotationVectorPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_ROTATION_VECTOR);
        rotationVectorPub = new RotationVectorPublisher(ctx, robotName);
        nodeMainExecutor.execute(rotationVectorPub, nc);
    }

    public void configureGameRotationVector(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating GameRotationVectorPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_GAME_ROTATION_VECTOR);
        gameRotationVectorPub = new GameRotationVectorPublisher(ctx, robotName);
        nodeMainExecutor.execute(gameRotationVectorPub, nc);
    }

    public void configureMagneticField(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating MagneticFieldPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_MAGNETIC_FIELD);
        magneticFieldPub = new MagneticFieldPublisher(ctx, robotName);
        nodeMainExecutor.execute(magneticFieldPub, nc);
    }

    public void configureMagneticUncalibrated(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating MagneticFieldPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_MAGNETIC_FIELD_UNCALIBRATED);
        magneticFieldUncalibratedPub = new MagneticFieldUncalibratedPublisher(ctx, robotName);
        nodeMainExecutor.execute(magneticFieldPub, nc);
    }

    public void configureOrientation(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating OrientationPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_ORIENTATION);
        orientationPub = new OrientationPublisher(ctx, robotName);
        nodeMainExecutor.execute(orientationPub, nc);
    }

    public void configureTemperature(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating AmbientTemperaturePublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_AMBIENT_TEMPERATURE);
        temperaturePub = new AmbientTemperaturePublisher(ctx, robotName);
        nodeMainExecutor.execute(temperaturePub, nc);
    }

    public void configureRelativeHumidity(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating RelativeHumidityPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_RELATIVE_HUMIDITY);
        relativeHumidityPub = new RelativeHumidityPublisher(ctx, robotName);
        nodeMainExecutor.execute(relativeHumidityPub, nc);
    }


    public void configureNavSatFix(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating NavSatFixPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_NAV_SAT_FIX);
        navSatFixPub = new NavSatFixPublisher(ctx, robotName);
        nodeMainExecutor.execute(navSatFixPub, nc);
    }

    public void configureCamara(Context ctx, NodeMainExecutor nodeMainExecutor, RosCameraPreviewView rcp, int camaraId, int displayOrientation) {
        Log.i(C.TAG, "Iniciando la camara");
        rosCameraPreviewView = rcp;
        Camera c = Camera.open(camaraId);
        c.setDisplayOrientation(displayOrientation);

        rosCameraPreviewView.setCamera(c);
        Log.i(C.TAG, "Creando configuracion video");
        NodeConfiguration ncCamara = NodeConfiguration.copyOf(nodeConfiguration);
        ncCamara.setNodeName("/" + robotName + "/" + Constantes.NODE_IMAGE);
        rosCameraPreviewView.setRobotName(robotName);
        nodeMainExecutor.execute(rosCameraPreviewView, ncCamara);
    }

    public void configureAudio(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating AudioPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_AUDIO);
        audioPub = new AudioPublisher(ctx, robotName);
        nodeMainExecutor.execute(audioPub, nc);
    }

    public void stopPublisher(NodeMainExecutor node, int publisher) {

        switch (publisher) {
            case ActionCommand.PUBLISHER_ACCELEROMTER:
                node.shutdownNodeMain(accelerometerPub);
                accelerometerPub = null;
                break;
            case ActionCommand.PUBLISHER_AMBIENT_TEMPERATURE:
                node.shutdownNodeMain(temperaturePub);
                temperaturePub = null;
                break;
            case ActionCommand.PUBLISHER_GAME_ROTATION_VECTOR:
                node.shutdownNodeMain(gameRotationVectorPub);
                gameRotationVectorPub = null;
                break;
            case ActionCommand.PUBLISHER_GRAVITY:
                node.shutdownNodeMain(gravityPub);
                gravityPub = null;
                break;
            case ActionCommand.PUBLISHER_GYROSCOPE:
                node.shutdownNodeMain(gyroscopePub);
                gyroscopePub = null;
                break;
            case ActionCommand.PUBLISHER_GYROSCOPE_UNCALIBRATED:
                node.shutdownNodeMain(gyroscopeUncalibratedPub);
                gyroscopeUncalibratedPub = null;
                break;
            case ActionCommand.PUBLISHER_IMU:
                node.shutdownNodeMain(imuPub);
                imuPub = null;
                break;
            case ActionCommand.PUBLISHER_LIGHT:
                node.shutdownNodeMain(lightPub);
                lightPub = null;
                break;
            case ActionCommand.PUBLISHER_LINEAL_ACCELERATION:
                node.shutdownNodeMain(linearAccelerationPub);
                linearAccelerationPub = null;
                break;
            case ActionCommand.PUBLISHER_MAGNETIC_FIELD:
                node.shutdownNodeMain(magneticFieldPub);
                magneticFieldPub = null;
                break;
            case ActionCommand.PUBLISHER_MAGNETIC_FIELD_UNCALIBRATED:
                node.shutdownNodeMain(magneticFieldUncalibratedPub);
                magneticFieldUncalibratedPub = null;
                break;
            case ActionCommand.PUBLISHER_ORIENTATION:
                node.shutdownNodeMain(orientationPub);
                orientationPub = null;
                break;
            case ActionCommand.PUBLISHER_PRESSURE:
                node.shutdownNodeMain(fluidPressurePub);
                fluidPressurePub = null;
                break;
            case ActionCommand.PUBLISHER_PROXIMITY:
                node.shutdownNodeMain(proximityPub);
                proximityPub = null;
                break;
            case ActionCommand.PUBLISHER_RELATIVE_HUMIDITY:
                node.shutdownNodeMain(relativeHumidityPub);
                relativeHumidityPub = null;
                break;
            case ActionCommand.PUBLISHER_ROTATION_VECTOR:
                node.shutdownNodeMain(rotationVectorPub);
                rotationVectorPub = null;
                break;
            case ActionCommand.PUBLISHER_AUDIO:
                node.shutdownNodeMain(audioPub);
                audioPub = null;
                break;
            case ActionCommand.PUBLISHER_BATERY:
                node.shutdownNodeMain(bateryStatusPublisher);
                bateryStatusPublisher = null;
                break;
            case ActionCommand.PUBLISHER_GPS:
                node.shutdownNodeMain(navSatFixPub);
                navSatFixPub = null;
                break;
            case ActionCommand.PUBLISHER_VIDEO:
                node.shutdownNodeMain(rosCameraPreviewView);
                break;
            default:
                Log.w(C.TAG, "Unknown publisher to stop [ " + publisher + " ]");
        }
    }

}
