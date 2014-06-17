package es.udc.fic.android.robot_control.roscore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


/**
 * A Hierarchy defined by keys of type K and values of type V.
 *
 */
public class Hierarchy<K, V> {

    public static class KeyNotFoundException extends Exception {
        KeyNotFoundException(String message){
            super(message);
        }
    }

    Map<K, Tuple<Hierarchy<K, V>, V>> children =
        new HashMap<K, Tuple<Hierarchy<K, V>, V>>();


    /**
     * Sets a element to a value.
     *
     */
    public void set(K[] path, V value){
        set(Arrays.asList(path), value);
    }


    // The public function uses arrays to avoid side-effects.
    private void set(List<K> path, V value){
        if (path.size() == 0){
            throw new IllegalArgumentException("Empty path");
        }
        else if (path.size() == 1){
            // Found target
            children.put(path.get(0), new Tuple<Hierarchy<K, V>, V>(null, value));
        }
        else {
            Tuple<Hierarchy<K, V>, V> child = children.get(path.get(0));
            if (child == null){
                // Creating new namespace
                child = new Tuple<Hierarchy<K, V>, V>(new Hierarchy<K, V>(), null);
                children.put(path.get(0), child);
            }
            else if (child.y != null){
                throw new IllegalArgumentException("Path uses value as namespace");
            }
            child.x.set(path.subList(1, path.size()), value);
        }
    }


    /**
     * Check if the hierarchy contains a element with a certain path.
     *
     */
    public boolean has(K[] path) {
        // Relying too much on exceptions may not be good for performance.
        try {
            get(path);
            return true;
        }
        catch (KeyNotFoundException e){
            return false;
        }
    }


    /**
     * Retrieves the value of a certain path.
     *
     */
    public Tuple<Hierarchy<K, V>, V> get(K[] path) throws KeyNotFoundException {
        if (path.length == 0){ // This can just happen at root
            return new Tuple<Hierarchy<K, V>, V>(this, null);
        }
        return get(Arrays.asList(path));
    }


    private Tuple<Hierarchy<K, V>, V> get(List<K> path) throws KeyNotFoundException {
        if (path.size() == 0){
            throw new IllegalArgumentException("Empty path");
        }
        else if (path.size() == 1){
            // Found target
            return children.get(path.get(0));
        }
        else {
            Tuple<Hierarchy<K, V>, V> child = children.get(path.get(0));
            if (child == null){
                throw new KeyNotFoundException("Key not found '" + path.get(0)
                                               + "' remaining steps="
                                               + path.size());
            }
            return child.x.get(path.subList(1, path.size()));
        }
    }


    /**
     * Returns the hierarchy as a hierarchical map.
     *
     */
    public Map asMap(){
        Map<K, Object> map = new HashMap<K, Object>();

        for(K key : children.keySet()){
            Tuple<Hierarchy<K, V>, V> value = children.get(key);

            if (value.y == null){
                map.put(key, value.x.asMap());
            }
            else {
                map.put(key, value.y);
            }
        }

        return map;
    }
}
