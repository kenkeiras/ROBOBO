package es.udc.fic.android.robot_control.tasks;

import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;


public class TaskRunner {

    public final static String TASK_NAME = "org.ros.robobo.Robobo";
    public final static String CALLED_METHOD_NAME = "main";
    public final static String NAME_PROPERTY = "taskName";
    public final static String DESCRIPTION_PROPERTY = "taskDescription";


    public static Class<?> getMainClass(String path, Context context)
        throws ClassNotFoundException, NoSuchMethodException,
        IllegalAccessException, InvocationTargetException {

        ClassLoader loader = new DexClassLoader(
            path, context.getCacheDir().getAbsolutePath(),
            null, TaskRunner.class.getClassLoader());

        return Class.forName(TASK_NAME, true, loader);
    }


    public static void run(String path, Context context,
                           String masterUri, String robotName)
        throws ClassNotFoundException, NoSuchMethodException,
        IllegalAccessException, InvocationTargetException {

        Class<?> clazz = getMainClass(path, context);

        String[] args = new String[]{ path, masterUri, robotName };

        for (Method m : clazz.getDeclaredMethods()){
            if (m.getName().equals(CALLED_METHOD_NAME)){
                m.invoke(null, new Object[]{args});
                return;
            }
        }

        throw new NoSuchMethodException(CALLED_METHOD_NAME);
    }


    public static String getStringProperty(String path,
                                           Context context,
                                           String prop)
        throws ClassNotFoundException, NoSuchMethodException,
        IllegalAccessException, InvocationTargetException {

        Class<?> clazz = getMainClass(path, context);
        Field field = null;

        try {
            field = clazz.getDeclaredField(prop);
        }
        catch (NoSuchFieldException e){
            e.printStackTrace();
            return null;
        }

        return (String) field.get(null);
    }

    public static String getName(String path, Context context)
        throws ClassNotFoundException, NoSuchMethodException,
        IllegalAccessException, InvocationTargetException {

        return getStringProperty(path, context, NAME_PROPERTY);
    }


    public static String getDescription(String path, Context context)
        throws ClassNotFoundException, NoSuchMethodException,
        IllegalAccessException, InvocationTargetException {

        return getStringProperty(path, context, DESCRIPTION_PROPERTY);
    }
}
