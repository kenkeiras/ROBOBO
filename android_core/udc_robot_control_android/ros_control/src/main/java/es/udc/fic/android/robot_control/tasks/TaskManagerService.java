package es.udc.fic.android.robot_control.tasks;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;

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
            taskList.add(new Task(f));
        }

        _taskList = taskList;
    }


    public List<Task> getTaskList(){
        return _taskList;
    }

    public void toggle(Task task, String masterUri){
        Log.v("UDC", task + "");

        switch(task.getState()){
        case Task.STOP: case Task.CRASHED:
            Log.v("UDC", "Resuming task");

            task.run(this, masterUri);
            break;

        case Task.RUNNING:
            Log.v("UDC", "Stopping task");
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
        Log.d("UDC", "Bind, " + this.hashCode());
        refreshTaskList();
        return sBinder;
    }
}
