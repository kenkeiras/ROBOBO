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
    private String name;
    private String description;
    private Context context;

    public Task(File file, Context context) throws NotATaskException {
        this.context = context;
        this.file = file;
        this.fileName = file.getName();
        this.state = STOP;

        String path = file.getAbsolutePath();
        try {
            this.name = TaskRunner.getName(path, context);
            this.description = TaskRunner.getDescription(path, context);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new NotATaskException();
        }
        thread = null;
    }


    public String getName(){
        if (name != null){
            return name;
        }
        return fileName;
    }


    public String getDescription(){
        if (description != null){
            return description;
        }
        return "";
    }


    public String getPath(){
        return file.getAbsolutePath();
    }


    public int getState(){
        return state;
    }


    public void run(final String masterUri, final String robotName){
        final Task task = this;
        thread = new Thread() {
                public void run(){
                    try {
                        TaskRunner.run(file.getAbsolutePath(),
                                       context, masterUri,
                                       robotName);
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


    public synchronized void stop(){
        state = STOP;

        if (thread != null){
            thread.interrupt();
            thread = null;
        }

        if (this.service != null){
            service.refreshTaskInfo();
        }
    }
}
