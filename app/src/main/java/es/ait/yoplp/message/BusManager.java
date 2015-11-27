package es.ait.yoplp.message;

import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by aitkiar on 2/10/15.
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
