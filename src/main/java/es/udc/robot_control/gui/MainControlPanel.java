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

package es.udc.robot_control.gui;

import audio_common_msgs.AudioData;
import es.udc.robot_control.gui.action.LedPanel;
import es.udc.robot_control.gui.action.MotorPanel;
import es.udc.robot_control.gui.action.SensorModel;
import es.udc.robot_control.gui.action.SensorsPanel;
import es.udc.robot_control.gui.http_server.HttpServerProcess;
import es.udc.robot_control.gui.robot_selector.RobotSelector;
import es.udc.robot_control.gui.viewer.VisorEntradas;
import es.udc.robotcontrol.BlinkingRobotControl;
import es.udc.robotcontrol.HeadlessRobotControl;
import es.udc.robotcontrol.RosListener;
import org.ros.RosCore;
import org.ros.address.InetAddressFactory;
import org.ros.internal.message.Message;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import sensor_msgs.*;
import udc_robot_control_java.*;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 2/08/13
 * Time: 18:11
 * To change this template use File | Settings | File Templates.
 */
public class MainControlPanel implements RosListener {
    private JPanel MainContainer;
    private VisorEntradas visorEntradas;
    private LedPanel ledPanel;
    private MotorPanel motorPanel;
    private SensorsPanel sensorsPanel1;
    private RobotSelector robotSelector;
    private JLabel labelInfo;


