package org.ros.robobo;

public class Robobo {
    public static void main(String args[]){
        if (args.length == 0){
            System.err.println("No args provided");
            return;
        }

        System.out.println("Starting example: " + args[0] + " with " + args.length + " args");
        String masterUri = args[1];

        System.out.println("Connecting to master [URI: " + masterUri + " ]");
    }
}
