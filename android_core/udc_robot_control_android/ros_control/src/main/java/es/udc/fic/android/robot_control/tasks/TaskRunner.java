package es.udc.fic.android.robot_control.tasks;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;

import dalvik.system.DexClassLoader;


public class TaskRunner {

    public final static String TASK_NAME = "org.ros.robobo.Robobo";

    public static void run(Task task, Context context)
        throws ClassNotFoundException, NoSuchMethodException,
        IllegalAccessException, InvocationTargetException {

        ClassLoader loader = new DexClassLoader(
            task.getPath(), context.getCacheDir().getAbsolutePath(),
            null, TaskRunner.class.getClassLoader());

        Log.v("UDC", "Loader " + loader);
        Class<?> clazz = Class.forName(TASK_NAME, true, loader);
        clazz.getMethod("main").invoke(null);
    }
}
