package es.ait.yoplp.message;

import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Clase que devuelve el bus de comunicaciones con el que se intercambian mensajes la actividad principal
 * y los servicios.
 */
public class BusManager
{
    private static Bus bus;

    public static Bus getBus()
    {
        if ( bus == null )
        {
            Log.e("[YOPLP]", "creando un nuevo bus");
            bus = new Bus(ThreadEnforcer.ANY );
        }
        return bus;
    }
}
