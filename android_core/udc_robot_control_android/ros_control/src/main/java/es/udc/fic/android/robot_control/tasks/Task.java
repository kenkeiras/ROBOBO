package es.udc.fic.android.robot_control.tasks;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class Task {

    public final static int STOP    = 1;
    public final static int RUNNING = 2;
    public final static int CRASHED = 3;


    private TaskManagerService service;
    private File file;
    private String fileName;
    private int state;
    private Thread thread;

    public Task(File file){
        this.file = file;
        this.fileName = file.getName();
        this.state = STOP;
        thread = null;
    }


    public String getName(){
        return fileName;
    }


    public String getPath(){
        return file.getAbsolutePath();
    }


    public int getState(){
        return state;
    }


    public void run(final TaskManagerService context, final String masterUri){
        final Task task = this;
        this.service = context;
        thread = new Thread() {
                public void run(){
                    try {
                        TaskRunner.run(task, context, masterUri);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        task.crashed();
                    }
                }
            };
        thread.start();
        state = RUNNING;
    }


    public void crashed(){
        state = CRASHED;

        if (this.service != null){
            service.refreshTaskInfo();
        }
    }


    public void stop(){
        state = STOP;
        thread.interrupt();
        thread = null;

        if (this.service != null){
            service.refreshTaskInfo();
        }
    }
}
