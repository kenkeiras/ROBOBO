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
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import es.udc.fic.android.robot_control.R;
import es.udc.fic.android.robot_control.UDCAndroidControl;
import es.udc.fic.android.robot_control.PublisherFactory;
import es.udc.fic.android.robot_control.camara.RosCameraPreviewView;
import es.udc.fic.android.robot_control.utils.C;
import udc_robot_control_msgs.ActionCommand;

import org.ros.RosCore;
import org.ros.node.NodeMainExecutor;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Servicio de comunicación con el robot
 * Se encargará de abrir la manejar la conexión.
 *
 * Created by kerry on 2/06/13.
 */
public class RobotCommController extends Service {

    public boolean continuar;
    public UDCAndroidControl androidControl;

    private EstadoRobot estadoRobot;
    private long readSleepTime;
    private HiloControl control;
    private ConectorPlaca conector;
    private String robotName;
    private URI masterURI;

    private PublisherFactory pf;
    private RobotSensorPublisher rsp;
    private NodeMainExecutor nodeMainExecutor;
    private RosCameraPreviewView rosCameraPreviewView;

    private static final int DEFAULT_MASTER_PORT = 11311;
    private RosCore core = null;
    private boolean createdInitialNodes = false;


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
        this.estadoRobot = new EstadoRobot();
        continuar = false;

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
        if ((masterURI == null)
            || (nodeMainExecutor == null)
            || (androidControl == null)){

            return;
        }

