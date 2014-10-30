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

package es.udc.robotcontrol.utils;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 1/08/13
 * Time: 19:39
 * This class is used to define shared Strings between the robot and the control library (node, topic and command names)
 */
public abstract class Constants {

    // Node names
    public static final String NODE_ENGINES     = "cmd_vel";
    public static final String NODE_COMMANDS    = "commands";
    public static final String NODE_IR_SENSORS  = "irsensors";
    public static final String NODE_AUDIO       = "audio";
    public static final String NODE_SPEECH_RECOGNITION = "speech_recognition";
    public static final String NODE_TEXT_TO_SPEECH = "text_to_speech";
    public static final String NODE_BATTERY     = "battery";
    public static final String NODE_IMAGE       = "image";
    public static final String NODE_CAMERA_INFO = "camerainfo";
    public static final String NODE_NAV_SAT_FIX = "nav_sat_fix";
    public static final String NODE_IMU         = "imu";
    public static final String NODE_APRIL_TAGS  = "april_tags";
    public static final String NODE_SCREEN      = "screen";


    public static final String NODE_ACCELEROMETER               = "accelerometer";
    public static final String NODE_MAGNETIC_FIELD              = "magnetic_field";
    public static final String NODE_GYROSCOPE                   = "gyroscope";
    public static final String NODE_LIGHT                       = "light";
    public static final String NODE_PRESSURE                    = "pressure";
    public static final String NODE_PROXIMITY                   = "proximity";
    public static final String NODE_GRAVITY                     = "gravity";
    public static final String NODE_LINEAL_ACCELERATION         = "lineal_acceleration";
    public static final String NODE_ROTATION_VECTOR             = "rotation_vector";
    public static final String NODE_ORIENTATION                 = "orientation";
    public static final String NODE_RELATIVE_HUMIDITY           = "relative_humidity";
    public static final String NODE_AMBIENT_TEMPERATURE         = "ambient_temperature";
    public static final String NODE_MAGNETIC_FIELD_UNCALIBRATED = "magnetic_fied_uncalibrated";
    public static final String NODE_GAME_ROTATION_VECTOR        = "game_rotation_vector";
    public static final String NODE_GYROSCOPE_UNCALIBRATED      = "gyroscope_uncalibrated";

    // Queue names
    public static final String TOPIC_ENGINES       = "cmd_vel";
    public static final String TOPIC_COMMANDS      = "commands";
    public static final String TOPIC_IR_SENSORS    = "irsensors";
    public static final String TOPIC_AUDIO         = "audio";
    public static final String TOPIC_SPEECH_RECOGNITION = "speech_recognition";
    public static final String TOPIC_TEXT_TO_SPEECH = "text_to_speech";
    public static final String TOPIC_BATTERY       = "battery";
    public static final String TOPIC_IMAGE         = "image";
    public static final String TOPIC_CAMERA_INFO   = "camerainfo";
    public static final String TOPIC_NAV_SAT_FIX   = "nav_sat_fix";
    public static final String TOPIC_IMU           = "imu";
    public static final String TOPIC_APRIL_TAGS    = "april_tags";
    public static final String TOPIC_SCREEN        = "screen";


    public static final String TOPIC_ACCELEROMETER               = "accelerometer";
    public static final String TOPIC_MAGNETIC_FIELD              = "magnetic_filed";
    public static final String TOPIC_GYROSCOPE                   = "gyroscope";
    public static final String TOPIC_LIGHT                       = "light";
    public static final String TOPIC_PRESSURE                    = "pressure";
    public static final String TOPIC_PROXIMITY                   = "proximity";
    public static final String TOPIC_GRAVITY                     = "gravity";
    public static final String TOPIC_LINEAL_ACCELERATION         = "lineal_acceleration";
    public static final String TOPIC_ROTATION_VECTOR             = "rotation_vector";
    public static final String TOPIC_ORIENTATION                 = "orientation";
    public static final String TOPIC_RELATIVE_HUMIDITY           = "relative_humidity";
    public static final String TOPIC_AMBIENT_TEMPERATURE         = "ambient_temperature";
    public static final String TOPIC_MAGNETIC_FIELD_UNCALIBRATED = "magnetic_fied_uncalibrated";
    public static final String TOPIC_GAME_ROTATION_VECTOR        = "game_rotation_vector";
    public static final String TOPIC_GYROSCOPE_UNCALIBRATED      = "gyroscope_uncalibrated";

}
