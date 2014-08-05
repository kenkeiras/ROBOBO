package es.udc.fic.android.robot_control.screen;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;

import java.util.List;

import es.udc.fic.android.robot_control.ConfigActivity;
import es.udc.fic.android.robot_control.R;
import es.udc.fic.android.robot_control.camera.RosCameraPreviewView;
import es.udc.fic.android.robot_control.robot.RobotCommController;

import org.ros.node.NodeConfiguration;

public class InfoActivity extends Activity {

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
            String msg = robot.getLastInfo();
            if (msg == null){
                msg = DEFAULT_MSG;
            }
            webView.loadData(msg, "text/html", null);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();

        unbindService(rConn);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

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

                String msg = robot.getLastInfo();
                if (msg == null){
                    msg = DEFAULT_MSG;
                }
                webView.loadData(msg, "text/html", null);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        cameraPreview = null;
        if (robot != null){
                robot.setCameraPreview(null);
        }
    }
}
