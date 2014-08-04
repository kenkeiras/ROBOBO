package es.udc.fic.android.robot_control.tasks;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
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

public class TaskManagerActivity extends Activity {


    private RobotCommController robot;
    private RosCameraPreviewView cameraPreview;
    private Intent taskServiceIntent;
    private TaskManagerService taskService;
    private ServiceConnection mConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder bind) {
            TaskManagerService.SimpleBinder sBinder = (TaskManagerService.SimpleBinder) bind;
            taskService = sBinder.getService();
            if (!populateList()){
                setContentView(R.layout.task_manager_no_tasks);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };


    private Intent robotControllerIntent;
    private ServiceConnection rConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder bind) {
            RobotCommController.SimpleBinder sBinder = (RobotCommController.SimpleBinder) bind;
            robot = sBinder.getService();
            robot.setCameraPreview(cameraPreview);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    private boolean populateList(){
        List<Task> taskList = taskService.getTaskList();
        if (taskList.size() == 0){
            return false;
        }

        final TaskListAdapter adapter = new TaskListAdapter(this, taskList);
        ListView lv = (ListView) findViewById(R.id.task_list);
        lv.setAdapter(adapter);

        final TaskManagerService tService = taskService;
        final Activity ctx = this;
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView<?> arg0,
                                               View arg1, int pos, long id) {

                    String masterUri = ctx.getSharedPreferences(
                        ConfigActivity.class.getName(), MODE_PRIVATE).getString(
                            ConfigActivity.PREFS_KEY_URI,
                            NodeConfiguration.DEFAULT_MASTER_URI.toString());

                    Log.v("long clicked","pos: " + pos);
                    tService.toggle(adapter.getItem(pos), masterUri);
                    return true;
                }
            });

        return true;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();

        unbindService(mConn);
        unbindService(rConn);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onResume();
        setContentView(R.layout.task_manager);


        cameraPreview = (RosCameraPreviewView) findViewById(R.id.ros_camera_preview_view_task_manager);
        cameraPreview.hide();
        robotControllerIntent = new Intent(this, RobotCommController.class);
        bindService(robotControllerIntent, rConn, 0);


        taskServiceIntent = new Intent(this, TaskManagerService.class);
        startService(taskServiceIntent);
        bindService(taskServiceIntent, mConn, 0);
    }
}
