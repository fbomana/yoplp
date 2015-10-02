package es.ait.yoplp.message;

import android.os.Handler;
import android.os.Looper;

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
            bus = new Bus(ThreadEnforcer.ANY );
        }
        return bus;
    }
}
