package es.udc.fic.android.robot_control.tasks;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;


public class TaskRunner {

    public final static String TASK_NAME = "org.ros.robobo.Robobo";
    public final static String CALLED_METHOD_NAME = "main";

    public static void run(Task task, Context context, String masterUri)
        throws ClassNotFoundException, NoSuchMethodException,
        IllegalAccessException, InvocationTargetException {

        ClassLoader loader = new DexClassLoader(
            task.getPath(), context.getCacheDir().getAbsolutePath(),
            null, TaskRunner.class.getClassLoader());

        Log.v("UDC", "Loader " + loader);
        Class<?> clazz = Class.forName(TASK_NAME, true, loader);

        String[] args = new String[]{ task.getPath(), masterUri };

        for (Method m : clazz.getDeclaredMethods()){
            if (m.getName().equals(CALLED_METHOD_NAME)){
                m.invoke(null, new Object[]{args});
                return;
            }
        }

        throw new NoSuchMethodException(CALLED_METHOD_NAME);
    }
}