        createdInitialNodes = true;
        // Configurar nodo inicial. Un listener. Es el encargado de recibir instrucciones desde el exterior
        pf.configureCommandListener(androidControl, nodeMainExecutor);
        rsp = pf.configureIRSensorPublisher(androidControl, nodeMainExecutor);
    }


    public void setCameraPreview(RosCameraPreviewView cameraPreview){
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


    public void arrancarListener(ActionCommand actionCommand){
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
            case ActionCommand.PUBLISHER_BATERY:
                pf.configureBatery(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_GPS:
                pf.configureNavSatFix(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_IMU:
                pf.configureImu(this, nodeMainExecutor);
                break;
            case ActionCommand.PUBLISHER_VIDEO:
                int camaraId = actionCommand.getParam0();
                int orientation = actionCommand.getParam1();
                pf.configureCamara(androidControl, nodeMainExecutor, rosCameraPreviewView, camaraId, orientation);
                break;
            default:
                Log.w(C.CMD_TAG, "Publisher desconocido [ " + actionCommand.getPublisher() + " ]");
        }
    }


    public void iniciar(UDCAndroidControl androidControl, Intent intent) {
        this.androidControl = androidControl;
        Log.i(C.ROBOT_TAG, "Iniciando controlador de robot");
        try {

            if (continuar) { // YA conectado
                Log.w(C.ROBOT_TAG, "Llamado a INICIAR con un robot ya conectado");
                return;
            }
            continuar = true;
            Log.i(C.ROBOT_TAG, "Creando conector Placa");
            conector = new ConectorPlaca();
            Log.i(C.ROBOT_TAG, "Conector placa creado. Llamando a conectar");
            conector.conectar(this, intent);
            Log.i(C.ROBOT_TAG, "La llamada a conectar ha tenido exito");

            Toast.makeText(this, R.string.robot_service_started, Toast.LENGTH_SHORT).show();

            Log.i(C.ROBOT_TAG, "Creando hilo lector");
            control = new HiloControl(this);

            Log.i(C.ROBOT_TAG, "Lanzando hilo lector");
            control.start();
            Log.i(C.ROBOT_TAG, "Todos los hilos han sido lanzados. iniciar completado.");
        }
        catch (Exception ex) {
            Log.w(C.ROBOT_TAG, "Error iniciando conexion [ " + ex.getMessage() + " ] ", ex);
        }
    }

    public void iniciarManual() {
        try {
            Log.v("UDC", "Iniciando manual, continuado? " + continuar);
            if(continuar) { // Ya conectado
                Log.w(C.ROBOT_TAG, "Llamado a INICIAR MANUAL con un robot ya conectado");
                return;
            }
            Log.i(C.ROBOT_TAG, "Iniciando controlador manualmente.");
            continuar = true;
            if (conector == null) {
                conector = new ConectorPlaca();
            }
            conector.conectarManual(androidControl);
            control = new HiloControl(this);
            control.start();
        }
        catch (Exception ex) {
            Log.w(C.ROBOT_TAG, "Error iniciando conexion [ " + ex.getMessage() + " ] ", ex);
        }
    }


    public void terminar() {
        if (conector != null) {
            conector.desconectar();
        }
        continuar = false;
    }


    public synchronized boolean continuarLector(HiloControl hl) {
        return (continuar && (control == hl));
    }


    public synchronized  byte[] leer() {
        return conector.leer();
    }

    public long getReadSleepTime() {
        return readSleepTime;
    }

    public void setReadSleepTime(long readSleepTime) {
        this.readSleepTime = readSleepTime;
    }

    public void escribir(ActionCommand comando) {
        try {
            switch (comando.getCommand()) {
                case ActionCommand.CMD_HARD_RESET:
                    terminar();
                    iniciarManual();
                    break;
                case ActionCommand.CMD_RESET:
                    estadoRobot.reset();
                    conector.escribir(estadoRobot);
                    break;
                case ActionCommand.CMD_SET_ENGINES:
                    estadoRobot.setMotores(comando.getEngines());
                    conector.escribir(estadoRobot);
                    break;
                case ActionCommand.CMD_SET_LEDS:
                    estadoRobot.setLeds(comando.getLeds());
                    conector.escribir(estadoRobot);
                    break;
            }
        }
        catch (Exception ex) {
            if (comando != null) {
                Log.e(C.ROBOT_TAG, "Error ejecutando comando [ " + comando.getCommand() + " ]", ex);
            }
            else {
                Log.e(C.ROBOT_TAG, "Error ejecutando comando. El comando es null", ex);
            }
        }
    }

    public void sendToRos(SensorInfo ss) {
        rsp.sendInfo(ss);
    }

    public void refreshRobot(){
        conector.escribir(estadoRobot);
    }

    public void stop(ActionCommand actionCommand){
        pf.stopPublisher(nodeMainExecutor, actionCommand.getPublisher());
    }

}


class HiloControl extends Thread {

    RobotCommController parent;

    public HiloControl (RobotCommController parent) {
        this.parent = parent;
    }


    @Override
    public void run() {
        while (parent.continuarLector(this)) {
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
            Log.d(C.ROBOT_TAG, "Leyendo sensores");
            byte[] read = parent.leer();
            if (read != null) {
                Log.i(C.ROBOT_TAG, "Leidos [ " + read.length + " ] bytes");
                try {
                    // Parsear la lectura
                    SensorInfo info = new SensorInfo(read);
                    parent.sendToRos(info);

                } catch (Exception e) {
                    Log.w("Error recuperando info", e);
                    StringBuilder sb = new StringBuilder();
                    for (int x = 0; x < read.length; x++) {
                        sb.append("byte [ " + x + " ] = (" + read[x] + ")");
                    }
                    Log.w(C.ROBOT_TAG, "Leido => " + sb.toString());
                }
            }
            else {
                Log.i(C.ROBOT_TAG, "Nada que leer");
            }
            try {
                sleep(parent.getReadSleepTime());
            }
            catch (InterruptedException ie) {
                Log.w(C.ROBOT_TAG, "InterruptedException en el lector");
            }
        }
        catch (Exception ex) {
            Log.w(C.ROBOT_TAG, "Exception en hilo lector", ex);
            parent.continuar = false;
        }

        // TODO: Quitar esto en función de cómo funcionen las lecturas (si es que funcionan)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.w(C.ROBOT_TAG, "Error al dormir el hilo lector" + e.getMessage());
            e.printStackTrace();
        }
    }
}
