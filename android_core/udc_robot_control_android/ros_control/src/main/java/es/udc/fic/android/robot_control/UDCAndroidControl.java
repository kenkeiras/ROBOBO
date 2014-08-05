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

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.common.base.Preconditions;

import es.udc.fic.android.robot_control.camera.RosCameraPreviewView;
import es.udc.fic.android.robot_control.robot.RobotCommController;
import es.udc.fic.android.robot_control.robot.RobotCommController.SimpleBinder;
import es.udc.fic.android.robot_control.robot.RobotSensorPublisher;
import es.udc.fic.android.robot_control.robot.SensorInfo;
import es.udc.fic.android.robot_control.screen.InfoActivity;
import es.udc.fic.android.robot_control.tasks.TaskManagerActivity;

import es.udc.fic.android.robot_control.utils.C;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import udc_robot_control_msgs.ActionCommand;

import java.net.URI;


public class UDCAndroidControl extends RosActivity {

    private static int MASTER_CHOOSER_REQUEST_CODE = 0;
    private static int MASTER_CHOOSER_REQUEST_CODE_FAKE = 99;
    private RosCameraPreviewView cameraPreview;
    private URI masterURI;
    private Intent robotControllerIntent;
    private NodeMainExecutor nodeMainExecutor;
    private Intent usbIntent;

    private String robotName = "no_robot_name";
    private RobotCommController robot;

    public UDCAndroidControl() {
        super("UDC Android Control", "UDC Android Control");
    }

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder bind) {
            SimpleBinder sBinder = (SimpleBinder) bind;
            robot = sBinder.getService();
            robot.setRobotName(robotName);
            if (masterURI != null){
                robot.setMasterUri(masterURI);
            }
            if (nodeMainExecutor != null){
                robot.setNodeMainExecutor(nodeMainExecutor);
            }
            if (usbIntent != null){
                robot.start(UDCAndroidControl.this, usbIntent);
            }
            if (cameraPreview != null){
                robot.setCameraPreview(cameraPreview);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    robotControllerIntent = new Intent(this, RobotCommController.class);

    startService(robotControllerIntent);
    bindService(robotControllerIntent, mConn, 0);
  }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        masterURI = getMasterUri();
        if (robot != null){
            robot.setRobotName(robotName);
            robot.setMasterUri(masterURI);
            robot.setNodeMainExecutor(nodeMainExecutor);
        }
        this.nodeMainExecutor = nodeMainExecutor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_tasks) {
            // Free camera here, on onStop would be too late for
            // TaskManagerActivity to take it
            cameraPreview = null;
            if (robot != null){
                robot.setCameraPreview(null);
            }

            Intent i = new Intent(this, TaskManagerActivity.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_info){
            cameraPreview = null;
            if (robot != null){
                robot.setCameraPreview(null);
            }

            Intent i = new Intent(this, InfoActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void startMasterChooser() {
        Preconditions.checkState(getMasterUri() == null);
        // Call this method on super to avoid triggering our precondition in the
        // overridden startActivityForResult().
        super.startActivityForResult(new Intent(this, ConfigActivity.class), MASTER_CHOOSER_REQUEST_CODE_FAKE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MASTER_CHOOSER_REQUEST_CODE_FAKE) {
            requestCode = MASTER_CHOOSER_REQUEST_CODE;
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == MASTER_CHOOSER_REQUEST_CODE) {
                if (data != null) {
                    robotName = data.getStringExtra("ROS_ROBOT_NAME");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * New BroadcastReceiver object that will handle all of the USB device
     * attach and detach events.
     */

//    BroadcastReceiver receiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//    		/* Get the information about what action caused this event */
//            String action = intent.getAction();
//
//            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
//                Log.i(C.ROBOT_TAG, "Disconnected device");
//                // A device has been disconnected
//                if (robot != null) {
//                    Log.i(C.ROBOT_TAG, "Disconnected device... stopping");
//                    robot.stop();
//                }
//            }
//
//            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
//                Log.i(C.ROBOT_TAG, "Device connected");
//                // A device has been connected
//                if (robot != null) {
//                    Log.i(C.ROBOT_TAG, "Device connected... starting");
//                    robot.start(context, intent);
//                }
//            }
//        }
//    };

    @Override
    public void onResume() {
        super.onResume();

    	/* Check to see if it was a USB device attach that caused the app to
    	 * start or if the user opened the program manually.
    	 */
        Intent intent = getIntent();
        String action = intent.getAction();

        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            Log.i(C.ROBOT_TAG, "OnResume by connected device");
            usbIntent = intent;
            if (robot != null){
                robot.start(this, intent);
            }
        }
        else {
            // Ha sido arrancada manualmente
            Log.w(C.ROBOT_TAG, "Manually started WITHOUT robot");
            Toast.makeText(this, R.string.robot_service_manual_not_start, Toast.LENGTH_SHORT).show();
            if (robot != null){
                robot.manualStart(this);
            }
        }

        //Register to listen the USB events
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        registerReceiver(receiver, filter);
    }


    @Override
    public void onStart(){
        super.onStart();

        cameraPreview = (RosCameraPreviewView) findViewById(R.id.ros_camera_preview_view);
        if (robot != null){
            robot.setCameraPreview(cameraPreview);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if (cameraPreview != null){
            cameraPreview = null;
            if (robot != null){
                robot.setCameraPreview(null);
            }
        }
    }


    public void startListener(ActionCommand actionCommand) {
        robot.startListener(actionCommand);
    }

    public void stopListener(ActionCommand actionCommand) {
        robot.stop(actionCommand);
    }

    public void sendRobot(ActionCommand comando) {
        robot.write(comando);
    }

}
