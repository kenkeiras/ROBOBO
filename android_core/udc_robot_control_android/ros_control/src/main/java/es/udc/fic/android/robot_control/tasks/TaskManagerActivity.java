package es.udc.fic.android.robot_control.tasks;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;

import es.udc.fic.android.robot_control.R;

import java.io.File;

public class TaskManagerActivity extends Activity {

    public final static String taskDirectory = "/sdcard/ros/";

    private boolean populateList(){
        File directory = new File(taskDirectory);
        File[] files = directory.listFiles();

        if ((files == null) || (files.length == 0)){
            return false;
        }

        TaskListAdapter adapter = new TaskListAdapter(this, files);
        ListView lv = (ListView) findViewById(R.id.task_list);
        lv.setAdapter(adapter);

        return true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.task_manager);

        if (!populateList()){
            setContentView(R.layout.task_manager_no_tasks);
        }
    }
}
