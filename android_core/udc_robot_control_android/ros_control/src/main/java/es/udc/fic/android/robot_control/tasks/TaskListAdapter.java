package es.udc.fic.android.robot_control.tasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import es.udc.fic.android.robot_control.R;

import java.io.File;
import java.util.List;

public class TaskListAdapter extends BaseAdapter implements ListAdapter {
    private static final int STOP_COLOR = Color.DKGRAY;
    private static final int RUNNING_COLOR = Color.WHITE;
    private static final int ERROR_COLOR = Color.RED;


    private Context _context;
    private List<Task> _tasks;

    public class TaskChangedReceiver extends BroadcastReceiver{
        public TaskChangedReceiver(){}

        @Override
        public void onReceive(Context context, Intent intent) {
            TaskListAdapter.this.notifyDataSetChanged();
        }
    }
    BroadcastReceiver changedReceiver = new TaskChangedReceiver();


    public TaskListAdapter(Context context, List<Task> tasks) {
        /// @TODO filter non runnable tasks
        _context = context;
        _tasks = tasks;

        IntentFilter filter = new IntentFilter(TaskManagerService.TASKS_CHANGED_TAG);
        context.registerReceiver(changedReceiver, filter);
    }


    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }


    @Override
    public boolean isEnabled(int i) {
        return true;
    }


    @Override
    public int getCount() {
        return _tasks.size();
    }


    @Override
    public Task getItem(int i) {
        if (i < _tasks.size()){
            return _tasks.get(i);
        }

        return null;
    }


    @Override
    public long getItemId(int i) {
        if (i < _tasks.size()){
            return _tasks.get(i).hashCode();
        }

        return 0;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Initialice all the stuff
        if (i >= _tasks.size()){
            return null;
        }
        Task task = _tasks.get(i);

        View v = view;
        if (v == null) {
            LayoutInflater layout = (LayoutInflater) _context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

            v = layout.inflate(R.layout.task_item_row, null);
        }


        // Get basic fields
        TextView nameTv = (TextView) v.findViewById(R.id.task_name);
        nameTv.setText(task.getName());

        // Set color indicator
        View colorBlock = v.findViewById(R.id.color_block);
        switch(task.getState()){
        case Task.STOP:
            colorBlock.setBackgroundColor(STOP_COLOR);
            break;

        case Task.RUNNING:
            colorBlock.setBackgroundColor(RUNNING_COLOR);
            break;

        case Task.CRASHED:
            colorBlock.setBackgroundColor(ERROR_COLOR);
            break;
        }


        return v;
    }


    @Override
    public int getItemViewType(int i) {
        return 0;
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }


    @Override
    public boolean isEmpty() {
        return _tasks.size() != 0;
    }
}
