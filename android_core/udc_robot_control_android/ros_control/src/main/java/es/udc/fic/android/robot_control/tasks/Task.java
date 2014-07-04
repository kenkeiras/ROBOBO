package es.udc.fic.android.robot_control.tasks;

import android.util.Log;

import java.io.File;

public class Task {

    public final static int STOP    = 1;
    public final static int RUNNING = 2;
    public final static int CRASHED = 3;


    private File f;
    private String fileName;
    private int state;

    public Task(File f){
        this.f = f;
        this.fileName = f.getName();
        this.state = STOP;
    }


    public String getName(){
        return fileName;
    }


    public int getState(){
        return state;
    }


    public void run(){
        state = RUNNING;
    }


    public void stop(){
        state = STOP;
    }
}
