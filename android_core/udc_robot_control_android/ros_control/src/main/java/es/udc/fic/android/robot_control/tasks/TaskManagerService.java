package es.udc.fic.android.robot_control.tasks;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import es.udc.fic.android.robot_control.utils.C;

public class TaskManagerService extends Service {

    public final static String TASK_DIRECTORY = "/sdcard/ros/";
    public final static String TASKS_CHANGED_TAG = "es.udc.fic.android.robot_control.tasks.TasksChanged";

    private List<Task> _taskList;

    private final IBinder sBinder = (IBinder) new SimpleBinder();
    private Intent tasksChangedIntent = new Intent(TASKS_CHANGED_TAG);

    class SimpleBinder extends Binder {
        TaskManagerService getService(){
            return TaskManagerService.this;
        }
    }


    private void refreshTaskList(){
        File directory = new File(TASK_DIRECTORY);
        File[] files = directory.listFiles();
        if (files == null){
            files = new File[0];
        }

        List<Task> taskList = new ArrayList<Task>();
        for (File f : files){
            try {
                taskList.add(new Task(f, this));
            }
            catch (NotATaskException e){}
        }

        _taskList = taskList;
    }


    public List<Task> getTaskList(){
        return _taskList;
    }

    public void toggle(Task task, String masterUri, String robotName){
        Log.v(C.TAG, task + "");

        switch(task.getState()){
        case Task.STOP: case Task.CRASHED:
            Log.v(C.TAG, "Resuming task");

            task.stop();
            task.run(masterUri, robotName);
            break;

        case Task.RUNNING:
            Log.v(C.TAG, "Stopping task");
            task.stop();
            break;
        }

        refreshTaskInfo();
    }


    public void refreshTaskInfo(){
        this.sendBroadcast(tasksChangedIntent);
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(C.TAG, "Bind, " + this.hashCode());
        refreshTaskList();
        return sBinder;
    }
}
