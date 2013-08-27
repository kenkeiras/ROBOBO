package net.adiaz.prueba3.comunication;

/**
 * Created by kerry on 7/06/13.
 */
public class TransmisionErrorException extends Exception {

    public TransmisionErrorException() {
        super();
    }

    public TransmisionErrorException(String msg) {
        super(msg);
    }

    public TransmisionErrorException(String msg, Throwable th) {
        super(msg, th);
    }

}