    private String actualRobot;
    private String rosCoreAddr;
    private HeadlessRobotControl nodeMain;
    private NodeMainExecutor nodeMainExecutor;
    private RosCore master;
    private HttpServerProcess httpServer;

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainControlPanel");
        MainControlPanel app = new MainControlPanel();
        app.init();
        frame.setContentPane(app.MainContainer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void init() {
        ledPanel.setPadre(this);
        motorPanel.setPadre(this);
        sensorsPanel1.setPadre(this);
        robotSelector.setPadre(this);
        String host = InetAddressFactory.newNonLoopback().getHostAddress();
        String uri = "http://" + host + ":11311/";
        robotSelector.setUrl(uri);
    }

    public String startStopMaster() {
        String salida = null;
        if (master == null) {
            master = RosCore.newPublic();
            master.start();
            salida = master.getUri().toString();
        }
        else {
            master.shutdown();
            master = null;
            salida = null;
        }

        if (httpServer == null) {
            try {
                httpServer = new HttpServerProcess();
                String url = httpServer.getURL();
                labelInfo.setText("HTTP URL: " + url + " Documento: " + HttpServerProcess.DOCNAME);
            } catch (IOException e) {
                labelInfo.setText("No se ha podido arrancar el servidor web " + e.getMessage());
                e.printStackTrace();
            }
        }
        return salida;

    }


    public void connectRobot(String robotName, String rosCore) {
        if (nodeMain != null) {
            disconnectRobot();
            nodeMain = null;
        }
        actualRobot = robotName;
        rosCoreAddr = rosCore;

        System.out.println("Conectando a [ " + rosCore + " ] para robot [ " + robotName + " ]");
        String host = InetAddressFactory.newNonLoopback().getHostAddress();
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(host);
        URI masterUri = null;
        try {
            masterUri = new URI(rosCore);
            nodeConfiguration.setMasterUri(masterUri);
        } catch (URISyntaxException e) {
            visorEntradas.showSending("ERROR Master RosCore URI");
            e.printStackTrace();
            return;
        }
        //nodeMain = new HeadlessRobotControl(robotName);
        // Luces encendidas medio segundo y apagadas un segundo al parpadear
        nodeMain = new BlinkingRobotControl(robotName, 500, 1000);
        nodeMain.registerNotificador(this);
        if (nodeMainExecutor == null) {
            nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        }
        nodeMainExecutor.execute(nodeMain, nodeConfiguration);

    }

    public void disconnectRobot() {
        nodeMainExecutor.shutdownNodeMain(nodeMain);
    }


    public void activarSensor(SensorModel sensor) {
        visorEntradas.showSending("Activando " + sensor);
        System.out.println("Activar " + sensor);
        ActionCommand ac = nodeMain.newCommand();
        ac.setCommand(ActionCommand.CMD_START_PUBLISHER);
        ac.setPublisher(sensor.getSensorValue());
        ac.setParam0(0);
        ac.setParam1(90); // Este parametro lo usa la camara para girar la imagen.
        nodeMain.sendCommand(ac);
    }

    public void desactivarSensor(SensorModel sensor) {
        visorEntradas.showSending("Desactivando " + sensor);
        System.out.println("DesActivar " + sensor);
        ActionCommand ac = nodeMain.newCommand();
        ac.setCommand(ActionCommand.CMD_STOP_PUBLISHER);
        ac.setPublisher(sensor.getSensorValue());
        nodeMain.sendCommand(ac);
    }

    public void detenerMotores() {
        visorEntradas.showSending("Detener Motores");
        System.out.println("Detener Motores");
        ActionCommand ac = nodeMain.newCommand();
        ac.setCommand(ActionCommand.CMD_SET_ENGINES);
        ac.getEngines().setLeftEngine(0);
        ac.getEngines().setRightEngine(0);
        ac.getEngines().setMotorMode(0);
        nodeMain.sendCommand(ac);
    }

    public void enviarMotores(boolean izquierdo, boolean derecho, int velocidad) {
        String txt = "Izquierdo [" + izquierdo + "] Derecho [ " + derecho + " ] velocidad [ " + velocidad + " ]";
        visorEntradas.showSending("Motores " + txt);
        System.out.println("Enviando motores " + txt);
        ActionCommand ac = nodeMain.newCommand();
        ac.setCommand(ActionCommand.CMD_SET_ENGINES);
        int i = izquierdo?velocidad:0;
        int r = derecho?velocidad:0;
        ac.getEngines().setLeftEngine(i);
        ac.getEngines().setRightEngine(r);
        // TODO: MODO MOTOR?
        ac.getEngines().setMotorMode(0);
        nodeMain.sendCommand(ac);
    }

    public void enviarLeds(int posicion, boolean r, boolean g, boolean b, boolean blink) {
        ActionCommand ac = nodeMain.newCommand();
        ac.setCommand(ActionCommand.CMD_SET_LEDS);

        if (posicion == 0) {
            sendOneLed(ac, Led.ALL_LEDS, r, g, b, blink);
        }
        else {
            sendOneLed(ac, posicion - 1, r, g, b, blink);
        }

        nodeMain.sendCommand(ac);
    }

    private void sendOneLed(ActionCommand ac, int pos, boolean r, boolean g, boolean b, boolean blink) {
        String txt = "Led [ " + pos + " ] Rojo [ " + r + "] Verde [ " + g + "] Azul [ " + b + "  ] Parpadeo [ " + blink + " ]";
        visorEntradas.showSending(txt);
        System.out.println("Enviando " + txt);
        Led led = nodeMain.newLed();
        led.setLedNumber(pos);
        led.setBlinking(blink);
        int blue  = b?255:0;
        int red   = r?255:0;
        int green = g?255:0;
        led.setBlue(blue);
        led.setRed(red);
        led.setGreen(green);
        ac.getLeds().add(led);
    }

    @Override
    public void onMsgArrived(Message message) {
        if (!(message instanceof AudioData) && !(message instanceof CompressedImage)){
            String txt = logMsg(message);
            visorEntradas.showReceivedMsg(txt);
        }
    }

    @Override
    public void onError(Node node, Throwable throwable) {
        String txt = "ERROR [ " + node.getName() + " ] [ " + throwable.getMessage() + " ]";
        throwable.printStackTrace();
        visorEntradas.showReceivedMsg(txt);
    }



    private String logMsg(Message message) {
        String txt = "Mensaje desconocido " + message.toRawMessage().getName();
        if (message instanceof Imu) {
            Imu imu = (Imu) message;
            txt = "IMU FrameID [ " + imu.getHeader().getFrameId() + " ]" +
                    "Lineal (" + imu.getLinearAcceleration().getX() + ", " + imu.getLinearAcceleration().getY() + ", " + imu.getLinearAcceleration().getZ() + ") " +
                    "Angular (" + imu.getAngularVelocity().getX() + ", " + imu.getAngularVelocity().getY() + ", " + imu.getAngularVelocity().getZ() + ") " +
                    "Orientacion (" + imu.getOrientation().getX() + ", " + imu.getOrientation().getY() + ", " + imu.getOrientation().getZ() + ")";
        }
        if (message instanceof BateryStatus) {
            BateryStatus bat = (BateryStatus) message;
            txt = "Bateria [ " + bat.getHeader().getFrameId() + " ][ " + bat.getDescription() + " ] [ " + bat.getLevel() + " ] [ " + bat.getStatus() + " ]";
        }
        if (message instanceof NavSatFix) {
            NavSatFix nsf = (NavSatFix) message;
            txt = "NavSatFix [ " + nsf.getHeader().getFrameId() + " ] Alt [ " + nsf.getAltitude() + " ] Lat [ " + nsf.getLatitude() + " ] Lon [ " + nsf.getLongitude() + " ]" ;
        }
        if (message instanceof MagneticField) {
            MagneticField mf = (MagneticField) message;
            txt = "Magnetic [ " + mf.getHeader().getFrameId() + " ] (" + mf.getMagneticField().getX() + ", " + mf.getMagneticField().getY() + ", " + mf.getMagneticField().getZ() + ")";
        }
        if (message instanceof  Range) {
            Range r = (Range) message;
            txt = "Range [ " + r.getHeader().getFrameId() + " ] Range [" + r.getRange() + " ] Interval (" + r.getMinRange() + " - " + r.getMaxRange() + ")";
        }
        if (message instanceof Temperature) {
            Temperature tmp = (Temperature) message;
            txt = "Temperature [ " + tmp.getHeader().getFrameId() + " ][ " + tmp.getTemperature() + " ]";
        }
        if (message instanceof SensorStatus) {
            SensorStatus ss = (SensorStatus) message;
            txt = "IRSensors " + ss.getHeader().getFrameId() + " ]" +
                    "[ " + ss.getSIr1() + " ]" +
                    "[ " + ss.getSIr2() + " ]" +
                    "[ " + ss.getSIr3() + " ]" +
                    "[ " + ss.getSIr4() + " ]" +
                    "[ " + ss.getSIr5() + " ]" +
                    "[ " + ss.getSIr6() + " ]" +
                    "[ " + ss.getSIr7() + " ]" +
                    "[ " + ss.getSIr8() + " ]" +
                    "[ " + ss.getSIrS1() + " ]" +
                    "[ " + ss.getSIrS2() + " ]";
        }
        if (message instanceof Illuminance) {
            Illuminance ilu = (Illuminance)message;
            txt = "Iluminación [ " + ilu.getHeader().getFrameId() + " ][ " + ilu.getIlluminance() + " ][ " + ilu.getVariance() + " ]";
        }
        if (message instanceof FluidPressure) {
            FluidPressure fl = (FluidPressure)message;
            txt = "Presión [ " + fl.getHeader().getFrameId() + " ][ " + fl.getFluidPressure() + " ][ " + fl.getVariance() + " ]";
        }

        return txt;
    }
}

