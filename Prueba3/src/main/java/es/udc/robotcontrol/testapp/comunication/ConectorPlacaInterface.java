package es.udc.robotcontrol.testapp.comunication;

import android.content.Context;
import android.content.Intent;

/**
 * Created by kerry on 6/06/13.
 */
public interface ConectorPlacaInterface {
    public boolean escribir(Comando c);
    public byte[] leer();
    public void conectar(Context ctx, Intent intent) throws TransmisionErrorException;
    public void conectarManual(Context ctx) throws TransmisionErrorException;
    public void desconectar();
}
