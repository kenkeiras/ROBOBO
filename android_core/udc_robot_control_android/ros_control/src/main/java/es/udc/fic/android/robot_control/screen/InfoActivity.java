package es.udc.fic.android.robot_control.screen;

import android.app.Activity;
import android.content.ComponentName;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;
import android.webkit.WebView;

import es.udc.fic.android.robot_control.R;
import es.udc.fic.android.robot_control.camera.RosCameraPreviewView;
import es.udc.fic.android.robot_control.robot.RobotCommController;

public class InfoActivity extends Activity {

    public final static String NEW_INFO_TAG = "es.udc.fic.android.robot_control.screen.NEW_INFO";

    private RobotCommController robot;
    private RosCameraPreviewView cameraPreview;

    private WebView webView;
    private String DEFAULT_MSG = "<html><head></head><body>No information "
        + "has been sent yet</body></html>";

    private Intent robotControllerIntent;
    private ServiceConnection rConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder bind) {
            RobotCommController.SimpleBinder sBinder = (RobotCommController.SimpleBinder) bind;
            robot = sBinder.getService();
            if (cameraPreview != null){
                robot.setCameraPreview(cameraPreview);
            }
            InfoActivity.this.notifyNewInfo();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };


    public class NewInfoReceiver extends BroadcastReceiver{
        public NewInfoReceiver(){}

        @Override
        public void onReceive(Context context, Intent intent) {
            InfoActivity.this.notifyNewInfo();
        }
    }
    BroadcastReceiver newInfoReceiver = new NewInfoReceiver();

    @Override
    public void onDestroy(){
        super.onDestroy();

        unbindService(rConn);
    }


    protected void notifyNewInfo(){
        if (robot != null){

            String msg = robot.getLastInfo();
            if (msg == null){
                msg = DEFAULT_MSG;
            }
            webView.loadData(msg, "text/html", null);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        webView = (WebView)findViewById(R.id.info_web_view);
        robotControllerIntent = new Intent(this, RobotCommController.class);
        bindService(robotControllerIntent, rConn, 0);
    }

    @Override
    public void onResume(){
        super.onResume();
        cameraPreview = (RosCameraPreviewView) findViewById(R.id.ros_camera_preview_view_task_manager);
        cameraPreview.hide();

        if (robot != null){
            robot.setCameraPreview(cameraPreview);
        }

        notifyNewInfo();

        IntentFilter filter = new IntentFilter(InfoActivity.NEW_INFO_TAG);
        registerReceiver(newInfoReceiver, filter);
    }


    @Override
    public void onPause(){
        super.onPause();
        cameraPreview = null;
        if (robot != null){
                robot.setCameraPreview(null);
        }

        unregisterReceiver(newInfoReceiver);
    }
}
