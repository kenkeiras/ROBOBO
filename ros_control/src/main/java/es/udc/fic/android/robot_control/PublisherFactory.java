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
import es.udc.fic.android.robot_control.gps.NavSatFixPublisher;
import es.udc.fic.android.robot_control.robot.RobotSensorPublisher;
import es.udc.fic.android.robot_control.sensors.*;
import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constantes;
import org.ros.address.InetAddressFactory;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import udc_robot_control_java.ActionCommand;

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
    private GyroPublisher gyroPub;
    private QuatPublisher quatPub;
    private AccelerometerPublisher accelerometerPub;
    private MagneticFieldPublisher magneticFieldPub;
    private FluidPressurePublisher fluidPressurePub;
    private IlluminancePublisher illuminancePub;
    private TemperaturePublisher temperaturePub;
    private ProximityPublisher proximityPub;


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


    public void setRobotName(String r) {
        robotName = r;
        checkConfig();
    }
    public void setMasterUri(URI u) {
        masterUri = u;
        checkConfig();
    }

    private void checkConfig() {
        if ((robotName != null) && (masterUri != null)) {
            nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
            nodeConfiguration.setMasterUri(masterUri);
            nodeConfiguration.setNodeName("/" + robotName);
        }
    }

    public void configureCommandListener(UDCAndroidControl ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating Commands Listener");
        NodeConfiguration nc0 = NodeConfiguration.copyOf(nodeConfiguration);
        nc0.setNodeName("/" + robotName + "/" + Constantes.NODE_COMMANDS);
        cmdl = new CommandListener(ctx, robotName, nodeMainExecutor);
        nodeMainExecutor.execute(cmdl, nc0);
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
        ncProximity.setNodeName("/" + robotName + "/" + Constantes.NODE_RANGE);
        proximityPub = new ProximityPublisher(ctx, robotName);
        nodeMainExecutor.execute(proximityPub, ncProximity);
    }

    public void configurePressure(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating PressurePublisher");
        NodeConfiguration ncProximity = NodeConfiguration.copyOf(nodeConfiguration);
        ncProximity.setNodeName("/" + robotName + "/" + Constantes.TOPIC_PRESSURE);
        fluidPressurePub = new FluidPressurePublisher(ctx, robotName);
        nodeMainExecutor.execute(fluidPressurePub, ncProximity);
    }

    public void configureIlluminance(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating IlluminancePublisher");
        NodeConfiguration nc= NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_ILLUMINANCE);
        illuminancePub = new IlluminancePublisher(ctx, robotName);
        nodeMainExecutor.execute(illuminancePub, nc);
    }

    public void configureImu(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating ImuPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_IMU);
        imuPub = new ImuPublisher(ctx, robotName);
        nodeMainExecutor.execute(imuPub, nc);
    }

    public void configureGyro(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating GyroPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_GYRO);
        gyroPub = new GyroPublisher(ctx, robotName);
        nodeMainExecutor.execute(gyroPub, nc);
    }

    public void configureAccel(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating AccelPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_ACEL);
        accelerometerPub = new AccelerometerPublisher(ctx, robotName);
        nodeMainExecutor.execute(accelerometerPub, nc);
    }

    public void configureQuat(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating QuatPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_ROTATION);
        quatPub = new QuatPublisher(ctx, robotName);
        nodeMainExecutor.execute(quatPub, nc);
    }

    public void configureMagnetic(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating MagneticFieldPublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_MAGNETIC);
        magneticFieldPub = new MagneticFieldPublisher(ctx, robotName);
        nodeMainExecutor.execute(magneticFieldPub, nc);
    }


    public void configureTemperature(Context ctx, NodeMainExecutor nodeMainExecutor) {
        Log.i(C.TAG, "Creating TemperaturePublisher");
        NodeConfiguration nc = NodeConfiguration.copyOf(nodeConfiguration);
        nc.setNodeName("/" + robotName + "/" + Constantes.NODE_TEMPERATURE);
        temperaturePub = new TemperaturePublisher(ctx, robotName);
        nodeMainExecutor.execute(temperaturePub, nc);
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
            case ActionCommand.PUBLISHER_ACCEL:
                node.shutdownNodeMain(accelerometerPub);
                accelerometerPub = null;
                break;
            case ActionCommand.PUBLISHER_GYRO:
                node.shutdownNodeMain(gyroPub);
                gyroPub = null;
                break;
            case ActionCommand.PUBLISHER_ANGULAR:
                node.shutdownNodeMain(quatPub);
                quatPub = null;
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
            case ActionCommand.PUBLISHER_ILLUMINANCE:
                node.shutdownNodeMain(illuminancePub);
                illuminancePub = null;
                break;
            case ActionCommand.PUBLISHER_IMU:
                node.shutdownNodeMain(imuPub);
                imuPub = null;
                break;
            case ActionCommand.PUBLISHER_MAG:
                node.shutdownNodeMain(magneticFieldPub);
                magneticFieldPub = null;
                break;
            case ActionCommand.PUBLISHER_PRESSURE:
                node.shutdownNodeMain(fluidPressurePub);
                fluidPressurePub = null;
                break;
            case ActionCommand.PUBLISHER_PROXIMITY:
                node.shutdownNodeMain(proximityPub);
                proximityPub = null;
                break;
            case ActionCommand.PUBLISHER_TEMPERATURE:
                node.shutdownNodeMain(temperaturePub);
                temperaturePub = null;
                break;
            case ActionCommand.PUBLISHER_VIDEO:
                node.shutdownNodeMain(rosCameraPreviewView);
                break;
            default:
                Log.w(C.TAG, "Unknown publisher to stop [ " + publisher + " ]");
        }
    }

}
