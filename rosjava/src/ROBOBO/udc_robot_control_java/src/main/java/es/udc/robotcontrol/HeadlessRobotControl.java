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

package es.udc.robotcontrol;

import audio_common_msgs.AudioData;
import es.udc.robotcontrol.utils.Constants;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import sensor_msgs.*;
import sensor_msgs.RelativeHumidity;
import udc_robot_control_msgs.*;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 23/07/13
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public class HeadlessRobotControl extends AbstractRobotControl {


    /**
     * Subscriptores a las colas de mensajes emitidos por el robot
     */
    private GeneralSubscriber[] subscriptors;
    /**
     * Publicador de mensajes
     */
    private CommandsPublisher publisher;

    // Tipos de mensajes emitidos por el robot
    private String[] msgTypes = {
            AudioData._TYPE,
            BatteryStatus._TYPE,
            CompressedImage._TYPE,
            CameraInfo._TYPE,
            NavSatFix._TYPE,
            Imu._TYPE, // imu
            AndroidSensor3._TYPE, // accelerometer
            MagneticField._TYPE,
            AndroidSensor3._TYPE, // gyroscope
            Illuminance._TYPE,
            FluidPressure._TYPE,
            Range._TYPE,
            AndroidSensor3._TYPE, // gravity
            AndroidSensor3._TYPE, // lineal acceleration
            AndroidSensor3._TYPE,  // rotation vector
            AndroidSensor3._TYPE, // orientation
            RelativeHumidity._TYPE, //
            Temperature._TYPE,
            MagneticField._TYPE, // Magnetic field uncalibrated
            AndroidSensor4._TYPE, // Game Rotation Vector
            AndroidSensor3._TYPE, // giroscope uncalibrated
            SensorStatus._TYPE
    };

    // Names of the queues where the robot publishes
    private String[] topicNames = {
            Constants.TOPIC_AUDIO,
            Constants.TOPIC_BATTERY,
            Constants.TOPIC_IMAGE,
            Constants.TOPIC_CAMERA_INFO,
            Constants.TOPIC_NAV_SAT_FIX,
            Constants.TOPIC_IMU,
            Constants.TOPIC_ACCELEROMETER,
            Constants.TOPIC_MAGNETIC_FIELD,
            Constants.TOPIC_GYROSCOPE,
            Constants.TOPIC_LIGHT,
            Constants.TOPIC_PRESSURE,
            Constants.TOPIC_PROXIMITY,
            Constants.TOPIC_GRAVITY,
            Constants.TOPIC_LINEAL_ACCELERATION,
            Constants.TOPIC_ROTATION_VECTOR,
            Constants.TOPIC_ORIENTATION,
            Constants.TOPIC_RELATIVE_HUMIDITY,
            Constants.TOPIC_AMBIENT_TEMPERATURE,
            Constants.TOPIC_MAGNETIC_FIELD_UNCALIBRATED,
            Constants.TOPIC_GAME_ROTATION_VECTOR,
            Constants.TOPIC_GYROSCOPE_UNCALIBRATED,
            Constants.TOPIC_IR_SENSORS
    };



    public HeadlessRobotControl(String rName) {
        super(rName);
        setRobotName(rName);
        subscriptors = new GeneralSubscriber[msgTypes.length];
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        super.onStart(connectedNode);
        for (int i = 0; i < subscriptors.length; i++) {
            subscriptors[i] = new GeneralSubscriber(this, msgTypes[i]);
            subscriptors[i].connect(connectedNode, queueName(topicNames[i]));
        }
        publisher = new CommandsPublisher(this);
        publisher.connect(connectedNode, queueName(Constants.TOPIC_COMMANDS));
    }

    @Override
    public void onShutdown(Node node) {
        publisher.disconnect();
        for (int i = 0 ; i < subscriptors.length; i++) {
            if (subscriptors[i] != null) {
                subscriptors[i].disconnect();
            }
        }
    }

    @Override
    public ActionCommand newCommand() {
        return publisher.newMsg();
    }

    @Override
    public void sendCommand(ActionCommand msg) {
        publisher.publish(msg);
    }



}
