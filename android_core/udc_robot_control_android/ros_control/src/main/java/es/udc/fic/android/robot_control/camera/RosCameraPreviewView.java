/*
 * Copyright (C) 2011 Google Inc.
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

package es.udc.fic.android.robot_control.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import es.udc.fic.android.robot_control.ConfigActivity;
import es.udc.fic.android.robot_control.utils.C;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;

/**
 * Displays and publishes preview frames from the camera.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
public class RosCameraPreviewView extends CameraPreviewView implements NodeMain {

    private String robotName;
    private Context context;

    public RosCameraPreviewView(Context context) {
        super(context);
        this.context = context;
    }

    public RosCameraPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public RosCameraPreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(C.DefaultBaseNodeName + "/camera");
    }


    private boolean runLocalAprilTagDetector(){
        return context.getSharedPreferences(
            ConfigActivity.class.getName(),
            ConfigActivity.MODE_PRIVATE).getBoolean(
                ConfigActivity.PREFS_KEY_LOCAL_APRIL_TAG_DETECTION,
                false);
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        Log.d(C.TAG, "Starting [ " + connectedNode.getName() + " ]");
        addRawImageListener(new CompressedImagePublisher(getRobotName(), connectedNode, context));

        if (runLocalAprilTagDetector()){
            addRawImageListener(new AprilTagPublisher(getRobotName(),
                                                      connectedNode));
        }
    }


    @Override
    public void onShutdown(Node node) {
        Log.d(C.TAG, "Shutting down [ " + node.getName() + " ]");
    }

    @Override
    public void onShutdownComplete(Node node) {
        Log.d(C.TAG, "Shutdown Complete [ " + node.getName() + " ]");
    }

    @Override
    public void onError(Node node, Throwable throwable) {
        Log.w(C.TAG, "Error [ " + node.getName() + " ]", throwable);
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }
}
